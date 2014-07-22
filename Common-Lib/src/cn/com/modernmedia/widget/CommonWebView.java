package cn.com.modernmedia.widget;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.WebProcessListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.Atlas;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmedia.util.UseLocalDataUtil;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;

public abstract class CommonWebView extends WebView {
	private CommonWebView me;
	private final static int TIME_OUT = 20 * 1000;// 设置超时时间
	private final static int TIME_OUT_MSG = 100;
	private WebProcessListener listener;
	private Timer timer;
	private boolean loadOk = true;
	private FavoriteItem detail;
	private Context mContext;
	private boolean isChangeStatus = false;
	private boolean isFetchNull = false;
	private UseLocalDataUtil useUtil;
	private GestureDetector gestureDetector;

	final class InJavaScriptLocalObj {
		public void showSource(final String html) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					if (TextUtils.isEmpty(html)) {
						isFetchNull = true;
						me.getSettings()
								.setCacheMode(WebSettings.LOAD_NO_CACHE);
						if (listener != null)
							listener.showStyle(2);
					} else {
						if (isFetchNull) {
							isFetchNull = false;
							me.getSettings().setCacheMode(
									WebSettings.LOAD_CACHE_ELSE_NETWORK);
						}
					}
				}
			});
		}
	}

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
		this(context, null);
	}

	public CommonWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		useUtil = new UseLocalDataUtil(context, this);
		init();
	}

	private void init() {
		me = this;
		gestureDetector = new GestureDetector(new OnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				if (mContext instanceof CommonArticleActivity)
					((CommonArticleActivity) mContext).getHideListener().hide();
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {

			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				return false;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}
		});
		this.setBackgroundColor(0);// 设置webview本身的白色背景为透明，以显示在它下面的view
		WebSettings s = getSettings();
		s.setSupportZoom(false);
		s.setBuiltInZoomControls(false);
		s.setUseWideViewPort(true);
		s.setJavaScriptEnabled(true);
		s.setDomStorageEnabled(true);
		s.setRenderPriority(RenderPriority.HIGH);// 提高渲染优先级
		s.setBlockNetworkImage(true);// 图片加载放在最后来加载渲染(onPageFinished)
		s.setAllowFileAccess(true);
		s.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		s.setJavaScriptCanOpenWindowsAutomatically(true);
		s.setPluginState(PluginState.ON);
		s.setDefaultTextEncodingName("UTF -8");
		this.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);// 去掉白边
		this.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
		this.setWebViewClient(new WebViewClient() {

			/**
			 * 在页面加载开始时调用
			 */
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			/**
			 * 在页面加载结束时调用
			 */
			@Override
			public void onPageFinished(WebView view, String url) {
				if (loadOk) {
					cancelTimer();
					getSettings().setBlockNetworkImage(false);
					if (mContext instanceof CommonArticleActivity) {
						((CommonArticleActivity) mContext).addLoadOkUrl(url);
						if (((CommonArticleActivity) mContext).getCurrentUrl()
								.equals(url)) {
							ReadDb.getInstance(mContext).addReadArticle(
									detail.getId());
							LogHelper.logAndroidShowArticle(mContext,
									detail.getCatid() + "", detail.getId() + "");
						}
					}
					changeFont();
					changeLineHeight();
					if (listener != null)
						listener.showStyle(0);
					view.loadUrl("javascript:window.local_obj.showSource(document.getElementsByTagName('head')[0].innerHTML)");
				}
			}

			/**
			 * 在点击请求的是链接是才会调用， 重写此方法返回true表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
			 */
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				UriParse.clickSlate(mContext, url,
						new Entry[] { new ArticleItem() }, me, false);
				return true;
			}

			/**
			 * 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次
			 */
			@Override
			public void onLoadResource(WebView view, String url) {
				super.onLoadResource(view, url);
			}

			/**
			 * 处理https请求
			 */
			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				loadOk = false;
				cancelTimer();
				if (listener != null)
					listener.showStyle(2);
				handler.cancel();// 不支持ssl
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

			/**
			 * 如果是同一篇文章从缓存拿出，如果以前没有拦截过，那么shouldInterceptRequest只能拦截到它的url,
			 * html里面的东西都拦截不到了；
			 * 反之，当第一次拦截了东西，那么以后shouldInterceptRequest还是会拦截相同的东西
			 * PS:getSettings().setBlockNetworkImage不改回来，是不会拦截图片url的
			 */
			// @Override
			// public WebResourceResponse shouldInterceptRequest(WebView view,
			// String url) {
			// return null;
			// }

		});
		this.setWebChromeClient(new WebChromeClient() {

		});
	}

	public void startLoad(FavoriteItem detail) {
		if (detail == null)
			return;
		this.detail = detail;
		String url = detail.getLink();
		if (TextUtils.isEmpty(url) || !url.startsWith("http")) {
			getArticleById(detail);
			return;
		}
		if (!useUtil.getLocalHtml(url)) {
			loadUrl(url);
		}
	}

	@Override
	public void loadUrl(String url) {
		super.loadUrl(url);
		if (!isChangeStatus) {
			isChangeStatus = false;
			if (listener != null)
				listener.showStyle(1);
		}
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
				 * 超时后,首先判断页面加载进度,超时并且进度小于100,就执行超时后的动作
				 */
				handler.post(new Runnable() {

					@Override
					public void run() {
						if (me.getProgress() < 100 || !loadOk) {
							handler.sendEmptyMessage(TIME_OUT_MSG);
							cancelTimer();
						}
					}
				});
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
	 * keyword，头条等可点击跳转至具体文章页
	 * 
	 * @param articleId
	 */
	public void gotoArticle(int articleId) {
		if (mContext instanceof CommonArticleActivity && articleId != -1) {
			((CommonArticleActivity) mContext).moveToArticle(articleId);
		}
	}

	/**
	 * 跳转至某个广告
	 * 
	 * @param articleId
	 */
	public void gotoAdv(int advId) {
		if (mContext instanceof CommonArticleActivity && advId != -1) {
			((CommonArticleActivity) mContext).moveToAdv(advId);
		}
	}

	/**
	 * 跳转至文章相册
	 * 
	 * @param urlList
	 */
	public abstract void showGallery(List<String> urlList);

	/**
	 * 改变字体
	 */
	public void changeFont() {
		if (!loadOk) {
			isChangeStatus = true;
			return;
		} else if (ConstData.getAppId() != 20) {
			isChangeStatus = true;
			if (DataHelper.getFontSize(mContext) == 1) {
				if (this.getSettings().getTextSize() != TextSize.NORMAL) {
					this.getSettings().setTextSize(TextSize.NORMAL);
				}
			} else {
				if (this.getSettings().getTextSize() != TextSize.LARGER) {
					this.getSettings().setTextSize(TextSize.LARGER);
				}
			}
			return;
		}
		String url = "javascript:setFontSize("
				+ DataHelper.getFontSize(mContext) + ")";
		isChangeStatus = true;
		this.loadUrl(url);
	}

	/**
	 * 改变行间距
	 */
	public void changeLineHeight() {
		if (!loadOk || ConstData.getAppId() != 20) {
			isChangeStatus = true;
			return;
		}
		String url = "javascript:setLineHeight("
				+ DataHelper.getLineHeight(mContext) + ");";
		isChangeStatus = true;
		this.loadUrl(url);
	}

	/**
	 * 如果传进来的对象没有link，重新获取article
	 * 
	 * @param item
	 */
	private void getArticleById(FavoriteItem item) {
		if (mContext instanceof CommonArticleActivity) {
			OperateController.getInstance(mContext).getArticleById(item,
					new FetchEntryListener() {

						@Override
						public void setData(Entry entry) {
							if (entry instanceof Atlas) {
								String link = ((Atlas) entry).getLink();
								if (!TextUtils.isEmpty(link)
										&& !useUtil.getLocalHtml(link)) {
									loadUrl(link);
								}
							}
						}
					});
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		gestureDetector.onTouchEvent(ev);
		return super.onTouchEvent(ev);
	}

}
