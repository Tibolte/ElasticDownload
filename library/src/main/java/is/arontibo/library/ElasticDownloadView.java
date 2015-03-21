package is.arontibo.library;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Gallery;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by thibaultguegan on 15/03/15.
 */
public class ElasticDownloadView extends FrameLayout implements IntroView.EnterAnimationListener{

    private static final String LOG_TAG = ElasticDownloadView.class.getSimpleName();

    private IntroView mIntroView;
    private ProgressDownloadView mProgressDownloadView;

    /**
     * MARK: Constructor
     */

    public ElasticDownloadView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_elasticdownload, this, true);
    }

    /**
     * MARK: Overrides
     */

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mIntroView = (IntroView) findViewById(R.id.intro_view);
        mIntroView.setListener(this);
        mProgressDownloadView = (ProgressDownloadView) findViewById(R.id.progress_download_view);

        ViewTreeObserver vto = mProgressDownloadView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mProgressDownloadView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mIntroView.getLayoutParams().width = mProgressDownloadView.getWidth();
                mIntroView.getLayoutParams().height = mProgressDownloadView.getHeight();
            }
        });
    }

    /**
     * MARK: Public methods
     */

    public void startIntro() {
        mIntroView.startAnimation();
    }


    /**
     * MARK: Enter animation overrides
     */

    @Override
    public void onEnterAnimationFinished() {
        mIntroView.setVisibility(INVISIBLE);
        mProgressDownloadView.setVisibility(VISIBLE);
        mProgressDownloadView.setProgress(mProgressDownloadView.getProgress());

        //temporary example
        Timer timer = new Timer();
        ProgressTask task= new ProgressTask();
        timer.schedule(task, 0, ProgressDownloadView.ANIMATION_DURATION_BASE);
    }

    class ProgressTask extends TimerTask {

        @Override
        public void run() {

            ((Activity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int percentage = (int) Math.floor(Math.random() * 100);
                    mProgressDownloadView.setPercentage(percentage);
                }
            });

        }

    }
}
