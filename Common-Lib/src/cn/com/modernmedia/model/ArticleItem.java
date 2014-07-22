package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.model.CatIndexArticle.SoloColumnIndexItem;
import cn.com.modernmedia.model.IndexArticle.Position;
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
	private String desc = "";// 描述
	private List<String> pictureList = new ArrayList<String>();// 文章图片url
	private Position position = new Position();// 图片位置,1:title,2:列表缩略图
	private boolean showTitleBar = false;// 首页文章titlebar
	private boolean showMoreCat = false;// 显示更多子栏目或者独立栏目
	private String slateLink = "";// 首页跳转文章
	private String tag = "";// 栏目名称
	private String author = "";
	private String outline = "";
	private SoloColumnIndexItem soloItem = null;// 独立栏目
	private Adv adv = new Adv();

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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<String> getPictureList() {
		return pictureList;
	}

	public void setPictureList(List<String> pictureList) {
		this.pictureList = pictureList;
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

	public Adv getAdv() {
		return adv;
	}

	public void setAdv(Adv adv) {
		this.adv = adv;
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
		for (String picture : pictureList) {
			Thumb thumb = new Thumb();
			thumb.setUrl(picture);
			item.getThumb().add(thumb);
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
}
