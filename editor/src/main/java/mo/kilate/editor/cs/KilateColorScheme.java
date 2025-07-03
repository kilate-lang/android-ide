package mo.kilate.editor.cs;

import java.util.HashMap;

public class KilateColorScheme {

  private HashMap<KilateColorKey, Integer> colors;

  public KilateColorScheme() {
    colors = new HashMap<>();
  }

  public void set(final KilateColorKey type, final int value) {
    colors.put(type, value);
  }

  public int get(final KilateColorKey type) {
    boolean foundInEnum = false;
    for (KilateColorKey key : KilateColorKey.values()) {
      if (key == type) {
        foundInEnum = true;
        break;
      }
    }
    if (!foundInEnum)
      throw new RuntimeException(
          "Invalid color. Valid colors: " + KilateColorKey.values().toString());
    if (!colors.containsKey(type))
      throw new RuntimeException(
          "Invalid color. Valid colors: " + KilateColorKey.values().toString());
    return colors.get(type);
  }
}
