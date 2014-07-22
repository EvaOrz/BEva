package cn.com.modernmedia.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.com.modernmedia.model.ArticleList.ArticleDetail;
import cn.com.modernmedia.model.Favorite;
import cn.com.modernmedia.util.DataHelper;

/**
 * 收藏数据库
 * 
 * @author ZhuQiao
 * 
 */
public class FavDb extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "fav.db";
	private static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "fav";
	// column name
	public static final String ID = "id";
	public static final String TITLE = "title";
	public static final String CATID = "catId";
	public static final String LINK = "link";
	public static final String PICTURE = "picture";
	public static final String UPDATETIME = "updateTime";
	public static final String ISSUEID = "issueid";
	public static final String TYPE = "type";
	public static final String DATE = "date";
	private Context mContext;
	private MyDBHelper helper;
	private static FavDb instance = null;

	private FavDb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		helper = new MyDBHelper(FavDb.TABLE_NAME);
		helper.addColumn(FavDb.ID, "INTEGER PRIMARY KEY");// 0
		helper.addColumn(FavDb.TITLE, "TEXT");// 1
		helper.addColumn(FavDb.CATID, "INTEGER");// 2
		helper.addColumn(FavDb.LINK, "TEXT");// 3
		helper.addColumn(FavDb.PICTURE, "TEXT");// 4
		helper.addColumn(FavDb.UPDATETIME, "TEXT");// 5
		helper.addColumn(FavDb.ISSUEID, "INTEGER");// 6
		helper.addColumn(FavDb.TYPE, "TEXT");// 7
		helper.addColumn(FavDb.DATE, "TEXT");// 8
		db.execSQL(helper.getSql());
	}

	public static synchronized FavDb getInstance(Context context) {
		if (null == instance) {
			instance = new FavDb(context);
		}
		return instance;
	}

	/**
	 * 获取所有收藏
	 */
	public Favorite getAllFav() {
		SQLiteDatabase db = this.getReadableDatabase();
		Favorite favorite = new Favorite();
		ArticleDetail detail;
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_NAME, null, null, null, null, null, DATE
					+ " desc");
			while (cursor.moveToNext()) {
				detail = new ArticleDetail();
				detail.setArticleId(cursor.getInt(0));
				detail.setTitle(cursor.getString(1));
				detail.setCatId(cursor.getInt(2));
				detail.setLink(cursor.getString(3));
				List<String> picList = new ArrayList<String>();
				picList.add(cursor.getString(4));
				detail.setPictureList(picList);
				detail.setUpdateTime(cursor.getString(5));
				detail.getProperty().setType(cursor.getString(7));
				favorite.getList().add(detail);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			if (db.isOpen())
				db.close();
		}
		return favorite;
	}

	/**
	 * 添加fav
	 * 
	 * @param favorite
	 */
	public void addFav(ArticleDetail favorite) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		try {
			cv.put(ID, favorite.getArticleId());
			cv.put(TITLE, favorite.getTitle());
			cv.put(CATID, favorite.getCatId());
			cv.put(LINK, favorite.getLink());
			if (favorite.getPictureList() != null
					&& !favorite.getPictureList().isEmpty()) {
				cv.put(PICTURE, favorite.getPictureList().get(0));
			} else {
				cv.put(PICTURE, "");
			}
			cv.put(UPDATETIME, favorite.getUpdateTime());
			cv.put(ISSUEID, DataHelper.getIssueId(mContext));
			cv.put(TYPE, favorite.getProperty().getType());
			cv.put(DATE, System.currentTimeMillis() + "");
			db.insert(TABLE_NAME, null, cv);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db.isOpen())
				db.close();
		}
	}

	/**
	 * 删除收藏
	 * 
	 * @param favorite
	 */
	public void deleteFav(ArticleDetail favorite) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.delete(TABLE_NAME, ID + "=?",
					new String[] { favorite.getArticleId() + "" });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db.isOpen())
				db.close();
		}
	}

	/**
	 * 删除收藏
	 * 
	 * @param favorite
	 */
	public void deleteFav(String articleId) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.delete(TABLE_NAME, ID + "=?", new String[] { articleId });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db.isOpen())
				db.close();
		}
	}

	/**
	 * 是否收藏过当前文章
	 * 
	 * @param favorite
	 * @return
	 */
	public boolean containThisFav(ArticleDetail favorite) {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] columns = { ID };
		Cursor c = null;
		try {
			c = db.query(TABLE_NAME, columns,
					ID + "=" + favorite.getArticleId(), null, null, null, null);
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
	 * 是否收藏过当前文章
	 * 
	 * @param favorite
	 * @return
	 */
	public boolean containThisFav(String id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] columns = { ID };
		Cursor c = null;
		try {
			c = db.query(TABLE_NAME, columns, ID + "=" + id, null, null, null,
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

	public void close() {
		if (null != instance) {
			this.close();
			instance = null;
		}
	}

	/**
	 * 如果想变更或删除当前表，把version改变，执行此方法
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion > newVersion) {
			return;
		}
		db.execSQL("drop table if exists " + TABLE_NAME);
		onCreate(db);
	}

}
