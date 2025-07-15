package com.nautilus.nat.component;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Locale;

public class BoundingBoxInfoPane extends VBox {

  @FXML
  private TextField imageFolder;
  @FXML
  private Button btnSelectFile;
  @FXML
  private ListView<String> lvImages;
  @FXML
  private ListView<String> lvClasses;
  @FXML
  private Button btnDeleteClass;
  @FXML
  private Button btnNewClass;
  @FXML
  private Button btnSaveBoundingBoxes;

  private final StringProperty selectedDirectoryProperty = new SimpleStringProperty(this, "selectDirectoryProperty", null);
  private final StringProperty selectedFileProperty = new SimpleStringProperty(this, "selectedFileProperty", null);

  public BoundingBoxInfoPane() {
    super();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/bbox_info_pane.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    try {
      loader.load();
      double imageFilePathWidth = getPrefWidth() - 130;
      imageFolder.setPrefWidth(imageFilePathWidth);
      initialize();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public StringProperty selectedFileProperty() {
    return selectedFileProperty;
  }

  public StringProperty selectedDirectorProperty() {
    return selectedDirectoryProperty;
  }

  public String getSelectedDirector() {
    return selectedDirectoryProperty.get();
  }

  public void setSelectedDirectory(String dirPath) {
    selectedDirectoryProperty.set(dirPath);
    Platform.runLater(() -> {
      imageFolder.setText(selectedDirectoryProperty.get());
      readImagesFromFolder();
    });
  }

  private void initialize() {
    prefWidthProperty().addListener((src, oldWidth, newWidth) -> {
      double imageFilePathWidth = newWidth.doubleValue() - 130;
      imageFolder.setPrefWidth(imageFilePathWidth);
    });
    btnSelectFile.setOnAction(this::onBtnSelectFolderClick);

    lvImages.focusModelProperty().addListener((src, old, newV) -> {
      String fileName = newV.getFocusedItem();
      String fullPath = imageFolder.getText() + "/" + fileName;
      if (!fullPath.equals(selectedFileProperty.get())) {
        selectedFileProperty.set(fullPath);
      }
    });

    lvImages.getSelectionModel()
        .selectedItemProperty()
        .addListener((src, oldModel, newModel) -> {
      if (newModel != null) {
        String fullPath = imageFolder.getText() + "\\" + newModel;
        if (!fullPath.equals(selectedFileProperty.get())) {
          selectedFileProperty.set(fullPath);
        }
      }
    });
  }

  private void readImagesFromFolder() {
    String folder = imageFolder.getText();
    File parentFolder = new File(folder);
    FilenameFilter filenameFilter = (dir, name) -> {
      String lowerCaseName = name.toLowerCase(Locale.ROOT);
      return lowerCaseName.endsWith(".png") || lowerCaseName.endsWith(".jpg");
    };

    final File[] imageFiles = parentFolder.listFiles(filenameFilter);

    if (imageFiles != null) {
      Platform.runLater(() -> {
        lvImages.setItems(FXCollections.observableList(Arrays.stream(imageFiles).map(File::getName).toList()));
      });
    }
  }

  private void onBtnSelectFolderClick(ActionEvent evt) {
    DirectoryChooser dirChooser = new DirectoryChooser();
    File imageDir = dirChooser.showDialog(this.getScene().getWindow());
    if (imageDir != null) {
      imageFolder.setText(imageDir.getPath());
      Thread listFileThread = new Thread(this::readImagesFromFolder);
      listFileThread.start();
    }
  }

}
