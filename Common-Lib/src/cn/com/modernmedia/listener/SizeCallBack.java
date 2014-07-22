package cn.com.modernmedia.listener;

/**
 * 首页加载时得到控件的大小
 * 
 * @author ZhuQiao
 * 
 */
public interface SizeCallBack {
	/**
	 * 在添加视图前计算大小
	 */
	public void onGlobalLayout();

	/**
	 * 指定视图的尺寸.
	 * 
	 * @param idx
	 *            view索引.
	 * @param w
	 *            父视图的宽度.
	 * @param h
	 *            父视图的高度.
	 * @param dims
	 *            dims[0]为View宽度，dims[1]为View高度.
	 */
	public void getViewSize(int idx, int width, int height, int[] dims);
}
