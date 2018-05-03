package cn.com.modernmediausermodel.db;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediausermodel.model.UserCardInfoList;
import cn.com.modernmediausermodel.model.UserCardInfoList.UserCardInfo;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 用户信息列表db(如朋友、粉丝、推荐用户等列表)
 * 
 * @author user
 * 
 */
public class UserListDb extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "users_list.db";
	private static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "users_list";
	public static final String LIMIT = UserConstData.MAX_USER_ITEM_COUNT + "";
	public static final String ID = "id";
	public static final String UID = "uid";
	public static final String NICKNAME = "nickname";
	public static final String AVATAR = "avatar";
	public static final String FOLLOW_NUMBER = "follow_number";
	public static final String FANS_NUMBER = "fans_number";
	public static final String CARD_NUMBER = "card_number";
	public static final String IS_FOLLOWED = "is_followed";
	public static final String TYPE = "type"; // 用于区分列表数据类型，当前有3中，0:推荐用户；1：朋友；2：粉丝
	public static final String OFFSET_ID = "offset_id"; // 数据分页用，配合type使用
	public static final String BIND_UID = "bind_uid";// 跟用户绑定

	private MyDBHelper helper;
	private static UserListDb instance = null;

	private UserListDb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static synchronized UserListDb getInstance(Context context) {
		if (null == instance) {
			instance = new UserListDb(context);
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		helper = new MyDBHelper(TABLE_NAME);
		helper.addColumn(ID, "INTEGER PRIMARY KEY AUTOINCREMENT");// 0
		helper.addColumn(UID, "TEXT");// 1
		helper.addColumn(NICKNAME, "TEXT");// 2
		helper.addColumn(AVATAR, "TEXT");// 3
		helper.addColumn(FOLLOW_NUMBER, "INTEGER");// 4
		helper.addColumn(FANS_NUMBER, "INTEGER");// 5
		helper.addColumn(CARD_NUMBER, "INTEGER");// 6
		helper.addColumn(IS_FOLLOWED, "INTEGER");// 7
		helper.addColumn(OFFSET_ID, "TEXT");// 8
		helper.addColumn(TYPE, "TEXT");// 9
		helper.addColumn(BIND_UID, "TEXT");// 10
		db.execSQL(helper.getSql());
	}

	/**
	 * 获取用户信息列表
	 * 
	 * @param type
	 * @param bindUid
	 * @return
	 */
	public UserCardInfoList getUserInfoList(String type, String bindUid,
			int dbId) {
		UserCardInfoList list = new UserCardInfoList();
		SQLiteDatabase db = this.getReadableDatabase();
		UserCardInfo userCardInfo;
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_NAME, null,
					getSelection(type, bindUid, dbId), null, null, null,
					OFFSET_ID + " desc", LIMIT);
			while (cursor != null && cursor.moveToNext()) {
				userCardInfo = new UserCardInfo();
				userCardInfo.setDb_id(cursor.getInt(0));
				userCardInfo.setUid(cursor.getString(1));
				userCardInfo.setNickName(cursor.getString(2));
				userCardInfo.setAvatar(cursor.getString(3));
				userCardInfo.setFollowNum(cursor.getInt(4));
				userCardInfo.setFansNum(cursor.getInt(5));
				userCardInfo.setCardNum(cursor.getInt(6));
				userCardInfo.setIsFollowed(cursor.getInt(7));
				list.getList().add(userCardInfo);
				list.setOffsetId(cursor.getString(8));
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
	 * 获取筛选条件
	 * 
	 * @param type
	 * @param offsetId
	 * @return
	 */
	private String getSelection(String type, String bindUid, int dbId) {
		return TYPE + "=" + type + " AND " + BIND_UID + "=" + bindUid + " AND "
				+ ID + ">" + dbId;
	}

	/**
	 * 添加用户信息
	 * 
	 * @param list
	 */
	public void addUsersInfo(List<UserCardInfo> list, String type,
			String offsetId, String bindUid) {
		if (!ParseUtil.listNotNull(list))
			return;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = null;
		try {
			db.beginTransaction();
			for (UserCardInfo userCardInfo : list) {
				ContentValues cv = createContentValues(userCardInfo, type,
						offsetId, bindUid);
				String select = BIND_UID + "=? AND " + TYPE + "=? AND " + UID
						+ "=?";
				String[] args = new String[] { bindUid, type,
						userCardInfo.getUid() };
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

	private ContentValues createContentValues(UserCardInfo userCardInfo,
			String type, String offsetId, String bindUid) {
		ContentValues cv = new ContentValues();
		cv.put(UID, userCardInfo.getUid());
		cv.put(NICKNAME, userCardInfo.getNickName());
		cv.put(AVATAR, userCardInfo.getAvatar());
		cv.put(FOLLOW_NUMBER, userCardInfo.getFollowNum());
		cv.put(FANS_NUMBER, userCardInfo.getFansNum());
		cv.put(CARD_NUMBER, userCardInfo.getCardNum());
		cv.put(IS_FOLLOWED, userCardInfo.getIsFollowed());
		cv.put(OFFSET_ID, offsetId);
		cv.put(TYPE, type);
		cv.put(BIND_UID, bindUid);
		return cv;
	}

	public void close() {
		if (null != instance) {
			instance = null;
		}
		super.close();
	}

	/**
	 * 当从服务器获取到最新的第一页数据后，清除以前的缓存
	 * 
	 * @param type
	 * @param offsetId
	 */
	public void clearTable(String type, String bindUid) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.delete(TABLE_NAME, TYPE + "=? AND " + BIND_UID + "=?",
					new String[] { type, bindUid });
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			db.execSQL("drop table if exists " + TABLE_NAME);
			onCreate(db);
		}
	}

}
