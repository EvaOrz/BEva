package cn.com.modernmedia.widget;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import java.util.List;

import cn.com.modernmedia.CommonSplashActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.PhonePageList;

public class WebViewPop {
    private Context mContext;
    private PopupWindow window;
    private ArticleDetailItem view;
    private CommonWebView webview;

    public WebViewPop(Context context, String link) {
        this(context, link, true);
    }

    /**
     * 商周即时头条点击专用
     *
     * @param context
     * @param link
     * @param titleRes
     */
    public WebViewPop(Context context, final String link, int titleRes) {
        mContext = context;
        View view1 = LayoutInflater.from(mContext).inflate(R.layout.webview_nav_for_zuixin, null);
        // titleRes不为0  则是即时头条，需要加上栏目标题
        if (titleRes != 0) ((TextView) view1.findViewById(R.id.webview_title)).setText(titleRes);

        view1.findViewById(R.id.webview_close).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });

        webview = (CommonWebView) view1.findViewById(R.id.webview_webview);
        webview.loadUrl(link);
        webview.setSlateWeb(true);
        Log.e("WebViewPop geturl", link);

        webview.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    window.dismiss();
                    return true;
                }
                return false;
            }
        });

        window = new PopupWindow(view1, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.setAnimationStyle(R.style.webview_pop);
        window.update();
        window.showAtLocation(view1, Gravity.CENTER, 0, 0);

        window.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                if (webview != null) webview.loadUrl(link);
            }
        });
    }

    public WebViewPop(final Context context, String link, boolean loadCache) {
        mContext = context;
        view = new ArticleDetailItem(mContext, false, true) {

            @Override
            public void showGallery(List<String> urlList, String currentUrl, List<String> descList) {
            }

            @Override
            public void setBackGroundRes(ImageView imageView) {
            }

        };
        if (!loadCache) {
            view.getWebView().getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
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
        window = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.setAnimationStyle(R.style.webview_pop);
        window.update();
        window.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        window.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                if (mContext instanceof CommonSplashActivity) {
                    ((CommonSplashActivity) mContext).gotoMainActivity();
                }
                if (view != null && view.getWebView() != null) view.getWebView().pop();
            }
        });
    }

}