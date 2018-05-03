package cn.com.modernmediausermodel.api;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 用户订购接口operate
 *
 * @author JianCong
 */
public class AddGoodsOrderOperate extends SlateBaseOperate {
    private ArrayList<NameValuePair> nameValuePairs; // post参数
    private ErrorMsg error;

    protected AddGoodsOrderOperate(String uid, String token, String goodsId) {
        error = new ErrorMsg();
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("appid", UserConstData
                .getInitialAppId() + ""));
        params.add(new BasicNameValuePair("uid", uid));
        params.add(new BasicNameValuePair("token", token));
        // 商品id
        params.add(new BasicNameValuePair("goodsid", goodsId));
        setPostParams(params);
    }

    // 实物兑换用
    protected AddGoodsOrderOperate(String uid, String token, String goodsId,
                                   String address, String name, String phone) {
        error = new ErrorMsg();
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("appid", UserConstData.getInitialAppId() + ""));
        params.add(new BasicNameValuePair("uid", uid));
        params.add(new BasicNameValuePair("token", token));
        // 商品id
        params.add(new BasicNameValuePair("goodsid", goodsId));
        params.add(new BasicNameValuePair("address", address));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("phone", phone));
        setPostParams(params);
    }

    @Override
    protected String getUrl() {
        return UrlMaker.getUserOrder();
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        if (jsonObject != null) {
            // 添加成功返回值 为status=>1,msg=>;失败返回 status=>0,msg=>消息
            error.setNo(jsonObject.optInt("status", 0));
            error.setDesc(jsonObject.optString("msg", ""));
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

    protected ErrorMsg getError() {
        return error;
    }
}
