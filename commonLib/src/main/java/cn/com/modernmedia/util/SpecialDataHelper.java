package cn.com.modernmedia.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 特刊保存数据
 *
 * @author: zhufei
 */
public class SpecialDataHelper {

    private static SharedPreferences mPref;

    private static final String ARTICLE_UPDATETIME = "articleupdatetime";
    private static final String COLOUMNUPDATETIME = "coloumnupdatetime";

    private static SharedPreferences getPref(Context context) {
        if (mPref == null) {
            mPref = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return mPref;
    }

    /**
     * 获取当前栏目文章更新时间
     *
     * @param context
     * @return
     */
    public static String getArticleUpdateTime(Context context, String tagName) {
        return getPref(context).getString(ARTICLE_UPDATETIME + tagName, "");
    }

    /**
     * 保存最近一次获取到的栏目文章更新时间
     *
     * @param context
     * @param issueId
     */
    public static void setArticleUpdateTime(Context context, String tagName,
                                               String updateTime) {
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putString(ARTICLE_UPDATETIME + tagName, updateTime);
        editor.commit();
    }

    /**
     * 获取当前栏目更新时间
     *
     * @param context
     * @return
     */
    public static String getIndexUpdateTime(Context context, String tagName) {
        return getPref(context).getString(COLOUMNUPDATETIME + tagName, "");
    }

    /**
     * 保存最近一次获取到的栏目更新时间
     *
     * @param context
     * @param issueId
     */
    public static void setIndexUpdateTime(Context context, String tagName,
                                             String updateTime) {
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putString(COLOUMNUPDATETIME + tagName, updateTime);
        editor.commit();
    }
}
