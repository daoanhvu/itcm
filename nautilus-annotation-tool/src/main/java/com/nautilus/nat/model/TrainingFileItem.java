package com.nautilus.nat.model;

import java.util.List;

public class TrainingFileItem {
  private String name;
  private List<BoundingBox> annotations;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<BoundingBox> getAnnotations() {
    return annotations;
  }

  public void setAnnotations(List<BoundingBox> annotations) {
    this.annotations = annotations;
  }
}
