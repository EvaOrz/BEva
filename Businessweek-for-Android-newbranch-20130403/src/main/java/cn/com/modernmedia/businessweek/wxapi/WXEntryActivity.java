package cn.com.modernmedia.businessweek.wxapi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.util.WeixinLoginUtil;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * 微信登陆返回
 * 
 * @author lusiyuan
 * 
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
		if (resp.errCode == BaseResp.ErrCode.ERR_OK && resp.state != null
				&& resp.state.equals("weixin_login")) {
			// 用户同意
			final SendAuth.Resp re = (SendAuth.Resp) resp;
			// int expireDate = re.expireDate;// 过期时间
			// Log.i("onResp", "expireDate" + expireDate);
			int errCode = re.errCode;// 错误码
			Log.i("onResp", "errCode" + errCode);
			String errStr = re.errStr;// 错误描述
			Log.i("onResp", "errStr" + errStr);
			String state = re.state; // 状态
			Log.i("onResp", "state" + state);
			String token = re.code;// 应该是类似code
			Log.i("onResp", "token" + token);
			String resultUrl = re.url;// 应该是url
			Log.i("onResp", "resultUrl" + resultUrl);

			new Thread() {

				@Override
				public void run() {
					try {
						URL mUrl = new URL(
								"https://api.weixin.qq.com/sns/oauth2/access_token?appid="
										+ SlateApplication.mConfig
												.getWeixin_app_id()
										+ "&secret="
										+ SlateApplication.mConfig
												.getWeixin_app_secret()
										+ "&code=" + re.code
										+ "&grant_type=authorization_code");
						HttpURLConnection conn = null;
						conn = (HttpURLConnection) mUrl.openConnection();
						// connect()函数，实际上只是建立了一个与服务器的tcp连接，并没有实际发送http请求。
						// 无论是post还是get，http请求实际上直到HttpURLConnection的getInputStream()这个函数里面才正式发送出去。
						conn.getInputStream();
						if (conn.getResponseCode() == HttpStatus.SC_OK) {
							InputStream is = conn.getInputStream();
							String data = receiveData(is);
							JSONObject json = new JSONObject(data);
							URL getInfo = new URL(
									"https://api.weixin.qq.com/sns/userinfo?access_token="
											+ json.getString("access_token")
											+ "&openid="
											+ json.getString("openid"));
							HttpURLConnection conn1 = null;
							conn1 = (HttpURLConnection) getInfo
									.openConnection();
							conn1.getInputStream();
							if (conn1.getResponseCode() == HttpStatus.SC_OK) {
								InputStream is1 = conn1.getInputStream();
								String data1 = receiveData(is1);
								Log.i("user_info", "&&&&&&" + data1);
								JSONObject info = new JSONObject(data1);
								doAfterWeiXinIsAuthed(info);
							}

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}.start();
		} else
			finish();
	}

	private void doAfterWeiXinIsAuthed(JSONObject json) {
		try {
			User user = SlateDataHelper.getUserLoginInfo(this);
			String weiId = json.getString("openid");

			if (user != null && !TextUtils.isEmpty(user.getWeixinId())
					&& user.getWeixinId().equals(weiId)) { // 已经用微信账号在本应用上登录
				WeixinLoginUtil.getInstance(this).getInfo(user);// false
				Log.i("WXEntryActivity 登陆过",
						"user.getUserName()" + user.getUserName());
				finish();
			} else {
				User mUser = new User();
				// 用户名不为空时，说明以前登录过；反之，则为第一次登录
				mUser.setNickName(json.optString("nickname"));
				mUser.setAvatar(json.optString("headimgurl"));
				mUser.setWeixinId(weiId);
				WeixinLoginUtil.getInstance(this).firstLogin(mUser);
				finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private String receiveData(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] buff = new byte[1024];
			int readed = -1;
			while ((readed = is.read(buff)) != -1) {
				baos.write(buff, 0, readed);
			}
			byte[] result = baos.toByteArray();
			if (result == null)
				return null;
			return new String(result);
		} finally {
			if (is != null)
				is.close();
			if (baos != null)
				baos.close();
		}
	}

	@Override
	public void onReq(BaseReq arg0) {
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:

			SendAuth.Resp re = (SendAuth.Resp) resp;

			// int expireDate = re.expireDate;// 过期时间

			// Log.i("onResp", "expireDate" + expireDate);

			int errCode = re.errCode;// 错误码

			Log.i("onResp", "errCode" + errCode);

			String errStr = re.errStr;// 错误描述

			Log.i("onResp", "errStr" + errStr);

			String state = re.state; // 状态

			Log.i("onResp", "state" + state);

			String token = re.code;// 应该是类似code

			Log.i("onResp", "token" + token);

			String resultUrl = re.url;// 应该是url

			Log.i("onResp", "resultUrl" + resultUrl);

			break;
		}
	}

}