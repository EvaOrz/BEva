package cn.com.modernmedia.views.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.view.View;
import cn.com.modernmediaslate.model.Entry;

/**
 * 首页模板
 * 
 * @author zhuqiao
 * 
 */
public class Template extends Entry {
	private static final long serialVersionUID = 1L;
	private int version;// 模板版本号
	private String supportVersions = "";// 支持的模板版本号
	private String host = "";// 图片域名
	private TemplateHead head = new TemplateHead();// 焦点图
	private TemplateHeadItem headItem = new TemplateHeadItem();// 焦点图item
	private TemplateList list = new TemplateList();
	private TemplateCatHead catHead = new TemplateCatHead();// 子栏目导航栏列表
	private TemplateListHead listHead = new TemplateListHead();// 列表headview

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getSupportVersions() {
		return supportVersions;
	}

	public void setSupportVersions(String supportVersions) {
		this.supportVersions = supportVersions;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public TemplateHead getHead() {
		return head;
	}

	public void setHead(TemplateHead head) {
		this.head = head;
	}

	public TemplateHeadItem getHeadItem() {
		return headItem;
	}

	public void setHeadItem(TemplateHeadItem headItem) {
		this.headItem = headItem;
	}

	public TemplateList getList() {
		return list;
	}

	public void setList(TemplateList list) {
		this.list = list;
	}

	public TemplateCatHead getCatHead() {
		return catHead;
	}

	public void setCatHead(TemplateCatHead catHead) {
		this.catHead = catHead;
	}

	public TemplateListHead getListHead() {
		return listHead;
	}

	public void setListHead(TemplateListHead listHead) {
		this.listHead = listHead;
	}

	/**
	 * 焦点图模板
	 * 
	 * @author zhuqiao
	 * 
	 */
	public static class TemplateHead extends Entry {
		private static final long serialVersionUID = 1L;
		private List<Integer> position = new ArrayList<Integer>();
		private String data = "";

		public List<Integer> getPosition() {
			return position;
		}

		public void setPosition(List<Integer> position) {
			this.position = position;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

	}

	/**
	 * 焦点图item模板
	 * 
	 * @author zhuqiao
	 * 
	 */
	public static class TemplateHeadItem extends Entry {
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
	 * 列表模板
	 * 
	 * @author zhuqiao
	 * 
	 */
	public static class TemplateList extends Entry {
		private static final long serialVersionUID = 1L;
		private List<Integer> position = new ArrayList<Integer>();
		@SuppressLint("UseSparseArrays")
		/**
		 * key:style对应的模板.0.titlebar;1小图文字;2.无图文字;3.大图文字;4.广告;5.跑马灯; value:列表item模板
		 */
		private Map<Integer, String> map = new HashMap<Integer, String>();
		/**
		 * 如果找不到对应的模板，那么取默认模板
		 */
		private String default_data = "";
		private int one_line_count = 1;// 一行显示多少item
		private int is_gallery = 0;// 是否是iweekly图集
		private int show_marquee = 0;// 是否显示跑马灯

		public List<Integer> getPosition() {
			return position;
		}

		public void setPosition(List<Integer> position) {
			this.position = position;
		}

		public Map<Integer, String> getMap() {
			return map;
		}

		public void setMap(Map<Integer, String> map) {
			this.map = map;
		}

		public String getDefault_data() {
			return default_data;
		}

		public void setDefault_data(String default_data) {
			this.default_data = default_data;
		}

		public int getOne_line_count() {
			return one_line_count;
		}

		public void setOne_line_count(int one_line_count) {
			this.one_line_count = one_line_count;
		}

		public int getIs_gallery() {
			return is_gallery;
		}

		public void setIs_gallery(int is_gallery) {
			this.is_gallery = is_gallery;
		}

		public int getShow_marquee() {
			return show_marquee;
		}

		public void setShow_marquee(int show_marquee) {
			this.show_marquee = show_marquee;
		}

	}

	/**
	 * 独立栏目/子栏目的栏目列表
	 * 
	 * @author zhuqiao
	 * 
	 */
	public static class TemplateCatHead extends Entry {
		private static final long serialVersionUID = 1L;
		private int cat_list_hold = 1;// 0,作为listview的headview;1.固定在头部
		private String color = "";
		private String data = "";

		public int getCat_list_hold() {
			return cat_list_hold;
		}

		public void setCat_list_hold(int cat_list_hold) {
			this.cat_list_hold = cat_list_hold;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

	}

	/**
	 * 列表headview
	 * 
	 * @author zhuqiao
	 * 
	 */
	public static class TemplateListHead extends Entry {
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
	 * grid形式列表数据
	 * 
	 * @author zhuqiao
	 * 
	 */
	public static class GridItem {
		private int index;// 位置,从0开始
		private View view;

		public GridItem(int index, View view) {
			this.index = index;
			this.view = view;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public View getView() {
			return view;
		}

		public void setView(View view) {
			this.view = view;
		}

	}
}
