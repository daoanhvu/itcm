package com.nautilus.nat.fxservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nautilus.nat.model.ApplicationConfig;
import com.nautilus.nat.model.BoundingBox;
import com.nautilus.nat.model.NautilusProject;
import com.nautilus.nat.model.TrainingFileItem;
import com.nautilus.nat.util.SystemUtil;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProjectLoadingService extends Service<NautilusProject> {

  private final File projectFile;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public ProjectLoadingService(File file) {
    this.projectFile = file;
  }

  @Override
  protected Task<NautilusProject> createTask() {
    return new Task<NautilusProject>() {
      @Override
      protected NautilusProject call() throws Exception {
        if (!projectFile.exists()) {
          throw new FileNotFoundException(projectFile.getPath() + " is not existed.");
        }
        JsonNode fileNode = objectMapper.readTree(projectFile);
        NautilusProject loadedProject = new NautilusProject();
        String projectName = fileNode.get("name").asText();
        final String location = fileNode.get("location").asText();
        loadedProject.setName(projectName);
        loadedProject.setLocation(location);
        List<String> categories = null;
        JsonNode categoriesNode = fileNode.get("categories");
        if (categoriesNode != null) {
          if (!categoriesNode.isArray()) {
            throw new IllegalArgumentException("categories in project must be an ARRAY");
          }

          Iterator<JsonNode> catIters = categoriesNode.iterator();
          categories = new ArrayList<>();
          while (catIters.hasNext()) {
            categories.add(catIters.next().asText());
          }

        }
        loadedProject.setCategories(categories);

        JsonNode fileArrayNode = fileNode.get("files");
        List<TrainingFileItem> trainingFileItems = null;
        if (fileArrayNode != null) {

          if (!fileArrayNode.isArray()) {
            throw new IllegalArgumentException("files in project must be an ARRAY");
          }

          ArrayNode itemNodes = (ArrayNode) fileArrayNode;
          Iterator<JsonNode> fileIter = itemNodes.iterator();
          trainingFileItems = new ArrayList<>();
          while (fileIter.hasNext()) {

            JsonNode json = fileIter.next();
            TrainingFileItem item = new TrainingFileItem();
            String fileName = json.get("name").asText();
            String pathDelimiter = "\\";
            if (ApplicationConfig.getInstance().OS_CODE != SystemUtil.OS_WINDOWS) {
              pathDelimiter = "/";
            }
            String fullPath = location + pathDelimiter + fileName;
            item.setFullPath(fullPath);
            item.setName(fileName);

            JsonNode bBoxesNode = json.get("annotations");
            List<BoundingBox> boundingBoxes = null;

            if (bBoxesNode != null) {
              if (!bBoxesNode.isArray()) {
                throw new IllegalArgumentException("annotations in each file must be an ARRAY");
              }

              Iterator<JsonNode> bBoxIter = bBoxesNode.iterator();
              boundingBoxes = new ArrayList<>();
              while (bBoxIter.hasNext()) {
                JsonNode boxNode = bBoxIter.next();
                BoundingBox bbox = new BoundingBox();
                int cIndex = boxNode.get("category_index").asInt();
                bbox.setCategoryIndex(cIndex);
                bbox.setCategory(boxNode.get("category").asText());
                JsonNode bBoxValueNodes = boxNode.get("bbox");
                if (bBoxValueNodes != null) {
                  if (!bBoxValueNodes.isArray()) {
                    throw new IllegalArgumentException("bbox in each file must be an ARRAY");
                  }

                  ArrayNode bBoxValueArrayNodes = (ArrayNode) bBoxValueNodes;
                  double x = bBoxValueArrayNodes.get(0).asDouble();
                  double y = bBoxValueArrayNodes.get(1).asDouble();
                  double width = bBoxValueArrayNodes.get(2).asDouble();
                  double height = bBoxValueArrayNodes.get(3).asDouble();
                  bbox.setX(x);
                  bbox.setY(y);
                  bbox.setWidth(width);
                  bbox.setHeight(height);
                  boundingBoxes.add(bbox);
                }
              }
            }
            item.setAnnotations(boundingBoxes);
            trainingFileItems.add(item);
          }
        }
        loadedProject.setFiles(trainingFileItems);
        loadImageFiles(loadedProject);

        return loadedProject;
      }

      private void loadImageFiles(NautilusProject loadedProject) {
        File parentFolder = new File(loadedProject.getLocation());
        FilenameFilter filenameFilter = (dir, name) -> {
          String lowerCaseName = name.toLowerCase(Locale.ROOT);
          return lowerCaseName.endsWith(".png") || lowerCaseName.endsWith(".jpg");
        };

        final File[] imageFiles = parentFolder.listFiles(filenameFilter);
        if (imageFiles != null) {
          if (loadedProject.getFiles() == null) {
            loadedProject.setFiles(new ArrayList<>());
          }

          Map<String, TrainingFileItem> itemsByFullPath = loadedProject.getFiles().stream()
              .collect(Collectors.toMap(TrainingFileItem::getFullPath, Function.identity(), (seenItem, newItem) -> seenItem));
          for (File f: imageFiles) {
            if (!itemsByFullPath.containsKey(f.getPath())) {
              TrainingFileItem newItem = new TrainingFileItem();
              newItem.setName(f.getName());
              newItem.setFullPath(f.getPath());
              loadedProject.getFiles().add(newItem);
            }
          }
        }
      }
    };
  }
}
