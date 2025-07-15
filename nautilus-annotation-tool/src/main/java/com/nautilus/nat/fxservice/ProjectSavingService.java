package com.nautilus.nat.fxservice;

import com.nautilus.nat.model.NautilusProject;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class ProjectSavingService extends Service<Boolean> {

  private final File projectFile;
  private final NautilusProject aProject;

  public ProjectSavingService(NautilusProject aProject, File file) {
    this.projectFile = file;
    this.aProject = aProject;
  }

  @Override
  protected Task<Boolean> createTask() {
    return new Task<Boolean>() {
      @Override
      protected Boolean call() throws Exception {
        if (aProject == null) {
          throw new IllegalArgumentException("Project to be saved can not be null");
        }
        try {
          final String projectContent = aProject.toString();
          Files.writeString(projectFile.toPath(), projectContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
          return true;
        } catch (Exception ex) {
          return false;
        }
      }
    };
  }
}
