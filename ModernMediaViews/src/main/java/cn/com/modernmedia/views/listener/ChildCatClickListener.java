package cn.com.modernmedia.views.listener;

import cn.com.modernmedia.model.TagInfoList.TagInfo;

/**
 * 子栏目导航栏点击回调接口
 * 
 * @author user
 * 
 */
public interface ChildCatClickListener {
	/**
	 * 点击子栏目导航栏
	 * 
	 * @param position
	 *            索引
	 */
	public void onClick(int position, TagInfo info);
}
