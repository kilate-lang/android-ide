package mo.kilate.ide.ui.activities.editor;

import android.os.Parcel;
import android.os.Parcelable;
import mo.kilate.ide.beans.BaseBean;
import mo.kilate.ide.beans.ProjectBean;
import mo.kilate.ide.io.File;
import mo.kilate.ide.utils.ParcelUtil;
import mo.kilate.ide.utils.PrintUtil;

public class EditorState extends BaseBean implements Parcelable {

  public static final Creator<EditorState> CREATOR =
      new Creator<EditorState>() {

        public EditorState createFromParcel(Parcel parcel) {
          return new EditorState(parcel);
        }

        public EditorState[] newArray(int i) {
          return new EditorState[i];
        }
      };

  public ProjectBean project;
  public File currentFile;
  public int currentIndex;

  public EditorState() {}

  public EditorState(final Parcel parcel) {
    project = ParcelUtil.readParcelable(parcel, ProjectBean.class);
    currentFile = ParcelUtil.readSerializable(parcel, File.class);
    currentIndex = parcel.readInt();
  }

  public EditorState(final File currentFile, final int currentIndex) {
    this.currentIndex = currentIndex;
    this.currentFile = currentFile;
  }

  public EditorState(final int currentIndex, final File currentFile) {
    this(currentFile, currentIndex);
  }

  @Override
  public Creator getCreator() {
    return CREATOR;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(final Parcel parcel, int flags) {
    parcel.writeParcelable(project, flags);
    parcel.writeSerializable(currentFile);
    parcel.writeInt(currentIndex);
  }

  @Override
  public void print() {
    project.print();
    PrintUtil.print(currentFile);
    PrintUtil.print(currentIndex);
  }
}
