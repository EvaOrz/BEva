package cn.com.modernmedia.api;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Color;
import android.text.TextUtils;
import cn.com.modernmedia.model.SoloColumn;
import cn.com.modernmedia.model.SoloColumn.SoloColumnChild;
import cn.com.modernmedia.model.SoloColumn.SoloColumnItem;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;

/**
 * 获取独立栏目列表(因为独立于期，没有字段表明是否更改，所以除非没网，都从服务器上取最新的)
 * 
 * @author ZhuQiao
 * 
 */
public class GetSoloColumnOperate extends BaseOperate {
	private SoloColumn column;
	private String url;

	protected GetSoloColumnOperate() {
		column = new SoloColumn();
		url = UrlMaker.getSoloCatList();
	}

	protected SoloColumn getColumn() {
		return column;
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		JSONArray array = jsonObject.optJSONArray("column");
		if (isNull(array)) {
			return;
		}
		JSONObject obj;
		for (int i = 0; i < array.length(); i++) {
			obj = array.optJSONObject(i);
			if (isNull(obj))
				continue;
			parseCat(obj);
		}
	}

	private void parseCat(JSONObject jsonObject) {
		SoloColumnItem item = new SoloColumnItem();
		item.setId(jsonObject.optInt("id", -1));
		item.setName(jsonObject.optString("name", ""));
		item.setEname(jsonObject.optString("ename", ""));
		item.setCname(jsonObject.optString("cname", ""));
		item.setColor(transformColor(jsonObject.optString("color", "")));
		item.setDisplayType(jsonObject.optInt("displayType", -1));
		JSONArray tagArr = jsonObject.optJSONArray("taglist");
		if (!isNull(tagArr)) {
			JSONObject tagObj;
			int length = tagArr.length();
			SoloColumnChild child;
			for (int i = 0; i < length; i++) {
				tagObj = tagArr.optJSONObject(i);
				if (isNull(tagObj))
					continue;
				child = new SoloColumnChild();
				child.setName(tagObj.optString("name", ""));
				child.setColor(tagObj.optString("color", ""));
				child.setType(tagObj.optString("type", "self"));
				item.getList().add(child);
			}
		}
		column.getList().add(item);
	}

	/**
	 * 把服务器返回的RGB转换成可用的色值
	 * 
	 * @param result
	 *            eg:235,3,13
	 * @return
	 */
	private int transformColor(String result) {
		int red = 0, green = 0, blue = 0;
		if (!TextUtils.isEmpty(result)) {
			String colorArr[] = result.split(",");
			if (colorArr.length == 3) {
				try {
					red = Integer.parseInt(colorArr[0]);
					green = Integer.parseInt(colorArr[1]);
					blue = Integer.parseInt(colorArr[2]);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				return Color.rgb(red, green, blue);
			}
		}
		return 0;
	}

	@Override
	protected void saveData(String data) {
		String fileName = ConstData.getSoloCatListFileName();
		FileManager.saveApiData(fileName, data);
	}

	@Override
	protected String getDefaultFileName() {
		return ConstData.getSoloCatListFileName();
	}

}
