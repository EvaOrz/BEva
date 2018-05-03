package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmediaslate.model.Entry;

/**
 * 用户订阅列表
 * 
 * @author jiancong
 * 
 */
public class SubscribeOrderList extends Entry {
	private static final long serialVersionUID = 1L;
	// 用户id
	private String uid = "";
	// 应用id
	private int appId;
	// 栏目列表
	private List<SubscribeColumn> columnList = new ArrayList<SubscribeColumn>();

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public List<SubscribeColumn> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<SubscribeColumn> columnList) {
		this.columnList = columnList;
	}

	/**
	 * 获取已订阅的栏目名称+父栏目名称
	 * 
	 * @return
	 */
	public List<String> getTagNameList() {
		List<String> tagNameList = new ArrayList<String>();
		if (columnList.size() == 0)
			return tagNameList;
		for (SubscribeColumn column : columnList) {
			String tagName = column.getName();
			if (column.getParent() != null && column.getParent().length() > 0) {
				tagName = tagName + "&" + column.getParent();
			}
			if (!tagNameList.contains(tagName))
				tagNameList.add(tagName);
		}
		return tagNameList;
	}

	/**
	 * 栏目tag及其父tag
	 * 
	 * @author jiancong
	 * 
	 */
	public static class SubscribeColumn extends Entry {
		private static final long serialVersionUID = 1L;
		// 栏目的tagname
		private String name = "";
		// 该栏目为子栏目时，其父栏目tagname
		private String parent = "";
		private int isDelete = 0;// isDelete == 1:删除栏目

		public SubscribeColumn() {
		}

		public SubscribeColumn(String name, String parent, int isDelete) {
			this.name = name;
			this.parent = parent;
			this.isDelete = isDelete;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getParent() {
			return parent;
		}

		public void setParent(String parent) {
			this.parent = parent;
		}

		public int getIsDelete() {
			return isDelete;
		}

		public void setIsDelete(int isDelete) {
			this.isDelete = isDelete;
		}
	}

}
