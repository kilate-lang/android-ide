package mo.kilate.ide.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

  public static final Drawable createDrawableFromStr(
      final Context context, String str, int sizePx, int color, Typeface typeFace) {
    final Bitmap bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888);
    final Canvas canvas = new Canvas(bitmap);

    final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setColor(color);
    paint.setTextSize(sizePx * 0.5f);
    paint.setTypeface(typeFace);
    paint.setTextAlign(Paint.Align.CENTER);

    final Paint.FontMetrics fontMetrics = paint.getFontMetrics();
    float x = sizePx / 2f;
    float y = sizePx / 2f - (fontMetrics.ascent + fontMetrics.descent) / 2f;

    canvas.drawColor(Color.TRANSPARENT);
    canvas.drawText(str, x, y, paint);

    return new BitmapDrawable(context.getResources(), bitmap);
  }
}
