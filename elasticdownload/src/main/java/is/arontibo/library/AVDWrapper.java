package is.arontibo.library;

import android.graphics.drawable.Animatable;
import android.os.Handler;

/**
 * Created by thibaultguegan on 21/03/15.
 */
public class AVDWrapper {

    private Handler mHandler;
    private Animatable mDrawable;
    private Callback mCallback;
    private Runnable mAnimationDoneRunnable = new Runnable() {

        @Override
        public void run() {
            if (mCallback != null) {
                mCallback.onAnimationDone();
            }
        }
    };

    public interface Callback {
        void onAnimationDone();

        void onAnimationStopped();
    }

    public AVDWrapper(Animatable drawable,
                      Handler handler, Callback callback) {
        mDrawable = drawable;
        mHandler = handler;
        mCallback = callback;
    }

    // Duration of the animation
    public void start(long duration) {
        mDrawable.start();
        mHandler.postDelayed(mAnimationDoneRunnable, duration);
    }

    public void stop() {
        mDrawable.stop();
        mHandler.removeCallbacks(mAnimationDoneRunnable);

        if (mCallback != null) {
            mCallback.onAnimationStopped();
        }
    }

}
