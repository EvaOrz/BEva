package cn.com.modernmediausermodel.api;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.unit.SlatePrintHelper;
import cn.com.modernmediausermodel.util.UserFileManager;

/**
 * 封装社区服务器返回数据父类
 * 
 * @author zhuqiao
 * 
 */
public abstract class UserBaseOperate extends SlateBaseOperate {

	@Override
	protected CallBackData fetchDataFromSD() {
		CallBackData callBackData = new CallBackData();

		String cache = getDataFromSD(getDefaultFileName());
		if (TextUtils.isEmpty(cache)) {
			SlatePrintHelper.print("fetch cache from sd error:" + getUrl());
			callBackData.success = false;
			callBackData.data = null;
		} else {
			SlatePrintHelper.print("fetch cache from sd success:" + getUrl());
			callBackData.success = true;
			callBackData.data = cache;
		}

		return callBackData;
	}

	/**
	 * 获取文件数据
	 * 
	 * @param name
	 * @return
	 */
	private String getDataFromSD(String name) {
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
}
