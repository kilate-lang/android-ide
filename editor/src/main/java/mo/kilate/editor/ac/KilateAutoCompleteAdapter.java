package mo.kilate.editor.ac;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mo.kilate.editor.R;

public class KilateAutoCompleteAdapter extends RecyclerView.Adapter<KilateAutoCompleteAdapter.ViewHolder> {

    private final Context context;
    private final List<KilateAutoCompleteItem> suggestions;

    public interface OnItemClickListener {
        void onItemClick(KilateAutoCompleteItem item);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public KilateAutoCompleteAdapter(Context context, List<KilateAutoCompleteItem> suggestions) {
        this.context = context;
        this.suggestions = suggestions;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        TextView type;
        TextView icon;
        
        public ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.item_text);
            type = itemView.findViewById(R.id.item_type);
            icon = itemView.findViewById(R.id.item_icon);
        }
    }

    @Override
    public KilateAutoCompleteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.editor_autocomplete_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        KilateAutoCompleteItem item = suggestions.get(position);
        holder.text.setText(item.getText());
        holder.type.setText(item.getType().toString());
        
        applyIcon(holder.icon, item);
        
        holder.itemView.setOnClickListener(v -> {
        if (listener != null) {
            listener.onItemClick(item);
        }
    });
    
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }
    
    public KilateAutoCompleteItem getItem(int position) {
        return suggestions.get(position);
    }

    public void update(List<KilateAutoCompleteItem> newList) {
        suggestions.clear();
        suggestions.addAll(newList);
        notifyDataSetChanged();
    }

    public void applyIcon(TextView icon, KilateAutoCompleteItem item){
        char prefix = item.getType().toString().toUpperCase().toCharArray()[0];
        icon.setText(String.valueOf(prefix));
        
        GradientDrawable oval = new GradientDrawable();
        oval.setShape(GradientDrawable.OVAL);
        
        //Customize the colors as you wish, according to your language and IDE
        int color = Color.MAGENTA;
        switch(item.getType()){
            case Keyword:
                color = Color.parseColor("#FF8AB0E2");
            break;      
            case Function:
                color = Color.GRAY;
            break;    
            case Type:
                color = Color.parseColor("#FF6AB0E2");
            break;      
            case Variable:
                color = Color.parseColor("#FFA500");
            break;    
        }
        oval.setColor(color);
        icon.setBackgroundDrawable(oval);
        
    }
}