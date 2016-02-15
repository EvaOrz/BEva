package cn.com.modernmediaslate.unit;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.SlateBaseFragmentActivity;

public class Tools {
	/**
	 * 检测网络状态
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNetWork(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		/* 网络连接状态 */
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();

		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	/**
	 * 判断当前网络连接形式 true：WiFi 、false：移动网络
	 */
	public static boolean isWiFiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		/* Wi-Fi连接状态 */
		NetworkInfo wifiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		/* 移动网络连接 */
		NetworkInfo mobileNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
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
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(50);
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
	 * @param resId
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
		return SlateDataHelper.getUid(context);
	}
}
