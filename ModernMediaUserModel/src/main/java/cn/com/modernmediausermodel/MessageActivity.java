package cn.com.modernmediausermodel;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.umeng.analytics.MobclickAgent;

import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediausermodel.model.Message;
import cn.com.modernmediausermodel.widget.MessageView;

/**
 * 通知中心页
 * 
 * @author user
 * 
 */
public class MessageActivity extends SlateBaseActivity {
	private Message mMessage;
	private MessageView messageView;

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
					MessageView.KEY_MESSAGE);
		}
	}

	private void init() {
		if (mMessage == null)
			return;
		messageView = new MessageView(this, mMessage);
		setContentView(messageView.fetchView());
		messageView.getBackBtn().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	@Override
	public String getActivityName() {
		return MessageActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
