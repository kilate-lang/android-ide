package mo.kilate.ide.ui.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import mo.kilate.ide.beans.ProjectBean;
import mo.kilate.ide.databinding.ActivityMainBinding;
import mo.kilate.ide.ui.activities.editor.EditorActivity;
import mo.kilate.ide.ui.activities.editor.EditorState;
import mo.kilate.ide.ui.activities.main.components.CreateProjectDialog;
import mo.kilate.ide.ui.activities.main.project.ProjectsAdapter;
import mo.kilate.ide.ui.activities.main.project.ProjectsViewModel;
import mo.kilate.ide.ui.base.BaseAppCompatActivity;

public class MainActivity extends BaseAppCompatActivity {

  @NonNull private ActivityMainBinding binding;

  private ProjectsViewModel projectsViewModel;
  private ProjectsAdapter projectsAdapter;

  @Override
  @NonNull
  protected View bindLayout() {
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  protected void onBindLayout(@Nullable final Bundle savedInstanceState) {
    projectsViewModel = new ViewModelProvider(this).get(ProjectsViewModel.class);
    projectsAdapter = new ProjectsAdapter();
    projectsAdapter.setOnProjectClick(this::openProject);
    projectsViewModel.fetch();
    projectsViewModel.getProjects().observe(this, projectsAdapter::submitList);
    binding.list.setAdapter(projectsAdapter);
    binding.list.setLayoutManager(new LinearLayoutManager(this));
    binding.createNew.setOnClickListener(
        v -> {
          final var cpd = new CreateProjectDialog(this);
          cpd.show();
          cpd.setOnDismissListener(dialog -> projectsViewModel.fetch());
        });
  }

  private void openProject(final ProjectBean project) {
    final var editorState = new EditorState();
    editorState.project = project;
    final var intent = new Intent(this, EditorActivity.class);
    intent.putExtra("editor_state", editorState);
    startActivity(intent);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    this.binding = null;
  }
}
