package cn.com.modernmediausermodel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import cn.com.modernmediausermodel.webridge.WBWebView;

public class TestActivity extends Activity {
	private WBWebView wv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_webtonative);
		init();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void init() {

		// URI以及js调用本地方法测试
		wv = (WBWebView) findViewById(R.id.test_webtonative_wv);
		// String url = "file:///android_asset/web/uri-test-landscape.html";
		String url = "http://adv.bbwc.cn/articles/webridge-test/index.html";
		wv.loadUrl(url);

	}

}
