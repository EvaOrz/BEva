package cn.com.modernmedia.util;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.text.format.Time;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.BaseFragmentActivity;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.breakpoint.BreakPointUtil;
import cn.com.modernmedia.common.ShareDialog;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.BreakPoint;
import cn.com.modernmedia.model.Cat;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;

public class ModernMediaTools {
	/**
	 * 检测网络状态
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNetWork(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
	 * 获取系统时间(m-d h:m)
	 * 
	 * @return
	 */
	public static String fetchTime() {
		Time localTime = new Time();
		localTime.setToNow();
		String tt = localTime.format("%m-%d %H:%M");
		return tt;
	}

	/**
	 * 获取slate跳转文章的id(eg 收藏index中的item，当这个item是跳转到别的文章，那么就需要收藏跳转到的文章，而不是当前这个)
	 * 
	 * @param item
	 * @return
	 */
	public static int fecthSlateArticleId(ArticleItem item) {
		if (item == null)
			return -1;
		int articleId = item.getArticleId();
		String link = "";
		// int type = item.getAdv().getAdvProperty().getIsadv();
		// if (type == 1) {// 广告
		// link = item.getAdv().getColumnAdv().getLink();
		// } else {
		link = item.getSlateLink();
		// }
		if (TextUtils.isEmpty(link))
			if (link.toLowerCase().startsWith("slate://")) {
				List<String> list = UriParse.parser(link);
				if (list.size() > 1) {
					if (list.get(0).equalsIgnoreCase("article")) {
						if (list.size() > 3) {
							articleId = ParseUtil.stoi(list.get(3), -1);
						}
					}
				}
			}
		return articleId;
	}

	/**
	 * 文章分享
	 * 
	 * @param context
	 * @param item
	 * @param issue
	 */
	public static void shareFavoriteItem(final Context context,
			FavoriteItem item, Issue issue) {
		if (item == null || issue == null)
			return;
		new ShareDialog(context, null) {

			@Override
			public void logAndroidShareToSinaCount(String articleId,
					String columnId) {
				LogHelper.logShareArticleByWeibo(context, articleId, columnId);
			}

			@Override
			public void logAndroidShareToMail(String articleId, String columnId) {
				LogHelper.logShareArticleByEmail(context, articleId, columnId);
			}

			@Override
			public void logAndroidSaveToImageAlbum() {
			}
		}.startShareDefault(issue, item.getCatid() + "", item.getId() + "");
	}

	/**
	 * 首页列表中分享
	 * 
	 * @param context
	 * @param item
	 * @param issue
	 */
	public static void shareArticleItem(final Context context,
			ArticleItem item, Issue issue) {
		if (item == null || issue == null)
			return;
		String url = "";
		if (ParseUtil.listNotNull(item.getPictureList())) {
			url = item.getPictureList().get(0);
		}
		new ShareDialog(context, null) {

			@Override
			public void logAndroidShareToSinaCount(String articleId,
					String columnId) {
				LogHelper.logShareArticleByWeibo(context, articleId, columnId);
			}

			@Override
			public void logAndroidShareToMail(String articleId, String columnId) {
				LogHelper.logShareArticleByEmail(context, articleId, columnId);
			}

			@Override
			public void logAndroidSaveToImageAlbum() {
			}
		}.startShareDefault(issue, item.getCatId() + "", item.getArticleId()
				+ "", url);
	}

	/**
	 * 显示loading dialog
	 * 
	 * @param context
	 * @param flag
	 */
	public static void showLoading(Context context, boolean flag) {
		if (context instanceof BaseActivity) {
			((BaseActivity) context).showLoadingDialog(flag);
		} else if (context instanceof BaseFragmentActivity) {
			((BaseFragmentActivity) context).showLoadingDialog(flag);
		}
	}

