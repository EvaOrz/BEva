package cn.com.modernmedia.views.model;

import cn.com.modernmedia.views.util.V;

/**
 * 首页导航栏参数
 * 
 * @author user
 * 
 */
public class IndexNavParm {
	private String type = V.BUSINESS;
	private String nav_column = "";// 导航栏左边按钮
	private String nav_fav = "";// 导航栏右边按钮
	private String nav_shadow = "";// 导航栏底下横线
	private String nav_bg = "";// 导航栏背景
	private String nav_title_img = ""; // 栏目名为图片时
	private int nav_title_img_top_padding;// 栏目名为图片时top padding;按1280换算
	private int show_title = 1;// 显示栏目名；0，不显示；默认1
	private IndexNavTitleParm titleParm = new IndexNavTitleParm();// 艺术新闻标题不一样

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNav_column() {
		return nav_column;
	}

	public void setNav_column(String nav_column) {
		this.nav_column = nav_column;
	}

	public String getNav_fav() {
		return nav_fav;
	}

	public void setNav_fav(String nav_fav) {
		this.nav_fav = nav_fav;
	}

	public String getNav_shadow() {
		return nav_shadow;
	}

	public void setNav_shadow(String nav_shadow) {
		this.nav_shadow = nav_shadow;
	}

	public String getNav_bg() {
		return nav_bg;
	}

	public void setNav_bg(String nav_bg) {
		this.nav_bg = nav_bg;
	}

	public int getShow_title() {
		return show_title;
	}

	public void setShow_title(int show_title) {
		this.show_title = show_title;
	}

	public IndexNavTitleParm getTitleParm() {
		return titleParm;
	}

	public void setTitleParm(IndexNavTitleParm titleParm) {
		this.titleParm = titleParm;
	}

	public String getNav_title_img() {
		return nav_title_img;
	}

	public void setNav_title_img(String nav_title_img) {
		this.nav_title_img = nav_title_img;
	}

	public int getNav_title_img_top_padding() {
		return nav_title_img_top_padding;
	}

	public void setNav_title_img_top_padding(int nav_title_img_top_padding) {
		this.nav_title_img_top_padding = nav_title_img_top_padding;
	}

	/**
	 * 标题属性
	 * 
	 * @author user
	 * 
	 */
	public static class IndexNavTitleParm {
		private int title_top_padding;// 标题padding;按1280换算
		private int title_textsize;// 标题大小
		private String title_color = "";// 标题颜色
		private int show_shadow = 0;// 显示阴影;0否1是

		public int getTitle_top_padding() {
			return title_top_padding;
		}

		public void setTitle_top_padding(int title_top_padding) {
			this.title_top_padding = title_top_padding;
		}

		public int getTitle_textsize() {
			return title_textsize;
		}

		public void setTitle_textsize(int title_textsize) {
			this.title_textsize = title_textsize;
		}

		public String getTitle_color() {
			return title_color;
		}

		public void setTitle_color(String title_color) {
			this.title_color = title_color;
		}

		public int getShow_shadow() {
			return show_shadow;
		}

		public void setShow_shadow(int show_shadow) {
			this.show_shadow = show_shadow;
		}

	}
}
