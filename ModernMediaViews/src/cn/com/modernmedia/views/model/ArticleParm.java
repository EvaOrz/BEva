package cn.com.modernmedia.views.model;

/**
 * 文章页数据
 * 
 * @author jiancong
 * 
 */
public class ArticleParm {
	private String type = ""; // 应用类型
	private String nav_back = ""; // 返回按钮
	private String nav_fav = ""; // 未收藏图片
	private String nav_faved = ""; // 已收藏图片
	private String nav_font_size = ""; // 调整字体大小的图片
	private String nav_share = ""; // 用于分享的图片
	private String nav_bg = ""; // 背景
	private String placeholder = ""; // 占位图
	private int isAlignToNav = 0; // 文章顶部是否和导航栏对齐， 0：不对齐，在导航栏下； 1:对齐
	private int has_user = 0;// 是否可绑定用户模块;0，否；1.是
	private int bgIsTransparent = 0; // 文章背景是否透明， 0：不透明；1：透明

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNav_back() {
		return nav_back;
	}

	public void setNav_back(String nav_back) {
		this.nav_back = nav_back;
	}

	public String getNav_fav() {
		return nav_fav;
	}

	public void setNav_fav(String nav_fav) {
		this.nav_fav = nav_fav;
	}

	public String getNav_faved() {
		return nav_faved;
	}

	public void setNav_faved(String nav_faved) {
		this.nav_faved = nav_faved;
	}

	public String getNav_font_size() {
		return nav_font_size;
	}

	public void setNav_font_size(String nav_font_size) {
		this.nav_font_size = nav_font_size;
	}

	public String getNav_share() {
		return nav_share;
	}

	public void setNav_share(String nav_share) {
		this.nav_share = nav_share;
	}

	public String getNav_bg() {
		return nav_bg;
	}

	public void setNav_bg(String nav_bg) {
		this.nav_bg = nav_bg;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public int getHas_user() {
		return has_user;
	}

	public void setHas_user(int has_user) {
		this.has_user = has_user;
	}

	public int getIsAlignToNav() {
		return isAlignToNav;
	}

	public void setIsAlignToNav(int isAlignToNav) {
		this.isAlignToNav = isAlignToNav;
	}

	public int getBgIsTransparent() {
		return bgIsTransparent;
	}

	public void setBgIsTransparent(int bgIsTransparent) {
		this.bgIsTransparent = bgIsTransparent;
	}

}
