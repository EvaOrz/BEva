package cn.com.modernmedia.solo.db;

import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.GetCatIndexOperate;
import cn.com.modernmedia.db.MyDBHelper;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.ParseUtil;

/**
 * 独立栏目首页db
 * 
 * @author ZhuQiao
 * 
 */
public class SoloDb extends SQLiteOpenHelper {
	public static final String LIMIT = "100";
	private static final String DATABASE_NAME = "solo1.db";
	private static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "solo1";
	// column name
	public static final String ID = "id";// 0
	public static final String PARENTID = "parentId";// 1 区分多个独立栏目
	public static final String OFFSET = "offset";// 2 偏移值
	public static final String VALUE = "value";// 3 jsonObject数据

	private Context mContext;
	private static SoloDb instance = null;

	private SoloDb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		MyDBHelper helper = new MyDBHelper(TABLE_NAME);
		helper.addColumn(ID, "INTEGER");
		helper.addColumn(PARENTID, "INTEGER");
		helper.addColumn(OFFSET, "TEXT");
		helper.addColumn(VALUE, "TEXT");
		helper.addColumn("PRIMARY KEY", "(ID,PARENTID)");
		db.execSQL(helper.getSql());
	}

	public static synchronized SoloDb getInstance(Context context) {
		if (null == instance) {
			instance = new SoloDb(context);
		}
		return instance;
	}

	/**
	 * 获取独立栏目首页
	 * 
	 * @param parentId
	 * @param fromOffset
	 * @param toOffset
	 * @param soloColumn
	 * @return
	 */
	@SuppressLint("UseSparseArrays")
	public CatIndexArticle getSoloIndexByOffset(int parentId,
			String fromOffset, String toOffset, boolean containFL,
			boolean limit, int position) {
		SQLiteDatabase db = this.getReadableDatabase();
		GetCatIndexOperate operate = new GetCatIndexOperate(mContext, parentId
				+ "", fromOffset, toOffset, CommonApplication.soloColumn,
				position);
		CatIndexArticle index = operate.getCatIndexArticle();
		index.setId(parentId);
		Cursor cursor = null;
		try {
			cursor = db.query(
					TABLE_NAME,
					new String[] { VALUE },
					PARENTID
							+ "="
							+ parentId
							+ ModernMediaTools.checkSelection(fromOffset,
									toOffset, OFFSET, containFL), null, null,
					null, OFFSET + " desc", limit ? LIMIT : null);// limit
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
			if (ParseUtil.mapContainsKey(index.getSoloMap(),
					index.getFullKeyTag())) {
				index.getSoloMap()
						.get(index.getFullKeyTag())
						.put(1,
								SoloFocusDb.getInstance(mContext)
										.getArticleItem(parentId, position));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			if (db.isOpen())
				db.close();
		}
		return index;
	}

	/**
	 * 读取完服务器后根据它的fromOffset和toOffset批量删除这之间的数据，再把服务器上的数据全部添加进数据库
	 * 
	 * @param parentId
	 * @param list
	 */
	public void addSoloItems(int parentId, List<ArticleItem> list) {
		String fromOffset = list.get(list.size() - 1).getSoloItem().getOffset();
		String toOffset = list.get(0).getSoloItem().getOffset();
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.beginTransaction();
			db.delete(
					TABLE_NAME,
					PARENTID
							+ "="
							+ parentId
							+ ModernMediaTools.checkSelection(fromOffset,
									toOffset, OFFSET, true), null);
			for (ArticleItem item : list) {
				db.delete(
						TABLE_NAME,
						ID + "=? and " + PARENTID + "=?",
						new String[] { item.getArticleId() + "", parentId + "" });
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
		}
		return cv;
	}

	/**
	 * 
	 * @param articleId
	 * @return
	 */
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
