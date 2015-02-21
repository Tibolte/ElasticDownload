package is.arontibo.library;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by thibaultguegan on 15/02/15.
 */
public class ProgressDownload extends View {

    private static final String LOG_TAG = ProgressDownload.class.getSimpleName();

    //TODO: adapt parameters depending on resolution
    private static final int STROKE_WIDTH = 10;
    private static final int PADDING = 60;
    private static final int BUBBLE_WIDTH = 90;
    private static final int BUBBLE_HEIGHT = 70;
    private static final long ANIMATION_DURATION_BASE = 1150;
    private static final String BACKGROUND_COLOR = "#EC5745";

    private int mWidth, mHeight;
    private int mProgress = 0;
    private Path mPathBlack, mPathWhite, mPathBubble;
    private Paint mPaintBlack, mPaintWhite, mPaintBubble, mPaintText;

    public ProgressDownload(Context context, AttributeSet attrs) {
        super(context, attrs);

        setBackgroundColor(Color.parseColor(BACKGROUND_COLOR));
        setPadding(PADDING, 0, PADDING, 0);

        mPaintBlack = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBlack.setStyle(Paint.Style.STROKE);
        mPaintBlack.setStrokeWidth(STROKE_WIDTH);
        mPaintBlack.setColor(Color.BLACK);
        mPaintBlack.setStrokeCap(Paint.Cap.ROUND);
        mPaintBlack.setPathEffect(new CornerPathEffect(10));

        mPaintWhite = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintWhite.setStyle(Paint.Style.STROKE);
        mPaintWhite.setStrokeWidth(STROKE_WIDTH);
        mPaintWhite.setColor(Color.WHITE);
        mPaintWhite.setStrokeCap(Paint.Cap.ROUND);
        mPaintWhite.setPathEffect(new CornerPathEffect(10));

        mPaintBubble = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBubble.setColor(Color.WHITE);
        mPaintBubble.setStyle(Paint.Style.FILL);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.BLACK);
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setTextSize(24);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mPathWhite != null && mPathBlack != null) {
            canvas.drawPath(mPathBlack, mPaintBlack);
            canvas.drawPath(mPathWhite, mPaintWhite);
            canvas.drawPath(mPathBubble, mPaintBubble);
            canvas.drawText(String.valueOf(mProgress) + " %", Math.max(getPaddingLeft()-(int)(BUBBLE_WIDTH/3.2f), mProgress*mWidth/100-(int)(BUBBLE_WIDTH/3.2f)), mHeight/2-BUBBLE_HEIGHT/2 + calculatedeltaY(), mPaintText);
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

    private void makePathBlack() {

        if(mPathBlack ==null) {
            mPathBlack = new Path();
        }

        Path p =  new Path();
        p.moveTo(Math.max(getPaddingLeft(), mProgress*mWidth/100), mHeight/2 + calculatedeltaY());
        p.lineTo(mWidth, mHeight/2);

        mPathBlack.set(p);
    }

    private void makePathWhite() {

        if(mPathWhite == null) {
            mPathWhite = new Path();
        }

        Path p = new Path();
        p.moveTo(getPaddingLeft(), mHeight / 2);
        p.lineTo(Math.max(getPaddingLeft(), mProgress * mWidth / 100), mHeight / 2 + calculatedeltaY());

        mPathWhite.set(p);
    }

    private void makePathBubble() {

        if(mPathBubble == null) {
            mPathBubble = new Path();
        }

        int width = BUBBLE_WIDTH;
        int height = BUBBLE_HEIGHT;
        int arrowWidth = width/3;

        Rect r = new Rect(Math.max(getPaddingLeft()-width/2-arrowWidth/4, mProgress*mWidth/100-width/2-arrowWidth/4), mHeight/2-height + calculatedeltaY(), Math.max(getPaddingLeft()+width/2-arrowWidth/4, mProgress*mWidth/100+width/2-arrowWidth/4), mHeight/2+height-height + calculatedeltaY());
        int arrowHeight = (int) (arrowWidth/1.5f);
        int radius = 8;

        Path path = new Path();

        //down arrow
        path.moveTo(r.left + r.width()/2-arrowWidth/2, r.top + r.height()-arrowHeight);
        path.lineTo(r.left + r.width()/2, r.top + r.height());
        path.lineTo(r.left + r.width()/2+arrowWidth/2, r.top + r.height()-arrowHeight);

        //go to bottom-right
        path.lineTo(r.left + r.width()-radius, r.top + r.height()-arrowHeight);

        //bottom-right arc
        path.arcTo(new RectF(r.left + r.width()-2*radius, r.top + r.height()-arrowHeight-2*radius, r.left + r.width(), r.top + r.height()-arrowHeight), 90, -90);

        //go to upper-right
        path.lineTo(r.left + r.width(), r.top + arrowHeight);

        //upper-right arc
        path.arcTo(new RectF(r.left + r.width()-2*radius, r.top, r.right, r.top + 2*radius), 0, -90);

        //go to upper-left
        path.lineTo(r.left + radius, r.top);

        //upper-left arc
        path.arcTo(new RectF(r.left, r.top, r.left + 2*radius, r.top + 2*radius), 270, -90);

        //go to bottom-left
        path.lineTo(r.left, r.top + r.height()-arrowHeight-radius);

        //bottom-left arc
        path.arcTo(new RectF(r.left, r.top + r.height()-arrowHeight-2*radius, r.left + 2*radius, r.top + r.height()-arrowHeight), 180, -90);

        path.close();

        mPathBubble.set(path);
    }

    private int calculatedeltaY() {
        int wireTension = 12;
        if(mProgress <= 50) {
            return  (mProgress * mWidth/wireTension)/50;
        } else {
            return  ((100-mProgress) * mWidth/wireTension)/50;
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
        makePathBubble();
        invalidate();
    }

    public int getProgress() {
        return mProgress;
    }
}
