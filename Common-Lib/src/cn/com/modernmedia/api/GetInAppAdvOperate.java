package cn.com.modernmedia.api;

import org.json.JSONObject;

import cn.com.modernmedia.model.InAppAdv;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;

public class GetInAppAdvOperate extends BaseOperate {
	private InAppAdv inAppAdv;

	public GetInAppAdvOperate() {
		inAppAdv = new InAppAdv();
	}

	public InAppAdv getInAppAdv() {
		return inAppAdv;
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getWeeklyInApp();
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		inAppAdv.setStart_date(jsonObject.optString("start_date", ""));
		inAppAdv.setEnd_date(jsonObject.optString("end_date", ""));
		inAppAdv.setFile(jsonObject.optString("file", ""));
		inAppAdv.setHtml_file(jsonObject.optString("html_file", ""));
		inAppAdv.setHtml_duration(jsonObject.optInt("html_duration", 0));
	}

	@Override
	protected void saveData(String data) {
		FileManager.saveApiData(ConstData.getWeeklyInApp(), data);
	}

	@Override
	protected String getDefaultFileName() {
		return ConstData.getWeeklyInApp();
	}

}
