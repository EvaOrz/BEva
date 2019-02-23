package cn.com.modernmediausermodel.vip;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.modernmedia.api.BaseOperate;
import cn.com.modernmedia.api.UrlMaker;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.model.ErrorMsg;

/**
 * 修改vip 邮寄地址
 *
 * @author: zhufei
 */

public class EditAddressOperate extends BaseOperate {
    private ErrorMsg errorMsg;
    private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();// post参数

    public EditAddressOperate(String uid, String token, String name, String phone, String city, String address, String code) {
        errorMsg = new ErrorMsg();
        JSONObject jsonObject = new JSONObject();
        try {
            addPostParamsCode(jsonObject, "uid", uid);
            addPostParamsCode(jsonObject, "token", token);
            addPostParamsCode(jsonObject, "name", name);
            addPostParamsCode(jsonObject, "phone", phone);
            addPostParamsCode(jsonObject, "city", city);
            addPostParamsCode(jsonObject, "address", address);
            addPostParamsCode(jsonObject, "code", code);
            addPostParamsCode(jsonObject, "appid", String.valueOf(ConstData.getAppId()));
        } catch (JSONException e) {

        } catch (Exception e) {

        }
        params.add(new BasicNameValuePair("data", jsonObject.toString()));
        setPostParams(params);
    }

    @Override
    protected String getUrl() {
        return UrlMaker.editAddress();
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        JSONObject object = jsonObject.optJSONObject("error");
        errorMsg.setNo(object.optInt("no"));
        errorMsg.setDesc(object.optString("desc"));
//        JSONArray array = jsonObject.optJSONArray("useraddress");
//        for (int i = 1; i < array.length(); i++) {
//            JSONObject object = array.optJSONObject(i);
//            errorMsg.setId(object.optInt("id"));
//            errorMsg.setName(object.optString("name"));
//            errorMsg.setProvince(object.optString("province"));
//            errorMsg.setPhone(object.optString("phone"));
//            errorMsg.setCity(object.optString("city"));
//            errorMsg.setAddress(object.optString("address"));
//            errorMsg.setCode(object.optString("code"));
//        }

    }

    protected void setPostParams(ArrayList<NameValuePair> params) {
        this.params = params;
    }

    @Override
    protected void saveData(String data) {
    }

    @Override
    protected ArrayList<NameValuePair> getPostParams() {
        return params;
    }

    @Override
    protected String getDefaultFileName() {
        return null;
    }

    public ErrorMsg getErrorMsg() {
        return errorMsg;
    }
}
