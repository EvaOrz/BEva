package cn.com.modernmediausermodel;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 意见反馈
 * 
 * @author ZhuQiao
 * 
 */
public class FeedBackActivity extends SlateBaseActivity {
	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_back);
		mWebView = (WebView) findViewById(R.id.feedback_web_view);
		mWebView.setBackgroundColor(Color.TRANSPARENT);
		String feedbackUrl = "http://beta.iweek.ly/minisite";
		if (Tools.checkNetWork(this)) {
			mWebView.loadUrl(feedbackUrl);
		} else {
			mWebView.loadUrl("about:blank;");
		}
	}

	@Override
	public String getActivityName() {
		return FeedBackActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

}
