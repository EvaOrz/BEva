package cn.com.modernmediausermodel.api;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.modernmediausermodel.model.ActionRuleList;
import cn.com.modernmediausermodel.model.ActionRuleList.ActionRuleItem;
import cn.com.modernmediausermodel.util.UserConstData;
import cn.com.modernmediausermodel.util.UserFileManager;

/**
 * 应用规则接口operate
 * 
 * @author JianCong
 * 
 */
public class GetAppActionRulesOperate extends UserBaseOperate {
	private ActionRuleList actionRuleList;

	public GetAppActionRulesOperate() {
		actionRuleList = new ActionRuleList();
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getAppActionRule();
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		JSONArray array = jsonObject.optJSONArray("actionrule");
		if (!isNull(array)) {
			int len = array.length();
			for (int i = 0; i < len; i++) {
				JSONObject object = array.optJSONObject(i);
				if (!isNull(object)) {
					ActionRuleItem item = new ActionRuleItem();
					item.setAppId(object.optInt("appid", 0));
					item.setId(object.optString("id", ""));
					item.setName(object.optString("name", ""));
					item.setTitle(object.optString("title", ""));
					item.setDesc(object.optString("desc", ""));
					item.setCent(object.optInt("cent", 0));
					item.setPoptype(object.optInt("poptype", 0));
					item.setPopdisplay(object.optInt("popdisplay", 0));
					actionRuleList.getList().add(item);
				}
			}
		}
	}

	@Override
	protected void saveData(String data) {
		UserFileManager.saveApiData(UserConstData.getActionRulesFileName(), data);
	}

	@Override
	protected String getDefaultFileName() {
		return UserConstData.getActionRulesFileName();
	}

	protected ActionRuleList getActionRuleList() {
		return actionRuleList;
	}
}
