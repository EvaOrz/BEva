package cn.com.modernmedia.views.model;

import cn.com.modernmedia.util.ImageScaleType;

/**
 * 栏目参数
 * 
 * @author user
 * 
 */
public class ColumnParm {
	private String type = "";// business..
	private String column_bg = "";// 栏目背景
	private String logo = "";// 栏目页logo
	private String logo_size = "";// logo大小(原图大小,以","分隔;PS:如果width=-1,那么高不做等比换算)
	private String logo_scale_type = ImageScaleType.FIT_XY;// logo的scaletype
	private String logo_bg = "";// logo背景
	private String top_image = "";// 最上面的图片
	private String head_divider = "";// headview底部分割线
	private String divider = "";// listview divider
	// ---item
	private String first_item_bg = ""; // 第一个item的未选择状态,如果没值则使用item_bg的值
	private String item_bg = "";// 未选择状态
	private String item_bg_select = "";// 选择状态
	private String name_color = "";// 名称颜色
	private String name_color_select = "";// 选中状态下名称颜色
	private String row = "";// 箭头
	private String row_img = ""; // 箭头，是一个单独的箭头图片，而row可以看作是一个包含箭头的背景图片，两者只能出现一种
	private int show_color = 0;// 1.显示（商周栏目有颜色btn）;0默认FALSE
	private int show_margin = 0;// 1.显示（商周栏目列表有间隔）;0默认FALSE
	private String about = "";// 关于图片（如果不为空，显示关于）
	private String recommend = "";// 应用推荐(如果不为空，显示推荐)
	private int item_height = 0; // item高度,根据1280换算
	private int item_margin_left = 0; // item起始项距左边的距离
	private int name_size = 0; // item文字大小

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getColumn_bg() {
		return column_bg;
	}

	public void setColumn_bg(String column_bg) {
		this.column_bg = column_bg;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getLogo_size() {
		return logo_size;
	}

	public void setLogo_size(String logo_size) {
		this.logo_size = logo_size;
	}

	public String getLogo_scale_type() {
		return logo_scale_type;
	}

	public void setLogo_scale_type(String logo_scale_type) {
		this.logo_scale_type = logo_scale_type;
	}

	public String getLogo_bg() {
		return logo_bg;
	}

	public void setLogo_bg(String logo_bg) {
		this.logo_bg = logo_bg;
	}

	public String getTop_image() {
		return top_image;
	}

	public void setTop_image(String top_image) {
		this.top_image = top_image;
	}

	public String getHead_divider() {
		return head_divider;
	}

	public void setHead_divider(String head_divider) {
		this.head_divider = head_divider;
	}

	public String getDivider() {
		return divider;
	}

	public void setDivider(String divider) {
		this.divider = divider;
	}

	public String getItem_bg() {
		return item_bg;
	}

	public void setItem_bg(String item_bg) {
		this.item_bg = item_bg;
	}

	public String getItem_bg_select() {
		return item_bg_select;
	}

	public void setItem_bg_select(String item_bg_select) {
		this.item_bg_select = item_bg_select;
	}

	public String getName_color() {
		return name_color;
	}

	public void setName_color(String name_color) {
		this.name_color = name_color;
	}

	public String getName_color_select() {
		return name_color_select;
	}

	public void setName_color_select(String name_color_select) {
		this.name_color_select = name_color_select;
	}

	public String getRow() {
		return row;
	}

	public void setRow(String row) {
		this.row = row;
	}

	public int getShow_color() {
		return show_color;
	}

	public void setShow_color(int show_color) {
		this.show_color = show_color;
	}

	public int getShow_margin() {
		return show_margin;
	}

	public void setShow_margin(int show_margin) {
		this.show_margin = show_margin;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getRecommend() {
		return recommend;
	}

	public void setRecommend(String recommend) {
		this.recommend = recommend;
	}

	public String getFirst_item_bg() {
		return first_item_bg;
	}

	public void setFirst_item_bg(String first_item_bg) {
		this.first_item_bg = first_item_bg;
	}

	public String getRow_img() {
		return row_img;
	}

	public void setRow_img(String row_img) {
		this.row_img = row_img;
	}

	public int getItem_height() {
		return item_height;
	}

	public void setItem_height(int item_height) {
		this.item_height = item_height;
	}

	public int getItem_margin_left() {
		return item_margin_left;
	}

	public void setItem_margin_left(int item_margin_left) {
		this.item_margin_left = item_margin_left;
	}

	public int getName_size() {
		return name_size;
	}

	public void setName_size(int name_size) {
		this.name_size = name_size;
	}

}
