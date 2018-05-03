package cn.com.modernmedia.api;


public class GetInAppAdvOperate {
	// extends BaseOperate {
	// private InAppAdv inAppAdv;
	//
	// public GetInAppAdvOperate() {
	// inAppAdv = new InAppAdv();
	// }
	//
	// public InAppAdv getInAppAdv() {
	// return inAppAdv;
	// }
	//
	// @Override
	// protected String getUrl() {
	// return UrlMaker.getWeeklyInApp();
	// }
	//
	// @Override
	// protected void handler(JSONObject jsonObject) {
	// inAppAdv.setStart_date(jsonObject.optString("start_date", ""));
	// inAppAdv.setEnd_date(jsonObject.optString("end_date", ""));
	// inAppAdv.setFile(jsonObject.optString("file", ""));
	// inAppAdv.setHtml_file(jsonObject.optString("html_file", ""));
	// inAppAdv.setHtml_duration(jsonObject.optInt("html_duration", 0));
	// JSONObject splash_ken_burns = jsonObject
	// .optJSONObject("splash_ken_burns");
	// if (!isNull(splash_ken_burns)) {
	// inAppAdv.setSplashFile(splash_ken_burns.optString("file"));
	// JSONArray splashImageArr = splash_ken_burns.optJSONArray("images");
	// if (!isNull(splashImageArr)) {
	// parseSpalshImgs(splashImageArr);
	// }
	// }
	// }
	//
	// private void parseSpalshImgs(JSONArray arr) {
	// int length = arr.length();
	// for (int i = 0; i < length; i++) {
	// inAppAdv.getSplashFileNameList().add(arr.optString(i));
	// }
	// }
	//
	// @Override
	// protected void saveData(String data) {
	// FileManager.saveApiData(ConstData.getWeeklyInApp(), data);
	// }
	//
	// @Override
	// protected String getDefaultFileName() {
	// return ConstData.getWeeklyInApp();
	// }

}
