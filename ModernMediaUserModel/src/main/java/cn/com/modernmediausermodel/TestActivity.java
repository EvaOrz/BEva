package cn.com.modernmediausermodel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.widget.CommonWebView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.DESCoder;
import cn.com.modernmediaslate.unit.MD5;
import cn.com.modernmediaslate.unit.ParseUtil;

public class TestActivity extends Activity {
    private CommonWebView wv;

    private int aId = 10071939;
    private String html = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_webtonative);
        init();
        initData();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        // URI以及js调用本地方法测试
        wv = (CommonWebView) findViewById(R.id.test_webtonative_wv);
        // String url = "file:///android_asset/web/uri-test-landscape.html";
        //        String url = "http://adv.bbwc.cn/articles/webridge-test/index.html";
        //        wv.loadUrl(url);

    }

    private void initData() {
        OperateController.getInstance(this).getArticleHtml(aId, new FetchEntryListener() {
            @Override
            public void setData(Entry entry) {
                if (entry instanceof TagArticleList) {
                    TagArticleList tagArticleList = (TagArticleList) entry;
                    if (ParseUtil.listNotNull(tagArticleList.getArticleList())) {
                        // 把这个文章添在列表末尾
                        final ArticleItem item = tagArticleList.getArticleList().get(0);
                        if (!TextUtils.isEmpty(item.getHtml())) {
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    html = DESCoder.decode(MD5.MD5Encode(aId + "").substring(0, 8), item.getHtml());
                                    wv.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
                                }
                            });
                        }
                    }
                }
            }
        });
    }

}
