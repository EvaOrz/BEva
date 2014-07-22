package cn.com.modernmediausermodel.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.adapter.CheckScrollAdapter;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.model.User.Error;
import cn.com.modernmediausermodel.model.Users;
import cn.com.modernmediausermodel.model.Users.UserCardInfo;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;
import cn.com.modernmediausermodel.widget.RecommendUserView;

public class RecommendUsersAdapter extends CheckScrollAdapter<User> {
	private Context mContext;
	private LayoutInflater inflater;
	private Users users;
	private int pageType;
	private String currUid = "";
	private UserOperateController controller;
	private User mUser;// 当前用户
	private Users mUsers;
	private boolean hasModify = false;

	public RecommendUsersAdapter(Context context, int pageType, User user) {
		super(context);
		this.mContext = context;
		this.pageType = pageType;
		this.inflater = LayoutInflater.from(mContext);
		currUid = UserTools.getUid(mContext);
		controller = UserOperateController.getInstance(mContext);
		mUser = user;
	}

	public void setmUsers(Users mUsers) {
		this.mUsers = mUsers;
	}

	public boolean isHasModify() {
		return hasModify;
	}

	public void setData(Users users) {
		isScroll = false;
		synchronized (users) {
			this.users = users;
			for (User user : users.getUserList()) {
				add(user);
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final User user = getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.recommenduser_list_item,
					null);
			holder = new ViewHolder();
			holder.avatar = (ImageView) convertView
					.findViewById(R.id.recommend_avatar);
			holder.userName = (TextView) convertView
					.findViewById(R.id.recommend_user_name);
			holder.userInfo = (TextView) convertView
					.findViewById(R.id.recommend_user_info);
			holder.checkBox = (Button) convertView
					.findViewById(R.id.recommend_checkbox);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			UserTools.setAvatar(mContext, "", holder.avatar);
		}
		if (user == null)
			return convertView;
		final ViewHolder viewHolder = holder;
		// 昵称
		viewHolder.userName.setText(user.getNickName());
		// 头像
		if (!isScroll && !TextUtils.isEmpty(user.getAvatar())) {
			UserTools.setAvatar(mContext, user.getAvatar(), holder.avatar);
		}
		// 笔记相关
		final UserCardInfo userCardInfo = users.getUserCardInfoMap().get(
				user.getUid());
		String des = String.format(mContext.getString(R.string.card_num),
				userCardInfo.getCardNum());
		viewHolder.userInfo.setText(des);
		final boolean isMe = user.getUid().equals(currUid);
		viewHolder.checkBox.setVisibility(isMe ? View.INVISIBLE : View.VISIBLE);
		if (userCardInfo.getIsFollowed() == 0) { // 未关注
			viewHolder.checkBox.setText(R.string.follow);
			viewHolder.checkBox.setTextColor(mContext.getResources().getColor(
					R.color.follow_all));
		} else {
			viewHolder.checkBox.setText(R.string.followed);
			viewHolder.checkBox.setTextColor(mContext.getResources().getColor(
					R.color.listitem_des));
		}

		viewHolder.checkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isMe)
					return;
				if (userCardInfo.getIsFollowed() == 1) { // 已关注，进行取消关注操作
					deleteFollow(user);
				} else {
					addFollow(user);
				}
			}
		});
		// 非推荐用户页面时，点击ITEM，进入用户资料页面
		if (pageType != RecommendUserView.PAGE_RECOMMEND_FRIEND) {
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					UserPageTransfer.gotoUserCardInfoActivity(mContext, user,
							false);
				}
			});
		}
		return convertView;
	}

	class ViewHolder {
		ImageView avatar;
		TextView userName;
		TextView userInfo;
		Button checkBox;
	}

	/**
	 * 关注用户（单个用户）
	 * 
	 * @param user
	 */
	private void addFollow(final User user) {
		if (user == null)
			return;
		List<User> users = new ArrayList<User>();
		users.add(user);
		ModernMediaTools.showLoading(mContext, true);
		controller.addFollow(UserTools.getUid(mContext), users, false,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						afterFollow(entry, user, 2);
					}
				});
	}

	/**
	 * 刷新adapter
	 * 
	 * @param user
	 *            操作的user
	 * @param isFollow
	 *            1.全部关注，2关注，3取消关注
	 */
	private void afterFollow(Entry entry, User user, int followType) {
		ModernMediaTools.showLoading(mContext, false);
		if (entry instanceof Error && ((Error) entry).getNo() == 0) {
			if (followType == 1) {
				UserPageTransfer.gotoSquareActivity(mContext, true);
				return;
			}
			if (mUser != null
					&& mUsers.getUserCardInfoMap().containsKey(user.getUid())) {
				hasModify = true;
				int follow = followType == 2 ? 1 : 0;
				mUsers.getUserCardInfoMap().get(user.getUid())
						.setIsFollowed(follow);
				notifyDataSetChanged();
			}
		}
	}

	/**
	 * 取消关注用户
	 */
	private void deleteFollow(final User user) {
		if (user == null)
			return;
		List<User> users = new ArrayList<User>();
		users.add(user);
		ModernMediaTools.showLoading(mContext, true);
		controller.deleteFollow(UserTools.getUid(mContext), users, false,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						afterFollow(entry, user, 3);
					}
				});
	}

}
