package cn.com.modernmediausermodel.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmediaslate.unit.DateFormatTool;

/**
 * 保存数据
 *
 * @author ZhuQiao
 */
@SuppressLint("UseSparseArrays")
public class UserDataHelper {
    public static final String SINA_LOGINED = "sina_logined_"; // 存储新浪用户ID对应的用户名
    public static final String QQ_LOGINED = "qq_logined_"; // 存储QQ用户ID对应的用户名
    public static final String WEIXIN_LOGINED = "weinxin_logined_"; // 存储微信用户ID对应的用户名
    public static final String LAST_ID = "last_id"; // 最后一个通知消息ID
    public static final String IS_FIRST_USE_COIN = "coin_"; // 标识用户是否第一次使用金币商城，后接用户ID
    public static final String LOGIN_DATE = "login_date_";// 返回用户最近一次登录的日期
    public static final String UNSUBMIT_COIN = "un_submit_coin_";// 存储用户没有成功添加的积分（主要存储因网络问题打开应用时没有添加的积分）

    private static final String SEARCH_KEY_SPLITE = "@#@";// 搜索词分隔符
    private static final int SEARCH_KEY_MAX = 50;// 历史搜索词存储最大条数
    public static final String SEARCH_KEY = "serach_key";//历史搜索词
    private static SharedPreferences mPref;

