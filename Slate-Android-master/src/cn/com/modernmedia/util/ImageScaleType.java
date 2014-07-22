package cn.com.modernmedia.util;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageScaleType {
	// public static final int KEY = 1000;
	public static String Type = "";

	/**
	 * ����������ͼƬ��size������ʾ��ʹ��ͼƬ��(��)���ڻ����View�ĳ�(��)
	 */
	public static final String CENTER_CROP = "centerCrop";

	/**
	 * ��ͼƬ����������/��С��View�Ŀ�ȣ�������ʾ
	 */
	public static final String FIT_CENTER = "fitCenter";

	/**
	 * ��ͼƬ��ԭ��size������ʾ����ͼƬ��/����View�ĳ�/�����ȡͼƬ�ľ��в�����ʾ
	 */
	public static final String CENTER = "center";

	/**
	 * ��ͼƬ����������������ʾ��ͨ����������С��ԭ����sizeʹ��ͼƬ��/����ڻ�С��View�ĳ�/��
	 */
	public static final String CENTER_INSIDE = "centerInside";

	/**
	 * ��ͼƬ����������/��С��View�Ŀ�ȣ���ʾ��View���²���λ��
	 */
	public static final String FIT_END = "fitEnd";

	/**
	 * ��ͼƬ����������/��С��View�Ŀ�ȣ���ʾ��View���ϲ���λ��
	 */
	public static final String FIT_START = "fitStart";

	/**
	 * ��ͼƬ������������/��С��View�Ĵ�С��ʾ
	 */
	public static final String FIT_XY = "fitXy";

	/**
	 * �þ���������
	 */
	public static final String MATRIX = "matrix";

	/**
	 * ����ͼƬ��scale type,Ĭ��Ϊfix_xy
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
