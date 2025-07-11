package mo.kilate.ide.c;

import android.content.Context;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import mo.kilate.ide.io.File;

public class KilateCBridge {
  private final Executor executor;
  private final Context context;

  public KilateCBridge(final Context context) {
    this.context = context;
    executor = new Executor();
  }

  public final StringWriter runFile(final File file) {
    return runFile(new File(file.getParentFile().getAbsolutePath()), file);
  }

  public final StringWriter runFile(final File workingDir, final File file) {
    executor.setWorkingDirectory(workingDir);
    final String relativePath =
        workingDir.toPath().toAbsolutePath().relativize(file.toPath().toAbsolutePath()).toString();
    executor.setCommands(List.of(getBinaryFile(context).getAbsolutePath(), "run", relativePath));
    try {
      return executor.execute().getOut();
    } catch (final Exception e) {
      e.printStackTrace(new PrintWriter(getErr()));
      return getOut();
    }
  }

  public final StringWriter getOut() {
    return executor.getOut();
  }

  public final StringWriter getErr() {
    return executor.getErr();
  }

  public static final File getBinaryFile(final Context context) {
    var bin = new File(context.getApplicationInfo().nativeLibraryDir, "libkilate.so");
    return bin;
  }
}
