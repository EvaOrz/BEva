package cn.com.modernmedia.listener;

/**
 * listview滑动状态,在滑动停止时渲染文字、图片等元素
 * 
 * @author ZhuQiao
 * 
 */
public interface ListScrollStateListener {

	/**
	 * 滑动中
	 */
	public void scrolling();

	/**
	 * 滑动停止
	 */
	public void scrollIdle();
}
