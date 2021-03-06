package cn.com.modernmediausermodel.api;

import android.text.TextUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ModifyUserPasswordOperate extends UserModelBaseOperate {

    protected ModifyUserPasswordOperate(String uid, String token, String userName, String password, String newPassword) {
        super();
        // post 参数设置
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject object = new JSONObject();
        try {
            object.put("uid", uid);
            object.put("token", token);
            object.put("username", userName);
            if (TextUtils.isEmpty(password)) password = "";

            object.put("password", password);
            object.put("newpassword", newPassword);
            params.add(new BasicNameValuePair("data", object.toString()));
            setPostParams(params);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    protected String getUrl() {
        return UrlMaker.getModifyPasswordUrl();
    }

}
