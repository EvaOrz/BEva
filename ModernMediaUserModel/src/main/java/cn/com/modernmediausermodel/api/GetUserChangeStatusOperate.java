package cn.com.modernmediausermodel.api;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediausermodel.model.Order;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 获取用户兑换按钮状态
 *
 * @author: zhufei
 */
public class GetUserChangeStatusOperate extends SlateBaseOperate {
    private ArrayList<NameValuePair> nameValuePairs; // post参数
    private Order order;

    protected GetUserChangeStatusOperate(String uid, String token) {
        order = new Order();
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("appid", UserConstData.getInitialAppId() + ""));
        params.add(new BasicNameValuePair("uid", uid));
        params.add(new BasicNameValuePair("token", token));
        setPostParams(params);
    }

    @Override
    protected String getUrl() {
        return UrlMaker.getChangeStatus();
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        if (jsonObject == null)
            return;
        JSONArray array = jsonObject.optJSONArray("orders");
        if (!isNull(array)) {
            int len = array.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = array.optJSONObject(i);
                if (!isNull(object)) {
                    Order.OrderItem item = new Order.OrderItem();
                    item.setGoodsid(object.optInt("goodsid", 0));
                    order.getList().add(item);
                }
            }
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

    protected Order getOrder() {
        return order;
    }
}
