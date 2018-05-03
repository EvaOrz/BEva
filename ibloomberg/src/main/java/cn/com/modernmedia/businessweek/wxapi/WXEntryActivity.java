package cn.com.modernmedia.businessweek.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.com.modernmedia.BaseActivity;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.LoginActivity;
import cn.com.modernmediausermodel.UserInfoActivity;

/**
 * 微信登陆和分享返回
 *
 * @author lusiyuan
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    private User user;

    @Override
    public Activity getActivity() {
        return WXEntryActivity.this;
    }

    @Override
    public String getActivityName() {
        return WXEntryActivity.class.getName();
    }

    @Override
    public void reLoadData() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("分享", "weixinshare");
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
        if (resp.errCode == BaseResp.ErrCode.ERR_OK && resp.state != null && resp.state.equals("weixin_login")) {
            //            showLoadingDialog(true);
            // 用户同意
            final SendAuth.Resp re = (SendAuth.Resp) resp;
            // int expireDate = re.expireDate;// 过期时间
            // Log.i("onResp", "expireDate" + expireDate);
            int errCode = re.errCode;// 错误码
            Log.i("onResp", "errCode" + errCode);
            String errStr = re.errStr;// 错误描述
            Log.i("onResp", "errStr" + errStr);
            String state = re.state; // 状态
            Log.i("onResp", "state" + state);
            String token = re.code;// 应该是类似code
            Log.i("onResp", "token" + token);
            String resultUrl = re.url;// 应该是url
            Log.i("onResp", "resultUrl" + resultUrl);

            new Thread() {

                @Override
                public void run() {
                    try {
                        URL mUrl = new URL("https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + SlateApplication.mConfig.getWeixin_app_id() + "&secret=" + SlateApplication.mConfig.getWeixin_app_secret() + "&code=" + re.code + "&grant_type=authorization_code");
                        HttpURLConnection conn = null;
                        Log.e("流量bug查询**", "WXEntryActivity:handleIntent()" + "-----" + mUrl.toString());
                        conn = (HttpURLConnection) mUrl.openConnection();

                        conn.getInputStream();
                        if (conn.getResponseCode() == HttpStatus.SC_OK) {
                            InputStream is = conn.getInputStream();
                            String data = receiveData(is);//{"errcode":40029,"errmsg":"invalid code, hints: [ req_id: 9dlIdA0696ssz5 ]"}
                            Log.e("微信获取权限", data);
                            JSONObject json = new JSONObject(data);
                            URL getInfo = new URL("https://api.weixin.qq.com/sns/userinfo?access_token=" + json.getString("access_token") + "&openid=" + json.getString("openid"));
                            HttpURLConnection conn1 = null;
                            Log.e("流量bug查询**", "WXEntryActivity:handleIntent()" + "-----" + getInfo.toString());
                            conn1 = (HttpURLConnection) getInfo.openConnection();
                            conn1.getInputStream();
                            if (conn1.getResponseCode() == HttpStatus.SC_OK) {
                                InputStream is1 = conn1.getInputStream();
                                String data1 = receiveData(is1);
                                Log.i("user_info", "&&&&&&" + data1);
                                JSONObject info = new JSONObject(data1);
                                doAfterWeiXinIsAuthed(info);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }.start();
        } else finish();
    }

    private void doAfterWeiXinIsAuthed(JSONObject json) {
        try {
            String weiId = json.getString("openid");
            user = SlateDataHelper.getUserLoginInfo(this);
            if (user != null && !TextUtils.isEmpty(user.getWeixinId()) && user.getWeixinId().equals(weiId)) { // 已经用微信账号在本应用上登录
                if (LoginActivity.weixin_login == 1) {//微信登录
                    if (LoginActivity.sWXLoginCallback != null)
                        LoginActivity.sWXLoginCallback.onLogin(false, user);
                    LoginActivity.weixin_login = 0;
                } else if (UserInfoActivity.weixin_band == 2) {//微信绑定
                    if (UserInfoActivity.sWXBandCallback != null)
                        UserInfoActivity.sWXBandCallback.onBand(false, user);
                    UserInfoActivity.weixin_band = 0;
                }
                Log.i("WXEntryActivity 登陆过", "user.getUserName()" + user.getUserName());
                finish();
            } else {
                user = new User();
                // 用户名不为空时，说明以前登录过；反之，则为第一次登录
                user.setNickName(json.optString("nickname"));
                user.setAvatar(json.optString("headimgurl"));
                user.setUnionId(json.optString("unionid"));//4.0新增
                user.setOpenLoginJson(json.toString());//4.0新增
                user.setWeixinId(weiId);
                if (LoginActivity.weixin_login == 1) {//微信登录
                    if (LoginActivity.sWXLoginCallback != null)
                        LoginActivity.sWXLoginCallback.onLogin(true, user);
                    LoginActivity.weixin_login = 0;
                } else if (UserInfoActivity.weixin_band == 2) {//微信绑定
                    if (UserInfoActivity.sWXBandCallback != null)
                        UserInfoActivity.sWXBandCallback.onBand(true, user);
                    UserInfoActivity.weixin_band = 0;
                }
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String receiveData(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buff = new byte[1024];
            int readed = -1;
            while ((readed = is.read(buff)) != -1) {
                baos.write(buff, 0, readed);
            }
            byte[] result = baos.toByteArray();
            if (result == null) return null;
            return new String(result);
        } finally {
            if (is != null) is.close();
            if (baos != null) baos.close();
        }
    }

    @Override
    public void onReq(BaseReq arg0) {
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:

                SendAuth.Resp re = (SendAuth.Resp) resp;

                // int expireDate = re.expireDate;// 过期时间

                // Log.i("onResp", "expireDate" + expireDate);

                int errCode = re.errCode;// 错误码

                Log.i("onResp", "errCode" + errCode);

                String errStr = re.errStr;// 错误描述

                Log.i("onResp", "errStr" + errStr);

                String state = re.state; // 状态

                Log.i("onResp", "state" + state);

                String token = re.code;// 应该是类似code

                Log.i("onResp", "token" + token);

                String resultUrl = re.url;// 应该是url

                Log.i("onResp", "resultUrl" + resultUrl);

                break;
        }
        //        UserCentManager.getInstance(this).shareArticleCoinCent();
        //        Log.d("weixinshare", "微信分享加积分");
        this.finish();
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}