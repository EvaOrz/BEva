package cn.com.modernmediausermodel.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.com.modernmedia.widget.CheckScrollListview;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.adapter.CheckScrollAdapter;
import cn.com.modernmediausermodel.listener.CardViewListener;
import cn.com.modernmediausermodel.model.Message;
import cn.com.modernmediausermodel.model.Message.MessageItem;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;

/**
 * 通知中心
 * 
 * @author user
 * 
 */
public class MessageView implements CardViewListener {
	public static final String KEY_MESSAGE = "message";

	private Context mContext;
	private View view, titleBar;
	private CheckScrollListview listView;
	private TextView NoMsg;
	private DataBaseApdater adapter;
	private Message mMessage = new Message();
	private boolean finish = true;// 是否离开页面的时候finish

	public MessageView(Context context, Message message) {
		mContext = context;
		mMessage = message;
		init();
	}

	private void init() {
		// 将lastid 存起来
		UserDataHelper.saveMessageLastId(mContext, mMessage.getLastId());
		view = LayoutInflater.from(mContext).inflate(R.layout.activity_message,
				null);
		titleBar = view.findViewById(R.id.message_bar_layout);
		listView = (CheckScrollListview) view
				.findViewById(R.id.message_list_view);
		NoMsg = (TextView) view.findViewById(R.id.message_no_msg);
		adapter = new DataBaseApdater(mContext);
		listView.setAdapter(adapter);

		if (ParseUtil.listNotNull(mMessage.getMessageList())) {
			adapter.setData(mMessage.getMessageList());
		} else {
			listView.setVisibility(View.GONE);
			NoMsg.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 是否显示titleBar
	 * 
	 * @param show
	 */
	@Override
	public void showTitleBar(boolean show) {
		titleBar.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	@Override
	public View fetchView() {
		return view;
	}

	public View getBackBtn() {
		return view.findViewById(R.id.message_button_back);
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	private class DataBaseApdater extends CheckScrollAdapter<MessageItem> {

		public DataBaseApdater(Context context) {
			super(context);
		}

		public void setData(List<MessageItem> list) {
			synchronized (list) {
				for (MessageItem item : list) {
					add(item);
				}
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.favorites_item, null);
				viewHolder.content = (TextView) convertView
						.findViewById(R.id.favorites_item_name);
				viewHolder.content.setSingleLine(false);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			final MessageItem messageItem = getItem(position);
			if (messageItem != null) {
				String text = "";
				String content = messageItem.getContent();
				switch (messageItem.getType()) {
				case 0: // 测试用
					break;
				case 1: // 添加评论
					text = String.format(
							mContext.getString(R.string.message_new_comment),
							content, messageItem.getCommentNum());
					break;
				case 2: // 新增粉丝
					text = String.format(
							mContext.getString(R.string.message_new_fans),
							messageItem.getFansNum());
					break;
				case 3: // 自己被推荐
					text = mContext
							.getString(R.string.message_user_is_recommended);
					break;
				case 4: // 自己的卡片被推荐
					text = String.format(mContext
							.getString(R.string.message_card_is_recommended),
							content);
					break;
				default:
					break;
				}
				viewHolder.content.setText(text);

				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						doItemClick(messageItem);
					}
				});

			}
			return convertView;
		}

		protected void doItemClick(MessageItem messageItem) {
			switch (messageItem.getType()) {
			case 2: // 前往粉丝页面
				UserPageTransfer.gotoUserListActivity(mContext,
						SlateDataHelper.getUserLoginInfo(mContext),
						RecommendUserView.PAGE_FANS, finish);
				break;
			case 3: // 前往用户卡片页
				User user = SlateDataHelper.getUserLoginInfo(mContext);
				UserPageTransfer.gotoUserCardInfoActivity(mContext, user,
						finish);
			case 1:
			case 4: // 前往用户卡片详情页
				UserPageTransfer.gotoCardDetailActivity(mContext,
						messageItem.getCardid() + "", finish);
				break;
			default:
				break;
			}
		}

	}

	class ViewHolder {
		TextView content;
	}

}
