package cn.com.modernmedia.views.model;

import cn.com.modernmediaslate.model.Entry;

/**
 * 栏目列表模板
 * 
 * @author zhuqiao
 * 
 */
public class TemplateFav extends Entry {
	private static final long serialVersionUID = 1L;
	private TemplateFavNavBar navBar = new TemplateFavNavBar();
	private TemplateFavList list = new TemplateFavList();
	private String background = "";

	public TemplateFavNavBar getNavBar() {
		return navBar;
	}

	public void setNavBar(TemplateFavNavBar navBar) {
		this.navBar = navBar;
	}

	public TemplateFavList getList() {
		return list;
	}

	public void setList(TemplateFavList list) {
		this.list = list;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	/**
	 * 导航栏
	 * 
	 * @author zhuqiao
	 * 
	 */
	public static class TemplateFavNavBar extends Entry {
		private static final long serialVersionUID = 1L;
		private String data = "";

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}
	}

	/**
	 * 列表item
	 * 
	 * @author zhuqiao
	 * 
	 */
	public static class TemplateFavList extends Entry {
		private static final long serialVersionUID = 1L;
		private String data = "";

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

	}
}
