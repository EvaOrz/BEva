package cn.com.modernmediausermodel.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.model.Card.CardItem;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 广场db
 *
 * @author user
 */
public class RecommendCardDb extends SQLiteOpenHelper {
    public static final String LIMIT = UserConstData.MAX_CARD_ITEM_COUNT + "";
    private static final String DATABASE_NAME = "recommend_card.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "recommend_card";
    // column name
    public static final String ID = "id";
    public static final String CARD_ID = "card_id";
    public static final String UID = "uid";
    public static final String APPID = "appId";
    public static final String TYPE = "type";
    public static final String FUID = "fuid";
    public static final String CONTENTS = "contents";
    public static final String COMMENT_NUM = "comment_num";
    public static final String FAV_NUM = "fav_num";
    public static final String TIMELINEID = "timeLineId";
    public static final String IS_DEL = "is_del";
    public static final String IS_FAV = "is_fav";
    public static final String TITLE = "title";
    public static final String TIME = "time";

    private MyDBHelper helper;
    private static RecommendCardDb instance = null;

    private RecommendCardDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized RecommendCardDb getInstance(Context context) {
        if (null == instance) {
            instance = new RecommendCardDb(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        helper = new MyDBHelper(TABLE_NAME);
        helper.addColumn(ID, "INTEGER PRIMARY KEY AUTOINCREMENT");// 0
        helper.addColumn(CARD_ID, "TEXT");// 1
        helper.addColumn(UID, "TEXT");// 2
        helper.addColumn(APPID, "INTEGER");// 3
        helper.addColumn(TYPE, "INTEGER");// 4
        helper.addColumn(FUID, "TEXT");// 5
        helper.addColumn(CONTENTS, "TEXT");// 6
        helper.addColumn(COMMENT_NUM, "INTEGER");// 7
        helper.addColumn(FAV_NUM, "INTEGER");// 8
        helper.addColumn(TIMELINEID, "TEXT");// 9
        helper.addColumn(IS_DEL, "INTEGER");// 10
        helper.addColumn(IS_FAV, "INTEGER");// 11
        helper.addColumn(TITLE, "TEXT");// 12
        helper.addColumn(TIME, "TEXT");// 13
        db.execSQL(helper.getSql());
    }

    /**
     * 获取card列表
     *
     * @return
     */
    public Card getCard(String timeLine) {
        SQLiteDatabase db = this.getReadableDatabase();
        Card card = new Card();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, null, getSelection(timeLine), null,
                    null, null, TIMELINEID + " desc", LIMIT);
            List<CardItem> list = card.getCardItemList();
            while (cursor != null && cursor.moveToNext()) {
                CardItem item = new CardItem();
                item.setId(cursor.getString(1));
                item.setUid(cursor.getString(2));
                item.setAppId(cursor.getInt(3));
                item.setType(cursor.getInt(4));
                item.setFuid(cursor.getString(5));
                item.setContents(cursor.getString(6));
                item.setCommentNum(cursor.getInt(7));
                item.setFavNum(cursor.getInt(8));
                item.setTimeLineId(cursor.getString(9));
                item.setIsDel(cursor.getInt(10));
                item.setIsFav(cursor.getInt(11));
                item.setTitle(cursor.getString(12));
                item.setTime(cursor.getString(13));
                list.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            if (db.isOpen())
                db.close();
        }
        return card;
    }

    /**
     * 获取筛选条件
     *
     * @param timeLine
     * @return
     */
    private String getSelection(String timeLine) {
        if (timeLine.equals("0"))
            return null;
        return TIMELINEID + "<" + timeLine;
    }

    /**
     * 搜索功能 查询db
     *
     * @param search
     * @return
     */
    public Card getSearchCard(String search) {
        SQLiteDatabase db = this.getReadableDatabase();
        Card card = new Card();
        Cursor cursor = null;
        try {
            String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + CONTENTS + " LIKE '%" + search + "%'";
            cursor = db.rawQuery(sql, null);

            List<CardItem> list = card.getCardItemList();
            while (cursor != null && cursor.moveToNext()) {
                CardItem item = new CardItem();
                item.setId(cursor.getString(1));
                item.setUid(cursor.getString(2));
                item.setAppId(cursor.getInt(3));
                item.setType(cursor.getInt(4));
                item.setFuid(cursor.getString(5));
                item.setContents(cursor.getString(6));
                item.setCommentNum(cursor.getInt(7));
                item.setFavNum(cursor.getInt(8));
                item.setTimeLineId(cursor.getString(9));
                item.setIsDel(cursor.getInt(10));
                item.setIsFav(cursor.getInt(11));
                item.setTitle(cursor.getString(12));
                item.setTime(cursor.getString(13));
                list.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            if (db.isOpen())
                db.close();
        }
        return card;
    }

    /**
     * 添加card
     *
     * @param card
     */
    public void addCardItem(Card card) {
        if (card == null || !ParseUtil.listNotNull(card.getCardItemList())) {
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            db.beginTransaction();
            for (CardItem item : card.getCardItemList()) {
                ContentValues cv = createContentValues(item);
                String select = CARD_ID + "=?";
                String[] args = new String[]{item.getId()};
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

    private ContentValues createContentValues(CardItem item) {
        ContentValues cv = new ContentValues();
        cv.put(CARD_ID, item.getId());
        cv.put(UID, item.getUid());
        cv.put(APPID, item.getAppId());
        cv.put(TYPE, item.getType());
        cv.put(FUID, item.getFuid());
        cv.put(CONTENTS, item.getContents());
        cv.put(COMMENT_NUM, item.getCommentNum());
        cv.put(FAV_NUM, item.getFavNum());
        cv.put(TIMELINEID, item.getTimeLineId());
        cv.put(IS_DEL, item.getIsDel());
        cv.put(IS_FAV, item.getIsFav());
        cv.put(TITLE, item.getTitle());
        cv.put(TIME, item.getTime());
        return cv;
    }

    /**
     * 当从服务器获取到最新的第一页数据后，清除跟bind_uid有关的以前的缓存
     */
    public void clearTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_NAME, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (db != null)
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
