package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.newtag.db.TagInfoListDb;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * tag 信息列表类
 * 
 * @author jiancong
 * 
 */
public class TagInfoList extends Entry {
	private static final long serialVersionUID = 1L;
	private List<TagInfo> list = new ArrayList<TagInfo>();// 所有（一级、二级）栏目列表
	/**
	 * key:父栏目tagname;value:子栏目列表
	 */
	private Map<String, List<TagInfo>> childMap = new HashMap<String, List<TagInfo>>();// 子栏目
	private List<String> parentList = new ArrayList<String>();// 父tag列表
	private List<String> topLevelList = new ArrayList<String>();// 一级栏目列表(只在广告时候找当前栏目所在位置使用)

	public List<TagInfo> getList() {
		return list;
	}

	public void setList(List<TagInfo> list) {
		this.list = list;
	}

	public Map<String, List<TagInfo>> getChildMap() {
		return childMap;
	}

	public void setChildMap(Map<String, List<TagInfo>> childMap) {
		this.childMap = childMap;
	}

	public List<String> getParentList() {
		return parentList;
	}

	public void setParentList(List<String> parentList) {
		this.parentList = parentList;
	}

	public List<String> getTopLevelList() {
		return topLevelList;
	}

	public void setTopLevelList(List<String> topLevelList) {
		this.topLevelList = topLevelList;
	}

	/**
	 * 获取hasSubscript=1的列表
	 * 
	 * @return
	 */
	public TagInfoList getHasSubscriptTagInfoList() {
		TagInfoList tagInfoList = new TagInfoList();
		for (TagInfo tagInfo : list) {
			if (tagInfo.hasSubscribe == 1) {
				tagInfoList.getList().add(tagInfo);
			}
		}
		return tagInfoList;
	}

	/**
	 * 获取子栏目hasSubscript=1的列表
	 * 
	 * @return
	 */
	public TagInfoList getChildHasSubscriptTagInfoList(String parent) {
		TagInfoList tagInfoList = new TagInfoList();
		if (childMap.containsKey(parent)) {
			List<TagInfo> list = childMap.get(parent);
			for (TagInfo tagInfo : list) {
				if (tagInfo.hasSubscribe == 1) {
					tagInfoList.getList().add(tagInfo);
				}
			}
		}
		return tagInfoList;
	}

	/**
	 * 是否有子栏目
	 * 
	 * @param parent
	 * @return
	 */
	public boolean hasChild(String parent) {
		return ParseUtil.listNotNull(getChildHasSubscriptTagInfoList(parent)
				.getList());
	}

	/**
	 * 添加childMap
	 * 
	 * @param tagInfo
	 */
	public void addChild(TagInfo tagInfo) {
		String parent = tagInfo.getParent();
		if (!TextUtils.isEmpty(parent) && tagInfo.getTagLevel() == 2) {
			if (!childMap.containsKey(parent)) {
				childMap.put(parent, new ArrayList<TagInfo>());
			}
			childMap.get(parent).add(tagInfo);
			if (!parentList.contains(parent))
				parentList.add(parent);
		}
	}

	/**
	 * 获取可以订阅的子栏目列表
	 * 
	 * @return
	 */
	public TagInfoList getEnSubscriptMap(String parent) {
		TagInfoList tagInfoList = new TagInfoList();
		if (childMap.isEmpty())
			return tagInfoList;
		for (String key : childMap.keySet()) {
			List<TagInfo> list = childMap.get(key);
			if (!TextUtils.isEmpty(parent) && !TextUtils.equals(parent, key)) {
				continue;
			}
			for (TagInfo tagInfo : list) {
				if (tagInfo.getEnablesubscribe() == 1) {
					// TODO 包含可以订阅的子栏目
					tagInfoList.getChildMap().put(key, list);
					tagInfoList.getParentList().add(key);
					break;
				}
			}
		}
		return tagInfoList;
	}

