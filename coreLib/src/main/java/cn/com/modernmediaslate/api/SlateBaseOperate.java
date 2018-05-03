package cn.com.modernmediaslate.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import cn.com.modernmediaslate.api.HttpRequestController.RequestThread;
import cn.com.modernmediaslate.listener.DataCallBack;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.unit.SlatePrintHelper;
import cn.com.modernmediaslate.unit.TimeCollectUtil;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 封装服务器返回数据父类
 *
 * @author ZhuQiao
 */
public abstract class SlateBaseOperate {
    private HttpRequestController mController = HttpRequestController.getInstance();

    public Context mContext;
    private boolean success = false;// 是否解析成功
    private boolean showToast = true;

    private boolean isNeedEncode = false;

    private DataCallBack callBack;
    protected FetchApiType mFetchApiType = FetchApiType.USE_HTTP_FIRST;
    public static final String KEY = "R3%3jg*3";

    /**
     * 缓存文件是否为数据库
     */
    protected boolean cacheIsDb = false;

    public static enum FetchApiType {
        /**
         * 优先获取服务器数据
         */
        USE_HTTP_FIRST,
        /**
         * 优先获取缓存
         */
        USE_CACHE_FIRST,
        /**
         * 只获取服务器数据
         */
        USE_HTTP_ONLY,
        /**
         * 只获取缓存数据
         */
        USE_CACHE_ONLY
    }

    public static class CallBackData {
        public boolean success = false;
        public String data = "";
    }

    /**
     * 缓存数据回调接口
     *
     * @author zhuqiao
     */
    protected static interface CacheCallBack {
        public void onCallBack(CallBackData callBackData);
    }

    /**
     * 由子类提供
     */
    protected abstract String getUrl();

    /**
     * 由子类提供
     */
    protected ArrayList<NameValuePair> getPostParams() {
        return null;
    }

    /**
     * 由子类提供
     */
    protected List<String> getPostImagePath() {
        return null;
    }


    public void setIsNeedEncode(boolean isNeedEncode) {
        this.isNeedEncode = isNeedEncode;
    }

    /**
     * 获取要添加的头部信息
     *
     * @return
     */
    protected Map<String, String> getHeader() {
        return new HashMap<String, String>();
    }

    public void setShowToast(boolean showToast) {
        this.showToast = showToast;
    }

    /**
     * 以get方式请求服务器，并解析Json数据
     *
     * @param context
     * @param fetchApiType 是否优先使用本地数据
     * @param callBack
     */
    public void asyncRequest(Context context, FetchApiType fetchApiType, DataCallBack callBack) {
        mContext = context;
        this.callBack = callBack;
        if (TextUtils.isEmpty(getUrl()) || callBack == null) {
            // TODO 提示错误信息
            return;
        }
        requestCache(fetchApiType, false);
    }

    /**
     * 以post方式请求服务器，并解析Json数据
     *
     * @param context
     * @param fetchApiType 是否优先使用本地数据
     * @param callBack
     */

    public void asyncRequestByPost(Context context, FetchApiType fetchApiType, DataCallBack callBack) {
        mContext = context;
        this.callBack = callBack;
        if (TextUtils.isEmpty(getUrl()) || callBack == null) {
            // TODO 提示错误信息
            return;
        }
        requestCache(fetchApiType, true);
    }

    /**
     * 请求缓存数据
     *
     * @param fetchApiType
     * @param isPost
     */

    private void requestCache(final FetchApiType fetchApiType, final boolean isPost) {
        mFetchApiType = fetchApiType;

        if (mFetchApiType == FetchApiType.USE_HTTP_FIRST || mFetchApiType == FetchApiType.USE_HTTP_ONLY) {
            requestHttp(fetchApiType, isPost);
            return;
        }

        fetchDataFromCache(new CacheCallBack() {

            @Override
            public void onCallBack(CallBackData callBackData) {
                if (callBackData == null) {
                    requestHttp(fetchApiType, isPost);
                    return;
                }
                if (callBackData.success) {
                    // 有效的缓存数据, 回调
                    doCacheCallBack(true, callBackData.data);
                    return;
                }
                if (mFetchApiType == FetchApiType.USE_CACHE_ONLY) {
                    // 缓存无效，并且只取缓存的形式，那么回调错误
                    doCacheCallBack(false, null);
                } else {
                    requestHttp(fetchApiType, isPost);
                }
            }
        });
    }

