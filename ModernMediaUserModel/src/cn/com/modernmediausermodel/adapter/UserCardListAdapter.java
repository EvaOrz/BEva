package cn.com.modernmediausermodel.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.modernmedia.adapter.CheckScrollAdapter;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.model.Card.CardItem;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.CardListPop;
import cn.com.modernmediausermodel.util.UserDataHelper;
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
	private LayoutInflater inflater;
	private Card card;
	private boolean isForUser = true; // 是否是登录用户资料,默认是
	public boolean firstScrollEnd = true;
	private CardListPop pop;

	public UserCardListAdapter(Context context) {
		super(context);
		this.mContext = context;
		this.inflater = LayoutInflater.from(mContext);
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
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.user_card_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.divider = (ImageView) convertView
					.findViewById(R.id.divider);
			viewHolder.content = (TextView) convertView
					.findViewById(R.id.item_card_content);
			viewHolder.slidingBtn = (ImageView) convertView
					.findViewById(R.id.user_card_sliding_button);
			viewHolder.userInfoLayout = (RelativeLayout) convertView
					.findViewById(R.id.user_info_layout);
			// 显示某一用户的卡片列表时，不需要显示头像及名称；广场显示则相反
			if (!isForUser) {
				viewHolder.userInfoLayout.setVisibility(View.VISIBLE);
				viewHolder.avatar = (ImageView) convertView
						.findViewById(R.id.item_avatar);
				viewHolder.userName = (TextView) convertView
						.findViewById(R.id.item_user_name);
				viewHolder.time = (TextView) convertView
						.findViewById(R.id.time);
			} else {
				viewHolder.userInfoLayout.setVisibility(View.GONE);
			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			if (!isForUser)
				UserTools.transforCircleBitmap(mContext,
						R.drawable.avatar_placeholder, viewHolder.avatar);
		}
		viewHolder.divider.setVisibility(position == 0 ? View.GONE
				: View.VISIBLE);
		if (cardItem != null) {
			if (!isForUser) {
				doUserInfoLayout(viewHolder, cardItem);
			}
			// 笔记
			viewHolder.content.setText(cardItem.getContents());

			viewHolder.slidingBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					pop.showPop(v, cardItem);
				}
			});

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 跳转到详情页
					if (checkLogin())
						UserPageTransfer.gotoCardDetailActivity(mContext, card,
								position);
				}
			});
		}
		return convertView;
	}

	private void doUserInfoLayout(ViewHolder viewHolder, CardItem cardItem) {
		String uid = cardItem.getUid();
		if (cardItem.getType() == 2) { // 收藏关系
			uid = cardItem.getFuid();
		}
		final User user = card.getUserInfoMap().get(uid);
		if (user != null) {
			// 昵称
			viewHolder.userName.setText(user.getNickName());
			// 头像
			if (!isScroll) {
				UserTools.setAvatar(mContext, user.getAvatar(),
						viewHolder.avatar);
			}
			viewHolder.avatar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 跳转到用户资料页
					if (checkLogin())
						UserPageTransfer.gotoUserCardInfoActivity(mContext,
								user, false);
				}
			});
			// time
			viewHolder.time.setText(UserTools.dateFormat(cardItem.getTime()));
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
	 * @return
	 */
	private boolean checkLogin() {
		User user = UserDataHelper.getUserLoginInfo(mContext);
		if (user == null) {
			UserPageTransfer.gotoLoginActivityRequest(mContext, TO_LOGIN);
			return false;
		}
		return true;
	}

	class ViewHolder {
		TextView content;
		ImageView slidingBtn, divider;
		RelativeLayout userInfoLayout;
		ImageView avatar;
		TextView userName, time;
		boolean isShowAll = true; // 是否显示收藏和评论按钮
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		pop.dismissPop();
	}

}
