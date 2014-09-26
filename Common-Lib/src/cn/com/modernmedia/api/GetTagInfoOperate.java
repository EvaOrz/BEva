package cn.com.modernmedia.api;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Color;
import android.text.TextUtils;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.AppProperty;
import cn.com.modernmedia.model.TagInfoList.ColumnProperty;
import cn.com.modernmedia.model.TagInfoList.IssueProperty;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.TagInfoListDb;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 获取某tag信息
 * 
 * @author jiancong
 * 
 */
public class GetTagInfoOperate extends BaseOperate {
	protected TagInfoList tagInfoList;
	private String parentTagName;
	protected String tagName;
	private String group;
	protected String top;
	private TAG_TYPE type;

	private String url;

	public enum TAG_TYPE {
		APP_INFO/** 应用信息 **/
		, TAG_INFO/** 栏目信息 **/
		, TREE_CAT/** 所有订阅栏目 **/
		, ISSUE_LIST/** 期信息 **/
		, CHILD_CAT/** 子栏目信息 **/
		;
	}

	public GetTagInfoOperate() {
		tagInfoList = new TagInfoList();
	}

	public GetTagInfoOperate(String parentTagName, String tagName,
			String group, String top, TAG_TYPE type) {
		this.parentTagName = parentTagName;
		this.tagName = tagName;
		this.group = group;
		this.top = top;
		tagInfoList = new TagInfoList();
		this.type = type;
		if (type == TAG_TYPE.APP_INFO) {
			url = UrlMaker.getTagInfo(tagName);
		} else if (type == TAG_TYPE.TAG_INFO) {
			url = UrlMaker.getTagInfo(tagName);
		} else if (type == TAG_TYPE.TREE_CAT) {
			url = UrlMaker.getTagChild("", tagName, group, top, false);
		} else if (type == TAG_TYPE.ISSUE_LIST) {
			url = UrlMaker.getTagChild("", tagName, group, top, true);
		} else if (type == TAG_TYPE.CHILD_CAT) {
			url = UrlMaker.getTagChild(parentTagName, "", group, top, true);
		}
		cacheIsDb = true;
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		parseTagInfo(jsonObject);
		TagInfoListDb.getInstance(getmContext()).clearTable(parentTagName,
				tagName, group, type);
		TagInfoListDb.getInstance(getmContext()).addEntry(tagInfoList, type);
	}

