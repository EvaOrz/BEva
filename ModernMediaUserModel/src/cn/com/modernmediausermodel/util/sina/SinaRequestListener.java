package cn.com.modernmediausermodel.util.sina;

import cn.com.modernmediaslate.model.Entry;

/**
 * SinaAPI 请求数据的接口类
 * 
 * @author jiancong
 * 
 */
public interface SinaRequestListener {
	/**
	 * 数据请求成功
	 * 
	 * @param entry
	 */
	public void onSuccess(Entry entry);

	/**
	 * 数据请求失败
	 * 
	 * @param entry
	 */
	public void onFailed(String error);
}
