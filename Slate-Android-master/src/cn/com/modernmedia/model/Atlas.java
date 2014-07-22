package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Í¼¼¯
 * 
 * @author ZhuQiao
 * 
 */
public class Atlas extends Entry {
	private static final long serialVersionUID = 1L;
	private int id;
	private String desc = "";
	private List<AtlasPicture> list = new ArrayList<AtlasPicture>();
	private String link = "";
	private String weburl = "";

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<AtlasPicture> getList() {
		return list;
	}

	public void setList(List<AtlasPicture> list) {
		this.list = list;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getWeburl() {
		return weburl;
	}

	public void setWeburl(String weburl) {
		this.weburl = weburl;
	}

	public static class AtlasPicture {
		private String url = "";
		private String desc = "";
		private String title = "";

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
