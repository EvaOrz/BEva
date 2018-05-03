package cn.com.modernmediausermodel.api;

import android.text.TextUtils;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import cn.com.modernmedia.util.ConstData;

/**
 * 修改用户信息
 *
 * @author ZhuQiao
 */
public class ModifyUserInfoOperate extends UserModelBaseOperate {

    /**
     * 修改用户资料 password为空时，更新用户昵称、头像；反之则更新邮箱
     * <p/>
     * 3.5.0 都为空，则订阅邮件
     *
     * @param uid
     * @param token
     * @param userName
     * @param nickName
     * @param url
     * @param password
     * @param desc
     */
    protected ModifyUserInfoOperate(String uid, String token, String userName, String nickName,
                                    String url, String password, String desc, boolean pushEmail) {
        super();
        // post 参数设置
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject object = new JSONObject();
        try {
            object.put("uid", uid);
            object.put("token", token);
            object.put("appid", ConstData.getInitialAppId());
            if (pushEmail) {
                object.put("pushmail", 1);
            } else {
                if (!TextUtils.isEmpty(password)) {
                    object.put("password", password);
                    if (!TextUtils.isEmpty(userName)) {
                        userName = URLEncoder.encode(userName, "UTF-8");
                        object.put("username", userName);
                    }
                } else {
                    if (!TextUtils.isEmpty(nickName)) {
                        nickName = URLEncoder.encode(nickName, "UTF-8");
                        object.put("nickname", nickName);
                    }
                    if (!TextUtils.isEmpty(url)) object.put("avatar", url);
                    if (!TextUtils.isEmpty(desc))
                        object.put("desc", URLEncoder.encode(desc, "UTF-8"));
                }
            }
            params.add(new BasicNameValuePair("data", object.toString()));
            Log.v("avatar", object.toString());
            setPostParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 补全VIP资料
     *
     * @return
     */
    protected ModifyUserInfoOperate(String uid, String token, String realname, int sex, String birth, String province, String city
            , String industry, String position, String income, String phone) {
        super();
        // post 参数设置
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject object = new JSONObject();
        try {
            object.put("uid", uid);
            object.put("token", token);
            object.put("appid", ConstData.getInitialAppId());
            realname = URLEncoder.encode(realname, "UTF-8");
            object.put("realname", realname);
            object.put("sex", sex);
            birth = URLEncoder.encode(birth, "UTF-8");
            object.put("birthday", birth);
            province = URLEncoder.encode(province, "UTF-8");
            object.put("province", province);
            city = URLEncoder.encode(city, "UTF-8");
            object.put("city", city);
            industry = URLEncoder.encode(industry, "UTF-8");
            object.put("industry", industry);
            position = URLEncoder.encode(position, "UTF-8");
            object.put("position", position);
            income = URLEncoder.encode(income, "UTF-8");
            object.put("income", income);
            phone = URLEncoder.encode(phone, "UTF-8");
            object.put("phone", phone);

            params.add(new BasicNameValuePair("data", object.toString()));
            setPostParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getUrl() {
        return UrlMaker.getModifyInfoUrl();
    }
}
