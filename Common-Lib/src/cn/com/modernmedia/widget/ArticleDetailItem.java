package cn.com.modernmedia.widget;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.com.modernmedia.R;
import cn.com.modernmedia.listener.CallWebStatusChangeListener;
import cn.com.modernmedia.listener.WebProcessListener;
import cn.com.modernmedia.model.ArticleItem;

/**
 * 文章详情
 * 
 * @author ZhuQiao
 * 
 */
public abstract class ArticleDetailItem extends BaseView implements
		CallWebStatusChangeListener {
	private Context mContext;
	private CommonWebView webView;
	private ArticleItem detail;
	private int errorType;

	private WebProcessListener listener = new WebProcessListener() {

		@Override
		public void showStyle(int style) {
			errorType = style;
			if (style == 0) {
				disProcess();
			} else if (style == 1) {
				showLoading();
			} else if (style == 2) {
				showError();
			}
		}

	};

	public ArticleDetailItem(Context context, boolean bgIsTransparent) {
		this(context, bgIsTransparent, false);
	}

	public ArticleDetailItem(Context context, boolean bgIsTransparent,
			boolean isSlateWeb) {
		super(context);
		mContext = context;
		init(bgIsTransparent, isSlateWeb);
	}

	/**
	 * 初始化
	 * 
	 * @param bgIsTransparent
	 *            webview 背景是否透明
	 * @param isSlateWeb
	 *            是否slate跳转内部浏览器
	 */
	private void init(boolean bgIsTransparent, boolean isSlateWeb) {
		this.addView(LayoutInflater.from(mContext).inflate(
				R.layout.article_detail, null));
		this.setBackgroundColor(Color.WHITE);
		initProcess();
		LinearLayout ll = (LinearLayout) findViewById(R.id.web_contain);
		webView = new CommonWebView(mContext, bgIsTransparent) {

			@Override
			public void gotoGalleryActivity(List<String> urlList,
					String currentUrl) {
				showGallery(urlList, currentUrl);
			}

			@Override
			protected void onScrollChanged(int l, int t, int oldl, int oldt) {
				super.onScrollChanged(l, t, oldl, oldt);
				doScroll(l, t, oldl, oldt);
			}

			@Override
			public void gotoWriteNewCardActivity(ArticleItem item) {
				myGotoWriteNewCardActivity(item);
			}

		};
		webView.setListener(listener);
		webView.setSlateWeb(isSlateWeb);
		ll.addView(webView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		setBackGroundRes((ImageView) findViewById(R.id.web_back));
	}

	public void setData(ArticleItem detail) {
		this.detail = detail;
		webView.startLoad(detail);
	}

	/**
	 * 改变行间距
	 */
	@Override
	public void changeLineHeight() {
		webView.changeLineHeight();
	}

	/**
	 * 改变字体
	 */
	@Override
	public void changeFontSize() {
		webView.changeFont();
	}

	@Override
	protected void reLoad() {
		// webView.reload();
		listener.showStyle(1);
		webView.startLoad(detail);
	}

	public CommonWebView getWebView() {
		return webView;
	}

	public abstract void setBackGroundRes(ImageView imageView);

	public abstract void showGallery(List<String> urlList, String currentUrl);

	/**
	 * 跳转至写卡片页
	 * 
	 * @param item
	 */
	public void myGotoWriteNewCardActivity(ArticleItem item) {
	}

	public void doScroll(int l, int t, int oldl, int oldt) {
	};

	public void changeFont() {
		webView.changeFont();
	}

	public int getErrorType() {
		return errorType;
	}

	public ArticleItem getDetail() {
		return detail;
	}

}
