package mo.kilate.editor.ac;

public class KilateAutoCompleteItem {
  private final String text;
  private final KilateAutoCompleteType type;

  public KilateAutoCompleteItem(final String text, final KilateAutoCompleteType type) {
    this.text = text;
    this.type = type;
  }

  public final String getText() {
    return text;
  }

  public final KilateAutoCompleteType getType() {
    return type;
  }

  @Override
  public String toString() {
    return text;
  }
}
