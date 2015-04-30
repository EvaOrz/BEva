package cn.com.modernmediausermodel.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmediaslate.adapter.ViewHolder;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.model.Card.CardItem;
import cn.com.modernmediausermodel.util.CardListPop;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 卡片列表adapter(广场、首页、用户卡片信息用)
 * 
 * @author jiancong
 * 
 */
public class UserCardListAdapter extends CheckScrollAdapter<CardItem> {
	public static final int TO_LOGIN = 100;// 从登录回来刷新页面

	private Context mContext;
	private Card card;
	private boolean isForUser = true; // 是否是登录用户资料,默认是
	private CardListPop pop;

	public UserCardListAdapter(Context context) {
		super(context);
		this.mContext = context;
		pop = new CardListPop(mContext, this);
	}

	public void setData(List<CardItem> cardList) {
		isScroll = false;
		synchronized (cardList) {
			for (CardItem item : cardList) {
				add(item);
			}
		}
	}

	public void setCard(Card card) {
		this.card = card;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final CardItem cardItem = getItem(position);
		ViewHolder viewHolder = ViewHolder.get(mContext, convertView,
				R.layout.user_card_list_item);
		ImageView divider = viewHolder.getView(R.id.divider);
		TextView content = viewHolder.getView(R.id.item_card_content);
		ImageView slidingBtn = viewHolder
				.getView(R.id.user_card_sliding_button);
		View userInfoLayout = viewHolder.getView(R.id.user_info_layout);
		ImageView avatar = viewHolder.getView(R.id.item_avatar);
		TextView userName = viewHolder.getView(R.id.item_user_name);
		TextView time = viewHolder.getView(R.id.time);
		// 显示某一用户的卡片列表时，不需要显示头像及名称；广场显示则相反
		if (!isForUser) {
			userInfoLayout.setVisibility(View.VISIBLE);
			UserTools.transforCircleBitmap(mContext,
					R.drawable.avatar_placeholder, avatar);
		} else {
			userInfoLayout.setVisibility(View.GONE);
		}

		divider.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
		if (cardItem != null) {
			if (!isForUser) {
				doUserInfoLayout(userName, avatar, time, cardItem);
			}
			// 笔记
			if (!TextUtils.isEmpty(cardItem.getContents())) {
				content.setText(cardItem.getContents());
			}
			slidingBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					pop.showPop(v, cardItem);
				}
			});
			viewHolder.getConvertView().setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 跳转到详情页
							if (checkLogin())
								UserPageTransfer.gotoCardDetailActivity(
										mContext, card, position);
						}
					});
		}
		return viewHolder.getConvertView();
	}

	private void doUserInfoLayout(TextView userName, ImageView avatar,
			TextView time, CardItem cardItem) {
		String uid = cardItem.getUid();
		if (cardItem.getType() == 2) { // 收藏关系
			uid = cardItem.getFuid();
		}
		final User user = card.getUserInfoMap().get(uid);
		if (user != null) {
			// 昵称
			userName.setText(user.getNickName());
			// 头像
			if (!isScroll) {
				UserTools.setAvatar(mContext, user.getAvatar(), avatar);
			}
			avatar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 跳转到用户资料页
					if (checkLogin())
						UserPageTransfer.gotoUserCardInfoActivity(mContext,
								user, false);
				}
			});
			// time
			time.setText(UserTools.dateFormat(cardItem.getTime()));
		}
	}

	/**
	 * 设置用于标识是否是显示用户资料页面卡片列表的adapter（true:用户资料页面；false:广场）
	 * 
	 * @param isForUser
	 */
	public void setIsForUser(boolean isForUser) {
		this.isForUser = isForUser;
	}

	/**
	 * 广场需要判断是否登录;可能刚进页面的时候是未登录状态，所以时时获取一下
	 * 
	 */
	private boolean checkLogin() {
		User user = SlateDataHelper.getUserLoginInfo(mContext);
		if (user == null) {
			UserPageTransfer.gotoLoginActivityRequest(mContext, TO_LOGIN);
			return false;
		}
		return true;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		pop.dismissPop();
	}

}
