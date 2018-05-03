package cn.com.modernmedia.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.PhonePageList;
import cn.com.modernmedia.model.ArticleItem.Picture;
import cn.com.modernmediaslate.unit.MyDBHelper;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 收藏数据库
 *
 * @author ZhuQiao
 */
public class FavDb extends SQLiteOpenHelper {
    public static final String IOS = "兼容ios";// ios的收藏时间只能是String类型
    private static final String DATABASE_NAME = "fav.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "fav";
    private static final String TABLE_NAME_TEMP = "fav_temp";
    public static final String CHARSET = "UTF-8";
    // column name
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String CATID = "catId";
    public static final String LINK = "link";

    public static final String PICTURE = "picture";
    public static final String UPDATETIME = "updateTime";
    public static final String ISSUEID = "issueid";
    public static final String TYPE = "type";
    public static final String DATE = "date";// 收藏时间。排序用

    // ===========新增
    /**
     * 默认的uid设置为0，当登录的时候成功同步了当条fav，再把uid设置为当前user的uid
     */
    public static final String UID = "uid";
    public static final String APPID = "appid";
    public static final String DESC = "desc";
    public static final String FAVDEL = "favdel";// 是否删除: 1.删除
    public static final String PAGENUM = "pagenum";
    public static final String OFFSET = "offset";
    public static final String TAG = "tag";
    public static final String SUCCESS = "success";// 是否同步成功;1。成功；0。不成功
    public static final String LEVEL = "level";// 付费 0：不需要 1：需要

    private MyDBHelper helper;
    private static FavDb instance = null;

    private FavDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getCreatSql(false));
    }

    public static synchronized FavDb getInstance(Context context) {
        if (null == instance) {
            instance = new FavDb(context);
        }
        return instance;
    }

    private String getCreatSql(boolean containNewColumn) {
        helper = new MyDBHelper(FavDb.TABLE_NAME);
        helper.addColumn(ID, "INTEGER PRIMARY KEY");// 0
        helper.addColumn(TITLE, "TEXT");// 1
        helper.addColumn(CATID, "INTEGER");// 2
        helper.addColumn(LINK, "TEXT");// 3
        helper.addColumn(PICTURE, "TEXT");// 4
        helper.addColumn(UPDATETIME, "LONG");// 5
        helper.addColumn(ISSUEID, "INTEGER");// 6
        helper.addColumn(TYPE, "TEXT");// 7
        helper.addColumn(DATE, "TEXT");// 8
        if (containNewColumn) {
            helper.addColumn(UID, "TEXT");// 9
            helper.addColumn(APPID, "TEXT");// 10
            helper.addColumn(DESC, "TEXT");// 11
            helper.addColumn(FAVDEL, "INTEGER");// 12
            helper.addColumn(PAGENUM, "INTEGER");// 13
            helper.addColumn(OFFSET, "TEXT");// 14
            helper.addColumn(TAG, "TEXT");// 15
            helper.addColumn(SUCCESS, "INTEGER");// 16
        }
        helper.addColumn(LEVEL,"INTEGER");//17
        return helper.getSql();
    }

    /**
     * 添加新字段
     */
    public void addColumn() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL(getAddColumnSql(UID, "TEXT"));// 9
            db.execSQL(getAddColumnSql(APPID, "TEXT"));// 10
            db.execSQL(getAddColumnSql(DESC, "TEXT"));// 11
            db.execSQL(getAddColumnSql(FAVDEL, "INTEGER"));// 12
            db.execSQL(getAddColumnSql(PAGENUM, "INTEGER"));// 13
            db.execSQL(getAddColumnSql(OFFSET, "TEXT"));// 14
            db.execSQL(getAddColumnSql(TAG, "TEXT"));// 15
            db.execSQL(getAddColumnSql(SUCCESS, "INTEGER"));// 16
            db.execSQL(getAddColumnSql(LEVEL, "INTEGER"));// 17
            dataTransfer(db);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen()) db.close();
        }
    }

    private String getAddColumnSql(String name, String type) {
        return "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + name + " " + type;
    }

    private void dataTransfer(SQLiteDatabase db) {
        // 将表名改为临时表
        String rename = "ALTER TABLE " + TABLE_NAME + " RENAME TO " + TABLE_NAME_TEMP;
        db.execSQL(rename);
        // 创建新表
        db.execSQL(getCreatSql(true));
        // 导入数据
        String insertSql = "INSERT INTO " + TABLE_NAME + " SELECT " + ID + "," + TITLE + "," + CATID + "," + LINK + "," + PICTURE + "," + UPDATETIME + "," + ISSUEID + "," + TYPE + "," + DATE + "," + "0" + "," + "''" + "," + "''" + "," + 0 + "," + 0 + "," + "''" + "," + "''" + "," + 0 + " FROM " + TABLE_NAME_TEMP;
        db.execSQL(insertSql);
        // 删除临时表
        db.execSQL("DROP TABLE " + TABLE_NAME_TEMP);
    }

    /**
     * 获取所有收藏(用户未登录时使用)
     *
     * @return
     */
    public List<ArticleItem> getLocalFav() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ArticleItem> list = new ArrayList<ArticleItem>();
        ArticleItem detail;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, null, null, null, null, null, DATE + " desc");
            while (cursor != null && cursor.moveToNext()) {
                detail = new ArticleItem();
                detail.setArticleId(cursor.getInt(0));
                String title = cursor.getString(1);
                detail.setTitle(title);
                Picture picture = new Picture();
                picture.setUrl(cursor.getString(4));
                detail.getThumbList().add(picture);
                detail.setUpdateTime(cursor.getLong(5));
                int type = ParseUtil.stoi(cursor.getString(7), 1);
                detail.getProperty().setType(type);

                String link = cursor.getString(3);
                if (!TextUtils.isEmpty(link)) {
                    if (type == 2) {// 图集
                        PhonePageList phonePage = new PhonePageList();
                        if (link.toLowerCase().startsWith("slate://")) {
                            phonePage.setUri(link);
                        } else {
                            phonePage.setUrl(link);
                        }
                        detail.getPageUrlList().add(phonePage);
                    } else { // 文章
                        if (link.toLowerCase().startsWith("slate://")) {
                            detail.setSlateLink(link);
                        } else {
                            PhonePageList phonePage = new PhonePageList();
                            phonePage.setUrl(link);
                            detail.getPageUrlList().add(phonePage);
                        }
                    }
                }
                // 把毫秒转换成秒
                String favtime = cursor.getString(8);
                detail.getDbData().setFavtime(favtime);
                detail.setFavTime(favtime);
                if (cursor.getColumnCount() == 17) {
                    detail.getDbData().setUid(cursor.getString(9));
                    detail.getDbData().setSuccess(cursor.getInt(16));
                    String desc = cursor.getString(11);
                    detail.setDesc(desc);
                    detail.setFavDel(cursor.getInt(12));
                    detail.setOffset(cursor.getString(14));
                    String tag = cursor.getString(15);
                    detail.setTag(tag);
                }
                detail.getProperty().setLevel(cursor.getInt(16));
                list.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db.isOpen()) db.close();
        }
        return list;
    }

    /**
     * 删除表
     */
    public void delete() {
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public void close() {
        if (null != instance) {
            instance = null;
        }
        super.close();
    }

    /**
     * 如果想变更或删除当前表，把version改变，执行此方法
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            onCreate(db);
        }
    }

}
