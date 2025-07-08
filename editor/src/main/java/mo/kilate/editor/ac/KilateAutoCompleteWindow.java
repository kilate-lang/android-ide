package mo.kilate.editor.ac;

import android.content.Context;
import android.text.Editable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import mo.kilate.editor.R;

public class KilateAutoCompleteWindow {

  private final PopupWindow popupWindow;
  private final RecyclerView recyclerView;
  private final EditText editText;
  private final List<KilateAutoCompleteItem> allSuggestions = new ArrayList<>();

  private final KilateAutoCompleteAdapter adapter;

  public KilateAutoCompleteWindow(Context context, EditText editText) {
    this.editText = editText;

    final LayoutInflater inflater = LayoutInflater.from(context);
    final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.editor_autocomplete_popup, null);

    recyclerView = layout.findViewById(R.id.suggestion_list);

    adapter = new KilateAutoCompleteAdapter(context, new ArrayList<>());
    recyclerView.setAdapter(adapter);

    recyclerView.setLayoutManager(new LinearLayoutManager(context));

    popupWindow =
        new PopupWindow(
            layout,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            false);

    popupWindow.setOutsideTouchable(true);
    popupWindow.setFocusable(false);
    popupWindow.setTouchable(true);
    popupWindow.setBackgroundDrawable(null);
    editText.post(
        new Runnable() {
          @Override
          public void run() {
            popupWindow.setWidth(editText.getWidth());
          }
        });
    adapter.setOnItemClickListener(
        item -> {
          if (item != null) {
            switch (item.getType()) {
              case Snippet:
                replaceWordWithSnippet(item.getCode());
                break;
              default:
                replaceCurrentWord(item.getText());
                break;
            }
            popupWindow.dismiss();
          }
        });
  }

  public void addSuggestion(KilateAutoCompleteItem item) {
    allSuggestions.add(item);
  }

  public void addSuggestions(List<KilateAutoCompleteItem> items) {
    allSuggestions.addAll(items);
  }

  public List<KilateAutoCompleteItem> getSuggestions() {
    return allSuggestions;
  }

  public void showIfMatches(final String currentWord) {
    final List<KilateAutoCompleteItem> matches = new ArrayList<>();
    for (final KilateAutoCompleteItem item : allSuggestions) {
      if (item.getText().startsWith(currentWord) && !currentWord.isEmpty()) {
        matches.add(item);
      }
    }

    if (!matches.isEmpty()) {
      adapter.update(matches);
      showPopup();
    } else {
      popupWindow.dismiss();
    }
  }

  public void dismiss() {
    popupWindow.dismiss();
  }

  private final void showPopup() {
    final int offset = editText.getSelectionStart();
    final Layout layout = editText.getLayout();
    if (layout == null) return;

    final int line = layout.getLineForOffset(offset);
    final float x = layout.getPrimaryHorizontal(offset);
    final int y = layout.getLineBottom(line);

    final int[] location = new int[2];
    editText.getLocationOnScreen(location);

    final float popupX = location[0] + x;
    final float popupY = location[1] + y;

    if (!popupWindow.isShowing()) {
      popupWindow.showAsDropDown(editText, (int) x, (int) y - editText.getHeight());
    } else {
      popupWindow.update((int) popupX, (int) popupY, -1, -1);
    }
  }

  private final void replaceCurrentWord(String suggestion) {
    final int cursorPos = editText.getSelectionStart();
    final Editable text = editText.getText();
    final int start = findWordStart(text.toString(), cursorPos);
    text.replace(start, cursorPos, suggestion);
    editText.setSelection(start + suggestion.length());
  }
    
    private void replaceWordWithSnippet(String snippet) {
    final int cursorPos = editText.getSelectionStart();
    final Editable text = editText.getText();
    final int wordStart = findWordStart(text.toString(), cursorPos);

    // Localiza a posição do marcador {$C} dentro do snippet
    final int cursorInSnippet = snippet.indexOf("{$C}");
    final String cleanSnippet = snippet.replace("{$C}", "");

    // Substitui a palavra atual pelo snippet limpo
    text.replace(wordStart, cursorPos, cleanSnippet);

    // Calcula a nova posição do cursor após a substituição
    int newCursorPos = wordStart + (cursorInSnippet != -1 ? cursorInSnippet : cleanSnippet.length());
    editText.setSelection(newCursorPos);
}

  private final int findWordStart(String text, int cursor) {
    int i = cursor - 1;
    while (i >= 0 && Character.isLetterOrDigit(text.charAt(i))) {
      i--;
    }
    return i + 1;
  }
}
