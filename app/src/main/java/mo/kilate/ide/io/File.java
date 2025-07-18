package mo.kilate.ide.io;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import mo.kilate.ide.utils.FileUtil;

public class File extends java.io.File {
  public File(@NonNull final String path) {
    super(path);
  }

  public File(final java.io.File file, final String path) {
    super(file, path);
  }

  public File(final String p, final String pp) {
    super(p, pp);
  }

  public final boolean checkIsModified(final String content) {
    final String ogContent = FileUtil.readFile(this, false);
    return !ogContent.equals(content);
  }

  public static final List<File> toFiles(final List<java.io.File> ogFiles) {
    var toReturnList = new ArrayList<File>();
    ogFiles.forEach(
        ogFile -> {
          toReturnList.add(new File(ogFile.getAbsolutePath()));
        });
    return toReturnList;
  }
}
