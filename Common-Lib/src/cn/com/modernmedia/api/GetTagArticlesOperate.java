package cn.com.modernmedia.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Color;
import android.text.TextUtils;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.IndexProperty;
import cn.com.modernmedia.model.ArticleItem.PhonePageList;
import cn.com.modernmedia.model.ArticleItem.Picture;
import cn.com.modernmedia.model.ArticleItem.Position;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList.ColumnProperty;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.TagArticleListDb;
import cn.com.modernmedia.newtag.db.TagIndexDb;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 获取tag对应的文章
 * 
 * @author jiancong
 * 
 */
public class GetTagArticlesOperate extends BaseIndexAdvOperate {
	protected String url = "";
	protected TagArticleList articleList;
	protected String tagName, parent = "";
	protected String top;
	protected boolean isCatIndex;
	private String limited;
	private TagInfo tagInfo;

	// 排序用
	private List<String> groupList = new ArrayList<String>();
	private DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	public GetTagArticlesOperate(TagInfo info, String top, String limited,
			TagArticleList _articleList) {
		reSetPosition();
		this.tagInfo = info;
		tagName = info.getMergeName(true);
		if (TextUtils.isEmpty(tagName)) {
			tagName = info.getTagName();
		} else {
			parent = info.getTagName();
		}
		if (info.getTagLevel() == 2)
			parent = info.getParent();
		this.top = top;
		isCatIndex = false;
		this.limited = limited;
		this.url = UrlMaker.getArticlesByTag(info, top, limited);
		if (_articleList == null)
			articleList = new TagArticleList();
		else
			articleList = _articleList;
		cacheIsDb = true;
		if (TextUtils.isEmpty(top)) {
			int catPos = AdvTools.getCatPosition(info.getTagName());
			initAdv(info.getTagName(), catPos);
			initArticleAdv(info.getTagName(), catPos);
		}
	}

	public GetTagArticlesOperate() {
		articleList = new TagArticleList();
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		JSONArray articletag = jsonObject.optJSONArray("articletag");
		if (isNull(articletag))
			return;
		parseArr(articletag);
		addLarger();
		if (!cacheIsDb)
			return;
		if (TextUtils.equals(limited, "5")) {
			if (isCatIndex) {
				TagIndexDb.getInstance(getmContext())
						.clearSubscribeTopArticle();
				TagIndexDb.getInstance(getmContext()).saveSubscribeTopArticle(
						articleList);
			} else {
				TagArticleListDb.getInstance(getmContext())
						.clearSubscribeTopArticle();
				TagArticleListDb.getInstance(getmContext())
						.saveSubscribeTopArticle(articleList);
			}
			return;
		}
		if (TextUtils.isEmpty(top)) {
			if (isCatIndex) {
				TagIndexDb.getInstance(getmContext()).clearTable(tagName);
			} else {
				TagArticleListDb.getInstance(getmContext()).clearTable(tagName);
			}
		}
		if (isCatIndex) {
			TagIndexDb.getInstance(getmContext()).addEntry(articleList);
		} else {
			TagArticleListDb.getInstance(getmContext()).addEntry(articleList);
		}
	}

	/**
	 * 添加sort超出范围的广告
	 */
	private void addLarger() {
		getListFromMap(1).addAll(getAdvsLargerThisPosition("1"));
		getListFromMap(2).addAll(getAdvsLargerThisPosition("2"));
		articleList.getArticleList().addAll(getArticleAdvsLargerThisPosition());
	}

	protected void parseArr(JSONArray arr) {
		JSONObject jsonObject = arr.optJSONObject(0);
		if (isNull(jsonObject))
			return;
		articleList.setAppid(jsonObject.optInt("appid"));
		articleList.setTagName(jsonObject.optString("tagname"));
		articleList.setProperty(parseColumnProperty(jsonObject
				.optJSONObject("phoneColumnProperty")));
		articleList.setViewbygroup(jsonObject.optString("viewbygroup"));
		parseArtcle(jsonObject);
	}

	/**
	 * 解析栏目信息
	 * 
	 * @param object
	 * @return
	 */
	public ColumnProperty parseColumnProperty(JSONObject object) {
		ColumnProperty property = new ColumnProperty();
		if (!isNull(object)) {
			articleList.setColumnJson(object.toString());
			property.setTemplate(object.optString("template"));
			property.setName(object.optString("name"));
			property.setEname(object.optString("ename"));
			property.setCname(object.optString("cname"));
			property.setColor(transformColor(object.optString("color")));
			property.setNoColumn(object.optInt("noColumn"));
			property.setNoMenuBar(object.optInt("noMenuBar"));
			property.setNoLeftMenu(object.optInt("noLeftMenu"));
			property.setHasSpecialColumn(object.optInt("hasSpecialColumn"));
			property.setIsMergeChild(object.optInt("isMergeChild"));
		}
		return property;
	}

