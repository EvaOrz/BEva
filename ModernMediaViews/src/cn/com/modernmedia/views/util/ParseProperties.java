package cn.com.modernmedia.views.util;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import cn.com.modernmedia.util.ImageScaleType;
import cn.com.modernmedia.views.model.ArticleParm;
import cn.com.modernmedia.views.model.AtlasParm;
import cn.com.modernmedia.views.model.ColumnParm;
import cn.com.modernmedia.views.model.FavParm;
import cn.com.modernmedia.views.model.IndexListParm;
import cn.com.modernmedia.views.model.IndexNavParm;
import cn.com.modernmedia.views.model.IssueListParm;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.PropertyListParser;

/**
 * 解析配置文件
 * 
 * @author user
 * 
 */
public class ParseProperties {
	private static Context mContext;
	private static ParseProperties instance = null;

	private ParseProperties(Context context) {
		mContext = context;
	}

	public static ParseProperties getInstance(Context context) {
		mContext = context;
		if (instance == null)
			instance = new ParseProperties(context);
		return instance;
	}

	/**
	 * 解析首页导航栏
	 * 
	 * @return
	 */
	public IndexNavParm parseIndexNav() {
		return parseNav("index_nav.plist");
	}

	/**
	 * 解析关于页导航栏
	 * 
	 * @return
	 */
	public IndexNavParm parseAboutNav() {
		return parseNav("about_nav.plist");

	}

