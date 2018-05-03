package cn.com.modernmedia.pay.newlogic;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.UrlMaker;
import cn.com.modernmedia.model.ShangchengIndex.ShangchengIndexItem;
import cn.com.modernmedia.model.VipGoodList;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.CustomMultipartEntity;
import cn.com.modernmediaslate.unit.DESCoder;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.SlatePrintHelper;
import cn.com.modernmediaslate.unit.TimeCollectUtil;
import cn.com.modernmediaslate.unit.Tools;

/**
 * Created by Eva. on 16/6/24.
 */
public class PayHttpsOperate {

    public static String SUCCESS = "SUCCESS";
    public static String ERROR = "ERROR";
    public static String CANCEL = "CANCEL";
    public static String PAYING = "PAYING";
    /**
     * DB存储订单的key
     */
    public static String NEW_WEIXIN_KEY = "NEW_WEIXIN";
    public static String NEW_ALI_KEY = "NEW_ALI";
    /**
     * 连接超时时间
     **/
    public static final int CONNECT_TIMEOUT = 10 * 1000;
    /**
     * 读取数据超时时间
     **/
    public static final int READ_TIMEOUT = 10 * 1000;
    /**
     * 读取数据buffer大小
     **/
    public static final int BUFFERSIZE = 1024;


    private static PayHttpsOperate instance;
    private ArrayList<NameValuePair> postParas;

    private static Context mContext;

    private PayHttpsOperate(Context context) {
        mContext = context;
    }

    public static synchronized PayHttpsOperate getInstance(Context context) {
        mContext = context;
        if (instance == null) instance = new PayHttpsOperate(context);
        return instance;
    }

