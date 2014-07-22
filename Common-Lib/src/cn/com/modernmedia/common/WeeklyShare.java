package cn.com.modernmedia.common;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.common.ShareDialog.WeeklyArgs;

/**
 * iweekly分享
 * 
 * @author user
 * 
 */
public class WeeklyShare extends BaseShare {
	private WeeklyArgs args;

	public WeeklyShare(Context context, WeeklyArgs args, ShareDialog shareDialog) {
		super(context, shareDialog);
		this.args = args;
	}

	@Override
	protected void afterFetchBitmap() {
		shareDialog.showShareDialog(mBitmap);
	}

	/**
	 * 微信好友分享
	 */
	@Override
	public void shareByFriend() {
		shareTool.shareToFriend(getContentByWeiBo());
	}

	/**
	 * 微信朋友圈分享
	 */
	@Override
	public void shareByFriends() {
		shareTool.shareToMoments(mBitmap);
	}

	@Override
	public void shareByWeibo() {
		shareDialog.logAndroidShareToSinaCount("", "");
		showWeiboDialog(getContentByWeiBo(), true);
	}

	@Override
	protected void shareToWeiBo(String content) {
		if (args == null)
			return;
		if (mContext instanceof CommonArticleActivity) {
			// iweekly文章分享，新浪微博文章截图
			shareTool.shareWithScreen(content, args.bottomResId);
		} else {
			shareTool.shareWithSina(content, mBitmap);
		}
	}

	@Override
	public void shareToOthers(Intent intent) {
		if (intent == null || TextUtils.isEmpty(intent.getPackage())
				|| args == null)
			return;
		String pack = intent.getPackage();
		String shareType = getShareType(intent, pack);
		String emailBody = String.format(
				mContext.getString(R.string.share_email_html), args.title,
				args.desc);
		String extraText = String.format(
				mContext.getString(R.string.cover_share_message), args.title,
				args.desc, "");

		if (shareType.equals("01")) {
			shareTool.shareByMail(intent, args.title, emailBody, mBitmap);
			shareDialog.logAndroidShareToMail("", "");
		} else {
			shareTool.shareWithoutMail(intent, extraText, mBitmap);
		}
	}

	/**
	 * 获取微信分享内容
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getContentByWeiXin() {
		return String.format(mContext.getString(R.string.cover_share_message),
				args.title, args.desc, "");
	}

	/**
	 * 获取iweekly新浪微博分享内容
	 * 
	 * @return
	 */
	private String getContentByWeiBo() {
		if (args == null)
			return "";
		String webUrl = mContext.getString(R.string.entry_share_url)
				+ args.webUrl;
		String extraText = String.format(
				mContext.getString(R.string.cover_share_message), args.title,
				args.desc, webUrl);
		return extraText;
	}
}