	/**
	 * 解析导航栏
	 * 
	 * @param name
	 *            资源名
	 * @return
	 */
	private IndexNavParm parseNav(String name) {
		IndexNavParm parm = new IndexNavParm();
		InputStream is = null;
		try {
			is = mContext.getAssets().open(name);
			NSDictionary rootDic = (NSDictionary) PropertyListParser.parse(is);
			parm.setType(getStringValue(rootDic, "type", V.BUSINESS));
			parm.setNav_column(getStringValue(rootDic, "nav_column", ""));
			parm.setNav_fav(getStringValue(rootDic, "nav_fav", ""));
			parm.setNav_shadow(getStringValue(rootDic, "nav_shadow", ""));
			parm.setNav_bg(getStringValue(rootDic, "nav_bg", ""));
			parm.setNav_title_img(getStringValue(rootDic, "nav_title_img", ""));
			parm.setNav_title_img_top_padding(getIntValue(rootDic,
					"nav_title_img_top_padding", 0));
			parm.setShow_title(getIntValue(rootDic, "show_title", 1));
			parm.getTitleParm().setTitle_top_padding(
					getIntValue(rootDic, "title_top_padding", 0));
			parm.getTitleParm().setTitle_textsize(
					getIntValue(rootDic, "title_textsize", 0));
			parm.getTitleParm().setTitle_color(
					getStringValue(rootDic, "title_color", ""));
			parm.getTitleParm().setShow_shadow(
					getIntValue(rootDic, "show_shadow", 0));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return parm;
	}

	/**
	 * 解析首页列表
	 * 
	 * @return
	 */
	public IndexListParm parseIndexList() {
		IndexListParm parm = new IndexListParm();
		InputStream is = null;
		try {
			is = mContext.getAssets().open("index_list.plist");
			NSDictionary rootDic = (NSDictionary) PropertyListParser.parse(is);
			parm.setType(getStringValue(rootDic, "type", ""));
			parm.setItem_position(getIntValue(rootDic, "item_position", 2));
			parm.setList_bg(getStringValue(rootDic, "list_bg", "#FFFFFFFF"));
			parm.setItem_bg(getStringValue(rootDic, "item_bg", ""));
			parm.setItem_bg_select(getStringValue(rootDic, "item_bg_select", ""));
			parm.setPlaceholder(getStringValue(rootDic, "placeholder", ""));
			parm.setImage_background(getStringValue(rootDic,
					"image_background", ""));
			parm.setDivider(getStringValue(rootDic, "divider", ""));
			parm.setMoreinstant(getStringValue(rootDic, "moreinstant", ""));

			parm.setHead_type(getStringValue(rootDic, "head_type", ""));
			parm.setHead_position(getIntValue(rootDic, "head_position", 1));
			parm.setHead_placeholder(getStringValue(rootDic,
					"head_placeholder", ""));
			parm.setHead_height(getIntValue(rootDic, "head_height", 0));
			parm.setHead_dot(getStringValue(rootDic, "head_dot", ""));
			parm.setHead_dot_active(getStringValue(rootDic, "head_dot_active",
					""));
			parm.setHead_title_bg(getStringValue(rootDic, "head_title_bg", ""));
			parm.setHead_show_title(getIntValue(rootDic, "head_show_title", 1));
			parm.setHead_dot_type(getIntValue(rootDic, "head_dot_type", 0));
			parm.setRow(getStringValue(rootDic, "row", ""));
			parm.setItem_fav(getStringValue(rootDic, "item_fav", ""));
			parm.setItem_faved(getStringValue(rootDic, "item_faved", ""));
			parm.setItem_share(getStringValue(rootDic, "item_share", ""));
			parm.setItem_show_fav(getIntValue(rootDic, "item_show_fav", 0));
			parm.setItem_img_width(getIntValue(rootDic, "item_img_width", 0));
			parm.setItem_img_height(getIntValue(rootDic, "item_img_height", 0));
			parm.setItem_show_margin(getIntValue(rootDic, "item_show_margin", 1));
			parm.setIs_picture(getIntValue(rootDic, "is_picture", 1) == 1);
			parm.setScale_type(getStringValue(rootDic, "scale_type",
					ImageScaleType.FIT_XY));
			parm.setItem_outline_img(getStringValue(rootDic,
					"item_outline_img", ""));
			parm.setItem_title_bar_height(getStringValue(rootDic,
					"item_title_bar_height", ""));
			parm.setItem_title_bar_bg(getStringValue(rootDic,
					"item_title_bar_bg", ""));
			parm.setItem_date_bg(getStringValue(rootDic, "item_date_bg", ""));
			parm.setCat_list_type(getStringValue(rootDic, "cat_list_type",
					V.BUSINESS));
			parm.setCat_list_hold(getIntValue(rootDic, "cat_list_hold", 1));
			parm.setProcess_type(getStringValue(rootDic, "process_type",
					V.BUSINESS));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return parm;
	}

	/**
	 * 解析栏目列表
	 * 
	 * @return
	 */
	public ColumnParm parseColumn() {
		ColumnParm parm = new ColumnParm();
		InputStream is = null;
		try {
			is = mContext.getAssets().open("column.plist");
			NSDictionary rootDic = (NSDictionary) PropertyListParser.parse(is);
			parm.setType(getStringValue(rootDic, "type", ""));
			parm.setColumn_bg(getStringValue(rootDic, "column_bg", ""));
			parm.setLogo(getStringValue(rootDic, "logo", ""));
			parm.setLogo_size(getStringValue(rootDic, "logo_size", ""));
			parm.setLogo_scale_type(getStringValue(rootDic, "logo_scale_type",
					ImageScaleType.FIT_XY));
			parm.setLogo_bg(getStringValue(rootDic, "logo_bg", ""));
			parm.setTop_image(getStringValue(rootDic, "top_image", ""));
			parm.setHead_divider(getStringValue(rootDic, "head_divider", ""));
			parm.setDivider(getStringValue(rootDic, "divider", ""));
			parm.setFirst_item_bg(getStringValue(rootDic, "first_item_bg", ""));
			parm.setItem_bg(getStringValue(rootDic, "item_bg", ""));
			parm.setItem_bg_select(getStringValue(rootDic, "item_bg_select", ""));
			parm.setName_color(getStringValue(rootDic, "name_color", ""));
			parm.setName_color_select(getStringValue(rootDic,
					"name_color_select", ""));
			parm.setRow(getStringValue(rootDic, "row", ""));
			parm.setRow_img(getStringValue(rootDic, "row_img", ""));
			parm.setShow_color(getIntValue(rootDic, "show_color", 0));
			parm.setShow_margin(getIntValue(rootDic, "show_margin", 0));
			parm.setAbout(getStringValue(rootDic, "about", ""));
			parm.setRecommend(getStringValue(rootDic, "recommend", ""));
			parm.setItem_height(getIntValue(rootDic, "item_height", 0));
			parm.setName_size(getIntValue(rootDic, "name_size", 0));
			parm.setItem_margin_left(getIntValue(rootDic, "item_margin_left", 0));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return parm;
	}

	/**
	 * 解析收藏列表
	 * 
	 * @return
	 */
	public FavParm parseFav() {
		FavParm parm = new FavParm();
		InputStream is = null;
		try {
			is = mContext.getAssets().open("fav.plist");
			NSDictionary rootDic = (NSDictionary) PropertyListParser.parse(is);
			parm.setBackground(getStringValue(rootDic, "background", ""));
			parm.setBar_background(getStringValue(rootDic, "bar_background", ""));
			parm.setRow(getStringValue(rootDic, "row", ""));
			parm.setRow_img(getStringValue(rootDic, "row_img", ""));
			parm.setFav_item_background(getStringValue(rootDic,
					"fav_item_background", ""));
			parm.setTitle_color(getStringValue(rootDic, "title_color", ""));
			parm.setText_color(getStringValue(rootDic, "text_color", ""));
			parm.setTop_image(getStringValue(rootDic, "top_image", ""));
			parm.setHead_divider(getStringValue(rootDic, "head_divider", ""));
			parm.setDivider(getStringValue(rootDic, "divider", ""));
			parm.setMargin(getIntValue(rootDic, "margin", 1));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return parm;
	}

	/**
	 * 解析文章页
	 */
	public ArticleParm parseArticle() {
		ArticleParm parm = new ArticleParm();
		InputStream is = null;
		try {
			is = mContext.getAssets().open("article.plist");
			NSDictionary rootDic = (NSDictionary) PropertyListParser.parse(is);
			parm.setType(getStringValue(rootDic, "type", ""));
			parm.setNav_back(getStringValue(rootDic, "nav_back", ""));
			parm.setNav_fav(getStringValue(rootDic, "nav_fav", ""));
			parm.setNav_faved(getStringValue(rootDic, "nav_faved", ""));
			parm.setNav_bg(getStringValue(rootDic, "nav_bg", ""));
			parm.setNav_font_size(getStringValue(rootDic, "nav_font_size", ""));
			parm.setNav_share(getStringValue(rootDic, "nav_share", ""));
			parm.setPlaceholder(getStringValue(rootDic, "placeholder", ""));
			parm.setHas_user(getIntValue(rootDic, "has_user", 0));
			parm.setIsAlignToNav(getIntValue(rootDic, "isAlignToNav", 0));
			parm.setBgIsTransparent(getIntValue(rootDic, "bgIsTransparent", 0));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return parm;
	}

	/**
	 * 解析图集
	 */
	public AtlasParm parseAtlas() {
		AtlasParm parm = new AtlasParm();
		InputStream is = null;
		try {
			is = mContext.getAssets().open("atlas.plist");
			NSDictionary rootDic = (NSDictionary) PropertyListParser.parse(is);
			parm.setType(getStringValue(rootDic, "type", ""));
			parm.setPlaceholder(getStringValue(rootDic, "placeholder", ""));
			parm.setImage_dot(getStringValue(rootDic, "image_dot", ""));
			parm.setImage_dot_active(getStringValue(rootDic,
					"image_dot_active", ""));
			parm.setWidth(getIntValue(rootDic, "width", 0));
			parm.setHeight(getIntValue(rootDic, "height", 0));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return parm;
	}

	/**
	 * 解析期刊列表参数
	 */
	public IssueListParm parseIssueList() {
		IssueListParm parm = new IssueListParm();
		InputStream is = null;
		try {
			is = mContext.getAssets().open("issue_list.plist");
			NSDictionary rootDic = (NSDictionary) PropertyListParser.parse(is);
			parm.setType(getStringValue(rootDic, "type", ""));
			parm.setIssue_bg(getStringValue(rootDic, "issue_bg", ""));
			parm.setPlaceholder(getStringValue(rootDic, "placeholder", ""));
			parm.setFooter_bg(getStringValue(rootDic, "footer_bg", ""));
			parm.setFooter_text_color(getStringValue(rootDic,
					"footer_text_color", ""));
			parm.setItem_title_color(getStringValue(rootDic,
					"item_title_color", ""));
			parm.setItem_desc_color(getStringValue(rootDic, "item_desc_color",
					""));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return parm;
	}

	/**
	 * 根据key从字典中取得字符串，若key不存在，则返回默认值
	 * 
	 * @param dic
	 * @param key
	 * @param defVal
	 * @return
	 */
	private String getStringValue(NSDictionary dic, String key, String defVal) {
		return dic != null && dic.containsKey(key) ? dic.objectForKey(key)
				.toString() : defVal;
	}

	/**
	 * 根据key从字典中取得整形值，若key不存在，则返回默认值
	 * 
	 * @param dic
	 * @param key
	 * @param defVal
	 * @return
	 */
	private int getIntValue(NSDictionary dic, String key, int defVal) {
		return dic != null && dic.containsKey(key) ? ((NSNumber) dic
				.objectForKey(key)).intValue() : defVal;
	}

}
