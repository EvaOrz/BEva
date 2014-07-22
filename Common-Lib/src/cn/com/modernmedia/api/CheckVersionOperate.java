package cn.com.modernmedia.api;

import org.json.JSONObject;

import cn.com.modernmedia.model.Version;

/**
 * 判断版本
 * 
 * @author ZhuQiao
 * 
 */
public class CheckVersionOperate extends BaseOperate {
	private String url = "";
	private Version version;

	protected CheckVersionOperate() {
		url = UrlMaker.checkVersion();
		version = new Version();
		setShowToast(false);
	}

	protected Version getVersion() {
		return version;
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		version.setVersion(jsonObject.optInt("version", -1));
		version.setSrc(jsonObject.optString("src", ""));
		version.setChangelog(jsonObject.optString("changelog", ""));
		version.setDownload_url(jsonObject.optString("download_url", ""));
	}

	@Override
	protected void saveData(String data) {
	}

	@Override
	protected String getDefaultFileName() {
		return "";
	}

}
