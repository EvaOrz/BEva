package cn.com.modernmedia.views.model;

import cn.com.modernmediaslate.model.Entry;

/**
 * 图集模板
 * 
 * @author jiancong
 * 
 */
public class TemplateAtlas extends Entry {
	private static final long serialVersionUID = 1L;
	private String data = "";
	private TemplatePagerItem pagerItem = new TemplatePagerItem();

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public TemplatePagerItem getPagerItem() {
		return pagerItem;
	}

	public void setPagerItem(TemplatePagerItem pagerItem) {
		this.pagerItem = pagerItem;
	}

	/**
	 * ViewPager item
	 * 
	 * @author jiancong
	 * 
	 */
	public static class TemplatePagerItem extends Entry {
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
