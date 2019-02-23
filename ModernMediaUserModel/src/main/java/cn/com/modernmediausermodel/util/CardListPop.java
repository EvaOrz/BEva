package cn.com.modernmediausermodel.util;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;

import cn.com.modernmedia.common.SharePopWindow;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.adapter.UserCardListAdapter;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Card.CardItem;

/**
 * 卡片列表弹出分享、评论pop
 * 
 * @author user
 * 
 */
public class CardListPop {
	private Context mContext;
	private PopupWindow window;
	private int width, padding;
	private BaseAdapter adapter;
	private Button shareBtn;
	private String uid;

	private Button favBtn;
	private String myUid;// 当前用户

	public CardListPop(Context context, BaseAdapter adapter) {
		mContext = context;
		this.adapter = adapter;
		width = 2 * mContext.getResources().getDimensionPixelSize(
				R.dimen.item_comment_num_width);
		width += mContext.getResources().getDimensionPixelSize(
				R.dimen.list_item_fav_comment_margin);
		padding = mContext.getResources().getDimensionPixelSize(R.dimen.dp15);
	}

	/**i
	 * 显示分享、评论pop
	 * 
	 * @param parent
	 * @param item
	 */
	public void showPop(View parent, final CardItem item) {
		if (dismissPop())
			return;
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.card_list_pop, null);
		window = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		window.setFocusable(false);
		window.setOutsideTouchable(true);
		window.setAnimationStyle(R.style.card_list_popup_anim);
		window.update();
		window.setBackgroundDrawable(new BitmapDrawable());
		int[] location = new int[2];
		parent.getLocationOnScreen(location);
		window.showAtLocation(parent, Gravity.NO_GRAVITY, location[0] - width
				- width / 10 + padding, location[1] + padding / 2);

		shareBtn = (Button) view.findViewById(R.id.card_item_share);
		shareBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {//分享
				ArticleItem a = new ArticleItem();
				a.setTitle("【商周笔记】");
				a.setDesc(item.getContents());
				SharePopWindow sharePopWindow = new SharePopWindow(mContext,a);
//				String content = item.getContents();
//				UserTools.shareCard(mContext, content);
			}
		});
//		setFavBtnText(item.getIsFav());
//		favBtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (item.getIsFav() == 0) {
//					addCardFav(item);
//				} else {
//					deleteCardFav(item);
//				}
//				dismissPop();
//			}
//		});
		view.findViewById(R.id.card_item_comment).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!TextUtils.isEmpty(checkLogin())) {
							UserPageTransfer.gotoWriteComment(mContext, -1,
									item.getId(), true);
						}
						dismissPop();
					}
				});
	}

	/**
	 * 广场需要判断是否登录
	 *
	 * @return
	 */
	private String checkLogin() {
		User user = SlateDataHelper.getUserLoginInfo(mContext);
		if (user == null) {
			UserPageTransfer.gotoLoginActivityRequest(mContext,
					UserCardListAdapter.TO_LOGIN);
			return "";
		}
		return Tools.getUid(mContext);
	}

	/**
	 * 添加收藏
	 * 
	 * @param item
	 */
	private void addCardFav(final CardItem item) {
		myUid = checkLogin();
		if (TextUtils.isEmpty(myUid) || myUid.equals(item.getUid()))
			return;
		Tools.showLoading(mContext, true);
		UserOperateController.getInstance(mContext).addCardFav(myUid,
				item.getId(), new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						Tools.showLoading(mContext, false);
						if (entry instanceof ErrorMsg) {
							ErrorMsg error = (ErrorMsg) entry;
							if (error.getNo() == 0) {
								item.setIsFav(1);
								adapter.notifyDataSetChanged();
								Tools.showToast(mContext,
										R.string.collect_success);
							} else {
								Tools.showToast(mContext,
										R.string.collect_failed);
							}
						}
					}
				});
	}

	/**
	 * 取消卡片收藏
	 * 
	 *
	 */
	private void deleteCardFav(final CardItem item) {
		myUid = checkLogin();
		if (TextUtils.isEmpty(myUid) || myUid.equals(item.getUid()))
			return;
		Tools.showLoading(mContext, true);
		UserOperateController.getInstance(mContext).cancelCardFav(myUid,
				item.getId(), new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						Tools.showLoading(mContext, false);
						if (entry instanceof ErrorMsg) {
							ErrorMsg error = (ErrorMsg) entry;
							if (error.getNo() == 0) {
								item.setIsFav(0);
								adapter.notifyDataSetChanged();
								Tools.showToast(mContext,
										R.string.uncollect_success);
							} else {
								Tools.showToast(mContext,
										R.string.uncollect_failed);
							}
						}
					}
				});
	}

	/**
	 * 设置收藏按钮显示的文本内容
	 * 
	 * @param isFav
	 */
	private void setFavBtnText(int isFav) {
		if (isFav == 0) {
			shareBtn.setText(R.string.collect);
		} else if (isFav == 1) {
			shareBtn.setText(R.string.has_collected);
		}
	}

	/**
	 * 关闭window
	 */
	public boolean dismissPop() {
		if (window != null) {
			window.dismiss();
			window = null;
			return true;
		}
		return false;
	}

}
