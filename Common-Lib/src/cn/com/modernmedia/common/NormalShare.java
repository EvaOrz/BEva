package cn.com.modernmedia.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.common.ShareDialog.Args;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.ImageDownloadStateListener;
import cn.com.modernmedia.model.Share;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Entry;

/**
 * 非iweekly应用分享
 * 
 * @author user
 * 
 */
public class NormalShare extends BaseShare {
	private Args args;

	public NormalShare(Context context, Args args, ShareDialog shareDialog) {
		super(context, shareDialog);
		this.args = args;
	}

	@Override
	protected void afterFetchBitmap() {
		shareDialog.showShareDialog(mBitmap);
	}

	@Override
	public void shareByFriend() {
		share(null, "03", SHARE_TYPE.WEIXIN_FRIEND);
	}

	@Override
	public void shareByFriends() {
		share(null, "03", SHARE_TYPE.WEIXIN_FRIENDS);
	}

	@Override
	public void shareByWeibo() {
		share(null, "02", SHARE_TYPE.SINA);
	}

	@Override
	protected void shareToWeiBo(String content) {
		shareTool.shareWithSina(content, mBitmap);
	}

	@Override
	public void shareToOthers(Intent intent) {
		if (intent == null || TextUtils.isEmpty(intent.getPackage())
				|| args == null)
			return;
		String pack = intent.getPackage();
		share(intent, getShareType(intent, pack), null);
	}

	/**
	 * 获取分享内容
	 * 
	 * @param intent
	 * @param shareType
	 * @param _Type
	 *            为了区分微信好友和朋友圈
	 */
	private void share(final Intent intent, final String shareType,
			final SHARE_TYPE _Type) {
		ModernMediaTools.showLoading(mContext, true);
		OperateController.getInstance(mContext).share(args.issue,
				args.columnId, args.articleId, shareType,
				new FetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						ModernMediaTools.showLoading(mContext, false);
						if (entry instanceof Share) {
							Share share = (Share) entry;
							if (ParseUtil.listNotNull(share.getPicList())) {
								fecthBitmapAfterShare(share, intent, shareType,
										_Type);
							} else {
								afterFecthShare(share, intent, shareType, _Type);
							}
						}
					}
				});
	}

	/**
	 * 非iweekly应用获取完share接口下载图片
	 * 
	 * @param share
	 * @param intent
	 * @param shareType
	 */
	private void fecthBitmapAfterShare(final Share share, final Intent intent,
			final String shareType, final SHARE_TYPE _Type) {
		CommonApplication.finalBitmap.display(share.getPicList().get(0),
				new ImageDownloadStateListener() {

					@Override
					public void loading() {
						ModernMediaTools.showLoading(mContext, true);
					}

					@Override
					public void loadOk(Bitmap bitmap) {
						ModernMediaTools.showLoading(mContext, false);
						mBitmap = bitmap;
						afterFecthShare(share, intent, shareType, _Type);
					}

					@Override
					public void loadError() {
						ModernMediaTools.showLoading(mContext, false);
						afterFecthShare(share, intent, shareType, _Type);
					}
				});
	}

	/**
	 * 非iweekly应用获取完share接口
	 * 
	 * @param share
	 * @param intent
	 * @param shareType
	 */
	private void afterFecthShare(Share share, Intent intent, String shareType,
			SHARE_TYPE _Type) {
		String title = share.getTitle();
		String msg = share.getContent();
		String url = share.getWeburl();
		String content = msg.contains(url) ? msg : msg + " " + url;

		if (shareType.equals("01")) {
			shareTool.shareByMail(intent, title, content, mBitmap);
			shareDialog.logAndroidShareToMail(args.articleId, args.columnId);
		} else if (shareType.equals("02")) {
			shareDialog.logAndroidShareToSinaCount(args.articleId,
					args.columnId);
			showWeiboDialog(content, false);
		} else if (shareType.equals("03")) {
			if (_Type == SHARE_TYPE.WEIXIN_FRIEND) {
//				WeixinShare.getInstance(mContext).shareImageAndTextToWeixin(
//						title, msg, url, mBitmap, false);
				shareTool.shareToFriend(content);
			} else if (_Type == SHARE_TYPE.WEIXIN_FRIENDS) {
//				WeixinShare.getInstance(mContext).shareImageAndTextToWeixin(
//						title, msg, url, mBitmap, true);
				shareTool.shareToMoments(mBitmap);
			}
		} else {
			shareTool.shareWithoutMail(intent, content, mBitmap);
		}
	}
}
