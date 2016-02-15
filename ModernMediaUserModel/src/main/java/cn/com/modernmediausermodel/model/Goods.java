package cn.com.modernmediausermodel.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmediaslate.model.Entry;

/**
 * 商品
 * 
 * @author user
 * 
 */
public class Goods extends Entry {
	private static final long serialVersionUID = 1L;
	private List<GoodsItem> list = new ArrayList<Goods.GoodsItem>();

	public List<GoodsItem> getList() {
		return list;
	}

	public void setList(List<GoodsItem> list) {
		this.list = list;
	}

	public static class GoodsItem extends Entry {
		private static final long serialVersionUID = 1L;
		private String id = "";// 商品id
		private int appId;// 应用id，暂时无用，可能以后会显示来自"商周、周末画报"..使用
		private String name = "";// 商品名称
		private String categeryname = "";// 商品分类名称
		private String desc = ""; // 描述
		private int price = 0;// 兑换所需金币数

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public int getAppId() {
			return appId;
		}

		public void setAppId(int appId) {
			this.appId = appId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCategeryname() {
			return categeryname;
		}

		public void setCategeryname(String categeryname) {
			this.categeryname = categeryname;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public int getPrice() {
			return price;
		}

		public void setPrice(int price) {
			this.price = price;
		}
	}
}