    private static SharedPreferences getPref(Context context) {
        if (mPref == null) {
            mPref = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return mPref;
    }

    /**
     * 保存
     *
     * @param context
     * @param keywork
     * @return
     */
    public static List<String> saveSearchHistory(Context context, String keywork) {
        List<String> currList = loadSearchHistory(context);

        if (TextUtils.isEmpty(keywork)) {
            // 容错
            return currList;
        }

        if (currList.contains(keywork)) {
            // 去重
            currList.remove(keywork);
        }

        if (currList.size() == SEARCH_KEY_MAX) {
            // 即将超出最大值，删掉第一个
            currList.remove(0);
        }

        currList.add(keywork);

        String value = "";
        for (String key : currList) {
            value += key + SEARCH_KEY_SPLITE;
        }

        value = value.substring(0, value.length() - SEARCH_KEY_SPLITE.length());

        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putString(SEARCH_KEY, value);
        editor.commit();

        return currList;
    }

    /**
     * 取值
     *
     * @param context
     */
    public static List<String> loadSearchHistory(Context context) {
        List<String> list = new ArrayList<String>();

        String values = getPref(context).getString(SEARCH_KEY, "");
        if (TextUtils.isEmpty(values))
            return list;

        String[] arr = values.split(SEARCH_KEY_SPLITE);
        if (arr != null && arr.length > 0)
            for (int i = 0; i < arr.length; i++) {
                list.add(arr[i]);
            }

        return list;
    }

    /**
     * 清除历史搜索数据
     *
     * @param context
     * @return
     */
    public static void cleanSearchHistory(Context context) {
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putString(SEARCH_KEY, "");
        editor.commit();
    }

    /**
     * 存储本次通知最近的消息的ID
     *
     * @param context
     * @param lastId
     */
    public static void saveMessageLastId(Context context, int lastId) {
        Editor editor = getPref(context).edit();
        editor.putInt(LAST_ID, lastId);
        editor.commit();
    }

    /**
     * 获取LASTID
     *
     * @param context
     * @return
     */
    public static int getMessageLastId(Context context) {
        return getPref(context).getInt(LAST_ID, 0);
    }

    /**
     * 存储新浪ID及对应的用户名
     *
     * @param context
     * @param userName
     */
    public static void saveSinaLoginedName(Context context, String sinaId,
                                           String userName) {
        Editor editor = getPref(context).edit();
        editor.putString(SINA_LOGINED + sinaId, userName);
        editor.commit();
    }

    /**
     * 获取新浪ID及对应的用户名
     *
     * @param context
     * @return
     */
    public static String getSinaLoginedName(Context context, String sinaId) {
        return getPref(context).getString(SINA_LOGINED + sinaId, "");
    }

    /**
     * 存储qq用户openid及对应的用户名
     *
     * @param context
     * @param userName
     */
    public static void saveQqLoginedName(Context context, String openId,
                                         String userName) {
        Editor editor = getPref(context).edit();
        editor.putString(QQ_LOGINED + openId, userName);
        editor.commit();
    }

    /**
     * 获取qq用户openid及对应的用户名
     *
     * @param context
     * @return
     */
    public static String getQqLoginedName(Context context, String openId) {
        return getPref(context).getString(QQ_LOGINED + openId, "");
    }

    /**
     * 存储微信用户openid及对应的用户名
     *
     * @param context
     * @param userName
     */
    public static void saveWeinxinLoginedName(Context context, String openId,
                                              String userName) {
        Editor editor = getPref(context).edit();
        editor.putString(WEIXIN_LOGINED + openId, userName);
        editor.commit();
    }

    /**
     * 获取微信用户openid及对应的用户名
     *
     * @param context
     * @return
     */
    public static String getWeixinLoginedName(Context context, String openId) {
        return getPref(context).getString(WEIXIN_LOGINED + openId, "");
    }

    /**
     * 存储是否第一次使用金币商城
     *
     * @param context
     * @param uid
     */
    public static void saveIsFirstUseCoin(Context context, String uid,
                                          boolean isFirstLogin) {
        Editor editor = getPref(context).edit();
        editor.putBoolean(IS_FIRST_USE_COIN + uid, isFirstLogin);
        editor.commit();
    }

    /**
     * 取得是否第一次登录
     *
     * @param context
     * @return
     */
    public static boolean getIsFirstUseCoin(Context context, String uid) {
        return getPref(context).getBoolean(IS_FIRST_USE_COIN + uid, true);
    }

    /**
     * 当请求接口失败，清除登录日期缓存
     *
     * @param context
     * @param uid
     */
    public static void clearLoginDate(Context context, String uid) {
        Editor editor = getPref(context).edit();
        editor.putString(LOGIN_DATE + uid, "");
        editor.commit();
    }

    /**
     * 保存用户最近一次登录的日期
     *
     * @param context
     * @param uid
     * @action 进入应用
     * @action 登录成功
     */
    private static void saveLoginDate(Context context, String uid) {
        Editor editor = getPref(context).edit();
        editor.putString(LOGIN_DATE + uid,
                DateFormatTool.format(System.currentTimeMillis(), "yyyy-MM-dd"));
        editor.commit();
    }

    /**
     * 获取用户最近一次登录的日期
     *
     * @param context
     * @param uid
     * @return
     */
    private static String getLoginDate(Context context, String uid) {
        return getPref(context).getString(LOGIN_DATE + uid, "");
    }

    /**
     * 保存用户没有成功添加的操作，以便下一次添加
     *
     * @param context
     * @param uid
     * @param names
     * @action 进入应用
     * @action 登录
     */
    public static void saveUnSubmitCoin(Context context, String uid,
                                        String names) {
        String newNames = names + "," + getUnSubmitCoin(context, uid);
        Editor editor = getPref(context).edit();
        editor.putString(UNSUBMIT_COIN + uid, newNames);
        editor.commit();
    }

    /**
     * 获取用户没有成功添加的积分
     *
     * @param context
     * @param uid
     * @return
     */
    public static String getUnSubmitCoin(Context context, String uid) {
        return getPref(context).getString(UNSUBMIT_COIN + uid, "");
    }

    /**
     * 判断用户当天是否已经登录
     *
     * @param context
     * @return
     */
    public static boolean checkHasLogin(Context context, String uid) {
        String curr = DateFormatTool.format(System.currentTimeMillis(),
                "yyyy-MM-dd");
        if (curr.equals(getLoginDate(context, uid))) {
            // 已经登录
            return true;
        } else {
            saveLoginDate(context, uid);
            return false;
        }
    }

}
