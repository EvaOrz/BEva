package cn.com.modernmediausermodel.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmediaslate.adapter.ViewHolder;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.UserCardInfoList;
import cn.com.modernmediausermodel.model.UserCardInfoList.UserCardInfo;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;
import cn.com.modernmediausermodel.widget.RecommendUserView;

/**
 * 用户列表adapter(推荐用户、粉丝、朋友用)
 * 
 * @author jiancong
 * 
 */
public class RecommendUsersAdapter extends CheckScrollAdapter<UserCardInfo> {
	private Context mContext;
	private int pageType;
	private String currUid = "";
	private UserOperateController controller;
	private User mUser;// 当前用户
	private boolean hasModify = false;

	public RecommendUsersAdapter(Context context, int pageType, User user) {
		super(context);
		this.mContext = context;
		this.pageType = pageType;
		currUid = Tools.getUid(mContext);
		controller = UserOperateController.getInstance(mContext);
		mUser = user;
	}

	public boolean isHasModify() {
		return hasModify;
	}

	public void setData(List<UserCardInfo> list) {
		isScroll = false;
		synchronized (list) {
			for (UserCardInfo userCardInfo : list) {
				add(userCardInfo);
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final UserCardInfo userCardInfo = getItem(position);
		ViewHolder holder = ViewHolder.get(mContext, convertView,
				R.layout.recommenduser_list_item);
		ImageView avatar = holder.getView(R.id.recommend_avatar);
		TextView nickName = holder.getView(R.id.recommend_user_name);
		TextView userInfo = holder.getView(R.id.recommend_user_info);
		Button checkBox = holder.getView(R.id.recommend_checkbox);
		UserTools.setAvatar(mContext, "", avatar);
		if (userCardInfo == null)
			return convertView;
		// 昵称
		nickName.setText(userCardInfo.getNickName());
		// 头像
		if (!isScroll && !TextUtils.isEmpty(userCardInfo.getAvatar())) {
			UserTools.setAvatar(mContext, userCardInfo.getAvatar(), avatar);
		}
		String des = String.format(mContext.getString(R.string.card_num),
				userCardInfo.getCardNum());
		userInfo.setText(des);
		final boolean isMe = userCardInfo.getUid().equals(currUid);
		checkBox.setVisibility(isMe ? View.INVISIBLE : View.VISIBLE);
		if (userCardInfo.getIsFollowed() == 0) { // 未关注
			checkBox.setText(R.string.follow);
			checkBox.setTextColor(mContext.getResources().getColor(
					R.color.follow_all));
		} else {
			checkBox.setText(R.string.followed);
			checkBox.setTextColor(mContext.getResources().getColor(
					R.color.listitem_des));
		}
		checkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isMe)
					return;
				if (userCardInfo.getIsFollowed() == 1) { // 已关注，进行取消关注操作
					deleteFollow(userCardInfo);
				} else {
					addFollow(userCardInfo);
				}
			}
		});
		// 非推荐用户页面时，点击ITEM，进入用户资料页面
		if (pageType != RecommendUserView.PAGE_RECOMMEND_FRIEND) {
			holder.getConvertView().setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					User user = new User();
					user.setUid(userCardInfo.getUid());
					user.setNickName(userCardInfo.getNickName());
					user.setAvatar(userCardInfo.getAvatar());
					UserPageTransfer.gotoUserCardInfoActivity(mContext, user,
							false);
				}
			});
		}
		return holder.getConvertView();
	}

	/**
	 * 关注用户（单个用户）
	 * 
	 * @param user
	 */
	private void addFollow(final UserCardInfo userCardInfo) {
		if (userCardInfo == null)
			return;
		UserCardInfoList list = new UserCardInfoList();
		list.getList().add(userCardInfo);
		Tools.showLoading(mContext, true);
		controller.addFollow(Tools.getUid(mContext), list.getList(), false,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						afterFollow(entry, userCardInfo, 2);
					}
				});
	}

	/**
	 * 刷新adapter
	 * 
	 * @param user
	 *            操作的user
	 * @param isFollow
	 *            2关注，3取消关注
	 */
	private void afterFollow(Entry entry, UserCardInfo userCardInfo,
			int followType) {
		Tools.showLoading(mContext, false);
		if (entry instanceof ErrorMsg && ((ErrorMsg) entry).getNo() == 0) {
			if (mUser != null) {
				hasModify = true;
				int follow = followType == 2 ? 1 : 0;
				userCardInfo.setIsFollowed(follow);
				notifyDataSetChanged();
			}
		}
	}

	/**
	 * 取消关注用户
	 */
	private void deleteFollow(final UserCardInfo userCardInfo) {
		if (userCardInfo == null)
			return;
		UserCardInfoList list = new UserCardInfoList();
		list.getList().add(userCardInfo);
		Tools.showLoading(mContext, true);
		controller.deleteFollow(Tools.getUid(mContext), list.getList(),
				false, new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						afterFollow(entry, userCardInfo, 3);
					}
				});
	}

}
