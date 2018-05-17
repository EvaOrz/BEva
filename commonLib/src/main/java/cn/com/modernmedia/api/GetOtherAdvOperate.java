package cn.com.modernmedia.api;

import android.content.Context;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.OtherAdvRequestEntry;
import cn.com.modernmedia.model.OtherAdvResultEntry;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.unit.SlateDataHelper;

/**
 * 获取第三方广告
 * Created by administrator on 2018/5/11.
 */

public class GetOtherAdvOperate extends BaseOperate{
    private OtherAdvResultEntry otherAdvResultEntry;
    private ArrayList<NameValuePair> nameValuePairs; // post参数

    public GetOtherAdvOperate() {
        otherAdvResultEntry = new OtherAdvResultEntry();
        // post 参数设置
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject postObject = new JSONObject();

        //gson
        Gson gson = new Gson();
        OtherAdvRequestEntry requestEntry = new OtherAdvRequestEntry();
        requestEntry.setVersion("");
        requestEntry.setSrcType(1);
        requestEntry.setReqType(1);
        requestEntry.setTimeStamp((int)System.currentTimeMillis());
        requestEntry.setAppid("1856");
        requestEntry.setAppVersion("1.0");

        try {



            ///////
            addPostParams(postObject, "appid", ConstData.getAppId() + "");
//            addPostParams(postObject, "uid", SlateDataHelper.getUid(context));
//            addPostParams(postObject, "usertoken", SlateDataHelper.getToken(context));
            addPostParams(postObject, "marketkey", CommonApplication.CHANNEL);
//            addPostParams(postObject, "sceneid", sceneid);
            params.add(new BasicNameValuePair("data", postObject.toString()));
            setPostParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OtherAdvResultEntry getOtherAdvList(){
        return otherAdvResultEntry;
    }

    @Override
    protected String getUrl() {
        return UrlMaker.getOtherAdvUrl();
    }

    @Override
    protected ArrayList<NameValuePair> getPostParams() {
        return nameValuePairs;
    }

    protected void setPostParams(ArrayList<NameValuePair> params) {
        this.nameValuePairs = params;
    }

    @Override
    protected void handler(JSONObject jsonObject) {

    }

    @Override
    protected void saveData(String data) {

    }

    @Override
    protected String getDefaultFileName() {
        return null;
    }
}
