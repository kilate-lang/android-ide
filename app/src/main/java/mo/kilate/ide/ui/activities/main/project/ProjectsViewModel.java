package mo.kilate.ide.ui.activities.main.project;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.Arrays;
import java.util.List;
import mo.kilate.ide.io.File;
import mo.kilate.ide.project.manage.ProjectManager;
import mo.kilate.ide.utils.PrintUtil;

public class ProjectsViewModel extends ViewModel {
  private final MutableLiveData<List<File>> projects = new MutableLiveData<>();

  public final void fetch() {
    var ogFiles = ProjectManager.getProjectsFile().listFiles();
    PrintUtil.print(ogFiles);
    if (ogFiles == null) return;
    projects.setValue(File.toFiles(Arrays.asList(ogFiles)));
  }

  public final LiveData<List<File>> getProjects() {
    return projects;
  }
}
