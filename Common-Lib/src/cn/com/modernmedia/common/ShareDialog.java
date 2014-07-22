package cn.com.modernmedia.common;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.adapter.ShareAdapter;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.ImageDownloadStateListener;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.model.Share;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmediaslate.model.Entry;

/**
 * 分享
 * 
 * @author ZhuQiao
 * 
 */
public abstract class ShareDialog {
	/**
	 * email
	 */
	public static final String MAIL = "mail";
	/**
	 * gmail
	 */
	public static final String GM = "gm";
	/**
	 * 新浪微博
	 */
	public static final String SINA = "com.sina.weibo";
	/**
	 * 微信
	 */
	public static final String WEIXIN = "com.tencent.mm";
	/**
	 * LinkedIn
	 */
	public static final String LINKEDIN = "com.linkedin.android";
	/**
	 * evernote
	 */
	public static final String EVERNOTE = "com.evernote.world";

	private Context mContext;
	private AlertDialog mAlertDialog;
	private ListView mListView;
	private List<Intent> mShareIntents;
	List<String> packList = new ArrayList<String>();
	private ShareAdapter adapter;
	private ShareTool tool;
	private Bitmap mBitmap;
	private Args args;
	private WeeklyArgs weeklyArgs;

	// --非iweekly应用参数
	private class Args {
		Issue issue;
		String columnId;
		String articleId;
		String url;

		public Args(Issue issue, String columnId, String articleId) {
			this.issue = issue;
			this.columnId = columnId;
			this.articleId = articleId;
		}

		public Args(Issue issue, String columnId, String articleId, String url) {
			this.issue = issue;
			this.columnId = columnId;
			this.articleId = articleId;
			this.url = url;
		}

	}

	// --iweekly参数
	private class WeeklyArgs {
		String title;
		String desc;
		String url;
		int bottomResId;// 文章页截屏底部资源

		public WeeklyArgs(String title, String desc, String url, int bottomResId) {
			this.title = title;
			this.desc = desc;
			this.url = url;
			this.bottomResId = bottomResId;
		}

	}

	public ShareDialog(Context context, Bitmap bitmap) {
		mContext = context;
		mBitmap = bitmap;
		mListView = new ListView(mContext);
		mListView.setCacheColorHint(Color.WHITE);
		mListView.setDivider(new ColorDrawable(Color.LTGRAY));
		mListView.setDividerHeight(1);
		mListView.setFadingEdgeLength(mContext.getResources()
				.getDimensionPixelOffset(R.dimen.share_list_fade_length));
		mListView.setBackgroundColor(Color.WHITE);
		mShareIntents = new ArrayList<Intent>();
		adapter = new ShareAdapter(mContext);
		mListView.setAdapter(adapter);
		tool = new ShareTool(context);
	}

	/**
	 * 非iweekly应用开始分享(不带图片)
	 * 
	 * @param issue
	 * @param columnId
	 * @param articleId
	 */
	public void startShareDefault(Issue issue, String columnId, String articleId) {
		args = new Args(issue, columnId, articleId);
		queryTargetIntentsDefault();
	}

	/**
	 * 非iweekly应用开始分享(带图片)
	 * 
	 * @param issue
	 * @param columnId
	 * @param articleId
	 */
	public void startShareDefault(Issue issue, String columnId,
			String articleId, String url) {
		args = new Args(issue, columnId, articleId, url);
		prepareShareAfterFetchBitmap(false);
	}

	/**
	 * iweekly应用开始分享
	 * 
	 * @param title
	 * @param desc
	 * @param url
	 */
	public void startShareWeekly(String title, String desc, String url,
			int bottomResId) {
		weeklyArgs = new WeeklyArgs(title, desc, url, bottomResId);
		prepareShareAfterFetchBitmap(true);
	}

