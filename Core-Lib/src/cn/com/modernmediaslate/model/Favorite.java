package cn.com.modernmediaslate.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * 收藏（因为用户模块和普通收藏都需要，所以放入底层）
 * 
 * @author ZhuQiao
 * 
 */

public class Favorite extends Entry {
	private static final long serialVersionUID = 1L;
	@Expose
	private String uid = "";// 用户id
	@Expose
	private String appid = "";
	@Expose
	private List<FavoriteItem> article = new ArrayList<FavoriteItem>();

	public List<FavoriteItem> getList() {
		return article;
	}

	public void setList(List<FavoriteItem> list) {
		this.article = list;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public static class FavoriteItem extends Entry {
		private static final long serialVersionUID = 1L;
		@Expose
		private int id;
		@Expose
		private String title = "";
		@Expose
		private String desc = "";
		@Expose
		private int catid;
		@Expose
		private String link = "";
		@Expose
		private int issueid;
		@Expose
		private String updateTime = "";
		@Expose
		private String favtime = "";// 收藏时间，用来排序
		@Expose
		private int favdel;// 1.删除
		@Expose
		private List<Thumb> thumb = new ArrayList<Thumb>();
		@Expose
		private Property property = new Property();
		@Expose
		private int pagenum;
		@Expose
		private String offset = "";// 独立栏目offset
		@Expose
		private String tag = "";
		private String jsonObject = "";// 独立栏目的json
		private boolean isAdv = false;
		private int advId = -1;
		private String impressionUrl = "";// 广告展示的统计
		private String clickUrl = "";// 广告点击的统计

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public int getCatid() {
			return catid;
		}

		public void setCatid(int catid) {
			this.catid = catid;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public int getIssueid() {
			return issueid;
		}

		public void setIssueid(int issueid) {
			this.issueid = issueid;
		}

		public String getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(String updateTime) {
			this.updateTime = updateTime;
		}

		public String getFavtime() {
			return favtime;
		}

		public void setFavtime(String favtime) {
			this.favtime = favtime;
		}

		public int getFavdel() {
			return favdel;
		}

		public void setFavdel(int favdel) {
			this.favdel = favdel;
		}

		public List<Thumb> getThumb() {
			return thumb;
		}

		public void setThumb(List<Thumb> thumb) {
			this.thumb = thumb;
		}

		public Property getProperty() {
			return property;
		}

		public void setProperty(Property property) {
			this.property = property;
		}

		public int getPagenum() {
			return pagenum;
		}

		public void setPagenum(int pagenum) {
			this.pagenum = pagenum;
		}

		public String getOffset() {
			return offset;
		}

		public void setOffset(String offset) {
			this.offset = offset;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public String getJsonObject() {
			return jsonObject;
		}

		public void setJsonObject(String jsonObject) {
			this.jsonObject = jsonObject;
		}

		public boolean isAdv() {
			return isAdv;
		}

		public void setAdv(boolean isAdv) {
			this.isAdv = isAdv;
		}

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

		public int getAdvId() {
			return advId;
		}

		public void setAdvId(int advId) {
			this.advId = advId;
		}

	}

	public static class Thumb extends Entry {
		private static final long serialVersionUID = 1L;
		@Expose
		private String url = "";
		private String title = "";
		private String desc = "";
		private String link = "";
		private int width;
		private int height;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
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

	/**
	 * 文章属性
	 * 
	 * @author ZhuQiao
	 * 
	 */
	public static class Property extends Entry {
		private static final long serialVersionUID = 1L;
		@Expose
		private int type = 1;// 1 html 2 gallery 3 video 4 只有一张图片
		private int scrollHidden = 0;// 滑动时是否隐藏该文章。0.否。1.是

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getScrollHidden() {
			return scrollHidden;
		}

		public void setScrollHidden(int scrollHidden) {
			this.scrollHidden = scrollHidden;
		}

	}
}
