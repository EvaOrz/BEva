package cn.com.modernmedia.db;

import java.net.URLEncoder;
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
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediaslate.model.Favorite.Thumb;

/**
 * 收藏数据库
 * 
 * @author ZhuQiao
 * 
 */
public class FavDb extends SQLiteOpenHelper {
	public static final String IOS = "兼容ios";// ios的收藏时间只能是String类型
	private static final String DATABASE_NAME = "fav.db";
	private static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "fav";
	private static final String TABLE_NAME_TEMP = "fav_temp";
	public static final String CHARSET = "UTF-8";
	// column name
	public static final String ID = "id";
	public static final String TITLE = "title";
	public static final String CATID = "catId";
	public static final String LINK = "link";
	public static final String PICTURE = "picture";
	public static final String UPDATETIME = "updateTime";
	public static final String ISSUEID = "issueid";
	public static final String TYPE = "type";
	public static final String DATE = "date";// 收藏时间。排序用

	// ===========新增
	/**
	 * 默认的uid设置为0，当登录的时候成功同步了当条fav，再把uid设置为当前user的uid
	 */
	public static final String UID = "uid";
	public static final String APPID = "appid";
	public static final String DESC = "desc";
	public static final String FAVDEL = "favdel";// 是否删除: 1.删除
	public static final String PAGENUM = "pagenum";
	public static final String OFFSET = "offset";
	public static final String TAG = "tag";
	public static final String SUCCESS = "success";// 是否同步成功;1。成功；0。不成功

	private Context mContext;
	private MyDBHelper helper;
	private static FavDb instance = null;

