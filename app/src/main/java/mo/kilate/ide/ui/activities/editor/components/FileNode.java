package mo.kilate.ide.ui.activities.editor.components;

import mo.kilate.ide.io.File;

public class FileNode {
  private String name;
  private boolean isDirectory;
  private File absolute;

  public FileNode(final File absolute) {
    this.name = absolute.getName();
    this.isDirectory = absolute.isDirectory();
    this.absolute = absolute;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isDirectory() {
    return this.isDirectory;
  }

  public void setIsDirectory(boolean isDirectory) {
    this.isDirectory = isDirectory;
  }

  public File getAbsolute() {
    return this.absolute;
  }

  public void setAbsolute(File absolute) {
    this.absolute = absolute;
  }
}
