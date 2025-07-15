package com.nautilus.nat.component;

import com.nautilus.nat.model.BoundingBox;
import com.nautilus.nat.model.BoundingUtils;
import com.nautilus.nat.model.ControlPoint;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

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

  private double lastMouseXPos;
  private double lastMouseYPos;
  private double previousMouseXPosOnClick;
  private double previousMouseYPosOnClick;

  /**
   * These are used for controlling the dragging
   */
  private final BooleanProperty creatingNewBBox = new SimpleBooleanProperty(this, "creatingNewBBox", false);
  private boolean resizingBBox = false;
  private BoundingBox selectedBoundingBox;
  private boolean drawingTempBBox = false;

  private ControlPoint mControlPoint = ControlPoint.NONE;
  private GraphicsPaneEventHandler listener;

  private Image image;
  private String imagePath;
  private String selectedClassLabel;
  private double imageDrawX;

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

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    imageCanvas.widthProperty().bind(this.widthProperty());
    imageCanvas.heightProperty().bind(this.heightProperty());
    boundingBoxCanvas.widthProperty().bind(this.widthProperty());
    boundingBoxCanvas.heightProperty().bind(this.heightProperty());

//        movingCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClick);
    boundingBoxCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, evt -> {
      System.out.println("MOUSE_PRESSED");
      lastMouseXPos = evt.getX();
      lastMouseYPos = evt.getY();
      previousMouseXPosOnClick = lastMouseXPos;
      previousMouseYPosOnClick = lastMouseYPos;
      final double mouseXPos = evt.getX();
      final double mouseYPos = evt.getY();

      // Check if user is trying to resize a bounding box
      for(BoundingBox s: boundingBoxes) {
        ControlPoint cp = BoundingUtils.getControlPoint(s, mouseXPos, mouseYPos);
        if(ControlPoint.LEFT.equals(cp)) {
          resizingBBox = true;
          mControlPoint = ControlPoint.LEFT;
          this.getScene().setCursor(Cursor.H_RESIZE);
          return;
        }

        if(ControlPoint.TOP.equals(cp)) {
          resizingBBox = true;
          mControlPoint = ControlPoint.TOP;
          this.getScene().setCursor(Cursor.V_RESIZE);
          return;
        }

        if(ControlPoint.RIGHT.equals(cp)) {
          resizingBBox = true;
          mControlPoint = ControlPoint.RIGHT;
          this.getScene().setCursor(Cursor.H_RESIZE);
          return;
        }

        if(ControlPoint.BOTTOM.equals(cp)) {
          resizingBBox = true;
          mControlPoint = ControlPoint.BOTTOM;
          this.getScene().setCursor(Cursor.V_RESIZE);
          return;
        }

        if(ControlPoint.BOTTOM_RIGHT.equals(cp)) {
          resizingBBox = true;
          mControlPoint = ControlPoint.BOTTOM_RIGHT;
          this.getScene().setCursor(Cursor.SE_RESIZE);
          return;
        }
      }
      System.out.println("MOUSE_PRESSED 8");
    });

    boundingBoxCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, evt -> {
      System.out.println("MOUSE_RELEASED");
      this.getScene().setCursor(Cursor.DEFAULT);

      if(creatingNewBBox.get()) {
        drawingTempBBox = false;
        double x0 = Math.min(previousMouseXPosOnClick, lastMouseXPos);
        double y0 = Math.min(previousMouseYPosOnClick, lastMouseYPos);
        double dx = Math.abs(previousMouseXPosOnClick - lastMouseXPos);
        double dy = Math.abs(previousMouseYPosOnClick - lastMouseYPos);

        if (dx > 4 && dy > 4) {
          if (selectedClassLabel == null || selectedClassLabel.isEmpty()) {
            AlertDialogUtil.showCommonAlert(Alert.AlertType.ERROR, "Missing class label",
                "Class label is not selected",
                "Please select a class label");
            return;
          }
          addBoundingBox(selectedClassLabel, x0, y0, dx, dy);
          renderBBoxesCanvas();
        }
      }
      resizingBBox = false;

      boundingBoxCanvas.setTranslateX(0);
      boundingBoxCanvas.setTranslateY(0);
    });

    /**
     * When a mouse drag occurs, it may be moving existing bounding box
     * or creating a new bounding box
     */
    boundingBoxCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, evt -> {
//            System.out.println("MOUSE_DRAGGED");
      double xOffset = lastMouseXPos - evt.getX();
      double yOffset = lastMouseYPos - evt.getY();
      lastMouseXPos = evt.getX();
      lastMouseYPos = evt.getY();

      if(creatingNewBBox.get()) {
        drawingTempBBox = true;
//        boundingBoxCanvas.setTranslateX(boundingBoxCanvas.getTranslateX() - xOffset);
//        boundingBoxCanvas.setTranslateY(boundingBoxCanvas.getTranslateY() - yOffset);
        renderBBoxesCanvas();
        drawingTempBBox = false;
      }

      if(resizingBBox) {

      }

    });
  }

  public void setSelectedClassLabel(String updatedLabel) {
    selectedClassLabel = updatedLabel;
  }

  public BooleanProperty creatingNewBBoxProperty() {
    return creatingNewBBox;
  }

  public void setCreatingNewBBox(boolean value) {
    creatingNewBBox.set(value);
  }

  public boolean isCreatingNewBBox() {
    return creatingNewBBox.get();
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
    renderImageCanvas();
    renderBBoxesCanvas();
  }

  private void renderImageCanvas() {
    final double width = imageCanvas.getWidth();
    final double height = imageCanvas.getHeight();
    final double ZERO = 0.0;
    if (width <= ZERO || height <= ZERO) {
      return;
    }
    if (image != null) {
      calcImagePosition();
      GraphicsContext imgGc = imageCanvas.getGraphicsContext2D();
      imgGc.clearRect(ZERO, ZERO, imageCanvas.getWidth(), imageCanvas.getHeight());
      imgGc.drawImage(image, imageDrawX, ZERO, image.getWidth(), image.getHeight());
    }
  }

  private void renderBBoxesCanvas() {
    final double width = boundingBoxCanvas.getWidth();
    final double height = boundingBoxCanvas.getHeight();
    final double ZERO = 0.0;
    if (width <= ZERO || height <= ZERO) {
      return;
    }
    GraphicsContext g = boundingBoxCanvas.getGraphicsContext2D();
    g.clearRect(ZERO, ZERO, width, height);
    Paint oldStroke = g.getStroke();
    g.setStroke(Color.BLUE);
    for(BoundingBox s: boundingBoxes) {
      BoundingUtils.draw(g, s);
    }

    if (drawingTempBBox) {
      g.setStroke(Color.DARKGREY);
      g.setLineDashes(4.0);
      double x0 = Math.min(previousMouseXPosOnClick, lastMouseXPos);
      double y0 = Math.min(previousMouseYPosOnClick, lastMouseYPos);
      g.strokeRect(x0, y0,
          Math.abs(lastMouseXPos - previousMouseXPosOnClick),
          Math.abs(lastMouseYPos - previousMouseYPosOnClick));
      g.setLineDashes(ZERO);
    }
    g.setStroke(oldStroke);
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

  public void addBoundingBox(String className, double x, double y, double width, double height) {
    BoundingBox shape = new BoundingBox();
    shape.setClassName(className);
    shape.setX(x);
    shape.setY(y);
    shape.setHeight(height);
    shape.setWidth(width);
    boundingBoxes.add(shape);
  }
}
