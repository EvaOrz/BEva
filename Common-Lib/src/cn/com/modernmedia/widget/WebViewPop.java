package cn.com.modernmedia.widget;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.PopupWindow;
import cn.com.modernmedia.R;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.PhonePageList;

public class WebViewPop {
	private Context mContext;
	private PopupWindow window;
	private ArticleDetailItem view;

	public WebViewPop(Context context, String link) {
		this(context, link, true);
	}

	public WebViewPop(Context context, String link, boolean loadCache) {
		mContext = context;
		view = new ArticleDetailItem(mContext, false, true) {

			@Override
			public void showGallery(List<String> urlList, String currentUrl) {
			}

			@Override
			public void setBackGroundRes(ImageView imageView) {
			}

		};
		if (!loadCache) {
			view.getWebView().getSettings()
					.setCacheMode(WebSettings.LOAD_NO_CACHE);
		}
		ArticleItem item = new ArticleItem();
		PhonePageList page = new PhonePageList();
		page.setUrl(link);
		item.getPageUrlList().add(page);
		view.setData(item);
		view.getWebView().setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					window.dismiss();
					return true;
				}
				return false;
			}
		});
		window = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		window.setFocusable(true);
		window.setOutsideTouchable(true);
		window.setAnimationStyle(R.style.webview_pop);
		window.update();
		window.showAtLocation(view, Gravity.BOTTOM, 0, 0);
	}

}
