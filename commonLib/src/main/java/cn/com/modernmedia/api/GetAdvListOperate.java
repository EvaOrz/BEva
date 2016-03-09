package cn.com.modernmedia.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;

/**
 * 获取广告信息
 * 
 * @author user
 * 
 */
public class GetAdvListOperate extends BaseOperate {
	private AdvList advList;

	public GetAdvListOperate() {
		advList = new AdvList();
	}

	public AdvList getAdvList() {
		// test
		// advList = new AdvList();
		// AdvTest.addRuBanWeeklyTest(advList);
		// AdvTest.addRuBanZipTest(advList);
		// AdvTest.addRuBanIBBTest(advList);
		// advList.getAdvMap().put(3, new ArrayList<AdvItem>());
		// AdvTest.addShiyeAdvTest(advList);
		// AdvTest.addZuiXinTitleAdvTest(advList);
		// AdvTest.addXinWenListAdvTest(advList);
		return advList;
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getAdvList();
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		JSONArray adver = jsonObject.optJSONArray("adver");
		if (!isNull(adver)) {
			JSONObject obj;
			AdvItem advItem;
			int length = adver.length();
			for (int i = 0; i < length; i++) {
				obj = adver.optJSONObject(i);
				if (isNull(obj))
					continue;
				advItem = new AdvItem();
				advItem.setStartTime(obj.optString("startTime", ""));
				advItem.setEndTime(obj.optString("endTime", ""));
				advItem.setAdvId(obj.optInt("advId", -1));
				advItem.setAppId(obj.optInt("appId", -1));
				advItem.setDeviceType(obj.optInt("deviceType", -1));
				advItem.setAdvType(obj.optInt("advType", -1));
				advItem.setTagname(obj.optString("tagname", ""));
				if (advItem.getAdvType() == AdvList.BETWEEN_ARTICLE
						&& TextUtils.isEmpty(advItem.getTagname())) {
					advItem.setTagname(AdvList.ARTICLE_NULL_CAT);
				}

				advItem.setPosId(obj.optString("posId", ""));
				advItem.setSort(obj.optString("sort", ""));
				advItem.setSection(obj.optInt("section", -1));
				advItem.setArticleId(obj.optString("articleId", ""));
				advItem.setPage(obj.optString("page", ""));
				advItem.setEffects(obj.optString("effects", ""));
				advItem.setShowType(Integer.valueOf(obj.optString("showType")));
				parseAdvSource(obj.optJSONArray("sourceH"), advItem);
				parseTrackerUrl(obj.optJSONObject("trackerUrl"), advItem);
				parsePosInfo(obj.optJSONObject("posInfoV"), advItem);
				if (!advList.getAdvMap().containsKey(advItem.getAdvType())) {
					advList.getAdvMap().put(advItem.getAdvType(),
							new ArrayList<AdvItem>());
				}
				advList.getAdvMap().get(advItem.getAdvType()).add(advItem);
			}
		}
	}

	private void parseAdvSource(JSONArray arr, AdvItem item) {
		if (!isNull(arr)) {
			int length = arr.length();
			AdvSource source;
			JSONObject obj;
			for (int i = 0; i < length; i++) {
				obj = arr.optJSONObject(i);
				if (isNull(obj))
					continue;
				source = new AdvSource();
				source.setUrl(obj.optString("url", ""));
				source.setDesc(obj.optString("desc", ""));
				source.setTitle(obj.optString("title", ""));
				source.setLink(obj.optString("link", ""));
				source.setWidth(obj.optInt("width"));
				source.setHeight(obj.optInt("height"));
				source.setLinks(parseAdvLinks(obj.optJSONArray("links")));
				source.setVideolink(obj.optString("videolink"));
				item.getSourceList().add(source);
			}
		}
	}

	private List<String> parseAdvLinks(JSONArray arr) {
		List<String> links = new ArrayList<String>();
		if (isNull(arr))
			return links;
		int length = arr.length();
		JSONObject obj;
		for (int i = 0; i < length; i++) {
			obj = arr.optJSONObject(i);
			if (!isNull(obj))
				links.add(obj.optString("url"));
		}
		return links;
	}

	private void parseTrackerUrl(JSONObject obj, AdvItem item) {
		if (!isNull(obj)) {
			item.getTracker().setImpressionUrl(
					obj.optString("impressionUrl", ""));
			item.getTracker().setClickUrl(obj.optString("clickUrl", ""));
		}
	}

	/**
	 * 解析广告位置
	 * 
	 * @param obj
	 * @param item
	 */
	private void parsePosInfo(JSONObject obj, AdvItem item) {
		if (!isNull(obj)) {
			item.getPosInfo().setLeft(obj.optInt("left", -1));
			item.getPosInfo().setTop(obj.optInt("top", -1));
		}
	}

	@Override
	protected void saveData(String data) {
		FileManager.saveApiData(ConstData.getAdvList(), data);
	}

	@Override
	protected String getDefaultFileName() {
		return ConstData.getAdvList();
	}

}
