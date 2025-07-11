package mo.kilate.ide.ui.activities.editor;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import mo.kilate.editor.KilateEditor;
import mo.kilate.editor.KilateEditorDefaults;
import mo.kilate.editor.cs.KilateMaterialColorScheme;
import mo.kilate.ide.R;
import mo.kilate.ide.beans.ProjectBean;
import mo.kilate.ide.c.KilateCBridge;
import mo.kilate.ide.databinding.ActivityEditorBinding;
import mo.kilate.ide.io.File;
import mo.kilate.ide.project.manage.ProjectManager;
import mo.kilate.ide.ui.activities.editor.components.FileNode;
import mo.kilate.ide.ui.base.BaseAppCompatActivity;
import mo.kilate.ide.ui.components.dialog.InputBottomSheet;
import mo.kilate.ide.ui.components.dialog.OptionsBottomSheetDialog;
import mo.kilate.ide.utils.FileUtil;
import mo.kilate.ide.utils.StringUtil;

public class EditorActivity extends BaseAppCompatActivity
    implements TabLayout.OnTabSelectedListener {

  private ActivityEditorBinding binding;
  private EditorViewModel editorViewModel;
  private ActionBarDrawerToggle actionBarDrawerToggle;
  private ProjectBean project;

  private OnBackPressedCallback onBackPressedCallback =
      new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
          onBackPressedLogic();
        }
      };

  @Override
  @NonNull
  protected View bindLayout() {
    binding = ActivityEditorBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  protected void onBindLayout(@Nullable final Bundle savedInstanceState) {
    configureData(savedInstanceState);
    editorViewModel = new ViewModelProvider(this).get(EditorViewModel.class);
  }

  @Override
  public void onPostBind(@Nullable final Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
    observeViewModel();
    configureToolbar(binding.toolbar);
    configureDrawer();
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
    if (actionBarDrawerToggle.onOptionsItemSelected(menuItem)) {
      return true;
    }
    return super.onOptionsItemSelected(menuItem);
  }

  @Override
  public void onSaveInstanceState(final Bundle bundle) {
    bundle.putParcelable("project_bean", project);
  }

  /** Get and define all needed variables */
  private final void configureData(@Nullable final Bundle savedInstanceState) {
    if (savedInstanceState == null) {
      project = getParcelable("project_bean", ProjectBean.class);
    } else {
      project = getParcelable(savedInstanceState, "project_bean", ProjectBean.class);
    }
  }

  @Override
  protected void configureToolbar(@NonNull MaterialToolbar toolbar) {
    super.configureToolbar(toolbar);
    toolbar.setSubtitle(project.name);
  }

  @Override
  public void onTabSelected(final TabLayout.Tab tab) {
    editorViewModel.setCurrentFile(tab.getPosition());
  }

  @Override
  public void onTabUnselected(final TabLayout.Tab tab) {}

  @Override
  public void onTabReselected(final TabLayout.Tab tab) {
    var pm = new PopupMenu(this, tab.view);
    pm.getMenu().add(0, 0, 0, StringUtil.getString(R.string.common_word_close));
    pm.getMenu().add(0, 1, 0, StringUtil.getString(R.string.common_word_close_others));
    pm.getMenu().add(0, 2, 0, StringUtil.getString(R.string.common_word_close_all));
    pm.setOnMenuItemClickListener(
        item -> {
          switch (item.getItemId()) {
            case 0 -> editorViewModel.closeFile(tab.getPosition());
            case 1 -> editorViewModel.closeOthers();
            case 2 -> editorViewModel.closeAll();
          }
          ;
          return true;
        });
    pm.show();
  }

  /** Configures drawer */
  private final void configureDrawer() {
    binding.tabs.addOnTabSelectedListener(this);
    binding.drawer.setOnFileClickListener(
        (n, v) -> {
          var fileNode = (FileNode) v;
          if (!fileNode.getAbsolute().isDirectory()) {
            if (binding.drawerLayout.isDrawerOpen(Gravity.START)) {
              binding.drawerLayout.closeDrawer(Gravity.START);
            }
            editorViewModel.openFile(fileNode.getAbsolute());
          }
        });
    binding.drawer.setOnNodeLongClickListener(
        (n, v) -> {
          var fileNode = (FileNode) v;
          if (fileNode.getAbsolute().isDirectory()) {
            new OptionsBottomSheetDialog(this)
                .setTitle(StringUtil.getString(R.string.title_options))
                .add(getCreateOption(fileNode))
                .add(getDeleteOption(fileNode))
                .show();
          } else {
            new OptionsBottomSheetDialog(this)
                .setTitle(StringUtil.getString(R.string.title_options))
                .add(getDeleteOption(fileNode))
                .show();
          }
          return true;
        });

    binding.drawer.setDirectory(new File(ProjectManager.getProjectsFile(), project.name));
    actionBarDrawerToggle =
        new ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.common_word_open,
            R.string.common_word_close);
    binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);
    actionBarDrawerToggle.syncState();
    var main = new File(ProjectManager.getProjectsFile(), project.name + "/src/main.klt");
    if (main.exists()) {
      editorViewModel.openFile(main);
    }
  }

  private final void observeViewModel() {
    editorViewModel
        .getEditorState()
        .observe(
            this,
            state -> {
              int index = state.currentIndex;
              if (index >= 0 && index < binding.editorContainer.getChildCount()) {
                binding.editorContainer.setDisplayedChild(index);
                TabLayout.Tab tab = binding.tabs.getTabAt(index);
                if (tab != null && !tab.isSelected()) tab.select();
              }
            });

    editorViewModel
        .getFiles()
        .observe(
            this,
            files -> {
              binding.tabs.setVisibility((files.size() > 0) ? View.VISIBLE : View.GONE);
              binding.tabs.removeAllTabs();
              binding.editorContainer.removeAllViews();

              for (int i = 0; i < files.size(); i++) {
                final File file = files.get(i);

                binding.editorContainer.addView(createKilateEditor(file));

                final TabLayout.Tab tab = binding.tabs.newTab().setText(file.getName());
                binding.tabs.addTab(tab, i == editorViewModel.getCurrentFileIndex());
                if (tab != null && !tab.isSelected()) tab.select();
              }

              binding.editorContainer.setDisplayedChild(editorViewModel.getCurrentFileIndex());
            });

    editorViewModel
        .getEditorEvent()
        .observe(
            this,
            event -> {
              if (event instanceof EditorViewModel.EditorEvent.OpenFile e) {
                handleOpenFile(e.file);
              } else if (event instanceof EditorViewModel.EditorEvent.CloseFile e) {
                handleCloseFile(e.index);
              } else if (event instanceof EditorViewModel.EditorEvent.CloseOthers) {
                handleCloseOthers();
              } else if (event instanceof EditorViewModel.EditorEvent.CloseAll) {
                handleCloseAll();
              }
            });
  }

  private final KilateEditor createKilateEditor(final File file) {
    final KilateEditor editorView = new KilateEditor(this);
    editorView.setLayoutParams(
        new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    editorView.setText(FileUtil.readFile(file, false));
    editorView.setColorScheme(new KilateMaterialColorScheme(this));
    editorView.setTypeface(Typeface.MONOSPACE);
    editorView.addSuggestions(new ArrayList<>(KilateEditorDefaults.Suggestions));
    return editorView;
  }

  private void handleOpenFile(File file) {
    final List<File> files = editorViewModel.getOpenedFiles();
    for (int i = 0; i < files.size(); i++) {
      if (files.get(i).getAbsolutePath().equals(file.getAbsolutePath())) {
        editorViewModel.setCurrentFile(i);
        return;
      }
    }
    editorViewModel.addFile(file);
    editorViewModel.setCurrentFile(files.size());
  }

  private void handleCloseFile(final int index) {
    editorViewModel.removeFile(index);
    final int size = editorViewModel.getFileCount();
    if (size == 0) {
      editorViewModel.setCurrentFile(-1);
    } else {
      editorViewModel.setCurrentFile(Math.max(0, index - 1));
    }
  }

  private void handleCloseOthers() {
    final int currentIndex = editorViewModel.getCurrentFileIndex();
    final File current = editorViewModel.getCurrentFile();
    editorViewModel.setFiles(new ArrayList<>(List.of(current)));
    editorViewModel.setCurrentFile(0);
  }

  private void handleCloseAll() {
    editorViewModel.setFiles(new ArrayList<>());
    editorViewModel.setCurrentFile(-1);
  }

  private final void onBackPressedLogic() {
    if (binding.drawerLayout.isDrawerOpen(Gravity.START)) {
      binding.drawerLayout.closeDrawer(Gravity.START);
      return;
    }
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

  private final OptionsBottomSheetDialog.Option getCreateOption(final FileNode node) {
    return new OptionsBottomSheetDialog.Option(
        StringUtil.getString(R.string.common_word_create_new),
        R.drawable.ic_add,
        (obsd, op) -> {
          new InputBottomSheet(this)
              .setTitle(StringUtil.getString(R.string.common_word_name))
              .setSaveListener(
                  (ibs, value) -> {
                    var file = new File(node.getAbsolute(), value);
                    if (value.endsWith("/")) {
                      FileUtil.makeDir(file);
                    } else {
                      FileUtil.writeText(file, "");
                    }
                    toast(getString(R.string.msg_file_created, file.getAbsolutePath()));
                    ibs.dismiss();
                    obsd.dismiss();
                  })
              .show();
        });
  }

  private final OptionsBottomSheetDialog.Option getDeleteOption(final FileNode node) {
    return new OptionsBottomSheetDialog.Option(
        StringUtil.getString(R.string.common_word_delete),
        R.drawable.ic_mtrl_delete,
        (obsd, op) -> {
          if (node.getAbsolute().exists()) {
            if (FileUtil.deleteRecursive(node.getAbsolute())) {
              toast(getString(R.string.msg_file_deleted, node.getAbsolute().getAbsolutePath()));
            }
          }
          obsd.dismiss();
        });
  }

  private final void save() {
    save(() -> {});
  }

  private final void save(final Runnable afterSave) {
    showProgress();
    doWithProgress(
        () -> {
          final int index = editorViewModel.getCurrentFileIndex();
          if (index >= 0 && index < binding.editorContainer.getChildCount()) {
            final KilateEditor editor = (KilateEditor) binding.editorContainer.getChildAt(index);
            final File file = editorViewModel.getOpenedFiles().get(index);
            FileUtil.writeText(file, editor.getText().toString());
          }
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
    final int index = editorViewModel.getCurrentFileIndex();
    if (index >= 0 && index < binding.editorContainer.getChildCount()) {
      final KilateEditor editor = (KilateEditor) binding.editorContainer.getChildAt(index);
      final File file = editorViewModel.getOpenedFiles().get(index);
      runCode(file);
    }
  }

  private final void runCode(final File file) {
    showProgress();
    final var kilateRunner = new KilateCBridge(this);
    kilateRunner.runFile(new File(ProjectManager.getProjectsFile(), project.name), file);
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
