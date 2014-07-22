package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.common.Adv;
import cn.com.modernmedia.model.IndexArticle.Position;

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
	private String slateLink = "";// 首页跳转文章
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

	public List<String> getPictureList() {
		return pictureList;
	}

	public void setPictureList(List<String> pictureList) {
		this.pictureList = pictureList;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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

	public String getSlateLink() {
		return slateLink;
	}

	public void setSlateLink(String slateLink) {
		this.slateLink = slateLink;
	}

	public Adv getAdv() {
		return adv;
	}

	public void setAdv(Adv adv) {
		this.adv = adv;
	}

}
