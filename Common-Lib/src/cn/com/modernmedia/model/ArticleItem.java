package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.AdvList.AdvTracker;
import cn.com.modernmedia.model.CatIndexArticle.SoloColumnIndexItem;
import cn.com.modernmedia.model.IndexArticle.Position;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediaslate.model.Favorite.Thumb;

/**
 * 列表文章统一数据
 * 
 * @author ZhuQiao
 * 
 */
public class ArticleItem extends Entry {
	private static final long serialVersionUID = 1L;
	private int articleId = -1;// 文章id
	private String title = "";// 文章title
	private int catId = -1;// 文章所属栏目id
	private int issueId = -1;// 期id
	private String desc = "";// 描述
	private List<String> picList = new ArrayList<String>();// 文章大图url
	private List<String> thumbList = new ArrayList<String>();// 文章缩略图url
	private Position position = new Position();// 图片位置,1:title,2:列表缩略图
	private boolean showTitleBar = false;// 首页文章titlebar
	private boolean showMoreCat = false;// 显示更多子栏目或者独立栏目
	private String slateLink = "";// 首页跳转文章
	private List<String> slateLinkList = new ArrayList<String>();// iweekly视野栏目使用
	private String tag = "";// 栏目名称
	private String author = "";
	private String outline = "";
	private SoloColumnIndexItem soloItem = null;// 独立栏目
	private AdvSource advSource = null;// 广告
	private boolean isAdv = false;
	private AdvTracker advTracker = null;// 广告统计
	private String inputtime = "";
	private boolean isDateFirst = false;// iweekly新闻栏目根据日期组合，是否为当前日期集合的第一个元素
	private String weburl = "";// 分享链接
	private IndexProperty property = new IndexProperty();

	public int getArticleId() {
		return articleId;
	}

	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getCatId() {
		return catId;
	}

	public void setCatId(int catId) {
		this.catId = catId;
	}

	public int getIssueId() {
		return issueId;
	}

	public void setIssueId(int issueId) {
		this.issueId = issueId;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<String> getPicList() {
		return picList;
	}

	public void setPicList(List<String> picList) {
		this.picList = picList;
	}

	public List<String> getThumbList() {
		return thumbList;
	}

	public void setThumbList(List<String> thumbList) {
		this.thumbList = thumbList;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public boolean isShowTitleBar() {
		return showTitleBar;
	}

	public void setShowTitleBar(boolean showTitleBar) {
		this.showTitleBar = showTitleBar;
	}

	public boolean isShowMoreCat() {
		return showMoreCat;
	}

	public void setShowMoreCat(boolean showMoreCat) {
		this.showMoreCat = showMoreCat;
	}

	public String getSlateLink() {
		return slateLink;
	}

	public void setSlateLink(String slateLink) {
		this.slateLink = slateLink;
	}

	public List<String> getSlateLinkList() {
		return slateLinkList;
	}

	public void setSlateLinkList(List<String> slateLinkList) {
		this.slateLinkList = slateLinkList;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getOutline() {
		return outline;
	}

	public void setOutline(String outline) {
		this.outline = outline;
	}

	public SoloColumnIndexItem getSoloItem() {
		return soloItem;
	}

	public void setSoloItem(SoloColumnIndexItem soloItem) {
		this.soloItem = soloItem;
	}

	public AdvSource getAdvSource() {
		return advSource;
	}

	public void setAdvSource(AdvSource advSource) {
		this.advSource = advSource;
	}

	public boolean isAdv() {
		return isAdv;
	}

	public void setAdv(boolean isAdv) {
		this.isAdv = isAdv;
	}

	public AdvTracker getAdvTracker() {
		return advTracker;
	}

	public void setAdvTracker(AdvTracker advTracker) {
		this.advTracker = advTracker;
	}

	public String getInputtime() {
		return inputtime;
	}

	public void setInputtime(String inputtime) {
		this.inputtime = inputtime;
	}

	public boolean isDateFirst() {
		return isDateFirst;
	}

	public void setDateFirst(boolean isDateFirst) {
		this.isDateFirst = isDateFirst;
	}

	public String getWeburl() {
		return weburl;
	}

	public void setWeburl(String weburl) {
		this.weburl = weburl;
	}

	public IndexProperty getProperty() {
		return property;
	}

	public void setProperty(IndexProperty property) {
		this.property = property;
	}

	/**
	 * 当首页需要收藏时，用来做model转换
	 * 
	 * @param issueId
	 * @return
	 */
	public FavoriteItem convertToFavoriteItem(int issueId) {
		FavoriteItem item = new FavoriteItem();
		item.setId(articleId);
		item.setTitle(title);
		item.setDesc(desc);
		item.setCatid(catId);
		// item.setLink("");
		item.setIssueid(issueId);
		// item.setFavtime("");
		// item.setFavdel(0);
		if (ParseUtil.listNotNull(picList)) {
			for (String picture : picList) {
				Thumb thumb = new Thumb();
				thumb.setUrl(picture);
				item.getThumb().add(thumb);
			}
		} else {
			for (String picture : thumbList) {
				Thumb thumb = new Thumb();
				thumb.setUrl(picture);
				item.getThumb().add(thumb);
			}
		}

		// item.setProperty(null);
		if (soloItem != null) {
			item.setPagenum(soloItem.getPagenum());
			item.setUpdateTime(soloItem.getUpdateTime());
			item.setOffset(soloItem.getOffset());
		}
		item.setTag(tag);
		return item;
	}

	public static class IndexProperty extends Entry {
		private static final long serialVersionUID = 1L;
		private int level;
		private int type;// 1.普通文章；2.组图文章；3.视频文章；4.组合文章；5.动态文章；6.链接文章；7.音频文章
		private int havecard;

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getHavecard() {
			return havecard;
		}

		public void setHavecard(int havecard) {
			this.havecard = havecard;
		}

	}
}
