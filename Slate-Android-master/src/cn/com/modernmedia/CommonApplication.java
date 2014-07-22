package cn.com.modernmedia;

import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import cn.com.modernmedia.api.ImageDownloader;
import cn.com.modernmedia.db.FavDb;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.FavNotifykListener;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.CommonCrashHandler;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.MD5;
import cn.com.modernmedia.util.PrintHelper;

/**
 * 全局变量
 * 
 * @author ZhuQiao
 * 
 */
public class CommonApplication extends Application {
	public static int width;
	public static int height;
	public static float density;
	public static Context mContext;
	public static boolean issueIdSame = false;// issueId是否相同
	public static boolean columnUpdateTimeSame = false;// 栏目更新时间是否相同
	public static boolean articleUpdateTimeSame = false;// 文章更新时间是否相同
	// public static Issue oldIssue;// 上一期(当有新的一期但用户选择看上一期时选择)
	// public static Issue newIssue;// 新的一期
	public static Issue currentIssue;// 当前看的一期
	public static int oldIssueId;
	public static boolean hasNewIssue = false;
	public static boolean viewOldIssue = false;// 看上一期
	public static boolean isFetchPush = false;
	public static FavNotifykListener favListener;
	/**
	 * 渠道
	 */
	public static String CHANNEL = "";

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this.getApplicationContext();
		initScreenInfo();
		if (ConstData.IS_DEBUG != 0)
			CommonCrashHandler.getInstance().init(this);
	}

	@Override
	public void onTerminate() {
		PrintHelper.print("onTerminate");
		super.onTerminate();
		favListener = null;
		ReadDb.getInstance(mContext).close();
		FavDb.getInstance(mContext).close();
		clear();
	}

	public static ImageDownloader getImageDownloader() {
		return ImageDownloader.getInstance(mContext);
	}

	public static void clear() {
		issueIdSame = false;
		columnUpdateTimeSame = false;
		articleUpdateTimeSame = false;
		hasNewIssue = false;
		viewOldIssue = false;
		oldIssueId = -1;
		currentIssue = null;
		isFetchPush = false;
	}

	public static void setFavListener(FavNotifykListener favListener) {
		CommonApplication.favListener = favListener;
	}

	public static void notifyFav() {
		if (favListener != null)
			favListener.refreshFav();
	}

	/**
	 * 获取屏幕信息
	 */
	public void initScreenInfo() {
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		CommonApplication.width = dm.widthPixels;
		CommonApplication.height = dm.heightPixels;
		PrintHelper.print("width:" + dm.widthPixels + "height:"
				+ dm.heightPixels);

		// 获取屏幕密度
		CommonApplication.density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		int densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
		float xdpi = dm.xdpi;
		float ydpi = dm.ydpi;
		PrintHelper.print("xdpi=" + xdpi + "; ydpi=" + ydpi);
		PrintHelper.print("density=" + dm.density + "; densityDPI="
				+ densityDPI);
	}

	/**
	 * 返回手机唯一标识
	 * 
	 * @return
	 */
	public String getMyUUID() {
		final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String imie = "" + tm.getDeviceId();
		String tmSerial = "" + tm.getSimSerialNumber();
		String androidId = ""
				+ android.provider.Settings.Secure.getString(
						getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) imie.hashCode() << 32) | tmSerial.hashCode());
		return MD5.MD5Encode(deviceUuid.toString());
	}

	/**
	 * 初始化渠道号
	 */
	public void initChannel() {
		try {
			ApplicationInfo ai = getPackageManager().getApplicationInfo(
					getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			CHANNEL = bundle.getString("UMENG_CHANNEL");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (TextUtils.isEmpty(CHANNEL)) {
			CHANNEL = "bbwc";
		} else if (CHANNEL.equals("m91")) {// 91有点问题。。
			CHANNEL = "91";
		} else if (CHANNEL.equals("m360")) {
			CHANNEL = "360";
		}
	}

	public static void callGc() {
		System.gc();
		Runtime.getRuntime().gc();
	}

}
