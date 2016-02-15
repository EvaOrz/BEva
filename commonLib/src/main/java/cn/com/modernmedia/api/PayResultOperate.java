package cn.com.modernmedia.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.unit.SlateDataHelper;

/**
 * 支付成功后通知服务器端支付结果
 * 
 * @author lusiyuan
 *
 */
public class PayResultOperate extends BaseOperate {
	private ErrorMsg error;
	private String data;
	private Context context;
	private String aliOrWeixin;
	private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

	/**
	 * 支付宝支付构造方法
	 * 
	 * @param context
	 * @param uid
	 * @param token
	 * @param data
	 */
	public PayResultOperate(Context context, String aliOrWeixin, String data) {
		this.data = data;
		this.context = context;
		this.aliOrWeixin = aliOrWeixin;
		// post 参数设置
		JSONObject object = new JSONObject();
		try {
			object.put("uid", SlateDataHelper.getUid(context));
			object.put("usertoken", SlateDataHelper.getToken(context));
			object.put("data", data);
			params.add(new BasicNameValuePair("data", object.toString()));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 微信支付构造方法 array("openid"=>"UpF8uMEb4qRXf22hE3X68TekukE","product_id"=>
	 * "bbwcsub_oneyearsub","clientversion"=>"2.3.8","app_id"=>"1");
	 */
	// public PayResultOperate(Context context, String aliOrWeixin,
	// String jsonString) {
	// this.context = context;
	// this.aliOrWeixin = aliOrWeixin;
	// this.data = jsonString;
	// // // post 参数设置
	// params.add(new BasicNameValuePair("data", jsonString));
	// }

	@Override
	protected ArrayList<NameValuePair> getPostParams() {
		return params;
	}

	@Override
	protected String getUrl() {
		return UrlMaker.notifyServerPayResult(aliOrWeixin);
	}

	public ErrorMsg getError() {
		return error;
	}

	// {"error_no":0,"error_desc":"数据库中已经有这条数据，不在插入"}
	@Override
	protected void handler(JSONObject jsonObject) {
		error = new ErrorMsg();
		if (jsonObject != null) {// 插入成功
			error.setNo(jsonObject.optInt("error_no"));
			error.setDesc(jsonObject.optString("error_desc"));
			SlateDataHelper.setOrder(context, aliOrWeixin, null);
		}
		if (jsonObject == null || error.getNo() != 0) {// 插入未提交订单
			SlateDataHelper.setOrder(context, aliOrWeixin, data);
		}

	}

	@Override
	protected void saveData(String data) {
	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}

}
