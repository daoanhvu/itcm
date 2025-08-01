package com.nautilus.nat.model;

import java.util.ArrayList;
import java.util.List;

public class TrainingFileItem {
  private String name;
  private String fullPath;
  private final List<BoundingBox> annotations = new ArrayList<>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFullPath() {
    return fullPath;
  }

  public void setFullPath(String fullPath) {
    this.fullPath = fullPath;
  }

  public List<BoundingBox> getAnnotations() {
    return annotations;
  }

  public void setAnnotations(List<BoundingBox> bBoxes) {
    this.annotations.clear();
    this.annotations.addAll(bBoxes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("{");
    sb.append("\"name\":").append("\"").append(name).append("\",");
    sb.append("\"annotations\":").append("[");
    if (!annotations.isEmpty()) {
      int annotationCount = annotations.size();
      for (int i = 0; i < annotationCount - 1; i++) {
        sb.append(annotations.get(i).toString()).append(",");
      }
      sb.append(annotations.get(annotationCount-1).toString());
    }
    sb.append("]");
    sb.append("}");
    return sb.toString();
  }
}
