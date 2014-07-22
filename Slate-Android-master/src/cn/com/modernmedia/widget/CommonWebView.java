package cn.com.modernmedia.widget;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.VideoPlayerActivity;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.WebProcessListener;
import cn.com.modernmedia.model.ArticleList.ArticleDetail;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.util.UriParse;

@SuppressLint("SetJavaScriptEnabled")
public abstract class CommonWebView extends WebView {
	private CommonWebView me;
	private final static int TIME_OUT = 20 * 1000;// ���ó�ʱʱ��
	private final static int TIME_OUT_MSG = 100;
	private WebProcessListener listener;
	private Timer timer;
	private boolean loadOk = true;
	private ArticleDetail detail;
	private Context mContext;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TIME_OUT_MSG:
				if (listener != null)
					listener.showStyle(2);
				break;
			default:
				break;
			}
		}

	};

	public CommonWebView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public CommonWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		me = this;
		this.setBackgroundColor(0);// ����webview����İ�ɫ����Ϊ͸��������ʾ���������view
		// this.setBackgroundResource(R.drawable.web_bg);
		WebSettings s = getSettings();
		s.setSupportZoom(false);
		s.setBuiltInZoomControls(false);
		s.setUseWideViewPort(true);
		s.setJavaScriptEnabled(true);
		s.setDomStorageEnabled(true);
		s.setRenderPriority(RenderPriority.HIGH);// �����Ⱦ���ȼ�
		s.setBlockNetworkImage(true);// ͼƬ���ط��������������Ⱦ(onPageFinished)
		s.setAllowFileAccess(true);
		s.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		s.setJavaScriptCanOpenWindowsAutomatically(true);
		this.setWebViewClient(new WebViewClient() {

			/**
			 * ��ҳ����ؿ�ʼʱ����
			 */
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// PrintHelper.print("onPageStarted");
				super.onPageStarted(view, url, favicon);
			}

			/**
			 * ��ҳ����ؽ���ʱ����
			 */
			@Override
			public void onPageFinished(WebView view, String url) {
				// PrintHelper.print("onPageFinished");
				if (loadOk) {
					cancelTimer();
					getSettings().setBlockNetworkImage(false);
					if (mContext instanceof CommonArticleActivity) {
						((CommonArticleActivity) mContext).addLoadOkUrl(url);
						if (((CommonArticleActivity) mContext).getCurrentUrl()
								.equals(url)) {
							ReadDb.getInstance(mContext).addReadArticle(
									detail.getArticleId());
						}
					}
					if (listener != null)
						listener.showStyle(0);
				}
			}

			/**
			 * �ڵ��������������ǲŻ���ã� ��д�˷�������true���������ҳ��������ӻ����ڵ�ǰ��webview����ת��������������Ǳ�
			 */
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (!TextUtils.isEmpty(url) && url.startsWith("http://")) {
					Uri uri = Uri.parse(url);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					if (mContext instanceof Activity) {
						((Activity) mContext).startActivity(intent);
						return true;
					}
				}
				List<String> list = UriParse.parser(url);
				if (list.size() > 1) {
					if (list.get(0).equalsIgnoreCase("video")) {
						String path = list.get(1).replace(".m3u8", ".mp4");//
						// "http://v.cdn.imlady.bbwc.cn/turningpoints/2012/12/20121218032816456/20121218032816456.mp4";
						if (path.toLowerCase().endsWith(".mp4")) {
							Intent intent = new Intent(view.getContext(),
									VideoPlayerActivity.class);
							intent.putExtra("vpath", path);
							view.getContext().startActivity(intent);
						}
					} else if (list.get(0).equalsIgnoreCase("article")) {
						// ��ת��������ҳ
						if (list.size() > 3) {
							gotoArticle(ParseUtil.stoi(list.get(3), -1));
						}
					}
				}
				return true;
			}

			/**
			 * �ڼ���ҳ����Դʱ����ã�ÿһ����Դ������ͼƬ���ļ��ض������һ��
			 */
			@Override
			public void onLoadResource(WebView view, String url) {
				super.onLoadResource(view, url);
			}

			/**
			 * ����https����
			 */
			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				loadOk = false;
				cancelTimer();
				if (listener != null)
					listener.showStyle(2);
				handler.cancel();// ��֧��ssl
				// PrintHelper.print("onReceivedSslError");
				super.onReceivedSslError(view, handler, error);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				loadOk = false;
				if (listener != null)
					listener.showStyle(2);
				cancelTimer();
				// PrintHelper.print("onReceivedError");
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

		});
	}

	public void startLoad(ArticleDetail detail) {
		if (detail == null)
			return;
		this.detail = detail;
		String url = detail.getLink();
		if (TextUtils.isEmpty(url) || !url.startsWith("http")) {
			loadData("û��url���߲���http��ͷ" + url == null ? "" : ":" + url,
					"text/html", "utf-8");
			return;
		}
		loadUrl(url);
	}

	@Override
	public void loadUrl(String url) {
		super.loadUrl(url);
		if (listener != null)
			listener.showStyle(1);
		// startTimer();
	}

	@Override
	public void reload() {
		super.reload();
		if (listener != null)
			listener.showStyle(1);
		// startTimer();
	}

	@Override
	public void goBack() {
		super.goBack();
		// listener.showProcess(false);
		// listener.showError(false);
	}

	@SuppressWarnings("unused")
	private void startTimer() {
		timer = new Timer();
		TimerTask tt = new TimerTask() {
			@Override
			public void run() {
				/*
				 * ��ʱ��,�����ж�ҳ����ؽ���,��ʱ���ҽ���С��100,��ִ�г�ʱ��Ķ���
				 */
				if (me.getProgress() < 100 || !loadOk) {
					handler.sendEmptyMessage(TIME_OUT_MSG);
					cancelTimer();
				}
			}
		};
		timer.schedule(tt, TIME_OUT);
	}

	private void cancelTimer() {
		if (timer == null)
			return;
		timer.cancel();
		timer.purge();
	}

	public void setListener(WebProcessListener listener) {
		this.listener = listener;
	}

	/**
	 * keyword��ͷ���ȿɵ����ת����������ҳ
	 * 
	 * @param articleId
	 */
	public abstract void gotoArticle(int articleId);

}
