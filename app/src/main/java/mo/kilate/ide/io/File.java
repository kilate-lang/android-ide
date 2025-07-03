package mo.kilate.ide.io;

import androidx.annotation.NonNull;

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
}
