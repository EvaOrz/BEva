package cn.com.modernmedia.api;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.unit.MD5;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;


/**
 * 推送：提交设备信息接口
 * <p/>
 * Created by Eva. on 16/7/13.
 */
public class PushDeviceInfoOperate extends BaseOperate {

    private ArrayList<NameValuePair> nameValuePairs; // post参数

    public static String SECRET = "dream20160711_";
    private ErrorMsg errorMsg;

    /**
     * devicetoken 第三方平台token
     * devicetoken 设备token(小米或者极光推送的注册id)
     * appversion
     * screenwidth
     * screenheight
     * devicetype
     * pushtype MI||JPUSH
     * devicename 设备名称
     * macadress
     * osversion    android系统版本号
     * osbuild      android系统构建号
     * marketkey    APP下载市场标识
     * encryk       用于网络信息干扰，无实际意义。随机生成一个字符串，长度暂定6位。
     * encryv       使用md5加密后的值。加密项：appid,deviceid,userid及1～9，互相用”|”连接后的字符串，实用的加密KEY双方协商确定一个值。
     */
    public PushDeviceInfoOperate(Context context, String devicetoken, String type) {
        // post 参数设置
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject postObject = new JSONObject();
        try {
            addPostParams(postObject, "pushtype", type);
            addPostParams(postObject, "devicetoken", devicetoken);
            addPostParams(postObject, "appversion", Tools.getAppVersionName(context));
            addPostParams(postObject, "screenwidth", SlateApplication.width + "");
            addPostParams(postObject, "screenheight", SlateApplication.height + "");
            addPostParams(postObject, "devicetype", android.os.Build.MODEL);
            addPostParams(postObject, "devicename", Build.USER);
            addPostParams(postObject, "macadress", Tools.getNetMacAdress(context));
            addPostParams(postObject, "osversion", android.os.Build.VERSION.RELEASE);
            addPostParams(postObject, "osbuild", android.os.Build.VERSION.SDK);
            addPostParams(postObject, "marketkey", CommonApplication.CHANNEL);
            String encryk = getRandomString(6);
            addPostParams(postObject, "encryk", encryk);

            String md5 = ConstData.getInitialAppId() + "|" + Tools.getDeviceId(context) + "|" +
                    SlateDataHelper.getUid(context) + "|" + devicetoken + "|" + Tools.getAppVersionName(context) + "|" +
                    SlateApplication.width + "|" + SlateApplication.height + "|" + android.os.Build.MODEL + "|" + Tools.getNetMacAdress(context) + "|" + android.os.Build.VERSION.RELEASE + "|" + android.os.Build.VERSION.SDK + "|" + CommonApplication.CHANNEL + "|" + encryk + "|" + SECRET;
            addPostParams(postObject, "encryv", MD5.MD5Encode(md5));
            Log.e("********************", md5);
            Log.e("********************", postObject.toString());

            params.add(new BasicNameValuePair("data", postObject.toString()));
            setPostParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取随机字符串
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    @Override
    protected String getUrl() {
        return UrlMaker.getDeviceInfoForPush();
    }


    @Override
    protected void handler(JSONObject jsonObject) {
        errorMsg = new ErrorMsg();
        if (jsonObject != null) {
            Log.e("PushDeviceInfoOperate", jsonObject.toString());
        }

    }

    public ErrorMsg getError() {
        return errorMsg;
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
}
