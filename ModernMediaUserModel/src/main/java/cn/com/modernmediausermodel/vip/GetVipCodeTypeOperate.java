package cn.com.modernmediausermodel.vip;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.modernmedia.api.BaseOperate;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediausermodel.api.UrlMaker;

/**
 * 获取兑换码类型 接口
 * Created by Eva. on 17/2/28.
 */

public class GetVipCodeTypeOperate extends BaseOperate {
    private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();// post参数

    private VipCodeType vipCodeType;

    public GetVipCodeTypeOperate(String uid, String token, String sn) {

        JSONObject jsonObject = new JSONObject();
        try {
            addPostParamsCode(jsonObject, "uid", uid);
            addPostParamsCode(jsonObject, "token", token);
            addPostParamsCode(jsonObject, "appid", String.valueOf(ConstData.getAppId()));
            addPostParamsCode(jsonObject, "sn", sn);
        } catch (JSONException e) {

        } catch (Exception e) {

        }
        params.add(new BasicNameValuePair("data", jsonObject.toString()));
        setPostParams(params);
    }

    @Override
    protected String getUrl() {
        return UrlMaker.getCodeVip();
    }

    /**
     * {
     * "id": "1",
     * "title": "7天阅读权限",
     * "desc": "赠阅",
     * "isvip": "1",   #0不是vip 1是vip
     * "needaddress": "1",   #0 不需要补全地址，1需要
     * "error": {
     * "code": 200,
     * "msg": "成功"
     * }
     * }
     *
     * @param jsonObject
     */
    @Override
    protected void handler(JSONObject jsonObject) {
        if (jsonObject != null) {
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
        return params;
    }

    protected void setPostParams(ArrayList<NameValuePair> params) {
        this.params = params;
    }

    public VipCodeType getVipCodeType() {
        return vipCodeType;
    }

    /**
     * 兑换码类型 model
     */
   public class VipCodeType extends Entry {
        private String isvip;
        private String needaddress;
        private String id;
        private String desc;
        private String title;

        private ErrorMsg errorMsg = new ErrorMsg();

        public VipCodeType(JSONObject jsonObject) {
            isvip = jsonObject.optString("isvip");
            needaddress = jsonObject.optString("needaddress");

            JSONObject object = jsonObject.optJSONObject("error");
            if (object != null) {
                errorMsg.setNo(object.optInt("code", 0));
                errorMsg.setDesc(object.optString("msg", ""));
            }
        }

        public String getIsvip() {
            return isvip;
        }

        public void setIsvip(String isvip) {
            this.isvip = isvip;
        }

        public String getNeedaddress() {
            return needaddress;
        }

        public void setNeedaddress(String needaddress) {
            this.needaddress = needaddress;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public ErrorMsg getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(ErrorMsg errorMsg) {
            this.errorMsg = errorMsg;
        }
    }
}
