package cn.com.modernmedia.common;

import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import cn.com.modernmedia.R;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * 
 * @version 1.0
 * @created 2012-3-21
 *          $hostname/v($version)/app_($appid)/issue_$issueId/articles/
 *          $articleId
 *          /share-$deviceType-$dataType-$issueId-$columnId-$articleId-
 *          $pageNumber-$shareType_$articleUpdateTime.html $shareType
 *          分享类型,(01:邮件,02:微博,03:微信,04:短信,05:LinkedIn,06:evernote)
 */
public class UIHelper {

	public final static int LISTVIEW_ACTION_INIT = 0x01;
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;

	public final static int LISTVIEW_DATA_MORE = 0x01;
	public final static int LISTVIEW_DATA_LOADING = 0x02;
	public final static int LISTVIEW_DATA_FULL = 0x03;
	public final static int LISTVIEW_DATA_EMPTY = 0x04;

	public final static int LISTVIEW_DATATYPE_NEWS = 0x01;
	public final static int LISTVIEW_DATATYPE_BLOG = 0x02;
	public final static int LISTVIEW_DATATYPE_POST = 0x03;
	public final static int LISTVIEW_DATATYPE_TWEET = 0x04;
	public final static int LISTVIEW_DATATYPE_ACTIVE = 0x05;
	public final static int LISTVIEW_DATATYPE_MESSAGE = 0x06;
	public final static int LISTVIEW_DATATYPE_COMMENT = 0x07;

	public final static int REQUEST_CODE_FOR_RESULT = 0x01;
	public final static int REQUEST_CODE_FOR_REPLY = 0x02;

	/** 表情图片匹配 */
	private static Pattern facePattern = Pattern
			.compile("\\[{1}([0-9]\\d*)\\]{1}");

	/** 全局web样式 */
	public final static String WEB_STYLE = "<style>* {font-size:16px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
			+ "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
			+ "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;} "
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";

	/**
	 * 显示登录页面
	 * 
	 * @param activity
	 */
	@SuppressWarnings("unused")
	private static void showLoginDialog(Context context) {
		/*
		 * Intent intent = new Intent(context,LoginDialog.class); if(context
		 * instanceof Main) intent.putExtra("LOGINTYPE",
		 * LoginDialog.LOGIN_MAIN); else if(context instanceof Setting)
		 * intent.putExtra("LOGINTYPE", LoginDialog.LOGIN_SETTING); else
		 * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * context.startActivity(intent);
		 */
	}

