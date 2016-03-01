package cn.com.modernmedia.newtag.db;

import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import cn.com.modernmedia.api.BaseOperate;
import cn.com.modernmedia.api.GetTagInfoOperate;
import cn.com.modernmedia.api.GetTagInfoOperate.TAG_TYPE;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.MyDBHelper;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 栏目列表db
 * 
 * @author zhuqiao
 * 
 */
public class TagInfoListDb extends TagDbListenerImplement {
	private static final String DATABASE_NAME = "tag_info_list.db";
	private static final int DATABASE_VERSION = 3;
	private static final String TABLE_NAME = "tag_info_list";

	// column name
	private static final String PARENT_TAG = "parent_tag";// 4
	private static final String HAVE_CHILDREN = "haveChildren";// 5
	private static final String ARTICLE_UPDATETIME = "articleupdatetime";// 6
	private static final String GROUP = "my_group";// 7
	private static final String ENABLE_SUBSCRIBE = "enablesubscribe";// 8
	private static final String DEFAULT_SUBSCRIBE = "defaultsubscribe";// 9
	private static final String IS_FIX = "isfix";// 10
	private static final String PHONE_COLUMN_PROPERTY = "phoneColumnProperty";// 11
	private static final String APP_PROPERTY = "appProperty";// 12
	private static final String ISSUE_PROPERTY = "issueProperty";// 13
	private static final String HAS_SUBSCRIBE = "hasSubscribe";// 14
	private static final String TAG_LEVEL = "tagLevel";// 15
	private static final String COLUMN_UPDATETIME = "coloumnupdatetime";// 16
	private static final String TAG_TAG_TYPE = "tag_type";// 17
	private static final String IS_RADIO = "isRadio";// 18
	private static final String TYPE = "type";// 19

	private static TagInfoListDb instance = null;
	private Context mContext;

	private TagInfoListDb(Context context) {
		super(context, DATABASE_NAME, DATABASE_VERSION, TABLE_NAME);
	}

	public static synchronized TagInfoListDb getInstance(Context context) {
		if (null == instance) {
			instance = new TagInfoListDb(context);
		}
		instance.setContext(context);
		return instance;
	}

