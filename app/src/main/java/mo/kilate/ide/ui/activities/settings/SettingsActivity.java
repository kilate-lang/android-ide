package mo.kilate.ide.ui.activities.settings;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import mo.kilate.ide.databinding.ActivitySettingsBinding;
import mo.kilate.ide.ui.base.BaseAppCompatActivity;

public class SettingsActivity extends BaseAppCompatActivity {

  @NonNull private ActivitySettingsBinding binding;

  @Override
  @NonNull
  protected View bindLayout() {
    binding = ActivitySettingsBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  protected void onBindLayout(@Nullable final Bundle savedInstanceState) {
    toast("No implemented yet Nigeria bro");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    this.binding = null;
  }
}
