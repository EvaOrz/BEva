package cn.com.modernmediausermodel.api;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.unit.SlatePrintHelper;
import cn.com.modernmediausermodel.util.UserFileManager;

/**
 * 封装社区服务器返回数据父类
 * 
 * @author zhuqiao
 * 
 */
public abstract class UserBaseOperate extends SlateBaseOperate {

	/**
	 * 缓存文件是否为数据库
	 */
	protected boolean cacheIsDb = false;

	@Override
	protected String getLocalData(String name) {
		if (TextUtils.isEmpty(name) || !UserFileManager.containFile(name))
			return null;
		String data = UserFileManager.getApiData(name);
		if (TextUtils.isEmpty(data))
			return null;
		try {
			new JSONObject(data);
		} catch (JSONException e) {
			e.printStackTrace();
			SlatePrintHelper.print(name + "文件被异常修改，无法封装成json数据！！");
			UserFileManager.deleteFile(name);
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
				SlatePrintHelper.print("from sdcard by bad net:" + getUrl());
			} else {
				SlatePrintHelper.print("net error:" + getUrl());
				mFetchDataListener.fetchData(false, null, false);
			}
		}
		super.fetchLocalDataInBadNet(mFetchDataListener);
	}

}
