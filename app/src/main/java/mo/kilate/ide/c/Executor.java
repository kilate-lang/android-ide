package mo.kilate.ide.c;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;
import mo.kilate.ide.io.File;

public class Executor {
  protected final ProcessBuilder mProcessBuilder;
  protected final StringWriter out;
  protected final StringWriter err;

  public Executor() {
    mProcessBuilder = new ProcessBuilder();
    out = new StringWriter();
    err = new StringWriter();
  }

  public Executor(final List<String> commands) {
    this();
    setCommands(commands);
  }

  public Executor(final String[] commands) {
    this();
    setCommands(commands);
  }

  public final Executor setCommands(final String[] commands) {
    mProcessBuilder.command(commands);
    return this;
  }

  public final Executor setCommands(final List<String> commands) {
    mProcessBuilder.command(commands);
    return this;
  }

  public final Executor setWorkingDirectory(final File file) {
    mProcessBuilder.directory(file);
    return this;
  }

  public final StringWriter getOut() {
    return out;
  }

  public final StringWriter getErr() {
    return err;
  }

  public final Executor execute() throws IOException, InterruptedException {
    var process = mProcessBuilder.start();
    // Thread for stdout
    var outThread =
        new Thread(
            () -> {
              var buff = new BufferedReader(new InputStreamReader(process.getInputStream()));
              buff.lines()
                  .forEach(
                      line -> {
                        out.append(line).append('\n');
                      });
            });
    // Thread for stderr
    var errThread =
        new Thread(
            () -> {
              var buff = new BufferedReader(new InputStreamReader(process.getErrorStream()));
              buff.lines()
                  .forEach(
                      line -> {
                        err.append(line).append('\n');
                      });
            });

    outThread.start();
    errThread.start();

    outThread.join();
    errThread.join();

    process.waitFor();
    return this;
  }
}
