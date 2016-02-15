package cn.com.modernmedia.views.listener;

/**
 * 刷新、加载完成
 * 
 * @author zhuqiao
 * 
 */
public interface LoadCallBack {
	public void onRefreshed(boolean success);

	public void onLoaded(boolean success);
}
