package cn.com.modernmedia.widget.zoom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import cn.com.modernmedia.R;

/**
 * 自定义scale type的imageview
 * 
 * @CROP 等比缩放图片,使图片的两维等于或者大于相应的视图的维度(至少有一个等于,一个大于或者等于)
 * @INSIDE 等比缩放图片,使图片的两维（宽度和高度）等于或者小于相应的视图的维度(至少有一个等于,一个小于或者等于)
 * 
 * @author user
 *
 */
public class CSTImageView extends ImageView {
	protected Context mContext;
	private int mScaleType;

	protected Matrix mMatrix = new Matrix();

	/**
	 * imageview宽高
	 */
	protected int mWidth, mHeight;
	/**
	 * bitmap宽高
	 */
	protected int bmWidth, bmHeight;

	/**
	 * bitmap整体显示在imageview的区域(默认显示bitmap中间)
	 * 
	 * @author user
	 *
	 */
	public enum CustomScaleType {
		/**
		 * 显示bitmap左侧
		 */
		LEFT(0x0001),
		/**
		 * 显示bitmap上面
		 */
		TOP(0x0002),
		/**
		 * 显示bitmap右侧
		 */
		RIGHT(0x0004),
		/**
		 * 显示bitmap下面
		 */
		BOTTOM(0x0008),
		/**
		 * crop
		 */
		CROP(0x0040),
		/**
		 * inside
		 */
		INSIDE(0x0080);

		CustomScaleType(int ni) {
			nativeInt = ni;
		}

		final int nativeInt;

		public int getNativeInt() {
			return nativeInt;
		}

	}

	public CSTImageView(Context context) {
		this(context, null);
	}