	/**
	 * 获取栏目列表显示的
	 * 
	 * @param isSubscript
	 *            是否获取订阅栏目
	 * @param addPic
	 *            添加一个显示"订阅"图片的空栏目
	 * @return
	 */
	public List<TagInfo> getColumnTagList(boolean isSubscript, boolean addPic) {
		List<TagInfo> result = new ArrayList<TagInfo>();
		for (TagInfo tagInfo : list) {
			if (tagInfo.getTagLevel() != 1 || tagInfo.getHasSubscribe() == 0)
				continue;
			if (isSubscript) {
				if (tagInfo.getAppId() != ConstData.getInitialAppId()) {
					result.add(tagInfo);
				}
			} else {
				if (tagInfo.getAppId() == ConstData.getInitialAppId()) {
					result.add(tagInfo);
				}
			}
		}
		if (isSubscript && addPic && result.size() > 0) {
			TagInfo tagInfo = new TagInfo();
			tagInfo.setAdapter_id(3);
			// TODO 添加一个显示"订阅"图片的空栏目
			result.add(0, tagInfo);
		}
		return result;
	}

	/**
	 * 获取所有订阅栏目的tagname集合
	 * 
	 * @return
	 */
	public String getSubscriptTagMergeName() {
		String mergeName = "";
		for (TagInfo info : list) {
			if (info.getHasSubscribe() == 0 || info.getTagLevel() != 1
					|| info.getAppId() == ConstData.getInitialAppId())
				continue;
			if (info.getHaveChildren() == 0) {
				mergeName += info.getTagName() + ",";
			} else {
				String childNames = info.getMergeName(false);
				if (!TextUtils.isEmpty(childNames)) {
					mergeName += childNames + ",";
				}
			}
		}
		if (!TextUtils.isEmpty(mergeName)) {
			mergeName = mergeName.substring(0, mergeName.length() - 1);
		}
		return mergeName;
	}

	public TagInfoList copy() {
		TagInfoList result = new TagInfoList();
		for (TagInfo tagInfo : list) {
			result.getList().add(tagInfo.copy());
		}
		if (childMap.keySet().isEmpty())
			return result;
		for (String key : childMap.keySet()) {
			List<TagInfo> list = childMap.get(key);
			for (TagInfo tagInfo : list) {
				if (!result.getChildMap().containsKey(key)) {
					result.getChildMap().put(key, new ArrayList<TagInfo>());
				}
				result.getChildMap().get(key).add(tagInfo.copy());
			}
		}
		for (String str : parentList) {
			result.getParentList().add(str);
		}
		result.getTopLevelList().addAll(topLevelList);
		return result;
	}

	/**
	 * tag 信息类
	 * 
	 * @author jiancong
	 * 
	 */
	public static class TagInfo extends Entry {
		private static final long serialVersionUID = 1L;
		private int appId = 0;
		// 统一标识
		private String tagName = "";
		// 父tag
		private String parent = "";
		// 是否存在子栏目(0:不存在，1:存在)
		private int haveChildren = 0;
		// 内容指向指定的内容 (例：slate://app_1/cat_432_11)
		private String link = "";
		// tagName为栏目，获取文章列表时使用该时间戳，以期获得最新的文章列表
		private String articleupdatetime = "";
		private String coloumnupdatetime = "";
		// 1.app类 2.issue类 3.column类 4.独立栏目
		private int group = 0;
		// 是否可以订阅，1代表是，group此时必须为3
		private int enablesubscribe = 1;
		// 是否默认处于订阅选中状态，1代表是
		private int defaultsubscribe = 1;
		// 是否可以取消订阅选中状态, 1代表不可以
		private int isFix = 1;
		// 是否已经订阅
		private int hasSubscribe = 0;
		// tag等级
		private int tagLevel = 1;
		// tagName为期时，表明其发布时间
		private String publishTime = "";
		// 应用属性
		private AppProperty appProperty = new AppProperty();
		// 期属性
		private IssueProperty issueProperty = new IssueProperty();
		// 栏目属性
		private ColumnProperty columnProperty = new ColumnProperty();
		private int adapter_id;// 关于、推荐之类(1.关于；2.推荐；3.订阅图片；4.底部padding)
		private boolean isUriTag;// 是否是通过uri生成的taginfo

