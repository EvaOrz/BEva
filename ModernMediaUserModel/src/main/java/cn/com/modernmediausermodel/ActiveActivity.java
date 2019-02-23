package cn.com.modernmediausermodel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;

import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.widget.CommonWebView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.api.UrlMaker;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Active;

/**
 * 活动页面（用于显示赠阅）
 *
 * @author Eva.
 */
public class ActiveActivity extends BaseActivity {
    private Active active;
    private CommonWebView webViewPop;
    private String url = "";
    private TextView title;
    private boolean isFinish = false;// 是否直接返回

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);
        ImageView imageView = findViewById(R.id.active_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webViewPop.canGoBack() && !isFinish) {
                    webViewPop.goBack();// 返回前一个页面
                } else finish();
            }
        });
        title = findViewById(R.id.active_title);
        webViewPop = findViewById(R.id.active_webview);

        webViewPop.setSlateWeb(true);
        webViewPop.getSettings().setDomStorageEnabled(true);
        webViewPop.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webViewPop.getSettings().setAppCachePath(appCachePath);
        webViewPop.getSettings().setAllowFileAccess(true);
        webViewPop.getSettings().setAppCacheEnabled(true);


        if (getIntent() != null && getIntent().getExtras() != null) {
            int is_from_splash = getIntent().getExtras().getInt("is_from_splash", 0);
            String pushArticleUrl = getIntent().getExtras().getString("from_splash_url");
            if (is_from_splash != -1 || TextUtils.isEmpty(pushArticleUrl)) {
                finish();
            }
            pushArticleUrl = pushArticleUrl.replace("slate://webOnlyClose/", "");
            pushArticleUrl = pushArticleUrl.replace("slate://web/", "");
            url = pushArticleUrl;
            handler.sendEmptyMessage(0);

        } else {

            initActiveList();
        }
    }

    /**
     * 初始化活动列表页面
     */
    private void initActiveList() {
        UserOperateController.getInstance(this).getActiveList(SlateDataHelper.getUid(this), SlateDataHelper.getToken(this), new UserFetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                active = (Active) entry;
                if (active != null) {
                    url = active.getUrl();
                    handler.sendEmptyMessage(0);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webViewPop.clearCache(true);
        webViewPop.destroy();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("ActiveActivity url", url);
            if (url.equals(UrlMaker.getMyOrderedUrl())) {
                title.setText(R.string.my_ordered);
                title.setVisibility(View.VISIBLE);
            } else if (url.equals(UrlMaker.getMyLicaiUrl())) {
                title.setText(R.string.my_licai);
                title.setVisibility(View.VISIBLE);
                isFinish = true;
            }
            //            webViewPop.loadDataWithBaseURL(url,"","", HTTP.UTF_8,url);
            webViewPop.loadUrl(url);
            webViewPop.requestFocus();

            //             在当前浏览器跳转不指向外部浏览器
            //            webViewPop.setWebViewClient(new WebViewClient() {
            //                @Override
            //                public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //                    return true;
            //                }
            //            });
        }
    };

    @Override
    public void reLoadData() {

    }


    @Override
    public String getActivityName() {
        return ActiveActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webViewPop.canGoBack()) {
            webViewPop.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, iUiListener);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_API) {
                if (resultCode == Constants.REQUEST_QQ_SHARE || resultCode == Constants.REQUEST_QZONE_SHARE || resultCode == Constants.REQUEST_OLD_SHARE) {
                    Tencent.handleResultData(data, iUiListener);
                }
            }
        }
    }

}