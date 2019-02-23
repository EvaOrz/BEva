package cn.com.modernmediaslate.unit;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.SlateBaseFragmentActivity;

import static cn.com.modernmediaslate.SlateApplication.mContext;

public class Tools {
    /**
     * 检测网络状态
     *
     * @param context
     * @return
     */
    public static boolean checkNetWork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        /* 网络连接状态 */
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * 判断当前网络连接形式 true：WiFi 、false：移动网络
     */
    public static boolean isWiFiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        /* Wi-Fi连接状态 */
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        /* 移动网络连接 */
        NetworkInfo mobileNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return wifiNetworkInfo != null && wifiNetworkInfo.isConnected();

    }

    /**
     * 当前service是否正在运行
     *
     * @param mContext
     * @param className
     * @return
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(50);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 显示loading dialog
     *
     * @param context
     * @param flag
     */
    public static void showLoading(Context context, boolean flag) {
        if (context instanceof SlateBaseActivity) {
            ((SlateBaseActivity) context).showLoadingDialog(flag);
        } else if (context instanceof SlateBaseFragmentActivity) {
            ((SlateBaseFragmentActivity) context).showLoadingDialog(flag);
        }
    }

    /**
     * 显示toast
     *
     * @param context
     * @param resId
     */
    public static void showToast(Context context, int resId) {
        if (context instanceof SlateBaseActivity) {
            ((SlateBaseActivity) context).showToast(resId);
        } else if (context instanceof SlateBaseFragmentActivity) {
            ((SlateBaseFragmentActivity) context).showToast(resId);
        }
    }

    /**
     * 显示toast
     *
     * @param context
     * @param resStr
     */
    public static void showToast(Context context, String resStr) {
        if (context instanceof SlateBaseActivity) {
            ((SlateBaseActivity) context).showToast(resStr);
        } else if (context instanceof SlateBaseFragmentActivity) {
            ((SlateBaseFragmentActivity) context).showToast(resStr);
        }
    }

    /**
     * 获取uid
     *
     * @param context
     * @return
     */
    public static String getUid(Context context) {
        String uid = SlateDataHelper.getUid(context);
        return TextUtils.isEmpty(uid) ? SlateApplication.UN_UPLOAD_UID : uid;
    }

    /**
     * 获取token
     *
     * @param context
     * @return
     */
    public static String getToken(Context context) {
        return SlateDataHelper.getToken(context);
    }

    /**
     * 获取版本号
     */
    public static String getAppVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取版本号
     */
    public static int getAppIntVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (TextUtils.isEmpty(info.versionName)) {
                return 0;
            } else return Integer.valueOf(info.versionName.replace(".", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取版本号
     */
    public static String getAppVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return info.versionCode + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }


    /**
     * 返回手机唯一标识
     *
     * @return
     */
    public static String getMyUUID(Context mContext) {
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String imie = "" + tm.getDeviceId();
        String tmSerial = "" + tm.getSimSerialNumber();


        UUID deviceUuid = new UUID(getDeviceToken(mContext).hashCode(), ((long) imie.hashCode() << 32) | tmSerial.hashCode());
        return MD5.MD5Encode(deviceUuid.toString());
    }

    /**
     * The Android ID
     * 通常被认为不可信，因为它有时为null。开发文档中说明了：这个ID会改变如果进行了出厂设置。并且，如果某个Andorid手机被Root过的话，这个ID也可以被任意改变
     *
     * @return
     */
    public static String getDeviceToken(Context mContext) {
        return "" + android.provider.Settings.Secure.getString(mContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }




    /**
     * IMEI 只对Android设备有效，水货手机可能没有
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * 判断当前手机是否有ROOT权限
     *
     * @return
     */
    public static boolean isRooted() {
        boolean bool = false;

        try {
            if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())) {
                bool = false;
            } else bool = true;
        } catch (Exception e) {
        }
        return bool;
    }

    /**
     * 获取设备网卡mac地址
     *
     * @param context
     * @return
     */
    public static String getNetMacAdress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        WifiInfo info = wifi.getConnectionInfo();

        return info.getMacAddress();
    }




}