	@Override
	public boolean fecthLocalData(String fileName) {
		Entry entry = TagInfoListDb.getInstance(getmContext()).getEntry(this,
				parentTagName, tagName, group, top, type);
		if (entry instanceof TagInfoList) {
			TagInfoList list = (TagInfoList) entry;
			if (ParseUtil.listNotNull(list.getList())) {
				tagInfoList = (TagInfoList) entry;
				if (callBack != null) {
					PrintHelper.print("from db:" + "====" + url);
					callBack.callback(true, false);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 解析tag 信息列表
	 * 
	 * @param jsonObject
	 */
	protected void parseTagInfo(JSONObject jsonObject) {
		JSONArray array = jsonObject.optJSONArray("articletag");
		if (!isNull(array)) {
			int len = array.length();
			for (int i = 0; i < len; i++) {
				JSONObject object = array.optJSONObject(i);
				if (!isNull(object)) {
					TagInfo tagInfo = new TagInfo();
					tagInfo.setAppId(object.optInt("appid"));
					tagInfo.setTagName(object.optString("tagname"));
					String parent = object.optString("parent");
					tagInfo.setParent(parent);
					tagInfo.setLink(object.optString("link"));
					tagInfo.setGroup(object.optInt("group"));
					tagInfo.setHaveChildren(object.optInt("haveChildren"));
					tagInfo.setEnablesubscribe(object.optInt("enablesubscribe"));
					tagInfo.setDefaultsubscribe(object
							.optInt("defaultsubscribe"));
					tagInfo.setIsFix(object.optInt("isfix"));
					tagInfo.setTagLevel(object.optInt("tagLevel"));
					tagInfo.setArticleupdatetime(object
							.optString("articleupdatetime"));
					tagInfo.setColoumnupdatetime(object
							.optString("coloumnupdatetime"));
					tagInfo.setPublishTime(object.optString("publishtime"));
					tagInfo.setAppProperty(parseAppProperty(object
							.optJSONObject("appProperty")));
					tagInfo.getAppProperty().setUpdatetime(
							object.optString("updatetime"));
					tagInfo.setIssueProperty(parseIssueProperty(object
							.optJSONObject("issueProperty")));
					tagInfo.setColumnProperty(parseColumnProperty(
							object.optJSONObject("phoneColumnProperty"),
							tagInfo.getTagName()));

					if (tagInfo.getTagLevel() == 1)
						tagInfoList.getTopLevelList().add(tagInfo.getTagName());

					tagInfoList.addChild(tagInfo);
					tagInfoList.getList().add(tagInfo);
				}
			}
		}
	}

	/**
	 * 解析app信息
	 * 
	 * @param object
	 */
	public AppProperty parseAppProperty(JSONObject object) {
		AppProperty property = new AppProperty();
		if (!isNull(object)) {
			property.setAppJson(object.toString());
			property.setAppname(object.optString("appname"));
			property.setName(object.optString("name"));
			property.setHaveSolo(object.optInt("haveSolo"));
			property.setHaveIssue(object.optInt("haveIssue"));
			property.setHaveSubscribe(object.optInt("haveSubscribe"));
			property.setStartTag(object.optString("startTag"));
			property.getVersion().setVersion(object.optInt("version"));
			property.getVersion().setSrc(object.optString("src"));
			property.getVersion().setChangelog(object.optString("changelog"));
			property.getVersion().setDownload_url(
					object.optString("download_url"));
		}
		return property;
	}

	/**
	 * 解析栏目信息
	 * 
	 * @param object
	 * @return
	 */
	public ColumnProperty parseColumnProperty(JSONObject object, String tagName) {
		ColumnProperty property = new ColumnProperty();
		if (!isNull(object)) {
			property.setColumnJson(object.toString());
			property.setTemplate(object.optString("template"));
			property.setName(object.optString("name"));
			property.setEname(object.optString("ename"));
			property.setCname(object.optString("cname"));
			property.setColor(transformColor(object.optString("color")));
			property.setNoColumn(object.optInt("noColumn"));
			property.setNoMenuBar(object.optInt("noMenuBar"));
			property.setNoLeftMenu(object.optInt("noLeftMenu"));
			property.setHasSpecialColumn(object.optInt("hasSpecialColumn"));
			property.setIsMergeChild(object.optInt("isMergeChild", 0));
			parseSubscriptPicture(property,
					object.optJSONArray("subscriptPicture"));
			parseBigPicture(property, object.optJSONArray("subscriptPicture_l"));
			// TODO 防止同一个tag出现多次，导致后面的空数据覆盖前面的数据
			if (!TextUtils.isEmpty(object.optString("color"))) {
				DataHelper.columnColorMap.put(tagName, property.getColor());
			}
			if (!TextUtils.isEmpty(property.getCname())) {
				DataHelper.columnTitleMap.put(tagName, property.getCname());
			}
			if (ParseUtil.listNotNull(property.getSubscriptPicture())) {
				DataHelper.columnPicMap.put(tagName,
						property.getSubscriptPicture());
			}
			if (ParseUtil.listNotNull(property.getBigPicture())) {
				DataHelper.columnBigMap.put(tagName, property.getBigPicture());
			}
		}
		return property;
	}

	/**
	 * 解析栏目小图
	 * 
	 * @param property
	 * @param arr
	 */
	private void parseSubscriptPicture(ColumnProperty property, JSONArray arr) {
		if (isNull(arr))
			return;
		int length = arr.length();
		JSONObject obj;
		for (int i = 0; i < length; i++) {
			obj = arr.optJSONObject(i);
			if (isNull(obj))
				continue;
			property.getSubscriptPicture().add(obj.optString("url"));
		}
	}

	/**
	 * 解析栏目大图
	 * 
	 * @param property
	 * @param arr
	 */
	private void parseBigPicture(ColumnProperty property, JSONArray arr) {
		if (isNull(arr))
			return;
		int length = arr.length();
		JSONObject obj;
		for (int i = 0; i < length; i++) {
			obj = arr.optJSONObject(i);
			if (isNull(obj))
				continue;
			property.getBigPicture().add(obj.optString("url"));
		}
	}

	/**
	 * 解析期信息
	 * 
	 * @param object
	 * @return
	 */
	public IssueProperty parseIssueProperty(JSONObject object) {
		IssueProperty property = new IssueProperty();
		if (!isNull(object)) {
			property.setIssueJson(object.toString());
			property.setName(object.optString("name"));
			property.setTitle(object.optString("title"));
			property.setStartTime(object.optString("starttime"));
			property.setEndTime(object.optString("endtime"));
			property.setMemo(object.optString("memo"));
			property.setFullPackage(object.optString("fullPackage"));
			JSONArray array = object.optJSONArray("picture");
			if (!isNull(array)) {
				int len = array.length();
				for (int i = 0; i < len; i++) {
					JSONObject picObject = array.optJSONObject(i);
					if (!isNull(picObject)) {
						property.getPictureList().add(
								picObject.optString("url"));
					}
				}
			}
		}
		return property;
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

	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}

	public TagInfoList getTagInfoList() {
		return tagInfoList;
	}

}
