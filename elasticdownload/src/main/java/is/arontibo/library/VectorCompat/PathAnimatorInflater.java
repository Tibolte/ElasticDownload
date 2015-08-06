package is.arontibo.library.VectorCompat;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.TypeEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.InflateException;
import android.view.animation.AnimationUtils;

import java.io.IOException;
import java.util.ArrayList;

import is.arontibo.library.R;

public class PathAnimatorInflater {

    private static final String LOG_TAG = "PathAnimatorInflater";
    /**
     * These flags are used when parsing PathAnimatorSet objects
     */
    private static final int TOGETHER = 0;
    private static final int SEQUENTIALLY = 1;

    private static final int VALUE_TYPE_PATH = 2;

    private static final boolean DBG_ANIMATOR_INFLATER = false;

    public static Animator loadAnimator(Context c, Resources resources, Resources.Theme theme, int id,
                                        float pathErrorScale) throws Resources.NotFoundException {

        XmlResourceParser parser = null;
        try {
            parser = resources.getAnimation(id);
            return createAnimatorFromXml(c, resources, theme, parser, pathErrorScale);
        } catch (XmlPullParserException ex) {
            Resources.NotFoundException rnf =
                    new Resources.NotFoundException("Can't load animation resource ID #0x" +
                            Integer.toHexString(id));
            rnf.initCause(ex);
            throw rnf;
        } catch (IOException ex) {
            Resources.NotFoundException rnf =
                    new Resources.NotFoundException("Can't load animation resource ID #0x" +
                            Integer.toHexString(id));
            rnf.initCause(ex);
            throw rnf;
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
    }

    private static Animator createAnimatorFromXml(Context c, Resources res, Resources.Theme theme, XmlPullParser parser,
                                                  float pixelSize)
            throws XmlPullParserException, IOException {
        return createAnimatorFromXml(c, res, theme, parser, Xml.asAttributeSet(parser), null, 0,
                pixelSize);
    }

    private static Animator createAnimatorFromXml(Context c, Resources res, Resources.Theme theme, XmlPullParser parser,
                                                  AttributeSet attrs, AnimatorSet parent, int sequenceOrdering, float pixelSize)
            throws XmlPullParserException, IOException {

        Animator anim = null;
        ArrayList<Animator> childAnims = null;

        // Make sure we are on a start tag.
        int type;
        int depth = parser.getDepth();

        while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
                && type != XmlPullParser.END_DOCUMENT) {

            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals("objectAnimator")) {
                anim = loadObjectAnimator(c, res, theme, attrs, pixelSize);
            } else if (name.equals("animator")) {
                anim = loadAnimator(c, res, theme, attrs, null, pixelSize);
            } else if (name.equals("set")) {
                anim = new AnimatorSet();
                //TODO: don't care about 'set' attributes for now
//                TypedArray a;
//                if (theme != null) {
//                    a = theme.obtainStyledAttributes(attrs, AnimatorSet, 0, 0);
//                } else {
//                    a = res.obtainAttributes(attrs, AnimatorSet);
//                }
//                int ordering = a.getInt(R.styleable.AnimatorSet_ordering,
//                        TOGETHER);
                createAnimatorFromXml(c, res, theme, parser, attrs, (AnimatorSet) anim, TOGETHER,
                        pixelSize);
//                a.recycle();
            } else {
                throw new RuntimeException("Unknown animator name: " + parser.getName());
            }

            if (parent != null) {
                if (childAnims == null) {
                    childAnims = new ArrayList<Animator>();
                }
                childAnims.add(anim);
            }
        }
        if (parent != null && childAnims != null) {
            Animator[] animsArray = new Animator[childAnims.size()];
            int index = 0;
            for (Animator a : childAnims) {
                animsArray[index++] = a;
            }
            if (sequenceOrdering == TOGETHER) {
                parent.playTogether(animsArray);
            } else {
                parent.playSequentially(animsArray);
            }
        }

