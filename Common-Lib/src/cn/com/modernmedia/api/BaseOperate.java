package cn.com.modernmedia.api;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.listener.FetchDataListener;

/**
 * 封装服务器返回数据父类
 * 
 * @author ZhuQiao
 * 
 */
public abstract class BaseOperate extends SlateBaseOperate {
	/**
	 * 缓存文件是否为数据库
	 */
	protected boolean cacheIsDb = false;

	@Override
	protected String getLocalData(String name) {
		if (TextUtils.isEmpty(name) || !FileManager.containFile(name))
			return null;
		String data = FileManager.getApiData(name);
		if (TextUtils.isEmpty(data))
			return null;
		try {
			new JSONObject(data);
		} catch (JSONException e) {
			e.printStackTrace();
			PrintHelper.print(name + "文件被异常修改，无法封装成json数据！！");
			FileManager.deleteFile(name);
			return null;
		}
		return data;
	}

	@Override
	protected void fetchLocalDataInBadNet(FetchDataListener mFetchDataListener) {
		if (cacheIsDb) {
			if (!fecthLocalData(null))
				mFetchDataListener.fetchData(false, null, false);
		} else {
			String localData = getLocalData(getDefaultFileName());
			if (!TextUtils.isEmpty(localData)) {
				mFetchDataListener.fetchData(true, localData, false);
				PrintHelper.print("from sdcard by bad net:" + getUrl());
			} else {
				PrintHelper.print("net error:" + getUrl());
				mFetchDataListener.fetchData(false, null, false);
			}
		}
		super.fetchLocalDataInBadNet(mFetchDataListener);
	}

	/**
	 * 判断是否为广告（过滤老版本广告）
	 * 
	 * @param object
	 * @return
	 */
	protected boolean isAdv(JSONObject obj) {
		if (isNull(obj))
			return false;
		JSONObject property = obj.optJSONObject("property");
		if (isNull(property))
			return false;
		return property.optInt("isadv", 0) == 1;
	}

}
