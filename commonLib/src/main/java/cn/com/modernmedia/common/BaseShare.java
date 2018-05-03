package cn.com.modernmedia.common;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextUtils;
import android.widget.EditText;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.R;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmediaslate.listener.ImageDownloadStateListener;
import cn.com.modernmediaslate.unit.Tools;

/**
 * app分享基类
 * 
 * @flag 微博：【文章标题】文章摘要[空格]weburl
 * @flag 微信（好友/朋友圈）：标题就用文章标题，描述就是文章描述，图就是缩略图，如果文章描述为空，就用文章标题；如果没有缩略图，就用 app icon
 * @flag 邮件：主题：我在$appname看到了一篇文章，想要分享给你
 * 
 * @author user
 * 
 */
public class BaseShare {
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
	public static final String SINA_START = "com.sina";
	public static final String SINA_END = "weibo";
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

	/**
	 * 分享类型
	 * 
	 * @author user
	 * 
	 */
	protected enum SHARE_TYPE {
		MAIL, SINA, WEIXIN_FRIEND, WEIXIN_FRIENDS, LINKEDIN, EVERNOTE, OTHERS;
	}

	protected Context mContext;
	protected ShareTool shareTool;
	protected ShareDialog shareDialog;
	protected Bitmap serverBitmap;// 服务器端的图片
	protected Bitmap iconBitmap;// icon图片 只有微信微博在没有服务器端图片的情况下可以使用icon代替
	protected ArticleItem item;

	public BaseShare(Context context, ArticleItem articleItem,
			ShareDialog shareDialog) {
		mContext = context;
		if (articleItem == null)
			articleItem = new ArticleItem();
		item = articleItem;
		this.shareDialog = shareDialog;
		shareTool = new ShareTool(mContext);
	}

	/**
	 * 下载图片
	 * 
	 * @param title
	 * @param desc
	 * @param url
	 */
	protected void prepareShareAfterFetchBitmap(String url) {
		if (TextUtils.isEmpty(url)) {
			iconBitmap = getAppIcon();
			afterFetchBitmap();
			return;
		}
		Tools.showLoading(mContext, true);
		CommonApplication.finalBitmap.display(url,
				new ImageDownloadStateListener() {

					@Override
					public void loading() {
					}

					@Override
					public void loadOk(Bitmap bitmap,
							NinePatchDrawable drawable, byte[] gifByte) {
						serverBitmap = bitmap;
						afterFetchBitmap();
					}

					@Override
					public void loadError() {
						iconBitmap = getAppIcon();
						afterFetchBitmap();
					}
				});
	}

	/**
	 * 获取app icon
	 * 
	 * @return
	 */
	private Bitmap getAppIcon() {
		return BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.icon);
	}

	protected void afterFetchBitmap() {
	}

	/**
	 * 微信好友分享
	 */
	public void shareByFriend() {
		if (CommonApplication.mConfig.getHas_weixin() != 1) {
			shareTool.shareToFriend(item.getDesc());
		} else {
			checkIfShreByWeixin();
			Bitmap bitmap = serverBitmap == null ? iconBitmap : serverBitmap;
			WeixinShare.getInstance(mContext).shareImageAndTextToWeixin(
					item.getTitle(), item.getDesc(), item.getWeburl(), bitmap,
					false);
			// flurry log
			LogHelper.logShare(mContext, LogHelper.ANDROID_SHARE_WEIXIN,
					item.getWeburl(), item.getTitle());
		}
	}

	/**
	 * 微信朋友圈分享
	 */
	public void shareByFriends() {
		Bitmap bitmap = serverBitmap == null ? iconBitmap : serverBitmap;
		if (CommonApplication.mConfig.getHas_weixin() != 1) {
			shareTool.shareToMoments(bitmap);
		} else {
			checkIfShreByWeixin();
			WeixinShare.getInstance(mContext).shareImageAndTextToWeixin(
					item.getTitle(), item.getDesc(), item.getWeburl(), bitmap,
					true);
			// flurry log
			LogHelper.logShare(mContext, LogHelper.ANDROID_SHARE_WEIXIN,
					item.getWeburl(), item.getTitle());
		}
	}

	/**
	 * 点击微博分享
	 */
	public void shareByWeibo() {
	}

	/**
	 * 分享至微博
	 */
	protected void shareToWeiBo(String content) {
		// flurry log
		LogHelper.logShare(mContext, LogHelper.ANDROID_SHARE_WEIBO,
				item.getWeburl(), item.getTitle());
	}

	/**
	 * 分享至其他应用
	 */
	public void shareToOthers(Intent intent) {
		// flurry log
		LogHelper.logShare(mContext, LogHelper.ANDROID_SHARE_OTHERS,
				item.getWeburl(), item.getTitle());
	}

	/**
	 * 显示微博分享对话框
	 * 
	 * @param content
	 * @param isWeekly
	 */
	protected void showWeiboDialog(String content, final boolean isWeekly) {
		AlertDialog dialog;
		Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.share_to_weibo_title);
		final EditText editText = new EditText(mContext);
		editText.setText(content);
		builder.setView(editText);
		builder.setPositiveButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (dialog != null)
					dialog.dismiss();
			}
		}).setNegativeButton(R.string.send, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String text = editText.getText().toString();
				if (TextUtils.isEmpty(text)) {
					Tools.showToast(mContext, R.string.enter_content);
					return;
				}
				if (dialog != null)
					dialog.dismiss();
				shareToWeiBo(text);
			}
		});
		try {
			dialog = builder.create();
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取分享类型
	 * 
	 * @param intent
	 * @param pack
	 * @return
	 */
	protected String getShareType(Intent intent, String pack) {
		pack = pack.toLowerCase();
		String shareType = "04";
		if (pack.contains(MAIL) || pack.contains(GM)) {
			shareType = "01";
		} else if (pack.contains(SINA_START) && pack.contains(SINA_END)) {
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

	protected void checkIfShreByWeixin() {
		if (TextUtils.isEmpty(item.getTitle())) {
			item.setTitle(mContext.getString(R.string.app_name));
		}
		if (TextUtils.isEmpty(item.getDesc())) {
			item.setDesc(item.getTitle());
		}
	}
}