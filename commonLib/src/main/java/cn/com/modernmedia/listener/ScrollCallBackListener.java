package cn.com.modernmedia.listener;

/**
 * 首页scrollview是否显示的index
 * 
 * @author ZhuQiao
 * 
 */
public interface ScrollCallBackListener {
	/**
	 * 显示哪个页面
	 * 
	 * @param index
	 *            0.index;1.left;2.right
	 */
	public void showIndex(int index);
}
