package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

/**
 * ��Ŀ
 * 
 * @author ZhuQiao
 * 
 */
public class Cat extends Entry {
	private static final long serialVersionUID = 1L;
	private List<CatItem> list = new ArrayList<CatItem>();

	public List<CatItem> getList() {
		return list;
	}

	public void setList(List<CatItem> list) {
		this.list = list;
	}

	public static class CatItem {
		private int id;// ��Ŀid
		private int issueId;
		private String name = "";// ��Ŀ����
		private String ename = "";// ��ĿӢ������
		private String cname = "";// ��Ŀ��������
		private int color;// ��Ŀɫ����ɫ
		private int displayType;// ֻ��ʾ1,3
		private int complete;
		private String arrow;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getIssueId() {
			return issueId;
		}

		public void setIssueId(int issueId) {
			this.issueId = issueId;
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

		public int getDisplayType() {
			return displayType;
		}

		public void setDisplayType(int displayType) {
			this.displayType = displayType;
		}

		public int getComplete() {
			return complete;
		}

		public void setComplete(int complete) {
			this.complete = complete;
		}

		public String getArrow() {
			return arrow;
		}

		public void setArrow(String arrow) {
			this.arrow = arrow;
		}
	}

}
