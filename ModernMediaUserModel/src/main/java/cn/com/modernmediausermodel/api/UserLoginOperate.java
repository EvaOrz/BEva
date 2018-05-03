package cn.com.modernmediausermodel.api;

import android.content.Context;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.modernmediaslate.unit.DESCoder;
import cn.com.modernmediausermodel.util.UserConstData;

public class UserLoginOperate extends UserModelBaseOperate {
    private Context context;

    protected UserLoginOperate(Context context, String userName, String password) {
        super();
        this.context = context;
        setIsNeedEncode(true);
        // post 参数设置
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject object = new JSONObject();
        try {
            // username有邮箱check，可以不编码
            object.put("username", userName);
            // 密码在输入上已经做了限制，也可以不编码
            object.put("password", password);
            object.put("appid", UserConstData.getInitialAppId() + "");
            String msg = DESCoder.encode(KEY, object.toString());
            params.add(new BasicNameValuePair("data", msg));
            setPostParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected String getUrl() {
        return UrlMaker.getLoginUrl();
    }

    @Override
    protected void parseUser(JSONObject object) {
        super.parseUser(object);
        // 商周繁体版和verycity 如果是已支付状态
        //        if ((SlateApplication.APP_ID == 18 || SlateApplication.APP_ID == 37) && SlateDataHelper.getIssueLevel(context).equals("1"))
        //            return;
    }

}
