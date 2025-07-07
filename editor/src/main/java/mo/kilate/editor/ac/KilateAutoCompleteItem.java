package mo.kilate.editor.ac;

public class KilateAutoCompleteItem {
  private final String text;
  private final String code;
  private final KilateAutoCompleteType type;

  public KilateAutoCompleteItem(final String text, final KilateAutoCompleteType type) {
    this.text = text;
    this.code = text;
    this.type = type;
  }

  public KilateAutoCompleteItem(
      final String name, final String code, final KilateAutoCompleteType type) {
    this.text = name;
    this.code = code;
    this.type = type;
  }

  public final String getText() {
    return text;
  }

  public final String getCode() {
    return code;
  }

  public final KilateAutoCompleteType getType() {
    return type;
  }

  @Override
  public String toString() {
    return text;
  }
}
