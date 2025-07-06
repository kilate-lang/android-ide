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
          onBackPressedLogic();
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
    // Run button
    var runButton =
        menu.add(Menu.NONE, 0, Menu.NONE, StringUtil.getString(R.string.common_word_run));
    runButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    runButton.setIcon(R.drawable.ic_mtrl_run);
    // Save button
    var saveButton =
        menu.add(Menu.NONE, 1, Menu.NONE, StringUtil.getString(R.string.common_word_save));
    saveButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    saveButton.setIcon(R.drawable.ic_mtrl_save);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    int id = menuItem.getItemId();
    if (id == 0) {
      runCode();
      return true;
    } else if (id == 1) {
      save();
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

  private final void onBackPressedLogic() {
    new MaterialAlertDialogBuilder(this)
        .setTitle(StringUtil.getString(R.string.title_popup_want_save))
        .setMessage(StringUtil.getString(R.string.msg_popup_want_save))
        .setPositiveButton(
            StringUtil.getString(R.string.common_word_save),
            (d, w) -> {
              save(() -> finish());
            })
        .setNegativeButton(StringUtil.getString(R.string.text_exit_directy), (d, w) -> finish())
        .show();
  }

  private final void save() {
    save(() -> {});
  }

  private final void save(final Runnable afterSave) {
    showProgress();
    doWithProgress(
        () -> {
          FileUtil.writeText(editorState.currentFile, binding.editor.getText().toString());
          runOnUiThread(() -> getProgressDialog().getIndicator().setProgress(50, true));
        },
        () -> runOnUiThread(() -> getProgressDialog().getIndicator().setProgress(100, true)),
        () ->
            runOnUiThread(
                () -> {
                  toast(StringUtil.getString(R.string.text_saved) + '!');
                  dismissProgress();
                  afterSave.run();
                }));
  }

  private final void runCode() {
    showProgress();

    final var kilateRunner = new KilateCBridge(this);
    if (editorState.currentFile.checkIsModified(binding.editor.getText().toString())) {
      final File file = new File(getCacheDir(), editorState.currentFile.getName());
      FileUtil.writeText(file, binding.editor.getText().toString());
      kilateRunner.runFile(file);
    } else {
      kilateRunner.runFile(editorState.currentFile);
    }
    doWithProgress(
        () -> runOnUiThread(() -> getProgressDialog().getIndicator().setProgress(50, true)),
        () -> runOnUiThread(() -> getProgressDialog().getIndicator().setProgress(100, true)),
        () -> {
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
        });
  }
}
