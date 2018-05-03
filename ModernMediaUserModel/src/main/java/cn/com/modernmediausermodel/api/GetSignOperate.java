package cn.com.modernmediausermodel.api;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.modernmedia.api.BaseOperate;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.model.ErrorMsg;

/**
 * 签到接口operate
 *
 * @author: zhufei
 */

public class GetSignOperate extends BaseOperate {
    private ErrorMsg errorMsg;
    private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();// post参数

    public GetSignOperate(String uid, String token) {
        this.errorMsg = new ErrorMsg();

        JSONObject jsonObject = new JSONObject();
        try {
            addPostParamsCode(jsonObject, "uid", uid);
            addPostParamsCode(jsonObject, "token", token);
            addPostParamsCode(jsonObject, "appid", String.valueOf(ConstData.getAppId()));
        } catch (JSONException e) {

        } catch (Exception e) {

        }
        params.add(new BasicNameValuePair("data", jsonObject.toString()));
        setPostParams(params);
    }

    @Override
    protected String getUrl() {
        return UrlMaker.getSign();
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        if (jsonObject != null) {
            errorMsg.setNo(jsonObject.optInt("code", 0));
            errorMsg.setDesc(jsonObject.optString("msg", ""));
        }
    }

    @Override
    protected void saveData(String data) {

    }

    @Override
    protected String getDefaultFileName() {
        return null;
    }

    protected void setPostParams(ArrayList<NameValuePair> params) {
        this.params = params;
    }

    public ErrorMsg getErrorMsg() {
        return errorMsg;
    }
}
