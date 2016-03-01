package cn.com.modernmedia;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import cn.com.modernmedia.breakpoint.BreakPointUtil;
import cn.com.modernmedia.breakpoint.DownloadPackageCallBack;
import cn.com.modernmedia.common.ShareTool;
import cn.com.modernmedia.common.WeixinShare;
import cn.com.modernmedia.db.NewFavDb;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.FavNotifyListener;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.LastestArticleId;
import cn.com.modernmedia.newtag.mainprocess.MainProcessObservable;
import cn.com.modernmedia.util.CommonCrashHandler;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmedia.util.sina.SinaConstants;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.unit.MD5;

import com.parse.Parse;

/**
 * 全局变量
 * 
 * @author ZhuQiao
 * 
 */
public class CommonApplication extends SlateApplication {
	public static FavNotifyListener favListener;
	public static boolean onSystemDestory = true;// 是否被系统回收了
	@SuppressLint("UseSparseArrays")
	private static Map<String, BreakPointUtil> breakMap = new HashMap<String, BreakPointUtil>();
	public static DownloadPackageCallBack downBack;
	public static AdvList advList;
	public static LastestArticleId lastestArticleId;
	public static MainProcessObservable mainProcessObservable = new MainProcessObservable();

	public static Class<?> pushArticleCls;// PushArticleActivity

	public static MusicActivity musicActivity;// 全局电台播放Activity
	public static MusicService musicService;// 全局电台播放Service

	/**
	 * 渠道
	 */
	public static String CHANNEL = "";

	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}

	public void init() {
		initChannel();
		System.out.println(CHANNEL);
		mContext = this.getApplicationContext();
		SinaConstants.APP_KEY = mConfig.getWeibo_app_id();
		WeixinShare.APP_ID = mConfig.getWeixin_app_id();
		Parse.initialize(this, mConfig.getParse_app_id(),
				mConfig.getParse_client_id());
		FileManager.createNoMediaFile();
		if (ConstData.IS_DEBUG != 0)
			CommonCrashHandler.getInstance().init(this);
		mainProcessObservable.deleteObservers();
	}

	public static void clear() {
		advList = null;
		userUriListener = null;
		AppValue.clear();
	}

	public static void setFavListener(FavNotifyListener favListener) {
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
	public static void notityDwonload(String tagName, int flag) {
		if (downBack != null) {
			switch (flag) {
			case 0:
				downBack.onSuccess(tagName, null);
				break;
			case 1:
				downBack.onProcess(tagName, -1, -1);
				break;
			case 2:
				downBack.onFailed(tagName);
				break;
			case 3:
				downBack.onPause(tagName);
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
	public static void notifyDown(String tagName, long complete, long total) {
		if (downBack != null) {
			downBack.onProcess(tagName, complete, total);
		}
	}

	/**
	 * 添加往期下载
	 * 
	 * @param breakPointUtil
	 */
	public static void addPreIssueDown(String tagName,
			BreakPointUtil breakPointUtil) {
		breakMap.put(tagName, breakPointUtil);
		removePreIssueDown(tagName);
	}

	private static void removePreIssueDown(String tagName) {
		if (!breakMap.isEmpty()) {
			for (String id : breakMap.keySet()) {
				BreakPointUtil util = breakMap.get(id);
				if (!TextUtils.equals(tagName, id)) {
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
	public static String getMyUUID() {
		final TelephonyManager tm = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imie = "" + tm.getDeviceId();
		String tmSerial = "" + tm.getSimSerialNumber();
		String androidId = ""
				+ android.provider.Settings.Secure.getString(
						mContext.getContentResolver(),
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
		NewFavDb.getInstance(mContext).close();
		favListener = null;
		lastestArticleId = null;
		clear();
		removePreIssueDown("");
		breakMap.clear();
		new ShareTool(mContext).deleteShareImages();
		DataHelper.clear();
		mainProcessObservable.deleteObservers();
	}

	public static void callGc() {
		System.gc();
		Runtime.getRuntime().gc();
	}

}
