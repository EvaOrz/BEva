package cn.com.modernmedia.listener;

/**
 * webview���Ƿ���ʾprocess
 * 
 * @author ZhuQiao
 * 
 */
public interface WebProcessListener {
	/**
	 * process��ʾ״̬
	 * 
	 * @param style
	 *            0:����ʾ;1.��ʾloading;2.��ʾerror
	 */
	public void showStyle(int style);
}
