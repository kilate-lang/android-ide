package mo.kilate.editor.cs;

import android.content.Context;
import android.util.TypedValue;
import androidx.core.content.ContextCompat;
import mo.kilate.editor.R;

public class KilateMaterialColorScheme extends KilateDefaultColorScheme {

  private Context context;

  public KilateMaterialColorScheme(final Context context) {
    super();
    this.context = context;
    set(
        KilateColorKey.Background,
        getColorFromAttr(com.google.android.material.R.attr.colorSurface));
    set(
        KilateColorKey.Keyword,
        getColorFromAttr(com.google.android.material.R.attr.colorPrimary)
    );
    set(KilateColorKey.Type, getColorFromAttr(com.google.android.material.R.attr.colorSecondary));
    set(
        KilateColorKey.Function,
        getColorFromAttr(com.google.android.material.R.attr.colorOnSurfaceVariant));
    set(KilateColorKey.String, getColorFromAttr(com.google.android.material.R.attr.colorTertiary));
  }

  private int getColorFromAttr(final int resid) {
    final TypedValue tv = new TypedValue();
    context.getTheme().resolveAttribute(resid, tv, true);
    return ContextCompat.getColor(context, tv.resourceId);
  }
}
