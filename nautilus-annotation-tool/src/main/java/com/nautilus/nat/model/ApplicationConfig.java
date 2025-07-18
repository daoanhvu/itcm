package com.nautilus.nat.model;

import com.nautilus.nat.util.SystemUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ApplicationConfig {

  private final ObjectProperty<NautilusProject> projectProperty = new SimpleObjectProperty<>(this, "projectProperty", null);

  private static final ApplicationConfig INSTANCE = new ApplicationConfig();
  public final int OS_CODE;

  private ApplicationConfig() {
    OS_CODE = SystemUtil.detectOS();
  }

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
