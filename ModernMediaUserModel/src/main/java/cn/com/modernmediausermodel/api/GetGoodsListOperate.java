package cn.com.modernmediausermodel.api;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.modernmediausermodel.model.Goods;
import cn.com.modernmediausermodel.model.Goods.GoodsItem;
import cn.com.modernmediausermodel.util.UserConstData;
import cn.com.modernmediausermodel.util.UserFileManager;

/**
 * 应用商品列表operate
 * 
 * @author JianCong
 * 
 */
public class GetGoodsListOperate extends UserBaseOperate {
	private Goods goods; // 商品list

	public GetGoodsListOperate() {
		goods = new Goods();
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getAppGoods();
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		JSONArray array = jsonObject.optJSONArray("goods");
		if (!isNull(array)) {
			int len = array.length();
			for (int i = 0; i < len; i++) {
				JSONObject object = array.optJSONObject(i);
				if (!isNull(object)) {
					GoodsItem item = new GoodsItem();
					item.setAppId(object.optInt("appid", 0));
					item.setId(object.optString("id", ""));
					item.setName(object.optString("name", ""));
					item.setCategeryname(object.optString("categeryname", ""));
					item.setDesc(object.optString("desc", ""));
					item.setPrice(object.optInt("price", 0));
					goods.getList().add(item);
				}
			}
		}
	}

	@Override
	protected void saveData(String data) {
		UserFileManager.saveApiData(UserConstData.getGoodsListFileName(), data);
	}

	@Override
	protected String getDefaultFileName() {
		return UserConstData.getGoodsListFileName();
	}

	protected Goods getGoodsList() {
		return goods;
	}
}
