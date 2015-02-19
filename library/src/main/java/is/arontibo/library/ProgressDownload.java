package is.arontibo.library;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by thibaultguegan on 15/02/15.
 */
public class ProgressDownload extends View {

    private static final String LOG_TAG = ProgressDownload.class.getSimpleName();

    private static final int STROKE_WIDTH = 10;
    private static final int PADDING = 50;
    private static final long ANIMATION_DURATION_BASE = 1150;
    private static final String BACKGROUND_COLOR = "#EC5745";

    private int mWidth, mHeight;
    private int mProgress = 0;
    private Path mPathBlack, mPathWhite;
    private Paint mPaintBlack, mPaintWhite;
    private PathEffect mPathBlackEffect, mPathWhiteEffect;

    public ProgressDownload(Context context, AttributeSet attrs) {
        super(context, attrs);

        setBackgroundColor(Color.parseColor(BACKGROUND_COLOR));
        setPadding(PADDING, 0, 50, PADDING);

        mPaintBlack = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBlack.setStyle(Paint.Style.STROKE);
        mPaintBlack.setStrokeWidth(STROKE_WIDTH);
        mPaintBlack.setColor(Color.BLACK);

        mPaintWhite = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintWhite.setStyle(Paint.Style.STROKE);
        mPaintWhite.setStrokeWidth(STROKE_WIDTH);
        mPaintWhite.setColor(Color.WHITE);

        mPathBlackEffect = new CornerPathEffect(10);
        mPathWhiteEffect = new CornerPathEffect(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mPathWhite != null && mPathBlack != null) {
            mPaintBlack.setPathEffect(mPathBlackEffect);
            mPaintWhite.setPathEffect(mPathWhiteEffect);

            canvas.drawPath(mPathBlack, mPaintBlack);
            canvas.drawPath(mPathWhite, mPaintWhite);
        }
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        mWidth = xNew - getPaddingRight();
        mHeight = yNew;
        Log.d(LOG_TAG, String.format("width and height measured are %d and %d", mWidth, mHeight));

        setPercentage(mProgress);
    }

    private Path makePathBlack() {

        if(mPathBlack ==null) {
            mPathBlack = new Path();
        }

        Path p =  new Path();
        p.moveTo(Math.max(getPaddingLeft(), mProgress*mWidth/100), mHeight/2 + calculatedeltaY());
        p.lineTo(mWidth, mHeight/2);

        mPathBlack.set(p);

        return p;
    }

    private void makePathWhite() {

        if(mPathWhite ==null) {
            mPathWhite = new Path();
        }

        Path p = new Path();
        p.moveTo(getPaddingLeft(), mHeight / 2);
        p.lineTo(Math.max(getPaddingLeft(), mProgress * mWidth / 100), mHeight / 2 + calculatedeltaY());

        mPathWhite.set(p);
    }

    private int calculatedeltaY() {
        if(mProgress <= 50) {
            return  (mProgress * mWidth/6)/50;
        } else {
            return  ((100-mProgress) * mWidth/6)/50;
        }
    }

    public void setPercentage(int newProgress) {
        if(newProgress < 0 || newProgress > 100)
            throw new IllegalArgumentException("setPercentage not between 0 and 100");

        ObjectAnimator anim = ObjectAnimator.ofInt(this, "progress", getProgress(), newProgress);
        anim.setDuration(ANIMATION_DURATION_BASE-Math.abs(newProgress*10 - getProgress()*10));
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();
    }

    public void setProgress(int progress) {
        mProgress = progress;
        makePathBlack();
        makePathWhite();
        invalidate();
    }

    public int getProgress() {
        return mProgress;
    }
}
