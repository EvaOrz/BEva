package cn.com.modernmedia.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import cn.com.modernmedia.R;

public class RedProcess extends View {
	public static final int MAX_FONT_SIZE = 25;
	public static final int MIN_FONT_SIZE = 5;
	private Context mContext;
	private int mProgress;
	private boolean mInAnimation;
	private long mAnimationDuration;
	private Paint mCirclePaint;
	private Paint mProgressPaint;
	private TextPaint mTextPaint;
	private float mDensity;
	private int mOvalWidth = 0;
	private int mOvalHeight = 0;
	private String mText;// 在process里的text
	private float mTextCenterY;

	public RedProcess(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public RedProcess(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		mDensity = mContext.getResources().getDisplayMetrics().density;

		mCirclePaint = new Paint(1);
		mCirclePaint.setStyle(Paint.Style.STROKE);
		mCirclePaint.setColor(getResources().getColor(
				R.color.process_loading_ring_bg));
		mCirclePaint.setStrokeWidth(2.0F * mDensity);

		mProgressPaint = new Paint(1);
		mProgressPaint.setStyle(Paint.Style.STROKE);
		mProgressPaint.setColor(getResources().getColor(
				R.color.process_loading_red_ring));
		mProgressPaint.setStrokeWidth(2.0F * mDensity);

		mTextPaint = new TextPaint(1);
		mTextPaint.setColor(Color.argb(0, 255, 255, 255));
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setTextSize(25.0F * mDensity);
	}

	public void setText(String text) {
		mText = text;
	}

	public void setOvalSize(int ovalWidth, int ovalHeight) {
		this.mOvalWidth = ovalWidth;
		this.mOvalHeight = ovalHeight;
	}

	public void setStrokeWidth(float circleStrokeWidth,
			float progressStrokeWidth) {
		mCirclePaint.setStrokeWidth(circleStrokeWidth * mDensity);
		mProgressPaint.setStrokeWidth(progressStrokeWidth * mDensity);
	}

	public void setCirclePaintColor(int color) {
		mCirclePaint.setColor(color);
	}

	public void setProgressPaintColor(int color) {
		mProgressPaint.setColor(color);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawProgress(canvas);
		if (!TextUtils.isEmpty(mText)) {
			mTextPaint.setColor(Color.argb(Math.round(mProgress * 255 / 100),
					255, 255, 255));
			if (mInAnimation) {
				mTextPaint.setColor(Color.argb(255, 255, 255, 255));
			}
			canvas.drawText(mText, getWidth() / 2.0F, mTextCenterY, mTextPaint);
		}
		if (mInAnimation) {
			postInvalidateDelayed(mAnimationDuration);
			mProgress += 1;
			mProgress %= 100;
		}
	}

	private void drawProgress(Canvas canvas) {
		float circleStrokeWidth = mCirclePaint.getStrokeWidth();
		float padding = circleStrokeWidth / 2.0F;
		RectF oval = new RectF(padding + (getWidth() - mOvalWidth) / 2, padding
				+ (getHeight() - mOvalHeight) / 2, (getWidth() + mOvalWidth)
				/ 2 - padding, (getHeight() + mOvalHeight) / 2 - padding);
		if ((mOvalWidth == 0) && (mOvalHeight == 0)) {
			oval = new RectF(padding, padding, getWidth() - padding,
					getHeight() - padding);
		}
		canvas.drawArc(oval, 0.0F, 360.0F, false, mCirclePaint);
		float startAngel = 0;
		float sweepAngel = 0;
		Object tag = getTag();
		if (tag == null || TextUtils.isEmpty(tag.toString())) {
			startAngel = mProgress / 100.0F * 360.0F;
			sweepAngel = 45.0F;
		} else {
			if (tag.toString().equals(mContext.getString(R.string.loading_pre))) {
				startAngel = -mProgress / 100.0F * 180.0F;
				sweepAngel = mProgress / 100.0F * 360.0F;
			} else if (tag.toString().equals(
					mContext.getString(R.string.loading_more))) {
				startAngel = -this.mProgress / 100.0F * 180.0F + 180.0F;
				sweepAngel = this.mProgress / 100.0F * 360.0F;
			}
		}
		canvas.drawArc(oval, startAngel, sweepAngel, false, mProgressPaint);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		calcTextProperty();
	}

	private void calcTextProperty() {
		calcTextFontSize();
		calcTextCenterY();
	}

	private void calcTextFontSize() {
		if (TextUtils.isEmpty(mText)) {
			return;
		}
		mTextPaint.setTextSize(25.0F * mDensity);
		float availableSpace = getWidth() - mCirclePaint.getStrokeWidth()
				* 4.0F;
		while (mTextPaint.measureText(mText) > availableSpace) {
			float textSize = mTextPaint.getTextSize();
			mTextPaint.setTextSize(--textSize);
			if (textSize <= 5.0F * mDensity) {
				mTextPaint.setTextSize(5.0F * mDensity);
				return;
			}
		}
	}

	private void calcTextCenterY() {
		if (TextUtils.isEmpty(mText)) {
			return;
		}
		Rect bound = new Rect();
		mTextPaint.getTextBounds(mText, 0, mText.length(), bound);
		mTextCenterY = (getHeight() / 2.0F + bound.height() / 3.0F);
	}

	public void setProgress(int progress) {
		if (progress == mProgress)
			return;
		if ((progress < 0) || (progress > 100)) {
			throw new IllegalArgumentException(
					"progress should be between 0 and 100");
		}
		mProgress = progress;
		invalidate();
	}

	public void start() {
		start(1000L);
	}

	public void start(long duration) {
		mInAnimation = true;
		mAnimationDuration = (duration / 100L);
		postInvalidateDelayed(mAnimationDuration);
	}

	public void stop() {
		mInAnimation = false;
		mAnimationDuration = 0L;
		invalidate();
	}

}
