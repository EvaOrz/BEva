package cn.com.modernmedia;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.tsz.afinal.FinalBitmap;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import cn.com.modernmedia.breakpoint.BreakPointUtil;
import cn.com.modernmedia.breakpoint.DownloadPackageCallBack;
import cn.com.modernmedia.common.ShareTool;
import cn.com.modernmedia.db.FavDb;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.CardUriListener;
import cn.com.modernmedia.listener.FavNotifykListener;
import cn.com.modernmedia.mainprocess.BaseMainProcess;
import cn.com.modernmedia.mainprocess.MainProcessManage;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.model.SoloColumn;
import cn.com.modernmedia.solo.db.SoloArticleListDb;
import cn.com.modernmedia.solo.db.SoloDb;
import cn.com.modernmedia.solo.db.SoloFocusDb;
import cn.com.modernmedia.util.CommonCrashHandler;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.ConstData.APP_TYPE;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.MD5;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmediaslate.SlateApplication;

/**
 * 全局变量
 * 
 * @author ZhuQiao
 * 
 */
public class CommonApplication extends SlateApplication {
	public static Context mContext;
	public static boolean issueIdSame = false;// issueId是否相同
	public static boolean columnUpdateTimeSame = false;// 栏目更新时间是否相同
	public static boolean articleUpdateTimeSame = false;// 文章更新时间是否相同
	public static int lastestIssueId;// 最新一期的issueId
	public static int currentIssueId;
	public static FavNotifykListener favListener;
	private static int memorySize;
	public static boolean onSystemDestory = true;// 是否被系统回收了
	@SuppressLint("UseSparseArrays")
	private static Map<Integer, BreakPointUtil> breakMap = new HashMap<Integer, BreakPointUtil>();
	public static DownloadPackageCallBack downBack;
	public static boolean hasSolo = false;// 是否有独立栏目
	public static AdvList advList;
	public static List<String> issueIdList;
	public static Issue issue;// 当前issue
	public static Issue lastIssue;// 往期issue
	public static CardUriListener userUriListener;
	public static Class<?> drawCls;// 图片资源class
	public static Class<?> stringCls;// string资源class
	public static Class<?> mainCls;// MainActivity
	public static SoloColumn soloColumn;// 独立栏目列表
	public static MainProcessManage manage;

	public static FinalBitmap finalBitmap;
	public static APP_TYPE appType = APP_TYPE.OTHERS;// 应用类型

	/**
	 * 渠道
	 */
	public static String CHANNEL = "";

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this.getApplicationContext();
		FileManager.createNoMediaFile();
		if (ConstData.IS_DEBUG != 0)
			CommonCrashHandler.getInstance().init(this);
		initImageLoader(mContext);
	}

	/**
	 * 初始化整个app的配置
	 * 
	 * @param context
	 */
	public static void initImageLoader(Context context) {
		finalBitmap = FinalBitmap.create(context); // 初始化
		finalBitmap.configBitmapLoadThreadSize(3);// 定义线程数量

		finalBitmap.configMemoryCacheSize(memorySize);// 设置缓存大小
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void initScreenInfo() {
		super.initScreenInfo();
		initMemorySize();
	}

	private void initMemorySize() {
		final int memoryClass = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
				.getMemoryClass();
		memorySize = memoryClass * 1024 * 1024 / 8;
		PrintHelper.print("memorySize == " + memorySize);
	}

	// public static ImageDownloader getImageDownloader() {
	// return ImageDownloader.getInstance(mContext, memorySize);
	// }

	public static void clear() {
		issueIdSame = false;
		columnUpdateTimeSame = false;
		articleUpdateTimeSame = false;
		lastestIssueId = -1;
		currentIssueId = -1;
		advList = null;
		issueIdList = null;
		issue = null;
		userUriListener = null;
		hasSolo = false;
	}

	public static void setFavListener(FavNotifykListener favListener) {
		CommonApplication.favListener = favListener;
	}

	public static void notifyFav() {
		if (favListener != null)
			favListener.refreshFav();
	}

	/**
	 * 通知开始下载
	 * 
	 * @param issueId
	 * @param flag
	 *            0:success;1.loading;2.failed;3.pause
	 */
	public static void notityDwonload(int issueId, int flag) {
		if (downBack != null) {
			switch (flag) {
			case 0:
				downBack.onSuccess(issueId, null);
				break;
			case 1:
				downBack.onProcess(issueId, -1, -1);
				break;
			case 2:
				downBack.onFailed(issueId);
				break;
			case 3:
				downBack.onPause(issueId);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 刷新下载进度
	 * 
	 * @param issue
	 */
	public static void notifyDown(int issueId, long complete, long total) {
		if (downBack != null) {
			downBack.onProcess(issueId, complete, total);
		}
	}

	/**
	 * 添加往期下载
	 * 
	 * @param breakPointUtil
	 */
	public static void addPreIssueDown(int issueId,
			BreakPointUtil breakPointUtil) {
		breakMap.put(issueId, breakPointUtil);
		removePreIssueDown(issueId);
	}

	private static void removePreIssueDown(int issueId) {
		if (!breakMap.isEmpty()) {
			for (int id : breakMap.keySet()) {
				BreakPointUtil util = breakMap.get(id);
				if (id != issueId) {
					util.pause();
				}
			}
		}
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
		}
	}

	public static void exit() {
		PrintHelper.print("CommonApplication exit");
		slateExit();
		ReadDb.getInstance(mContext).close();
		FavDb.getInstance(mContext).close();
		favListener = null;
		clear();
		removePreIssueDown(-1);
		breakMap.clear();
		new ShareTool(mContext).deleteShareImages();
		BaseMainProcess.clear();
		SoloDb.getInstance(mContext).close();
		SoloArticleListDb.getInstance(mContext).close();
		SoloFocusDb.getInstance(mContext).close();
		soloColumn = null;
		manage = null;
	}

	public static void callGc() {
		System.gc();
		Runtime.getRuntime().gc();
	}

}
