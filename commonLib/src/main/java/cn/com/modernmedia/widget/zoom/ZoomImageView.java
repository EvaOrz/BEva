package cn.com.modernmedia.widget.zoom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Matrix;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.animation.AccelerateInterpolator;

/**
 * 缩放imageView(仅支持center&(crop|fitxy))
 *
 * @author user
 */
public class ZoomImageView extends CSTImageView implements
        OnViewInterceptTouchEvent {
    /**
     * 无状态
     */
    private static final int NONE = 0;
    /**
     * 拖动状态
     */
    private static final int DRAG = 1;
    /**
     * 缩放状态
     */
    private static final int ZOOM = 2;

    /**
     * 初始缩放比例
     */
    private static final float DEFAULT_SCALE = 1.0f;
    /**
     * 最小缩放比例
     */
    private static final float MIN_SCALE = 1.0f;
    /**
     * 最大缩放比例
     */
    private static final float MAX_SCALE = 10.0f;

    private float mInitMotionX;
    private float mInitMotionY;
    private float mLastMotionX;
    private float mLastMotionY;

    /**
     * 初始时候的x缩放比例
     */
    private float mOriScaleX;
    /**
     * 初始时候的y缩放比例
     */
    private float mOriScaleY;
    /**
     * 初始时候的x偏移
     */
    private float mOriTranX;
    /**
     * 初始时候的y偏移
     */
    private float mOriTranY;

    /**
     * 动画时上一次缩放比例
     */
    private float mLastScaleInAnim;

    private int mState = NONE;
    /**
     * 包含了matrix的9个状态
     */
    private float[] mValues = new float[9];
    private float mSaveScale = DEFAULT_SCALE;

    /**
     * 上次点击时间
     */
    private long mLastClickTime;

    /**
     * 是否正在执行动画
     */
    private boolean mAniming;

    /**
     * 缩放手势
     */
    private ScaleGestureDetector mScaleDetector;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMatrix.getValues(mValues);
        mOriScaleX = mValues[Matrix.MSCALE_X];
        mOriScaleY = mValues[Matrix.MSCALE_Y];
        mOriTranX = mValues[Matrix.MTRANS_X];
        mOriTranY = mValues[Matrix.MTRANS_Y];
    }

    private void init() {
        mScaleDetector = new ScaleGestureDetector(mContext,
                new OnScaleListener());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mState == ZOOM) {
            return true;
        }
        final int action = event.getAction() & MotionEventCompat.ACTION_MASK;
        final float x = event.getX();
        final float y = event.getY();
        if (action == MotionEvent.ACTION_DOWN
                || action == MotionEvent.ACTION_POINTER_DOWN) {
            mInitMotionX = x;
            mInitMotionY = y;
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (Math.round(mSaveScale - DEFAULT_SCALE) == 0)
                return false;
            return checkShouldLock(event);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mAniming)
            return true;
        mScaleDetector.onTouchEvent(event);
        final int action = event.getAction() & MotionEventCompat.ACTION_MASK;
        final float x = event.getX();
        final float y = event.getY();
        if (action == MotionEvent.ACTION_DOWN) {
            mInitMotionX = x;
            mInitMotionY = y;
            mLastMotionX = x;
            mLastMotionY = y;
            mState = DRAG;
        } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
            mState = ZOOM;
        } else if (action == MotionEvent.ACTION_MOVE) {
            final float deltaX = x - mLastMotionX;
            final float deltaY = y - mLastMotionY;
            mLastMotionX = x;
            mLastMotionY = y;
            if (mState == DRAG) {
                onDrag(deltaX, deltaY);
            }
        } else if (action == MotionEvent.ACTION_UP) {
            final float diffX = Math.abs(x - mInitMotionX);
            final float diffY = Math.abs(y - mInitMotionY);
            if (diffX < 10 && diffY < 10) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - mLastClickTime <= 600) {
                    // NOTE 双击
                    doDoubleClick();
                    mLastClickTime = 0;
                } else {
                    mLastClickTime = clickTime;
                }
            } else {
                mLastClickTime = 0;
            }
            mState = NONE;
        } else if (action == MotionEvent.ACTION_POINTER_UP) {
            if (mSaveScale < DEFAULT_SCALE)
                resetScale();
            mState = NONE;
        }

        setImageMatrix(mMatrix);
        return true;
    }

    /**
     * 判断是否需要拦截手势
     *
     * @return
     */
    private boolean checkShouldLock(MotionEvent event) {
        mMatrix.getValues(mValues);
        final float mTransX = mValues[Matrix.MTRANS_X];
        final float mScaleX = mValues[Matrix.MSCALE_X];
        final float mScaleWidth = bmWidth * mScaleX;

        /**
         * view宽与bitmap宽差值
         */
        final float xSpace = Math.round(mWidth - mScaleWidth);

        if (xSpace > 0) {
            return true;
        }

        final float x = event.getX();
        final float y = event.getY();
        final float diffX = x - mInitMotionX;
        final float diffY = y - mInitMotionY;

        if (Math.abs(diffX) < Math.abs(diffY) * 2) {
            return true;
        }

        if (mTransX == 0) {
            // 在最左边
            if (diffX > 0) {
                return false;
            }
        }
        // 可能同时在左边和右边(space==0)，所以不要else!!
        if (Math.round(mTransX - xSpace) == 0) {
            // 在最右边
            if (diffX < 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * 移动
     *
     * @param x
     * @param y
     */
    private void onDrag(float x, float y) {
        mMatrix.getValues(mValues);
        final float mTransX = mValues[Matrix.MTRANS_X];
        final float mTransY = mValues[Matrix.MTRANS_Y];
        final float mScaleX = mValues[Matrix.MSCALE_X];
        final float mScaleWidth = bmWidth * mScaleX;
        final float mScaleY = mValues[Matrix.MSCALE_Y];
        final float mScaleHeight = bmHeight * mScaleY;

        /**
         * view宽与bitmap宽差值
         */
        final float xSpace = Math.round(mWidth - mScaleWidth);
        /**
         * view高于bitmap高差值
         */
        final float ySpace = Math.round(mHeight - mScaleHeight);

        if (xSpace == 0) {
            // NOTE fit size
            x = -mTransX;
        } else if (xSpace > 0) {
            // NOTE bitmap宽在view内,保持居中
            x = (mWidth - mScaleWidth) / 2 - mTransX;
        } else {
            if (mTransX + x > 0) {
                x = -mTransX;
            } else if (mTransX + x < xSpace) {
                x = xSpace - mTransX;
            }
        }

        if (ySpace == 0) {
            // NOTE fit size
            y = -mTransY;
        } else if (ySpace > 0) {
            // NOTE bitmap高在view内
            y = (mHeight - mScaleHeight) / 2 - mTransY;
        } else {
            if (mTransY + y > 0) {
                y = -mTransY;
            } else if (mTransY + y < ySpace) {
                y = ySpace - mTransY;
            }
        }

        if (x != 0 || y != 0) {
            mMatrix.postTranslate(x, y);
        }
    }

    /**
     * 缩放
     *
     * @param scaleFactor 缩放比例
     * @param focusX
     * @param focusY
     */
    private void doScale(float scaleFactor, float focusX, float focusY) {
        mMatrix.getValues(mValues);
        final float mToScaleX = mValues[Matrix.MSCALE_X] * scaleFactor;
        final float mToScaleY = mValues[Matrix.MSCALE_Y] * scaleFactor;

        Pivot pivot = calcPivotXY(mToScaleX, mToScaleY, focusX, focusY);
        mMatrix.postScale(scaleFactor, scaleFactor, pivot.x, pivot.y);

        onDrag(0, 0);
    }

    /**
     * 计算相对中心点
     *
     * @param mToScaleX
     * @param mToScaleY
     * @param focusX
     * @param focusY
     * @return
     */
    private Pivot calcPivotXY(float mToScaleX, float mToScaleY, float focusX,
                              float focusY) {
        final float mToScaleWidth = bmWidth * mToScaleX;
        final float mToScaleHeight = bmHeight * mToScaleY;
        final float xToSpace = Math.round(mWidth - mToScaleWidth);
        final float yToSpace = Math.round(mHeight - mToScaleHeight);

        Pivot pivot = new Pivot();

        if (xToSpace >= 0 && yToSpace >= 0) {
            pivot.x = mWidth / 2;
            pivot.y = mHeight / 2;
        } else {
            if (xToSpace < 0 && yToSpace < 0) {
                pivot.x = focusX;
                pivot.y = focusY;
            } else if (xToSpace < 0) {
                pivot.x = focusX;
                pivot.y = mHeight / 2;
            } else {
                pivot.x = mWidth / 2;
                pivot.y = focusY;
            }
        }
        return pivot;
    }

    /**
     * 双击
     */
    private void doDoubleClick() {
        final float from = mSaveScale;
        if (mSaveScale < MAX_SCALE / 2) {
            mSaveScale = MAX_SCALE / 2;
        } else if (Math.round(mSaveScale - MAX_SCALE) == 0) {
            mSaveScale = DEFAULT_SCALE;
        } else {
            mSaveScale = MAX_SCALE;
        }
        final float to = mSaveScale;
        doAnim(from, to, mInitMotionX, mInitMotionY,
                mSaveScale == DEFAULT_SCALE);
    }

    /**
     * 重置
     */
    public void resetScale() {
        doAnim(mSaveScale, DEFAULT_SCALE, mWidth / 2, mHeight / 2, true);
        mSaveScale = DEFAULT_SCALE;
    }

    /**
     * 重置，无动画
     */
    public void resetScaleNoAnim() {
        clearAnimation();
        mMatrix.setScale(mOriScaleX, mOriScaleY);
        mMatrix.postTranslate(mOriTranX, mOriTranY);
        setImageMatrix(mMatrix);
        mSaveScale = DEFAULT_SCALE;
    }

    /**
     * 执行动画
     */
    private void doAnim(float from, float to, final float focusX,
                        final float focusY, final boolean reset) {
        mAniming = true;
        ValueAnimator animator = ValueAnimator.ofFloat(from, to);
        mLastScaleInAnim = from;
        animator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currScale = (Float) animation.getAnimatedValue();
                // NOTE 算法：a/from * b/a * c/b *.......* n/m * to/n
                float scaleFactor = currScale / mLastScaleInAnim;// 追加缩放比例
                doScale(scaleFactor, focusX, focusY);
                setImageMatrix(mMatrix);
                mLastScaleInAnim = currScale;
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                mAniming = false;
                if (reset) {
                    resetScaleNoAnim();
                }
            }

        });
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    /**
     * 缩放手势监听
     *
     * @author user
     */
    private class OnScaleListener extends SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            scaleFactor = Math.min(1.05f, Math.max(0.95f, scaleFactor));
            final float originalScale = mSaveScale;
            mSaveScale *= scaleFactor;
            if (mSaveScale > MAX_SCALE) {
                mSaveScale = MAX_SCALE;
                scaleFactor = MAX_SCALE / originalScale;
            } else if (mSaveScale < MIN_SCALE) {
                mSaveScale = MIN_SCALE;
                scaleFactor = MIN_SCALE / originalScale;
            }

            doScale(scaleFactor, detector.getFocusX(), detector.getFocusY());
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mState = ZOOM;
            return true;
        }

    }

    private class Pivot {
        public float x;
        public float y;
    }

}