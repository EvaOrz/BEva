package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import cn.com.modernmediaslate.model.Entry;

/**
 * 栏目
 * 
 * @author ZhuQiao
 * 
 */
@SuppressLint("UseSparseArrays")
public class Cat extends Entry {
	private static final long serialVersionUID = 1L;
	/**
	 * 栏目中父栏目和子栏目有两张图，根据SPLITE分隔(第一个元素为子栏目，第二个元素为父栏目)
	 */
	public static final String SPLITE = "###";
	private List<CatItem> list = new ArrayList<CatItem>();
	/**
	 * 栏目id列表(用来判断是否需要显示广告)
	 */
	private List<String> idList = new ArrayList<String>();

	public List<CatItem> getList() {
		return list;
	}

	public void setList(List<CatItem> list) {
		this.list = list;
	}

	public List<String> getIdList() {
		return idList;
	}

	public void setIdList(List<String> idList) {
		this.idList = idList;
	}

	public static class CatItem extends Entry {
		private static final long serialVersionUID = 1L;
		private int id;// 栏目id
		private int issueId;
		private String name = "";// 栏目名称
		private String ename = "";// 栏目英文名称
		private String cname = "";// 栏目中文名称
		private int color;// 栏目色条颜色
		private int displayType;// 只显示1,3
		private int complete;
		private String arrow;
		private int parentId = -1;// 父catid
		private int haveChildren = 0;// 是否有子栏目。0.无。1.有
		private String tagname = "";// 子栏目显示在tag上的名字
		private String link = "";// slate
		private boolean isSoloCat;// 是否是独立栏目
		private CatProperty property = new CatProperty();// 弃用displayType

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

		public int getParentId() {
			return parentId;
		}

		public void setParentId(int parentId) {
			this.parentId = parentId;
		}

		public int getHaveChildren() {
			return haveChildren;
		}

		public void setHaveChildren(int haveChildren) {
			this.haveChildren = haveChildren;
		}

		public String getTagname() {
			return tagname;
		}

		public void setTagname(String tagname) {
			this.tagname = tagname;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public boolean isSoloCat() {
			return isSoloCat;
		}

		public void setSoloCat(boolean isSoloCat) {
			this.isSoloCat = isSoloCat;
		}

		public CatProperty getProperty() {
			return property;
		}

		public void setProperty(CatProperty property) {
			this.property = property;
		}

	}

	public static class CatProperty extends Entry {
		private static final long serialVersionUID = 1L;
		private int noColumn;// 0.显示。1.不显示
		private int noMenuBar;
		private int noLeftMenu;

		public int getNoColumn() {
			return noColumn;
		}

		public void setNoColumn(int noColumn) {
			this.noColumn = noColumn;
		}

		public int getNoMenuBar() {
			return noMenuBar;
		}

		public void setNoMenuBar(int noMenuBar) {
			this.noMenuBar = noMenuBar;
		}

		public int getNoLeftMenu() {
			return noLeftMenu;
		}

		public void setNoLeftMenu(int noLeftMenu) {
			this.noLeftMenu = noLeftMenu;
		}

	}
}
