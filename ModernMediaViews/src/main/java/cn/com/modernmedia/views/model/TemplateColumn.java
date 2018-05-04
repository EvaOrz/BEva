package cn.com.modernmedia.views.model;

import cn.com.modernmediaslate.model.Entry;

/**
 * 栏目列表模板
 * 
 * @author zhuqiao
 * 
 */
public class TemplateColumn extends Entry {
	private static final long serialVersionUID = 1L;
	private TemplateColumnHead head = new TemplateColumnHead();
	private TemplateColumnList list = new TemplateColumnList();
	private String background = "";
	private String about = "";// 关于
	private String recommend = "";// 推荐

	public TemplateColumnHead getHead() {
		return head;
	}

	public void setHead(TemplateColumnHead head) {
		this.head = head;
	}

	public TemplateColumnList getList() {
		return list;
	}

	public void setList(TemplateColumnList list) {
		this.list = list;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
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

	/**
	 * headview
	 * 
	 * @author zhuqiao
	 * 
	 */
	public static class TemplateColumnHead extends Entry {
		private static final long serialVersionUID = 1L;
		private String data = "";
		private int hold = 0;// 是否固定
		private int need_calculate_height = 1;// 是否需要重新计算高度

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public int getHold() {
			return hold;
		}

		public void setHold(int hold) {
			this.hold = hold;
		}

		public int getNeed_calculate_height() {
			return need_calculate_height;
		}

		public void setNeed_calculate_height(int need_calculate_height) {
			this.need_calculate_height = need_calculate_height;
		}

	}

	/**
	 * 列表item
	 * 
	 * @author zhuqiao
	 * 
	 */
	public static class TemplateColumnList extends Entry {
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