        return anim;

    }

    private static ObjectAnimator loadObjectAnimator(Context c, Resources res, Resources.Theme theme, AttributeSet attrs,
                                                     float pathErrorScale) throws Resources.NotFoundException {
        ObjectAnimator anim = new ObjectAnimator();

        loadAnimator(c, res, theme, attrs, anim, pathErrorScale);

        return anim;
    }

    /**
     * Creates a new animation whose parameters come from the specified context
     * and attributes set.
     *
     * @param res   The resources
     * @param attrs The set of attributes holding the animation parameters
     * @param anim  Null if this is a ValueAnimator, otherwise this is an
     *              ObjectAnimator
     */
    private static ValueAnimator loadAnimator(Context c, Resources res, Resources.Theme theme,
                                              AttributeSet attrs, ValueAnimator anim, float pathErrorScale)
            throws Resources.NotFoundException {

        TypedArray arrayAnimator = null;
        TypedArray arrayObjectAnimator = null;

        if (theme != null) {
            arrayAnimator = theme.obtainStyledAttributes(attrs, R.styleable.Animator, 0, 0);
        } else {
            arrayAnimator = res.obtainAttributes(attrs, R.styleable.Animator);
        }

        // If anim is not null, then it is an object animator.
        if (anim != null) {
            if (theme != null) {
                arrayObjectAnimator = theme.obtainStyledAttributes(attrs,
                        R.styleable.PropertyAnimator, 0, 0);
            } else {
                arrayObjectAnimator = res.obtainAttributes(attrs, R.styleable.PropertyAnimator);
            }
        }

        if (anim == null) {
            anim = new ValueAnimator();
        }

        parseAnimatorFromTypeArray(anim, arrayAnimator, arrayObjectAnimator);

        final int resId =
                arrayAnimator.getResourceId(R.styleable.Animator_android_interpolator, 0);
        if (resId > 0) {
            anim.setInterpolator(AnimationUtils.loadInterpolator(c, resId));
        }

        arrayAnimator.recycle();
        if (arrayObjectAnimator != null) {
            arrayObjectAnimator.recycle();
        }

        return anim;
    }

    /**
     * @param anim                The animator, must not be null
     * @param arrayAnimator       Incoming typed array for Animator's attributes.
     * @param arrayObjectAnimator Incoming typed array for Object Animator's
     *                            attributes.
     */
    private static void parseAnimatorFromTypeArray(ValueAnimator anim,
                                                   TypedArray arrayAnimator, TypedArray arrayObjectAnimator) {
        long duration = arrayAnimator.getInt(R.styleable.Animator_android_duration, 300);

        long startDelay = arrayAnimator.getInt(R.styleable.Animator_android_startOffset, 0);

        int valueType = arrayAnimator.getInt(R.styleable.Animator_vc_valueType, 0);

        TypeEvaluator evaluator = null;

        // Must be a path animator by the time I reach here
        if (valueType == VALUE_TYPE_PATH) {
            evaluator = setupAnimatorForPath(anim, arrayAnimator);
        } else {
            throw new IllegalArgumentException("target is not a pathType target");
        }

        anim.setDuration(duration);
        anim.setStartDelay(startDelay);

        if (arrayAnimator.hasValue(R.styleable.Animator_android_repeatCount)) {
            anim.setRepeatCount(
                    arrayAnimator.getInt(R.styleable.Animator_android_repeatCount, 0));
        }
        if (arrayAnimator.hasValue(R.styleable.Animator_android_repeatMode)) {
            anim.setRepeatMode(
                    arrayAnimator.getInt(R.styleable.Animator_android_repeatMode,
                            ValueAnimator.RESTART));
        }
        if (evaluator != null) {
            anim.setEvaluator(evaluator);
        }

        if (arrayObjectAnimator != null) {
            setupObjectAnimator(anim, arrayObjectAnimator);
        }
    }

    /**
     * Setup the Animator to achieve path morphing.
     *
     * @param anim          The target Animator which will be updated.
     * @param arrayAnimator TypedArray for the ValueAnimator.
     * @return the PathDataEvaluator.
     */
    private static TypeEvaluator setupAnimatorForPath(ValueAnimator anim,
                                                      TypedArray arrayAnimator) {
        TypeEvaluator evaluator = null;
        String fromString = arrayAnimator.getString(R.styleable.Animator_vc_valueFrom);
        String toString = arrayAnimator.getString(R.styleable.Animator_vc_valueTo);
        PathParser.PathDataNode[] nodesFrom = PathParser.createNodesFromPathData(fromString);
        PathParser.PathDataNode[] nodesTo = PathParser.createNodesFromPathData(toString);

        if (nodesFrom != null) {
            if (nodesTo != null) {
                anim.setObjectValues(nodesFrom, nodesTo);
                if (!PathParser.canMorph(nodesFrom, nodesTo)) {
                    throw new InflateException(arrayAnimator.getPositionDescription()
                            + " Can't morph from " + fromString + " to " + toString);
                }
            } else {
                anim.setObjectValues((Object) nodesFrom);
            }
            evaluator = new PathDataEvaluator(PathParser.deepCopyNodes(nodesFrom));
        } else if (nodesTo != null) {
            anim.setObjectValues((Object) nodesTo);
            evaluator = new PathDataEvaluator(PathParser.deepCopyNodes(nodesTo));
        }

        if (DBG_ANIMATOR_INFLATER && evaluator != null) {
            Log.v(LOG_TAG, "create a new PathDataEvaluator here");
        }

        return evaluator;
    }

    /**
     * Setup ObjectAnimator's property or values from pathData.
     *
     * @param anim                The target Animator which will be updated.
     * @param arrayObjectAnimator TypedArray for the ObjectAnimator.
     */
    private static void setupObjectAnimator(ValueAnimator anim, TypedArray arrayObjectAnimator) {
        ObjectAnimator oa = (ObjectAnimator) anim;
        String propertyName =
                arrayObjectAnimator.getString(R.styleable.PropertyAnimator_vc_propertyName);
        oa.setPropertyName(propertyName);
    }

    /**
     * PathDataEvaluator is used to interpolate between two paths which are
     * represented in the same format but different control points' values.
     * The path is represented as an array of PathDataNode here, which is
     * fundamentally an array of floating point numbers.
     */
    private static class PathDataEvaluator implements TypeEvaluator<PathParser.PathDataNode[]> {
        private PathParser.PathDataNode[] mNodeArray;

        /**
         * Create a PathParser.PathDataNode[] that does not reuse the animated value.
         * Care must be taken when using this option because on every evaluation
         * a new <code>PathParser.PathDataNode[]</code> will be allocated.
         */
        private PathDataEvaluator() {
        }

        /**
         * Create a PathDataEvaluator that reuses <code>nodeArray</code> for every evaluate() call.
         * Caution must be taken to ensure that the value returned from
         * {@link android.animation.ValueAnimator#getAnimatedValue()} is not cached, modified, or
         * used across threads. The value will be modified on each <code>evaluate()</code> call.
         *
         * @param nodeArray The array to modify and return from <code>evaluate</code>.
         */
        public PathDataEvaluator(PathParser.PathDataNode[] nodeArray) {
            mNodeArray = nodeArray;
        }

        @Override
        public PathParser.PathDataNode[] evaluate(float fraction,
                                                  PathParser.PathDataNode[] startPathData,
                                                  PathParser.PathDataNode[] endPathData) {
            if (!PathParser.canMorph(startPathData, endPathData)) {
                throw new IllegalArgumentException("Can't interpolate between"
                        + " two incompatible pathData");
            }

            if (mNodeArray == null || !PathParser.canMorph(mNodeArray, startPathData)) {
                mNodeArray = PathParser.deepCopyNodes(startPathData);
            }

            for (int i = 0; i < startPathData.length; i++) {
                mNodeArray[i].interpolatePathDataNode(startPathData[i],
                        endPathData[i], fraction);
            }

            return mNodeArray;
        }
    }

}
