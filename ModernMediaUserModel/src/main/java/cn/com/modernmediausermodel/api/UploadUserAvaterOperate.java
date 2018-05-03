package cn.com.modernmediausermodel.api;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediausermodel.model.UploadAvatarResult;

/**
 * 上传头像
 *
 * @author ZhuQiao
 *
 */
public class UploadUserAvaterOperate extends SlateBaseOperate {

	private String imagePath; // 头像在本地的路径
	private UploadAvatarResult result; // 上传结果

	protected UploadUserAvaterOperate(String imagePath) {
		this.imagePath = imagePath;
		result = new UploadAvatarResult();
	}

	protected UploadAvatarResult getUploadResult() {
		return result;
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getUploadAvatarUrl();
	}

	@Override
	protected List<String> getPostImagePath() {
		List<String> list = new ArrayList<String>();
		list.add(imagePath);
		return list;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		if (jsonObject != null) {
			result.setStatus(jsonObject.optString("status", ""));
			result.setMsg(jsonObject.optString("msg", ""));
			result.setImagePath(jsonObject.optString("url", ""));
			result.setAvatarPath(jsonObject.optString("avatar", ""));
		}
	}

	@Override
	protected void saveData(String data) {

	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}

}
