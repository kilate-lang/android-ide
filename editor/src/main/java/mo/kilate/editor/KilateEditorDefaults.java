package mo.kilate.editor;

import java.util.Arrays;
import java.util.List;
import mo.kilate.editor.ac.KilateAutoCompleteItem;
import mo.kilate.editor.ac.KilateAutoCompleteType;

public class KilateEditorDefaults {
  public static final List<KilateAutoCompleteItem> Suggestions =
      Arrays.asList(
          new KilateAutoCompleteItem("work", KilateAutoCompleteType.Keyword),
          new KilateAutoCompleteItem("import", KilateAutoCompleteType.Keyword),
          new KilateAutoCompleteItem("var", KilateAutoCompleteType.Keyword),
          new KilateAutoCompleteItem("let", KilateAutoCompleteType.Keyword),
          new KilateAutoCompleteItem("return", KilateAutoCompleteType.Keyword),
          new KilateAutoCompleteItem("false", KilateAutoCompleteType.Keyword),
          new KilateAutoCompleteItem("true", KilateAutoCompleteType.Keyword),
          new KilateAutoCompleteItem("string", KilateAutoCompleteType.Type),
          new KilateAutoCompleteItem("int", KilateAutoCompleteType.Type),
          new KilateAutoCompleteItem("float", KilateAutoCompleteType.Type),
          new KilateAutoCompleteItem("long", KilateAutoCompleteType.Type),
          new KilateAutoCompleteItem("bool", KilateAutoCompleteType.Type),
          new KilateAutoCompleteItem("any", KilateAutoCompleteType.Type),
          new KilateAutoCompleteItem("print", KilateAutoCompleteType.Function),
          new KilateAutoCompleteItem("for", "for(var i = 0 i < count i++){\n\t\n}",KilateAutoCompleteType.Snippet),
          new KilateAutoCompleteItem("while", "while(condition){\n\t\n}",KilateAutoCompleteType.Snippet),
          new KilateAutoCompleteItem("method", "work method(){\n\t\n}",KilateAutoCompleteType.Snippet));
          
}
