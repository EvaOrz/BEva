package cn.com.modernmediaslate.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.unit.SlatePrintHelper;
import cn.com.modernmediaslate.unit.TimeCollectUtil;

/**
 * Http请求控制器
 *
 * @author ZhuQiao
 */
public class HttpRequestController {
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
    private static HttpRequestController instance = null;

    private HttpRequestController() {
    }

    public static HttpRequestController getInstance() {
        if (instance == null) {
            instance = new HttpRequestController();
        }
        return instance;
    }

    public void fetchHttpData(RequestThread requestThread) {
        requestThread.start();
    }

    public void requestHttp(final String url) {
        requestHttp(url, null);
    }

    public void requestHttp(final String url, final String userAgent) {
        if (TextUtils.isEmpty(url)) return;
        new Thread() {

            @Override
            public void run() {
                try {
                    URL mUrl = new URL(url);
                    HttpURLConnection conn = null;
                    Log.e("流量bug查询**", "HttpRequestController:requestHttp()" + "-----" + url);
                    conn = (HttpURLConnection) mUrl.openConnection();

                    if (!TextUtils.isEmpty(userAgent)) {
                        conn.addRequestProperty("User-Agent", userAgent);
                    }
                    // connect()函数，实际上只是建立了一个与服务器的tcp连接，并没有实际发送http请求。
                    // 无论是post还是get，http请求实际上直到HttpURLConnection的getInputStream()这个函数里面才正式发送出去。
                    conn.getInputStream();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    /**
     * 读取网络接口子线程
     *
     * @author ZhuQiao
     */
    public static class RequestThread extends Thread {
        @SuppressWarnings("unused")
        private Context context;
        private URL mUrl = null;
        private FetchDataListener mFetchDataListener;
        private String url = "";
        private boolean isPost = false; // 默认用GET方式
        private List<NameValuePair> params; // post用参数
        private List<String> imagePathList = new ArrayList<String>(); // 要上传的图片存储路径(目前仅支持post方式)
        private SlateBaseOperate mOperate;
        private String userAgent = "";// iweekly统计广告使用
        private Map<String, String> headerMap = new HashMap<String, String>();
        private int responseCode = -1;

        public RequestThread(Context context, String url, SlateBaseOperate baseOperate) {
            this.context = context;
            this.url = url == null ? "" : url;
            mOperate = baseOperate;
            if (!TextUtils.isEmpty(url)) {
                try {
                    mUrl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setmFetchDataListener(FetchDataListener mFetchDataListener) {
            this.mFetchDataListener = mFetchDataListener;
        }

        protected boolean isPost() {
            return isPost;
        }

        public void setPost(boolean isPost) {
            this.isPost = isPost;
        }

        protected void setPostParams(ArrayList<NameValuePair> nameValuePairs) {
            this.params = nameValuePairs;
        }

        protected void setImagePath(List<String> imagePath) {
            this.imagePathList = imagePath;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public void setHeaderMap(Map<String, String> headerMap) {
            if (headerMap != null && !headerMap.isEmpty()) {
                this.headerMap = headerMap;
            }
        }

        @Override
        public void run() {
            if (mUrl == null) {
                return;
            }
            // time collect
            TimeCollectUtil.getInstance().saveRequestTime(url, true, 0, false);
            Log.d("http", url);
            if (isPost) {
                doPost();
            } else {
                doGet();
            }
        }

        private void doGet() {
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) mUrl.openConnection();
                Log.e("流量bug查询**", "HttpRequestController:doGet()" + "-----" + url);
                if (!TextUtils.isEmpty(userAgent)) {
                    conn.addRequestProperty("User-Agent", userAgent);
                }
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setReadTimeout(READ_TIMEOUT);
                // 添加头部信息
                Iterator<String> iterator = headerMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = headerMap.get(key);
                    if (!TextUtils.isEmpty(key)) {
                        conn.setRequestProperty(key, value);
                    }
                }
                responseCode = conn.getResponseCode();
                if (responseCode == HttpStatus.SC_OK) {
                    InputStream is = conn.getInputStream();
                    if (is == null) {
                        fetchLocalDataInBadNet();
                        return;
                    }
                    String data = receiveData(is);
                    if (TextUtils.isEmpty(data)) {
                        fetchLocalDataInBadNet();
                        return;
                    }
                    // time collect
                    TimeCollectUtil.getInstance().saveRequestTime(url, false, HttpStatus.SC_OK, false);
                    showToast("from http:" + url);
                    if (mFetchDataListener != null) mFetchDataListener.fetchData(true, data, true);
                    reSetUpdateTime();
                } else {
                    fetchLocalDataInBadNet();
                }
            } catch (Exception e) {
                fetchLocalDataInBadNet();
                if (e != null && !TextUtils.isEmpty(e.getMessage()))
                    SlatePrintHelper.logE("HTTP", e.getMessage());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }

        private void doPost() {
            HttpPost httpPost = null;
            HttpClient httpClient = null;

            try {

                httpPost = new HttpPost(url);
                // 添加头部信息
                Iterator<String> iterator = headerMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = headerMap.get(key);
                    if (!TextUtils.isEmpty(key)) {
                        httpPost.addHeader(key, value);
                    }
                }

                MultipartEntity multipartContent = new MultipartEntity();
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

                // 参数设置
                if (params != null) {
                    for (NameValuePair param : params) {
                        String value = param.getValue();
                        if (!TextUtils.isEmpty(value)) {
                            multipartContent.addPart(param.getName(), new StringBody(value));
                            SlatePrintHelper.print(param.getName() + value);
                        }
                    }
                }
                httpPost.setEntity(multipartContent);
                httpClient = new DefaultHttpClient();
                // 请求超时
                httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECT_TIMEOUT);
                // 读取超时
                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, READ_TIMEOUT);
                HttpResponse response = httpClient.execute(httpPost);

                String resData = null;
                responseCode = response.getStatusLine().getStatusCode();
                if (responseCode == HttpStatus.SC_OK) {
                    HttpEntity resEntity = response.getEntity();
                    resData = (resEntity == null) ? null : EntityUtils.toString(resEntity, HTTP.UTF_8);
                }

                // Header[] reqHeaders = response.getAllHeaders();
                // // 遍历Header数组，并打印出来
                // for (int i = 0; i < reqHeaders.length; i++) {
                // String name = reqHeaders[i].getName();
                // String value = reqHeaders[i].getValue();
                // SlatePrintHelper
                // .print("Name—>" + name + ",Value—>" + value);
                // }
                if (resData == null) {
                    Log.e("&&&&&&&&&11111", url);
                    fetchLocalDataInBadNet();
                    return;
                }

                TimeCollectUtil.getInstance().saveRequestTime(url, false, HttpStatus.SC_OK, false);
                showToast("from http:" + url);
                if (mFetchDataListener != null) mFetchDataListener.fetchData(true, resData, true);
                reSetUpdateTime();
            } catch (Exception e) {
                Log.e("&&&&&&&&&22222", url);
                fetchLocalDataInBadNet();
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

        private void fetchLocalDataInBadNet() {
            if (mOperate != null) mOperate.fetchDataFromCacheByNetError(responseCode);
            // 给除了SlateBaseOperate之外的别的可能需要调用http请求的方法回调
            if (mFetchDataListener != null) mFetchDataListener.fetchData(false, null, false);
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


}
