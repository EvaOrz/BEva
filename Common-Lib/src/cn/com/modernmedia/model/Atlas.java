package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;

/**
 * 图集
 * 
 * @author ZhuQiao
 * 
 */
public class Atlas extends FavoriteItem {
	private static final long serialVersionUID = 1L;
	private List<AtlasPicture> list = new ArrayList<AtlasPicture>();
	private String weburl = "";

	public List<AtlasPicture> getList() {
		return list;
	}

	public void setList(List<AtlasPicture> list) {
		this.list = list;
	}

	public String getWeburl() {
		return weburl;
	}

	public void setWeburl(String weburl) {
		this.weburl = weburl;
	}

	public static class AtlasPicture extends Entry {
		private static final long serialVersionUID = 1L;
		private int articleId = 0;
		private String url = "";
		private String desc = "";
		private String title = "";

		public int getArticleId() {
			return articleId;
		}

		public void setArticleId(int articleId) {
			this.articleId = articleId;
		}

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

	}
}
