package is.arontibo.library;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by thibaultguegan on 15/02/15.
 */
public class ProgressDownloadView extends View {

    private static final String LOG_TAG = ProgressDownloadView.class.getSimpleName();

    public static final long ANIMATION_DURATION_BASE = 1250;

    private int mWidth, mHeight, bubbleAnchorX, bubbleAnchorY, mBubbleWidth, mBubbleHeight, mPadding;
    private Path mPathBlack, mPathWhite, mPathBubble;
    private Paint mPaintBlack, mPaintWhite, mPaintBubble, mPaintText;
    private float mDensity = getResources().getDisplayMetrics().density;
    private float mProgress = 0, mTarget = 0, mSpeedAngle = 0, mBubbleAngle = 0, mFailAngle = 0, mFlipFactor;
    private State mState = State.STATE_WORKING;

    private enum State {
        STATE_WORKING,
        STATE_FAILED,
        STATE_SUCCESS
    }

    /**
     * MARK: Constructor
     */

    public ProgressDownloadView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPadding = (int) (30 * mDensity);
        mBubbleWidth = (int) (45 * mDensity);
        mBubbleHeight = (int) (35 * mDensity);

        setPadding(mPadding, 0, mPadding, 0);

        mPaintBlack = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBlack.setStyle(Paint.Style.STROKE);
        mPaintBlack.setStrokeWidth(5 * mDensity);
        mPaintBlack.setColor(getResources().getColor(R.color.red_wood));
        mPaintBlack.setStrokeCap(Paint.Cap.ROUND);
        //mPaintBlack.setPathEffect(new CornerPathEffect(5*mDensity));

        mPaintWhite = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintWhite.setStyle(Paint.Style.STROKE);
        mPaintWhite.setStrokeWidth(5 * mDensity);
        mPaintWhite.setColor(Color.WHITE);
        mPaintWhite.setStrokeCap(Paint.Cap.ROUND);
        //mPaintWhite.setPathEffect(new CornerPathEffect(5*mDensity));

