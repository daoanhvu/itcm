package com.nautilus.nat.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public final class BoundingUtils {
  private static final double ControlPointRadius = 4.0;
  private static final double ControlPointDiameter = ControlPointRadius * 2.0;
  
  public static ControlPoint getControlPoint(BoundingBox bbox, double inputX, double inputY) {
    final float x = bbox.getX();
    final float y = bbox.getY();
    final float width = bbox.getWidth();
    final float height = bbox.getHeight();
    
    if(inputX > (x - ControlPointRadius) && (inputX < x + ControlPointRadius)
        && (inputY > bbox.getY() + ControlPointRadius) && inputY < (y + height - ControlPointRadius)) {
      return ControlPoint.LEFT;
    }

    if(inputX > (x + ControlPointRadius) && (inputX < (x + width - ControlPointRadius))
        && (inputY > (y - ControlPointRadius)) && inputY < (y + ControlPointRadius)) {
      return ControlPoint.TOP;
    }

    if((inputX > (x + width - ControlPointRadius)) && (inputX < (x + width + ControlPointRadius))
        && (inputY > (y + ControlPointRadius)) && inputY < (y + height - ControlPointRadius)) {
      return ControlPoint.RIGHT;
    }

    if(inputX > (x + ControlPointRadius) && (inputX < (x + width - ControlPointRadius))
        && (inputY > (y + height - ControlPointRadius)) && inputY < (y + height + ControlPointDiameter)) {
      return ControlPoint.BOTTOM;
    }

    if(inputX > (x - ControlPointRadius) && (inputX < x + ControlPointRadius)
        && (inputY > (y - ControlPointRadius)) && inputY < y + ControlPointRadius) {
      return ControlPoint.TOP_LEFT;
    }

    if(inputX > (x + width) && (inputX < (x + width + ControlPointRadius))
        && (inputY > (y - ControlPointRadius)) && inputY < y + ControlPointRadius ) {
      return ControlPoint.TOP_RIGHT;
    }

    if(inputX > (x - ControlPointRadius) && (inputX < x + ControlPointRadius)
        && (inputY > (y + height - ControlPointRadius)) && inputY < (y + height + ControlPointRadius)) {
      return ControlPoint.BOTTOM_LEFT;
    }

    if(inputX > (x + width - ControlPointRadius) && (inputX < (x + width + ControlPointRadius))
        && (inputY > (y + height - ControlPointRadius)) && inputY < (y + height + ControlPointRadius)) {
      return ControlPoint.BOTTOM_RIGHT;
    }
    return ControlPoint.NONE;
  }

  public static void draw(GraphicsContext g, BoundingBox bbox) {
    Paint oldFill = g.getFill();
    float xx = bbox.getX();
    float yy = bbox.getY();
    g.strokeText(bbox.getClassName(), xx, yy - 24);
    g.strokeRect(xx, yy, bbox.getWidth(), bbox.getHeight());
    g.setFill(oldFill);
  }
}
