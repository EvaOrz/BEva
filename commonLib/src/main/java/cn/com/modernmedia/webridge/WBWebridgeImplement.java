package cn.com.modernmedia.webridge;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.UploadPicsActivity;
import cn.com.modernmedia.common.SharePopWindow;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.Picture;
import cn.com.modernmedia.model.VipGoodList;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.webridge.WBWebridge.AsynExecuteCommandListener;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;

/**
 * 根据js发送的command注册的方法
 *
 * @author user
 */
public class WBWebridgeImplement implements WBWebridgeListener {

    private Context mContext;

    public WBWebridgeImplement(Context c) {
        mContext = c;
    }

    // ======================js调用的native方法======================

    /**
     * 分享 (异步) {"command":"share","sequence":2,"params":{"content":
     * "展望2016：一年大事一览:彭博新闻记者将继续报道2016年的重大事件，让我们按时间顺序一起展望2016热点事件。","thumb":
     * "http:\/\/s.qiniu.bb.bbwc.cn\/issue_0\/category\/2016\/0104\/5689cda80b488_90x90.jpg","link":"http:\/\/read.bbwc.cn\/dufabs.html
     * " } }
     * <p>
     * shareChannel: ShareTypeWeibo,        ShareTypeWeixin,        ShareTypeWeixinFriendCircle
     */
    public void share(JSONObject json, AsynExecuteCommandListener listener) {
        if (listener != null) {
            try {
                ArticleItem item = new ArticleItem();
                List<Picture> thumbs = new ArrayList<Picture>();

                Picture pic = new Picture();
                pic.setUrl(json.optString("thumb"));
                thumbs.add(pic);

                item.setThumbList(thumbs);
                item.setTitle(json.optString("content"));
                item.setWeburl(json.optString("link"));
                item.setDesc(json.optString("desc"));

                String shareChannel = json.optString("shareChannel");
                if (shareChannel.endsWith("ShareTypeWeibo")) {
                    new SharePopWindow(mContext, item);

                } else if (shareChannel.endsWith("ShareTypeWeixin")) {
                    new SharePopWindow(mContext, item);
                } else if (shareChannel.endsWith("ShareTypeWeixinFriendCircle")) {
                    new SharePopWindow(mContext, item);
                } else {
                    new SharePopWindow(mContext, item);

                }
                json.put("shareResult", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listener.onCallBack(json.toString());
        }
    }


    /**
     * js查询本地付费权限
     *
     * @param json {"level":0}
     */
    public void isPaid(JSONObject json, AsynExecuteCommandListener listener) {
        if (listener != null) {
            JSONObject result = new JSONObject();
            try {
                if (SlateDataHelper.getLevelByType(mContext, 1)) result.put("isPaid", true);

                else result.put("isPaid", false);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            listener.onCallBack(result.toString());
        }
    }

    public void isPaidForLevel(JSONObject json, AsynExecuteCommandListener listener) {
        if (listener != null) {
            JSONObject result = new JSONObject();
            try {
                int level = json.optInt("level");
                if (SlateDataHelper.getLevelByType(mContext, level)) {
                    result.put("isPaid", true);
                    result.put("expire", SlateDataHelper.getEndTimeByType(mContext, level));

                } else result.put("isPaid", false);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            listener.onCallBack(result.toString());
        }
    }


    //    public void queryAppInfo( AsynExecuteCommandListener listener) {
    //        if (listener != null) {
    //
    //            JSONObject result = new JSONObject();
    //            try {
    //                PackageManager packageManager = mContext.getPackageManager();
    //                PackageInfo info = packageManager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
    //                result.put("appID", ConstData.getInitialAppId());
    //                result.put("deviceUUID", CommonApplication.getMyUUID());
    //                result.put("appMode", ConstData.IS_DEBUG);
    //                result.put("appApiVersion", ConstData.API_VERSION);
    //                result.put("appDisplayName", info.applicationInfo.loadLabel(packageManager));
    //                result.put("appBundleName", "");
    //                result.put("appVersion", info.versionName);
    //                result.put("appBuild", info.versionCode);
    //
    //
    //            } catch (JSONException e) {
    //                e.printStackTrace();
    //            } catch (PackageManager.NameNotFoundException e) {
    //                e.printStackTrace();
    //            }
    //            listener.onCallBack(result.toString());
    //        }
    //    }

    public void domReady(JSONObject json, AsynExecuteCommandListener listener) {

    }

    /**
     * js 查询登录状态
     *
     * @param listener
     */
    public void queryLoginStatus(String s, AsynExecuteCommandListener listener) {

        if (listener != null) {

            JSONObject result = new JSONObject();
            try {
                User u = SlateDataHelper.getUserLoginInfo(mContext);
                if (u == null) result.put("loginStatus", false);
                else {
                    result.put("loginStatus", true);

                    JSONObject uJson = new JSONObject();
                    uJson.put("userId", u.getUid());
                    uJson.put("userToken", u.getToken());
                    result.put("user", uJson);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listener.onCallBack(result.toString());
        }


    }

    /**
     * js 查询登录状态
     *
     * @param listener
     */
    public void queryLoginStatus(JSONObject json, AsynExecuteCommandListener listener) {

        if (listener != null) {

            JSONObject result = new JSONObject();
            try {
                User u = SlateDataHelper.getUserLoginInfo(mContext);
                if (u == null) result.put("loginStatus", false);
                else {
                    result.put("loginStatus", true);

                    JSONObject uJson = new JSONObject();
                    uJson.put("userId", u.getUid());
                    uJson.put("userToken", u.getToken());
                    result.put("user", uJson);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listener.onCallBack(result.toString());
        }


    }

    /**
     * js 调用原生登录
     */
    public void login(String s, AsynExecuteCommandListener listener) {

        if (listener != null) CommonApplication.asynExecuteCommandListener = listener;

        Intent i = new Intent();
        i.setAction("cn.com.modernmediausermodel.LoginActivity_nomal");
        mContext.sendBroadcast(i);

    }
    /**
     * js 调用原生登录
     */
    public void login(JSONObject s, AsynExecuteCommandListener listener) {

        if (listener != null) CommonApplication.asynExecuteCommandListener = listener;

        Intent i = new Intent();
        i.setAction("cn.com.modernmediausermodel.LoginActivity_nomal");
        mContext.sendBroadcast(i);

    }


    /**
     * js 获取app info
     *
     * @param listener
     */
    public void queryAppInfo(String s, AsynExecuteCommandListener listener) {
        if (listener != null) {

            JSONObject result = new JSONObject();
            try {
                PackageManager packageManager = mContext.getPackageManager();
                PackageInfo info = packageManager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
                result.put("appID", ConstData.getInitialAppId());
                result.put("deviceUUID", cn.com.modernmediaslate.unit.Tools.getMyUUID(mContext));
                result.put("appMode", ConstData.IS_DEBUG);
                result.put("appApiVersion", ConstData.API_VERSION);
                result.put("appDisplayName", info.applicationInfo.loadLabel(packageManager));
                result.put("appBundleName", "");
                result.put("appVersion", info.versionName);
                result.put("appBuild", info.versionCode);


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            listener.onCallBack(result.toString());
        }
    }

    public void uploadPics(JSONObject json, AsynExecuteCommandListener listener) {
        if (listener != null) {
            JSONObject result = new JSONObject();
            mContext.startActivity(new Intent(mContext, UploadPicsActivity.class));
            listener.onCallBack(result.toString());
        }
    }

    /**
     * js调用原生直播（支付）
     */
    public void getLiveInfos(JSONObject json, AsynExecuteCommandListener listener) {

        try {
            JSONObject params = json.optJSONObject("params");


            json.put("shareResult", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listener.onCallBack(json.toString());
    }

    /**
     * js调用vip支付
     * "params": {
     * "goodId": "event_10000",
     * "goodName": "金融创新与人民币国际化论坛",
     * "price": 100,
     * "needTel": "0"
     * },
     * <p>
     * 返回值
     * {
     * "result":{
     * “success": true/false,
     * "data":{
     * "uid":uid,              // uid
     * "appid":appid,          // appid
     * "pid":pid,              // pid
     * "tradeNum":tradeNum,    // 订单号
     * "tradeName":tradeName,  // 产品名称
     * "tradeType":tradeType,  // 支付方式(1微信,2阿里,5applePay)
     * "tradePrice":tradePrice // 价格
     * }
     * },
     * "error": error
     * }
     */
    public void slatePay(JSONObject json, AsynExecuteCommandListener listener) {
        if (json == null) return;
        android.util.Log.e("slate pay", json.toString());
        if (json == null) return;
        String pid = json.optString("goodId");
        String needTel = json.optString("needTel");
        if (TextUtils.isEmpty(pid)) return;

        VipGoodList.VipGood p = new VipGoodList.VipGood();
        p.setGoodId(pid);
        p.setPirce(json.optInt("price"));
        p.setGoodName(json.optString("goodName"));

        List<VipGoodList.VipPayType> l = new ArrayList<>();
        VipGoodList.VipPayType pp = new VipGoodList.VipPayType();
        pp.setPayTypeId(json.optString("payType"));
        l.add(pp);
        p.setPayTypeLists(l);

        String broadcastIntent = "cn.com.modernmediausermodel.VipProductPayActivity_nomal";
        Intent intent = new Intent(broadcastIntent);
        Bundle b = new Bundle();
        b.putSerializable("product", p);
        b.putString("needTel", needTel);
        intent.putExtra("html_pay", b);

        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        mContext.sendBroadcast(intent);

        CommonApplication.asynExecuteCommandListener = listener;

    }

    /**
     * js调用渠道号
     */
    public void getChannel(JSONObject json, AsynExecuteCommandListener listener) {

        try {
            JSONObject result = new JSONObject();


            result.put("businessweek_channel", CommonApplication.CHANNEL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listener.onCallBack(json.toString());
    }


}
