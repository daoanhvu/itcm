package com.nautilus.nat.fxservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nautilus.nat.model.BoundingBox;
import com.nautilus.nat.model.NautilusProject;
import com.nautilus.nat.model.TrainingFileItem;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        String projectName = fileNode.get("name").asText();
        final String location = fileNode.get("location").asText();
        List<String> categories = null;
        JsonNode categoriesNode = fileNode.get("categories");
        if (categoriesNode != null) {
          if (!categoriesNode.isArray()) {
            throw new IllegalArgumentException("categories in project must be an ARRAY");
          }

          Iterator<JsonNode> catIters = categoriesNode.iterator();
          categories = new ArrayList<>();
          for (; catIters.hasNext(); ) {
            categories.add(catIters.next().asText());
          }

        }
        JsonNode fileArrayNode = fileNode.get("files");
        List<TrainingFileItem> trainingFileItems = null;
        if (fileArrayNode != null) {

          if (!fileArrayNode.isArray()) {
            throw new IllegalArgumentException("files in project must be an ARRAY");
          }

          ArrayNode itemNodes = (ArrayNode) fileArrayNode;
          Iterator<JsonNode> fileIter = itemNodes.iterator();
          trainingFileItems = new ArrayList<>();
          for (Iterator<JsonNode> fileItr = fileIter; fileItr.hasNext(); ) {

            JsonNode json = fileItr.next();
            String fileName = json.get("name").asText();
            String fullPath = location + "/" + fileName;
            JsonNode bBoxesNode = json.get("annotations");
            List<BoundingBox> boundingBoxes = null;

            if (bBoxesNode != null) {
              if (!bBoxesNode.isArray()) {
                throw new IllegalArgumentException("annotations in each file must be an ARRAY");
              }

              Iterator<JsonNode> bBoxIter = bBoxesNode.iterator();
              boundingBoxes = new ArrayList<>();
              for (Iterator<JsonNode> boxItr = bBoxIter; boxItr.hasNext(); ) {
                JsonNode boxNode = boxItr.next();
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
            TrainingFileItem item = new TrainingFileItem();
            item.setAnnotations(boundingBoxes);
            item.setFullPath(fullPath);
            item.setName(fileName);
            trainingFileItems.add(item);
          }
        }
        NautilusProject loadedProject = new NautilusProject();
        loadedProject.setName(projectName);
        loadedProject.setLocation(location);
        loadedProject.setCategories(categories);
        loadedProject.setFiles(trainingFileItems);
        return loadedProject;
      }
    };
  }
}