    /**
     * 请求服务器数据
     *
     * @param fetchApiType 获取接口数据形式
     * @param isPost
     */
    private void requestHttp(FetchApiType fetchApiType, boolean isPost) {
        String url = getUrl();
        RequestThread thread = new RequestThread(mContext, url, this);

        if (isPost) {
            thread.setPost(true);
            thread.setPostParams(getPostParams());
            thread.setImagePath(getPostImagePath());
        }
        thread.setHeaderMap(getHeader());
        thread.setmFetchDataListener(new FetchDataListener() {

            @Override
            public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                if (isSuccess && !TextUtils.isEmpty(data)) {
                    // NOTE
                    // 如果获取成功，那么执行handler;否则，会去执行fetchDataFromCacheByNetError这个方法
                    handlerData(isSuccess, data, fromHttp);
                }
            }
        });
        mController.fetchHttpData(thread);
    }

    /**
     * 解析数据
     *
     * @param isSuccess
     * @param data
     * @param fromHttp
     */
    private void handlerData(boolean isSuccess, String data, boolean fromHttp) {
        if (isSuccess) {
            if (TextUtils.isEmpty(data)) {
                Log.e("********", "网络出错111" + getUrl());
                // showToast(R.string.net_error);
            } else {
                if (data.equals("[]")) {
                    data = "{}";
                }
                try {
                    JSONObject obj;
                    if (isNeedEncode) {
                        String msg = decode(KEY, data);
                        obj = new JSONObject(msg);
                    } else obj = new JSONObject(data);
                    if (isNull(obj)) {
                        Log.e("********", "网络出错222" + getUrl());
                        // showToast(R.string.net_error);
                    } else {
                        handler(obj);
                        saveData(data);
                        success = true;
                    }
                } catch (JSONException e) {
                    SlatePrintHelper.print(getUrl() + ":can not transform to jsonobject");
                    e.printStackTrace();
                    Log.e("********", "网络出错333" + getUrl());
                    // showToast(R.string.net_error);
                }
            }
        } else {
            Log.e("********", "网络出错444" + getUrl());
            //			showToast(R.string.net_error);
        }
        if (callBack != null) callBack.callback(success, fromHttp);
    }

    /**
     * 从缓存中获取数据(优先使用或者只能使用缓存)
     *
     * @return
     */
    private void fetchDataFromCache(CacheCallBack cacheCallBack) {
        if (cacheCallBack == null) return;
        if (cacheIsDb) {
            fetchDataFromDB(cacheCallBack);
        } else {
            fetchDataFromSD(cacheCallBack);
        }
    }

    /**
     * 接口请求失败，尝试获取缓存
     */
    protected void fetchDataFromCacheByNetError(int responseCode) {
        TimeCollectUtil collect = TimeCollectUtil.getInstance();
        if (mFetchApiType == FetchApiType.USE_HTTP_ONLY) {
            collect.saveRequestTime(getUrl(), false, responseCode, false);
            // 如果只能从网络获取，那么直接返回错误
            SlatePrintHelper.printErr("net error:" + getUrl());
            doCacheCallBack(false, null);
            return;
        }
        if (mFetchApiType == FetchApiType.USE_CACHE_FIRST) {
            collect.saveRequestTime(getUrl(), false, responseCode, false);
            // 如果缓存优先，说明一开始已经获取过缓存了，那么直接返回错误
            SlatePrintHelper.printErr("net error:" + getUrl());
            doCacheCallBack(false, null);
            return;
        }
        // NOTE 本身已经在子线程中了，所以不需要重新开启子线程了
        CallBackData result = cacheIsDb ? fetchDataFromDB() : fetchDataFromSD();
        doCacheCallBack(result.success, result.data);

        collect.saveRequestTime(getUrl(), false, responseCode, result.success);
    }

    /**
     * 缓存回调给调用者
     *
     * @param success
     * @param data
     */
    private void doCacheCallBack(boolean success, String data) {
        if (cacheIsDb) {
            if (!success) {
                // showToast(R.string.net_error);
                Log.e("********", "网络出错99999" + getUrl());
            }
            if (callBack != null) {
                callBack.callback(success, false);
            }
        } else {
            handlerData(success, data, false);
        }
    }

    /**
     * 从sd卡获取数据
     *
     * @param cacheCallBack
     */
    private void fetchDataFromSD(final CacheCallBack cacheCallBack) {
        if (cacheCallBack == null) return;
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                cacheCallBack.onCallBack(fetchDataFromSD());
            }
        });
        thread.start();
        thread = null;
    }

    /**
     * 从数据库获取数据
     *
     * @param cacheCallBack
     */
    private void fetchDataFromDB(final CacheCallBack cacheCallBack) {
        if (cacheCallBack == null) return;
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                cacheCallBack.onCallBack(fetchDataFromDB());
            }
        });
        thread.start();
        thread = null;
    }

    /**
     * 从sd卡获取数据(由统一的子类提供)
     *
     * @return
     */
    protected CallBackData fetchDataFromSD() {
        return new CallBackData();
    }

    /**
     * 从数据库获取数据(由特定的子类提供)
     *
     * @return
     */
    protected CallBackData fetchDataFromDB() {
        return new CallBackData();
    }

    /**
     * 由子类去解析json数据
     *
     * @param jsonObject
     */
    protected abstract void handler(JSONObject jsonObject);

    /**
     * 解析完保存数据
     *
     * @param data
     */
    protected abstract void saveData(String data);

    /**
     * 获取默认的文件名(由于不需要getissue接口，其它接口都是在getissue之后进行的并且在getissue时保存过相应的护具，
     * 所以所有默认的文件后缀都用sharep里面的)
     *
     * @return
     */
    protected abstract String getDefaultFileName();

    private void showToast(int resId) {
        // 升级提示和统计不提示错误信息
        if (showToast && mContext != null) {
            Tools.showToast(mContext, resId);
        }
    }

    public Context getmContext() {
        return mContext;
    }

    /**
     * JSONObject是否为空
     *
     * @param object
     * @return
     */
    protected boolean isNull(JSONObject object) {
        return JSONObject.NULL.equals(object) || object == null;
    }

    /**
     * JSONArray是否为空
     *
     * @param array
     * @return
     */
    protected boolean isNull(JSONArray array) {
        return JSONObject.NULL.equals(array) || array == null || array.length() == 0;
    }

    /**
     * 将Value值进行UTF-8编码
     *
     * @param obj
     * @param key
     * @param value
     * @throws Exception
     */
    protected void addPostParams(JSONObject obj, String key, String value) throws Exception {
        if (!TextUtils.isEmpty(value)) {
            String encode = URLEncoder.encode(value, HTTP.UTF_8);
            // 数据中含换行符时，不能编码，否则服务器端在解析该json时会无法解析
            String br = URLEncoder.encode("\n", HTTP.UTF_8);
            if (encode.contains(br)) {
                encode = encode.replace(br, "\n");
            }
            obj.put(key, encode);
        }
    }

    /**
     * @param obj
     * @param key
     * @param value
     */
    protected void addPostParamsCode(JSONObject obj, String key, String value) throws Exception {
        if (!TextUtils.isEmpty(value)) {
            obj.put(key, value);
        }
    }

    /**
     * 3DES解密
     *
     * @param encryptText 加密文本
     * @return
     * @throws Exception
     */
    private String decode(String secretKey, String encryptText) {
        byte[] output = null;
        try {
            SecretKeySpec skey = new SecretKeySpec(secretKey.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(decode(encryptText));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return new String(output);
    }


    private byte[] decode(String s) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            decode(s, bos);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        byte[] decodedBytes = bos.toByteArray();
        try {
            bos.close();
            bos = null;
        } catch (IOException ex) {
            System.err.println("Error while decoding BASE64: " + ex.toString());
        }
        return decodedBytes;
    }

    private void decode(String s, OutputStream os) throws IOException {
        int i = 0;

        int len = s.length();

        while (true) {
            while (i < len && s.charAt(i) <= ' ') i++;

            if (i == len) break;

            int tri = (decode(s.charAt(i)) << 18) + (decode(s.charAt(i + 1)) << 12) + (decode(s.charAt(i + 2)) << 6) + (decode(s.charAt(i + 3)));

            os.write((tri >> 16) & 255);
            if (s.charAt(i + 2) == '=') break;
            os.write((tri >> 8) & 255);
            if (s.charAt(i + 3) == '=') break;
            os.write(tri & 255);

            i += 4;
        }
    }

    private int decode(char c) {
        if (c >= 'A' && c <= 'Z') return ((int) c) - 65;
        else if (c >= 'a' && c <= 'z') return ((int) c) - 97 + 26;
        else if (c >= '0' && c <= '9') return ((int) c) - 48 + 26 + 26;
        else switch (c) {
                case '+':
                    return 62;
                case '/':
                    return 63;
                case '=':
                    return 0;
                default:
                    throw new RuntimeException("unexpected code: " + c);
            }
    }
}