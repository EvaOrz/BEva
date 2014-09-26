package cn.com.modernmedia.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.PhonePageList;
import cn.com.modernmedia.model.ArticleItem.Picture;
import cn.com.modernmedia.model.FavList;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.MyDBHelper;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 收藏数据库
 * 
 * @author ZhuQiao
 * 
 */
public class NewFavDb extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "new_fav.db";
	private static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "new_fav";
	public static final String CHARSET = "UTF-8";
	// column name
	public static final String _ID = "_id";
	public static final String ID = "id";
	public static final String PAGE = "page";
	public static final String TITLE = "title";
	public static final String LINK = "link";
	public static final String PICTURE = "picture";
	public static final String UPDATETIME = "updateTime";
	public static final String TYPE = "type";
	public static final String DATE = "date";// 收藏时间。
	// 默认的uid设置为0，当登录的时候成功同步了当条fav，再把uid设置为当前user的uid
	public static final String UID = "uid";
	public static final String APPID = "appid";
	public static final String DESC = "desc";
	public static final String FAVDEL = "favdel";// 是否删除: 1.删除
	public static final String SUCCESS = "success";// 是否同步成功;1。成功；0。不成功
	public static final String WEBURL = "weburl";

	private Context mContext;
	private MyDBHelper helper;
	private static NewFavDb instance = null;
	private AtlasTable atlasTable; // 图集表,该表为字表，通过文章id与收藏主表关联

	private NewFavDb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		atlasTable = new AtlasTable();
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(getCreatSql());
		db.execSQL(atlasTable.getCreatSql());
	}

	public static synchronized NewFavDb getInstance(Context context) {
		// 建立图集表
		if (null == instance) {
			instance = new NewFavDb(context);
		}
		return instance;
	}

	private String getCreatSql() {
		helper = new MyDBHelper(NewFavDb.TABLE_NAME);
		helper.addColumn(_ID, "INTEGER PRIMARY KEY AUTOINCREMENT");// 0
		helper.addColumn(ID, "INTEGER");// 0
		helper.addColumn(TITLE, "TEXT");// 1
		helper.addColumn(PAGE, "TEXT");// 2
		helper.addColumn(LINK, "TEXT");// 3
		helper.addColumn(PICTURE, "TEXT");// 4
		helper.addColumn(UPDATETIME, "TEXT");// 5
		helper.addColumn(TYPE, "TEXT");// 6
		helper.addColumn(DATE, "TEXT");// 7
		helper.addColumn(UID, "TEXT");// 8
		helper.addColumn(APPID, "TEXT");// 19
		helper.addColumn(DESC, "TEXT");// 10
		helper.addColumn(FAVDEL, "INTEGER");// 11
		helper.addColumn(SUCCESS, "INTEGER");// 14
		helper.addColumn(WEBURL, "TEXT");// 15

		return helper.getSql();
	}

	public void dataTransfer() {
		FavDb favDb = FavDb.getInstance(mContext);
		List<ArticleItem> articles = favDb.getLocalFav();
		if (ParseUtil.listNotNull(articles)) {
			addFavs(articles, "", false, true);
		}
		// 删除旧表
		favDb.delete();
	}

	/**
	 * 获取用户需要上传的收藏
	 * 
	 * @param uid
	 *            用户id
	 * @return
	 */
	public List<ArticleItem> getUserUnUpdateFav(String uid) {
		String where = "";
		if (uid.equals(SlateApplication.UN_UPLOAD_UID)) {
			where = UID + "=" + uid;
		} else {
			where = UID + " in(" + SlateApplication.UN_UPLOAD_UID + "," + uid
					+ ")";
		}
		where += " AND " + SUCCESS + "=0";
		return getLocalFav(uid, where);
	}

	/**
	 * 获取用户所有的收藏
	 * 
	 * @param uid
	 * @param isShow
	 * @return
	 */
	public List<ArticleItem> getUserFav(String uid) {
		String where = "";
		if (uid.equals(SlateApplication.UN_UPLOAD_UID)) {
			where = UID + "=" + uid;
		} else {
			where = UID + " in(" + SlateApplication.UN_UPLOAD_UID + "," + uid
					+ ")";
		}
		where += " AND " + FAVDEL + "=0";
		return getLocalFav(uid, where);
	}

	/**
	 * 获取所有收藏(用户未登录时使用)
	 * 
	 * @param uid
	 * @return
	 */
	private List<ArticleItem> getLocalFav(String uid, String where) {
		SQLiteDatabase db = this.getReadableDatabase();
		List<ArticleItem> list = new ArrayList<ArticleItem>();
		ArticleItem detail;
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_NAME, null, where, null, null, null, DATE
					+ " desc");
			while (cursor != null && cursor.moveToNext()) {
				detail = new ArticleItem();
				detail.setArticleId(cursor.getInt(cursor.getColumnIndex(ID)));
				detail.setAppid(cursor.getInt(cursor.getColumnIndex(APPID)));
				detail.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
				int type = ParseUtil.stoi(
						cursor.getString(cursor.getColumnIndex(TYPE)), 1);
				detail.getProperty().setType(type);
				detail.setSlateLink(cursor.getString(cursor
						.getColumnIndex(LINK)));
				Picture picture = new Picture();
				picture.setUrl(cursor.getString(cursor.getColumnIndex(PICTURE)));
				detail.getThumbList().add(picture);
				detail.setUpdateTime(cursor.getString(cursor
						.getColumnIndex(UPDATETIME)));
				detail.setFavTime(cursor.getString(cursor.getColumnIndex(DATE)));
				detail.setDesc(cursor.getString(cursor.getColumnIndex(DESC)));
				detail.setFavDel(cursor.getInt(cursor.getColumnIndex(FAVDEL)));
				detail.setWeburl(cursor.getString(cursor.getColumnIndex(WEBURL)));
				if (type == 2) { // 图集数据从图集表中取得
					detail.getPageUrlList()
							.addAll(atlasTable.getAtlas(db,
									detail.getArticleId(), uid));
				} else {
					PhonePageList phonePage = new PhonePageList();
					phonePage.setUrl(cursor.getString(cursor
							.getColumnIndex(PAGE)));
					detail.getPageUrlList().add(phonePage);
				}
				list.add(detail);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return list;
	}

	/**
	 * 添加收藏
	 * 
	 * @param favorite
	 * @param uid
	 *            用户id
	 * @param success
	 *            是否同步成功的数据
	 */
	public void addFav(ArticleItem favorite, String uid, boolean success) {
		if (favorite == null)
			return;
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.delete(TABLE_NAME, getWhere(favorite.getArticleId(), uid), null);
			db.insert(TABLE_NAME, null,
					createContentValues(favorite, uid, success, false));
			if (favorite.getProperty().getType() == 2) { // 图集
				atlasTable.insert(db, favorite.getArticleId(), uid,
						favorite.getPageUrlList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	/**
	 * 批量添加收藏
	 * 
	 * @param list
	 * @param uid
	 * @param success
	 * @param isDataTransfer
	 *            是否数据迁移
	 */
	private void addFavs(List<ArticleItem> list, String uid, boolean success,
			boolean isDataTransfer) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();
		try {
			for (ArticleItem item : list) {
				db.delete(TABLE_NAME, getWhere(item.getArticleId(), uid), null);
				db.insert(TABLE_NAME, null,
						createContentValues(item, uid, success, isDataTransfer));
				if (item.getProperty().getType() == 2) {
					atlasTable.deleteAtlas(db, item.getArticleId(), uid);
					for (PhonePageList page : item.getPageUrlList()) {
						atlasTable.insert(db, item.getArticleId(), uid, page);
					}
				}
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	private ContentValues createContentValues(ArticleItem favorite, String uid,
			boolean success, boolean isDataTransfer) {
		ContentValues cv = new ContentValues();
		cv.put(ID, favorite.getArticleId());
		cv.put(TITLE, favorite.getTitle());
		cv.put(LINK, favorite.getSlateLink()); // 文章link
		if (ParseUtil.listNotNull(favorite.getPageUrlList())
				&& favorite.getPageUrlList().get(0) != null) {
			PhonePageList phonePage = favorite.getPageUrlList().get(0);
			cv.put(PAGE, phonePage.getUrl());
		}
		// 没有缩略图时用大图替换
		if (ParseUtil.listNotNull(favorite.getThumbList())
				&& favorite.getThumbList().get(0) != null
				&& !TextUtils.isEmpty(favorite.getThumbList().get(0).getUrl())) {
			cv.put(PICTURE, favorite.getThumbList().get(0).getUrl());
		} else if (ParseUtil.listNotNull(favorite.getPicList())
				&& !TextUtils.isEmpty(favorite.getPicList().get(0))) {
			cv.put(PICTURE, favorite.getPicList().get(0));
		}
		cv.put(UPDATETIME, favorite.getUpdateTime());
		cv.put(TYPE, favorite.getProperty().getType());
		if (isDataTransfer) {
			String favtime = favorite.getDbData().getFavtime();
			if (favtime.length() > 10) {
				cv.put(DATE, favtime.substring(0, 10));
			} else {
				cv.put(DATE, favtime);
			}
		} else if (!TextUtils.isEmpty(favorite.getFavTime())) {
			cv.put(DATE, favorite.getFavTime());
		} else {
			cv.put(DATE, System.currentTimeMillis() / 1000 + "");
		}
		if (isDataTransfer)
			cv.put(UID, favorite.getDbData().getUid());
		else
			cv.put(UID, uid);
		cv.put(APPID, favorite.getAppid() == 0 ? ConstData.getInitialAppId()
				+ "" : favorite.getAppid() + "");
		cv.put(DESC, favorite.getDesc());
		cv.put(FAVDEL, favorite.getFavDel() == 0 ? 0 : 1);
		if (isDataTransfer)
			cv.put(SUCCESS, favorite.getDbData().getSuccess());
		else
			cv.put(SUCCESS, success ? 1 : 0);
		cv.put(WEBURL, favorite.getWeburl());
		return cv;
	}

	/**
	 * 删除收藏(当同步成功后删除)
	 * 
	 * @param id
	 */
	public void deleteFav(int id, String uid) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		try {
			cv.put(SUCCESS, 0);
			cv.put(FAVDEL, 1);
			db.update(TABLE_NAME, cv, getWhere(id, uid), null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	/**
	 * 是否收藏过当前文章(favdel=0)
	 * 
	 * @param id
	 * @return
	 */
	public boolean containThisFav(int id, String uid) {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] columns = { ID };
		Cursor c = null;
		try {
			c = db.query(TABLE_NAME, columns, ID + "=" + id + " AND " + UID
					+ "=" + uid + " AND " + FAVDEL + "=0", null, null, null,
					null);
			return c.moveToNext();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return false;
	}

	/**
	 * 从服务器成功获取数据后的操作
	 * 
	 * @param entry
	 * @param uid
	 * 
	 */
	public void fetchDataFromHttp(Entry entry, String uid) {
		if (entry instanceof FavList) {
			FavList favorite = (FavList) entry;
			clearTable(uid);
			List<ArticleItem> list = favorite.getArticle();
			addFavs(list, uid, true, false);
			CommonApplication.notifyFav();
		}
	}

	private String getWhere(int articleId, String uid) {
		String where = "";
		if (articleId != -1)
			where = ID + " = " + articleId;
		String and = TextUtils.isEmpty(where) ? "" : " and ";
		if (TextUtils.isEmpty(uid)
				|| uid.equals(SlateApplication.UN_UPLOAD_UID)) {
			where += and + UID + " = " + SlateApplication.UN_UPLOAD_UID;
		} else {
			where += and + UID + " in(" + SlateApplication.UN_UPLOAD_UID + ","
					+ uid + ")";
		}
		return where;
	}

	/**
	 * 当从服务器成功获取到数据后，清空该用户在数据库的所有数据，再把服务器的数据add进来，因为服务器返回的时候是全部数据
	 */
	private void clearTable(String uid) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.delete(TABLE_NAME, getWhere(-1, uid), null);
			atlasTable.clearTable(db, uid);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		}
	}

	public void close() {
		if (null != instance) {
			instance = null;
		}
		super.close();
	}

	/**
	 * 如果想变更或删除当前表，把version改变，执行此方法
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			db.execSQL("drop table if exists " + TABLE_NAME);
			db.execSQL("drop table if exists " + AtlasTable.TABLE_NAME);
			onCreate(db);
		}
	}

	public static class AtlasTable {
		private static final String TABLE_NAME = "atlas_tbl";
		private static final String ARTICLE_ID = "article_id";
		private static final String TITLE = "title";
		private static final String IMAGE_URL = "image_url";
		private static final String DESC = "desc";
		private static final String LINK = "link";
		private static final String UID = "uid";

		public String getCreatSql() {
			MyDBHelper dbHelper = new MyDBHelper(TABLE_NAME);
			dbHelper.addColumn(ARTICLE_ID, "INTEGER");// 0
			dbHelper.addColumn(TITLE, "TEXT");// 1
			dbHelper.addColumn(DESC, "TEXT");// 2
			dbHelper.addColumn(IMAGE_URL, "TEXT");// 3
			dbHelper.addColumn(LINK, "TEXT");// 4
			dbHelper.addColumn(UID, "TEXT");// 5
			return dbHelper.getSql();
		}

		private ContentValues createContentValues(int articleId, String uid,
				PhonePageList page) {
			ContentValues cv = new ContentValues();
			cv.put(ARTICLE_ID, articleId);
			cv.put(TITLE, page.getTitle());
			cv.put(IMAGE_URL, page.getUrl());
			cv.put(DESC, page.getDesc());
			cv.put(LINK, page.getUri());
			cv.put(UID, uid);
			return cv;
		}

		/**
		 * 插入单条图集数据
		 * 
		 * @param db
		 *            具有write权限
		 * @param articleId
		 * @param page
		 */
		public void insert(SQLiteDatabase db, int articleId, String uid,
				PhonePageList page) {
			db.insert(TABLE_NAME, null,
					createContentValues(articleId, uid, page));
		}

		/**
		 * 插入图集数据
		 * 
		 * @param db
		 *            具有write权限
		 * @param articleId
		 * @param page
		 */
		public void insert(SQLiteDatabase db, int articleId, String uid,
				List<PhonePageList> pageList) {
			deleteAtlas(db, articleId, uid);
			db.beginTransaction();
			try {
				for (PhonePageList page : pageList) {
					db.insert(TABLE_NAME, null,
							createContentValues(articleId, uid, page));
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.endTransaction();
			}
		}

		/**
		 * 删除图集
		 * 
		 * @param db
		 * @param articleId
		 */
		public void deleteAtlas(SQLiteDatabase db, int articleId, String uid) {
			db.delete(TABLE_NAME, getWhere(articleId, uid), null);
		}

		/**
		 * 取得图集数据
		 * 
		 * @param db
		 * @param articleId
		 * @return
		 */
		public List<PhonePageList> getAtlas(SQLiteDatabase db, int articleId,
				String uid) {
			List<PhonePageList> list = new ArrayList<PhonePageList>();
			Cursor cursor = null;
			try {
				cursor = db.query(TABLE_NAME, null, getWhere(articleId, uid),
						null, null, null, null);
				while (cursor != null && cursor.moveToNext()) {
					PhonePageList page = new PhonePageList();
					page.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
					page.setDesc(cursor.getString(cursor.getColumnIndex(DESC)));
					page.setUrl(cursor.getString(cursor
							.getColumnIndex(IMAGE_URL)));
					page.setUri(cursor.getString(cursor.getColumnIndex(LINK)));
					list.add(page);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
			return list;
		}

		private String getWhere(int articleId, String uid) {
			String where = ARTICLE_ID + " = " + articleId;
			if (TextUtils.isEmpty(uid)
					|| uid.equals(SlateApplication.UN_UPLOAD_UID)) {
				where += " and " + UID + " = " + SlateApplication.UN_UPLOAD_UID;
			} else {
				where += " and " + UID + " in("
						+ SlateApplication.UN_UPLOAD_UID + "," + uid + ")";
			}
			return where;
		}

		public void clearTable(SQLiteDatabase db, String uid) {
			db.delete(TABLE_NAME, getWhere(-1, uid), null);
		}
	}
}
