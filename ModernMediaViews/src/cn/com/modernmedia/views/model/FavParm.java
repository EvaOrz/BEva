package cn.com.modernmedia.views.model;

/**
 * 收藏页参数
 * 
 * @author user
 * 
 */
public class FavParm {
	private String background = "";// 背景色
	private String bar_background = "";// titlebar背景色
	private String top_image = "";// 最上面的图片
	private String head_divider = "";// headview底部分割线
	private String divider = "";// listview分割线
	// ---item
	private String row = "";// fav_item箭头
	private String row_img = ""; // 箭头，是一个单独的箭头图片，而row可以看作是一个包含箭头的背景图片，两者只能出现一种
	private String fav_item_background = "";// fav_item背景
	private String title_color = "";// 标题颜色
	private String text_color = "";// 字体颜色
	private int margin = 1;// 0item没有间隔;默认1

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getBar_background() {
		return bar_background;
	}

	public void setBar_background(String bar_background) {
		this.bar_background = bar_background;
	}

	public String getRow() {
		return row;
	}

	public void setRow(String row) {
		this.row = row;
	}

	public String getFav_item_background() {
		return fav_item_background;
	}

	public void setFav_item_background(String fav_item_background) {
		this.fav_item_background = fav_item_background;
	}

	public String getTitle_color() {
		return title_color;
	}

	public void setTitle_color(String title_color) {
		this.title_color = title_color;
	}

	public String getDivider() {
		return divider;
	}

	public void setDivider(String divider) {
		this.divider = divider;
	}

	public int getMargin() {
		return margin;
	}

	public void setMargin(int margin) {
		this.margin = margin;
	}

	public String getText_color() {
		return text_color;
	}

	public void setText_color(String text_color) {
		this.text_color = text_color;
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

	public String getRow_img() {
		return row_img;
	}

	public void setRow_img(String row_img) {
		this.row_img = row_img;
	}

}
