package mo.kilate.editor;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mo.kilate.editor.R;
import mo.kilate.editor.ac.KilateAutoCompleteItem;
import mo.kilate.editor.ac.KilateAutoCompleteWindow;
import mo.kilate.editor.cs.KilateColorKey;
import mo.kilate.editor.cs.KilateColorScheme;

public class KilateEditor extends LinearLayout {

  private EditText editText;
  private TextView lineNumbers;
  private ScrollView lineScroll;
  private ScrollView editScroll;

  private KilateAutoCompleteWindow autoCompleteWindow;
  private KilateColorScheme colorScheme;

  public KilateEditor(final Context context) {
    super(context);
    init(context);
  }

  public KilateEditor(final Context context, final AttributeSet attrSet) {
    super(context, attrSet);
    init(context);
  }

  public KilateEditor(final Context context, final AttributeSet attrSet, final int styleResId) {
    super(context, attrSet, styleResId);
    init(context);
  }

  public KilateEditor(
      final Context context,
      final AttributeSet attrSet,
      final int styleResId,
      final int attrResId) {
    super(context, attrSet, styleResId, attrResId);
    init(context);
  }

  private final void init(final Context context) {
    inflate(context, R.layout.editor, this);
    editText = findViewById(R.id.edit_text);
    lineNumbers = findViewById(R.id.line_numbers);
    lineScroll = findViewById(R.id.line_scroll);
    editScroll = findViewById(R.id.edit_scroll);
    autoCompleteWindow = new KilateAutoCompleteWindow(context, editText);
    editText
        .getViewTreeObserver()
        .addOnScrollChangedListener(
            new ViewTreeObserver.OnScrollChangedListener() {
              @Override
              public void onScrollChanged() {
                lineScroll.scrollTo(0, editScroll.getScrollY());
              }
            });
    editText.addTextChangedListener(
        new TextWatcher() {
          private boolean isHandlingIndent = false;
          private int lastTextLength = 0;

          @Override
          public void beforeTextChanged(
              final CharSequence s, final int start, final int count, final int after) {
            lastTextLength = s.length();
          }

          @Override
          public void onTextChanged(
              final CharSequence s, final int start, final int before, final int count) {}

          @Override
          public void afterTextChanged(final Editable s) {
            if (isHandlingIndent) return;
            final int cursor = editText.getSelectionStart();
            if (cursor == 0 || cursor > s.length()) return;
            if (s.length() > lastTextLength && cursor > 1 && s.charAt(cursor - 1) == '\n') {
              isHandlingIndent = true;
              insertAutoIndent(s, cursor);
              isHandlingIndent = false;
            }
            final String currentWord = getCurrentWord();
            autoCompleteWindow.showIfMatches(currentWord);
            update(s);
          }
        });
  }

  public void setText(final String text) {
    editText.setText(text);
    new Handler()
        .postDelayed(
            new Runnable() {
              @Override
              public void run() {
                update(getText());
              }
            },
            100);
  }

  public void setColorScheme(final KilateColorScheme colorScheme) {
    this.colorScheme = colorScheme;
  }

  public void setTypeface(final Typeface typeface) {
    editText.setTypeface(typeface);
  }

  public void addSuggestion(final KilateAutoCompleteItem suggestion) {
    autoCompleteWindow.addSuggestion(suggestion);
  }

  public void addSuggestions(final List<KilateAutoCompleteItem> suggestions) {
    autoCompleteWindow.addSuggestions(suggestions);
  }

  public Editable getText() {
    return editText.getText();
  }

  private final void update(final Editable s) {
    updateLineNumbers();
    highlightSyntax(s);
    setBackgroundColor(colorScheme.get(KilateColorKey.Background));
  }

  private final void updateLineNumbers() {
    final int lineCount = editText.getLineCount();
    final StringBuilder builder = new StringBuilder();
    for (int i = 1; i <= lineCount; i++) {
      builder.append(i).append("\n");
    }
    lineNumbers.setText(builder.toString());
  }

  private final void highlightSyntax(final Editable editable) {
    final ForegroundColorSpan[] spans =
        editable.getSpans(0, editable.length(), ForegroundColorSpan.class);
    for (final ForegroundColorSpan span : spans) {
      editable.removeSpan(span);
    }
    // Keywords
    for (final KilateAutoCompleteItem item : autoCompleteWindow.getSuggestions()) {
      final String keyword = item.getText();
      final Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b");
      final Matcher matcher = pattern.matcher(editable);
      KilateColorKey color;
      switch (item.getType()) {
        case Keyword:
          color = KilateColorKey.Keyword;
          break;
        case Type:
          color = KilateColorKey.Type;
          break;
        case Function:
          color = KilateColorKey.Function;
          break;
        default:
          color = KilateColorKey.Keyword;
          break;
      }

      while (matcher.find()) {
        editable.setSpan(
            new ForegroundColorSpan(colorScheme.get(color)),
            matcher.start(),
            matcher.end(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      }
    }
    // string, text beetween ""
    final Pattern stringPattern = Pattern.compile("\"(\\\\.|[^\"])*\"");
    final Matcher stringMatcher = stringPattern.matcher(editable);
    while (stringMatcher.find()) {
      editable.setSpan(
          new ForegroundColorSpan(colorScheme.get(KilateColorKey.String)),
          stringMatcher.start(),
          stringMatcher.end(),
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
  }

  private final String getCurrentWord() {
    final int cursorPos = editText.getSelectionStart();
    final String text = getText().toString();
    final int start = Math.max(findWordStart(text, cursorPos), 0);
    return text.substring(start, cursorPos);
  }

  private final int findWordStart(String text, int cursor) {
    int i = cursor - 1;
    while (i >= 0 && Character.isLetterOrDigit(text.charAt(i))) {
      i--;
    }
    return i + 1;
  }

  private final void insertAutoIndent(final Editable editable, final int cursor) {
    int lineStart = cursor - 2;
    while (lineStart >= 0 && editable.charAt(lineStart) != '\n') {
      lineStart--;
    }
    lineStart++;

    final StringBuilder currentLine = new StringBuilder();
    for (int i = lineStart; i < cursor - 1; i++) {
      currentLine.append(editable.charAt(i));
    }

    final StringBuilder indent = new StringBuilder();
    for (int i = 0; i < currentLine.length(); i++) {
      char c = currentLine.charAt(i);
      if (c == ' ' || c == '\t') {
        indent.append(c);
      } else {
        break;
      }
    }

    if (currentLine.toString().trim().endsWith("{")) {
      indent.append("  ");
    }

    editable.insert(cursor, indent.toString());
  }
}
