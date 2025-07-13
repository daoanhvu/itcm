package com.nautilus.nat.component;

import com.nautilus.nat.model.BoundingBox;
import com.nautilus.nat.model.BoundingUtils;
import com.nautilus.nat.model.ControlPoint;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GraphicsPane extends Pane implements Initializable, BoundingBox.BoundingBoxChangeListener {

  public interface GraphicsPaneEventHandler {
    void onShapeSelected(BoundingBox selected);
  }

  private final List<BoundingBox> boundingBoxes = new ArrayList<>();

  @FXML
  private Canvas imageCanvas;
  @FXML
  private Canvas boundingBoxCanvas;

  private double mouseXPosOnClick;
  private double mouseYPosOnClick;
  private double lastMouseXPos;
  private double lastMouseYPos;
  private double previousMouseXPosOnClick;
  private double previousMouseYPosOnClick;
  private boolean mouseDragging = false;
  private boolean resizing = false;
  private ControlPoint mControlPoint = ControlPoint.NONE;
  private GraphicsPaneEventHandler listener;

  private BoundingBox selectedBoundingBox;
  private Image image;
  private String imagePath;
  private double imageDrawX;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    imageCanvas.widthProperty().bind(this.widthProperty());
    imageCanvas.heightProperty().bind(this.heightProperty());
    boundingBoxCanvas.widthProperty().bind(this.widthProperty());
    boundingBoxCanvas.heightProperty().bind(this.heightProperty());

//        movingCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClick);
    boundingBoxCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, evt -> {
      System.out.println("MOUSE_PRESSED");
      lastMouseXPos = evt.getSceneX();
      lastMouseYPos = evt.getSceneY();
      final double mouseXPos = evt.getX();
      final double mouseYPos = evt.getY();

      // Check if user is trying to resize a bounding box
      for(BoundingBox s: boundingBoxes) {
        ControlPoint cp = BoundingUtils.getControlPoint(s, mouseXPos, mouseYPos);
        if(ControlPoint.LEFT.equals(cp)) {
          resizing = true;
          mControlPoint = ControlPoint.LEFT;
          this.getScene().setCursor(Cursor.H_RESIZE);
          return;
        }

        if(ControlPoint.TOP.equals(cp)) {
          resizing = true;
          mControlPoint = ControlPoint.TOP;
          this.getScene().setCursor(Cursor.V_RESIZE);
          return;
        }

        if(ControlPoint.RIGHT.equals(cp)) {
          resizing = true;
          mControlPoint = ControlPoint.RIGHT;
          this.getScene().setCursor(Cursor.H_RESIZE);
          return;
        }

        if(ControlPoint.BOTTOM.equals(cp)) {
          resizing = true;
          mControlPoint = ControlPoint.BOTTOM;
          this.getScene().setCursor(Cursor.V_RESIZE);
          return;
        }

        if(ControlPoint.BOTTOM_RIGHT.equals(cp)) {
          resizing = true;
          mControlPoint = ControlPoint.BOTTOM_RIGHT;
          this.getScene().setCursor(Cursor.SE_RESIZE);
          return;
        }
      }
      System.out.println("MOUSE_PRESSED 8");
    });

    boundingBoxCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, evt -> {
      System.out.println("MOUSE_RELEASED");
      previousMouseXPosOnClick = mouseXPosOnClick;
      previousMouseYPosOnClick = mouseYPosOnClick;
      this.getScene().setCursor(Cursor.DEFAULT);
      if(mouseDragging && selectedBoundingBox != null) {

        mouseDragging = false;
      }
      resizing = false;
      boundingBoxCanvas.setTranslateX(0);
      boundingBoxCanvas.setTranslateY(0);
    });

    /**
     * When a mouse drag occurs, it may be moving existing bounding box
     * or creating a new bounding box
     */
    boundingBoxCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, evt -> {
//            System.out.println("MOUSE_DRAGGED");
      double xOffset = lastMouseXPos - evt.getSceneX();
      double yOffset = lastMouseYPos - evt.getSceneY();
      lastMouseXPos = evt.getSceneX();
      lastMouseYPos = evt.getSceneY();

      if(mouseDragging) {
        boundingBoxCanvas.setTranslateX(boundingBoxCanvas.getTranslateX() - xOffset);
        boundingBoxCanvas.setTranslateY(boundingBoxCanvas.getTranslateY() - yOffset);
      }

      if(resizing) {

      }

    });

  }

  public GraphicsPane() {
    super();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/graphics_pane.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    try {
      loader.load();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setImage(Image image) {
    this.image = image;
    renderBBoxesCanvas();
  }

  public GraphicsPaneEventHandler getListener() {
    return listener;
  }

  public void setListener(GraphicsPaneEventHandler listener) {
    this.listener = listener;
  }

  private void onMouseClick(MouseEvent evt) {
    System.out.println("MOUSE CLICKED");
  }

  @Override
  public void resize(double w, double h) {
    super.resize(w, h);
    renderBBoxesCanvas();
  }

  private void renderBBoxesCanvas() {
    final double width = boundingBoxCanvas.getWidth();
    final double height = boundingBoxCanvas.getHeight();
    if (width <= 0.0 || height <= 0.0) {
      return;
    }
    if (image != null) {
      calcImagePosition();
      GraphicsContext imgGc = imageCanvas.getGraphicsContext2D();
      imgGc.clearRect(0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());
      imgGc.drawImage(image, imageDrawX, 0, image.getWidth(), image.getHeight());
    }
    GraphicsContext g = boundingBoxCanvas.getGraphicsContext2D();
    g.clearRect(0, 0, width, height);
    for(BoundingBox s: boundingBoxes) {
      BoundingUtils.draw(g, s);
    }
  }

  private void calcImagePosition() {
    if (imageCanvas.getWidth() > image.getWidth()) {
      imageDrawX = (imageCanvas.getWidth() - image.getWidth()) / 2.0;
    } else {
      imageDrawX = 0.0;
    }
  }


  @Override
  public void onPropertyChanged(BoundingBox srcShap, Object notifier) {
    Platform.runLater(this::renderBBoxesCanvas);
  }

  public void saveBoundingBoxes() {

  }

  public void addBoundingBox(String type) {
    BoundingBox shape = new BoundingBox();
    shape.setX(120);
    shape.setY(50);
    shape.setHeight(140);
    shape.setWidth(60);
    boundingBoxes.add(shape);
    renderBBoxesCanvas();
  }
}
