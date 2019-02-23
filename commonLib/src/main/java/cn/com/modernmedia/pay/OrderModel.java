package cn.com.modernmedia.pay;

import org.json.JSONObject;

import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;

/**
 * 恢复订单用的订单model
 * Created by Eva. on 16/5/31.
 */
public class OrderModel extends Entry {

    private int paytype;// 2：支付宝；1：微信
    private String toid;// 第三方平台订单号
    private String oid;// 商户订单号
    private String openid;// 第三方平台购买用户的标识
    //    private String nonce_str;// 随机串
    private String weixinKey;// 微信平台app_key

    public String getWeixinKey() {
        return weixinKey;
    }

    public void setWeixinKey(String weixinKey) {
        this.weixinKey = weixinKey;
    }

    private String status;// 订单状态
    private ErrorMsg error = new ErrorMsg();

    public OrderModel(JSONObject jsonObject) {
        if (jsonObject != null) {
            setStatus(jsonObject.optString("status"));
            setOid(jsonObject.optString("oid"));
            setPaytype(jsonObject.optInt("paytype"));
            setOpenid(jsonObject.optString("openid"));
            setToid(jsonObject.optString("toid"));
            setWeixinKey(jsonObject.optString("weixinkey"));
        }

    }

    public int getPaytype() {
        return paytype;
    }

    public void setPaytype(int paytype) {
        this.paytype = paytype;
    }

    public String getToid() {
        return toid;
    }

    public void setToid(String toid) {
        this.toid = toid;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ErrorMsg getError() {
        return error;
    }

    public void setError(ErrorMsg error) {
        this.error = error;
    }
}
