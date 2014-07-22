package cn.com.modernmediausermodel.db;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.com.modernmedia.db.MyDBHelper;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.model.Users;

/**
 * 用户信息db
 * 
 * @author user
 * 
 */
public class UserInfoDb extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "users_info.db";
	private static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "users_info";

	public static final String ID = "id";
	public static final String UID = "uid";
	public static final String USERNAME = "username";
	public static final String NICKNAME = "nickname";
	public static final String AVATAR = "avatar";

	private MyDBHelper helper;
	private static UserInfoDb instance = null;

	private UserInfoDb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static synchronized UserInfoDb getInstance(Context context) {
		if (null == instance) {
			instance = new UserInfoDb(context);
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		helper = new MyDBHelper(TABLE_NAME);
		helper.addColumn(ID, "INTEGER PRIMARY KEY AUTOINCREMENT");// 0
		helper.addColumn(UID, "TEXT");// 1
		helper.addColumn(USERNAME, "TEXT");// 2
		helper.addColumn(NICKNAME, "TEXT");// 3
		helper.addColumn(AVATAR, "TEXT");// 4
		db.execSQL(helper.getSql());
	}

	/**
	 * 获取用户信息
	 * 
	 * @return
	 */
	public Users getUsersInfo() {
		Users users = new Users();
		SQLiteDatabase db = this.getReadableDatabase();
		User user;
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
			while (cursor != null && cursor.moveToNext()) {
				user = new User();
				user.setUid(cursor.getString(1));
				user.setUserName(cursor.getString(2));
				user.setNickName(cursor.getString(3));
				user.setAvatar(cursor.getString(4));
				users.getUserList().add(user);
				users.getUserInfoMap().put(user.getUid(), user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			if (db.isOpen())
				db.close();
		}
		return users;
	}

	/**
	 * 添加用户信息
	 * 
	 * @param list
	 */
	public void addUsersInfo(List<User> list) {
		if (!ParseUtil.listNotNull(list))
			return;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = null;
		try {
			db.beginTransaction();
			for (User user : list) {
				ContentValues cv = createContentValues(user);
				String select = UID + "=?";
				String[] args = new String[] { user.getUid() };
				cursor = db.query(TABLE_NAME, null, select, args, null, null,
						null);
				if (cursor.moveToNext()) {
					db.update(TABLE_NAME, cv, select, args);
				} else {
					db.insert(TABLE_NAME, null, cv);
				}
			}
			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			if (db.isOpen())
				db.close();
		}
	}

	private ContentValues createContentValues(User user) {
		ContentValues cv = new ContentValues();
		cv.put(UID, user.getUid());
		cv.put(USERNAME, user.getUserName());
		cv.put(NICKNAME, user.getNickName());
		cv.put(AVATAR, user.getAvatar());
		return cv;
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
