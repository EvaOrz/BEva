package cn.com.modernmedia.common;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.model.ArticleItem;

/**
 * iweekly分享
 * 
 * @author user
 * 
 */
public class WeeklyShare extends BaseShare {

	public WeeklyShare(Context context, ArticleItem item,
			ShareDialog shareDialog) {
		super(context, item, shareDialog);
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
		super.shareByFriend();
	}

	/**
	 * 微信朋友圈分享
	 */
	@Override
	public void shareByFriends() {
		super.shareByFriends();
	}

	@Override
	public void shareByWeibo() {
		shareDialog.logAndroidShareToSinaCount("", "");
		showWeiboDialog(getContentByWeiBo(), true);
	}

	@Override
	protected void shareToWeiBo(String content) {
		if (mContext instanceof CommonArticleActivity) {
			// iweekly文章分享，新浪微博文章截图
			shareTool.shareWithScreen(content, item.getBottomResId());
		} else {
			shareTool.shareWithSina(content, mBitmap);
		}
	}

	@Override
	public void shareToOthers(Intent intent) {
		if (intent == null || TextUtils.isEmpty(intent.getPackage()))
			return;
		String pack = intent.getPackage();
		String shareType = getShareType(intent, pack);
		String emailBody = "";
		String extraText = "";

		emailBody = String.format(
				mContext.getString(R.string.share_email_html), item.getTitle(),
				item.getDesc());
		extraText = String.format(
				mContext.getString(R.string.cover_share_message),
				item.getTitle(), item.getDesc(), "");

		if (shareType.equals("01")) {
			shareTool.shareByMail(intent, item.getTitle(), emailBody, mBitmap);
			shareDialog.logAndroidShareToMail("", "");
		} else {
			shareTool.shareWithoutMail(intent, extraText, mBitmap);
		}
	}

	/**
	 * 获取iweekly新浪微博分享内容
	 * 
	 * @return
	 */
	private String getContentByWeiBo() {
		String webUrl = TextUtils.isEmpty(item.getWeburl()) ? "" : mContext
				.getString(R.string.entry_share_url) + item.getWeburl();
		String extraText = String.format(
				mContext.getString(R.string.cover_share_message),
				item.getTitle(), item.getDesc(), webUrl);
		return extraText;
	}

}
