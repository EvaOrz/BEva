package cn.com.modernmediausermodel.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.DESCoder;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 开放平台(新浪微博、QQ、微信)账号登录
 *
 * @author: zhufei
 */

public class NewOpenLoginOperate extends UserModelBaseOperate {
    private int mType = 0; // 开放平台类型(1:新浪微博；2:QQ；3:微信；4Facebook；5phone
    private Context context;

    protected NewOpenLoginOperate(Context context, User user, String avatar, String code, int type) {
        super();
        this.context = context;
        setIsNeedEncode(true);
        mType = type;
        // post 参数设置
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject object = new JSONObject();
        try {//4.0整合第三方
            addPostParams(object, "nickname", user.getNickName());
            addPostParams(object, "avatar", avatar);
            if (type == 1) { // 新浪微博
                addPostParams(object, "username", user.getSinaId());
                object.put("openid", user.getSinaId());
                object.put("param", user.getOpenLoginJson());
            } else if (type == 2) { // qq
                addPostParams(object, "username", user.getQqId());
                object.put("openid", user.getQqId());
                object.put("param", user.getOpenLoginJson());
            } else if (type == 3) { // 微信
                addPostParams(object, "username", user.getWeixinId());
                object.put("openid", user.getWeixinId());
                object.put("unionid", user.getUnionId());
                object.put("param", user.getOpenLoginJson());
            } else if (type == 5) { // 手机登录 手机验证码
                object.put("openid", user.getPhone());
                object.put("code", code);
            }
            object.put("logintype", type + "");
            object.put("deviceid", Tools.getDeviceId(context));
            object.put("appid", UserConstData.getInitialAppId() + "");
            Log.e("des2", object.toString());
            String data = DESCoder.encode(KEY, object.toString());

            params.add(new BasicNameValuePair("data", data));
            setPostParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getUrl() {
        return UrlMaker.getOpenLoginUrl();
    }

    @Override
    protected void parseUser(JSONObject object) {
        Log.e("des3", object.toString());
        super.parseUser(object);//3.6整合第三方
        if (!TextUtils.isEmpty(user.getOpenId()) && mType == 2) { // QQ账号登录
            user.setQqId(user.getOpenId());
        } else if (!TextUtils.isEmpty(user.getOpenId()) && mType == 3) {// 微信账号登录
            user.setWeixinId(user.getOpenId());
        } else if (!TextUtils.isEmpty(user.getOpenId()) && mType == 1) {// 新浪微博账号登录
            user.setSinaId(user.getOpenId());
        }
    }
}