	/**
	 * iweekly分享
	 * 
	 * @param title
	 * @param desc
	 * @param url
	 */
	private void prepareShareAfterFetchBitmap(final boolean isWeekly) {
		String url = "";
		if (isWeekly && weeklyArgs != null)
			url = weeklyArgs.url;
		else if (!isWeekly && args != null)
			url = args.url;
		if (!TextUtils.isEmpty(url)) {
			ModernMediaTools.showLoading(mContext, true);
			CommonApplication.getImageDownloader().download(url,
					new ImageDownloadStateListener() {

						@Override
						public void loading() {
						}

						@Override
						public void loadOk(Bitmap bitmap) {
							mBitmap = bitmap;
							afterFetchBitmap(isWeekly);
						}

						@Override
						public void loadError() {
							mBitmap = null;
							afterFetchBitmap(isWeekly);
						}
					});
		} else {
			mBitmap = null;
			afterFetchBitmap(isWeekly);
		}
	}

	private void afterFetchBitmap(boolean isWeekly) {
		if (isWeekly)
			queryTargetIntentsForWeekly();
		else
			queryTargetIntentsDefault();
	}

	/**
	 * 查询所有包好图片分享的应用
	 * 
	 */
	private void queryTargetIntentsDefault() {
		queryTargetIntents();
		showShareDialog(false);
	}

	private void queryTargetIntentsForWeekly() {
		queryTargetIntents();
		showShareDialog(true);
	}

	private void queryTargetIntents() {
		mShareIntents.clear();
		packList.clear();
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType(mBitmap == null ? "text/*" : "image/*");
		List<ResolveInfo> resInfo = mContext.getPackageManager()
				.queryIntentActivities(shareIntent, 0);
		if (!resInfo.isEmpty()) {
			for (ResolveInfo info : resInfo) {
				addIntent(info);
			}
		}
		if (mBitmap != null) {
			// 记录能浏览、保存图片的app
			Intent galleryIntent = new Intent(Intent.ACTION_VIEW);
			galleryIntent.setType("image/*");
			resInfo = mContext.getPackageManager().queryIntentActivities(
					galleryIntent, 0);
			if (!resInfo.isEmpty()) {
				ResolveInfo info = resInfo.get(0);
				addIntent(info);
			}
		}
	}

	private void addIntent(ResolveInfo info) {
		String pack = info.activityInfo.packageName;
		if (!packList.contains(pack)) {
			Intent targeted = new Intent(Intent.ACTION_SEND);
			targeted.setPackage(pack);
			mShareIntents.add(targeted);
			packList.add(pack);
		}
	}

