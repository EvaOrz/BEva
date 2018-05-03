package cn.com.modernmediaslate.unit;

import android.util.Log;

/**
 * 打印调试日志
 * 
 * @author ZhuQiao
 * 
 */
public class SlatePrintHelper {
	public static boolean is_debug = false;

	public static void print(String msg) {
		if (is_debug)
			System.out.println(msg);
	}

	public static void printErr(String msg) {
		if (is_debug)
			System.err.println(msg);
	}

	public static void logE(String tag, String msg) {
		Log.e(tag, msg);
	}

	public static void logI(String tag, String msg) {
		Log.i(tag, msg);
	}

	public static void logV(String tag, String msg) {
		Log.v(tag, msg);
	}

	public static void logW(String tag, String msg) {
		Log.w(tag, msg);
	}

	public static void logD(String tag, String msg) {
		Log.d(tag, msg);
	}
}