	/**
	 * 解析文章详情
	 * 
	 * @param jsonObject
	 * @return
	 */
	private void parseArtcle(JSONObject jsonObject) {
		JSONArray artcleArr = jsonObject.optJSONArray("article");
		if (!isNull(artcleArr)) {
			int length = artcleArr.length();
			JSONObject obj;
			for (int i = 0; i < length; i++) {
				obj = artcleArr.optJSONObject(i);
				if (isNull(obj))
					continue;
				parseArticleItem(obj);
			}
		}
	}

	public void parseArticleItem(JSONObject obj) {
		ArticleItem item = new ArticleItem();
		item.setJsonObject(obj.toString());
		item.setArticleId(obj.optInt("articleid", -1));
		item.setTitle(obj.optString("title", ""));
		item.setDesc(obj.optString("desc", ""));
		if (tagInfo != null) {
			item.setAppid(tagInfo.getAppId());
		} else {
			item.setAppid(obj.optInt("appid", ConstData.getInitialAppId()));
		}
		item.setOffset(obj.optString("offset", ""));
		JSONArray links = obj.optJSONArray("links");
		if (!isNull(links)) {
			item.setSlateLinkList(parseLinks(links));
			if (ParseUtil.listNotNull(item.getSlateLinkList()))
				item.setSlateLink(item.getSlateLinkList().get(0));
		}
		item.setAuthor(obj.optString("author", ""));
		item.setOutline(obj.optString("outline", ""));
		item.setInputtime(obj.optString("inputtime"));
		item.setUpdateTime(obj.optString("updatetime"));
		item.setWeburl(obj.optString("weburl", ""));
		item.setProperty(parseProperty(obj.optJSONObject("property")));
		item.setGroupname(obj.optString("groupname"));
		item.setTagName(item.getGroupname());
		item.setApiTag(tagName);
		item.setParent(parent);
		item.setSubtitle(obj.optString("subtitle"));
		item.setCreateuser(obj.optString("createuser"));
		item.setModifyuser(obj.optString("modifyuser"));
		item.setSubscribeMerge(TextUtils.equals(limited, "5"));
		// 文章url地址解析
		JSONArray pageUrlList = obj.optJSONArray("phonepagelist");
		if (!isNull(pageUrlList)) {
			item.setPageUrlList(parsePageUrl(pageUrlList));
		}
		// 大图解析
		JSONArray picArr = obj.optJSONArray("picture");
		if (!isNull(picArr)) {
			item.setPicList(parsePicture(picArr));
		}
		// 列表图解析
		JSONArray thumbArr = obj.optJSONArray("thumb");
		if (!isNull(thumbArr)) {
			item.setThumbList(parseThumb(thumbArr));
		}
		// 图片位置
		item.setPosition(parsePosition(obj.optJSONObject("position")));
		articleList.setEndOffset(item.getOffset());

		int position = item.getPosition().getId();

		setSort(item);
		addDataToList(item, position);
	}

	/**
	 * 获取当前position对应的列表
	 * 
	 * @param position
	 * @return
	 */
	private List<ArticleItem> getListFromMap(int position) {
		if (!articleList.getMap().containsKey(position)) {
			articleList.getMap().put(position, new ArrayList<ArticleItem>());
		}
		return articleList.getMap().get(position);
	}

	/**
	 * 添加数据
	 * 
	 * @param item
	 */
	private void addDataToList(ArticleItem item, int position) {
		if (position == 1) {
			getListFromMap(1).addAll(getAdvsMatchThisPosition("1"));
			getListFromMap(1).add(item);
			currentPosition1++;
		} else if (position == 2) {
			getListFromMap(2).addAll(getAdvsMatchThisPosition("2"));
			getListFromMap(2).add(item);
			currentPosition2++;
		} else if (position == 3) {
			getListFromMap(3).addAll(getAdvsMatchThisPosition("3"));
			getListFromMap(3).add(item);
			currentPositionInSection++;
		}
		if (item.getProperty().getScrollHidden() == 0) {
			articleList.getArticleList().addAll(
					getArticleAdvsMatchThisPosition());
			currentArticlePosition++;
		}
		articleList.getArticleList().add(item);
	}

	/**
	 * 设置排序数据
	 * 
	 * @param item
	 */
	private void setSort(ArticleItem item) {
		if (TextUtils.isEmpty(articleList.getViewbygroup())
				|| item.getPosition().getId() == 1)
			return;
		if (TextUtils.equals(articleList.getViewbygroup(),
				TagArticleList.BY_CATNAME)) {
			// 根据栏目排序
			if (item.getPosition().getId() != 3
					|| TextUtils.isEmpty(item.getGroupname()))
				return;
			if (!groupList.contains(item.getGroupname())) {
				item.setShowTitleBar(true);
				groupList.add(item.getGroupname());
				currentSection++;
				currentPositionInSection = 0;
			}
		} else if (TextUtils.equals(articleList.getViewbygroup(),
				TagArticleList.BY_INPUTTIME)) {
			// 根据日期排序
			String date = format.format(new Date(ParseUtil.stol(item
					.getInputtime()) * 1000L));
			if (!articleList.getDateList().contains(date)) {
				item.setShowTitleBar(true);
				articleList.getDateList().add(date);
			}
		}
	}

