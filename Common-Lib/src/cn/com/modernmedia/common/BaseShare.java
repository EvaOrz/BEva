package cn.com.modernmedia.common;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.EditText;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.R;
import cn.com.modernmedia.listener.ImageDownloadStateListener;
import cn.com.modernmedia.util.ModernMediaTools;

/**
 * app分享基类
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
	protected Bitmap mBitmap;

	public BaseShare(Context context, ShareDialog shareDialog) {
		mContext = context;
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
			mBitmap = null;
			afterFetchBitmap();
			return;
		}
		ModernMediaTools.showLoading(mContext, true);
		CommonApplication.finalBitmap.display(url,
				new ImageDownloadStateListener() {

					@Override
					public void loading() {
					}

					@Override
					public void loadOk(Bitmap bitmap) {
						mBitmap = bitmap;
						afterFetchBitmap();
					}

					@Override
					public void loadError() {
						mBitmap = null;
						afterFetchBitmap();
					}
				});
	}

	protected void afterFetchBitmap() {
	}

	/**
	 * 微信好友分享
	 */
	public void shareByFriend() {
	}

	/**
	 * 微信朋友圈分享
	 */
	public void shareByFriends() {
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
	}

	/**
	 * 分享至其他应用
	 */
	public void shareToOthers(Intent intent) {
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
					ModernMediaTools
							.showToast(mContext, R.string.enter_content);
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
		} else if (pack.contains(SINA)) {
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
}
