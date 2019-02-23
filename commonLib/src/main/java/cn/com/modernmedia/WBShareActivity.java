package cn.com.modernmedia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;

import cn.com.modernmedia.util.sina.AccessTokenKeeper;
import cn.com.modernmedia.util.sina.SinaAuth;
import cn.com.modernmediaslate.listener.OpenAuthListener;
import cn.com.modernmediaslate.unit.DateFormatTool;


/**
 * Created by Eva. on 17/5/25.
 */

public class WBShareActivity extends BaseActivity implements IWeiboHandler.Response, WeiboAuthListener {


    /**
     * 微博微博分享接口实例
     */
    private IWeiboShareAPI mWeiboShareAPI = null;

    private SinaAuth sinaAuth;
    private String content, path;
    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new View(this));
        // 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, CommonApplication.mConfig.getWeibo_app_id());

        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
        mWeiboShareAPI.registerApp();

        // 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }


        sinaAuth = new SinaAuth(this);
        content = getIntent().getStringExtra("SINA_CONTENT");
        path = getIntent().getStringExtra("SINA_BITMAP");

        bitmap = BitmapFactory.decodeFile(path);
        if (bitmap == null) {
            handler.sendEmptyMessage(1);
        }
        handler.sendEmptyMessage(0);


    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
                        showLoading();
                        sendMultiMessage(content, bitmap);

                    } else {
                        Toast.makeText(WBShareActivity.this, "api too low", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    showToast("获取分享图片失败");
                    break;
            }
        }
    };

    @Override
    public void onResponse(BaseResponse baseResp) {
        if (baseResp != null) {
            switch (baseResp.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    Toast.makeText(this, "分享成功", Toast.LENGTH_LONG).show();
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    Toast.makeText(this, "分享取消", Toast.LENGTH_LONG).show();
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    Toast.makeText(this, "Error Message: " + baseResp.errMsg, Toast.LENGTH_LONG).show();
                    break;
            }
            finish();
        }
    }

    /**
     * @see {@link Activity#onNewIntent}
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    private void sendMultiMessage(String content, Bitmap bitmap) {

        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = getTextObj(content);
        if (bitmap != null) weiboMessage.imageObject = getImageObj(bitmap);

        // 2. 初始化从第三方到微博的消息请求
        final SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        //这里直接调用客户端，避免与Web分享时的token冲突
        if (mWeiboShareAPI.isWeiboAppInstalled()) {
            boolean isSuccess = mWeiboShareAPI.sendRequest(WBShareActivity.this, request);
            //Log.e("sinaweibo", "sinaweibo send share msg result is : " + isSuccess);
            return;
        }

        // 新浪微博认证
        sinaAuth = new SinaAuth(this);
        if (!sinaAuth.checkIsOAuthed()) {
            sinaAuth.oAuth();
        } else {
            mWeiboShareAPI.sendRequest(this, request, sinaAuth.mAuthInfo, sinaAuth.mAccessToken.getToken(), this);
        }
        sinaAuth.setWeiboAuthListener(new OpenAuthListener() {

            @Override
            public void onCallBack(boolean isSuccess, String uid, String token) {

                mWeiboShareAPI.sendRequest(WBShareActivity.this, request, sinaAuth.mAuthInfo, token, WBShareActivity.this);

            }


        });


        //Log.e("sinaweibo", "sinaweibo send share msg result is : " + isSuccess);
    }

    @Override
    public void onWeiboException(WeiboException arg0) {
        Log.e("sinaweibo", "WeiboAuthListener onWeiboException: ");
        arg0.printStackTrace();
        //        GameShare.deleteImage();
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (sinaAuth != null) sinaAuth.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj(String content) {
        TextObject textObject = new TextObject();
        textObject.text = content;
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj(Bitmap bitmap) {
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    @Override
    public void onComplete(Bundle bundle) {
        Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(bundle);
        if (accessToken != null && accessToken.isSessionValid()) {
            String date = DateFormatTool.format(accessToken.getExpiresTime(), "yyyy/MM/dd HH:mm:ss");
            String text = String.format("Token：%1$s \n有效期：%2$s", accessToken.getToken(), date);
            AccessTokenKeeper.writeAccessToken(this, accessToken);
        }
    }

    @Override
    public void onCancel() {
        Log.e("sinaweibo", "WeiboAuthListener oncancel");
        //        GameShare.deleteImage();
        finish();
    }

    @Override
    public String getActivityName() {
        return WBShareActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return WBShareActivity.this;
    }

    @Override
    public void reLoadData() {

    }

}
