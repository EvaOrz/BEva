package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 栏目
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
		private int id;// 栏目id
		private int issueId;
		private String name = "";// 栏目名称
		private String ename = "";// 栏目英文名称
		private String cname = "";// 栏目中文名称
		private int color;// 栏目色条颜色
		private int displayType;// 只显示1,3
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
