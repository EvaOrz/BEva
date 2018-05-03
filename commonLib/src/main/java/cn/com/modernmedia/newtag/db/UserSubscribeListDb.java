package cn.com.modernmedia.newtag.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import cn.com.modernmedia.api.BaseOperate;
import cn.com.modernmedia.api.GetUserSubscribeListOpertate;
import cn.com.modernmedia.model.SubscribeOrderList;
import cn.com.modernmedia.model.SubscribeOrderList.SubscribeColumn;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.MyDBHelper;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 用户订阅栏目列表db
 * 
 * @author zhuqiao
 * 
 */
public class UserSubscribeListDb extends TagDbListenerImplement {
	private static final String DATABASE_NAME = "user_subscribe_list.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "user_subscribe_list";

	// column_name
	private static final String UID = "uid";// 4
	private static final String PARENT = "parent";// 5

	private static UserSubscribeListDb instance = null;

	private UserSubscribeListDb(Context context) {
		super(context, DATABASE_NAME, DATABASE_VERSION, TABLE_NAME);
	}

	public static synchronized UserSubscribeListDb getInstance(Context context) {
		if (null == instance) {
			instance = new UserSubscribeListDb(context);
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
		helper.addColumn(UID, "TEXT");
		helper.addColumn(PARENT, "TEXT");
		db.execSQL(helper.getSql());
	}

	/**
	 * 获取用户订阅列表
	 * 
	 * @param o
	 * @param uid
	 * @return
	 */
	public synchronized Entry getEntry(BaseOperate o, String uid) {
		if (!(o instanceof GetUserSubscribeListOpertate))
			return null;
		GetUserSubscribeListOpertate operate = (GetUserSubscribeListOpertate) o;
		SQLiteDatabase db = this.getReadableDatabase();
		SubscribeOrderList list = operate.getSubscribeOrderList();
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_NAME, null, UID + "=?",
					new String[] { uid }, null, null, null);
			while (cursor != null && cursor.moveToNext()) {
				if (TextUtils.isEmpty(list.getUid())) {
					list.setAppId(cursor.getInt(1));
					list.setUid(cursor.getString(4));
				}
				SubscribeColumn column = new SubscribeColumn();
				column.setName(cursor.getString(2));
				column.setParent(cursor.getString(5));
				list.getColumnList().add(column);
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
	 * 添加
	 */
	@Override
	public synchronized void addEntry(Entry entry) {
		if (!(entry instanceof SubscribeOrderList))
			return;
		SubscribeOrderList orderList = (SubscribeOrderList) entry;
		if (!ParseUtil.listNotNull(orderList.getColumnList()))
			return;
		int appid = orderList.getAppId();
		String uid = orderList.getUid();
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.beginTransaction();
			for (SubscribeColumn column : orderList.getColumnList()) {
				ContentValues cv = createContentValues(appid, uid, column);
				db.delete(TABLE_NAME, TAG_NAME + "=? and " + PARENT + "=?",
						new String[] { column.getName(), column.getParent() });
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
		if (obj == null || obj.length != 3)
			return cv;
		if (!(obj[2] instanceof SubscribeColumn))
			return cv;
		SubscribeColumn column = (SubscribeColumn) obj[2];
		cv.put(APP_ID, ParseUtil.stoi(obj[0].toString()));
		cv.put(TAG_NAME, column.getName());
		cv.put(OFFSET, "");
		cv.put(UID, obj[1].toString());
		cv.put(PARENT, column.getParent());
		return cv;
	}

	/**
	 * 判断是否已订阅
	 * 
	 * @param tagName
	 * @return
	 */
	public boolean hasSubscribe(String tagName, String parent) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		try {
			String where = TAG_NAME + " = " + "'" + tagName + "'";
			if (!TextUtils.isEmpty(parent)) {
				where += " and " + PARENT + " = " + "'" + parent + "'";
			}
			cursor = db.query(TABLE_NAME, null, where, null, null, null, null);
			if (cursor != null && cursor.moveToNext()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return false;
	}

	public void clearTable(String uid) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.delete(TABLE_NAME, UID + "=?", new String[] { uid });
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
}
