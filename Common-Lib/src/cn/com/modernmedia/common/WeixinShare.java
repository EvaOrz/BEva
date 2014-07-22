/**
 * 
 */
package cn.com.modernmedia.common;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.text.TextUtils;
import cn.com.modernmedia.R;
import cn.com.modernmedia.util.ModernMediaTools;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

/**
 * 名称: ShareToWeixin.java<br>
 * 描述: <br>
 * 最近修改时间:2013-2-27 下午5:46:39<br>
 * 创建时间：2013-2-27 <br>
 * 
 * @author
 */
public class WeixinShare {
	private static final int THUMB_SIZE = 80;

	public static String APP_ID = "";

	private static WeixinShare instance;
	private Context mContext;

	// 第三方app与微信通信的openapi接口
	private IWXAPI api;

	/**
	 * @return the api
	 */
	public IWXAPI getApi() {
		return api;
	}

	/**
	 * 构造方法
	 */
	private WeixinShare(Context context) {
		mContext = context;
		api = WXAPIFactory.createWXAPI(context, APP_ID, true);
		// 将应用注册到手机上
		if (!api.registerApp(APP_ID)) {
			ModernMediaTools
					.showToast(mContext, R.string.weixin_register_error);
		}
	}

	/**
	 * 获取ShareToWeixin单例对象
	 * 
	 * @param context
	 *            上下文
	 * @return ShareToWeixin的单例对象
	 * @变更记录 2013-2-28 下午2:42:26
	 * @author
	 */
	public static WeixinShare getInstance(Context context) {
		if (instance == null) {
			instance = new WeixinShare(context);
		}
		return instance;
	}

	/**
	 * 发送图片和文字到微信
	 * 
	 * @param title
	 *            要发送的标题
	 * @param content
	 *            要发送的文字内容
	 * @param url
	 *            点击显示的页面
	 * @param bmp
	 *            要发送的图片
	 * @param isSharedToFriends
	 *            true表示发送到朋友圈，false表示发送到会话
	 * @变更记录 2013-2-28 下午2:43:17
	 * @author
	 */
	public void shareImageAndTextToWeixin(String title, String content,
			String url, Bitmap bmp, boolean isSharedToFriends) {
		// 检测微信是否安装
		if (!api.isWXAppInstalled()) {
			ModernMediaTools.showToast(mContext, R.string.no_weixin);
			return;
		}

		// 微信4.2以上支持发送到朋友圈
		if (isSharedToFriends && api.getWXAppSupportAPI() < 0x21020001) {
			ModernMediaTools.showToast(mContext, R.string.weixin_version_low);
			return;
		}

		WXWebpageObject webpage = new WXWebpageObject();
		if (!TextUtils.isEmpty(url))
			webpage.webpageUrl = url;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		if (!TextUtils.isEmpty(title))
			msg.title = title;
		if (!TextUtils.isEmpty(content))
			msg.description = content;

		if (bmp != null) {
			Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE,
					THUMB_SIZE, true);
			msg.thumbData = bmpToByteArray(thumbBmp); // 设置缩略图
			int imageSize = msg.thumbData.length / 1024;
			if (imageSize > 32) {
				ModernMediaTools.showToast(mContext, "您分享的图片过大");
				return;
			}
		}

		// 构造一个Req
		SendMessageToWX.Req request = new SendMessageToWX.Req();
		request.transaction = buildTransaction("webpage"); // transaction字段用于唯一标识一个请求
		request.message = msg;
		// 判断是否分享到朋友圈（默认分享到会话）
		if (isSharedToFriends) {
			request.scene = SendMessageToWX.Req.WXSceneTimeline;
		}
		api.sendReq(request);
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	private static byte[] bmpToByteArray(Bitmap bmp) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
