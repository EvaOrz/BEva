package cn.com.modernmedia.newtag.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

/**
 * 栏目列表、首页等数据库方法实现
 * 
 * @author zhuqiao
 * 
 */
public abstract class TagDbListenerImplement extends SQLiteOpenHelper implements
		TagDbListener {
	public static final String LIMIT = "100";
	protected static final String ID = "id";// 0
	protected static final String APP_ID = "appid";// 1
	protected static final String TAG_NAME = "tagname";// 2
	/**
	 * 偏移值
	 */
	protected static final String OFFSET = "offset";// 3

	private final String tableName;

	public TagDbListenerImplement(Context context, String dbName, int version,
			String tableName) {
		super(context, dbName, null, version);
		this.tableName = tableName;
	}

	/**
	 * 获取ContentValues对象
	 * 
	 * @param obj
	 * @return
	 */
	public abstract ContentValues createContentValues(Object... obj)
			throws Exception;

	protected String checkTagName(String tagName) {
		String result = "";
		if (TextUtils.isEmpty(tagName)) {
			return result;
		}
		if (tagName.contains(",")) {
			String arr[] = tagName.split(",");
			String where = "";
			for (String s : arr) {
				where += "'" + s + "'" + ",";
			}
			if (where.endsWith(",")) {
				where = where.substring(0, where.length() - 1);
			}
			result = TAG_NAME + " in(" + where + ")";
		} else {
			result = TAG_NAME + " = '" + tagName + "'";
		}
		return result;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			db.execSQL("drop table if exists " + tableName);
			onCreate(db);
		}
	}

}