	private FavDb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(getCreatSql(false));
	}

	public static synchronized FavDb getInstance(Context context) {
		if (null == instance) {
			instance = new FavDb(context);
		}
		return instance;
	}

	private String getCreatSql(boolean containNewColumn) {
		helper = new MyDBHelper(FavDb.TABLE_NAME);
		helper.addColumn(ID, "INTEGER PRIMARY KEY");// 0
		helper.addColumn(TITLE, "TEXT");// 1
		helper.addColumn(CATID, "INTEGER");// 2
		helper.addColumn(LINK, "TEXT");// 3
		helper.addColumn(PICTURE, "TEXT");// 4
		helper.addColumn(UPDATETIME, "TEXT");// 5
		helper.addColumn(ISSUEID, "INTEGER");// 6
		helper.addColumn(TYPE, "TEXT");// 7
		helper.addColumn(DATE, "TEXT");// 8
		if (containNewColumn) {
			helper.addColumn(UID, "TEXT");// 9
			helper.addColumn(APPID, "TEXT");// 10
			helper.addColumn(DESC, "TEXT");// 11
			helper.addColumn(FAVDEL, "INTEGER");// 12
			helper.addColumn(PAGENUM, "INTEGER");// 13
			helper.addColumn(OFFSET, "TEXT");// 14
			helper.addColumn(TAG, "TEXT");// 15
			helper.addColumn(SUCCESS, "INTEGER");// 16
		}
		return helper.getSql();
	}

	/**
	 * 添加新字段
	 */
	public void addColumn() {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.execSQL(getAddColumnSql(UID, "TEXT"));// 9
			db.execSQL(getAddColumnSql(APPID, "TEXT"));// 10
			db.execSQL(getAddColumnSql(DESC, "TEXT"));// 11
			db.execSQL(getAddColumnSql(FAVDEL, "INTEGER"));// 12
			db.execSQL(getAddColumnSql(PAGENUM, "INTEGER"));// 13
			db.execSQL(getAddColumnSql(OFFSET, "TEXT"));// 14
			db.execSQL(getAddColumnSql(TAG, "TEXT"));// 15
			db.execSQL(getAddColumnSql(SUCCESS, "INTEGER"));// 16
			dataTransfer(db);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (db.isOpen())
				db.close();
		}
	}

	private String getAddColumnSql(String name, String type) {
		return "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + name + " " + type;
	}

	private void dataTransfer(SQLiteDatabase db) {
		// 将表名改为临时表
		String rename = "ALTER TABLE " + TABLE_NAME + " RENAME TO "
				+ TABLE_NAME_TEMP;
		db.execSQL(rename);
		// 创建新表
		db.execSQL(getCreatSql(true));
		// 导入数据
		String insertSql = "INSERT INTO " + TABLE_NAME + " SELECT " + ID + ","
				+ TITLE + "," + CATID + "," + LINK + "," + PICTURE + ","
				+ UPDATETIME + "," + ISSUEID + "," + TYPE + "," + DATE + ","
				+ "0" + "," + "''" + "," + "''" + "," + 0 + "," + 0 + ","
				+ "''" + "," + "''" + "," + 0 + " FROM " + TABLE_NAME_TEMP;
		db.execSQL(insertSql);
		// 删除临时表
		db.execSQL("DROP TABLE " + TABLE_NAME_TEMP);
	}

	/**
	 * 获取用户需要上传的收藏
	 * 
	 * @param uid
	 *            用户id
	 * @param isShow
	 *            是否是用来显示的。true:不编码，false:编码传给服务器
	 * @return
	 */
	public List<FavoriteItem> getUserUnUpdateFav(String uid, boolean isShow) {
		String where = "";
		if (uid == ConstData.UN_UPLOAD_UID) {
			where = UID + "=" + uid;
		} else {
			where = UID + " in(" + ConstData.UN_UPLOAD_UID + "," + uid + ")";
		}
		where += " AND " + SUCCESS + "=0";
		return getLocalFav(uid, isShow, where);
	}

	/**
	 * 获取用户所有的收藏
	 * 
	 * @param uid
	 * @param isShow
	 * @return
	 */
	public List<FavoriteItem> getUserFav(String uid, boolean isShow) {
		String where = "";
		if (uid == ConstData.UN_UPLOAD_UID) {
			where = UID + "=" + uid;
		} else {
			where = UID + " in(" + ConstData.UN_UPLOAD_UID + "," + uid + ")";
		}
		where += " AND " + FAVDEL + "=0";
		return getLocalFav(uid, isShow, where);
	}

	/**
	 * 获取所有收藏(用户未登录时使用)
	 * 
	 * @param uid
	 * @param isShow
	 *            是否是用来显示的。true:不编码，false:编码传给服务器
	 * @return
	 */
	private List<FavoriteItem> getLocalFav(String uid, boolean isShow,
			String where) {
		SQLiteDatabase db = this.getReadableDatabase();
		List<FavoriteItem> list = new ArrayList<FavoriteItem>();
		FavoriteItem detail;
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_NAME, null, where, null, null, null, DATE
					+ " desc");
			while (cursor != null && cursor.moveToNext()) {
				detail = new FavoriteItem();
				detail.setId(cursor.getInt(0));
				String title = cursor.getString(1);
				if (!isShow && !TextUtils.isEmpty(title)) {
					title = URLEncoder.encode(title, CHARSET);
				}
				detail.setTitle(title);
				detail.setCatid(cursor.getInt(2));
				detail.setLink(cursor.getString(3));
				Thumb thumb = new Thumb();
				thumb.setUrl(cursor.getString(4));
				detail.getThumb().add(thumb);
				detail.setUpdateTime(cursor.getString(5));
				detail.setIssueid(cursor.getInt(6));
				detail.getProperty().setType(
						ParseUtil.stoi(cursor.getString(7), 1));
				// 把毫秒转换成秒
				String favtime = cursor.getString(8);
				if (!TextUtils.isEmpty(favtime)) {
					long time = ParseUtil.stol(favtime, -1);
					if (time != -1) {
						time = time / 1000;
						detail.setFavtime(time + IOS);
					} else {
						detail.setFavtime(IOS);
					}
				} else {
					detail.setFavtime(IOS);
				}
				//
				if (cursor.getColumnCount() == 17) {
					String desc = cursor.getString(11);
					if (!isShow && !TextUtils.isEmpty(desc)) {
						desc = URLEncoder.encode(desc, CHARSET);
					}
					detail.setDesc(desc);
					detail.setFavdel(cursor.getInt(12));
					detail.setPagenum(cursor.getInt(13));
					detail.setOffset(cursor.getString(14));
					String tag = cursor.getString(15);
					if (!isShow && !TextUtils.isEmpty(tag)) {
						tag = URLEncoder.encode(tag, CHARSET);
					}
					detail.setTag(tag);
				}
				list.add(detail);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			if (db.isOpen())
				db.close();
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
	public void addFav(FavoriteItem favorite, String uid, boolean success) {
		if (favorite == null)
			return;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_NAME, null, ID + "=" + favorite.getId(),
					null, null, null, null);
			if (cursor.moveToNext()) {
				// 如果存在，更新状态
				db.update(TABLE_NAME,
						createContentValues(favorite, uid, success), ID + "=?",
						new String[] { favorite.getId() + "" });
			} else {
				db.insert(TABLE_NAME, null,
						createContentValues(favorite, uid, success));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db.isOpen())
				db.close();
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * 批量添加收藏
	 * 
	 * @param list
	 * @param uid
	 * @param success
	 */
	private void addFavs(List<FavoriteItem> list, String uid, boolean success) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.beginTransaction();
			for (FavoriteItem item : list) {
				db.insert(TABLE_NAME, null,
						createContentValues(item, uid, success));
			}
			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db.isOpen())
				db.close();
		}
	}

	private ContentValues createContentValues(FavoriteItem favorite,
			String uid, boolean success) {
		ContentValues cv = new ContentValues();
		cv.put(ID, favorite.getId());
		cv.put(TITLE, favorite.getTitle());
		cv.put(CATID, favorite.getCatid());
		cv.put(LINK, favorite.getLink());
		if (ParseUtil.listNotNull(favorite.getThumb())
				&& favorite.getThumb().get(0) != null) {
			cv.put(PICTURE, favorite.getThumb().get(0).getUrl());
		} else {
			cv.put(PICTURE, "");
		}
		cv.put(UPDATETIME, favorite.getUpdateTime());
		cv.put(ISSUEID, DataHelper.getIssueId(mContext));
		cv.put(TYPE, favorite.getProperty().getType());
		cv.put(DATE, System.currentTimeMillis() + "");
		// 新增
		if (DataHelper.getAddColumn(mContext)) {
			cv.put(UID, uid);
			cv.put(APPID, ConstData.getInitialAppId() + "");
			cv.put(DESC, favorite.getDesc());
			cv.put(FAVDEL, 0);
			cv.put(PAGENUM, favorite.getPagenum());
			cv.put(OFFSET, favorite.getOffset());
			cv.put(TAG, favorite.getTag());
			cv.put(SUCCESS, success ? 1 : 0);
		}
		return cv;
	}

	/**
	 * 删除收藏(当同步成功后删除)
	 * 
	 * @param id
	 */
	public void deleteFav(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		try {
			cv.put(SUCCESS, 0);
			cv.put(FAVDEL, 1);
			db.update(TABLE_NAME, cv, ID + "=?", new String[] { id + "" });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db.isOpen())
				db.close();
		}
	}

	/**
	 * 同步成功后批量删除本地数据
	 * 
	 * @param idArr
	 */
	public void deleteFav(String[] idArr) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.delete(TABLE_NAME, ID + "=?", idArr);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db.isOpen())
				db.close();
		}
	}

	/**
	 * 当同步成功后更新fav状态
	 * 
	 * @param success
	 *            是否同步成功
	 */
	public void updateFavStatus(int articleId, boolean success) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		try {
			cv.put(SUCCESS, success ? 1 : 0);
			db.update(TABLE_NAME, cv, ID + "=?",
					new String[] { articleId + "" });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db.isOpen())
				db.close();
		}
	}

	/**
	 * 当同步成功后更新fav状态
	 * 
	 * @param ids
	 * @param success
	 *            是否同步成功
	 */
	public void updateFavStatus(int[] ids, boolean success) {
		String whereArgs = "";
		for (int i : ids) {
			whereArgs = "," + i;
		}
		if (!TextUtils.isEmpty(whereArgs) && whereArgs.length() > 1) {
			whereArgs = whereArgs.substring(1, whereArgs.length());
		}
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "UPDATE " + TABLE_NAME + " SET " + SUCCESS + " = "
				+ (success ? 1 : 0) + " WHERE " + ID + " in(" + whereArgs + ")";
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (db.isOpen())
				db.close();
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
			if (db.isOpen())
				db.close();
		}
		return false;
	}

	/**
	 * 从服务器成功获取数据后的操作
	 * 
	 * @param favorite
	 */
	public void fetchDataFromHttp(Entry entry) {
		if (entry instanceof Favorite) {
			Favorite favorite = (Favorite) entry;
			clearTable();
			List<FavoriteItem> list = favorite.getList();
			addFavs(list, favorite.getUid(), true);
			CommonApplication.notifyFav();
		}
	}

	/**
	 * 当从服务器成功获取到数据后，清空数据库所有数据，再把服务器的数据add进来，因为服务器返回的时候是全部数据
	 */
	private void clearTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.execSQL("DELETE FROM " + TABLE_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
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
			onCreate(db);
		}
	}

}
