package com.nautilus.nat.model;

public class BoundingBox {
  private int categoryIndex;
  private String category;
  private double x;
  private double y;
  private double width;
  private double height;

  public interface BoundingBoxChangeListener {
    void onPropertyChanged(BoundingBox bbox, Object notifier);
  }

  public int getCategoryIndex() {
    return categoryIndex;
  }

  public void setCategoryIndex(int categoryIndex) {
    this.categoryIndex = categoryIndex;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
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
    sb.append("\"category_index\":").append(categoryIndex).append(",");
    sb.append("\"category\":").append("\"").append(category).append("\",");
    sb.append("\"bbox\":").append("[").append(x).append(",").append(y).append(",").append(width).append(",").append(height).append("]");
    sb.append("}");
    return sb.toString();
  }
}
