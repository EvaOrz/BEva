package cn.com.modernmediausermodel;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.adapter.CheckScrollAdapter;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.widget.CheckScrollListview;
import cn.com.modernmediausermodel.model.Message;
import cn.com.modernmediausermodel.model.Message.MessageItem;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;

/**
 * 通知中心页
 * 
 * @author user
 * 
 */
public class MessageActivity extends BaseActivity {
	public final static String KEY_MESSAGE = "message";
	private CheckScrollListview listView;
	private ImageView back;
	private TextView NoMsg;
	private DataBaseApdater adapter;
	private Message mMessage = new Message();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		initDataFromBundle();
		init();
	}

	private void initDataFromBundle() {
		if (getIntent() != null && getIntent().getExtras() != null) {
			this.mMessage = (Message) getIntent().getExtras().getSerializable(
					KEY_MESSAGE);
			// 将lastid 存起来
			if (mMessage != null) {
				UserDataHelper.saveMessageLastId(this, mMessage.getLastId());
			}
		}
	}

	private void init() {
		listView = (CheckScrollListview) findViewById(R.id.message_list_view);
		back = (ImageView) findViewById(R.id.message_button_back);
		NoMsg = (TextView) findViewById(R.id.message_no_msg);
		adapter = new DataBaseApdater(this);
		listView.setAdapter(adapter);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		if (ParseUtil.listNotNull(mMessage.getMessageList())) {
			adapter.setData(mMessage.getMessageList());
		} else {
			listView.setVisibility(View.GONE);
			NoMsg.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void reLoadData() {
	}

	@Override
	public String getActivityName() {
		return MessageActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	class DataBaseApdater extends CheckScrollAdapter<MessageItem> {
		private Context mContext;

		public DataBaseApdater(Context context) {
			super(context);
			this.mContext = context;
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
						UserDataHelper.getUserLoginInfo(mContext),
						RecommendUserActivity.PAGE_FANS, true);
				break;
			case 3: // 前往用户卡片页
				User user = UserDataHelper.getUserLoginInfo(mContext);
				UserPageTransfer.gotoUserCardInfoActivity(mContext, user, true);
			case 1:
			case 4: // 前往用户卡片详情页
				UserPageTransfer.gotoCardDetailActivity(mContext,
						messageItem.getCardid() + "", true);
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
