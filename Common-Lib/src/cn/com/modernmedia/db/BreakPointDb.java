package cn.com.modernmedia.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.com.modernmedia.model.BreakPoint;
import cn.com.modernmedia.util.ParseUtil;

public class BreakPointDb extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "breakpoint.db";
	private static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "breakpoint";

	// column name
	public static final String ID = "id";// 主键，自增
	public static final String URL = "url";// package url 标识
	public static final String COMPLETE = "complete";// 完成度
	public static final String TOTAL = "total";// 总大小
	public static final String ISSUE_ID = "issue_id";// 期id

	private static BreakPointDb instance = null;
	private static MyDBHelper helper;

	private BreakPointDb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		helper = new MyDBHelper(TABLE_NAME);
		helper.addColumn(ID, "INTEGER PRIMARY KEY AUTOINCREMENT");// 0
		helper.addColumn(URL, "TEXT");// 1
		helper.addColumn(COMPLETE, "TEXT");// 2
		helper.addColumn(TOTAL, "TEXT");// 3
		helper.addColumn(ISSUE_ID, "INTEGER");// 4
		db.execSQL(helper.getSql());
	}

	public static synchronized BreakPointDb getInstance(Context context) {
		if (null == instance) {
			instance = new BreakPointDb(context);
		}
		return instance;
	}

	/**
	 * 添加BreakPoint
	 * 
	 * @param breakPoint
	 */
	public void addThreadInfoList(BreakPoint breakPoint) {
		boolean contain = containThisBreakPoint(breakPoint.getUrl());
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			if (contain) {
				db.update(TABLE_NAME, createBreakPoint(breakPoint), URL + "=?",
						new String[] { breakPoint.getUrl() });
			} else {
				db.insert(TABLE_NAME, null, createBreakPoint(breakPoint));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db.isOpen())
				db.close();
		}
	}

	private ContentValues createBreakPoint(BreakPoint breakPoint) {
		ContentValues cv = new ContentValues();
		cv.put(URL, breakPoint.getUrl());
		cv.put(COMPLETE, breakPoint.getComplete() + "");
		cv.put(TOTAL, breakPoint.getTotal() + "");
		cv.put(ISSUE_ID, breakPoint.getId());
		return cv;
	}

	/**
	 * 更新完成度
	 * 
	 * @param id
	 * @param complete
	 */
	public synchronized void updateComplete(String url, String complete) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		try {
			cv.put(COMPLETE, complete);
			db.update(TABLE_NAME, cv, URL + "=?", new String[] { url });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取完成度
	 * 
	 * @param issueId
	 * @return
	 */
	public synchronized long getComplete(int issueId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		String columns[] = { COMPLETE };
		try {
			cursor = db.query(TABLE_NAME, columns, ISSUE_ID + "=?",
					new String[] { issueId + "" }, null, null, null);
			while (cursor.moveToNext()) {
				return ParseUtil.stol(cursor.getString(0), 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			if (db.isOpen())
				db.close();
		}
		return 0;
	}

	/**
	 * 获取完成度
	 * 
	 * @param issueId
	 * @return
	 */
	public synchronized long getTotal(int issueId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		String columns[] = { TOTAL };
		try {
			cursor = db.query(TABLE_NAME, columns, ISSUE_ID + "=?",
					new String[] { issueId + "" }, null, null, null);
			while (cursor.moveToNext()) {
				return ParseUtil.stol(cursor.getString(0), 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			if (db.isOpen())
				db.close();
		}
		return 0;
	}

	/**
	 * 是否包含当前BreakPoint
	 * 
	 * @param id
	 * @return
	 */
	public boolean containThisBreakPoint(String url) {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] columns = { ID };
		Cursor c = null;
		try {
			c = db.query(TABLE_NAME, columns, URL + "=?", new String[] { url },
					null, null, null);
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
