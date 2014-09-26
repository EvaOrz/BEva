package cn.com.modernmedia.views.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmedia.views.model.TemplateAbout;
import cn.com.modernmedia.views.model.TemplateAriticle;
import cn.com.modernmedia.views.model.TemplateAtlas;
import cn.com.modernmedia.views.model.TemplateColumn;
import cn.com.modernmedia.views.model.TemplateFav;
import cn.com.modernmedia.views.model.TemplateIndexNavbar;
import cn.com.modernmediaslate.unit.ParseUtil;

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
	 * 解析关于页导航栏
	 * 
	 * @return
	 */
	public TemplateAbout parseAbout() {
		TemplateAbout template = new TemplateAbout();
		try {
			String data = getData("about.json");
			if (TextUtils.isEmpty(data))
				return template;
			JSONObject object = new JSONObject(data);
			if (object == null || JSONObject.NULL.equals(object))
				return template;
			template.setData(object.optString("data"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return template;
	}

	/**
	 * 解析首页列表默认模板
	 * 
	 * @return
	 */
	public Template parseIndexListNormal() {
		String data = getData("index.json");
		if (!TextUtils.isEmpty(data))
			return parseIndex(data, "");
		return new Template();
	}

	public Template parseIndexGalleryNormal() {
		String data = getData("iweekly_gallery.json");
		if (!TextUtils.isEmpty(data))
			return parseIndex(data, "");
		return new Template();
	}

	public Template parseIndex(String data, String host) {
		Template template = new Template();
		if (TextUtils.isEmpty(data))
			return template;
		try {
			template.setHost(host);
			JSONObject object = new JSONObject(data);
			template.setVersion(object.optInt("version"));
			template.setSupportVersions(object
					.optString("supportVersions", "1"));
			JSONObject head = object.optJSONObject("head");
			if (head != null && !JSONObject.NULL.equals(head)) {
				String pos = head.optString("position");
				if (!TextUtils.isEmpty(pos)) {
					String pArr[] = pos.split(",");
					for (String p : pArr) {
						template.getHead().getPosition()
								.add(ParseUtil.stoi(p, 1));
					}
				}
				template.getHead().setData(head.optString("data"));
			}

			JSONObject head_item = object.optJSONObject("head_item");
			if (head_item != null && !JSONObject.NULL.equals(head_item)) {
				template.getHeadItem().setData(head_item.optString("data"));
			}

			JSONObject list = object.optJSONObject("list");
			if (list != null && !JSONObject.NULL.equals(list)) {
				String pos = list.optString("position");
				if (!TextUtils.isEmpty(pos)) {
					String pArr[] = pos.split(",");
					for (String p : pArr) {
						template.getList().getPosition()
								.add(ParseUtil.stoi(p, 1));
					}
				}
				template.getList().setOne_line_count(
						list.optInt("one_line_count", 1));
				template.getList().setIs_gallery(list.optInt("is_gallery"));
				template.getList().setDefault_data(
						list.optString("default_data"));
				JSONArray listDataArr = list.optJSONArray("data");
				if (listDataArr != null && !JSONObject.NULL.equals(listDataArr)) {
					for (int i = 0; i < listDataArr.length(); i++) {
						JSONObject listDataObj = listDataArr.optJSONObject(i);
						if (listDataObj != null
								&& !JSONObject.NULL.equals(listDataObj)) {
							template.getList()
									.getMap()
									.put(listDataObj.optInt("style", 1),
											listDataObj.optString("data"));
						}
					}
				}
			}

			JSONObject cat_head = object.optJSONObject("cat_head");
			if (cat_head != null && !JSONObject.NULL.equals(cat_head)) {
				template.getCatHead().setCat_list_hold(
						cat_head.optInt("cat_list_hold", 1));
				template.getCatHead().setColor(cat_head.optString("color"));
				template.getCatHead().setData(cat_head.optString("data"));
			}

			JSONObject list_head = object.optJSONObject("list_head");
			if (list_head != null && !JSONObject.NULL.equals(list_head)) {
				template.getListHead().setData(list_head.optString("data"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return template;
		}
		return template;
	}

	/**
	 * 解析首页导航栏
	 * 
	 * @return
	 */
	public TemplateIndexNavbar parseIndexNav() {
		TemplateIndexNavbar template = new TemplateIndexNavbar();
		try {
			String data = getData("index_nav.json");
			if (TextUtils.isEmpty(data))
				return template;
			JSONObject object = new JSONObject(data);
			if (object == null || JSONObject.NULL.equals(object))
				return template;
			template.setData(object.optString("data"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return template;
	}

	/**
	 * 解析栏目列表
	 * 
	 * @return
	 */
	public TemplateColumn parseColumn() {
		TemplateColumn template = new TemplateColumn();
		try {
			String data = getData("column.json");
			if (TextUtils.isEmpty(data))
				return template;
			JSONObject object = new JSONObject(data);
			if (object == null || JSONObject.NULL.equals(object))
				return template;
			template.setBackground(object.optString("background", "#FFFFFFFF"));
			template.setAbout(object.optString("about"));
			template.setRecommend(object.optString("recommend"));

			// head
			JSONObject head = object.optJSONObject("head");
			if (head != null && !JSONObject.NULL.equals(head)) {
				template.getHead().setHold(head.optInt("hold", 0));
				template.getHead().setData(head.optString("data"));
				template.getHead().setNeed_calculate_height(
						head.optInt("need_calculate_height", 1));
			}

			// list
			JSONObject list = object.optJSONObject("list");
			if (list != null && !JSONObject.NULL.equals(list)) {
				template.getList().setData(list.optString("data"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return template;
	}

	/**
	 * 解析收藏列表
	 * 
	 * @return
	 */
	public TemplateFav parseFav() {
		TemplateFav template = new TemplateFav();
		try {
			String data = getData("fav.json");
			if (TextUtils.isEmpty(data))
				return template;
			JSONObject object = new JSONObject(data);
			if (object == null || JSONObject.NULL.equals(object))
				return template;
			template.setBackground(object.optString("background", "#FFFFFFFF"));
			JSONObject head = object.optJSONObject("head");
			if (head != null && !JSONObject.NULL.equals(head)) {
				template.getNavBar().setData(head.optString("data"));
			}
			// list
			JSONObject list = object.optJSONObject("list");
			if (list != null && !JSONObject.NULL.equals(list)) {
				template.getList().setData(list.optString("data"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return template;
	}

	/**
	 * 解析文章页
	 */
	public TemplateAriticle parseArticle() {
		TemplateAriticle template = new TemplateAriticle();
		try {
			String data = getData("article.json");
			if (TextUtils.isEmpty(data))
				return template;
			JSONObject object = new JSONObject(data);
			if (object == null || JSONObject.NULL.equals(object))
				return template;
			template.setHas_user(object.optInt("has_user"));
			template.setIsAlignToNav(object.optInt("isAlignToNav"));
			template.setBgIsTransparent(object.optInt("bgIsTransparent"));
			JSONObject head = object.optJSONObject("head");
			if (head != null && !JSONObject.NULL.equals(head)) {
				template.getNavBar().setData(head.optString("data"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return template;
	}

	/**
	 * 解析图集
	 * 
	 * @param isCurrApp
	 *            是否当前app
	 */
	public TemplateAtlas parseAtlas(boolean isCurrApp) {
		TemplateAtlas template = new TemplateAtlas();
		try {
			String data = "";
			if (isCurrApp) {
				data = getData("atlas.json");
				if (TextUtils.isEmpty(data))
					data = getData("atlas_normal.json");
			} else {
				data = getData("atlas_normal.json");
			}
			if (TextUtils.isEmpty(data))
				return template;
			JSONObject object = new JSONObject(data);
			if (object == null || JSONObject.NULL.equals(object))
				return template;
			template.setData(object.optString("data"));
			// list
			JSONObject list = object.optJSONObject("list");
			if (list != null && !JSONObject.NULL.equals(list)) {
				template.getPagerItem().setData(list.optString("data"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return template;
	}

	private String getData(String name) {
		InputStreamReader is = null;
		BufferedReader br = null;
		String data = "";
		try {
			is = new InputStreamReader(mContext.getAssets().open(name));
			br = new BufferedReader(is, 1024);
			String line = "";
			while ((line = br.readLine()) != null) {
				data += line;
			}
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

}
