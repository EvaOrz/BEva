package cn.com.modernmedia.api;

import org.json.JSONObject;

/**
 * 获取往期子栏目
 * 
 * @author jiancong
 * 
 */
public class GetLastIssueCatsOperate extends GetTagInfoOperate {

	public GetLastIssueCatsOperate(String tagName) {
		super("", tagName, "", "", TAG_TYPE.TREE_CAT);
		cacheIsDb = false;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		parseTagInfo(jsonObject);
	}

}
