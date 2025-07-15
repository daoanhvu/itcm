package com.nautilus.nat.fxservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nautilus.nat.model.NautilusProject;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileNotFoundException;

public class ProjectLoadingService extends Service<NautilusProject> {

  private final File projectFile;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public ProjectLoadingService(File file) {
    this.projectFile = file;
  }

  @Override
  protected Task<NautilusProject> createTask() {
    return new Task<NautilusProject>() {
      @Override
      protected NautilusProject call() throws Exception {
        if (!projectFile.exists()) {
          throw new FileNotFoundException(projectFile.getPath() + " is not existed.");
        }
        return objectMapper.readValue(projectFile, NautilusProject.class);
      }
    };
  }
}
