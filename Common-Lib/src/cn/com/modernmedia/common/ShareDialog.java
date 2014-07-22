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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.com.modernmedia.R;
import cn.com.modernmedia.adapter.ShareAdapter;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.model.ShareDialogItem;
import cn.com.modernmedia.util.ModernMediaTools;

/**
 * 分享
 * 
 * @author ZhuQiao
 * 
 */
public abstract class ShareDialog {
	public static final int WEIXIN_FRIEND = 1;
	public static final int WEIXIN_FRIENDS = 2;
	public static final int SINA_WEIBO = 3;
	public static final int MORE_ID = 4;

	private Context mContext;
	private AlertDialog mAlertDialog;
	private ListView mListView;
	private List<ShareDialogItem> shareDialogItemList;
	List<String> packList = new ArrayList<String>();
	private ShareAdapter adapter;
	private ShareTool tool;
	private BaseShare baseShare;
	private boolean hasWeixin;

	// --非iweekly应用参数
	public class Args {
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
	public class WeeklyArgs {
		String title;
		String desc;
		String url;
		String webUrl;
		int bottomResId;// 文章页截屏底部资源

		public WeeklyArgs(String title, String desc, String url,
				int bottomResId, String webUrl) {
			this.title = title;
			this.desc = desc;
			this.url = url;
			this.bottomResId = bottomResId;
			this.webUrl = webUrl;
		}

	}

	public ShareDialog(Context context) {
		mContext = context;
		hasWeixin = isAvilible(mContext, BaseShare.WEIXIN);
		mListView = new ListView(mContext);
		mListView.setCacheColorHint(Color.WHITE);
		mListView.setDivider(new ColorDrawable(Color.LTGRAY));
		mListView.setDividerHeight(1);
		mListView.setFadingEdgeLength(mContext.getResources()
				.getDimensionPixelOffset(R.dimen.share_list_fade_length));
		mListView.setBackgroundColor(Color.WHITE);
		shareDialogItemList = new ArrayList<ShareDialogItem>();
		adapter = new ShareAdapter(mContext);
		mListView.setAdapter(adapter);
		tool = new ShareTool(context);
		initDefaultApps(true);
	}

	private boolean isAvilible(Context context, String packageName) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("image/*");
		List<ResolveInfo> resInfo = mContext.getPackageManager()
				.queryIntentActivities(shareIntent, 0);
		if (!resInfo.isEmpty()) {
			for (ResolveInfo info : resInfo) {
				String pack = info.activityInfo.packageName;
				if (pack.equals(BaseShare.WEIXIN))
					return true;
			}
		}
		return false;
	}

	/**
	 * 初始化默认分享的app
	 */
	private void initDefaultApps(boolean showMore) {
		ShareDialogItem item = new ShareDialogItem();
		// TODO 微信好友
		item.setId(WEIXIN_FRIEND);
		item.setIcon(R.drawable.wechat);
		item.setName(mContext.getString(R.string.weixin_friend));
		shareDialogItemList.add(item);
		// TODO 微信朋友圈
		item = new ShareDialogItem();
		item.setId(WEIXIN_FRIENDS);
		item.setIcon(R.drawable.moments);
		item.setName(mContext.getString(R.string.weixin_friends));
		shareDialogItemList.add(item);
		// TODO 新浪微博
		item = new ShareDialogItem();
		item.setId(SINA_WEIBO);
		item.setIcon(R.drawable.weibo);
		item.setName(mContext.getString(R.string.sina_weibo));
		shareDialogItemList.add(item);
		// TODO 更多
		if (!showMore)
			return;
		item = new ShareDialogItem();
		item.setIcon(0);
		item.setName(mContext.getString(R.string.more));
		item.setId(MORE_ID);
		shareDialogItemList.add(item);
	}

	/**
	 * 非iweekly应用开始分享(不带图片)
	 * 
	 * @param issue
	 * @param columnId
	 * @param articleId
	 */
	public void startShareDefault(Issue issue, String columnId, String articleId) {
		baseShare = new NormalShare(mContext, new Args(issue, columnId,
				articleId), this);
		baseShare.prepareShareAfterFetchBitmap("");
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
		baseShare = new NormalShare(mContext, new Args(issue, columnId,
				articleId, url), this);
		baseShare.prepareShareAfterFetchBitmap(url);
	}

