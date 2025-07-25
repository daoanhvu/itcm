package com.nautilus.nat.component;

import com.nautilus.nat.model.ApplicationConfig;
import com.nautilus.nat.model.NautilusProject;
import com.nautilus.nat.model.TrainingFileItem;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

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

  private UIActionListener actionListener;

  private final StringProperty selectedDirectoryProperty = new SimpleStringProperty(this, "selectDirectoryProperty", null);
  private final ObjectProperty<TrainingFileItem> selectedFileProperty = new SimpleObjectProperty<>(this, "selectedFileProperty", null);
  private final StringProperty selectedClassNameProperty = new SimpleStringProperty(this, "selectedClassNameProperty", null);

  private final ReadWriteLock unSavedChangeLock = new ReentrantReadWriteLock();
  private boolean hasUnsavedChange = false;

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

  public ObjectProperty<TrainingFileItem> selectedFileProperty() {
    return selectedFileProperty;
  }

  public StringProperty selectedDirectorProperty() {
    return selectedDirectoryProperty;
  }

  public StringProperty selectedClassNameProperty() {
    return selectedClassNameProperty;
  }

  public String getSelectedDirector() {
    return selectedDirectoryProperty.get();
  }

  private void initialize() {
    prefWidthProperty().addListener((src, oldWidth, newWidth) -> {
      /* 130 is the width of the label plus the width of the button "select folder"
      * */
      double imageFilePathWidth = newWidth.doubleValue() - 130;
      imageFolder.setPrefWidth(imageFilePathWidth);
    });
    btnSelectFile.setOnAction(this::onBtnSelectFolderClick);

    lvImages.getSelectionModel()
        .selectedItemProperty()
        .addListener((src, oldModel, newModel) -> {
          if (newModel != null) {
            String fullPath = imageFolder.getText() + "\\" + newModel;
            if (selectedFileProperty.get() == null || !fullPath.equals(selectedFileProperty.get().getFullPath())) {
              synchronized (unSavedChangeLock) {
                if (hasUnsavedChange) {
                  Alert saveChangesQuestionDlg = new Alert(Alert.AlertType.CONFIRMATION);
                  saveChangesQuestionDlg.setTitle("Unsaved Changes");
                  saveChangesQuestionDlg.setHeaderText("You have unsaved changes.");
                  saveChangesQuestionDlg.setContentText("Do you want to save before switching?");
                  ButtonType yes = new ButtonType("Yes");
                  ButtonType no = new ButtonType("No");
                  saveChangesQuestionDlg.getButtonTypes().setAll(yes, no);

                  Optional<ButtonType> result = saveChangesQuestionDlg.showAndWait();
                  if (result.isPresent() && result.get() == yes) {
                    // Simulate save action
                    System.out.println("Saving changes...");
                    actionListener.saveBoundingBoxes();
                    hasUnsavedChange = false;
                  }
                }
              }
              /*
               * Everytime we select a new file, we need to check if there is a
               * training item associate with that file in the project, if not
               * then we need to create one
               */
              NautilusProject currentProject = ApplicationConfig.getInstance().getProject();
              final Map<String, TrainingFileItem> mapByFileName = currentProject.getFiles().stream()
                  .collect(Collectors.toMap(TrainingFileItem::getName, Function.identity(), (existing, newFound) -> existing));

              TrainingFileItem fileItem = mapByFileName.get(newModel);
              if (fileItem == null) {
                fileItem = new TrainingFileItem();
                fileItem.setName(newModel);
                fileItem.setFullPath(currentProject.getLocation() + "/" + newModel);
                // TODO: Does it cause a event fire for the "projectProperty" in ApplicationConfig?
                currentProject.getFiles().add(fileItem);
              }

              selectedFileProperty.set(fileItem);
            }
          }
    });

    lvClasses.getSelectionModel()
        .selectedItemProperty()
        .addListener((src, oldLabel, newLabel) -> {
          selectedClassNameProperty.set(newLabel);
        });

    // Listen to the change from application project so that we can update accordingly
    ApplicationConfig.getInstance()
        .projectObjectProperty().addListener((src, oldProject, newProject) -> {
          selectedDirectoryProperty.set(newProject.getLocation());
          Platform.runLater(() -> {
            imageFolder.setText(selectedDirectoryProperty.get());
            lvImages.setItems(FXCollections.observableList(newProject
                .getFiles()
                .stream()
                .map(TrainingFileItem::getName).toList()));
            lvClasses.setItems(FXCollections.observableList(newProject.getCategories()));
//            readImagesFromFolder();
          });
        });
  }

  public void setActionListener(UIActionListener actionListener) {
    this.actionListener = actionListener;
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

  public void setHasUnsavedChange(boolean value) {
    synchronized (unSavedChangeLock) {
      hasUnsavedChange = value;
    }
  }

}
