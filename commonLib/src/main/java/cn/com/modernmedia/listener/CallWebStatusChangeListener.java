package cn.com.modernmedia.listener;

/**
 * 改变webview的字体或者行距
 * 
 * @author ZhuQiao
 * 
 */
public interface CallWebStatusChangeListener {
	public void changeFontSize();

	public void changeLineHeight();

	// 检查付费状态改变
	public void checkPayStatus();
}
