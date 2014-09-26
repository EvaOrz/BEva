package cn.com.modernmedia.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.com.modernmediaslate.unit.MyDBHelper;

/**
 * 已阅读过的文章
 * 
 * @author ZhuQiao
 * 
 */
public class ReadDb extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "read1.db";
	private static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "read1";

	/**
	 * 数据库没有这个article,即未读
	 */
	public static final int FLAG_ERROR = -1;
	/**
	 * 已读，未获取金币
	 */
	public static final int FLAG_UNCOIN = 0;
	/**
	 * 已获取金币
	 */
	public static final int FLAG_COINED = 1;
	/**
	 * iweekly设置中批量阅读，实际没有真正阅读过
	 */
	public static final int FLAG_READED_UNCOIN = 2;

	// column name
	public static final String ID = "id";
	public static final String FLAG = "flag";

	private static ReadDb instance = null;
	private static MyDBHelper helper;

	private ReadDb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		helper = new MyDBHelper(TABLE_NAME);
		helper.addColumn(ID, "INTEGER PRIMARY KEY");// 0
		helper.addColumn(FLAG, "INTEGER");
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
	 * 获取所有未获取金币的文章
	 * 
	 * @action 离开文章页
	 * @action 进入金币商城
	 * 
	 * @return
	 */
	public List<Integer> getAllUpCoinArticle() {
		SQLiteDatabase db = this.getReadableDatabase();
		List<Integer> list = new ArrayList<Integer>();
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_NAME, null, FLAG + "=0", null, null, null,
					null);
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
	 * 当批量获取金币成功，把所有flag==0的文章都设置flag=1
	 */
	public void updateArticleStatus() {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		try {
			cv.put(FLAG, 1);
			db.update(TABLE_NAME, cv, FLAG + "=" + FLAG_UNCOIN, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db.isOpen())
				db.close();
		}
	}

	/**
	 * 添加阅读过的文章
	 * 
	 * @param id
	 */
	public synchronized void addReadArticle(int id) {
		addReadArticle(id, FLAG_UNCOIN);
	}

	/**
	 * 添加阅读过的文章
	 * 
	 * @param id
	 */
	public synchronized void addReadArticle(int id, int flag) {
		int f = isReaded(id);
		if (f == FLAG_COINED || f == FLAG_UNCOIN)
			return;
		if (f == FLAG_READED_UNCOIN) {
			updateFlag(id);
			return;
		}
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		try {
			cv.put(ID, id);
			cv.put(FLAG, flag);
			db.insert(TABLE_NAME, null, cv);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db.isOpen())
				db.close();
		}
	}

	/**
	 * 当flag=-1时，把它置为0
	 * 
	 * @param id
	 */
	public synchronized void updateFlag(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		try {
			cv.put(FLAG, FLAG_UNCOIN);
			db.update(TABLE_NAME, cv, ID + "=" + id + " and " + FLAG + "="
					+ FLAG_READED_UNCOIN, null);
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
	public synchronized int isReaded(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] columns = { FLAG };
		Cursor c = null;
		try {
			c = db.query(TABLE_NAME, columns, ID + "=" + id, null, null, null,
					null);
			if (c.moveToNext())
				return c.getInt(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
			if (db.isOpen())
				db.close();
		}
		return FLAG_ERROR;
	}

	/**
	 * 删除已读文章
	 * 
	 * @param id
	 */
	public void deleteArticle(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.delete(TABLE_NAME, ID + "=?", new String[] { id + "" });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db.isOpen())
				db.close();
		}
	}

	public void clearAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.execSQL("drop table if exists " + TABLE_NAME);
			onCreate(db);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (db.isOpen())
				db.close();
		}
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
