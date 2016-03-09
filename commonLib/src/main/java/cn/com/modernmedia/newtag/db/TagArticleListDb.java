package cn.com.modernmedia.newtag.db;

import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import cn.com.modernmedia.api.BaseOperate;
import cn.com.modernmedia.api.GetTagArticlesOperate;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.MyDBHelper;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 
 * 栏目首页DB
 * 
 * 
 * 
 * @author zhuqiao
 * 
 * 
 */

public class TagArticleListDb extends TagDbListenerImplement {

	private static final String DATABASE_NAME = "tag_article_list.db";

	private static final int DATABASE_VERSION = 3;

	private static final String TABLE_NAME = "tag_article_list";

	public static final String SUBSCRIBE_TOP_ARTICLE = "subscribe_top_article";// 所有订阅栏目的前5篇文章

	// column name

	/**
	 * 
	 * 栏目json数据
	 */

	private static final String PHONE_COLUMN_PROPERTY = "phoneColumnProperty";// 4

	/**
	 * 
	 * 文章json数据
	 */

	private static final String VALUE = "value";// 5

	private static final String VIEW_BY_GROUP = "viewbygroup";// 6

	private static final String IS_RADIO = "isRadio";// 7
	private static final String LINK = "link";// 8
	private static final String TYPE = "type";// 9

	private static final String SPECIAL_TAG = "special_tag";

	private static TagArticleListDb instance = null;

	private TagArticleListDb(Context context) {

		super(context, DATABASE_NAME, DATABASE_VERSION, TABLE_NAME);

	}

	public static synchronized TagArticleListDb getInstance(Context context) {

		if (null == instance) {

			instance = new TagArticleListDb(context);

		}

		return instance;

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		MyDBHelper helper = new MyDBHelper(TABLE_NAME);

		helper.addColumn(ID, "INTEGER PRIMARY KEY AUTOINCREMENT");

		helper.addColumn(APP_ID, "INTEGER");

		helper.addColumn(TAG_NAME, "TEXT");

		helper.addColumn(OFFSET, "TEXT");

		helper.addColumn(PHONE_COLUMN_PROPERTY, "TEXT");

		helper.addColumn(VALUE, "TEXT");

		helper.addColumn(VIEW_BY_GROUP, "TEXT");
		helper.addColumn(IS_RADIO, "INTEGER");

		helper.addColumn(LINK, "TEXT");
		helper.addColumn(TYPE, "INTEGER");
		helper.addColumn(SPECIAL_TAG, "TEXT");

		db.execSQL(helper.getSql());

	}

	@Override
	public synchronized void addEntry(Entry entry) {

		if (!(entry instanceof TagArticleList))

			return;

		TagArticleList articleList = (TagArticleList) entry;

		List<ArticleItem> list = articleList.getArticleList();

		if (!ParseUtil.listNotNull(list))

			return;

		int appid = articleList.getAppid();

		String columnJson = articleList.getColumnJson();

		String viewbygroup = articleList.getViewbygroup();

		String tagName = articleList.getTagName();
		int isRadio = articleList.getIsRadio();
		int type = articleList.getType();

		String link = articleList.getLink();

		SQLiteDatabase db = this.getWritableDatabase();

		try {

			db.beginTransaction();

			for (ArticleItem item : list) {

				ContentValues cv = createContentValues(appid, columnJson, item,

				viewbygroup, tagName, isRadio, link, type);

				db.insert(TABLE_NAME, null, cv);

			}

			db.setTransactionSuccessful();

			db.endTransaction();

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

		}

	}

	@Override
	public ContentValues createContentValues(Object... obj) throws Exception {

		ContentValues cv = new ContentValues();

		cv.put(APP_ID, ParseUtil.stoi(obj[0].toString()));

		cv.put(PHONE_COLUMN_PROPERTY, obj[1].toString());

		ArticleItem item = (ArticleItem) obj[2];

		if (item.getApiTag().contains(",")) {

			// TODO 如果是集合栏目，那么tagname取自己的

			cv.put(TAG_NAME, item.getTagName());

		} else {

			// TODO 如果不是集合栏目，那么取最外层节点的(商周首页只能取最外层节点)

			cv.put(TAG_NAME, obj[4].toString());

		}

		cv.put(OFFSET, item.getOffset());

		cv.put(VALUE, item.getJsonObject());

		cv.put(VIEW_BY_GROUP, obj[3].toString());

		cv.put(IS_RADIO, ParseUtil.stoi(obj[5].toString()));
		cv.put(LINK, obj[6].toString());
		cv.put(TYPE, ParseUtil.stoi(obj[7].toString()));//
		if (obj.length == 9) {

			cv.put(SPECIAL_TAG, obj[8].toString());

		}
		return cv;

	}

