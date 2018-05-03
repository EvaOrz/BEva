package cn.com.modernmedia.util.sina;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import cn.com.modernmediaslate.listener.OpenAuthListener;
import cn.com.modernmediaslate.unit.DateFormatTool;

/**
 * 新浪微博认证类
 *
 * @author jiancong
 */
public class SinaAuth {
    /**
     * 授权认证所需要的信息
     */
    public AuthInfo mAuthInfo;
    /**
     * SSO 授权认证实例
     */
    public SsoHandler mSsoHandler;
    /**
     * 微博授权认证回调
     */
    private OpenAuthListener openAuthListener;
    private Context mContext;
    public Oauth2AccessToken mAccessToken;


    public SinaAuth(Context context) {
        this.mContext = context;
        // 创建授权认证信息
        mAuthInfo = new AuthInfo(context, SinaConstants.APP_KEY, SinaConstants.REDIRECT_URL, SinaConstants.SCOPE);
    }


    /**
     * 设置微博授权所需信息以及回调函数。
     *
     * @param openAuthListener 微博授权认证回调接口
     */
    public void setWeiboAuthListener(OpenAuthListener openAuthListener) {
        this.openAuthListener = openAuthListener;
    }

    /**
     * 弹出窗口进行微博认证
     */
    public void oAuth() {

        if (null == mSsoHandler && mAuthInfo != null) {
            mSsoHandler = new SsoHandler((Activity) mContext, mAuthInfo);
        }

        if (mSsoHandler != null) {
            mSsoHandler.authorize(new AuthListener());
        } else {
            Log.e("SINA LOGIN", "Please setWeiboAuthInfo(...) for first");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 确认当前应用是否已经授权，若授权则检查token是否有效
     *
     * @return
     */
    public boolean checkIsOAuthed() {
        mAccessToken = AccessTokenKeeper.readAccessToken(mContext);
        return mAccessToken.isSessionValid();
    }

    /**
     * 清空token信息
     */
    public void clear() {
        AccessTokenKeeper.clear(mContext);
    }


    /**
     * 微博认证授权回调类。 1.SSO 授权时,需要在 {@link #onActivityResult} 中调用
     * {@link SsoHandler#authorizeCallBack} * 后,该回调才会被执行。 2. 非 SSO
     * 授权时,当授权结束后,该回调就会被执行。 当授权成功后,请保存该 access_token、expires_in、uid 等信息到
     * SharedPreferences 中。
     */
    private class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken != null && mAccessToken.isSessionValid()) {
                String date = DateFormatTool.format(mAccessToken.getExpiresTime(), "yyyy/MM/dd HH:mm:ss");
                String text = String.format("Token：%1$s \n有效期：%2$s", mAccessToken.getToken(), date);
                AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
                if (openAuthListener != null)
                    openAuthListener.onCallBack(true, mAccessToken.getUid(), mAccessToken.getToken());
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            System.out.println("auth exception:" + e.getMessage());
            openAuthListener.onCallBack(false, null, null);
        }

        @Override
        public void onCancel() {
            openAuthListener.onCallBack(false, null, null);
        }
    }
}
