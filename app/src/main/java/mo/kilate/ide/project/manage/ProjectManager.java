package mo.kilate.ide.project.manage;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import mo.kilate.ide.KilateIDE;
import mo.kilate.ide.base.Contextualizable;
import mo.kilate.ide.beans.ProjectBean;
import mo.kilate.ide.io.File;
import mo.kilate.ide.utils.FileUtil;

public class ProjectManager extends Contextualizable {
  private String name;

  public ProjectManager(@NonNull final Context context) {
    this(context, null);
  }

  public ProjectManager(@NonNull final Context context, final String name) {
    super(context);
    this.name = name;
  }

  @Nullable
  public final ProjectBean getCurrentProject() {
    return getProjectByName(name);
  }

  /**
   * Creates and returns a ProjectBean based in files by scId
   *
   * @param scId The id of project to be searched
   */
  @NonNull
  public static final ProjectBean getProjectByName(final String name) {
    var toReturnProject = new ProjectBean();
    toReturnProject.name = name;
    return toReturnProject;
  }

  /**
   * Creates nescessary files of project
   *
   * @param project The instance of ProjectBean with data to be created.
   */
  public static final void createProjectByBean(@NonNull final ProjectBean project) {
    var projectRootDir = new File(getProjectsFile(), project.name);
    FileUtil.makeDir(projectRootDir);
    var projectMain = new File(projectRootDir, "src/main.klt");
    FileUtil.makeDir(new File(projectMain.getParentFile().getAbsolutePath()));
    FileUtil.writeText(
        projectMain,
        """
    work main(): bool {
      print -> "Hello, world!", "\\n"
      return -> true
    }
    """);
  }

  /** Folder where all projects are stored */
  public static final File getProjectsFile() {
    return new File(KilateIDE.getPublicFolderFile(), "projects");
  }

  @Nullable
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }
}
