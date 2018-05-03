package cn.com.modernmedia.api;

import android.content.Context;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.ShangchengIndex.ShangchengIndexItem;
import cn.com.modernmedia.model.VipGoodList;
import cn.com.modernmedia.pay.newlogic.PayHttpsOperate;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.SlateDataHelper;

/**
 * 直接跳转 商城简介和列表页面，需要获取初始数据
 * Created by Eva. on 2017/12/22.
 */

public class GetShangchengSplashDataOperate extends BaseOperate {
    private ShangchengIndexItem shangchengIndexItem = new ShangchengIndexItem();
    private ArrayList<NameValuePair> nameValuePairs; // post参数
    private Context context;

    public GetShangchengSplashDataOperate(Context context, String sceneid) {
        this.context =  context;
        // post 参数设置
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject postObject = new JSONObject();
        try {
            addPostParams(postObject, "appid", ConstData.getAppId() + "");
            addPostParams(postObject, "uid", SlateDataHelper.getUid(context));
            addPostParams(postObject, "usertoken", SlateDataHelper.getToken(context));
            addPostParams(postObject, "marketkey", CommonApplication.CHANNEL);
            addPostParams(postObject, "sceneid", sceneid);
            params.add(new BasicNameValuePair("data", postObject.toString()));
            setPostParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Entry getDatas() {
        return shangchengIndexItem;
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        Log.e("ssss",jsonObject.toString());
        if (jsonObject == null) return;
        shangchengIndexItem.setId(jsonObject.optString("id"));
        shangchengIndexItem.setName(jsonObject.optString("name"));
        shangchengIndexItem.setReadLevel(jsonObject.optInt("readLevel"));
        shangchengIndexItem.setCmsTagId(jsonObject.optString("cmsTagId"));
        JSONObject icon = jsonObject.optJSONObject("icon");
        if (!isNull(icon)) shangchengIndexItem.setIcon(icon.optString("normal"));
        shangchengIndexItem.setShowPrice(jsonObject.optString("showPrice"));

        JSONArray pics = jsonObject.optJSONArray("picture");
        if (!isNull(pics)) {
            List<String> pictures = new ArrayList<>();
            for (int j = 0; j < pics.length(); j++) {
                JSONObject pic = pics.optJSONObject(j);
                if (!isNull(pic)) {
                    pictures.add(pic.optString("url"));
                }
            }
        }
        shangchengIndexItem.setCmsShowStyle(jsonObject.optInt("cmsShowStyle"));
        shangchengIndexItem.setDescUrl(jsonObject.optString("descUrl"));

        List<VipGoodList.VipGood> goods = new ArrayList<>();
        JSONArray array = jsonObject.optJSONArray("good");
        if (!isNull(array)) {
            int len = array.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = array.optJSONObject(i);
                if (!isNull(object)) {
                    goods = PayHttpsOperate.getInstance(context).parseJson(array, goods);
                }
            }
        }
        shangchengIndexItem.setGoods(goods);

    }

    @Override
    protected void saveData(String data) {

    }

    @Override
    protected String getDefaultFileName() {
        return null;
    }

    @Override
    protected String getUrl() {
        return UrlMaker.getShangchengList();
    }

    @Override
    protected ArrayList<NameValuePair> getPostParams() {
        return nameValuePairs;
    }

    protected void setPostParams(ArrayList<NameValuePair> params) {
        this.nameValuePairs = params;
    }

}
