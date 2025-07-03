package mo.kilate.ide;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import mo.kilate.ide.io.File;

public final class KilateIDE extends Application {

  private static Context appContext;

  @Override
  public void onCreate() {
    super.onCreate();
    appContext = this;
  }

  public static final String getPublicFolderPath() {
    return getPublicFolderFile().getAbsolutePath();
  }

  public static final File getPublicFolderFile() {
    return new File(Environment.getExternalStorageDirectory(), ".kilateide");
  }

  public static final Context getAppContext() {
    return appContext;
  }
}
