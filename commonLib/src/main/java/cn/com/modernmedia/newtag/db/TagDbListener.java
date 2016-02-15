package cn.com.modernmedia.newtag.db;

import cn.com.modernmediaslate.model.Entry;

public interface TagDbListener {
	/**
	 * 添加数据
	 * 
	 * @param entry
	 */
	public void addEntry(Entry entry);

}
