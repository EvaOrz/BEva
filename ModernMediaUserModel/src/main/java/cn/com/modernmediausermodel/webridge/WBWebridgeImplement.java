package cn.com.modernmediausermodel.webridge;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediausermodel.BandActivity;
import cn.com.modernmediausermodel.BandDetailActivity;
import cn.com.modernmediausermodel.LoginActivity;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.webridge.WBWebridge.AsynExecuteCommandListener;

/**
 * 根据js发送的command注册的方法
 * 
 * @author user
 * 
 */
public class WBWebridgeImplement implements WBWebridgeListener {

	private Context mContext;
	private User user;
	private UserOperateController mController;
	private boolean ifGetStatus = false;// 是否取到绑定状态

	public WBWebridgeImplement(Context c) {
		mContext = c;
		// user = SlateDataHelper.getUserLoginInfo(c);// 获取系统user对象
		mController = UserOperateController.getInstance(c);
		// getBandStatus();
	}

	// ======================js调用的native方法======================
	/**
	 * 查询绑定状态 (异步方法) result.loginStatus == true
	 * 
	 * @return
	 */
	public void queryLoginStatus(String s, AsynExecuteCommandListener listener) {
		if (listener != null) {
			JSONObject json = new JSONObject();
			try {
				if (user == null) {
					json.put("loginStatus", false);
				} else {
					json.put("loginStatus", true);
					JSONObject j = new JSONObject();
					j.put("userName", user.getUserName());
					json.put("user", j);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			listener.onCallBack(json.toString());
		}
	}

	/**
	 * 查询绑定状态 (同步)
	 * 
	 * @return
	 */
	public String queryLoginStatus(String s) {
		JSONObject json = new JSONObject();
		try {
			json.put("loginStatus", "true");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	/**
	 * 分享 (异步)
	 */
	public void share(JSONObject json, AsynExecuteCommandListener listener) {
		if (listener != null) {
			try {
				json.put("shareResult", true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			listener.onCallBack(json.toString());
		}

	}

	/**
	 * 登陆 (异步)
	 */
	public void login(String s, AsynExecuteCommandListener listener) {
		if (listener != null) {
			JSONObject json = new JSONObject();
			try {
				if (user == null) {
					Intent i = new Intent(mContext, LoginActivity.class);
					mContext.startActivity(i);
				} else {
					json.put("loginResult", true);
					JSONObject j = new JSONObject();
					j.put("userName", user.getUserName());
					json.put("user", j);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			listener.onCallBack(json.toString());
		}
	}

	/**
	 * 绑定
	 */
	public void bind(JSONObject paramObject, AsynExecuteCommandListener listener) {
		if (listener != null) {
			try {
				String type = "";
				if (paramObject != null && !paramObject.isNull("type")) {
					type = paramObject.getString("type");
				}
				Intent i = new Intent(mContext, BandDetailActivity.class);
				if (type.equals("mobile")) {// 绑定手机
					if (ifGetStatus && user.isBandPhone())
						paramObject.put("bindResult", true);
					else
						i.putExtra("band_type", BandActivity.BAND_PHONE);
				} else if (type.equals("email")) {
					if (ifGetStatus && user.isBandEmail())
						paramObject.put("bindResult", true);
					else
						i.putExtra("band_type", BandActivity.BAND_EMAIL);
				} else if (type.equals("weibo")) {
					if (ifGetStatus && user.isBandWeibo())
						paramObject.put("bindResult", true);
					else
						i.putExtra("band_type", BandActivity.BAND_WEIBO);
				} else if (type.equals("weixin")) {
					if (ifGetStatus && user.isBandWeixin())
						paramObject.put("bindResult", true);
					else
						i.putExtra("band_type", BandActivity.BAND_WEIXIN);
				} else if (type.equals("qq")) {
					if (ifGetStatus && user.isBandQQ())
						paramObject.put("bindResult", true);
					else
						i.putExtra("band_type", BandActivity.BAND_QQ);
				}
				listener.onCallBack(paramObject.toString());
				mContext.startActivity(i);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取绑定状态
	 */
	private void getBandStatus() {
		mController.getBandStatus(user.getUid(), user.getToken(),
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						User u = (User) entry;
						if (u != null) {
							user.setBandPhone(u.isBandPhone());
							user.setBandEmail(u.isBandEmail());
							user.setBandQQ(u.isBandQQ());
							user.setBandWeibo(u.isBandWeibo());
							user.setBandWeixin(u.isBandWeixin());
						}
						ifGetStatus = true;
					}
				});
	}
}
