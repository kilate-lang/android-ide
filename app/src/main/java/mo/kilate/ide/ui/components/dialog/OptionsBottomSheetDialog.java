package mo.kilate.ide.ui.components.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.List;
import mo.kilate.ide.databinding.DialogOptionBinding;
import mo.kilate.ide.databinding.DialogOptionsBottomSheetBinding;

public class OptionsBottomSheetDialog extends BottomSheetDialog {
  private final DialogOptionsBottomSheetBinding binding;
  private final List<Option> options = new ArrayList<>();
  private final OptionsAdapter adapter;

  public OptionsBottomSheetDialog(final Context context) {
    super(context);
    binding = DialogOptionsBottomSheetBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    adapter = new OptionsAdapter(this);
    binding.list.setAdapter(adapter);
    binding.list.setLayoutManager(new LinearLayoutManager(context));
  }

  public final OptionsBottomSheetDialog add(final Option op) {
    options.add(op);
    return this;
  }

  public final OptionsBottomSheetDialog setTitle(final String title) {
    binding.title.setText(title);
    return this;
  }

  @Override
  public void show() {
    adapter.submitList(new ArrayList<>(options));
    super.show();
  }

  public record Option(String text, int icon, OptionClickListener onClickListener) {}

  public interface OptionClickListener {
    void onClick(final OptionsBottomSheetDialog it, final Option op);
  }

  public static class OptionsAdapter extends ListAdapter<Option, OptionsAdapter.ViewHolder> {
    private final OptionsBottomSheetDialog obsd;

    public OptionsAdapter(final OptionsBottomSheetDialog obsd) {
      super(new OptionsAdapterDiffUtil());
      this.obsd = obsd;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int parentType) {
      final DialogOptionBinding binding =
          DialogOptionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
      return new ViewHolder(binding);
    }

    @Override
    @NonNull
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      final Option op = getItem(position);
      holder.binding.text.setText(op.text);
      holder.binding.icon.setImageResource(op.icon);
      holder
          .binding
          .getRoot()
          .setOnClickListener(
              v -> {
                op.onClickListener.onClick(obsd, op);
              });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
      public final DialogOptionBinding binding;

      public ViewHolder(final DialogOptionBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
      }
    }

    public static class OptionsAdapterDiffUtil extends DiffUtil.ItemCallback<Option> {
      @Override
      public boolean areItemsTheSame(@NonNull Option oldItem, @NonNull Option newItem) {
        return oldItem == newItem;
      }

      @Override
      public boolean areContentsTheSame(@NonNull Option oldItem, @NonNull Option newItem) {
        return oldItem.text.equals(newItem.text);
      }
    }
  }
}
