package is.arontibo.library.VectorCompat;

import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.NonNull;

/**
 * Defines a simple shape, used for bounding graphical regions.
 * <p>
 * Can be computed for a View, or computed by a Drawable, to drive the shape of
 * shadows cast by a View, or to clip the contents of the View.
 *
 * @see android.view.ViewOutlineProvider
 * @see android.view.View#setOutlineProvider(android.view.ViewOutlineProvider)
 */
public final class Outline {

    public Path mPath;
    public Rect mRect;
    public float mRadius;
    public float mAlpha;

    /**
     * Constructs an empty Outline. Call one of the setter methods to make
     * the outline valid for use with a View.
     */
    public Outline() {}

    /**
     * Constructs an Outline with a copy of the data in src.
     */
    public Outline(@NonNull Outline src) {
        set(src);
    }

    /**
     * Sets the outline to be empty.
     *
     * @see #isEmpty()
     */
    public void setEmpty() {
        mPath = null;
        mRect = null;
        mRadius = 0;
    }

    /**
     * Returns whether the Outline is empty.
     * <p>
     * Outlines are empty when constructed, or if {@link #setEmpty()} is called,
     * until a setter method is called
     *
     * @see #setEmpty()
     */
    public boolean isEmpty() {
        return mRect == null && mPath == null;
    }


    /**
     * Returns whether the outline can be used to clip a View.
     * <p>
     * Currently, only Outlines that can be represented as a rectangle, circle,
     * or round rect support clipping.
     *
     * {@link android.view.View#setClipToOutline(boolean)}
     */
    public boolean canClip() {
        return !isEmpty() && mRect != null;
    }

    /**
     * Sets the alpha represented by the Outline - the degree to which the
     * producer is guaranteed to be opaque over the Outline's shape.
     * <p>
     * An alpha value of <code>0.0f</code> either represents completely
     * transparent content, or content that isn't guaranteed to fill the shape
     * it publishes.
     * <p>
     * Content producing a fully opaque (alpha = <code>1.0f</code>) outline is
     * assumed by the drawing system to fully cover content beneath it,
     * meaning content beneath may be optimized away.
     */
    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }

    /**
     * Returns the alpha represented by the Outline.
     */
    public float getAlpha() {
        return mAlpha;
    }

    /**
     * Replace the contents of this Outline with the contents of src.
     *
     * @param src Source outline to copy from.
     */
    public void set(@NonNull Outline src) {
        if (src.mPath != null) {
            if (mPath == null) {
                mPath = new Path();
            }
            mPath.set(src.mPath);
            mRect = null;
        }
        if (src.mRect != null) {
            if (mRect == null) {
                mRect = new Rect();
            }
            mRect.set(src.mRect);
        }
        mRadius = src.mRadius;
        mAlpha = src.mAlpha;
    }

    /**
     * Sets the Outline to the rounded rect defined by the input rect, and
     * corner radius.
     */
    public void setRect(int left, int top, int right, int bottom) {
        setRoundRect(left, top, right, bottom, 0.0f);
    }

    /**
     * Convenience for {@link #setRect(int, int, int, int)}
     */
    public void setRect(@NonNull Rect rect) {
        setRect(rect.left, rect.top, rect.right, rect.bottom);
    }

    /**
     * Sets the Outline to the rounded rect defined by the input rect, and corner radius.
     * <p>
     * Passing a zero radius is equivalent to calling {@link #setRect(int, int, int, int)}
     */
    public void setRoundRect(int left, int top, int right, int bottom, float radius) {
        if (left >= right || top >= bottom) {
            setEmpty();
            return;
        }

        if (mRect == null) mRect = new Rect();
        mRect.set(left, top, right, bottom);
        mRadius = radius;
        mPath = null;
    }

    /**
     * Convenience for {@link #setRoundRect(int, int, int, int, float)}
     */
    public void setRoundRect(@NonNull Rect rect, float radius) {
        setRoundRect(rect.left, rect.top, rect.right, rect.bottom, radius);
    }

}
