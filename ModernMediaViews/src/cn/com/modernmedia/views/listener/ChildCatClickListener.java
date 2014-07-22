package cn.com.modernmedia.views.listener;

import cn.com.modernmedia.model.SoloColumn.SoloColumnChild;

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
	 * @param parentId
	 *            如果是子栏目，为子栏目父id;else -1
	 * @param soloChild
	 *            如果是独立栏目，那么为子独立栏目;else null
	 */
	public void onClick(int position, int parentId, SoloColumnChild soloChild);
}