	public synchronized Entry getEntry(BaseOperate o, String tagName,

	String top, boolean limit, String special_tag) {

		if (!(o instanceof GetTagArticlesOperate))

			return null;

		GetTagArticlesOperate operate = (GetTagArticlesOperate) o;

		SQLiteDatabase db = this.getReadableDatabase();

		TagArticleList articleList = operate.getArticleList();

		Cursor cursor = null;

		try {

			if (TextUtils.equals(special_tag, SUBSCRIBE_TOP_ARTICLE)) {

				cursor = db.query(TABLE_NAME, null, SPECIAL_TAG + " = '"

				+ SUBSCRIBE_TOP_ARTICLE + "'", null, null, null, ID

				+ " asc");

			} else {

				cursor = db.query(TABLE_NAME, null, getWhrerCase(tagName, top),

				null, null, null, ID + " asc", limit ? LIMIT : null);

			}

			while (cursor != null && cursor.moveToNext()) {

				if (TextUtils.isEmpty(articleList.getTagName())) {

					articleList.setAppid(cursor.getInt(1));

					articleList.setTagName(cursor.getString(2));

					try {

						JSONObject j = new JSONObject(cursor.getString(4));

						if (!JSONObject.NULL.equals(j))

							articleList.setProperty(operate

							.parseColumnProperty(j));

					} catch (Exception e) {

						e.printStackTrace();

					}

					articleList.setViewbygroup(cursor.getString(6));
					articleList.setLink(cursor.getString(8));
				}

				String value = cursor.getString(5);

				if (TextUtils.isEmpty(value))

					continue;

				try {

					JSONObject j = new JSONObject(value);

					if (!JSONObject.NULL.equals(j) && j != null) {

						operate.parseArticleItem(j);

					}

				} catch (Exception e) {

					e.printStackTrace();

					continue;

				}

			}

		} catch (Exception e) {

			e.printStackTrace();

			return null;

		} finally {

			if (cursor != null)

				cursor.close();

		}

		return articleList;

	}

	/**
	 * 
	 * 获取查询条件
	 * 
	 * 
	 * 
	 * @param tagName
	 * 
	 * @return
	 */

	private String getWhrerCase(String tagName, String top) {

		String result = checkTagName(tagName);

		if (!TextUtils.isEmpty(top)) {

			result += " and " + OFFSET + " < " + "'" + top + "'";

		}

		return result;

	}

	/**
	 * 
	 * 保存所有订阅栏目的前5篇文章
	 * 
	 * 
	 * 
	 * @param articleList
	 */

	public void saveSubscribeTopArticle(TagArticleList articleList) {

		if (articleList == null

		|| !ParseUtil.listNotNull(articleList.getArticleList())) {

			return;

		}

		int appid = articleList.getAppid();

		String columnJson = articleList.getColumnJson();

		String viewbygroup = articleList.getViewbygroup();

		String tagName = articleList.getTagName();

		int isRadio = articleList.getIsRadio();
		int type = articleList.getType();

		String link = articleList.getLink();
		SQLiteDatabase db = this.getWritableDatabase();

		try {

			db.beginTransaction();

			for (ArticleItem item : articleList.getArticleList()) {

				ContentValues cv = createContentValues(appid, columnJson, item,

				viewbygroup, tagName, isRadio, link, type,
						SUBSCRIBE_TOP_ARTICLE);

				db.insert(TABLE_NAME, null, cv);

			}

			db.setTransactionSuccessful();

			db.endTransaction();

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

		}

	}

	/**
	 * 
	 * 清除所有订阅栏目的前5篇文章
	 * 
	 * 
	 * 
	 * @return
	 */

	public void clearSubscribeTopArticle() {

		SQLiteDatabase db = this.getWritableDatabase();

		try {

			db.delete(TABLE_NAME, SPECIAL_TAG + " = '" + SUBSCRIBE_TOP_ARTICLE

			+ "'", null);

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

		}

	}

	@Override
	public synchronized void close() {

		if (null != instance) {

			instance = null;

		}

		super.close();

	}

	public void clearTable(String tagName) {

		SQLiteDatabase db = this.getWritableDatabase();

		try {

			db.delete(TABLE_NAME, checkTagName(tagName), null);

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

		}

	}

}