    /**
     * 获取用户支付权限
     */
    public static void getUserPermission() {
        JSONObject object = new JSONObject();
        try {
            object.put("usertoken", SlateDataHelper.getToken(mContext));
            object.put("appid", ConstData.getInitialAppId());
            object.put("marketkey", CommonApplication.CHANNEL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data", object.toString()));
        instance.setPostParams(params);
        instance.requestHttpAsycle(true, UrlMaker.newGetUserPermission(), new FetchDataListener() {
            @Override
            public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                if (isSuccess) {
                    saveLevel(data);
                }
            }
        });
    }

    /**
     * 支付成功 ||微信回调出错
     *
     * @param data
     */
    public static int saveLevel(String data) {
        String token = SlateDataHelper.getToken(mContext);
        String key = token.substring(token.length() - 8, token.length());// 解析的key
        String json = DESCoder.decode(key, data);
        // 存储解密之后的明文权限
        SlateDataHelper.saveBusinessWeekCrt(mContext, json);
        // 解析付费状态
        int status = 0;
        if (json != null) try {
            JSONObject jsonObject = new JSONObject(json);
            Log.e("getUserPermission", json);
            /**
             * 解析服务器返回的权限值{ status= (已验证-成功) 2 || (已验证-假订单) 4 }刷新本地存储
             */
            status = jsonObject.optInt("status");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;

    }

    /**
     * Login 页面监听返回
     *
     * @param fetchDataListener
     */
    public static void getUserPermission(FetchDataListener fetchDataListener) {
        JSONObject object = new JSONObject();
        try {
            object.put("usertoken", SlateDataHelper.getToken(mContext));
            object.put("appid", ConstData.getInitialAppId());
            object.put("marketkey", CommonApplication.CHANNEL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data", object.toString()));
        Log.e("&&&&&&&&&&&&", object.toString());
        instance.setPostParams(params);
        instance.requestHttpAsycle(true, UrlMaker.newGetUserPermission(), fetchDataListener);
    }

    /**
     * 恢复购买查询订单
     *
     * @param listener
     */
    public static void revertOrder(FetchDataListener listener) {
        JSONObject object = new JSONObject();
        try {
            object.put("uid", SlateDataHelper.getUid(mContext));
            object.put("usertoken", SlateDataHelper.getToken(mContext));
            object.put("appid", ConstData.getInitialAppId());
            object.put("marketkey", CommonApplication.CHANNEL);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data", object.toString()));
        instance.setPostParams(params);
        instance.requestHttpAsycle(true, UrlMaker.newRecoverOrder(), listener);
    }

    /**
     * vip 我的特权 4.1.0
     *
     * @param listener
     */
    public static void myPermission(FetchDataListener listener) {
        JSONObject object = new JSONObject();
        try {
            object.put("uid", SlateDataHelper.getUid(mContext));
            object.put("usertoken", SlateDataHelper.getToken(mContext));
            object.put("appid", ConstData.getInitialAppId());
            object.put("marketkey", CommonApplication.CHANNEL);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data", object.toString()));
        instance.setPostParams(params);
        instance.requestHttpAsycle(true, UrlMaker.myPermission(), listener);
    }

    /**
     * 订阅到vip的升级套餐列表
     *
     * @param listener
     */
    public static void toVipUpList(FetchDataListener listener) {
        JSONObject object = new JSONObject();
        try {
            object.put("uid", SlateDataHelper.getUid(mContext));
            object.put("usertoken", SlateDataHelper.getToken(mContext));
            object.put("appid", ConstData.getInitialAppId());
            object.put("marketkey", CommonApplication.CHANNEL);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data", object.toString()));
        instance.setPostParams(params);
        instance.requestHttpAsycle(true, UrlMaker.vipUpdateList(), listener);
    }

    /**
     * vip到vip的升级套餐列表
     *
     * @param listener
     */
    public static void vip2vipUpList(FetchDataListener listener) {
        JSONObject object = new JSONObject();
        try {
            object.put("uid", SlateDataHelper.getUid(mContext));
            object.put("usertoken", SlateDataHelper.getToken(mContext));
            object.put("appid", ConstData.getInitialAppId());
            object.put("marketkey", CommonApplication.CHANNEL);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data", object.toString()));
        instance.setPostParams(params);
        instance.requestHttpAsycle(true, UrlMaker.getVipUpList(), listener);
    }

    /**
     * vip 升级接口 4.1.0
     *
     * @param listener
     */
    public static void vipUp(FetchDataListener listener) {
        JSONObject object = new JSONObject();
        try {
            object.put("uid", SlateDataHelper.getUid(mContext));
            object.put("usertoken", SlateDataHelper.getToken(mContext));
            object.put("appid", ConstData.getInitialAppId());
            object.put("marketkey", CommonApplication.CHANNEL);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data", object.toString()));
        instance.setPostParams(params);
        instance.requestHttpAsycle(true, UrlMaker.vipUp(), listener);
    }

    /**
     * vip 获取信息接口
     *
     * @param listener
     */
    public static void getProduct(Context c, String pid, FetchDataListener listener) {
        JSONObject object = new JSONObject();
        try {
            object.put("pid", pid);
            object.put("appid", ConstData.getInitialAppId());
            object.put("marketkey", CommonApplication.CHANNEL);
            object.put("uid", SlateDataHelper.getUid(c));
            object.put("usertoken", SlateDataHelper.getToken(c));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data", object.toString()));
        instance.setPostParams(params);
        instance.requestHttpAsycle(true, UrlMaker.getProduct(), listener);
    }

    /**
     * 获取我的订阅列表
     * appid,uid，usertoken,toid
     */
    public static void getMyBooks(Context c, FetchDataListener listener) {
        JSONObject object = new JSONObject();
        try {
            object.put("appid", ConstData.getInitialAppId());
            object.put("uid", SlateDataHelper.getUid(c));
            object.put("usertoken", SlateDataHelper.getToken(c));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data", object.toString()));
        instance.setPostParams(params);
        instance.requestHttpAsycle(true, UrlMaker.getMyBooks(), listener);
    }

    public ShangchengIndexItem parseShangchengIndexItem(JSONObject jj) {
        ShangchengIndexItem shangchengIndexItem = new ShangchengIndexItem();
        JSONObject jsonObject = jj.optJSONObject("scene");
        if (jsonObject != null) {
            shangchengIndexItem.setId(jsonObject.optString("id"));
            shangchengIndexItem.setName(jsonObject.optString("name"));
            shangchengIndexItem.setReadLevel(jsonObject.optInt("readLevel"));
            shangchengIndexItem.setCmsTagId(jsonObject.optString("cmsTagId"));
            shangchengIndexItem.setCmsShowStyle(jsonObject.optInt("cmsShowStyle"));
            shangchengIndexItem.setDescUrl(jsonObject.optString("descUrl"));
            JSONObject icon = jsonObject.optJSONObject("icon");
            if (icon != null) shangchengIndexItem.setIcon(icon.optString("normal"));
            shangchengIndexItem.setShowPrice(jsonObject.optString("showPrice"));
            shangchengIndexItem.setProtocolList(jsonObject.optString("protocolList"));
            JSONArray pics = jsonObject.optJSONArray("picture");
            if (pics != null && pics.length() > 0) {
                List<String> pictures = new ArrayList<>();
                for (int j = 0; j < pics.length(); j++) {
                    JSONObject pic = pics.optJSONObject(j);
                    if (pic != null) {
                        pictures.add(pic.optString("url"));
                    }
                }
            }
        }

        shangchengIndexItem.setGoodId(jj.optString("goodId"));
        shangchengIndexItem.setPlatformName(jj.optString("platformName"));
        shangchengIndexItem.setLevel(jj.optInt("level"));
        shangchengIndexItem.setEndTime(jj.optLong("endTime"));

        return shangchengIndexItem;
    }

    /**
     * 存储微信支付订单vip
     *
     * @param user
     * @param type
     * @param oid
     * @param toid
     * @param status
     */
    public static void saveOrder(Context context, User user, String type, String oid, String toid, String status, VipGoodList.VipGood product, String payStyle, String time, String name, String phone, String address, Boolean postCard) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getUid());
            jsonObject.put("usertoken", user.getToken());
            jsonObject.put("oid", oid);// slate订单号
            jsonObject.put("toid", toid);// 第三方平台订单号
            jsonObject.put("status", status);
            jsonObject.put("product_name", product.getGoodName());
            jsonObject.put("product_price", product.getPirce());
            jsonObject.put("pay_style", payStyle);
            jsonObject.put("time", time);
            jsonObject.put("name", name);
            jsonObject.put("phone", phone);
            jsonObject.put("address", address);
            jsonObject.put("postCard", postCard);
            SlateDataHelper.setOrder(context, type, jsonObject.toString());
        } catch (JSONException e) {

        }
    }

    /**
     * 存储支付订单
     *
     * @param user
     * @param type
     * @param oid
     * @param toid
     * @param status
     */
    public static void saveOrder(Context context, User user, String type, String oid, String toid, String status) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getUid());
            jsonObject.put("usertoken", user.getToken());
            jsonObject.put("oid", oid);// slate订单号
            jsonObject.put("toid", toid);// 第三方平台订单号
            jsonObject.put("status", status);
            SlateDataHelper.setOrder(context, type, jsonObject.toString());
        } catch (JSONException e) {

        }
    }

    /**
     * 支付之后更新服务器订单状态
     */

    public static void notifyServer(FetchDataListener listener) {
        String data = SlateDataHelper.getOrder(mContext, NEW_WEIXIN_KEY);
        if (TextUtils.isEmpty(data)) return;

        try {
            JSONObject jsonObject = new JSONObject(data);
            jsonObject.put("appid", ConstData.getInitialAppId());
            jsonObject.put("marketkey", CommonApplication.CHANNEL);

            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("data", jsonObject.toString()));
            instance.setPostParams(params);


            instance.requestHttpAsycle(true, UrlMaker.newUpdateOrderStatus(1), listener);
        } catch (JSONException e) {

        }

    }


    /**
     * 支付之后更新服务器订单状态
     */

    public static void notifyServer(final String statuStr, final String type) {
        String data = SlateDataHelper.getOrder(mContext, type);
        if (TextUtils.isEmpty(data)) return;

        try {
            JSONObject jsonObject = new JSONObject(data);
            // 如果状态为空，则是入版更新，取本地订单状态更新
            if (!TextUtils.isEmpty(statuStr)) jsonObject.put("status", statuStr);
            jsonObject.put("appid", ConstData.getInitialAppId());
            jsonObject.put("marketkey", CommonApplication.CHANNEL);

            SlateDataHelper.setOrder(mContext, type, jsonObject.toString());// 更新本地订单状态

            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("data", jsonObject.toString()));
            instance.setPostParams(params);
            int urltype = type.equals(NEW_WEIXIN_KEY) ? 1 : 2;

            instance.requestHttpAsycle(true, UrlMaker.newUpdateOrderStatus(urltype), new FetchDataListener() {
                @Override
                public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                    if (isSuccess) {
                        SlateDataHelper.clearOrder(mContext, type);
                        if (TextUtils.isEmpty(statuStr) || statuStr.equals(SUCCESS))
                            saveLevel(data);
                    }
                }
            });

        } catch (JSONException e) {

        }

    }

    /**
     * 支付之后更新服务器订单状态
     */

    public static void notifyServer(final String statuStr, final String type, FetchDataListener listener) {
        String data = SlateDataHelper.getOrder(mContext, type);
        if (TextUtils.isEmpty(data)) return;

        try {
            JSONObject jsonObject = new JSONObject(data);
            // 如果状态为空，则是入版更新，取本地订单状态更新
            if (!TextUtils.isEmpty(statuStr)) jsonObject.put("status", statuStr);
            jsonObject.put("appid", ConstData.getInitialAppId());
            jsonObject.put("marketkey", CommonApplication.CHANNEL);

            SlateDataHelper.setOrder(mContext, type, jsonObject.toString());// 更新本地订单状态

            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("data", jsonObject.toString()));
            instance.setPostParams(params);
            int urltype = type.equals(NEW_WEIXIN_KEY) ? 1 : 2;

            instance.requestHttpAsycle(true, UrlMaker.newUpdateOrderStatus(urltype), listener);
        } catch (JSONException e) {

        }

    }

    /**
     * 获取vip邮寄地址
     */
    public static void addressList(FetchDataListener listener) {
        JSONObject object = new JSONObject();
        try {
            object.put("uid", SlateDataHelper.getUid(mContext));
            object.put("token", SlateDataHelper.getToken(mContext));
            object.put("appid", ConstData.getInitialAppId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data", object.toString()));
        instance.setPostParams(params);
        instance.requestHttpAsycle(true, UrlMaker.listddress(), listener);
    }

    /**
     * 修改vip邮寄地址
     */
    public static void addressEdit(String name, String phone, String city, String address, String code, String id, FetchDataListener listener) {
        JSONObject object = new JSONObject();
        try {
            object.put("uid", SlateDataHelper.getUid(mContext));
            object.put("token", SlateDataHelper.getToken(mContext));
            object.put("appid", ConstData.getInitialAppId());
            object.put("name", name);
            object.put("phone", phone);
            object.put("city", city);
            object.put("address", address);
            object.put("code", code);
            object.put("id", Integer.parseInt(id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data", object.toString()));
        instance.setPostParams(params);
        instance.requestHttpAsycle(true, UrlMaker.editAddress(), listener);
    }

    /**
     * * 匹配设备的广告标签列表
     *
     * @param listener
     */
    public static void getAdvTagList(FetchDataListener listener) {
        JSONObject object = new JSONObject();
        try {
            object.put("appid", ConstData.getAppId());
            object.put("uid", SlateDataHelper.getUid(mContext));
            object.put("usertoken", SlateDataHelper.getToken(mContext));
            object.put("marketkey", CommonApplication.CHANNEL);
            object.put("deviceid", Tools.getDeviceId(mContext));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data", object.toString()));
        instance.setPostParams(params);
        instance.requestHttpAsycle(true, UrlMaker.getAdvTagUrl(), listener);
    }

    /**
     * 有赞登录
     * <p>
     * appid,uid,usertoken,marketkey
     *
     * @param listener
     */
    public static void youzanLogin(FetchDataListener listener) {
        JSONObject object = new JSONObject();
        try {
            object.put("appid", ConstData.getAppId());
            object.put("uid", SlateDataHelper.getUid(mContext));
            object.put("usertoken", SlateDataHelper.getToken(mContext));
            object.put("marketkey", CommonApplication.CHANNEL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data", object.toString()));
        instance.setPostParams(params);
        instance.requestHttpAsycle(true, UrlMaker.youzanLogin(), listener);
    }

    /**
     * 登出有赞
     */
    public static void youzanLogout(String uid) {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("client_id", CommonApplication.mConfig.getYouzan_client_id()));
        params.add(new BasicNameValuePair("client_secret", CommonApplication.mConfig.getYouzan_client_secret()));
        params.add(new BasicNameValuePair("open_user_id", uid));
        instance.setPostParams(params);
        instance.requestHttpAsycle(true, UrlMaker.youzanLogout(), new FetchDataListener() {
            @Override
            public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                if (isSuccess) {
                    Log.e("youzanLogout", data);
                }
            }
        });
    }

    /**
     * 添加vip邮寄地址
     */
    public static void addressAdd(String name, String phone, String city, String address, String code, FetchDataListener listener) {
        JSONObject object = new JSONObject();
        try {
            object.put("uid", SlateDataHelper.getUid(mContext));
            object.put("token", SlateDataHelper.getToken(mContext));
            object.put("appid", ConstData.getInitialAppId());
            object.put("name", name);
            object.put("phone", phone);
            object.put("city", city);
            object.put("address", address);
            object.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data", object.toString()));
        instance.setPostParams(params);
        instance.requestHttpAsycle(true, UrlMaker.addAddress(), listener);
    }

    /**
     * 同步请求服务器数据:post
     */
    public static void requestHttpPost(final String url, JSONObject data, FetchDataListener listener) {
        String resData = null;
        HttpPost httpPost = null;
        HttpClient httpClient = null;
        try {
            httpPost = new HttpPost(url);
            // 添加头部信息
            addHeaderMap(httpPost);

            NameValuePair param = new BasicNameValuePair("data", data.toString());
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(param);
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));

            httpClient = getHttpConnection(mContext, url);
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity resEntity = response.getEntity();
                resData = (resEntity == null) ? null : EntityUtils.toString(resEntity, HTTP.UTF_8);
                if (!TextUtils.isEmpty(resData)) {
                    listener.fetchData(true, resData, true);
                }
            }
        } catch (Exception e) {
            Log.e("上传订单错误信息", e.getMessage());
        }

    }


    /**
     * 异步请求服务器数据
     *
     * @param isPost
     */
    public void requestHttpAsycle(boolean isPost, String url, FetchDataListener listener) {
        HTTPThread thread = new HTTPThread(url);

        if (isPost) {
            thread.setPost(true);
            thread.setPostParams(postParas);
        }
        thread.setmFetchDataListener(listener);
        thread.start();
    }

    /**
     * post 参数
     */
    public void setPostParams(ArrayList<NameValuePair> nameValuePairs) {
        this.postParas = nameValuePairs;
    }

    /**
     * 获取要添加的头部信息
     *
     * @return
     */
    private static void addHeaderMap(HttpPost httpPost) {
        Map<String, String> headerMap = ModernMediaTools.getHttpHeaders(mContext);
        // 添加头部信息
        Iterator<String> iterator = headerMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = headerMap.get(key);
            if (!TextUtils.isEmpty(key)) {
                httpPost.addHeader(key, value);
            }
        }
    }


    /**
     * 读取网络接口子线程
     *
     * @author ZhuQiao
     */
    public static class HTTPThread extends Thread {
        @SuppressWarnings("unused")
        private URL mUrl = null;
        private FetchDataListener mFetchDataListener;
        private String url = "";
        private boolean isPost = false; // 默认用GET方式
        private List<NameValuePair> params; // post用参数
        private List<String> imagePathList = new ArrayList<String>(); // 要上传的图片存储路径(目前仅支持post方式)
        private String userAgent = "";// iweekly统计广告使用
        private int responseCode = -1;

        public HTTPThread(String url) {
            this.url = url == null ? "" : url;
            if (!TextUtils.isEmpty(url)) {
                try {
                    mUrl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setPost(boolean isPost) {
            this.isPost = isPost;
        }

        public void setmFetchDataListener(FetchDataListener mFetchDataListener) {
            this.mFetchDataListener = mFetchDataListener;
        }

        protected void setPostParams(ArrayList<NameValuePair> nameValuePairs) {
            this.params = nameValuePairs;
        }

        @Override
        public void run() {
            if (mUrl == null) {
                return;
            }
            // time collect
            TimeCollectUtil.getInstance().saveRequestTime(url, true, 0, false);
            if (isPost) {
                doPost();
            } else {
                doGet();
            }
        }

        private void doGet() {
            //将URL与参数拼接
            HttpGet getMethod = new HttpGet(url);
            try {
                HttpClient httpClient = getHttpConnection(mContext, url);
                HttpResponse response = httpClient.execute(getMethod); //发起GET请求
                String resData = null;
                responseCode = response.getStatusLine().getStatusCode();
                if (responseCode == HttpStatus.SC_OK) {
                    HttpEntity resEntity = response.getEntity();
                    resData = (resEntity == null) ? null : EntityUtils.toString(resEntity, HTTP.UTF_8);
                    Log.e("https get", resData);
                    Log.e("https url", url);
                }

                if (mFetchDataListener != null) mFetchDataListener.fetchData(true, resData, true);
                reSetUpdateTime();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void doPost() {
            HttpPost httpPost = null;
            HttpClient httpClient = null;
            try {
                httpPost = new HttpPost(url);
                // 添加头部信息
                addHeaderMap(httpPost);

                CustomMultipartEntity multipartContent = new CustomMultipartEntity();
                if (imagePathList != null) {
                    // 上传图片处理
                    if (imagePathList.size() == 3) {// 反馈接口固定传三张图片，可为空
                        for (int i = 0; i < imagePathList.size(); i++) {
                            if (!TextUtils.isEmpty(imagePathList.get(i)))
                                multipartContent.addPart("file" + (i + 1), new FileBody(new File(imagePathList.get(i))));
                        }
                    } else if (imagePathList.size() == 1)
                        multipartContent.addPart("image", new FileBody(new File(imagePathList.get(0))));
                }


                httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                httpClient = getHttpConnection(mContext, url);
                // 请求超时
                httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECT_TIMEOUT);
                // 读取超时
                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, READ_TIMEOUT);
                HttpResponse response = httpClient.execute(httpPost);

                String resData = null;
                responseCode = response.getStatusLine().getStatusCode();
                Log.e("https responseCode", responseCode + "");
                if (responseCode == HttpStatus.SC_OK) {
                    HttpEntity resEntity = response.getEntity();
                    resData = (resEntity == null) ? null : EntityUtils.toString(resEntity, HTTP.UTF_8);
                    Log.e("https url", url);
                    //                    Log.e("https back", resData);
                }

                if (mFetchDataListener != null) mFetchDataListener.fetchData(true, resData, true);
                reSetUpdateTime();
            } catch (Exception e) {
                if (e != null && !TextUtils.isEmpty(e.getMessage()))
                    SlatePrintHelper.logE("HTTP", e.getMessage());
            } finally {
                if (httpPost != null) httpPost.abort();
                if (httpClient != null) httpClient.getConnectionManager().shutdown();
            }
        }

        private String receiveData(InputStream is) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                byte[] buff = new byte[BUFFERSIZE];
                int readed = -1;
                while ((readed = is.read(buff)) != -1) {
                    baos.write(buff, 0, readed);
                }
                byte[] result = baos.toByteArray();
                if (result == null) return null;
                return new String(result);
            } finally {
                if (is != null) is.close();
                if (baos != null) baos.close();
            }
        }

        private FileBody getImageFilePama(String path) {
            FileBody fileBody = null;
            if (!TextUtils.isEmpty(path)) {
                // 适配jpg和png
                if (path.contains("jpg")) fileBody = new FileBody(new File(path), "image/jpg");
                else fileBody = new FileBody(new File(path), "image/png");
            }
            return fileBody;
        }

        private void reSetUpdateTime() {
        }

        private void showToast(String str) {
        }

    }

    /**
     * https 协议下需要认证服务器证书
     */
    private static HttpClient getHttpConnection(Context context, String url) throws IOException {
        Uri uri = Uri.parse(url);
        if (uri.getScheme().equals("https")) {
            InputStream ins = null;
            try {
                AssetManager am = context.getAssets();
                ins = am.open("x509.crt");
                //读取证书
                CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");  //问1
                Certificate cer = cerFactory.generateCertificate(ins);
                //创建一个证书库，并将证书导入证书库
                KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");   //问2
                keyStore.load(null, null);
                keyStore.setCertificateEntry("trust", cer);
                //把咱的证书库作为信任证书库
                SSLSocketFactory socketFactory = new MySSLSocketFactory(keyStore);
                Scheme sch = new Scheme("https", socketFactory, 443);
                //完工
                HttpClient mHttpClient = new DefaultHttpClient();
                mHttpClient.getConnectionManager().getSchemeRegistry().register(sch);
                return mHttpClient;

            } catch (KeyManagementException e) {

            } catch (UnrecoverableKeyException e) {

            } catch (NoSuchAlgorithmException e) {

            } catch (NoSuchProviderException e) {

            } catch (CertificateException e) {

            } catch (KeyStoreException e) {

            } finally {
                if (ins != null) ins.close();
            }
            return new DefaultHttpClient();
        } else {
            return new DefaultHttpClient();
        }
    }

    /**
     * 自定义一个SSLSocketFactory，忽略证书的验证
     */
    private static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    public static List<VipGoodList.VipGood> parseJson(JSONArray array, List<VipGoodList.VipGood> pros) {
        pros.clear();
        for (int i = 0; i < array.length(); i++) {
            JSONObject j = array.optJSONObject(i);

            pros.add(parseVipGood(j));
        }
        return pros;
    }

    public static VipGoodList.VipGood parseVipGood(JSONObject j) {
        VipGoodList.VipGood pro = new VipGoodList.VipGood();
        List<VipGoodList.VipPayType> payTypeLists = new ArrayList<>();
        List<VipGoodList.Fun> funList = new ArrayList<>();
        pro.setGoodId(j.optString("goodId"));
        pro.setGoodName(j.optString("goodName"));
        pro.setGoodType(j.optString("goodType"));
        pro.setPirce(j.optInt("price"));
        pro.setShowPrice(j.optString("showPrice"));
        pro.setDesc(j.optString("desc"));
        pro.setNum(j.optInt("num") + "");
        pro.setUnit(j.optString("unit"));
        pro.setExpireTime(j.optString("expireTime"));
        pro.setNeedAddress(j.optInt("needAddress"));
        pro.setNeednotice(j.optInt("needNotice"));
        pro.setShow_address_input(j.optInt("showAddressInput"));
        pro.setShow_notice_input(j.optInt("showNoticeInput"));
        pro.setIsExchange(j.optInt("isExchange"));
        pro.setDurationLeft(j.optInt("durationLeft"));
        pro.setDurationTotal(j.optInt("durationTotal"));
        pro.setDurationAdd(j.optInt("durationAdd"));
        pro.setDurationUnit(j.optString("durationUnit"));

        JSONArray payTypeArray = j.optJSONArray("payTypeList");
        if (payTypeArray != null) {
            for (int p = 0; p < payTypeArray.length(); p++) {
                JSONObject paytype = payTypeArray.optJSONObject(p);
                if (paytype != null) {
                    VipGoodList.VipPayType vipPayType = new VipGoodList.VipPayType();
                    vipPayType.setPayTypeId(paytype.optString("payTypeId"));
                    vipPayType.setPayTypeName(paytype.optString("payTypeName"));
                    vipPayType.setIsRecommend(paytype.optInt("isRecommend") + "");
                    vipPayType.setRecommendIconUrl(paytype.optString("recommendIconUrl"));
                    payTypeLists.add(vipPayType);
                }
            }
        }
        JSONArray funArray = j.optJSONArray("fun");
        if (funArray != null) {
            for (int k = 0; k < funArray.length(); k++) {
                JSONObject funObject = funArray.optJSONObject(k);
                VipGoodList.VipIcon vipIcon = new VipGoodList.VipIcon();
                if (funObject != null) {
                    VipGoodList.Fun fun = new VipGoodList.Fun();
                    fun.setFunId(funObject.optString("funId"));
                    fun.setFunName(funObject.optString("funName"));
                    fun.setDesc(funObject.optString("desc"));
                    fun.setNum(funObject.optInt("num") + "");
                    fun.setShowName(funObject.optString("showName"));
                    fun.setType(funObject.optInt("type") + "");
                    JSONObject iconObject = funObject.optJSONObject("icon");
                    vipIcon.setNormal(iconObject.optString("normal"));
                    vipIcon.setSelected(iconObject.optString("selected"));
                    vipIcon.setStyle1_normal(iconObject.optString("stylel_normal"));
                    fun.setVipIcon(vipIcon);
                    funList.add(fun);
                }
            }
        }
        pro.setFunList(funList);
        pro.setPayTypeLists(payTypeLists);
        return pro;
    }

}
