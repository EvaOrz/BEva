package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.model.Cat.CatItem;
import cn.com.modernmediaslate.model.Entry;

/**
 * 独立栏目
 * 
 * @author ZhuQiao
 * 
 */
public class SoloColumn extends Entry {
	private static final long serialVersionUID = 1L;
	private List<SoloColumnItem> list = new ArrayList<SoloColumnItem>();

	public List<SoloColumnItem> getList() {
		return list;
	}

	public void setList(List<SoloColumnItem> list) {
		this.list = list;
	}

	public static class SoloColumnItem extends CatItem {
		private static final long serialVersionUID = 1L;
		private List<SoloColumnChild> list = new ArrayList<SoloColumnChild>();

		public List<SoloColumnChild> getList() {
			return list;
		}

		public void setList(List<SoloColumnChild> list) {
			this.list = list;
		}
	}

	public static class SoloColumnChild extends Entry {
		private static final long serialVersionUID = 1L;
		public static String FULL_TYPE = "full";
		public static String SELF_TYPE = "self";
		
		private String name = "";
		private String color = "";// eg:25,67,171;转成16进制rgb
		private String type = "";// full显示所有数据；self显示自己的数据

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}
}
