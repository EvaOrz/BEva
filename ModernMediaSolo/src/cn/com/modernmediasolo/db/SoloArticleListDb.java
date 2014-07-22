package cn.com.modernmediasolo.db;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import cn.com.modernmedia.api.GetArticleListOperate;
import cn.com.modernmedia.db.MyDBHelper;
import cn.com.modernmedia.model.ArticleList;
import cn.com.modernmedia.model.ArticleList.ArticleColumnList;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediasolo.SoloApplication;
import cn.com.modernmediasolo.unit.SoloHelper;

/**
 * 独立栏目文章列表db
 * 
 * @author ZhuQiao
 * 
 */
public class SoloArticleListDb extends SQLiteOpenHelper {
	public static final String LIMIT = "100";
	private static final String DATABASE_NAME = "soloArticleList.db";
	private static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "soloArticleList";
	// column name
	public static final String ID = "id";// 0
	public static final String PARENTID = "parentId";// 1
	public static final String OFFSET = "offset";// 2
	public static final String VALUE = "value";// 3

	private Context mContext;
	private static SoloArticleListDb instance = null;

	private SoloArticleListDb(Context context) {
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
		db.execSQL(helper.getSql());
	}

	public static synchronized SoloArticleListDb getInstance(Context context) {
		if (null == instance) {
			instance = new SoloArticleListDb(context);
		}
		return instance;
	}

	/**
	 * 获取文章列表
	 * 
	 * @param parentId
	 * @param fromOffset
	 * @param toOffset
	 * @param limit
	 *            是否需要limit
	 * @param containFL
	 *            是否包含第一个和最后一个item(在显示文章的时候true)
	 * @return
	 */
	public ArticleList getArticleListByOffset(int parentId, String fromOffset,
			String toOffset, boolean containFL, boolean limit) {
		SQLiteDatabase db = this.getReadableDatabase();
		GetArticleListOperate operate = new GetArticleListOperate(mContext,
				"0", fromOffset, toOffset, SoloApplication.soloColumn, true);
		ArticleList articleList = operate.getArticleList();
		List<String> catIds = new ArrayList<String>();
		catIds.add(String.valueOf(parentId));
		operate.initAdv("0", catIds);
		Cursor cursor = null;
		try {
			cursor = db.query(
					TABLE_NAME,
					new String[] { VALUE },
					PARENTID
							+ "="
							+ parentId
							+ SoloHelper.checkSelection(fromOffset, toOffset,
									OFFSET, containFL), null, null, null,
					OFFSET + " desc", limit ? LIMIT : null);
			ArticleColumnList column = new ArticleColumnList();
			column.setId(parentId);
			articleList.getList().add(column);
			while (cursor.moveToNext()) {
				String value = cursor.getString(0);
				if (!TextUtils.isEmpty(value)) {
					try {
						JSONObject obj = new JSONObject(value);
						if (JSONObject.NULL.equals(obj))
							continue;
						operate.parseArticleItem(obj, parentId, column,
								cursor.isFirst(), cursor.isLast());
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
		return articleList;
	}

	/**
	 * 读取完服务器后根据它的fromOffset和toOffset批量删除这之间的数据，再把服务器上的数据全部添加进数据库
	 * 
	 * @param parentId
	 * @param list
	 */
	public void addSoloArticle(int parentId, List<FavoriteItem> list) {
		String fromOffset = list.get(list.size() - 1).getOffset();
		String toOffset = list.get(0).getOffset();
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.beginTransaction();
			db.delete(
					TABLE_NAME,
					PARENTID
							+ "="
							+ parentId
							+ SoloHelper.checkSelection(fromOffset, toOffset,
									OFFSET, true), null);
			for (FavoriteItem item : list) {
				if (!item.isAdv())
					db.insert(TABLE_NAME, null,
							createContentValues(parentId, item));
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

	private ContentValues createContentValues(int parentId, FavoriteItem item) {
		ContentValues cv = new ContentValues();
		cv.put(ID, item.getId());
		cv.put(PARENTID, parentId);
		cv.put(OFFSET, item.getOffset());
		cv.put(VALUE, item.getJsonObject());
		return cv;
	}

	public boolean containThisItem(int articleId) {
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
		if (newVersion < oldVersion)
			return;
		db.execSQL("drop table if exists " + TABLE_NAME);
		onCreate(db);
	}
}
