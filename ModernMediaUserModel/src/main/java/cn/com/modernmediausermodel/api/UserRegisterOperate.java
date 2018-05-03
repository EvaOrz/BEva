package cn.com.modernmediausermodel.api;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import cn.com.modernmediaslate.unit.DESCoder;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 注册api
 *
 * @author lusiyuan
 */
public class UserRegisterOperate extends UserModelBaseOperate {

    protected UserRegisterOperate(String userName, String password, String code, String phone, String nick) {
        super();
        setIsNeedEncode(true);
        // post 参数设置
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject object = new JSONObject();
        try {
            object.put("username", userName);
            object.put("password", password);
            object.put("appid", UserConstData.getInitialAppId() + "");

            if (code != null && code.length() > 0) object.put("code", code);// 验证码
            if (phone != null && phone.length() > 0) object.put("phone", phone);// 电话号码
            if (nick != null && nick.length() > 0) {// nick name
                object.put("nickname", URLEncoder.encode(nick, "UTF-8"));
            }
            String msg = DESCoder.encode(KEY, object.toString());
            params.add(new BasicNameValuePair("data", msg));
            setPostParams(params);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getUrl() {
        return UrlMaker.getRegisterUrl();
    }

}
