package cn.com.modernmedia.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Handler;
import android.text.Html;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.Entry;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.model.Share;
import cn.com.modernmedia.util.LogHelper;

/**
 * 分享
 * 
 * @author ZhuQiao
 * 
 */
public class ShareManager {
	private Context mContext;
	private OperateController controller;
	private Share share;
	private String shareMessage = "";// 分享的内容
	private Handler handler = new Handler();

	public ShareManager(Context context) {
		mContext = context;
		controller = new OperateController(context);
	}

	public void showDialog(final Issue issue, final String columnId,
			final String articleId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setIcon(android.R.drawable.btn_star);
		builder.setTitle(R.string.share);
		builder.setItems(R.array.app_share_items, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				// case 0:
				// share(issue, columnId, articleId, "02");
				// break;
				case 0:
					share(issue, columnId, articleId, "01");
					break;
				default:
					break;
				}
			}
		});
		try {
			builder.create().show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void share(Issue issue, final String columnId,
			final String articleId, final String shareType) {
		if (!(mContext instanceof Activity))
			return;
		showLoading(true);
		controller.share(issue, columnId, articleId, shareType,
				new FetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								showLoading(false);
								if (entry != null && entry instanceof Share) {
									share = (Share) entry;
									String msg = share.getContent();
									String url = share.getWeburl();
									shareMessage = msg.contains(url) ? msg
											: msg + " " + url;
									if (shareType.equals("01")) {
										showShareEmail();
										LogHelper.logShareArticleByEmail(
												mContext, articleId, columnId);
									}
									// else if (shareType.equals("02")) {
									// shareToSina();
									// LogHelper.logShareArticleByWeibo(
									// mContext, articleId, columnId);
									// }
								}
							}
						});
					}
				});
	}

	/**
	 * 分享到新浪微博
	 */
	@SuppressWarnings("unused")
	private void shareToSina() {
		AppConfig cfgHelper = AppConfig.getAppConfig(mContext);
		final AccessInfo access = cfgHelper.getAccessInfo();
		// 初始化微博
		if (SinaWeiboHelper.isWeiboNull()) {
			SinaWeiboHelper.initWeibo();
		}
		// 判断之前是否登陆过
		if (access != null) {
			SinaWeiboHelper.progressDialog = new ProgressDialog(mContext);
			SinaWeiboHelper.progressDialog
					.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			SinaWeiboHelper.progressDialog.setMessage(mContext
					.getString(R.string.sharing));
			SinaWeiboHelper.progressDialog.setCancelable(true);
			SinaWeiboHelper.progressDialog.show();
			new Thread() {
				public void run() {
					SinaWeiboHelper.setAccessToken(access.getAccessToken(),
							access.getAccessSecret(), access.getExpiresIn());
					SinaWeiboHelper.shareMessage((Activity) mContext,
							shareMessage);
				}
			}.start();
		} else {
			SinaWeiboHelper.authorize((Activity) mContext, shareMessage);
		}
	}

	/**
	 * 调用系统安装了的应用分享
	 * 
	 * @param context
	 * @param title
	 * @param url
	 */
	private void showShareEmail() {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("plain/html");
		// i.setType("message/rfc822");
		// i.putExtra(Intent.EXTRA_EMAIL, new String[] { "your@mail.com"
		// });//收件人
		i.putExtra(Intent.EXTRA_SUBJECT, share.getTitle());
		i.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(shareMessage));
		((Activity) mContext).startActivity(Intent.createChooser(i,
				mContext.getString(R.string.share_by_email)));
	}

	private void showLoading(boolean flag) {
		if (mContext instanceof BaseActivity) {
			((BaseActivity) mContext).showLoadingDialog(flag);
		}
	}
}