	public CSTImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		setScaleType(ScaleType.MATRIX);

		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs,
					R.styleable.CSTImageView, 0, 0);
			mScaleType = a.getInt(R.styleable.CSTImageView_scale_type, 0);
			a.recycle();
		}

		initBmSize();
	}

	@Override
	public void requestLayout() {
		super.requestLayout();
		initBmSize();
	}

	private void initBmSize() {
		Drawable drawable = getDrawable();
		if (drawable != null) {
			bmWidth = drawable.getIntrinsicWidth();
			bmHeight = drawable.getIntrinsicHeight();
		}
	}

	/**
	 * @param scaleType
	 */
	public void setCusScaleType(int scaleType) {
		if (mScaleType != scaleType) {
			mScaleType = scaleType;
			requestLayout();
			invalidate();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (bmWidth == 0 || bmHeight == 0) {
			return;
		}
		mWidth = MeasureSpec.getSize(widthMeasureSpec);
		mHeight = MeasureSpec.getSize(heightMeasureSpec);

		// 缩放后是否宽度==view宽度
		boolean fitX;
		float scale = 1f;
		if (includeType(CustomScaleType.CROP)) {
			fitX = bmWidth * mHeight <= mWidth * bmHeight;
			scale = calcCropScale(fitX);
		} else if (includeType(CustomScaleType.INSIDE)) {
			fitX = bmWidth * mHeight >= mWidth * bmHeight;
			scale = calcInsideScale(fitX);
		} else {
			onFitXY();
			return;
		}

		if (fitX) {
			onFitX(scale);
		} else {
			onFitY(scale);
		}
	}

	/**
	 * 计算crop缩放比例
	 * 
	 * @return 获取缩放比例
	 */
	private float calcCropScale(boolean fitX) {
		return fitX ? (float) mWidth / (float) bmWidth : (float) mHeight
				/ (float) bmHeight;
	}

	/**
	 * 计算inside缩放比例
	 * 
	 * @return 获取缩放比例
	 */
	private float calcInsideScale(boolean fitX) {
		return fitX ? (float) mWidth / (float) bmWidth : (float) mHeight
				/ (float) bmHeight;
	}

	/**
	 * 默认
	 */
	private void onFitXY() {
		float scaleX = (float) mWidth / (float) bmWidth;
		float scaleY = (float) mHeight / (float) bmHeight;
		mMatrix.setScale(scaleX, scaleY);
		setImageMatrix(mMatrix);
	}

	/**
	 * 宽度正好占满view
	 */
	private void onFitX(float scale) {
		float dx = 0f, dy = 0f;// 偏移
		if (includeType(CustomScaleType.TOP)) {
		} else if (includeType(CustomScaleType.BOTTOM)) {
			dy = mHeight - bmHeight * scale;
		} else {
			dy = (mHeight - bmHeight * scale) * 0.5f;
		}
		adjustMatrix(scale, dx, dy);
	}

	/**
	 * 高度正好占满view
	 */
	private void onFitY(float scale) {
		float dx = 0f, dy = 0f;// 偏移
		if (includeType(CustomScaleType.LEFT)) {
		} else if (includeType(CustomScaleType.RIGHT)) {
			dx = mWidth - bmWidth * scale;
		} else {
			dx = (mWidth - bmWidth * scale) * 0.5f;
		}
		adjustMatrix(scale, dx, dy);
	}

	/**
	 * 是否包含属性
	 * 
	 * @param type
	 * @return
	 */
	private boolean includeType(CustomScaleType type) {
		return (mScaleType & type.nativeInt) > 0;
	}

	/**
	 * 调整matrix
	 * 
	 * @param dx
	 * @param dy
	 */
	private void adjustMatrix(float scale, float dx, float dy) {
		mMatrix.setScale(scale, scale);
		mMatrix.postTranslate(dx, dy);
		setImageMatrix(mMatrix);
	}

	/**
	 * @pre 在队头插入一个方法
	 * @post 在队尾插入一个方法
	 * @set 把当前队列清空,并且总是位于队列的最中间位置
	 */

	/**
	 * 只有m.setTranslate(80, 80)有效,因为m.setRotate(45);被清除.
	 */
	@SuppressWarnings("unused")
	private void eg1() {
		Matrix m = new Matrix();
		m.setRotate(45);
		m.setTranslate(80, 80);
	}

	/**
	 * 先执行m.setTranslate(80, 80);后执行m.postRotate(45);
	 */
	@SuppressWarnings("unused")
	private void eg2() {
		Matrix m = new Matrix();
		m.setTranslate(80, 80);
		m.postRotate(45);
	}

	/**
	 * 先执行m.setTranslate(80, 80);后执行m.preRotate(45);
	 */
	@SuppressWarnings("unused")
	private void eg3() {
		Matrix m = new Matrix();
		m.setTranslate(80, 80);
		m.preRotate(45);
	}

	/**
	 * 执行顺序:m.preTranslate(50f, 20f)-->m.preScale(2f,2f)-->m.postScale(0.2f,
	 * 0.5f)-->m.postTranslate(20f, 20f)
	 * 
	 * @attention m.preTranslate(50f, 20f)比m.preScale(2f,2f)先执行,因为它查到了队列的最前端.
	 */
	@SuppressWarnings("unused")
	private void eg4() {
		Matrix m = new Matrix();
		m.preScale(2f, 2f);
		m.preTranslate(50f, 20f);
		m.postScale(0.2f, 0.5f);
		m.postTranslate(20f, 20f);
	}

	/**
	 * 执行顺序:m.preTranslate(0.5f, 0.5f)-->m.setScale(0.8f,
	 * 0.8f)-->m.postScale(3f, 3f)
	 * 
	 * @attention m.setScale(0.8f, 0.8f)清除了前面的m.postTranslate(20,
	 *            20)和m.preScale(0.2f, 0.5f);
	 */
	@SuppressWarnings("unused")
	private void eg5() {
		Matrix m = new Matrix();
		m.postTranslate(20, 20);
		m.preScale(0.2f, 0.5f);
		m.setScale(0.8f, 0.8f);
		m.postScale(3f, 3f);
		m.preTranslate(0.5f, 0.5f);
	}
}
