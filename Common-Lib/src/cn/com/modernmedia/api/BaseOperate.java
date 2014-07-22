package cn.com.modernmedia.api;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.model.Entry;

/**
 * 封装服务器返回数据父类
 * 
 * @author ZhuQiao
 * 
 */
public abstract class BaseOperate extends SlateBaseOperate {

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
		String localData = getLocalData(getDefaultFileName());
		if (!TextUtils.isEmpty(localData)) {
			mFetchDataListener.fetchData(true, localData);
			PrintHelper.print("from sdcard by bad net:" + getUrl());
		} else {
			PrintHelper.print("net error:" + getUrl());
			mFetchDataListener.fetchData(false, null);
		}
		super.fetchLocalDataInBadNet(mFetchDataListener);
	}

	@Override
	protected void reSetUpdateTime() {
		String fileName = getDefaultFileName();
		if (TextUtils.isEmpty(fileName))
			return;
		if ((fileName.equals(ConstData.getIndexFileName()) || fileName
				.contains(ConstData.getCatIndexFileName("")))
				&& !CommonApplication.columnUpdateTimeSame) {
			CommonApplication.columnUpdateTimeSame = true;
		} else if (fileName.equals(ConstData.getArticleListFileName())
				&& !CommonApplication.articleUpdateTimeSame) {
			CommonApplication.articleUpdateTimeSame = true;
		}
		super.reSetUpdateTime();
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

	protected void parseTemplate(Entry entry, JSONObject jsonObject) {
		if (entry != null && !isNull(jsonObject))
			entry.setTemplate(jsonObject.optString("template", ""));
	}

}
