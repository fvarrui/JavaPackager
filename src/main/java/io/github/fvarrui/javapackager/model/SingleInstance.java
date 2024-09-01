package io.github.fvarrui.javapackager.model;

public class SingleInstance {
  private String mutexName;
  private String windowTitle;
  
  public String getMutexName() {
    return mutexName;
  }
  
  public SingleInstance setMutexName(String mutexName) {
    this.mutexName = mutexName;
    return this;
  }
  
  public String getWindowTitle() {
    return windowTitle;
  }
  
  public SingleInstance setWindowTitle(String windowTitle) {
    this.windowTitle = windowTitle;
    return this;
  }
}
