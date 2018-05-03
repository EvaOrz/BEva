package cn.com.modernmediausermodel.api;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.modernmedia.util.ConstData;

public class GetUserInfoOperate extends UserModelBaseOperate {

    protected GetUserInfoOperate(String uid, String token) {
        super();
        // post 参数设置
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject object = new JSONObject();
        try {
            object.put("uid", uid);
            object.put("token", token);
            object.put("appid", ConstData.getAppId());
            params.add(new BasicNameValuePair("data", object.toString()));
            setPostParams(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected String getUrl() {
        return UrlMaker.getUserInfoUrlByUidAndToken();
    }

}
