package mo.kilate.ide.ui.components.dialog;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import mo.kilate.ide.databinding.DialogInputBinding;
import mo.kilate.ide.utils.LayoutUtil;

public class InputBottomSheet extends BottomSheetDialog {

  protected final DialogInputBinding binding;
  protected Listener onSaveListener;
  protected Listener onCancelListener;

  public InputBottomSheet(@NonNull Context context) {
    super(context);
    this.binding = DialogInputBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    binding.save.setOnClickListener(
        v -> {
          String value = binding.tie.getText().toString();
          onSaveListener.call(this, value);
        });

    binding.cancel.setOnClickListener(
        v -> {
          onCancelListener.call(this, null);
        });
  }

  public final InputBottomSheet setView(
      @NonNull View view, int left, int top, int right, int bottom) {
    binding.content.removeAllViews();
    int l = (int) LayoutUtil.getDip(getContext(), left);
    int t = (int) LayoutUtil.getDip(getContext(), top);
    int r = (int) LayoutUtil.getDip(getContext(), right);
    int b = (int) LayoutUtil.getDip(getContext(), bottom);
    view.setPadding(l, t, r, b);
    binding.content.addView(view);
    return this;
  }

  public final InputBottomSheet setTitle(@NonNull String title) {
    binding.title.setText(title);
    return this;
  }

  public final InputBottomSheet setText(@NonNull String value) {
    binding.tie.setText(value);
    return this;
  }

  public final InputBottomSheet setSaveListener(final Listener onSaveListener) {
    this.onSaveListener = onSaveListener;
    return this;
  }

  public final InputBottomSheet setCancelListener(final Listener onCancelListener) {
    this.onCancelListener = onCancelListener;
    return this;
  }

  public interface Listener {
    void call(final InputBottomSheet it, final String value);
  }
}
