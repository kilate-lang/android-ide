package mo.kilate.ide.ui.activities.editor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;
import mo.kilate.ide.io.File;

public class EditorViewModel extends ViewModel {

  private final MutableLiveData<EditorState> _editorState =
      new MutableLiveData<>(new EditorState(-1, null));
  private final MutableLiveData<EditorEvent> _editorEvent = new MutableLiveData<>();
  private final MutableLiveData<List<File>> _files = new MutableLiveData<>(new ArrayList<>());

  public LiveData<EditorState> getEditorState() {
    return _editorState;
  }

  public LiveData<EditorEvent> getEditorEvent() {
    return _editorEvent;
  }

  public LiveData<List<File>> getFiles() {
    return _files;
  }

  public List<File> getOpenedFiles() {
    List<File> value = _files.getValue();
    return value != null ? value : new ArrayList<>();
  }

  public int getFileCount() {
    List<File> value = _files.getValue();
    return value != null ? value.size() : 0;
  }

  public int getCurrentFileIndex() {
    EditorState state = _editorState.getValue();
    return state != null ? state.currentIndex : -1;
  }

  public File getCurrentFile() {
    EditorState state = _editorState.getValue();
    return state != null ? state.currentFile : null;
  }

  public void openFile(File file) {
    _editorEvent.setValue(new EditorEvent.OpenFile(file));
  }

  public void closeFile(int index) {
    _editorEvent.setValue(new EditorEvent.CloseFile(index));
  }

  public void closeOthers() {
    _editorEvent.setValue(EditorEvent.CloseOthers.INSTANCE);
  }

  public void closeAll() {
    _editorEvent.setValue(EditorEvent.CloseAll.INSTANCE);
  }

  public void setCurrentFile(int index) {
    List<File> files = getOpenedFiles();
    if (index >= 0 && index < files.size()) {
      _editorState.setValue(new EditorState(index, files.get(index)));
    }
  }

  public void addFile(File file) {
    List<File> files = _files.getValue();
    if (files != null) {
      files.add(file);
      _files.setValue(files);
    }
  }

  public void setFiles(List<File> files) {
    _files.setValue(files);
  }

  public void removeFile(int index) {
    List<File> files = _files.getValue();
    if (files != null && index >= 0 && index < files.size()) {
      files.remove(index);
      _files.setValue(files);
    }
  }

  public interface EditorEvent {
    class OpenFile implements EditorEvent {
      public final File file;

      public OpenFile(File file) {
        this.file = file;
      }
    }

    class CloseFile implements EditorEvent {
      public final int index;

      public CloseFile(int index) {
        this.index = index;
      }
    }

    class CloseOthers implements EditorEvent {
      public static final CloseOthers INSTANCE = new CloseOthers();

      private CloseOthers() {}
    }

    class CloseAll implements EditorEvent {
      public static final CloseAll INSTANCE = new CloseAll();

      private CloseAll() {}
    }
  }
}
