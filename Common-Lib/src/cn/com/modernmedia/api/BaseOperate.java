package cn.com.modernmedia.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.Adv;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.ParseUtil;
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
		// column时间只比较首页，栏目首页数据在判定为不相同时已经删除
		if (fileName.equals(ConstData.getIndexFileName())
				&& !CommonApplication.columnUpdateTimeSame) {
			CommonApplication.columnUpdateTimeSame = true;
		} else if (fileName.equals(ConstData.getArticleListFileName())
				&& !CommonApplication.articleUpdateTimeSame) {
			CommonApplication.articleUpdateTimeSame = true;
		}
		super.reSetUpdateTime();
	}

	/**
	 * 解析广告
	 * 
	 * @param object
	 * @return
	 */
	protected Adv parseAdv(JSONObject obj) {
		Adv adv = new Adv();
		JSONObject property = obj.optJSONObject("property");
		if (isNull(property))
			return adv;
		int isadv = property.optInt("isadv", 0);
		if (isadv != 1)
			return adv;
		adv.getAdvProperty().setIsadv(isadv);
		JSONArray columnadv = obj.optJSONArray("columnadv");
		if (isNull(columnadv))
			return adv;
		JSONObject columnadvObj = columnadv.optJSONObject(0);
		if (isNull(columnadvObj))
			return adv;
		String starttime = columnadvObj.optString("starttime", "0");
		String endtime = columnadvObj.optString("endtime", "0");
		long currentTime = System.currentTimeMillis() / 1000;
		if (currentTime > ParseUtil.stol(starttime)
				&& currentTime < ParseUtil.stol(endtime)) {
			adv.getColumnAdv().setId(columnadvObj.optInt("id", 0));
			adv.getColumnAdv().setLink(columnadvObj.optString("link", ""));
			adv.getColumnAdv().setStartTime(starttime);
			adv.getColumnAdv().setEndTime(endtime);
			JSONArray picture_h = columnadvObj.optJSONArray("picture_h");
			if (isNull(picture_h))
				return adv;
			JSONObject picture_h_json = picture_h.optJSONObject(0);
			if (isNull(picture_h_json))
				return adv;
			adv.getColumnAdv().setUrl(picture_h_json.optString("url", ""));
		} else {
			adv.setExpired(true);
		}
		return adv;
	}

	protected void parseTemplate(Entry entry, JSONObject jsonObject) {
		if (entry != null && !isNull(jsonObject))
			entry.setTemplate(jsonObject.optString("template", ""));
	}

}
