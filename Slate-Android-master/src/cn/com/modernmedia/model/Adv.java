package cn.com.modernmedia.model;


/**
 * 广告
 * 
 * @author ZhuQiao
 * 
 */
public class Adv extends Entry {
	private static final long serialVersionUID = 1L;
	private AdvProperty advProperty = new AdvProperty();
	private ColumnAdv columnAdv = new ColumnAdv();
	private boolean isExpired = false;

	public AdvProperty getAdvProperty() {
		return advProperty;
	}

	public void setAdvProperty(AdvProperty advProperty) {
		this.advProperty = advProperty;
	}

	public ColumnAdv getColumnAdv() {
		return columnAdv;
	}

	public void setColumnAdv(ColumnAdv columnAdv) {
		this.columnAdv = columnAdv;
	}

	public boolean isExpired() {
		return isExpired;
	}

	public void setExpired(boolean isExpired) {
		this.isExpired = isExpired;
	}

	/**
	 * 文章属性
	 * 
	 * @author ZhuQiao
	 * 
	 */
	public static class AdvProperty extends Entry {
		private static final long serialVersionUID = 1L;
		private int isadv = 0;// 是否是广告；1.是；otherwi 不是

		public int getIsadv() {
			return isadv;
		}

		public void setIsadv(int isadv) {
			this.isadv = isadv;
		}

	}

	/**
	 * 广告详情
	 * 
	 * @author ZhuQiao
	 * 
	 */
	public static class ColumnAdv extends Entry {
		private static final long serialVersionUID = 1L;
		private int id;
		private String url = "";// 图片路径
		private String link = "";// uri
		private String startTime = "";
		private String endTime = "";

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
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

	}
}
