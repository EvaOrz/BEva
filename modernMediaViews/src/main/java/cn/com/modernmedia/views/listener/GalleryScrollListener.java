package cn.com.modernmedia.views.listener;

/**
 * gallery滑动接口
 * 
 * @author ZhuQiao
 * 
 */
public interface GalleryScrollListener {
	/**
	 * 滑动中
	 * 
	 * @param currentX
	 *            当前滑动的距离
	 * @param position
	 *            child索引
	 */
	public void scrolling(int currentX, int position);

	/**
	 * 滑动结束
	 * 
	 * @param currentIndex
	 */
	public void scrollEnd(int currentIndex);

	public void destoryItem(int position);
}