	/**
	 * 显示toast
	 * 
	 * @param context
	 * @param resId
	 */
	public static void showToast(Context context, int resId) {
		if (context instanceof BaseActivity) {
			((BaseActivity) context).showToast(resId);
		} else if (context instanceof BaseFragmentActivity) {
			((BaseFragmentActivity) context).showToast(resId);
		}
	}

	/**
	 * 显示toast
	 * 
	 * @param context
	 * @param resId
	 */
	public static void showToast(Context context, String resStr) {
		if (context instanceof BaseActivity) {
			((BaseActivity) context).showToast(resStr);
		} else if (context instanceof BaseFragmentActivity) {
			((BaseFragmentActivity) context).showToast(resStr);
		}
	}

	public static void showLoadView(Context context, int flag) {
		if (context instanceof BaseActivity) {
			switch (flag) {
			case 0:
				((BaseActivity) context).disProcess();
				break;
			case 1:
				((BaseActivity) context).showLoading();
				break;
			case 2:
				((BaseActivity) context).showError();
				break;
			default:
				break;
			}
		} else if (context instanceof BaseFragmentActivity) {
			switch (flag) {
			case 0:
				((BaseFragmentActivity) context).disProcess();
				break;
			case 1:
				((BaseFragmentActivity) context).showLoading();
				break;
			case 2:
				((BaseFragmentActivity) context).showError();
				break;
			default:
				break;
			}
		}
	}

	public static String getPackageFileName(String url) {
		if (!TextUtils.isEmpty(url)) {
			if (url.contains("/")) {
				url = url.substring(url.lastIndexOf("/"));
			}
			if (url.startsWith("/")) {
				url = url.substring(1);
			}
		}
		return url;
	}

	public static String getPackageFolderName(String url) {
		url = getPackageFileName(url);
		if (url.endsWith(".zip")) {
			url = url.substring(0, url.lastIndexOf(".zip"));
		}
		return url;
	}

	/**
	 * 断点下载
	 * 
	 * @param context
	 * @param issue
	 *            期信息
	 * @param breakPointUtil
	 */
	public static void downloadPackage(Context context, Issue issue,
			BreakPointUtil breakPointUtil) {
		if (issue == null || TextUtils.isEmpty(issue.getFullPackage())) {
			showToast(context, "无下载包。。。");
			return;
		}
		BreakPoint breakPoint = new BreakPoint();
		if (FileManager.containThisPackageFolder(issue.getFullPackage())) {
			// TODO 如果包含解压包，则直接加载
			breakPoint.setStatus(BreakPointUtil.DONE);
		} else if (FileManager.containThisPackage(issue.getFullPackage())) {
			// TODO 如果包含zip包,执行断点下载
			breakPoint.setStatus(BreakPointUtil.BREAK);
		} else {
			breakPoint.setStatus(BreakPointUtil.NONE);
		}
		breakPoint.setId(issue.getId());
		breakPoint.setUrl(issue.getFullPackage());
		CommonApplication.addPreIssueDown(issue.getId(), breakPointUtil);
		CommonApplication.notityDwonload(issue.getId(), 1);
		breakPointUtil.downLoad(breakPoint);
	}

	/**
	 * 是否是独立栏目
	 * 
	 * @param link
	 * @return !=-1是独立栏目；-1不是
	 */
	public static int isSoloCat(String link) {
		if (TextUtils.isEmpty(link))
			return -1;
		ArrayList<String> list = UriParse.parser(link);
		if (ParseUtil.listNotNull(list) && list.size() == 3) {
			// slate://column/32/0/
			if (list.get(0).equals("column") && list.get(2).equals("0")) {
				return ParseUtil.stoi(list.get(1), -1);
			}
		}
		return -1;
	}

	/**
	 * 获取当前cat所在catList的位置
	 * 
	 * @return
	 */
	public static int getCatPosition(String catId, Cat cat) {
		if (!TextUtils.isEmpty(catId) && cat != null
				&& ParseUtil.listNotNull(cat.getIdList())) {
			return cat.getIdList().indexOf(catId);
		}
		return -1;
	}
}
