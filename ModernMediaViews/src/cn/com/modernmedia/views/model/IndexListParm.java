package cn.com.modernmedia.views.model;

import android.text.TextUtils;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.unit.ImageScaleType;

/**
 * 首页列表适配器参数
 * 
 * @author user
 * 
 */
public class IndexListParm {
	public static final String HTTP_START = "uri://";

	private String host = "";// 图片host
	private String type = "";// 选取的类型
	private int item_position = 2;// 列表取服务器中的位置
	private String list_bg = "#FFFFFFFF";// 列表背景
	private String item_bg = "";// list item背景
	private String item_bg_select = "";// list item选中状态背景
	private String placeholder = "";// 占位图
	private String image_background = "";// 图片背景
	private String divider = "";// 分割线
	private String moreinstant = "";// 商周"更多"
	private String row = "";// 右箭头
	private String item_fav = ""; // 未收藏
	private String item_faved = ""; // 已收藏
	private String item_outline_img = ""; // outline 图标
	private String item_share = ""; // 分享
	private int item_show_fav = 0; // 是否显示收藏图标
	private int item_img_width; // image的宽(宽是固定的，用来换算图片高度)
	private int item_img_height; // image的高
	private int item_show_margin = 1; // 是否显示margin,默认显示
	private boolean is_picture = true;// 是否显示大图(商周显示缩略图)
	private String scale_type = ImageScaleType.FIT_XY;// 图片scaleType,默认fitxy
	private String item_title_bar_height = ""; // 原图高度,title高度(目前仅乐活用)
	private String item_title_bar_bg = ""; // item title所在view的背景图或者颜色(目前仅乐活用)
	private String item_date_bg = ""; // item 显示日期的view的背景图或者颜色(目前仅乐活用)
	private int title_size;// dp 标题标题大小
	private int desc_size;// dp 描述字体大小

	// ----焦点图
	private String head_type = "";// 焦点图的类型
	private int head_position = 1;// 焦点图取服务器中的位置
	private String head_placeholder = "";// 焦点图的占位图
	private int head_height;// 焦点图高度,按720换算
	private String head_dot = "";// 焦点图dot
	private String head_dot_active = "";// 焦点图选中dot
	private String head_title_bg = "";// 焦点图标题背景
	private int head_title_size;// title字体大小 dp
	private int head_title_bg_height;// 标题背景高度 按1280换算
	private int head_show_title = 1; // 焦点图标题是否显示，默认显示
	private int head_dot_type = 1; // 焦点图dot类型，0:使用现有图片，宽高自适应；1：drawable绘制，直径大小默认为5dp
	private String head_scale_type = ImageScaleType.FIT_XY;// 图片scaleType,默认fitxy

	// ----子栏目、独立栏目导航栏
	private String cat_list_type = V.BUSINESS;// 子栏目、独立栏目导航栏类型
	private int cat_list_hold = 1;// 0,作为listview的headview;1.固定在头部

	// ----process type
	// private String process_type = V.BUSINESS;//只能跟着应用走，因为在获取index时候就需要使用

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getItem_position() {
		return item_position;
	}

	public void setItem_position(int item_position) {
		this.item_position = item_position;
	}

	public String getList_bg() {
		return list_bg;
	}

	public void setList_bg(String list_bg) {
		this.list_bg = getPicUrl(list_bg);
	}

	public String getItem_bg() {
		return item_bg;
	}

	public void setItem_bg(String item_bg) {
		this.item_bg = getPicUrl(item_bg);
	}

	public String getItem_bg_select() {
		return item_bg_select;
	}

	public void setItem_bg_select(String item_bg_select) {
		this.item_bg_select = getPicUrl(item_bg_select);
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = getPicUrl(placeholder);
	}

	public String getImage_background() {
		return image_background;
	}

	public void setImage_background(String image_background) {
		this.image_background = getPicUrl(image_background);
	}

	public String getDivider() {
		return divider;
	}

	public void setDivider(String divider) {
		this.divider = getPicUrl(divider);
	}

	public String getMoreinstant() {
		return moreinstant;
	}

	public void setMoreinstant(String moreinstant) {
		this.moreinstant = getPicUrl(moreinstant);
	}

	public String getRow() {
		return row;
	}

	public void setRow(String row) {
		this.row = getPicUrl(row);
	}

	public String getItem_fav() {
		return item_fav;
	}

	public void setItem_fav(String item_fav) {
		this.item_fav = getPicUrl(item_fav);
	}

	public String getItem_faved() {
		return item_faved;
	}

	public void setItem_faved(String item_faved) {
		this.item_faved = getPicUrl(item_faved);
	}

	public String getItem_outline_img() {
		return item_outline_img;
	}

	public void setItem_outline_img(String item_outline_img) {
		this.item_outline_img = getPicUrl(item_outline_img);
	}

	public String getItem_share() {
		return item_share;
	}

