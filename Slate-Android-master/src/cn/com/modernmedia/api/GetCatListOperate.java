package cn.com.modernmedia.api;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Color;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.Cat;
import cn.com.modernmedia.model.Cat.CatItem;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.FileManager;

/**
 * 获取本期栏目列表，用于左侧菜单的栏目列表，每期栏目可能会不同
 * 
 * @author ZhuQiao
 * 
 */
public class GetCatListOperate extends BaseOperate {
	private String url = "";
	private Cat cat;

	/**
	 * 
	 * @param issueId
	 *            期id
	 */
	protected GetCatListOperate(int issueId) {
		cat = new Cat();
		url = UrlMaker.getCatList(issueId + "");
	}

	protected Cat getCat() {
		return cat;
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		JSONArray array = jsonObject.optJSONArray("column");
		if (isNull(array))
			return;
		int length = array.length();
		CatItem item;
		JSONObject obj;
		DataHelper.clear();
		for (int i = 0; i < length; i++) {
			obj = array.optJSONObject(i);
			if (isNull(obj))
				continue;
			item = new CatItem();
			item.setId(obj.optInt("id", -1));
			item.setName(obj.optString("name", ""));
			item.setEname(obj.optString("ename", ""));
			item.setCname(obj.optString("cname", ""));
			item.setColor(transformColor(obj.optString("color", "")));
			item.setDisplayType(obj.optInt("displayType", -1));
			item.setHaveChildren(obj.optInt("haveChildren", 0));
			item.setTagname(obj.optString("tagname", ""));
			int parentId = obj.optInt("parentId", -1);
			item.setParentId(parentId);
			if (item.getDisplayType() == 1 || item.getDisplayType() == 3) {
				cat.getList().add(item);
			}
			if (parentId != -1) {
				if (!DataHelper.childMap.containsKey(parentId)) {
					DataHelper.childMap.put(parentId, new ArrayList<CatItem>());
				}
				DataHelper.childMap.get(parentId).add(item);
			}
			DataHelper.columnTitleMap.put(item.getId(), item.getCname());
			if (item.getColor() != 0)
				DataHelper.columnColorMap.put(item.getId(), item.getColor());
			JSONArray pictureArr = obj.optJSONArray("picture");
			if (!isNull(pictureArr))
				DataHelper.columnRowMap.put(item.getId(),
						parsePicture(pictureArr));
		}
	}

	/**
	 * 解析column对应的箭头
	 * 
	 * @param array
	 * @return
	 */
	private String parsePicture(JSONArray array) {
		String url = "";
		JSONObject object;
		for (int i = 0; i < array.length(); i++) {
			object = array.optJSONObject(i);
			if (isNull(object))
				continue;
			url = object.optString("url", "");
			break;
		}
		return url;
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
		String fileName = ConstData.getCatListFileName();
		if (!CommonApplication.columnUpdateTimeSame
				|| !FileManager.containFile(fileName)) {
			FileManager.saveApiData(fileName, data);
		}
	}

	@Override
	protected String getDefaultFileName() {
		return ConstData.getCatListFileName();
	}
}
