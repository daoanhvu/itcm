package com.nautilus.nat.controller;

import com.nautilus.nat.component.AlertDialogUtil;
import com.nautilus.nat.component.BoundingBoxInfoPane;
import com.nautilus.nat.component.GraphicsPane;
import com.nautilus.nat.fxservice.ProjectLoadingService;
import com.nautilus.nat.fxservice.ProjectSavingService;
import com.nautilus.nat.model.ApplicationConfig;
import com.nautilus.nat.model.NautilusProject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
  @FXML
  private ToolBar mainToolBar;
  @FXML
  private ToggleButton btnAddingBoundingBox;
  @FXML
  private Button btnOpenProject;
  @FXML
  private Button btnSaveProject;
  @FXML
  private MenuBar mainMenuBar;
  @FXML
  private GraphicsPane graphicsPane;
  @FXML
  private BoundingBoxInfoPane bBoxInfoPane;
  @FXML
  private Label lbMousePosition;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    bBoxInfoPane.selectedFileProperty()
        .addListener((src, oldPath, newPath) -> {
          Platform.runLater(() -> renderImage(newPath));
        });

    btnOpenProject.setOnAction(this::onOpenProjectClick);
    btnSaveProject.setOnAction(this::onSaveProjectClick);
    bBoxInfoPane.selectedClassNameProperty().addListener((src, oldLabel, updatedLabel) -> {
      graphicsPane.setSelectedClassLabel(updatedLabel);
    });

    btnAddingBoundingBox.selectedProperty().addListener((src, old, newValue) -> {
      graphicsPane.setCreatingNewBBox(newValue);
    });
  }

  private void renderImage(String imagePath) {
    File imgFile = new File(imagePath);
    Image image = new Image(imgFile.toURI().toString());
    graphicsPane.setImage(image);
  }

  private void onOpenProjectClick(ActionEvent evt) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters()
        .add(new FileChooser.ExtensionFilter("JSON", "*.json"));
    fileChooser.setTitle("Select JSON file contains project info");
    File projectFile = fileChooser.showOpenDialog(graphicsPane.getScene().getWindow());
    if(projectFile == null) {
      return;
    }
    loadProjectFromFile(projectFile);
  }

  private void onSaveProjectClick(ActionEvent actionEvent) {
    NautilusProject aProject = ApplicationConfig.getInstance().getProject();
    String fileName = aProject.getName() + ".json";
    ProjectSavingService savingService = new ProjectSavingService(ApplicationConfig.getInstance().getProject(), new File("D:\\data\\" + fileName));
    savingService.onFailedProperty().setValue(evt -> {
      AlertDialogUtil.showCommonAlert(Alert.AlertType.ERROR, "Save project",
          "Could not save project.",
          evt.getSource().getException().getMessage());
    });
    savingService.onSucceededProperty().setValue(evt -> {
      AlertDialogUtil.showCommonAlert(Alert.AlertType.INFORMATION, "Save project",
          "Project has been saved successfully",
          "");
    });
    savingService.start();
  }

  private void loadProjectFromFile(File jsonProject) {
    ProjectLoadingService loadingService = new ProjectLoadingService(jsonProject);
    loadingService.onFailedProperty().setValue(evt -> {
      AlertDialogUtil.showCommonAlert(Alert.AlertType.ERROR, "Error",
          "Could not load project.",
          evt.getSource().getException().getMessage());
    });
    loadingService.onSucceededProperty().setValue(evt -> {
      NautilusProject project = (NautilusProject) evt.getSource().getValue();
      ApplicationConfig.getInstance().setProject(project);
      Platform.runLater(() -> {
        ((Stage)graphicsPane.getScene().getWindow()).setTitle("Nautilus Image Annotation 1.0 - " + project.getName());
      });
    });
    loadingService.start();
  }
}
