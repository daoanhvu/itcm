package com.nautilus.nat.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ApplicationConfig {

  private final ObjectProperty<NautilusProject> projectProperty = new SimpleObjectProperty<>(this, "projectProperty", null);

  private static final ApplicationConfig INSTANCE = new ApplicationConfig();

  private ApplicationConfig() { }

  public static ApplicationConfig getInstance() {
    return INSTANCE;
  }

  public ObjectProperty<NautilusProject> projectObjectProperty() {
    return projectProperty;
  }

  public NautilusProject getProject() {
    return projectProperty.get();
  }

  public void setProject(NautilusProject project) {
    projectProperty.set(project);
  }
}
