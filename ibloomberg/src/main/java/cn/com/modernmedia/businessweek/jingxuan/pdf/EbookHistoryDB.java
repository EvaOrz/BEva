package cn.com.modernmedia.businessweek.jingxuan.pdf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.com.modernmediaslate.unit.MyDBHelper;

/**
 * 电子书阅读记录DB
 * Created by Eva. on 17/2/23.
 */

public class EbookHistoryDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ebook_history.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "ebook_his";
    public static final String CHARSET = "UTF-8";


    public static final String ID = "id";// 主键，自增
    // PDF文档id
    public static final String EBOOK_ID = "ebook_id";// 0
    // 阅读的页数
    public static final String PAGE_NUMBER = "page"; // 1

    private Context context;

    private static EbookHistoryDB instance = null;

    private MyDBHelper helper;

    private EbookHistoryDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        helper = new MyDBHelper(TABLE_NAME);
        helper.addColumn(ID, "INTEGER PRIMARY KEY AUTOINCREMENT");// 0
        helper.addColumn(EBOOK_ID, "INTEGER");// 1
        helper.addColumn(PAGE_NUMBER, "INTEGER");// 2
        db.execSQL(helper.getSql());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            onCreate(db);
        }
    }

    public static synchronized EbookHistoryDB getInstance(Context context) {
        if (null == instance) return new EbookHistoryDB(context);
        return instance;
    }


    /**
     * 是否包含当前ebook
     *
     * @param id
     * @return
     */
    public boolean containThisEbook(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {EBOOK_ID};
        Cursor c = null;
        try {
            c = db.query(TABLE_NAME, columns, EBOOK_ID + "=?", new String[]{id + ""}, null, null, null);
            return c.moveToNext();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
            if (db.isOpen()) db.close();
        }
        return false;
    }

    /**
     * 添加阅读历史
     *
     * @param hid
     */
    public void addHistory(int hid, int page_number) {
        boolean contain = containThisEbook(hid);
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(EBOOK_ID, hid);
            cv.put(PAGE_NUMBER, page_number);

            if (contain) {
                db.update(TABLE_NAME, cv, EBOOK_ID + "=?", new String[]{hid + ""});
            } else {
                db.insert(TABLE_NAME, null, cv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen()) db.close();
        }
    }

    /**
     * 获取完成度
     *
     * @param hid
     * @return
     */
    public synchronized int getHistoryPage(int hid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String columns[] = {PAGE_NUMBER};
        try {
            cursor = db.query(TABLE_NAME, columns, EBOOK_ID + "=?", new String[]{hid + ""}, null, null, null);
            while (cursor.moveToNext()) {
                return Integer.valueOf(cursor.getString(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db.isOpen()) db.close();
        }
        return 0;
    }
}