	public void setContext(Context c) {
		mContext = c;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		MyDBHelper helper = new MyDBHelper(TABLE_NAME);
		helper.addColumn(ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
		helper.addColumn(APP_ID, "INTEGER");
		helper.addColumn(TAG_NAME, "TEXT");
		helper.addColumn(OFFSET, "TEXT");
		helper.addColumn(PARENT_TAG, "TEXT");
		helper.addColumn(HAVE_CHILDREN, "INTEGER");
		helper.addColumn(ARTICLE_UPDATETIME, "TEXT");
		helper.addColumn(GROUP, "INTEGER");
		helper.addColumn(ENABLE_SUBSCRIBE, "INTEGER");
		helper.addColumn(DEFAULT_SUBSCRIBE, "INTEGER");
		helper.addColumn(IS_FIX, "INTEGER");
		helper.addColumn(PHONE_COLUMN_PROPERTY, "TEXT");
		helper.addColumn(APP_PROPERTY, "TEXT");
		helper.addColumn(ISSUE_PROPERTY, "TEXT");
		helper.addColumn(HAS_SUBSCRIBE, "INTEGER");
		helper.addColumn(TAG_LEVEL, "INTEGER");
		helper.addColumn(COLUMN_UPDATETIME, "TEXT");
		helper.addColumn(TAG_TAG_TYPE, "INTEGER");
		helper.addColumn(IS_RADIO, "INTEGER");
		helper.addColumn(TYPE, "INTEGER");
		db.execSQL(helper.getSql());
	}

	/**
	 * 
	 * @param o
	 * @param parentTagName
	 *            父tagName
	 * @param tagName
	 *            当前tagName
	 * @param group
	 *            分组
	 * @param top
	 * @return
	 */
	public Entry getEntry(BaseOperate o, String parentTagName, String tagName,
			String group, String top, TAG_TYPE type) {
		if (!(o instanceof GetTagInfoOperate))
			return null;
		GetTagInfoOperate operate = (GetTagInfoOperate) o;
		SQLiteDatabase db = this.getWritableDatabase();
		TagInfoList tagInfoList = operate.getTagInfoList();
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_NAME, null,
					getWhere(parentTagName, tagName, group, top, type), null,
					null, null, ID + " asc");
			while (cursor != null && cursor.moveToNext()) {
				TagInfo tagInfo = getTagInfo(cursor, operate);
				if (tagInfo.getTagLevel() == 1)
					tagInfoList.getTopLevelList().add(tagInfo.getTagName());
				tagInfoList.addChild(tagInfo);
				tagInfoList.getList().add(tagInfo);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return tagInfoList;
	}

	private TagInfo getTagInfo(Cursor cursor, GetTagInfoOperate operate) {
		TagInfo tagInfo = new TagInfo();
		tagInfo.setAppId(cursor.getInt(1));
		tagInfo.setTagName(cursor.getString(2));
		tagInfo.setParent(cursor.getString(4));
		tagInfo.setHaveChildren(cursor.getInt(5));
		tagInfo.setArticleupdatetime(cursor.getString(6));
		tagInfo.setGroup(cursor.getInt(7));
		tagInfo.setEnablesubscribe(cursor.getInt(8));
		tagInfo.setDefaultsubscribe(cursor.getInt(9));
		tagInfo.setIsFix(cursor.getInt(10));
		tagInfo.setHasSubscribe(cursor.getInt(14));
		tagInfo.setTagLevel(cursor.getInt(15));
		tagInfo.setColoumnupdatetime(cursor.getString(16));
		tagInfo.setIsRadio(cursor.getInt(18));
		tagInfo.setType(cursor.getInt(19));
		String columnJson = cursor.getString(11);
		if (!TextUtils.isEmpty(columnJson)) {
			try {
				JSONObject obj = new JSONObject(columnJson);
				tagInfo.setColumnProperty(operate.parseColumnProperty(obj,
						tagInfo.getTagName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String appJson = cursor.getString(12);
		if (!TextUtils.isEmpty(appJson)) {
			try {
				JSONObject obj = new JSONObject(appJson);
				tagInfo.setAppProperty(operate.parseAppProperty(obj));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String issueJson = cursor.getString(13);
		if (!TextUtils.isEmpty(issueJson)) {
			try {
				JSONObject obj = new JSONObject(issueJson);
				tagInfo.setIssueProperty(operate.parseIssueProperty(obj));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (operate != null && operate.getType() == TAG_TYPE.APP_INFO) {
			tagInfo.getAppProperty().setUpdatetime(
					DbLoseColumn.getUpdatetime(mContext));
			tagInfo.getAppProperty().setAdvUpdateTime(
					DbLoseColumn.getAdvUpdatetime(mContext));
		}
		return tagInfo;
	}

	private String getWhere(String parentTagName, String tagName, String group,
			String top, TAG_TYPE type) {
		String where = TAG_TAG_TYPE + " = " + getType(type);
		if (type == TAG_TYPE.TREE_CAT) {
			// TODO 获取所有栏目
			return where;
		}
		if (type == TAG_TYPE.APP_INFO) {
			// TODO 获取应用信息
			return where;
		}
		if (type == TAG_TYPE.TAG_INFO) {
			// TODO 获取tag信息
			return where + " and " + TAG_NAME + " = " + "'" + tagName + "'";
		}
		if (type == TAG_TYPE.CHILD_CAT) {
			// TODO 获取子栏目信息
			return where + " and " + PARENT_TAG + " in ('" + parentTagName
					+ "')";
		}
		return where;
	}

	public synchronized void addEntry(Entry entry, TAG_TYPE type) {
		if (!(entry instanceof TagInfoList))
			return;
		TagInfoList tagInfoList = (TagInfoList) entry;
		List<TagInfo> list = tagInfoList.getList();
		if (!ParseUtil.listNotNull(list))
			return;
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.beginTransaction();
			for (TagInfo info : list) {
				ContentValues cv = createContentValues(info, type);
				db.delete(TABLE_NAME, TAG_NAME + "=? and " + PARENT_TAG + "=?",
						new String[] { info.getTagName(), info.getParent() });
				db.insert(TABLE_NAME, null, cv);
			}
			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	/**
	 * 更新taginfo
	 * 
	 * @param entry
	 * @param type
	 */
	public void updateEntry(Entry entry, TAG_TYPE type) {
		if (!(entry instanceof TagInfo))
			return;
		TagInfo info = (TagInfo) entry;
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			ContentValues cv = createContentValues(info, type);
			db.update(TABLE_NAME, cv, TAG_NAME + "=? and " + PARENT_TAG + "=?",
					new String[] { info.getTagName(), info.getParent() });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	@Override
	public ContentValues createContentValues(Object... obj) throws Exception {
		ContentValues cv = new ContentValues();
		if (obj == null || !(obj[0] instanceof TagInfo))
			return cv;
		TagInfo tagInfo = (TagInfo) obj[0];
		cv.put(APP_ID, tagInfo.getAppId());
		cv.put(TAG_NAME, tagInfo.getTagName());
		cv.put(OFFSET, "");
		cv.put(PARENT_TAG, tagInfo.getParent());
		cv.put(HAVE_CHILDREN, tagInfo.getHaveChildren());
		cv.put(ARTICLE_UPDATETIME, tagInfo.getArticleupdatetime());
		cv.put(GROUP, tagInfo.getGroup());
		cv.put(ENABLE_SUBSCRIBE, tagInfo.getEnablesubscribe());
		cv.put(DEFAULT_SUBSCRIBE, tagInfo.getDefaultsubscribe());
		cv.put(IS_FIX, tagInfo.getIsFix());
		cv.put(PHONE_COLUMN_PROPERTY, tagInfo.getColumnProperty()
				.getColumnJson());
		cv.put(APP_PROPERTY, tagInfo.getAppProperty().getAppJson());
		cv.put(ISSUE_PROPERTY, tagInfo.getIssueProperty().getIssueJson());
		cv.put(HAS_SUBSCRIBE, tagInfo.getHasSubscribe());
		cv.put(TAG_LEVEL, tagInfo.getTagLevel());
		cv.put(COLUMN_UPDATETIME, tagInfo.getColoumnupdatetime());
		cv.put(IS_RADIO, tagInfo.getIsRadio());
		cv.put(TYPE, tagInfo.getType());
		if (obj.length > 1 && (obj[1] instanceof TAG_TYPE)) {
			int type = getType((TAG_TYPE) obj[1]);
			cv.put(TAG_TAG_TYPE, type);
			if (type == 0) {
				DbLoseColumn.setUpdatetime(mContext, tagInfo.getAppProperty()
						.getUpdatetime());
				DbLoseColumn.setAdvUpdatetime(mContext, tagInfo
						.getAppProperty().getAdvUpdateTime());
			}
		}
		return cv;
	}

	/**
	 * 根据tagname,parent获取taginfo
	 * 
	 * @param tagName
	 * @param parent
	 *            为空代表取一级栏目(获取只有二级栏目的时候需要填写parent,因为二级栏目可能会出现多次)
	 * @param ignoreParent
	 *            忽略父栏目
	 * @return
	 */
	public TagInfo getTagInfoByName(String tagName, String parent,
			boolean ignoreParent) {
		TagInfo info = new TagInfo();
		SQLiteDatabase db = this.getReadableDatabase();
		GetTagInfoOperate operate = new GetTagInfoOperate();
		Cursor cursor = null;
		try {
			String where = TAG_NAME + " = " + "'" + tagName + "'";
			if (!ignoreParent) {
				if (!TextUtils.isEmpty(parent)) {
					where += " and " + PARENT_TAG + " = " + "'" + parent + "'";
				} else {
					where += " and " + TAG_LEVEL + "=1";
				}
			}
			cursor = db.query(TABLE_NAME, null, where, null, null, null, null);
			if (cursor != null && cursor.moveToNext()) {
				return getTagInfo(cursor, operate);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return info;
	}

	public void clearTable(String parentTagName, String tagName, String group,
			TAG_TYPE type) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.delete(TABLE_NAME,
					getWhere(parentTagName, tagName, group, "", type), null);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		}
	}

	private int getType(TAG_TYPE type) {
		if (type == TAG_TYPE.APP_INFO) {
			return 0;
		} else if (type == TAG_TYPE.TAG_INFO) {
			return 1;
		} else if (type == TAG_TYPE.TREE_CAT) {
			return 2;
		} else if (type == TAG_TYPE.ISSUE_LIST) {
			return 3;
		} else if (type == TAG_TYPE.CHILD_CAT) {
			return 4;
		}
		return -1;
	}

	@Override
	public synchronized void close() {
		if (null != instance) {
			instance = null;
		}
		super.close();
	}

	@Override
	public void addEntry(Entry entry) {
	}
}