		public TagInfo() {
		}

		public TagInfo(int appId, String tagName, String parent,
				int haveChildren, String link, String articleupdatetime,
				String coloumnupdatetime, int group, int enablesubscribe,
				int defaultsubscribe, int isFix, int hasSubscribe,
				int tagLevel, String publishTime, AppProperty appProperty,
				IssueProperty issueProperty, ColumnProperty columnProperty) {
			super();
			this.appId = appId;
			this.tagName = tagName;
			this.parent = parent;
			this.haveChildren = haveChildren;
			this.link = link;
			this.articleupdatetime = articleupdatetime;
			this.coloumnupdatetime = coloumnupdatetime;
			this.group = group;
			this.enablesubscribe = enablesubscribe;
			this.defaultsubscribe = defaultsubscribe;
			this.isFix = isFix;
			this.hasSubscribe = hasSubscribe;
			this.tagLevel = tagLevel;
			this.publishTime = publishTime;
			this.appProperty = appProperty;
			this.issueProperty = issueProperty;
			this.columnProperty = columnProperty;
		}

		public TagInfo copy() {
			return new TagInfo(appId, tagName, parent, haveChildren, link,
					articleupdatetime, coloumnupdatetime, group,
					enablesubscribe, defaultsubscribe, isFix, hasSubscribe,
					tagLevel, publishTime, appProperty, issueProperty,
					columnProperty);
		}

		/**
		 * 是否显示子栏目
		 * 
		 * @return
		 */
		public boolean showChildren() {
			return haveChildren == 1
					&& AppValue.ensubscriptColumnList.hasChild(tagName)
					&& columnProperty.getIsMergeChild() == 0;
		}

		/**
		 * 获取父栏目下所有子栏目的merge
		 * 
		 * @param checkMerge
		 *            是否只取isMergeChild=1的父栏目
		 * @return 如果不为空，代表需要
		 */
		public String getMergeName(boolean checkMerge) {
			String mergeTagName = "";
			if (haveChildren == 0)
				return "";
			if (checkMerge && columnProperty.getIsMergeChild() == 0)
				return "";
			TagInfoList childInfoList = AppValue.ensubscriptColumnList
					.getChildHasSubscriptTagInfoList(tagName);
			for (TagInfo child : childInfoList.getList()) {
				mergeTagName += child.getTagName() + ",";
			}
			if (mergeTagName.endsWith(","))
				mergeTagName = mergeTagName.substring(0,
						mergeTagName.length() - 1);
			return mergeTagName;
		}

		/**
		 * 获取merge的父栏目；如果没有，返回自己
		 * 
		 * @param context
		 * @return
		 */
		public TagInfo getMergeParentTagInfo(Context context) {
			if (tagLevel == 2) {
				TagInfo parentInfo = TagInfoListDb.getInstance(context)
						.getTagInfoByName(parent, "", true);
				if (parentInfo.columnProperty.isMergeChild == 1) {
					return parentInfo;
				}
			}
			return this;
		}

		public int getAppId() {
			return appId;
		}

		public void setAppId(int appId) {
			this.appId = appId;
		}

		public String getTagName() {
			return tagName;
		}

		public void setTagName(String tagName) {
			this.tagName = tagName;
		}

		public String getParent() {
			return parent;
		}

		public void setParent(String parent) {
			this.parent = parent;
		}

		public int getHaveChildren() {
			return haveChildren;
		}

