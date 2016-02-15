package cn.com.modernmedia.views.model;

import cn.com.modernmediaslate.model.Entry;

/**
 * 文章模板
 * 
 * @author jiancong
 * 
 */
public class TemplateAriticle extends Entry {
	private static final long serialVersionUID = 1L;
	private int isAlignToNav = 0; // 文章顶部是否和导航栏对齐， 0：不对齐，在导航栏下； 1:对齐
	private int has_user = 0;// 是否可绑定用户模块;0，否；1.是
	private int bgIsTransparent = 0; // 文章背景是否透明， 0：不透明；1：透明
	private TemplateArticleNavBar navBar = new TemplateArticleNavBar();

	public int getIsAlignToNav() {
		return isAlignToNav;
	}

	public void setIsAlignToNav(int isAlignToNav) {
		this.isAlignToNav = isAlignToNav;
	}

	public int getHas_user() {
		return has_user;
	}

	public void setHas_user(int has_user) {
		this.has_user = has_user;
	}

	public int getBgIsTransparent() {
		return bgIsTransparent;
	}

	public void setBgIsTransparent(int bgIsTransparent) {
		this.bgIsTransparent = bgIsTransparent;
	}

	public TemplateArticleNavBar getNavBar() {
		return navBar;
	}

	public void setNavBar(TemplateArticleNavBar navBar) {
		this.navBar = navBar;
	}

	/**
	 * 导航栏
	 * 
	 * @author jiancong
	 * 
	 */
	public static class TemplateArticleNavBar extends Entry {
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
