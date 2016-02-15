package cn.com.modernmediausermodel.listener;

import android.view.View;

/**
 * 卡片view接口
 * 
 * @author user
 * 
 */
public interface CardViewListener {
	/**
	 * 是否显示titlebar
	 * 
	 * @param show
	 */
	public void showTitleBar(boolean show);

	/**
	 * 返回view
	 * 
	 * @return
	 */
	public View fetchView();
}
