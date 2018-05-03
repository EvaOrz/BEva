/**
 *
 */
package cn.com.modernmedia.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;

import cn.com.modernmedia.R;
import cn.com.modernmediaslate.unit.Tools;

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
     * 构造方法
     */
    private WeixinShare(Context context) {
        mContext = context;
        api = WXAPIFactory.createWXAPI(context, APP_ID, true);
        // 将应用注册到手机上
        if (!api.registerApp(APP_ID)) {
            Tools.showToast(mContext, R.string.weixin_register_error);
        }
    }

    /**
     * 获取ShareToWeixin单例对象
     *
     * @param context 上下文
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

    /**
     * @return the api
     */
    public IWXAPI getApi() {
        return api;
    }

    /**
     * 发送图片和文字到微信
     *
     * @param title             要发送的标题
     * @param content           要发送的文字内容
     * @param url               点击显示的页面
     * @param bmp               要发送的图片
     * @param isSharedToFriends true表示发送到朋友圈，false表示发送到会话
     * @变更记录 2013-2-28 下午2:43:17
     * @author
     */
    public void shareImageAndTextToWeixin(String title, String content, String url, Bitmap bmp, boolean isSharedToFriends) {
        // 检测微信是否安装
        if (!api.isWXAppInstalled()) {
            Tools.showToast(mContext, R.string.no_weixin);
            return;
        }

        // 微信4.2以上支持发送到朋友圈
        if (isSharedToFriends && api.getWXAppSupportAPI() < 0x21020001) {
            Tools.showToast(mContext, R.string.weixin_version_low);
            return;
        }

        WXWebpageObject webpage = new WXWebpageObject();
        if (!TextUtils.isEmpty(url)) webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        if (!TextUtils.isEmpty(title)) msg.title = title;
        else msg.title = mContext.getString(R.string.app_name);
        if (!TextUtils.isEmpty(content)) msg.description = content;

        if (bmp == null)
            bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        msg.thumbData = bmpToByteArray(thumbBmp);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        // 判断是否分享到朋友圈（默认分享到会话）
        if (isSharedToFriends) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }
        api.sendReq(req);
    }


    /*
     * 3.4 分享文字到朋友圈
     */
    public void shareText(String shareContent, boolean isSharedToFriends) {
        //初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = shareContent;
        //用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = shareContent;
        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        //transaction字段用于唯一标识一个请求
        req.transaction = buildTransaction("textshare");
        req.message = msg;
        //发送的目标场景， 可以选择发送到会话 WXSceneSession 或者朋友圈 WXSceneTimeline。 默认发送到会话。
        if (isSharedToFriends) req.scene = SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
