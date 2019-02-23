package cn.com.modernmedia.api;

import org.json.JSONObject;

import android.text.TextUtils;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;

/**
 * 获取往期列表
 * 
 * @author jiancong
 * 
 */
public class GetLastIssueListOperate extends GetTagInfoOperate {

	public GetLastIssueListOperate(String top) {
		super("", AppValue.appInfo.getTagName(), "2", top, TAG_TYPE.ISSUE_LIST);
		cacheIsDb = false;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		parseTagInfo(jsonObject);
	}

	@Override
	protected void saveData(String data) {
		if (TextUtils.isEmpty(top)) // 只缓存第一页
			FileManager.saveApiData(getDefaultFileName(), data);
	}

	@Override
	protected String getDefaultFileName() {
		return ConstData.getLastIssueListFileName(tagName);
	}
}
