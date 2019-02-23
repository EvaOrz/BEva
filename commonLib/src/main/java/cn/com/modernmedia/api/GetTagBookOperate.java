package cn.com.modernmedia.api;

import org.json.JSONObject;

import cn.com.modernmedia.model.SubscribeOrderList;

/**
 * 获取订阅栏目列表
 * 
 * @author lusiyuan
 *
 */
public class GetTagBookOperate extends BaseOperate {
	private SubscribeOrderList tags;

	public GetTagBookOperate() {
		tags = new SubscribeOrderList();
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getBookColumns();
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		// if (jsonObject!=null&&!jsonObject.isNull("articletag")) {
		// JSONArray a =jsonObject.getJSONArray("articletag");
		// for (int i = 0; i < a.length(); i++) {
		// tags.add(new TagInfo(a.get(i)));
		// }
		// }
		// tags =
	}

	@Override
	protected void saveData(String data) {

	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}

	public SubscribeOrderList getBookTagList() {
		return tags;
	}

}