        mPaintBubble = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBubble.setColor(Color.WHITE);
        mPaintBubble.setStyle(Paint.Style.FILL);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.BLACK);
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setTextSize(12 * mDensity);
    }

    /**
     * MARK: Overrides
     */

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPathWhite != null && mPathBlack != null) {

            float textX = Math.max(getPaddingLeft() - (int) (mBubbleWidth / 4.0f), mProgress * mWidth / 100 - (int) (mBubbleWidth / 4.0f));
            float textY = mHeight / 2 - mBubbleHeight / 2 + calculateDeltaY();

            switch (mState) {
                case STATE_WORKING:
                    // Save and restore prevent the rest of the canvas to not be rotated
                    canvas.save();
                    float speed = (getProgress() - mTarget) / 20;
                    mBubbleAngle += speed * 10;
                    if (mBubbleAngle > 20) {
                        mBubbleAngle = 20;
                    }
                    if (mBubbleAngle < -20) {
                        mBubbleAngle = -20;
                    }
                    if (Math.abs(speed) < 1) {
                        mSpeedAngle -= mBubbleAngle / 20;
                        mSpeedAngle *= .9f;
                    }
                    mBubbleAngle += mSpeedAngle;

                    canvas.rotate(mBubbleAngle, bubbleAnchorX, bubbleAnchorY);
                    canvas.drawPath(mPathBubble, mPaintBubble);
                    canvas.drawText(String.valueOf((int) mProgress) + " %", textX, textY, mPaintText);
                    canvas.restore();
                    break;
                case STATE_FAILED:
                    canvas.save();
                    canvas.rotate(mFailAngle, bubbleAnchorX, bubbleAnchorY);
                    canvas.drawPath(mPathBubble, mPaintBubble);
                    canvas.rotate(mFailAngle, bubbleAnchorX, textY - mBubbleHeight / 7);
                    mPaintText.setColor(getResources().getColor(R.color.red_wine));
                    textX = Math.max(getPaddingLeft() - (int) (mBubbleWidth / 3.2f), mProgress * mWidth / 100 - (int) (mBubbleWidth / 3.2f));
                    canvas.drawText(getResources().getString(R.string.failed), textX, textY, mPaintText);
                    canvas.restore();
                    break;
                case STATE_SUCCESS:
                    canvas.save();
                    mPaintText.setColor(getResources().getColor(R.color.green_grass));
                    textX = Math.max(getPaddingLeft() - (int) (mBubbleWidth / 3.2f), mProgress * mWidth / 100 - (int) (mBubbleWidth / 3.2f));
                    Matrix flipMatrix = new Matrix();
                    flipMatrix.setScale(mFlipFactor, 1, bubbleAnchorX, bubbleAnchorY);
                    canvas.concat(flipMatrix);
                    canvas.drawPath(mPathBubble, mPaintBubble);
                    canvas.concat(flipMatrix);
                    canvas.drawText(getResources().getString(R.string.done), textX, textY, mPaintText);
                    canvas.restore();
                    break;
            }

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

        // Call this if the enter animation is not used
        //setPercentage(mProgress);
    }

    /**
     * MARK: Update drawings
     */

    private void makePathBlack() {

        if (mPathBlack == null) {
            mPathBlack = new Path();
        }

        Path p = new Path();
        p.moveTo(Math.max(getPaddingLeft(), mProgress * mWidth / 100), mHeight / 2 + calculateDeltaY());
        p.lineTo(mWidth, mHeight / 2);

        mPathBlack.set(p);
    }

    private void makePathWhite() {

        if (mPathWhite == null) {
            mPathWhite = new Path();
        }

        Path p = new Path();
        p.moveTo(getPaddingLeft(), mHeight / 2);
        p.lineTo(Math.max(getPaddingLeft(), mProgress * mWidth / 100), mHeight / 2 + calculateDeltaY());

        mPathWhite.set(p);
    }

    private void makePathBubble() {

        if (mPathBubble == null) {
            mPathBubble = new Path();
        }

        int width = mBubbleWidth;
        int height = mBubbleHeight;
        int arrowWidth = width / 3;

        //Rect r = new Rect(Math.max(getPaddingLeft()-width/2-arrowWidth/4, mProgress*mWidth/100-width/2-arrowWidth/4), mHeight/2-height + calculatedeltaY(), Math.max(getPaddingLeft()+width/2-arrowWidth/4, mProgress*mWidth/100+width/2-arrowWidth/4), mHeight/2+height-height + calculatedeltaY());
        Rect r = new Rect((int) (Math.max(getPaddingLeft() - width / 2, mProgress * mWidth / 100 - width / 2)), (int) (mHeight / 2 - height + calculateDeltaY()), (int) (Math.max(getPaddingLeft() + width / 2, mProgress * mWidth / 100 + width / 2)), (int) (mHeight / 2 + height - height + calculateDeltaY()));
        int arrowHeight = (int) (arrowWidth / 1.5f);
        int radius = 8;

        Path path = new Path();

        // Down arrow
        path.moveTo(r.left + r.width() / 2 - arrowWidth / 2, r.top + r.height() - arrowHeight);
        bubbleAnchorX = r.left + r.width() / 2;
        bubbleAnchorY = r.top + r.height();
        path.lineTo(bubbleAnchorX, bubbleAnchorY);
        path.lineTo(r.left + r.width() / 2 + arrowWidth / 2, r.top + r.height() - arrowHeight);

        // Go to bottom-right
        path.lineTo(r.left + r.width() - radius, r.top + r.height() - arrowHeight);

        // Bottom-right arc
        path.arcTo(new RectF(r.left + r.width() - 2 * radius, r.top + r.height() - arrowHeight - 2 * radius, r.left + r.width(), r.top + r.height() - arrowHeight), 90, -90);

        // Go to upper-right
        path.lineTo(r.left + r.width(), r.top + arrowHeight);

        // Upper-right arc
        path.arcTo(new RectF(r.left + r.width() - 2 * radius, r.top, r.right, r.top + 2 * radius), 0, -90);

        // Go to upper-left
        path.lineTo(r.left + radius, r.top);

        // Upper-left arc
        path.arcTo(new RectF(r.left, r.top, r.left + 2 * radius, r.top + 2 * radius), 270, -90);

        // Go to bottom-left
        path.lineTo(r.left, r.top + r.height() - arrowHeight - radius);

        // Bottom-left arc
        path.arcTo(new RectF(r.left, r.top + r.height() - arrowHeight - 2 * radius, r.left + 2 * radius, r.top + r.height() - arrowHeight), 180, -90);

        path.close();

        mPathBubble.set(path);
    }

    /**
     * MARK: Animation functions
     */

    private float calculateDeltaY() {
        int wireTension = 15;
        if (mProgress <= 50) {
            return (mProgress * mWidth / wireTension) / 50 + Math.abs((mTarget - getProgress()) / wireTension) + Math.abs(mBubbleAngle);
        } else {
            return ((100 - mProgress) * mWidth / wireTension) / 50 + Math.abs((mTarget - getProgress()) / wireTension) + Math.abs(mBubbleAngle);
        }
    }

    public void setPercentage(float newProgress) {
        if (newProgress < 0 || newProgress > 100) {
            throw new IllegalArgumentException("setPercentage not between 0 and 100");
        }

        mState = State.STATE_WORKING;
        mTarget = newProgress;

        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "progress", getProgress(), mTarget);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration((long) (ANIMATION_DURATION_BASE + Math.abs(mTarget * 10 - getProgress() * 10) / 2));
        anim.start();
    }

    public void setProgress(float progress) {
        mProgress = progress;
        makePathBlack();
        makePathWhite();
        makePathBubble();
        invalidate();
    }

    public void setFailAngle(float failAngle) {
        mFailAngle = failAngle;
        makePathBlack();
        makePathWhite();
        makePathBubble();
        invalidate();
    }

    public void setFlip(float flipValue) {
        mFlipFactor = flipValue;
        makePathBlack();
        makePathWhite();
        makePathBubble();
        invalidate();
    }

    public float getProgress() {
        return mProgress;
    }

    public void drawFail() {
        mState = State.STATE_FAILED;

        ObjectAnimator failAnim = ObjectAnimator.ofFloat(this, "failAngle", 0, 180);
        failAnim.setInterpolator(new OvershootInterpolator());

        //This one doesn't do much actually, we just use it to take advantage of associating two different interpolators
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "progress", getProgress(), mTarget);
        anim.setInterpolator(new AccelerateInterpolator());

        AnimatorSet set = new AnimatorSet();
        set.setDuration((long) (ANIMATION_DURATION_BASE / 1.7f));
        set.playTogether(
                failAnim,
                anim
        );
        set.start();
    }

    public void drawSuccess() {
        mTarget = 100;

        final ObjectAnimator successAnim = ObjectAnimator.ofFloat(this, "flip", 1, -1);
        successAnim.setInterpolator(new OvershootInterpolator());
        successAnim.setDuration(ANIMATION_DURATION_BASE);

        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "progress", getProgress(), mTarget);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration((long) (ANIMATION_DURATION_BASE + Math.abs(mTarget * 10 - getProgress() * 10) / 2));
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mState = State.STATE_SUCCESS;
                successAnim.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }
}
