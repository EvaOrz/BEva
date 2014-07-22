package cn.com.modernmedia.util;

import java.text.SimpleDateFormat;

import android.util.Log;

/**
 * �������������
 * 
 * @author ZhuQiao
 * 
 */
public class PrintHelper {
	public static void print(String msg) {
		//if (ConstData.IS_DEBUG != 0) {
			System.out.println(msg);
		//}
	}

	public static void logE(String tag, String msg) {
		if (ConstData.IS_DEBUG != 0) {
			Log.e(tag, msg);
		}
	}

	public static void logI(String tag, String msg) {
		if (ConstData.IS_DEBUG != 0) {
			Log.i(tag, msg);
		}
	}

	public static void logV(String tag, String msg) {
		if (ConstData.IS_DEBUG != 0) {
			Log.v(tag, msg);
		}
	}

	public static void logW(String tag, String msg) {
		if (ConstData.IS_DEBUG != 0) {
			Log.w(tag, msg);
		}
	}

	public static void logD(String tag, String msg) {
		if (ConstData.IS_DEBUG != 0) {
			Log.d(tag, msg);
		}
	}

	public static void logApi(String api) {
		if (ConstData.IS_DEBUG != 0) {
			SimpleDateFormat sDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());
			String data = date + "==>" + api + "\n";
			FileManager.saveApiData(ConstData.API_LOG, data);
		}
	}
}
