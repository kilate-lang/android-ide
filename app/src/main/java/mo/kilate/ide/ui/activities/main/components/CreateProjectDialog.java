package mo.kilate.ide.ui.activities.main.components;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import mo.kilate.ide.R;
import mo.kilate.ide.beans.ProjectBean;
import mo.kilate.ide.databinding.DialogCreateProjectBinding;
import mo.kilate.ide.project.manage.ProjectManager;

import java.util.Objects;

public class CreateProjectDialog extends BottomSheetDialog {
  private final DialogCreateProjectBinding binding;

  public CreateProjectDialog(@NonNull Context context) {
    super(context);
    binding = DialogCreateProjectBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    binding.next.setOnClickListener(v -> onNext());
    binding.cancel.setOnClickListener(v -> dismiss());
  }

  private void onNext() {
    var project = new ProjectBean();
    project.name = Objects.requireNonNull(binding.projectName.getText()).toString();

    if (!(binding.projectName.getText().toString().isEmpty())) {
      ProjectManager.createProjectByBean(project);
      dismiss();
    } else {
      Toast.makeText(getContext(), R.string.w_fill_all_fields, Toast.LENGTH_SHORT).show();
    }
  }
}
