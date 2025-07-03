package mo.kilate.ide.utils;

import androidx.annotation.DrawableRes;
import java.util.List;
import mo.kilate.ide.R;

public class DesignUtil {
  DesignUtil() {}

  @DrawableRes
  public static final <T> int getShapedBackgroundForList(final List<T> list, final int position) {
    if (list.size() == 1) {
      return R.drawable.shape_alone;
    } else if (position == 0) {
      return R.drawable.shape_top;
    } else if (position == list.size() - 1) {
      return R.drawable.shape_bottom;
    } else {
      return R.drawable.shape_middle;
    }
  }
}
