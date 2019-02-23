package cn.com.modernmediaslate.unit;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageScaleType {
	// public static final int KEY = 1000;
	public static String Type = "";

	/**
	 * 按比例扩大图片的size居中显示，使得图片长(宽)等于或大于View的长(宽)
	 */
	public static final String CENTER_CROP = "centerCrop";

	/**
	 * 把图片按比例扩大/缩小到View的宽度/高度，居中显示
	 */
	public static final String FIT_CENTER = "fitCenter";

	/**
	 * 按图片的原来size居中显示，当图片长/宽超过View的长/宽，则截取图片的居中部分显示
	 */
	public static final String CENTER = "center";

	/**
	 * 将图片的内容完整居中显示，通过按比例缩小或原来的size使得图片长/宽等于或小于View的长/宽
	 */
	public static final String CENTER_INSIDE = "centerInside";

	/**
	 * 把图片按比例扩大/缩小到View的宽度，显示在View的下部分位置
	 */
	public static final String FIT_END = "fitEnd";

	/**
	 * 把图片按比例扩大/缩小到View的宽度，显示在View的上部分位置
	 */
	public static final String FIT_START = "fitStart";

	/**
	 * 把图片不按比例扩大/缩小到View的大小显示
	 */
	public static final String FIT_XY = "fitXy";

	/**
	 * 用矩阵来绘制
	 */
	public static final String MATRIX = "matrix";

	/**
	 * 占满高，宽等比缩放
	 */
	public static final String FIT_Y = "fitY";

	/**
	 * 占满宽，宽等比缩放
	 */
	public static final String FIT_X = "fitX";

	/**
	 * 设置图片的scale type,默认为fix_xy
	 * 
	 * @param iv
	 * @param scale_type
	 */
	public static void setScaleType(ImageView iv, String scale_type) {
		if (!TextUtils.isEmpty(scale_type)) {
			if (scale_type.equals(CENTER_CROP)) {
				iv.setScaleType(ScaleType.CENTER_CROP);
			} else if (scale_type.equals(FIT_CENTER)) {
				iv.setScaleType(ScaleType.FIT_CENTER);
			} else if (scale_type.equals(CENTER)) {
				iv.setScaleType(ScaleType.CENTER);
			} else if (scale_type.equals(CENTER_INSIDE)) {
				iv.setScaleType(ScaleType.CENTER_INSIDE);
			} else if (scale_type.equals(FIT_END)) {
				iv.setScaleType(ScaleType.FIT_END);
			} else if (scale_type.equals(FIT_START)) {
				iv.setScaleType(ScaleType.FIT_START);
			} else if (scale_type.equals(MATRIX)) {
				iv.setScaleType(ScaleType.MATRIX);
			} else {
				iv.setScaleType(ScaleType.FIT_XY);
			}
		} else {
			iv.setScaleType(ScaleType.FIT_XY);
		}
	}
}
