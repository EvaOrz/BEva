package cn.com.modernmediausermodel.adapter;

import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.adapter.CheckScrollAdapter;
import cn.com.modernmediausermodel.CardDetailActivity;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.model.MultiComment.Comment;
import cn.com.modernmediausermodel.model.MultiComment.CommentItem;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserConstData;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;

public class CardDetailListAdapter extends CheckScrollAdapter<CommentItem> {
	private Context mContext;
	private LayoutInflater inflater;
	private Map<String, User> mUserInfoMap;
	private boolean firstScrollEnd = true;

	public CardDetailListAdapter(Context context) {
		super(context);
		this.mContext = context;
		this.inflater = LayoutInflater.from(mContext);
	}

	public void setData(Comment comment, Map<String, User> userInfoMap) {
		isScroll = false;
		this.mUserInfoMap = userInfoMap;
		synchronized (comment) {
			for (CommentItem item : comment.getCommentItemList()) {
				add(item);
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final CommentItem commentItem = getItem(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.card_detatil_list_item,
					null);
			viewHolder = new ViewHolder();
			viewHolder.avatar = (ImageView) convertView
					.findViewById(R.id.card_detail_avatar);
			viewHolder.userName = (TextView) convertView
					.findViewById(R.id.card_detail_user_name);
			viewHolder.content = (TextView) convertView
					.findViewById(R.id.card_detail_comment_content);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			UserTools.transforCircleBitmap(mContext,
					R.drawable.avatar_placeholder, viewHolder.avatar);
		}
		if (commentItem != null) {
			final User user = mUserInfoMap.get(commentItem.getUid());
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
						if (mContext instanceof CardDetailActivity) {
							UserPageTransfer.gotoUserCardInfoActivity(mContext,
									user, false);
						}
					}
				});
			}
			// 评论内容
			String content = commentItem.getContent();
			String time = UserTools.dateFormat(commentItem.getTime());
			if (commentItem.getIsdel() == 1) {
				content = mContext.getString(R.string.comment_delete_toast);
			}
			viewHolder.content.setText(content + "(" + time + ")");
		}
		return convertView;
	}

	class ViewHolder {
		TextView content;
		ImageView avatar;
		TextView userName;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// 滑动到底部时，加载更多
		if (firstVisibleItem + visibleItemCount == totalItemCount
				&& totalItemCount >= UserConstData.MAX_CARD_ITEM_COUNT
				&& !isScroll) {
			if (firstScrollEnd) {
				firstScrollEnd = false;
			} else {
				CommentItem commentItem = getItem(totalItemCount - 2);
				int id = commentItem.getId();
				if (mContext instanceof CardDetailActivity) {
					((CardDetailActivity) mContext).getComments(id, true);
				}
			}
		}
	}

}
