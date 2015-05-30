package is.arontibo.library.VectorCompat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

public class ResourcesCompat {
    public static final boolean LOLLIPOP = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getDrawable(Context c, int resId) {
        Drawable d;
        try {
            if (LOLLIPOP) {
                d = c.getResources().getDrawable(resId, c.getTheme());
            } else {
                d = c.getResources().getDrawable(resId);
            }
        } catch (Resources.NotFoundException e) {

            try {
                d = VectorDrawable.getDrawable(c, resId);
            } catch (IllegalArgumentException e1) {

                //We're not a VectorDrawable, try AnimatedVectorDrawable
                try {
                    d = AnimatedVectorDrawable.getDrawable(c, resId);
                } catch (IllegalArgumentException e2) {
                    //Throw NotFoundException
                    throw e;
                }
            }
        }
        return d;
    }
}
