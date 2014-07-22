package cn.com.modernmedia.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 已阅读过的文章
 * 
 * @author ZhuQiao
 * 
 */
public class ReadDb extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "read.db";
	private static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "read";
	// column name
	public static final String ID = "id";

	private static ReadDb instance = null;
	private static MyDBHelper helper;

	private ReadDb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		helper = new MyDBHelper(TABLE_NAME);
		helper.addColumn(ReadDb.ID, "INTEGER PRIMARY KEY");// 0
		db.execSQL(helper.getSql());
	}

	public static synchronized ReadDb getInstance(Context context) {
		if (null == instance) {
			instance = new ReadDb(context);
		}
		return instance;
	}

	/**
	 * 获取所有已阅读过的文章ID
	 * 
	 * @return
	 */
	public List<Integer> getAllReadArticle() {
		SQLiteDatabase db = this.getReadableDatabase();
		List<Integer> list = new ArrayList<Integer>();
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
			while (cursor.moveToNext()) {
				list.add(cursor.getInt(0));
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
	 * 添加阅读过的文章
	 * 
	 * @param id
	 */
	public synchronized void addReadArticle(int id) {
		if (isReaded(id))
			return;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		try {
			cv.put(ID, id);
			db.insert(TABLE_NAME, null, cv);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db.isOpen())
				db.close();
		}
	}

	/**
	 * 当前文章是否已经被阅读过
	 * 
	 * @param id
	 */
	public synchronized boolean isReaded(int id) {
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

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_NAME);
		onCreate(db);
	}
}