	/**
	 * 解析links，iweekly视野使用
	 * 
	 * @param array
	 * @return
	 */
	private List<String> parseLinks(JSONArray array) {
		List<String> linkList = new ArrayList<String>();
		JSONObject object;
		for (int i = 0; i < array.length(); i++) {
			object = array.optJSONObject(i);
			if (isNull(object))
				continue;
			linkList.add(object.optString("url"));
		}
		return linkList;
	}

	/**
	 * 解析图片
	 * 
	 * @param array
	 * @return
	 */
	private List<PhonePageList> parsePageUrl(JSONArray array) {
		List<PhonePageList> pageUrlList = new ArrayList<PhonePageList>();
		JSONObject object;
		PhonePageList page;
		int length = array.length();
		for (int i = 0; i < length; i++) {
			object = array.optJSONObject(i);
			if (isNull(object))
				continue;
			page = new PhonePageList();
			page.setUrl(object.optString("url"));
			page.setTitle(object.optString("title"));
			page.setDesc(object.optString("desc"));
			page.setUri(object.optString("link"));
			pageUrlList.add(page);
		}
		return pageUrlList;
	}

	/**
	 * 解析图片
	 * 
	 * @param array
	 * @return
	 */
	private List<String> parsePicture(JSONArray array) {
		List<String> pictureList = new ArrayList<String>();
		JSONObject object;
		for (int i = 0; i < array.length(); i++) {
			object = array.optJSONObject(i);
			if (isNull(object))
				continue;
			pictureList.add(object.optString("url"));
			// TODO iweekly视野使用
			String bigUrl = object.optString("bigimgurl");
			if (!TextUtils.isEmpty(bigUrl)) {
				pictureList.add(bigUrl);
			}
		}
		return pictureList;
	}

	/**
	 * 解析缩略图
	 * 
	 * @param array
	 * @return
	 */
	private List<Picture> parseThumb(JSONArray array) {
		List<Picture> pictureList = new ArrayList<Picture>();
		JSONObject object;
		for (int i = 0; i < array.length(); i++) {
			object = array.optJSONObject(i);
			if (isNull(object))
				continue;
			Picture picture = new Picture();
			picture.setUrl(object.optString("url"));
			pictureList.add(picture);
		}
		return pictureList;
	}

	/**
	 * 解析position
	 * 
	 * @param obj
	 * @return
	 */
	private Position parsePosition(JSONObject obj) {
		Position position = new Position();
		if (!isNull(obj)) {
			position.setId(obj.optInt("positionid", -1));
			position.setStyle(obj.optInt("style", 1));
		}
		return position;
	}

	/**
	 * 解析Property
	 * 
	 * @param obj
	 * @return
	 */
	private IndexProperty parseProperty(JSONObject obj) {
		IndexProperty property = new IndexProperty();
		if (!isNull(obj)) {
			property.setLevel(obj.optInt("level", 0));
			property.setType(obj.optInt("type", 1));
			property.setHavecard(obj.optInt("havecard", 1));
			property.setScrollHidden(obj.optInt("scrollHidden", 0));
			property.setHasvideo(obj.optInt("hasvideo", 0));
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
	protected CallBackData fetchDataFromDB() {
		// TODO 有网的时候，缓存只拿第一页数据
		CallBackData callBackData = new CallBackData();
		if (Tools.checkNetWork(getmContext()) && !TextUtils.isEmpty(top)) {
			return callBackData;
		}
		Entry entry;
		if (isCatIndex) {
			entry = TagIndexDb.getInstance(getmContext()).getEntry(this,
					tagName, top, true, "");
		} else {
			entry = TagArticleListDb.getInstance(getmContext()).getEntry(this,
					tagName, top, false, "");
		}
		if (entry instanceof TagArticleList) {
			articleList = (TagArticleList) entry;
			boolean hasData = false;
			if (isCatIndex) {
				hasData = !articleList.getMap().isEmpty();
			} else {
				hasData = ParseUtil.listNotNull(articleList.getArticleList());
			}
			if (hasData) {
				addLarger();
				PrintHelper.print("from db:getTagArticleList=" + tagName
						+ "====" + url);
				callBackData.success = true;
			}
		}
		return callBackData;
	}

	@Override
	protected void saveData(String data) {
	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}

	public TagArticleList getArticleList() {
		return articleList;
	}
}
