package is.arontibo.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

/**
 * Created by thibaultguegan on 15/03/15.
 */
public class ElasticDownloadView extends FrameLayout {

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
        mProgressDownloadView = (ProgressDownloadView) findViewById(R.id.progress_download_view);
    }

    /**
     * MARK: Public methods
     */

    public void startIntro() {
        mIntroView.startAnimation();
    }
}
