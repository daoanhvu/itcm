package com.nautilus.nat.controller;

import com.nautilus.nat.component.BoundingBoxInfoPane;
import com.nautilus.nat.component.GraphicsPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
  @FXML
  private GraphicsPane graphicsPane;
  @FXML
  private BoundingBoxInfoPane bboxInfoPane;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    bboxInfoPane.getSelectedFileProperty()
        .addListener((src, oldPath, newPath) -> {
          Platform.runLater(() -> renderImage(newPath));
        });
  }

  private void renderImage(String imagePath) {
    File imgFile = new File(imagePath);
    Image image = new Image(imgFile.toURI().toString());
    graphicsPane.setImage(image);
  }
}