	/**
	 * iweekly应用开始分享
	 * 
	 * @param title
	 * @param desc
	 * @param url
	 */
	public void startShareWeekly(String title, String desc, String url,
			int bottomResId, String webUrl) {
		baseShare = new WeeklyShare(mContext, new WeeklyArgs(title, desc, url,
				bottomResId, webUrl), this);
		baseShare.prepareShareAfterFetchBitmap(url);
	}

	private void queryTargetIntents(Bitmap bitmap) {
		shareDialogItemList.clear();
		initDefaultApps(false);
		packList.clear();
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType(bitmap == null ? "text/*" : "image/*");
		List<ResolveInfo> resInfo = mContext.getPackageManager()
				.queryIntentActivities(shareIntent, 0);
		if (!resInfo.isEmpty()) {
			for (ResolveInfo info : resInfo) {
				addIntent(info, Intent.ACTION_SEND);
			}
		}
		if (bitmap != null) {
			// 记录能浏览、保存图片的app
			Intent galleryIntent = new Intent(Intent.ACTION_VIEW);
			galleryIntent.setType("image/*");
			resInfo = mContext.getPackageManager().queryIntentActivities(
					galleryIntent, 0);
			if (!resInfo.isEmpty()) {
				ResolveInfo info = resInfo.get(0);
				addIntent(info, Intent.ACTION_VIEW);
			}
		}

		adapter.clear();
		adapter.setData(shareDialogItemList);
	}

	private void addIntent(ResolveInfo info, String action) {
		String pack = info.activityInfo.packageName;
		if (!packList.contains(pack)) {
			Intent targeted = new Intent(action);
			targeted.setPackage(pack);
			if (pack.contains(BaseShare.SINA)
					|| pack.contains(BaseShare.WEIXIN)) {
			} else {
				ShareDialogItem item = new ShareDialogItem();
				item.setIntent(targeted);
				shareDialogItemList.add(item);
			}
			packList.add(pack);
		}
	}

	/**
	 * 显示分享dialog
	 * 
	 * @param isWeekly
	 */
	protected void showShareDialog(final Bitmap bitmap) {
		adapter.clear();
		adapter.setData(shareDialogItemList);

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
		dialogBuilder.setTitle(R.string.share_select);
		dialogBuilder.setView(mListView);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onListItemClick(position, bitmap);
			}
		});
		mAlertDialog = dialogBuilder.create();
		try {
			mAlertDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

		ModernMediaTools.showLoading(mContext, false);
	}

	/**
	 * 点击listview的item
	 * 
	 * @param position
	 */
	private void onListItemClick(int position, Bitmap bitmap) {
		if (adapter.getCount() <= position)
			return;
		ShareDialogItem item = adapter.getItem(position);
		if (item.getId() == WEIXIN_FRIEND) {
			if (hasWeixin)
				baseShare.shareByFriend();
			else
				ModernMediaTools.showToast(mContext, R.string.no_weixin);
		} else if (item.getId() == WEIXIN_FRIENDS) {
			if (hasWeixin)
				baseShare.shareByFriends();
			else
				ModernMediaTools.showToast(mContext, R.string.no_weixin);
		} else if (item.getId() == SINA_WEIBO) {
			baseShare.shareByWeibo();
		} else if (item.getId() == MORE_ID) {
			queryTargetIntents(bitmap);
			return;
		} else if (item.getIntent() != null) {
			Intent intent = item.getIntent();
			if (Intent.ACTION_SEND.equals(intent.getAction())) {
				baseShare.shareToOthers(intent);
			} else {
				if (bitmap != null) {
					tool.saveToGallery(bitmap);
					logAndroidSaveToImageAlbum();
					Toast.makeText(mContext, R.string.save_picture_success,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, R.string.save_picture_fail,
							Toast.LENGTH_SHORT).show();
				}
			}
		}
		dismissDialog();
	}

	private void dismissDialog() {
		if (mAlertDialog != null && mAlertDialog.isShowing()) {
			mAlertDialog.dismiss();
		}
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