	public void setItem_share(String item_share) {
		this.item_share = getPicUrl(item_share);
	}

	public int getItem_show_fav() {
		return item_show_fav;
	}

	public void setItem_show_fav(int item_show_fav) {
		this.item_show_fav = item_show_fav;
	}

	public int getItem_img_width() {
		return item_img_width;
	}

	public void setItem_img_width(int item_img_width) {
		this.item_img_width = item_img_width;
	}

	public int getItem_img_height() {
		return item_img_height;
	}

	public void setItem_img_height(int item_img_height) {
		this.item_img_height = item_img_height;
	}

	public int getItem_show_margin() {
		return item_show_margin;
	}

	public void setItem_show_margin(int item_show_margin) {
		this.item_show_margin = item_show_margin;
	}

	public boolean isIs_picture() {
		return is_picture;
	}

	public void setIs_picture(boolean is_picture) {
		this.is_picture = is_picture;
	}

	public String getScale_type() {
		return scale_type;
	}

	public void setScale_type(String scale_type) {
		this.scale_type = scale_type;
	}

	public String getItem_title_bar_height() {
		return item_title_bar_height;
	}

	public void setItem_title_bar_height(String item_title_bar_height) {
		this.item_title_bar_height = item_title_bar_height;
	}

	public String getItem_title_bar_bg() {
		return item_title_bar_bg;
	}

	public void setItem_title_bar_bg(String item_title_bar_bg) {
		this.item_title_bar_bg = getPicUrl(item_title_bar_bg);
	}

	public String getItem_date_bg() {
		return item_date_bg;
	}

	public void setItem_date_bg(String item_date_bg) {
		this.item_date_bg = getPicUrl(item_date_bg);
	}

	public int getTitle_size() {
		return title_size;
	}

	public void setTitle_size(int title_size) {
		this.title_size = title_size;
	}

	public int getDesc_size() {
		return desc_size;
	}

	public void setDesc_size(int desc_size) {
		this.desc_size = desc_size;
	}

	public String getHead_type() {
		return head_type;
	}

	public void setHead_type(String head_type) {
		this.head_type = head_type;
	}

	public int getHead_position() {
		return head_position;
	}

	public void setHead_position(int head_position) {
		this.head_position = head_position;
	}

	public String getHead_placeholder() {
		return head_placeholder;
	}

	public void setHead_placeholder(String head_placeholder) {
		this.head_placeholder = getPicUrl(head_placeholder);
	}

	public int getHead_height() {
		return head_height;
	}

	public void setHead_height(int head_height) {
		this.head_height = head_height;
	}

	public String getHead_dot() {
		return head_dot;
	}

	public void setHead_dot(String head_dot) {
		this.head_dot = getPicUrl(head_dot);
	}

	public String getHead_dot_active() {
		return head_dot_active;
	}

	public void setHead_dot_active(String head_dot_active) {
		this.head_dot_active = getPicUrl(head_dot_active);
	}

	public String getHead_title_bg() {
		return head_title_bg;
	}

	public void setHead_title_bg(String head_title_bg) {
		this.head_title_bg = getPicUrl(head_title_bg);
	}

	public int getHead_title_size() {
		return head_title_size;
	}

	public void setHead_title_size(int head_title_size) {
		this.head_title_size = head_title_size;
	}

	public int getHead_title_bg_height() {
		return head_title_bg_height;
	}

	public void setHead_title_bg_height(int head_title_bg_height) {
		this.head_title_bg_height = head_title_bg_height;
	}

	public int getHead_show_title() {
		return head_show_title;
	}

	public void setHead_show_title(int head_show_title) {
		this.head_show_title = head_show_title;
	}

	public int getHead_dot_type() {
		return head_dot_type;
	}

	public void setHead_dot_type(int head_dot_type) {
		this.head_dot_type = head_dot_type;
	}

	public String getHead_scale_type() {
		return head_scale_type;
	}

	public void setHead_scale_type(String head_scale_type) {
		this.head_scale_type = head_scale_type;
	}

	public String getCat_list_type() {
		return cat_list_type;
	}

	public void setCat_list_type(String cat_list_type) {
		this.cat_list_type = cat_list_type;
	}

	public int getCat_list_hold() {
		return cat_list_hold;
	}

	public void setCat_list_hold(int cat_list_hold) {
		this.cat_list_hold = cat_list_hold;
	}

	/**
	 * 获取图片绝对地址
	 * 
	 * @param pic
	 */
	private String getPicUrl(String pic) {
		if (TextUtils.isEmpty(pic) || !pic.startsWith(HTTP_START))
			return pic;
		String arr[] = pic.split(HTTP_START);
		if (arr != null && arr.length == 2) {
			if (ConstData.IS_DEBUG == 1) {
				return host + arr[1] + "?11";
			}
			return host + arr[1];
		}
		return pic;
	}
}
