package cn.com.modernmediausermodel.api;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.model.User;

/**
 * 用户模块基础operate
 *
 * @author ZhuQiao
 */
public abstract class UserModelBaseOperate extends SlateBaseOperate {
    protected User user;
    private ArrayList<NameValuePair> nameValuePairs; // post参数

    public UserModelBaseOperate() {
        user = new User();
    }

    public User getUser() {
        return user;
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        parseUser(jsonObject);
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

    //	/**
    //	 * 登陆成功后获取用户付费信息，由登陆接口提供
    //	 */
    //	protected abstract  void getIssueLevel();

    /**
     * 解析USER模块相关请求结果
     *
     * @param object 待解析的JSON对象
     * @return User对象
     */
    protected void parseUser(JSONObject object) {
        Log.e("Userbasemodel", getUrl() + object.toString());
        user.setUid(object.optString("uid", ""));
        user.setUserName(object.optString("username", ""));
        user.setPassword(object.optString("password", ""));
        user.setPhone(object.optString("phone", ""));
        user.setEmail(object.optString("email", ""));
        user.setNickName(object.optString("nickname", ""));
        user.setAvatar(object.optString("avatar", ""));
        user.setSinaId(object.optString("sinaid", ""));
        user.setOpenId(object.optString("openid", ""));//4.0过度整合用
        user.setToken(object.optString("token", ""));
        user.setDeviceId(object.optString("deviceid", ""));
        user.setDeviceToken(object.optString("devicetoken", ""));
        user.setNewPassword(object.optString("newpassword", ""));
        user.setAppid(object.optString("appid", ""));
        user.setVersion(object.optString("version", ""));
        user.setDesc(object.optString("desc", ""));
        user.setPushEmail(object.optInt("pushmail"));
        user.setRealname(object.optString("realname"));
        user.setSex(object.optInt("sex"));
        user.setBirth(object.optString("birthday"));
        user.setIndustry(object.optString("industry"));
        user.setPosition(object.optString("position"));
        user.setIncome(object.optString("income"));
        user.setStart_time(object.optLong("start_time"));
        user.setVip_end_time(object.optLong("end_time"));
        user.setVip(object.optString("vip"));
        user.setLevel(object.optInt("level"));
        user.setCompletevip(object.optInt("completevip"));
        user.setSend(object.optString("send"));
        user.setProvince(object.optString("province"));
        user.setCity(object.optString("city"));
        user.setAddress(object.optString("address"));
        user.setUser_status(object.optInt("user_status"));
        user.setWeixinId(object.optString("weixinid", "")); //整合微信
        user.setQqId(object.optString("qqid", ""));         //整合QQ
        JSONObject errorObject = object.optJSONObject("error");
        if (!isNull(errorObject)) {
            user.getError().setNo(errorObject.optInt("no", -1));
            user.getError().setDesc(errorObject.optString("desc", ""));
        }
    }
}