	/**
	 * 调用系统安装了的应用分享
	 * 
	 * @param context
	 * @param title
	 * @param url
	 */
	private static void showShareMore(Activity context, final String title,
			final String url) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
		intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
		context.startActivity(Intent.createChooser(intent, "选择分享"));
	}

	/**
	 * 调用系统安装了的应用分享
	 * 
	 * @param context
	 * @param title
	 * @param url
	 */
	private static void showShareEmail(Activity context, final String title,
			final String url) {
		Intent i = new Intent(Intent.ACTION_SEND);
		// i.setType("text/plain"); //模拟器请使用这行
		i.setType("message/rfc822"); // 真机上使用这行
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { "your@mail.com" });
		i.putExtra(Intent.EXTRA_SUBJECT, title);
		i.putExtra(Intent.EXTRA_TEXT, title + " " + url);
		context.startActivity(Intent.createChooser(i, "邮件分享"));
	}

	/**
	 * 分享到'新浪微博'或'腾讯微博'的对话框
	 * 
	 * @param context
	 *            当前Activity
	 * @param title
	 *            分享的标题
	 * @param url
	 *            分享的链接
	 * @分享链接由接口获得
	 */
	public static void showShareDialog(final Activity context,
			final String title, final String url) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.btn_star);
		builder.setTitle(R.string.share);
		builder.setItems(R.array.app_share_items,
				new DialogInterface.OnClickListener() {
					AppConfig cfgHelper = AppConfig.getAppConfig(context);
					AccessInfo access = cfgHelper.getAccessInfo();

					public void onClick(DialogInterface arg0, int arg1) {
						switch (arg1) {
						case 0:// 新浪微博
								// 分享的内容
							final String shareMessage = title + " " + url;
							// 初始化微博
							if (SinaWeiboHelper.isWeiboNull()) {
								SinaWeiboHelper.initWeibo();
							}
							// 判断之前是否登陆过
							if (access != null) {
								SinaWeiboHelper.progressDialog = new ProgressDialog(
										context);
								SinaWeiboHelper.progressDialog
										.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								SinaWeiboHelper.progressDialog
										.setMessage(context
												.getString(R.string.sharing));
								SinaWeiboHelper.progressDialog
										.setCancelable(true);
								SinaWeiboHelper.progressDialog.show();
								new Thread() {
									public void run() {
										SinaWeiboHelper.setAccessToken(
												access.getAccessToken(),
												access.getAccessSecret(),
												access.getExpiresIn());
										SinaWeiboHelper.shareMessage(context,
												shareMessage);
									}
								}.start();
							} else {
								SinaWeiboHelper
										.authorize(context, shareMessage);
							}
							break;
						case 1:// 腾讯微博
							QQWeiboHelper.shareToQQ(context, title, url);
							break;
						case 2:// 邮件分享
							showShareEmail(context, title, url);
							break;
						case 3:// 更多
							showShareMore(context, title, url);
							break;
						}
					}
				});
		builder.create().show();
	}

	/**
	 * url跳转
	 * 
	 * @param context
	 * @param url
	 */
	private static void showUrlRedirect(Context context, String url) {
		// URLs urls = URI.parseURL(url);
		// if(urls != null){
		// showLinkRedirect(context, urls.getObjType(), urls.getObjId(),
		// urls.getObjKey());
		// }else{
		openBrowser(context, url);
		// }
	}

	@SuppressWarnings("unused")
	private static void showLinkRedirect(Context context, int objType,
			int objId, String objKey) {
		openBrowser(context, objKey);
		/*
		 * switch (objType) {
		 * 
		 * case URLs.URL_OBJ_TYPE_NEWS: showNewsDetail(context, objId); break;
		 * case URLs.URL_OBJ_TYPE_QUESTION: showQuestionDetail(context, objId);
		 * break; case URLs.URL_OBJ_TYPE_QUESTION_TAG:
		 * showQuestionListByTag(context, objKey); break; case
		 * URLs.URL_OBJ_TYPE_SOFTWARE: showSoftwareDetail(context, objKey);
		 * break; case URLs.URL_OBJ_TYPE_ZONE: showUserCenter(context, objId,
		 * objKey); break; case URLs.URL_OBJ_TYPE_TWEET:
		 * showTweetDetail(context, objId); break; case URLs.URL_OBJ_TYPE_BLOG:
		 * showBlogDetail(context, objId); break;
		 * 
		 * //case URLs.URL_OBJ_TYPE_OTHER:
		 * 
		 * //break; }
		 */
	}

	/**
	 * 打开浏览器
	 * 
	 * @param context
	 * @param url
	 */
	private static void openBrowser(Context context, String url) {
		try {
			Uri uri = Uri.parse(url);
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			context.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
			ToastMessage(context, "无法浏览此网页", 500);
		}
	}

	/**
	 * 获取webviewClient对象
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private static WebViewClient getWebViewClient() {
		return new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				showUrlRedirect(view.getContext(), url);
				return true;
			}
		};
	}

	/**
	 * 组合回复引用文本
	 * 
	 * @param name
	 * @param body
	 * @return
	 */
	@SuppressWarnings("unused")
	private static SpannableString parseQuoteSpan(String name, String body) {
		SpannableString sp = new SpannableString("回复：" + name + "\n" + body);
		// 设置用户名字体加粗、高亮
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 3,
				3 + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), 3,
				3 + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return sp;
	}

	/**
	 * 弹出Toast消息
	 * 
	 * @param msg
	 */
	@SuppressWarnings("unused")
	private static void ToastMessage(Context cont, String msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	@SuppressWarnings("unused")
	private static void ToastMessage(Context cont, int msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	private static void ToastMessage(Context cont, String msg, int time) {
		Toast.makeText(cont, msg, time).show();
	}

	/**
	 * 点击返回监听事件
	 * 
	 * @param activity
	 * @return
	 */
	@SuppressWarnings("unused")
	private static View.OnClickListener finish(final Activity activity) {
		return new View.OnClickListener() {
			public void onClick(View v) {
				activity.finish();
			}
		};
	}
}
