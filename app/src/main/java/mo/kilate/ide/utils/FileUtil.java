package mo.kilate.ide.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import mo.kilate.ide.io.File;

public class FileUtil {

  FileUtil() {}

  public static boolean isExistFile(final String path) {
    return isExistFile(new File(path));
  }

  public static boolean isExistFile(final File file) {
    return file.exists();
  }

  public static void makeDir(final String path) {
    makeDir(new File(path));
  }

  public static void makeDir(final File file) {
    if (!isExistFile(file)) {
      file.mkdirs();
    }
  }

  public static void createNewFileIfNotPresent(final String path) {
    createNewFileIfNotPresent(new File(path));
  }

  public static void createNewFileIfNotPresent(final File file) {
    int lastSep = file.getAbsolutePath().lastIndexOf(File.separator);
    if (lastSep > 0) {
      String dirPath = file.getAbsolutePath().substring(0, lastSep);
      makeDir(dirPath);
    }

    try {
      if (!file.exists()) file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String readFile(final String path, final boolean createIfNotExists) {
    return readFile(new File(path), createIfNotExists);
  }

  public static String readFile(final File file, final boolean createIfNotExists) {
    if (createIfNotExists) createNewFileIfNotPresent(file);
    StringBuilder sb = new StringBuilder();
    try (FileReader fr = new FileReader(file)) {
      char[] buff = new char[1024];
      int length;

      while ((length = fr.read(buff)) > 0) {
        sb.append(new String(buff, 0, length));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return sb.toString();
  }

  public static String readFileIfExist(final String path) {
    return readFileIfExist(new File(path));
  }

  public static String readFileIfExist(final File file) {
    StringBuilder sb = new StringBuilder();
    try (FileReader fr = new FileReader(file)) {
      char[] buff = new char[1024];
      int length;

      while ((length = fr.read(buff)) > 0) {
        sb.append(new String(buff, 0, length));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return sb.toString();
  }

  public static void writeText(final String path, final String text) {
    writeText(new File(path), text);
  }

  public static void writeText(final File file, final String text) {
    createNewFileIfNotPresent(file);

    try (FileWriter fileWriter = new FileWriter(file, false)) {
      fileWriter.write(text);
      fileWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static boolean deleteRecursive(final File fileOrDir) {
    if (fileOrDir.isDirectory()) {
      for (java.io.File child : fileOrDir.listFiles()) {
        deleteRecursive(new File(child.getAbsolutePath()));
      }
    }
    return fileOrDir.delete();
  }
}
