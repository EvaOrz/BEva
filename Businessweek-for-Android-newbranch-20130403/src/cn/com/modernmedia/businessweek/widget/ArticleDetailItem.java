package cn.com.modernmedia.businessweek.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.webkit.WebSettings.TextSize;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.listener.WebProcessListener;
import cn.com.modernmedia.model.ArticleList.ArticleDetail;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.widget.BaseView;

/**
 * 文章详情
 * 
 * @author ZhuQiao
 * 
 */
public class ArticleDetailItem extends BaseView {
	private Context mContext;
	private MyWebView webView;

	private WebProcessListener listener = new WebProcessListener() {

		@Override
		public void showStyle(int style) {
			if (style == 0) {
				disProcess();
			} else if (style == 1) {
				showLoading();
			} else if (style == 2) {
				showError();
			}
		}

	};

	public ArticleDetailItem(Context context) {
		super(context);
		mContext = context;
		init();
	}

	private void init() {
		this.addView(LayoutInflater.from(mContext).inflate(
				R.layout.article_detail, null));
		this.setBackgroundColor(Color.WHITE);
		initProcess();
		webView = (MyWebView) findViewById(R.id.web);
		webView.setListener(listener);
	}

	public void setData(ArticleDetail detail) {
		webView.startLoad(detail);
	}

	@Override
	protected void reLoad() {
		webView.reload();
	}

	/**
	 * 改变文字大小
	 */
	public void changeFont() {
		if (DataHelper.getFontSize(mContext) == 1) {
			if (webView.getSettings().getTextSize() != TextSize.NORMAL) {
				webView.getSettings().setTextSize(TextSize.NORMAL);
			}
		} else {
			if (webView.getSettings().getTextSize() != TextSize.LARGER) {
				webView.getSettings().setTextSize(TextSize.LARGER);
			}
		}
	}
}
