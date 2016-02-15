package cn.com.modernmediausermodel;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediausermodel.widget.MyCoinUseNoticeView;

public class MyCoinUseNoticeActivity extends SlateBaseActivity {
	private MyCoinUseNoticeView noticeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		noticeView = new MyCoinUseNoticeView(this);
		setContentView(noticeView.fetchView());
		noticeView.getBackBtn().setOnClickListener(new OnClickListener() {

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
}
