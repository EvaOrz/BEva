package cn.com.modernmedia.views.model;

/**
 * 期刊列表参数
 * 
 * @author jiancong
 * 
 */
public class IssueListParm {
	private String type = ""; // 应用类型
	private String issue_bg = ""; // 背景
	private String placeholder = ""; // 期刊封面占位图
	private String footer_bg = ""; // list footer(加载更多)背景
	private String footer_text_color = ""; // list footer(加载更多)字体颜色
	private String item_title_color = ""; // 期刊标题颜色
	private String item_desc_color = ""; // 期刊日期字体颜色

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIssue_bg() {
		return issue_bg;
	}

	public void setIssue_bg(String issue_bg) {
		this.issue_bg = issue_bg;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public String getItem_title_color() {
		return item_title_color;
	}

	public void setItem_title_color(String item_title_color) {
		this.item_title_color = item_title_color;
	}

	public String getItem_desc_color() {
		return item_desc_color;
	}

	public void setItem_desc_color(String item_desc_color) {
		this.item_desc_color = item_desc_color;
	}

	public String getFooter_bg() {
		return footer_bg;
	}

	public void setFooter_bg(String footer_bg) {
		this.footer_bg = footer_bg;
	}

	public String getFooter_text_color() {
		return footer_text_color;
	}

	public void setFooter_text_color(String footer_text_color) {
		this.footer_text_color = footer_text_color;
	}
}
