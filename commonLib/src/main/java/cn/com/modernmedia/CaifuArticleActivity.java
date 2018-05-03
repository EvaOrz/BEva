package cn.com.modernmedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ShangchengIndex;
import cn.com.modernmedia.model.ShangchengIndex.ShangchengIndexItem;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.widget.CommonWebView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;

/**
 * 财富文章详情页
 * Created by Eva. on 2018/1/11.
 */

public class CaifuArticleActivity extends BaseActivity {
    private CommonWebView webView;
    private TextView titleTxt;
    private ImageView button;
    private String pid, title, articleId;
    private ArticleItem detail;
    private ShangchengIndexItem pro;
    private OperateController operateController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caifu_article);
        articleId = getIntent().getStringExtra("caifu_aid");
        pid = getIntent().getStringExtra("caifu_pid");
        title = getIntent().getStringExtra("caifu_title");
        operateController = OperateController.getInstance(this);
        initView();
        loadArticleHtml();
        checkLevel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CommonApplication.loginStatusChange) handler.sendEmptyMessage(0);// 刷新登录状态
        handler.sendEmptyMessage(1);
    }


    private boolean checkLogin() {
        return SlateDataHelper.getUserLoginInfo(this) != null;
    }


    private void checkLevel() {
        if (TextUtils.isEmpty(pid)) return;
        operateController.getShangchengSplash(this, pid, new FetchEntryListener() {
            @Override
            public void setData(Entry entry) {
                if (entry != null && entry instanceof ShangchengIndex.ShangchengIndexItem) {
                    pro = (ShangchengIndexItem) entry;

                }
                handler.sendEmptyMessage(1);
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (detail != null) webView.startLoadHtml(detail);
                    break;
                case 1:
                    button.setVisibility(View.VISIBLE);
                    if (pro != null) {
                        // 有权限
                        if (SlateDataHelper.getLevelByType(CaifuArticleActivity.this, pro.getReadLevel())) {
                            button.setImageResource(R.drawable.yiding);
                        } else {
                            button.setImageResource(R.drawable.dinggou);
                        }
                    }
                    break;
            }
        }
    };

    /**
     * 获取文章详情
     */
    private void loadArticleHtml() {
        if (TextUtils.isEmpty(articleId)) return;
        operateController.getArticleHtml(Integer.valueOf(articleId), new FetchEntryListener() {
            @Override
            public void setData(Entry entry) {
                if (entry instanceof TagArticleList) {
                    TagArticleList tagArticleList = (TagArticleList) entry;
                    if (ParseUtil.listNotNull(tagArticleList.getArticleList())) {
                        detail = tagArticleList.getArticleList().get(0);
                        handler.sendEmptyMessage(0);
                    }
                }
            }
        });
    }

    private void initView() {
        webView = findViewById(R.id.caifu_article_web);
        titleTxt = findViewById(R.id.caifu_article_title);
        button = findViewById(R.id.caifu_article_btn);
        findViewById(R.id.caifu_article_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        try {
            titleTxt.setText(URLDecoder.decode(title, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(pid)) {
                    if (checkLogin()) {
                        if (pro != null && !SlateDataHelper.getLevelByType(CaifuArticleActivity
                                .this, pro.getReadLevel())) {
                            String broadcastIntent = "cn.com.modernmedia.shangcheng_info";
                            Intent i = new Intent(broadcastIntent);
                            i.putExtra("is_from_splash", false);
                            i.putExtra("ShangchengList_type", pid);
                            sendBroadcast(i);

                        }

                    } else {
                        String broadcastIntent = "cn.com.modernmediausermodel.LoginActivity_nomal";
                        Intent intent = new Intent(broadcastIntent);
                        sendBroadcast(intent);
                    }

                }
            }
        });
    }

    @Override
    public void reLoadData() {

    }

    @Override
    public String getActivityName() {
        return CaifuArticleActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return CaifuArticleActivity.this;
    }

}
