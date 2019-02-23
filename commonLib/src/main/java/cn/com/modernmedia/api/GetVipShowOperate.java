package cn.com.modernmedia.api;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.modernmediaslate.model.User;

/**
 * 扫二维码跳转VIP会员信息
 *
 * @author: zhufei
 */
public class GetVipShowOperate extends BaseOperate {
    private User user = new User();
    private ArrayList<NameValuePair> nameValuePairs; // post参数

    public GetVipShowOperate(String uid, String token, String vipuid) {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject object = new JSONObject();
        try {
            addPostParamsCode(object, "uid", uid);
            addPostParamsCode(object, "token", token);
            addPostParamsCode(object, "vipuid", vipuid);
        } catch (Exception e) {
        }
        params.add(new BasicNameValuePair("data", object.toString()));
        setPostParams(params);
    }

    @Override
    protected String getUrl() {
        return UrlMaker.getVipShow();
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        if (!isNull(jsonObject)) {
            JSONObject obj = jsonObject.optJSONObject("error");
            if (obj.optString("no").equals("0")) {
                user.setRealname(jsonObject.optString("realname"));
                user.setProvince(jsonObject.optString("province"));
                user.setCity(jsonObject.optString("city"));
                user.setLevel(jsonObject.optInt("level"));
                user.setIndustry(jsonObject.optString("industry"));
                user.setVip(jsonObject.optString("vip"));
                user.setNickName(jsonObject.optString("nickname"));
                user.setAvatar(jsonObject.optString("avatar"));
            } else {
                return;
            }
        }
    }

    @Override
    protected void saveData(String data) {

    }

    @Override
    protected String getDefaultFileName() {
        return null;
    }

    @Override
    protected ArrayList<NameValuePair> getPostParams() {
        return nameValuePairs;
    }

    protected void setPostParams(ArrayList<NameValuePair> params) {
        this.nameValuePairs = params;
    }

    public User showVipInfo() {
        return user;
    }
}
