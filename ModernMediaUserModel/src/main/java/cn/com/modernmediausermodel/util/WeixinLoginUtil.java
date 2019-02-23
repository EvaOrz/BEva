//package cn.com.modernmediausermodel.util;
//
//import android.content.Context;
//
//import com.tencent.mm.sdk.modelmsg.SendAuth;
//import com.tencent.mm.sdk.openapi.IWXAPI;
//import com.tencent.mm.sdk.openapi.WXAPIFactory;
//
//import cn.com.modernmedia.common.WeixinShare;
//import cn.com.modernmediaslate.SlateApplication;
//import cn.com.modernmediaslate.model.User;
//import cn.com.modernmediaslate.unit.Tools;
//import cn.com.modernmediausermodel.R;
//
//public class WeixinLoginUtil {
//	// wxf697f48aefe1d229
//	private Context mContext;
//	private static WeixinLoginUtil instance;
//	private WeixinAuthListener mAuthListener;
//
//	// 第三方app与微信通信的openapi接口
//	private IWXAPI api;
//
//	private static User user;
//
//	private WeixinLoginUtil(Context context) {
//		this.mContext = context;
//	}
//
//	public static WeixinLoginUtil getInstance(Context context) {
//		if (instance == null) {
//			instance = new WeixinLoginUtil(context);
//		}
//		return instance;
//	}
//
//	/**
//	 * 授权登陆
//	 */
//	public void loginWithWeixin() {
//		if (api == null) {
//			api = WXAPIFactory.createWXAPI(mContext, WeixinShare.APP_ID, true);
//		}
//		System.out.print(SlateApplication.mConfig.getWeixin_app_id());
//
//		if (!api.isWXAppInstalled()) {
//			Tools.showToast(mContext, R.string.no_weixin);
//			return;
//		}
//
//		api.registerApp(WeixinShare.APP_ID);
//
//		SendAuth.Req req = new SendAuth.Req();
//		// post_timeline
//		req.scope = "snsapi_userinfo";
//		req.state = "weixin_login";
//		System.out.print("***********" + req.toString());
//		api.sendReq(req);
//
//	}
//
//	/**
//	 * 微信已经登录过
//	 *
//	 * @param json
//	 */
//	public void getInfo(User json) {
//		if (mAuthListener != null) {
//			mAuthListener.onCallBack(false, json);
//		}
//	}
//
//	/**
//	 * 微信第一次登录
//	 *
//	 * @param json
//	 */
//	public void firstLogin(User json) {
//		if (mAuthListener != null) {
//			user = json;
//			mAuthListener.onCallBack(true, json);
//		}
//	}
//
//	public User getUser() {
//		return user;
//	}
//
//	/**
//	 * 自定义登录回调接口
//	 *
//	 * @param userModelAuthListener
//	 */
//	public void setLoginListener(WeixinAuthListener userModelAuthListener) {
//		this.mAuthListener = userModelAuthListener;
//	}
//
//	public interface WeixinAuthListener {
//		/**
//		 * 授权后回调方法
//		 *
//		 * @param isSuccess
//		 */
//		public abstract void onCallBack(boolean isSuccess, User user);
//
//	}
//}
