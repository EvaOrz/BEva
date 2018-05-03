package cn.com.modernmediausermodel.vip;

import android.content.Context;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.modernmedia.api.BaseOperate;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.api.UrlMaker;

/**
 * VIP 兑换码兑换接口
 *
 * @author: zhufei
 */
public class GetCodeVipOperate extends BaseOperate {
    private ErrorMsg errorMsg;
    private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();// post参数
    private Context mContext;

    public GetCodeVipOperate(String uid, String token, String sn, Context context) {
        this.errorMsg = new ErrorMsg();
        mContext = context;
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

    @Override
    protected void handler(JSONObject jsonObject) {

        if (jsonObject != null) {
            SlateDataHelper.setCodeTitle(mContext, jsonObject.optString("title"));
            JSONObject object = jsonObject.optJSONObject("error");
            errorMsg.setNo(object.optInt("code", 0));
            errorMsg.setDesc(object.optString("msg", ""));

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

    public ErrorMsg getErrorMsg() {
        return errorMsg;
    }
}
