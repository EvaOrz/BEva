package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediaslate.model.Favorite.Thumb;

/**
 * 广告列表
 * 
 * @author user
 * 
 */
public class AdvList extends Entry {
	private static final long serialVersionUID = 1L;
	/**
	 * 入版广告
	 */
	public static final Integer RU_BAN = 1;
	/**
	 * 栏目间广告
	 */
	public static final Integer BETWEEN_CAT = 2;
	/**
	 * 栏目内固定位置广告
	 */
	public static final Integer IN_CAT = 3;
	/**
	 * 栏目内浮动广告
	 */
	public static final Integer FU_DONG = 4;
	/**
	 * 文章间广告(包括组图)
	 */
	public static final Integer BETWEEN_ARTICLE = 5;
	/**
	 * 文章内固定位置广告
	 */
	public static final Integer IN_ARTICLE = 6;

	/**
	 * 入版动画效果，一张图片渐出->进入首页
	 */
	public static final String IBB = "ibb";
	/**
	 * 入版动画效果，一张图片从120%缩小到100%，延迟2S，渐出->进入首页
	 */
	public static final String ILOHAS = "ilohas";
	/**
	 * 入版动画效果，多张图片渐入渐出->进入首页
	 */
	public static final String IWEEKLY = "iweekly";
	/**
	 * 当文章的广告没有catId时，以all表示，方便排序
	 */
	public static final String ARTICLE_NULL_CAT = "all";
	@SuppressLint("UseSparseArrays")
	/**
	 * 根据广告类型区分
	 */
	private Map<Integer, List<AdvItem>> advMap = new HashMap<Integer, List<AdvItem>>();

	public Map<Integer, List<AdvItem>> getAdvMap() {
		return advMap;
	}

	public void setAdvMap(Map<Integer, List<AdvItem>> advMap) {
		this.advMap = advMap;
	}

	public static class AdvItem extends Entry {
		private static final long serialVersionUID = 1L;
		private int advId;// 广告唯一标示
		private int appId;// 所属应用id
		private int deviceType;// 设备类型
		/**
		 * 广告类型：1.进版广告；2.栏目间广告；3.栏目内固定位置广告；4.栏目内浮动广告；5.文章间广告；6.文章内固定位置广告
		 */
		private int advType;
		/**
		 * @args * 代表所有期
		 * @args * / 2代表每两期有一个广告
		 * @args 0 独立栏目
		 * @args 具体数字 特定期
		 * @args L 最新一期 包含独立栏目
		 */
		private String issueId = "";// 所属期id
		/**
		 * @args 0 代表首页
		 * @args * 代表所有栏目
		 * @args * / 2 代表每两个栏目有一个广告...
		 */
		private String catId = "";// 所属栏目id
		/**
		 * @args * 代表所有文章前面都带广告
		 * @args * / 2 为每两篇文章有一个广告...
		 */
		private String articleId = "";// 所属文章
		/**
		 * 暂时指组图
		 * 
		 * @args * 代表所有图片前面加行广告
		 * @args * / 2 代表每两个图片有一个广告...
		 */
		private String page = "";// 所在页数
		/**
		 * 广告效果 string，ibb,商周的淡出，ilohas,lohas的淡出效果,iweekly,iweekly的图片动画效果
		 */
		private String effects = "";// 广告效果
		private int showType;// 0为图片(当为文章建广告时，为组图)，1为网页
		private List<AdvSource> sourceList = new ArrayList<AdvSource>();
		private AdvTracker tracker = new AdvTracker();// 广告统计地址
		private AdvPosInfo posInfo = new AdvPosInfo();// 广告所在位置
		private String startTime = "";// 广告开始时间
		private String endTime = "";// 广告结束时间
		private boolean isExpired = false;// 是否过期
		private String posId = "";// 广告所在位置，广告类型3和广告类型6才会有这个位置 string
		/**
		 * 广告类型3对应位置下的顺序，广告类型为2时，代表栏目顺序，广告类型为5时代表文章顺序 int,*代表所有，* /
		 * 2代表每两个有一个广告，例如文章顺序为＊时，每个文章前面都带此广告，* / 2时每两个文章带此广告
		 */
		private String sort = "";// 广告类型3对应位置下的顺序，广告类型为2时，代表栏目顺序，广告类型为5时代表文章顺序
		// "section":2//当广告投放在商周的首页，今日焦点下面的列表时所需的字段，2表示第二个栏目
		private int section = -1;