		public void setHaveChildren(int haveChildren) {
			this.haveChildren = haveChildren;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public String getArticleupdatetime() {
			return articleupdatetime;
		}

		public void setArticleupdatetime(String articleupdatetime) {
			this.articleupdatetime = articleupdatetime;
		}

		public String getColoumnupdatetime() {
			return coloumnupdatetime;
		}

		public void setColoumnupdatetime(String coloumnupdatetime) {
			this.coloumnupdatetime = coloumnupdatetime;
		}

		public int getGroup() {
			return group;
		}

		public void setGroup(int group) {
			this.group = group;
		}

		public int getEnablesubscribe() {
			return enablesubscribe;
		}

		public void setEnablesubscribe(int enablesubscribe) {
			this.enablesubscribe = enablesubscribe;
		}

		public int getDefaultsubscribe() {
			return defaultsubscribe;
		}

		public void setDefaultsubscribe(int defaultsubscribe) {
			this.defaultsubscribe = defaultsubscribe;
		}

		public int getIsFix() {
			return isFix;
		}

		public void setIsFix(int isFix) {
			this.isFix = isFix;
		}

		public int getHasSubscribe() {
			return hasSubscribe;
		}

		public void setHasSubscribe(int hasSubscribe) {
			this.hasSubscribe = hasSubscribe;
		}

		public int getTagLevel() {
			return tagLevel;
		}

		public void setTagLevel(int tagLevel) {
			this.tagLevel = tagLevel;
		}

		public IssueProperty getIssueProperty() {
			return issueProperty;
		}

		public AppProperty getAppProperty() {
			return appProperty;
		}

		public void setAppProperty(AppProperty appProperty) {
			this.appProperty = appProperty;
		}

		public void setIssueProperty(IssueProperty issueProperty) {
			this.issueProperty = issueProperty;
		}

		public ColumnProperty getColumnProperty() {
			return columnProperty;
		}

		public void setColumnProperty(ColumnProperty columnProperty) {
			this.columnProperty = columnProperty;
		}

		public int getAdapter_id() {
			return adapter_id;
		}

		public void setAdapter_id(int adapter_id) {
			this.adapter_id = adapter_id;
		}

		public String getPublishTime() {
			return publishTime;
		}

		public void setPublishTime(String publishTime) {
			this.publishTime = publishTime;
		}

		public boolean isUriTag() {
			return isUriTag;
		}

		public void setUriTag(boolean isUriTag) {
			this.isUriTag = isUriTag;
		}

	}

	/**
	 * 应用信息
	 * 
	 * @author jiancong
	 * 
	 */
	public static class AppProperty extends Entry {
		private static final long serialVersionUID = 1L;
		private String tagName = "";
		private String appname = "";// 英文名
		private String name = "";// 中文名
		@Deprecated
		private int haveSolo;// 是否有独立栏目
		private int haveIssue;// 是否有往期
		private int haveSubscribe;// 是否可以订阅
		@Deprecated
		private String startTag = "";// 首先请求的tag标签
		private Version version = new Version();// 检查更新
		private String appJson = "";
		private String updatetime = "";
		private String splash = "";// iweekly splash图

		public String getTagName() {
			return tagName;
		}

		public void setTagName(String tagName) {
			this.tagName = tagName;
		}

		public String getAppname() {
			return appname;
		}

		public void setAppname(String appname) {
			this.appname = appname;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Deprecated
		public int getHaveSolo() {
			return haveSolo;
		}

		public void setHaveSolo(int haveSolo) {
			this.haveSolo = haveSolo;
		}

		public int getHaveIssue() {
			return haveIssue;
		}

		public void setHaveIssue(int haveIssue) {
			this.haveIssue = haveIssue;
		}

		public int getHaveSubscribe() {
			return haveSubscribe;
		}

		public void setHaveSubscribe(int haveSubscribe) {
			this.haveSubscribe = haveSubscribe;
		}

		public String getStartTag() {
			return startTag;
		}

		public void setStartTag(String startTag) {
			this.startTag = startTag;
		}

		public Version getVersion() {
			return version;
		}

		public void setVersion(Version version) {
			this.version = version;
		}

		public String getAppJson() {
			return appJson;
		}

		public void setAppJson(String appJson) {
			this.appJson = appJson;
		}

		public String getUpdatetime() {
			return updatetime;
		}

		public void setUpdatetime(String updatetime) {
			this.updatetime = updatetime;
		}

		public String getSplash() {
			return splash;
		}

		public void setSplash(String splash) {
			this.splash = splash;
		}

	}

