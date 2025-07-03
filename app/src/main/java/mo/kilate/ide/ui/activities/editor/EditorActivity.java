package mo.kilate.ide.ui.activities.editor;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import mo.kilate.editor.KilateEditorDefaults;
import mo.kilate.editor.cs.KilateMaterialColorScheme;
import mo.kilate.ide.R;
import mo.kilate.ide.c.KilateCBridge;
import mo.kilate.ide.databinding.ActivityEditorBinding;
import mo.kilate.ide.io.File;
import mo.kilate.ide.project.manage.ProjectManager;
import mo.kilate.ide.ui.base.BaseAppCompatActivity;
import mo.kilate.ide.utils.FileUtil;
import mo.kilate.ide.utils.StringUtil;

public class EditorActivity extends BaseAppCompatActivity {

  private ActivityEditorBinding binding;

  private OnBackPressedCallback onBackPressedCallback =
      new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
          finish();
        }
      };

  @Nullable private EditorState editorState;
  private ProjectManager projectManager;

  @Override
  @NonNull
  protected View bindLayout() {
    binding = ActivityEditorBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  protected void onBindLayout(@Nullable final Bundle savedInstanceState) {
    configureData(savedInstanceState);
    projectManager = new ProjectManager(this, editorState.project.name);
  }

  @Override
  public void onPostBind(@Nullable final Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
    configureToolbar(binding.toolbar);
    configureEditor();
  }

  @Override
  public void onConfigurationChanged(@NonNull final Configuration configuration) {
    super.onConfigurationChanged(configuration);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    var runButton =
        menu.add(Menu.NONE, 0, Menu.NONE, StringUtil.getString(R.string.common_word_run));
    runButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    runButton.setIcon(R.drawable.ic_mtrl_run);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    if (menuItem.getItemId() == 0) {
      runCode();
      return true;
    }
    return super.onOptionsItemSelected(menuItem);
  }

  @Override
  public void onSaveInstanceState(final Bundle bundle) {
    bundle.putParcelable("editor_state", editorState);
  }

  @Override
  protected void configureToolbar(@NonNull MaterialToolbar toolbar) {
    super.configureToolbar(toolbar);
    toolbar.setSubtitle(editorState.project.name);
  }

  /** Get and define all needed variables */
  private final void configureData(@Nullable final Bundle savedInstanceState) {
    if (savedInstanceState == null) {
      editorState = getParcelable("editor_state", EditorState.class);
    } else {
      editorState = getParcelable(savedInstanceState, "editor_state", EditorState.class);
    }
  }

  /** Configures the editor */
  private final void configureEditor() {
    if (editorState.currentFile == null) {
      editorState.currentFile =
          new File(
              new File(ProjectManager.getProjectsFile(), editorState.project.name), "main.klt");
    }
    binding.editor.addSuggestions(KilateEditorDefaults.Suggestions);
    binding.editor.setColorScheme(new KilateMaterialColorScheme(this));
    binding.editor.setTypeface(Typeface.MONOSPACE);
    binding.editor.setText(FileUtil.readFile(editorState.currentFile, false));
  }

  private final void save() {
    // todo save code from editor
  }

  private final void runCode() {
    showProgress();

    new Thread(
            () -> {
              try {
                runOnUiThread(() -> getProgressDialog().getIndicator().setProgress(50, true));
                Thread.sleep(250);
                var kilateRunner = new KilateCBridge(this);
                kilateRunner.runFile(editorState.currentFile);
                runOnUiThread(() -> getProgressDialog().getIndicator().setProgress(100, true));
                Thread.sleep(250);
                runOnUiThread(
                    () -> {
                      new MaterialAlertDialogBuilder(this)
                          .setTitle(StringUtil.getString(R.string.text_result))
                          .setMessage(
                              (kilateRunner.getOut().getBuffer().length() > 0)
                                  ? kilateRunner.getOut().toString()
                                  : kilateRunner.getErr().toString())
                          .setPositiveButton(
                              StringUtil.getString(R.string.common_word_ok), (d, w) -> d.dismiss())
                          .show();
                    });

                runOnUiThread(() -> dismissProgress());
              } catch (InterruptedException e) {
                openSimplePopup(StringUtil.getString(R.string.text_error), e.toString(), () -> {});
              }
            })
        .start();
  }
}
