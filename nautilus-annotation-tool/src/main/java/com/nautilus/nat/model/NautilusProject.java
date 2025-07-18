package com.nautilus.nat.model;

import java.util.List;

public class NautilusProject {
  private String name;
  private String location;
  private List<String> categories;
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

  public List<String> getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  public List<TrainingFileItem> getFiles() {
    return files;
  }

  public void setFiles(List<TrainingFileItem> files) {
    this.files = files;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("{");
    sb.append("\"name\":").append("\"").append(name).append("\",");
    sb.append("\"location\":").append("\"").append(location.replace("\\", "\\\\")).append("\",");
    sb.append("\"categories\":").append("[");
    if (categories != null && !categories.isEmpty()) {
      int labelCount = categories.size();
      for (int i = 0; i < labelCount - 1; i++) {
        sb.append("\"").append(categories.get(i)).append("\",");
      }
      sb.append("\"").append(categories.get(labelCount-1)).append("\"");
    }
    sb.append("],");
    sb.append("\"files\":").append("[");
    if (files != null && !files.isEmpty()) {
      int fileCount = files.size();
      for (int i = 0; i < fileCount - 1; i++) {
        sb.append(files.get(i).toString()).append(",");
      }
      sb.append(files.get(fileCount-1).toString());
    }
    sb.append("]");
    sb.append("}");
    return sb.toString();
  }
}