		public int getAdvId() {
			return advId;
		}

		public void setAdvId(int advId) {
			this.advId = advId;
		}

		public int getAppId() {
			return appId;
		}

		public void setAppId(int appId) {
			this.appId = appId;
		}

		/**
		 * 
		 * @return 广告类型：1.进版广告；2.栏目间广告；3.栏目内固定位置广告；4.栏目内浮动广告；5.文章间广告；6.文章内固定位置广告
		 */
		public int getDeviceType() {
			return deviceType;
		}

		public void setDeviceType(int deviceType) {
			this.deviceType = deviceType;
		}

		public int getAdvType() {
			return advType;
		}

		public void setAdvType(int advType) {
			this.advType = advType;
		}

		public String getIssueId() {
			return issueId;
		}

		public void setIssueId(String issueId) {
			this.issueId = issueId;
		}

		public String getCatId() {
			return catId;
		}

		public void setCatId(String catId) {
			this.catId = catId;
		}

		public String getArticleId() {
			return articleId;
		}

		public void setArticleId(String articleId) {
			this.articleId = articleId;
		}

		public String getPage() {
			return page;
		}

		public void setPage(String page) {
			this.page = page;
		}

		public String getEffects() {
			return effects;
		}

		public void setEffects(String effects) {
			this.effects = effects;
		}

		public int getShowType() {
			return showType;
		}

		public void setShowType(int showType) {
			this.showType = showType;
		}

		public AdvTracker getTracker() {
			return tracker;
		}

		public void setTracker(AdvTracker tracker) {
			this.tracker = tracker;
		}

		public AdvPosInfo getPosInfo() {
			return posInfo;
		}

		public void setPosInfo(AdvPosInfo posInfo) {
			this.posInfo = posInfo;
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

		public List<AdvSource> getSourceList() {
			return sourceList;
		}

		public void setSourceList(List<AdvSource> sourceList) {
			this.sourceList = sourceList;
		}

		public boolean isExpired() {
			return isExpired;
		}

		public void setExpired(boolean isExpired) {
			this.isExpired = isExpired;
		}

		// template // 当广告为栏目间广告时，指定plist文件
		/**
		 * @args 首页(栏目首页):1.焦点图；2.列表
		 * @args 文章：1.
		 */
		public String getPosId() {
			return posId;
		}

		public void setPosId(String posId) {
			this.posId = posId;
		}

		/**
		 * 插入的位置
		 * PS:1.位置从1开始，如果sort=0，那么说明后台录入有误，不添加；2.每个相同类型的广告item的sort都不相等，如果相等，
		 * 那么后面的会把前面的替换
		 */
		public String getSort() {
			return sort;
		}

		public void setSort(String sort) {
			if (ParseUtil.stoi(sort, -1) != -1) {
				sort = String.valueOf(ParseUtil.stoi(sort, -1) - 1);
			}
			this.sort = sort;
		}

		public int getSection() {
			return section;
		}

		public void setSection(int section) {
			this.section = section - 1;
		}

		public ArticleItem convertToArticleItem() {
			ArticleItem item = new ArticleItem();
			item.setAdv(true);
			if (ParseUtil.listNotNull(sourceList)) {
				AdvSource source = sourceList.get(0);
				item.setTitle(source.getTitle());
				item.setDesc(source.getDesc());
				item.getPicList().add(source.getUrl());
				item.setSlateLink(source.getLink());
				item.setAdvSource(source);
			}
			if (posInfo != null) {
				item.getPosition().setId(ParseUtil.stoi(posId, 1));
			}
			item.setAdvTracker(tracker);
			return item;
		}

		public List<FavoriteItem> convertToFavoriteItem() {
			List<FavoriteItem> list = new ArrayList<FavoriteItem>();
			FavoriteItem item;
			for (AdvSource source : sourceList) {
				item = new FavoriteItem();
				item.setAdv(true);
				item.setAdvId(advId);
				item.setLink(source.getUrl());
				item.setTitle(source.getTitle());
				item.setDesc(source.getDesc());
				if (showType == 0 && advType == BETWEEN_ARTICLE) {
					// 组图
					item.getProperty().setType(2);
					Thumb thumb = new Thumb();
					thumb.setUrl(source.getUrl());
					thumb.setTitle(source.getTitle());
					thumb.setDesc(source.getDesc());
					thumb.setLink(source.getLink());
					thumb.setWidth(source.getWidth());
					thumb.setHeight(source.getHeight());
					item.getThumb().add(thumb);
				}
				if (tracker != null) {
					item.setImpressionUrl(tracker.getImpressionUrl());
					item.setClickUrl(tracker.getClickUrl());
				}
				list.add(item);
			}
			return list;
		}
	}

