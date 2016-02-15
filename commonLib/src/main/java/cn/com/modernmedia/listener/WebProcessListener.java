package cn.com.modernmedia.listener;

/**
 * webview中是否显示process
 * 
 * @author ZhuQiao
 * 
 */
public interface WebProcessListener {
	/**
	 * process显示状态
	 * 
	 * @param style
	 *            0:不显示;1.显示loading;2.显示error
	 */
	public void showStyle(int style);
}
