package cn.com.modernmedia.businessweek;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.youzan.androidsdk.YouzanToken;
import com.youzan.androidsdk.basic.YouzanBrowser;
import com.youzan.androidsdk.event.AbsAuthEvent;
import com.youzan.androidsdk.event.AbsShareEvent;
import com.youzan.androidsdk.event.AbsStateEvent;
import com.youzan.androidsdk.model.goods.GoodsShareModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.common.SharePopWindow;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.pay.newlogic.PayHttpsOperate;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.LoginActivity;

/**
 * 有赞商品详情页面
 * Created by Eva. on 2018/1/30.
 */

public class YouzanGoodActivity extends BaseActivity implements View.OnClickListener {
    final static int REQUEST_CODE_LOGIN = 0x11;
    private YouzanBrowser youzanWebview;
    private String pid;
    private YouzanToken token;
    private ArticleItem articleItem;
    private TextView titleTxt;
    private boolean isGoLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youzan_good);
        pid = getIntent().getStringExtra("youzan_good_id");
        if (TextUtils.isEmpty(pid)) {
            showToast("商品信息缺失");
            finish();
        }
        if (getIntent().getSerializableExtra("youzan_good_item") != null && getIntent().getSerializableExtra("youzan_good_item") instanceof ArticleItem) {
            articleItem = (ArticleItem) getIntent().getSerializableExtra("youzan_good_item");
        }
        initView();


    }

    @Override
    protected void onResume() {
        youzanWebview.onResume();

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        youzanWebview.onPause();
    }

    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
        if (youzanWebview != null) {
            youzanWebview.destroy();
            youzanWebview = null;
        }
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (youzanWebview == null || !youzanWebview.pageGoBack()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.youzan_finish:
                finish();
                break;
            case R.id.youzan_back:
                if (youzanWebview.canGoBack()) {
                    youzanWebview.pageGoBack();
                } else {
                    finish();
                }
                break;
            case R.id.youzan_share:
                youzanWebview.sharePage();
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_LOGIN) {
                youzanLogin();
            }
        } else {
            if (SlateDataHelper.getUserLoginInfo(YouzanGoodActivity.this) == null && youzanWebview.canGoBack()) {
                youzanWebview.pageGoBack();
            }
        }
    }

    private void initView() {
        youzanWebview = findViewById(R.id.youzan_webview);
        findViewById(R.id.youzan_back).setOnClickListener(this);
        findViewById(R.id.youzan_finish).setOnClickListener(this);
        findViewById(R.id.youzan_share).setOnClickListener(this);
        titleTxt = findViewById(R.id.youzan_title);

        //        if (articleItem != null) titleTxt.setText(articleItem.getTitle());
        showLoadingDialog(true);
        youzanWebview.loadUrl("https://h5.youzan.com/v2/goods/" + pid);
        youzanWebview.subscribe(new AbsStateEvent() {
            @Override
            public void call(Context context) {
                showLoadingDialog(false);
            }
        });
        youzanWebview.subscribe(new AbsAuthEvent() {

            /**
             * 建议实现逻辑:
             *
             *     判断App内的用户是否登录?
             *       => 已登录: 请求带用户角色的认证信息(login接口);
             *       => 未登录: needLogin为true, 唤起App内登录界面, 请求带用户角色的认证信息(login接口);
             *       => 未登录: needLogin为false, 请求不带用户角色的认证信息(initToken接口).
             *
             *     商周不考虑不需要登录的情况
             */
            @Override
            public void call(Context context, boolean needLogin) {
                Log.e("yyyyyy", needLogin ? "true" : "false");
                //判断App内的用户是否登录
                if (SlateDataHelper.getUserLoginInfo(YouzanGoodActivity.this) != null) {
                    youzanLogin();
                } else {//调用login接口, 获取数据, 组装成YouzanToken, 回传给 mView.sync()
                    Intent intent = new Intent(YouzanGoodActivity.this, LoginActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_LOGIN);
                }
            }
        });

        //分享事件, 回调表示: 获取到当前页面的分享信息数据
        youzanWebview.subscribe(new AbsShareEvent() {
            @Override
            public void call(Context context, GoodsShareModel data) {
                Log.e("yyyyyyy", data.getTitle());
                ArticleItem articleItem = new ArticleItem();
                articleItem.setTitle(data.getTitle());
                articleItem.setDesc(data.getDesc());
                List<ArticleItem.Picture> pics = new ArrayList<ArticleItem.Picture>();
                ArticleItem.Picture p = new ArticleItem.Picture();
                p.setUrl(data.getImgUrl());
                pics.add(p);
                articleItem.setPicList(pics);
                articleItem.setWeburl(data.getLink());
                new SharePopWindow(YouzanGoodActivity.this, articleItem);

            }
        });
    }

    /**
     * {
     * "error": {
     * "no": 0,//非0代表调用异常
     * "desc": ""//非0时的错误描述
     * },
     * "accesstoken": "17b3401c9b043be597b2b8a64e586533",//访问token，携带用户信息，有效期7天
     * "cookiekey": "open_cookie_ebbf3dd0d5b9a74345",//设置的COOKIE的key值，建议128长度字符串
     * "cookievalue": "YZ403519474055196672YZVPPdey7a"//设置的COOKIE的value值，建议128长度字符串
     * }
     */
    private void youzanLogin() {
        PayHttpsOperate.getInstance(this).youzanLogin(new FetchDataListener() {
            @Override
            public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                if (isSuccess) {
                    Log.e("yyyyyyyy", data);
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        if (jsonObject == null) return;
                        JSONObject ejson = jsonObject.optJSONObject("error");
                        if (ejson.optInt("no", -1) == 0) {
                            String accesstoken = jsonObject.optString("accesstoken");
                            String cookiekey = jsonObject.optString("cookiekey");
                            String cookievalue = jsonObject.optString("cookievalue");
                            token = new YouzanToken();
                            token.setAccessToken(accesstoken);
                            token.setCookieKey(cookiekey);
                            token.setCookieValue(cookievalue);
                            handler.sendEmptyMessage(0);

                        } else {
                            showToast(ejson.optString("desc"));
                        }

                    } catch (JSONException e) {

                    }

                }
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            youzanWebview.sync(token);
        }
    };

    @Override
    public String getActivityName() {
        return YouzanGoodActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return YouzanGoodActivity.this;
    }

    @Override
    public void reLoadData() {

    }
}
