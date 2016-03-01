package cn.com.modernmedia.widget;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.com.modernmedia.R;
import cn.com.modernmedia.listener.CallWebStatusChangeListener;
import cn.com.modernmedia.listener.WebProcessListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.pay.PayActivity;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.unit.SlateDataHelper;

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
	private boolean bgIsTransparent, isSlateWeb;
	private LinearLayout web_contain;
	private RelativeLayout payView;// 付费遮盖层

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
		this.bgIsTransparent = bgIsTransparent;
		this.isSlateWeb = isSlateWeb;
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
	@SuppressLint("NewApi")
	private void init(boolean bgIsTransparent, boolean isSlateWeb) {
		this.addView(LayoutInflater.from(mContext).inflate(
				R.layout.article_detail, null));
		this.setBackgroundColor(Color.WHITE);
		initProcess();
		initPayView();
		web_contain = (LinearLayout) findViewById(R.id.web_contain);
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
		webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		webView.setListener(listener);
		webView.setSlateWeb(isSlateWeb);
		web_contain.addView(webView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		setBackGroundRes((ImageView) findViewById(R.id.web_back));
	}

	/**
	 * 初始化付费遮盖view
	 */
	private void initPayView() {
		payView = (RelativeLayout) findViewById(R.id.default_pay_view);
		payView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, PayActivity.class);
				mContext.startActivity(i);
			}
		});
		ImageView payBanner = (ImageView) findViewById(R.id.pay_bannar_view);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.pay_bannar_view);
		int bwidth = bitmap.getWidth();
		int bHeight = bitmap.getHeight();

		int height = mContext.getResources().getDisplayMetrics().widthPixels
				* bHeight / bwidth;
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, height);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		payBanner.setLayoutParams(lp);
	}

	public void setData(ArticleItem detail) {
		this.detail = detail;
		int level = detail.getProperty().getLevel();// 付费
		webView.startLoad(detail);

		if (SlateApplication.APP_ID == 1
				&& level == 1
				&& !TextUtils.equals("1",
						SlateDataHelper.getIssueLevel(mContext))) {// 如果是商周，并且需要付费
			payView.setVisibility(View.VISIBLE);
		}
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

	@Override
	public void checkPayStatus() {
		if (SlateApplication.APP_ID == 1
				&& detail.getProperty().getLevel() == 1
				&& !TextUtils.equals("1",
						SlateDataHelper.getIssueLevel(mContext))) {// 如果是商周或者Vericity，并且需要付费
			payView.setVisibility(View.VISIBLE);
		} else {
			payView.setVisibility(View.GONE);
		}
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
