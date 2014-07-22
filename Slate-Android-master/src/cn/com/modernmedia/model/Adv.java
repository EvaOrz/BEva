package cn.com.modernmedia.model;


/**
 * ���
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
	 * ��������
	 * 
	 * @author ZhuQiao
	 * 
	 */
	public static class AdvProperty extends Entry {
		private static final long serialVersionUID = 1L;
		private int isadv = 0;// �Ƿ��ǹ�棻1.�ǣ�otherwi ����

		public int getIsadv() {
			return isadv;
		}

		public void setIsadv(int isadv) {
			this.isadv = isadv;
		}

	}

	/**
	 * �������
	 * 
	 * @author ZhuQiao
	 * 
	 */
	public static class ColumnAdv extends Entry {
		private static final long serialVersionUID = 1L;
		private int id;
		private String url = "";// ͼƬ·��
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
