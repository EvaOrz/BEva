package cn.com.modernmediasolo.db;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import cn.com.modernmedia.api.GetCatIndexOperate;
import cn.com.modernmedia.db.MyDBHelper;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmediasolo.SoloApplication;

/**
 * 独立栏目焦点图db
 * 
 * @author user
 * 
 */
public class SoloFocusDb extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "soloFocus.db";
	private static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "soloFcus";

	// column name
	public static final String ID = "id";// 0
	public static final String PARENTID = "parentId";// 1 区分多个独立栏目
	public static final String OFFSET = "offset";// 2 偏移值
	public static final String VALUE = "value";// 3 jsonObject数据
	public static final String DATE = "date";// 排序用

	private Context mContext;
	private static SoloFocusDb instance = null;

	public SoloFocusDb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		MyDBHelper helper = new MyDBHelper(TABLE_NAME);
		helper.addColumn(ID, "INTEGER PRIMARY KEY");
		helper.addColumn(PARENTID, "INTEGER");
		helper.addColumn(OFFSET, "TEXT");
		helper.addColumn(VALUE, "TEXT");
		helper.addColumn(DATE, "TEXT");
		db.execSQL(helper.getSql());
	}

	public static synchronized SoloFocusDb getInstance(Context context) {
		if (null == instance) {
			instance = new SoloFocusDb(context);
		}
		return instance;
	}

	/**
	 * 获取独立栏目首页焦点图(因为是根据请求回来的indexlist来获取，所以必须包含)
	 * 
	 * @param parentId
	 * @return
	 */
	public HashMap<String, List<ArticleItem>> getArticleItem(int parentId,
			int position) {
		SQLiteDatabase db = this.getReadableDatabase();
		GetCatIndexOperate operate = new GetCatIndexOperate(mContext, parentId
				+ "", "0", "0", SoloApplication.soloColumn, position);
		CatIndexArticle index = operate.getCatIndexArticle();
		index.setId(parentId);
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_NAME, new String[] { VALUE }, PARENTID
					+ "=?", new String[] { parentId + "" }, null, null, DATE
					+ " desc");// limit
			index.setHasData(cursor.getCount() > 0);
			while (cursor.moveToNext()) {
				String value = cursor.getString(0);
				if (!TextUtils.isEmpty(value)) {
					try {
						JSONObject obj = new JSONObject(value);
						if (!JSONObject.NULL.equals(obj) && obj != null) {
							operate.parseArticle(obj);
						}
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			if (db.isOpen())
				db.close();
		}
		return index.getHeadMap();
	}

	/**
	 * @param parentId
	 * @param list
	 */
	public void addSoloFoucsItems(int parentId, List<ArticleItem> list) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.beginTransaction();
			for (ArticleItem item : list) {
				db.insert(TABLE_NAME, null, createContentValues(parentId, item));
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

	private ContentValues createContentValues(int parentId, ArticleItem item) {
		ContentValues cv = new ContentValues();
		cv.put(ID, item.getArticleId());
		cv.put(PARENTID, parentId);
		if (item.getSoloItem() != null) {
			cv.put(OFFSET, item.getSoloItem().getOffset());
			cv.put(VALUE, item.getSoloItem().getJsonObject());
			cv.put(DATE, System.currentTimeMillis() + "");
		}
		return cv;
	}

	@SuppressWarnings("unused")
	private boolean containThisItem(int articleId) {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] columns = { ID };
		Cursor c = null;
		try {
			c = db.query(TABLE_NAME, columns, ID + "=" + articleId, null, null,
					null, null);
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
	 * 当从服务器成功获取到数据后，清空数据库所有数据，再把服务器的数据add进来，因为服务器返回的时候是全部数据
	 */
	public void clearTable() {
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

	@Override
	public synchronized void close() {
		if (null != instance) {
			instance = null;
		}
		super.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			db.execSQL("drop table if exists " + TABLE_NAME);
			onCreate(db);
		}
	}

}
