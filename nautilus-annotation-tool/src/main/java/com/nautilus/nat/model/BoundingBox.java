package com.nautilus.nat.model;

public class BoundingBox {
  private int classIndex;
  private String className;
  private double x;
  private double y;
  private double width;
  private double height;

  public interface BoundingBoxChangeListener {
    void onPropertyChanged(BoundingBox bbox, Object notifier);
  }

  public int getClassIndex() {
    return classIndex;
  }

  public void setClassIndex(int classIndex) {
    this.classIndex = classIndex;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public double getWidth() {
    return width;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public double getHeight() {
    return height;
  }

  public void setHeight(double height) {
    this.height = height;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("{");
    sb.append("\"category_id\":").append(classIndex).append(",");
    sb.append("\"category_name\":").append("\"").append(className).append("\",");
    sb.append("\"bbox\":").append("[").append(x).append(",").append(y).append(",").append(width).append(",").append(height).append("]");
    sb.append("}");
    return sb.toString();
  }
}