	/**
	 * 广告资源
	 * 
	 * @author user
	 * 
	 */
	public static class AdvSource extends Entry {
		private static final long serialVersionUID = 1L;
		private String url = "";// 图片或网页或pdfurl string
		private String desc = "";// 资源附属描述 string
		private String title = "";// 资源附属标题 string
		private String link = "";// 资源跳转链接 string
		private int width;// 资源宽 int
		private int height;// 资源高 int

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

	}

	// public static class AdvPic extends Entry {
	// private static final long serialVersionUID = 1L;
	// private String url = "";// 图片url
	// private String desc = "";// 图片附属描述
	// private String title = "";// 图片附属标题
	// private String link = "";// 图片跳转链接
	// private int width;// 图片宽
	// private int height;// 图片高
	//
	// public String getUrl() {
	// return url;
	// }
	//
	// public void setUrl(String url) {
	// this.url = url;
	// }
	//
	// public String getDesc() {
	// return desc;
	// }
	//
	// public void setDesc(String desc) {
	// this.desc = desc;
	// }
	//
	// public String getTitle() {
	// return title;
	// }
	//
	// public void setTitle(String title) {
	// this.title = title;
	// }
	//
	// public String getLink() {
	// return link;
	// }
	//
	// public void setLink(String link) {
	// this.link = link;
	// }
	//
	// public int getWidth() {
	// return width;
	// }
	//
	// public void setWidth(int width) {
	// this.width = width;
	// }
	//
	// public int getHeight() {
	// return height;
	// }
	//
	// public void setHeight(int height) {
	// this.height = height;
	// }
	//
	// }

	/**
	 * 广告统计
	 * 
	 * @author user
	 * 
	 */
	public static class AdvTracker extends Entry {
		private static final long serialVersionUID = 1L;
		private String impressionUrl = "";// 广告展示的统计
		private String clickUrl = "";// 广告点击的统计

		public String getImpressionUrl() {
			return impressionUrl;
		}

		public void setImpressionUrl(String impressionUrl) {
			this.impressionUrl = impressionUrl;
		}

		public String getClickUrl() {
			return clickUrl;
		}

		public void setClickUrl(String clickUrl) {
			this.clickUrl = clickUrl;
		}

	}

	/**
	 * 浮动广告
	 * 
	 * @author user
	 * 
	 */
	// public static class AdvPageInfo extends Entry {
	// private static final long serialVersionUID = 1L;
	// private String url = "";// 广告为一个weburl
	// private int autoClose;// 关闭时间
	// private int width;// 宽度
	// private int height;// 高度
	//
	// public String getUrl() {
	// return url;
	// }
	//
	// public void setUrl(String url) {
	// this.url = url;
	// }
	//
	// public int getAutoClose() {
	// return autoClose;
	// }
	//
	// public void setAutoClose(int autoClose) {
	// this.autoClose = autoClose;
	// }
	//
	// public int getWidth() {
	// return width;
	// }
	//
	// public void setWidth(int width) {
	// this.width = width;
	// }
	//
	// public int getHeight() {
	// return height;
	// }
	//
	// public void setHeight(int height) {
	// this.height = height;
	// }
	//
	// }

	/**
	 * 广告位置
	 * 
	 * @author user
	 * 
	 */
	public static class AdvPosInfo extends Entry {
		private static final long serialVersionUID = 1L;
		private int left;// 浮动广告距离左侧的距离
		private int top;// 浮动广告距离顶部距离

		public int getLeft() {
			return left;
		}

		public void setLeft(int left) {
			this.left = left;
		}

		public int getTop() {
			return top;
		}

		public void setTop(int top) {
			this.top = top;
		}
	}

	/**
	 * 广告资源(网页)
	 * 
	 * @author user
	 * 
	 */
	// public static class AdvSource extends Entry {
	// private static final long serialVersionUID = 1L;
	// private String url = "";
	//
	// public String getUrl() {
	// return url;
	// }
	//
	// public void setUrl(String url) {
	// this.url = url;
	// }
	//
	// }
}
