package cn.com.modernmediaslate.unit;

import android.content.Context;
import android.text.TextUtils;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;

import java.util.List;
import java.util.Map;

/**
 * 数据类型转换
 *
 * @author zhuqiao
 */
public class ParseUtil {
    /**
     * string to int
     *
     * @param str
     */
    public static int stoi(String str) {
        if (TextUtils.isEmpty(str)) return 0;
        int i = 0;
        try {
            i = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * string to int
     *
     * @param str
     * @param default_i 默认值
     * @return
     */
    public static int stoi(String str, int default_i) {
        if (TextUtils.isEmpty(str)) return default_i;
        int i = 0;
        try {
            i = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return default_i;
        }
        return i;
    }

    /**
     * string to float
     *
     * @param str
     * @return
     */
    public static float stof(String str) {
        if (TextUtils.isEmpty(str)) return 0;
        float f = 0;
        try {
            f = Float.parseFloat(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return f;
    }

    /**
     * string to float
     *
     * @param str
     * @param defaule_f 默认值
     * @return
     */
    public static float stof(String str, float defaule_f) {
        if (TextUtils.isEmpty(str)) return defaule_f;
        float f = 0;
        try {
            f = Float.parseFloat(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaule_f;
        }
        return f;
    }

    /**
     * string to double
     *
     * @param str
     * @return
     */
    public static double stod(String str) {
        if (TextUtils.isEmpty(str)) return 0.00;
        double d = 0.00;
        try {
            d = Double.parseDouble(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return d;
    }

    /**
     * string to double
     *
     * @param str
     * @param defaule_d 默认值
     * @return
     */
    public static double stod(String str, double defaule_d) {
        if (TextUtils.isEmpty(str)) return defaule_d;
        double d = 0.00;
        try {
            d = Double.parseDouble(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaule_d;
        }
        return d;
    }

    /**
     * string to long
     *
     * @param str
     * @return
     */
    public static long stol(String str) {
        if (TextUtils.isEmpty(str)) return 0;
        long l = 0;
        try {
            l = Long.parseLong(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return l;
    }

    /**
     * string to long
     *
     * @param default_l
     * @return
     */
    public static long stol(String str, long default_l) {
        if (TextUtils.isEmpty(str)) return default_l;
        long l = 0;
        try {
            l = Long.parseLong(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return default_l;
        }
        return l;
    }

    public static String parseString(String res, Object... args) {
        return String.format(res, args);
    }

    public static String parseString(Context context, int resId, Object... args) {
        return String.format(context.getString(resId), args);
    }

    public static <T> boolean listNotNull(List<T> list) {
        return list != null && !list.isEmpty();
    }

    /**
     * map是否包含key
     *
     * @param map
     * @param key
     * @return
     */
    public static boolean mapContainsKey(Map<? extends Object, ? extends Object> map, Object key) {
        return map != null && !map.isEmpty() && map.containsKey(key);
    }

    /**
     * 根据key从字典中取得字符串，若key不存在，则返回默认值
     *
     * @param dic
     * @param key
     * @param defVal
     * @return
     */
    public static String getStringValue(NSDictionary dic, String key, String defVal) {
        return dic != null && dic.containsKey(key) ? dic.objectForKey(key).toString() : defVal;
    }

    /**
     * 根据key从字典中取得整形值，若key不存在，则返回默认值
     *
     * @param dic
     * @param key
     * @param defVal
     * @return
     */
    public static int getIntValue(NSDictionary dic, String key, int defVal) {
        return dic != null && dic.containsKey(key) ? ((NSNumber) dic.objectForKey(key)).intValue() : defVal;
    }

    /**
     * 防止string为空
     *
     * @param value
     * @return
     */
    public static String initString(String value) {
        return value == null ? "" : value;
    }


}
