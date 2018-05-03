package cn.com.modernmediausermodel.api;

import android.content.Context;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.BaseOperate;
import cn.com.modernmedia.api.PushDeviceInfoOperate;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.unit.MD5;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;

/**
 * fm播放次数+1 接口
 * Created by Eva. on 2017/6/2.
 */

public class FMAddOperate extends BaseOperate {
    private ErrorMsg errorMsg;
    private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();// post参数


    public FMAddOperate(Context context, String articleid, String resid, int actiontype, int actionpostion) {
        this.errorMsg = new ErrorMsg();

        JSONObject jsonObject = new JSONObject();
        try {
            addPostParamsCode(jsonObject, "uid", SlateDataHelper.getUid(context));//3
            addPostParamsCode(jsonObject, "token", SlateDataHelper.getToken(context));
            addPostParamsCode(jsonObject, "appid", String.valueOf(ConstData.getAppId()));//1
            addPostParamsCode(jsonObject, "articleid", articleid);
            addPostParamsCode(jsonObject, "resid", resid);
            addPostParamsCode(jsonObject, "actiontype", actiontype + "");// 1：开始播放；2：暂停后播放
            addPostParamsCode(jsonObject, "actionpostion", actionpostion + "");//1：FM列表；2：全局播放器；3：文章
            addPostParamsCode(jsonObject, "appversion", Tools.getAppVersionName(context));//5
            addPostParamsCode(jsonObject, "screenwidth", SlateApplication.width + "");//6
            addPostParamsCode(jsonObject, "screenheight", SlateApplication.height + "");
            addPostParamsCode(jsonObject, "devicetype", android.os.Build.MODEL);
            addPostParamsCode(jsonObject, "marketkey", CommonApplication.CHANNEL);
            addPostParamsCode(jsonObject, "scale", 1 + "");
            addPostParamsCode(jsonObject, "osversion", android.os.Build.VERSION.RELEASE);
            addPostParamsCode(jsonObject, "osbuild",  android.os.Build.VERSION.SDK );
            addPostParamsCode(jsonObject, "macaddress", Tools.getNetMacAdress(context));
            addPostParamsCode(jsonObject, "deviceuuid", Tools.getDeviceId(context));//2
            addPostParamsCode(jsonObject, "devicetoken", SlateDataHelper.getPushToken(context));//4

            addPostParamsCode(jsonObject, "env", ConstData.IS_DEBUG == 1 ? "inhouse" : "product");


            String encryk = PushDeviceInfoOperate.getRandomString(6);
            addPostParamsCode(jsonObject, "encryk", encryk);

            String md5 = ConstData.getInitialAppId() + "|" + Tools.getDeviceId(context) + "|" +
                    SlateDataHelper.getUid(context) + "|" + SlateDataHelper.getPushToken(context) + "|" + Tools.getAppVersionName(context) + "|" + SlateApplication.width + "|" + SlateApplication.height + "|" + android.os.Build.MODEL + "|" + Tools.getNetMacAdress(context) + "|" + android.os.Build.VERSION.RELEASE + "|" + android.os.Build.VERSION.SDK + "|" + CommonApplication.CHANNEL + "|" + articleid + "|" + resid + "|" + actiontype + "|" + actionpostion + "|" + encryk + "|" + PushDeviceInfoOperate.SECRET;
            Log.e("fm add", md5);
            addPostParamsCode(jsonObject, "encryv", MD5.MD5Encode(md5));

        } catch (JSONException e) {

        } catch (Exception e) {

        }
        params.add(new BasicNameValuePair("data", jsonObject.toString()));
        setPostParams(params);
    }

    @Override
    protected String getUrl() {
        return UrlMaker.addFmPlaytime();
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        if (jsonObject != null) {
            Log.e("FMAddOperate", jsonObject.toString());
            errorMsg.setNo(jsonObject.optInt("code", 0));
            errorMsg.setDesc(jsonObject.optString("msg", ""));
        }
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

    protected void setPostParams(ArrayList<NameValuePair> params) {
        this.params = params;
    }

    public ErrorMsg getErrorMsg() {
        return errorMsg;
    }
}
