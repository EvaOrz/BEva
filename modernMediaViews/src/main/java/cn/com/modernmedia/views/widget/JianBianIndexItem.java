package cn.com.modernmedia.views.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

/**
 * 首页item 渐变色背景view
 * 
 * @author lusiyuan
 *
 */
public class JianBianIndexItem extends View {
	private Path mPath;
	private Paint mPaint;
	private Rect mRect;
	private GradientDrawable mDrawable;

	public JianBianIndexItem(Context context) {
		super(context);
		setFocusable(true);

		mPath = new Path();
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mRect = new Rect(0, 0, 120, 120);

		mDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
				new int[] { 0xFFFF0000, 0xFF00FF00, 0xFF0000FF });
		mDrawable.setShape(GradientDrawable.RECTANGLE);
		mDrawable.setGradientRadius((float) (Math.sqrt(2) * 60));
	}

	static void setCornerRadii(GradientDrawable drawable, float r0, float r1,
			float r2, float r3) {
		drawable.setCornerRadii(new float[] { r0, r0, r1, r1, r2, r2, r3, r3 });
	}

	@Override
	protected void onDraw(Canvas canvas) {

		mDrawable.setBounds(mRect);

		float r = 16;

		canvas.save();
		canvas.translate(10, 10);
		mDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		setCornerRadii(mDrawable, r, r, 0, 0);
		mDrawable.draw(canvas);
		canvas.restore();

//		canvas.save();
//		canvas.translate(10 + mRect.width() + 10, 10);
//		mDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
//		setCornerRadii(mDrawable, 0, 0, r, r);
//		mDrawable.draw(canvas);
//		canvas.restore();
//
//		canvas.translate(0, mRect.height() + 10);
//
//		canvas.save();
//		canvas.translate(10, 10);
//		mDrawable.setGradientType(GradientDrawable.SWEEP_GRADIENT);
//		setCornerRadii(mDrawable, 0, r, r, 0);
//		mDrawable.draw(canvas);
//		canvas.restore();
//
//		canvas.save();
//		canvas.translate(10 + mRect.width() + 10, 10);
//		mDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
//		setCornerRadii(mDrawable, r, 0, 0, r);
//		mDrawable.draw(canvas);
//		canvas.restore();
//
//		canvas.translate(0, mRect.height() + 10);
//
//		canvas.save();
//		canvas.translate(10, 10);
//		mDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
//		setCornerRadii(mDrawable, r, 0, r, 0);
//		mDrawable.draw(canvas);
//		canvas.restore();
//
//		canvas.save();
//		canvas.translate(10 + mRect.width() + 10, 10);
//		mDrawable.setGradientType(GradientDrawable.SWEEP_GRADIENT);
//		setCornerRadii(mDrawable, 0, r, 0, r);
//		mDrawable.draw(canvas);
//		canvas.restore();

	}
}
