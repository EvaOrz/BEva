package cn.com.modernmediausermodel.widget;

import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.modernmedia.widget.CheckScrollListview;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.adapter.CardDetailListAdapter;
import cn.com.modernmediausermodel.model.MultiComment.Comment;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 详情的单页类
 * 
 * @author jiancong
 * 
 */
public class CardDetailItemView extends RelativeLayout {

	private Context mContext;
	private CheckScrollListview listView;
	private CardDetailListAdapter adapter;
	private TextView cardContentText, nickNameText, timeText;
	private ImageView avatar;

	public CardDetailItemView(Context context) {
		this(context, null);
	}

	public CardDetailItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		this.addView(LayoutInflater.from(mContext).inflate(
				R.layout.card_detail_page_item, null));
		listView = (CheckScrollListview) findViewById(R.id.card_detail_page_list_view);

		// head view
		View headView = LayoutInflater.from(mContext).inflate(
				R.layout.card_detail_list_head, null);
		cardContentText = (TextView) headView
				.findViewById(R.id.card_detail_page_card_content);
		nickNameText = (TextView) headView
				.findViewById(R.id.card_detail_item__head_user_name);
		timeText = (TextView) headView
				.findViewById(R.id.card_detail_item_head_time);
		avatar = (ImageView) headView
				.findViewById(R.id.card_detail_item__head_avatar);
		listView.addHeaderView(headView);

		adapter = new CardDetailListAdapter(mContext);
		listView.setAdapter(adapter);
		UserTools.setAvatar(mContext, "", avatar);
	}

	public CardDetailListAdapter getAdapter() {
		return adapter;
	}

	/**
	 * 设置用户的信息及卡片内容
	 * 
	 * @param content
	 * @param user
	 * @param time
	 */
	public void setHeadData(String content, final User user, String time) {
		cardContentText.setText(content);
		if (user != null) {
			nickNameText.setText(user.getNickName());
			UserTools.setAvatar(mContext, user.getAvatar(), avatar);
			avatar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					UserPageTransfer.gotoUserCardInfoActivity(mContext, user,
							false);
				}
			});
		}
		timeText.setText(String.format(
				mContext.getString(R.string.create_time),
				UserTools.dateFormat(time)));
	}

	/**
	 * 设置用户的评论列表
	 * 
	 * @param comment
	 * @param userInfoMap
	 */
	public void setData(Comment comment, Map<String, User> userInfoMap) {
		adapter.clear();
		adapter.setData(comment, userInfoMap);
		adapter.notifyDataSetChanged();
	}

}
