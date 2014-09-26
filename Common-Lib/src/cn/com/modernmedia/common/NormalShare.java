package cn.com.modernmedia.common;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.com.modernmedia.R;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 非iweekly应用分享
 * 
 * @flag 微博：【文章标题】文章摘要[空格]weburl
 * @flag 微信（好友/朋友圈）：标题就用文章标题，描述就是文章描述，图就是缩略图，如果文章描述为空，就用文章标题；如果没有缩略图，就用 app icon
 * @flag 邮件：主题：我在$appname看到了一篇文章，想要分享给你
 * 
 * @author user
 * 
 */
public class NormalShare extends BaseShare {

	public NormalShare(Context context, ArticleItem item,
			ShareDialog shareDialog) {
		super(context, item, shareDialog);
	}

	@Override
	protected void afterFetchBitmap() {
		shareDialog.showShareDialog(mBitmap);
	}

	@Override
	public void shareByFriend() {
		super.shareByFriend();
	}

	@Override
	public void shareByFriends() {
		super.shareByFriends();
	}

	@Override
	public void shareByWeibo() {
		shareDialog.logAndroidShareToSinaCount(item.getArticleId() + "",
				item.getTagName());
		showWeiboDialog(getWeiBoContent(), false);
	}

	@Override
	protected void shareToWeiBo(String content) {
		shareTool.shareWithSina(content, mBitmap);
	}

	@Override
	public void shareToOthers(Intent intent) {
		if (intent == null || TextUtils.isEmpty(intent.getPackage()))
			return;
		String pack = intent.getPackage();
		String shareType = getShareType(intent, pack);
		if (shareType.equals("01")) {
			shareTool.shareByMail(intent, getEmailTitle(), getWeiBoContent(),
					mBitmap);
			shareDialog.logAndroidShareToMail(item.getArticleId() + "",
					item.getTagName());
		} else {
			shareTool.shareWithoutMail(intent, getWeiBoContent(), mBitmap);
		}
	}

	/**
	 * 获取邮件标题
	 * 
	 * @return
	 */
	private String getEmailTitle() {
		return ParseUtil.parseString(mContext, R.string.share_by_email_title,
				mContext.getString(R.string.app_name));
	}

	/**
	 * 获取微博分享内容（除了微信分享，其它分享内容都同微博）
	 * 
	 * @return
	 */
	private String getWeiBoContent() {
		return ParseUtil.parseString(mContext, R.string.share_wp_content,
				item.getTitle(), item.getDesc(), item.getWeburl());
	}

}
