package cn.com.modernmedia.api;

import org.json.JSONObject;

import android.content.Context;
import cn.com.modernmedia.model.Down;

/**
 * 统计渠道装机量
 * 
 * @author ZhuQiao
 * 
 */
public class DownOperate extends BaseOperate {
	private String url = "";
	private Down down;

	protected DownOperate(Context context) {
		url = UrlMaker.download(context);
		down = new Down();
	}

	protected Down getDown() {
		return down;
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		down.setSuccess(jsonObject.optString("result", "false").equals("true"));
	}

	@Override
	protected void saveData(String data) {

	}

	@Override
	protected String getDefaultFileName() {
		return "";
	}

}
