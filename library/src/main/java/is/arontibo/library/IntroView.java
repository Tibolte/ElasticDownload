package is.arontibo.library;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by thibaultguegan on 15/03/15.
 */
public class IntroView extends ImageView {

    /**
     * MARK: Constructor
     */

    public IntroView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setImageResource(R.drawable.avd_start);
    }

    /**
     * MARK: Public functions
     */

    public void startAnimation() {
        Drawable drawable = getDrawable();

        if (drawable instanceof Animatable)
            ((Animatable) drawable).start();
    }
}