	/**
	 * 期信息类
	 * 
	 * @author jiancong
	 * 
	 */
	public static class IssueProperty extends Entry {
		private static final long serialVersionUID = 1L;
		private String name = "";// 期显示名称
		private String title = "";// 期显示标题
		private String startTime = ""; // 开始时间
		private String endTime = ""; // 结束时间
		private String memo = "";// 摘要
		private List<String> pictureList = new ArrayList<String>();// 期封面图
		private String issueJson = "";
		private String fullPackage = "";

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public String getEndTime() {
			return endTime;
		}

		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}

		public List<String> getPictureList() {
			return pictureList;
		}

		public void setPictureList(List<String> pictureList) {
			this.pictureList = pictureList;
		}

		public String getIssueJson() {
			return issueJson;
		}

		public void setIssueJson(String issueJson) {
			this.issueJson = issueJson;
		}

		public String getFullPackage() {
			return fullPackage;
		}

		public void setFullPackage(String fullPackage) {
			this.fullPackage = fullPackage;
		}

	}

	/**
	 * 栏目信息类
	 * 
	 * @author jiancong
	 * 
	 */
	public static class ColumnProperty extends Entry {
		private static final long serialVersionUID = 1L;
		private String template = "";// 模板
		private String name = "";// 标签全称
		private String ename = ""; // 标签英文名
		private String cname = ""; // 标签中文名
		private int color; // 标签显示颜色
		private int noColumn = 0; // 栏目列表不存在，但在栏目内容列表中可见
		private int noMenuBar = 0;// 栏目列表不存在，但在滑动切换栏目时出现
		private int noLeftMenu = 0; // iPad用
		private int hasSpecialColumn = 0; // iPad用
		private List<String> subscriptPicture = new ArrayList<String>();// 栏目小图
		private List<String> bigPicture = new ArrayList<String>();// 栏目大图
		private String columnJson = "";
		private int isMergeChild;// 是否合并子栏目

		public String getTemplate() {
			return template;
		}

		public void setTemplate(String template) {
			this.template = template;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEname() {
			return ename;
		}

		public void setEname(String ename) {
			this.ename = ename;
		}

		public String getCname() {
			return cname;
		}

		public void setCname(String cname) {
			this.cname = cname;
		}

		public int getColor() {
			return color;
		}

		public void setColor(int color) {
			this.color = color;
		}

		public int getNoColumn() {
			return noColumn;
		}

		public void setNoColumn(int noColumn) {
			this.noColumn = noColumn;
		}

		public int getNoMenuBar() {
			return noMenuBar;
		}

		public void setNoMenuBar(int noMenuBar) {
			this.noMenuBar = noMenuBar;
		}

		public int getNoLeftMenu() {
			return noLeftMenu;
		}

		public void setNoLeftMenu(int noLeftMenu) {
			this.noLeftMenu = noLeftMenu;
		}

		public int getHasSpecialColumn() {
			return hasSpecialColumn;
		}

		public void setHasSpecialColumn(int hasSpecialColumn) {
			this.hasSpecialColumn = hasSpecialColumn;
		}

		public String getColumnJson() {
			return columnJson;
		}

		public void setColumnJson(String columnJson) {
			this.columnJson = columnJson;
		}

		public List<String> getSubscriptPicture() {
			return subscriptPicture;
		}

		public void setSubscriptPicture(List<String> subscriptPicture) {
			this.subscriptPicture = subscriptPicture;
		}

		public List<String> getBigPicture() {
			return bigPicture;
		}

		public void setBigPicture(List<String> bigPicture) {
			this.bigPicture = bigPicture;
		}

		public int getIsMergeChild() {
			return isMergeChild;
		}

		public void setIsMergeChild(int isMergeChild) {
			this.isMergeChild = isMergeChild;
		}

	}
}
