package cn.com.modernmedia;

import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;
import cn.com.modernmedia.db.FavDb;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.FavNotifykListener;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.CommonCrashHandler;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.MD5;

/**
 * 全局变量
 * 
 * @author ZhuQiao
 * 
 */
public class CommonApplication extends Application {
	public static int width;
	public static int height;
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

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this.getApplicationContext();
		if (ConstData.IS_DEBUG != 0)
			CommonCrashHandler.getInstance().init(this);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		favListener = null;
		ReadDb.getInstance(mContext).close();
		FavDb.getInstance(mContext).close();
		clear();
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
}
