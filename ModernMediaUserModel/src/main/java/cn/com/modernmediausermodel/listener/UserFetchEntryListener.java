package cn.com.modernmediausermodel.listener;

import cn.com.modernmediaslate.model.Entry;

/**
 * 所有自定义view必须实现此接口,调用接口成功后实现此接口
 * 
 * @author ZhuQiao
 * 
 */
public interface UserFetchEntryListener {
	/**
	 * 给View传递数据
	 * 
	 * @param entry
	 */
	public void setData(Entry entry);
}
