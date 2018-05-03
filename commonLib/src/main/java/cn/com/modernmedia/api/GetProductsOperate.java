package cn.com.modernmedia.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.modernmedia.model.ProductList;
import cn.com.modernmedia.model.ProductList.Product;

/**
 * 获取支付列表api
 * 
 * @author lusiyuan
 *
 */
public class GetProductsOperate extends BaseOperate {
	private ProductList pros = new ProductList();

	public GetProductsOperate() {

	}

	public ProductList getPros() {
		return pros;
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getProducts();
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		try {
			JSONArray array = jsonObject.getJSONArray("product");
			for (int i = 0; i < array.length(); i++) {
				JSONObject j = array.getJSONObject(i);
				Product pro = new Product();
				pro.setName(j.optString("name"));
				pro.setNum(j.optString("num"));
				pro.setPid(j.optString("pid"));
				pro.setPrice(j.optString("price"));
				pro.setType(j.optString("type"));
				pro.setUnit(j.optString("unit"));
				pros.getPros().add(pro);
			}
		} catch (JSONException e) {
			e.printStackTrace();
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
