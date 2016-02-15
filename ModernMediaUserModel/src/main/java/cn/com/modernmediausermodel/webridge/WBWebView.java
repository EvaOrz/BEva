package cn.com.modernmediausermodel.webridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WBWebView extends WebView {
	private static final String JS_NAME = "androidWebridge";

	private Context mContext;
	private WBUri mWbUri;
	private WBWebridge mWebridge;

	public WBWebView(Context context) {
		this(context, null);
		mContext = context;
		init();
	}

	public WBWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void init() {
		mWbUri = new WBUri(mContext, new WebUriImplement(mContext));

		WebSettings webSettings = getSettings();

		webSettings.setJavaScriptEnabled(true);
		webSettings.setSaveFormData(false);
		/** bug fix **/
		setSaveEnabled(false);
		webSettings.setSavePassword(false);
		webSettings.setSupportZoom(false);

		// JS_NAME是自己定义的，供javascript访问的接口
		mWebridge = new WBWebridge(this, new WBWebridgeImplement(mContext));
		if (mWebridge == null) {
			Log.e("初始化webridge出错", "mWebridge == null");
		}
		addJavascriptInterface(mWebridge, JS_NAME);

		setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				mWbUri.openURI(url);
				return true;
			}

		});

		setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				mContext.startActivity(intent);
			}
		});
	}

	public WBWebridge getWebridge() {
		return mWebridge;
	}

}
