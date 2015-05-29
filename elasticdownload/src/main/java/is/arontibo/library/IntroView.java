package is.arontibo.library;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.wnafee.vector.compat.AnimatedVectorDrawable;
import com.wnafee.vector.compat.DrawableCompat;

/**
 * Created by thibaultguegan on 15/03/15.
 */
public class IntroView extends ImageView {

    private static final String LOG_TAG = IntroView.class.getSimpleName();

    public interface EnterAnimationListener {
        public void onEnterAnimationFinished();
    }

    private EnterAnimationListener mListener;

    /**
     * MARK: Constructor
     */

    public IntroView(Context context, AttributeSet attrs) {
        super(context, attrs);
        AnimatedVectorDrawable drawable = AnimatedVectorDrawable.getDrawable(context, R.drawable.avd_start);
        setImageDrawable(drawable);
    }

    /**
     * MARK: Getters/setters
     */

    public void setListener(EnterAnimationListener listener) {
        mListener = listener;
    }

    /**
     * MARK: Public functions
     */

    public void startAnimation() {
        DrawableCompat drawable = (DrawableCompat) getDrawable();
        Animatable animatable = (Animatable) drawable;

        AVDWrapper.Callback callback = new AVDWrapper.Callback() {
            @Override
            public void onAnimationDone() {
                Log.d(LOG_TAG, "Enter animation finished");
                mListener.onEnterAnimationFinished();
            }

            @Override
            public void onAnimationStopped() {

            }
        };

        AVDWrapper wrapper = new AVDWrapper(animatable, new Handler(), callback);
        wrapper.start(getContext().getResources().getInteger(R.integer.enter_animation_duration));
    }
}