	/**
	 * 显示分享dialog
	 */
	private void showShareDialog(final boolean isWeekly) {
		setDataToAdapter();
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
		dialogBuilder.setTitle(R.string.share_select);
		dialogBuilder.setView(mListView);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = mShareIntents.get(position);
				if (intent != null) {
					if (Intent.ACTION_SEND.equals(intent.getAction())) {
						if (isWeekly) {
							onItemClickForWeekly(intent);
						} else {
							onItemClickDefault(intent);
						}
					} else {
						if (mBitmap != null) {
							tool.saveToGallery(mBitmap);
							logAndroidSaveToImageAlbum();
							Toast.makeText(mContext,
									R.string.save_picture_success,
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(mContext,
									R.string.save_picture_fail,
									Toast.LENGTH_SHORT).show();
						}
					}
				}
				dismissDialog();
			}
		});
		if (mShareIntents.size() > 0) {
			mAlertDialog = dialogBuilder.create();
			try {
				mAlertDialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(mContext, R.string.share_none_component,
					Toast.LENGTH_SHORT).show();
		}
		ModernMediaTools.showLoading(mContext, false);
	}

	/**
	 * 非iweekly点击item，需要单独请求接口获取数据
	 * 
	 */
	private void onItemClickDefault(Intent intent) {
		if (intent != null) {
			String pack = intent.getPackage();
			if (!TextUtils.isEmpty(pack) && args != null) {
				String shareType = prepareShare(intent, pack);
				share(intent, shareType);
			}
		}
	}

	/**
	 * iweekly点击item
	 */
	private void onItemClickForWeekly(Intent intent) {
		if (intent != null) {
			String pack = intent.getPackage();
			if (!TextUtils.isEmpty(pack) && weeklyArgs != null) {
				String shareType = prepareShare(intent, pack);
				String emailBody = String.format(
						mContext.getString(R.string.share_email_html),
						weeklyArgs.title, weeklyArgs.desc);
				String extraText = String.format(
						mContext.getString(R.string.cover_share_message),
						weeklyArgs.title, weeklyArgs.desc, "");

				if (shareType.equals("01")) {
					showShareByMail(intent, weeklyArgs.title, emailBody);
				} else if (shareType.equals("02") || shareType.equals("03")) {
					if (shareType.equals("02"))
						logAndroidShareToSinaCount("", "");
					if (mContext instanceof CommonArticleActivity) {
						// iweekly文章分享，新浪微博，微信分享文章截图
						showShareWithScreen(intent, extraText,
								weeklyArgs.bottomResId);
					} else {
						showShareOther(intent, extraText);
					}
				} else {
					showShareOther(intent, extraText);
				}
			}
		}
	}

	private void setDataToAdapter() {
		adapter.clear();
		adapter.setData(mShareIntents);
	}

	private void dismissDialog() {
		if (mAlertDialog != null && mAlertDialog.isShowing()) {
			mAlertDialog.dismiss();
		}
	}

	private String prepareShare(Intent intent, String pack) {
		pack = pack.toLowerCase();
		String shareType = "04";
		if (pack.contains(MAIL) || pack.contains(GM)) {
			shareType = "01";
		} else if (pack.equals(SINA)) {
			shareType = "02";
		} else if (pack.equals(WEIXIN)) {
			shareType = "03";
		} else if (pack.equals(LINKEDIN)) {
			shareType = "05";
		} else if (pack.equals(EVERNOTE)) {
			shareType = "06";
		}
		return shareType;
	}

	/**
	 * 获取分享内容
	 * 
	 * @param intent
	 * @param issue
	 * @param columnId
	 * @param articleId
	 * @param shareType
	 */
	private void share(final Intent intent, final String shareType) {
		ModernMediaTools.showLoading(mContext, true);
		OperateController.getInstance(mContext).share(args.issue,
				args.columnId, args.articleId, shareType,
				new FetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						ModernMediaTools.showLoading(mContext, false);
						if (entry instanceof Share) {
							Share share = (Share) entry;
							String title = share.getTitle();
							String msg = share.getContent();
							String url = share.getWeburl();
							String content = msg.contains(url) ? msg : msg
									+ " " + url;

							if (shareType.equals("01")) {
								showShareByMail(intent, title, content);
							} else {
								if (shareType.equals("02"))
									logAndroidShareToSinaCount(args.articleId,
											args.columnId);
								showShareOther(intent, content);
							}
						}
					}
				});
	}

	/**
	 * 通过email分享
	 * 
	 * @param intent
	 * @param title
	 * @param content
	 */
	private void showShareByMail(Intent intent, String title, String content) {
		tool.shareByMail(intent, title, content, mBitmap);
		if (ConstData.getAppId() == 20)
			logAndroidShareToMail("", "");
		else if (args != null)
			logAndroidShareToMail(args.articleId, args.columnId);
	}

	/**
	 * 通过非email分享
	 * 
	 * @param intent
	 * @param content
	 */
	private void showShareOther(Intent intent, String content) {
		tool.shareWithoutMail(intent, content, mBitmap);
	}

	/**
	 * 
	 * @param intent
	 * @param content
	 * @param bottomResId
	 */
	private void showShareWithScreen(Intent intent, String content,
			int bottomResId) {
		tool.shareWithScreen(intent, content, bottomResId);
	}

	/**
	 * 使用新浪微博分享统计
	 */
	public abstract void logAndroidShareToSinaCount(String articleId,
			String columnId);

	/**
	 * 保存图片统计
	 */
	public abstract void logAndroidSaveToImageAlbum();

	/**
	 * 使用邮件分享
	 */
	public abstract void logAndroidShareToMail(String articleId, String columnId);
}
