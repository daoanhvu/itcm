package com.nautilus.nat.model;

import java.util.List;

public class NautilusProject {
  private String name;
  private String location;
  private List<String> labels;
  private List<TrainingFileItem> files;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public List<String> getLabels() {
    return labels;
  }

  public void setLabels(List<String> labels) {
    this.labels = labels;
  }

  public List<TrainingFileItem> getFiles() {
    return files;
  }

  public void setFiles(List<TrainingFileItem> files) {
    this.files = files;
  }
}
