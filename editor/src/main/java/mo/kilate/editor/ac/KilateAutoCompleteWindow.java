package mo.kilate.editor.ac;

import android.content.Context;
import android.text.Editable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import mo.kilate.editor.R;

public class KilateAutoCompleteWindow {

  private final PopupWindow popupWindow;
  private final ListView listView;
  private final EditText editText;
  private final List<KilateAutoCompleteItem> allSuggestions = new ArrayList<>();

  private final KilateAutoCompleteAdapter adapter;

  public KilateAutoCompleteWindow(Context context, EditText editText) {
    this.editText = editText;

    final LayoutInflater inflater = LayoutInflater.from(context);
    final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.editor_autocomplete_popup, null);
    listView = layout.findViewById(R.id.suggestion_list);

    adapter = new KilateAutoCompleteAdapter(context, new ArrayList<>());
    listView.setAdapter(adapter);

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
    listView.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(
              final AdapterView<?> parent, final View view, final int position, long id) {
            KilateAutoCompleteItem selected = (KilateAutoCompleteItem) adapter.getItem(position);
            if (selected != null) {
              replaceCurrentWord(selected.getText());
              popupWindow.dismiss();
            }
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

  private final int findWordStart(String text, int cursor) {
    int i = cursor - 1;
    while (i >= 0 && Character.isLetterOrDigit(text.charAt(i))) {
      i--;
    }
    return i + 1;
  }

  public static class KilateAutoCompleteAdapter extends BaseAdapter {

    private final Context context;
    private final List<KilateAutoCompleteItem> suggestions;

    public KilateAutoCompleteAdapter(Context context, List<KilateAutoCompleteItem> suggestions) {
      this.context = context;
      this.suggestions = suggestions;
    }

    @Override
    public int getCount() {
      return suggestions.size();
    }

    @Override
    public Object getItem(final int position) {
      return suggestions.get(position);
    }

    @Override
    public long getItemId(final int position) {
      return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
      ViewHolder holder;

      if (convertView == null) {
        convertView =
            LayoutInflater.from(context).inflate(R.layout.editor_autocomplete_item, parent, false);
        convertView.setLayoutParams(
            new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        holder = new ViewHolder();
        holder.text = convertView.findViewById(R.id.item_text);
        holder.type = convertView.findViewById(R.id.item_type);
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      final KilateAutoCompleteItem item = suggestions.get(position);
      holder.text.setText(item.getText());
      holder.type.setText(item.getType().toString());

      return convertView;
    }

    static class ViewHolder {
      TextView text;
      TextView type;
    }

    public void update(final List<KilateAutoCompleteItem> newList) {
      suggestions.clear();
      suggestions.addAll(newList);
      notifyDataSetChanged();
    }
  }
}
