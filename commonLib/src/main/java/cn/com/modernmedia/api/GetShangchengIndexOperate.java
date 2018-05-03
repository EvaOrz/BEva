package cn.com.modernmedia.api;

import android.content.Context;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.ShangchengIndex;
import cn.com.modernmedia.model.ShangchengIndex.ShangchengIndexItem;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.SlateDataHelper;

/**
 * Created by Eva. on 2017/8/16.
 */

public class GetShangchengIndexOperate extends BaseOperate {
    private ShangchengIndex shangchengIndex = new ShangchengIndex();
    private ArrayList<NameValuePair> nameValuePairs; // post参数

    public GetShangchengIndexOperate(Context context) {
        // post 参数设置
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject postObject = new JSONObject();
        try {
            addPostParams(postObject, "appid", ConstData.getAppId() + "");
            addPostParams(postObject, "uid", SlateDataHelper.getUid(context));
            addPostParams(postObject, "usertoken", SlateDataHelper.getToken(context));
            addPostParams(postObject, "marketkey", CommonApplication.CHANNEL);

            params.add(new BasicNameValuePair("data", postObject.toString()));
            setPostParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Entry getDatas() {
        return shangchengIndex;
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        if (jsonObject == null) return;
        JSONArray array = jsonObject.optJSONArray("scene");
        if (!isNull(array)) {
            List<ShangchengIndexItem> datas = new ArrayList<>();
            int len = array.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = array.optJSONObject(i);
                ShangchengIndexItem item = new ShangchengIndexItem();
                item.setId(object.optString("id"));
                item.setName(object.optString("name"));
                item.setReadLevel(object.optInt("readLevel"));
                item.setCmsTagId(object.optString("cmsTagId"));
                JSONObject icon = object.optJSONObject("icon");
                if (!isNull(icon)) item.setIcon(icon.optString("normal"));
                item.setShowPrice(object.optString("showPrice"));

                JSONArray pics = object.optJSONArray("picture");
                if (!isNull(pics)) {
                    List<String> pictures = new ArrayList<>();
                    for (int j = 0; j < pics.length(); j++) {
                        JSONObject pic = pics.optJSONObject(j);
                        if (!isNull(pic)) {
                            pictures.add(pic.optString("url"));
                        }
                    }
                }
                item.setCmsShowStyle(object.optInt("cmsShowStyle"));
                item.setDescUrl(object.optString("descUrl"));
                datas.add(item);

            }
            shangchengIndex.setDatas(datas);
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
    protected String getUrl() {
        return UrlMaker.getShangchengIndex();
    }

    @Override
    protected ArrayList<NameValuePair> getPostParams() {
        return nameValuePairs;
    }

    protected void setPostParams(ArrayList<NameValuePair> params) {
        this.nameValuePairs = params;
    }

}
