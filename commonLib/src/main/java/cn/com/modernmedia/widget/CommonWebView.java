package cn.com.modernmedia.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.WebProcessListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.Picture;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmedia.webridge.JavascriptInterface;
import cn.com.modernmedia.webridge.WBWebView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.DESCoder;
import cn.com.modernmediaslate.unit.MD5;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.TimeCollectUtil;

@SuppressLint("SetJavaScriptEnabled")
public class CommonWebView extends WBWebView {
    private CommonWebView me;
    public static final String ERROR_WEB = "file:///android_asset/error_web.html?g="; // 错误页面
    private static final int TIME_OUT_MSG = 100;
    private WebProcessListener listener;
    private boolean loadOk = true;
    private ArticleItem detail;
    private Context mContext;
    private boolean isChangeStatus = false;
    private boolean isFetchNull = false;
    private GestureDetector gestureDetector;
    private int x, y;
    private List<String> urlList = new ArrayList<String>();// 图片列表
    private List<String> descList = new ArrayList<String>();// 描述列表
    private boolean hasLoadFromHttp = false;// 防止无限获取http
    private boolean isError = false;// 因为disProcess会延迟0.5S,所以判断一下
    private boolean isSlateWeb = false;// 是否是slate内部浏览器，true,拦截http请求
    private boolean hasPush;
    private boolean isPushArticle = false;// 是否是push文章
    private boolean isShowColumn = false; // 是否是栏目内容
    private WebViewLoadListener mLoadListener;

    public interface WebViewLoadListener {
        /**
         * 网页数据加载完成
         *
         * @param success
         */
        public void loadComplete(boolean success);
    }

    @Override
    public void setVerticalScrollbarOverlay(boolean overlay) {
        if (overlay) {
            this.setOverScrollMode(OVER_SCROLL_NEVER);
        }
    }

//    @JavascriptInterface
//    final class InJavaScriptLocalObj {
//        @JavascriptInterface
//        public void showSource(final String html) {
//            handler.post(new Runnable() {
//
//                @Override
//                public void run() {
//                    checkHtmlIsNull(html);
//                }
//            });
//        }
//    }

    @JavascriptInterface
    final class MakeCard {
        @JavascriptInterface
        public void make(final String result) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    ArticleItem item = getCard(result);
                    if (item != null) {
                        gotoWriteNewCardActivity(item);
                    }
                }
            });
        }

        @JavascriptInterface
        public void test(){
            Log.e("-----1","=======");
        }
    }

    class Test{
        @android.webkit.JavascriptInterface
        public void test(final String result){
//            Toast.makeText(mContext,"webview + 响应了参数：" + result,Toast.LENGTH_SHORT).show();
            handler.post(new Runnable() {

                @Override
                public void run() {
                    ArticleItem item = getCard(result);
                    if (item != null) {
                        gotoWriteNewCardActivity(item);
                    }
                }
            });
        }
    }

    /**
     * 获取点击的图片的src值
     *
     * @author user
     */
    @JavascriptInterface
    final class GetImageSrc {
        @JavascriptInterface
        public void getSrc(final String result) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    String currentUrl = TextUtils.isEmpty(result) ? "" : result;
                    gotoGalleryActivity(urlList, currentUrl, descList);
                }
            });
        }
    }

    /**
     * 获取分享内容
     *
     * @author zhuqiao
     */
    @JavascriptInterface
    final class GetShareMsg {

        @JavascriptInterface
        public void getShareMsg(final String result) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    parseShareMessage(result);
                }
            });
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_OUT_MSG:
                    showErrorType(2);
                    break;
                default:
                    break;
            }
        }

    };

    public CommonWebView(Context context, boolean bgIsTransparent) {
        this(context, null, bgIsTransparent);
    }

    public CommonWebView(Context context, AttributeSet attrs, boolean bgIsTransparent) {
        super(context, attrs);
        mContext = context;
        init(bgIsTransparent);
    }

    public CommonWebView(Context context, AttributeSet attrs) {
        this(context, attrs, true);
    }


    @SuppressLint("JavascriptInterface")
    private void init(boolean bgIsTransparent) {
        me = this;
        gestureDetector = new GestureDetector(new OnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (mContext instanceof CommonArticleActivity)
                    ((CommonArticleActivity) mContext).getHideListener().hide();
                x = (int) e.getX();
                y = (int) e.getY();
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                onLongClick((int) e.getX(), (int) e.getY());
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }
        });
        if (bgIsTransparent) this.setBackgroundColor(0);// 设置webview本身的白色背景为透明，以显示在它下面的view

        this.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);// 去掉白边

        /**
         * 网页调试用
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (0 != (mContext.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
        //        this.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        this.addJavascriptInterface(new MakeCard(), "make");
        this.addJavascriptInterface(new Test(),"test");
        //        this.addJavascriptInterface(new GetImageSrc(), "getSrc");
        //        this.addJavascriptInterface(new GetShareMsg(), "getShareMsg");


        this.setWebViewClient(new WebViewClient() {

            /**
             * 在页面加载开始时调用
             */
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            /**
             * 在页面加载结束时调用
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                if (loadOk) {
                    getSettings().setBlockNetworkImage(false);
                    if (isSlateWeb || isPushArticle) {
                        push();
                    } else if (mContext instanceof CommonArticleActivity) {
                        onPageSelected();
                    }
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (!isError) showErrorType(0);
                        }
                    }, 500);
                    //                    view.loadUrl("javascript:window.local_obj.showSource(document.getElementsByTagName('head')[0].innerHTML)");
                    if (isPushArticle || isShowColumn) {
                        getShareMessage();
                    }
                    //                    targetUrl = url;
                }
                if (mLoadListener != null) mLoadListener.loadComplete(loadOk);
            }

            /**
             * 在点击请求的是链接是才会调用， 重写此方法返回true表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("内部网页跳转拦截", url);
                if (mWbUri.canOpenURI(url, view,detail)) return true;

                HitTestResult hitTestResult = view.getHitTestResult();
                if (hitTestResult == null || hitTestResult.getType() == HitTestResult.UNKNOWN_TYPE) {
                    //                    targetUrl = url;
                    me.loadUrl(url);
                    return false;
                }
                if ((isSlateWeb || isShowColumn) && !TextUtils.isEmpty(url) && url.startsWith("http")) {
                    //                    targetUrl = url;
                    me.loadUrl(url);
                    return false;
                }

                UriParse.clickSlate(mContext, url, new Entry[]{detail}, me);
                return true;
            }

            /**
             * 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次
             */
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            /**
             * 处理https请求
             */
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                loadOk = false;
                if (mLoadListener != null) mLoadListener.loadComplete(false);
                showErrorType(2);
                handler.proceed(); // 接受所有证书
                //                handler.cancel();// 不支持ssl
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e("onReceivedError", failingUrl);

                loadOk = false;

                if (mLoadListener != null) mLoadListener.loadComplete(false);
                showErrorType(2);
                if (isShowColumn && !failingUrl.startsWith(ERROR_WEB)) {// 已经是错误页面时不用再加载
                    view.stopLoading();
                    view.clearView();
                    me.loadUrl(ERROR_WEB + failingUrl);
                } else {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                }

            }

            /**
             * 如果是同一篇文章从缓存拿出，如果以前没有拦截过，那么shouldInterceptRequest只能拦截到它的url,
             * html里面的东西都拦截不到了；
             * 反之，当第一次拦截了东西，那么以后shouldInterceptRequest还是会拦截相同的东西
             * PS:getSettings().setBlockNetworkImage不改回来，是不会拦截图片url的
             */
            // @Override
            // public WebResourceResponse shouldInterceptRequest(WebView view,
            // String url) {
            // return null;
            // }
        });
        setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(intent);
            }
        });
    }

    /**
     * 加载html页面
     *
     * @param detail
     */
    public void startLoadHtml(final ArticleItem detail) {
        if (!TextUtils.isEmpty(detail.getHtml())) {
            hasLoadFromHttp = false;
            loadOk = true;
            this.detail = detail;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    String ttt = DESCoder.decode(MD5.MD5Encode(detail.getArticleId() + "").substring(0, 8), detail.getHtml());
                    loadDataWithBaseURL(null, ttt, "text/html", "UTF-8", null);
                }
            });
        }

    }

    //    public void startLoad(ArticleItem detail) {
    //        hasLoadFromHttp = false;
    //        loadOk = true;
    //        if (detail == null) return;
    //        this.detail = detail;
    //        String url = "";
    //        if (ParseUtil.listNotNull(detail.getPageUrlList())) {
    //            url = detail.getPageUrlList().get(0).getUrl();
    //        }
    //        if (TextUtils.isEmpty(url) || !url.startsWith("http")) {
    //            return;
    //        }
    //        loadUrl(url);
    //    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url, ModernMediaTools.getHttpHeaders(mContext));
        if (!isChangeStatus) {
            isChangeStatus = false;
            if (!url.startsWith(ERROR_WEB)) {
                showErrorType(1);
            }
        }
    }

    @Override
    public void reload() {
        if (detail != null) {
            me.startLoadHtml(detail);
        } else {
            super.reload();
            showErrorType(1);
        }
    }

    @Override
    public void goBack() {
        super.goBack();
    }

    private void onPageSelected() {
        if (detail == null) return;
        ((CommonArticleActivity) mContext).addLoadOkIds(detail.getArticleId());
        if (((CommonArticleActivity) mContext).getCurrArticleId() == detail.getArticleId()) {
            ReadDb.getInstance(mContext).addReadArticle(detail.getArticleId());
            LogHelper.logAndroidShowArticle(mContext, detail.getTagName(), detail.getArticleId() + "");
            LogHelper.logNextArticleAfterPush(mContext, detail.getArticleId() + "", detail.getTitle());
            AdvTools.requestImpression(detail);
            push();
        }
        changeFont();
        changeLineHeight();
    }

    /**
     * 判断加载完的html是否为空
     *
     * @param html
     */
    private void checkHtmlIsNull(String html) {
        Log.e("is null about html", html + "");
        if (TextUtils.isEmpty(html)) {
            isFetchNull = true;
            me.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            if (!hasLoadFromHttp) {
                hasLoadFromHttp = true;
                showErrorType(1);
                new Thread() {

                    @Override
                    public void run() {
                        getHtmlIfNull();
                    }

                }.start();
            } else {
                showErrorType(2);
            }
        } else {
            if (isFetchNull) {
                isFetchNull = false;
                me.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }
            if (isPushArticle)
                TimeCollectUtil.getInstance().savePageTime(TimeCollectUtil.EVENT_OPEN_PUSH, false);
        }
    }

    public void setListener(WebProcessListener listener) {
        this.listener = listener;
    }

    /**
     * keyword，头条等可点击跳转至具体文章页
     *
     * @param transferArticle
     */
    public void gotoArticle(TransferArticle transferArticle) {
        if (isSlateWeb || isPushArticle) return;
        if (mContext instanceof CommonArticleActivity && transferArticle != null) {
            ((CommonArticleActivity) mContext).moveToArticle(transferArticle);
        }
    }

    /**
     * 跳转至某个广告
     *
     * @param advId
     */
    public void gotoAdv(int advId) {
        if (isSlateWeb || isPushArticle) return;
        if (mContext instanceof CommonArticleActivity && advId != -1) {
            ((CommonArticleActivity) mContext).moveToAdv(advId);
        }
    }

    /**
     * 获取文章相册列表
     *
     * @param urlList
     */
    public void fetchGalleryList(List<String> urlList, List<String> descList) {
        this.urlList = urlList;
        this.descList = descList;
        getImageSrc(x, y);
    }

    ;

    /**
     * 跳转至相册页
     *
     * @param urlList    图片地址列表
     * @param currentUrl 当前选中的图片地址
     * @param descList   描述列表
     */
    public void gotoGalleryActivity(List<String> urlList, String currentUrl, List<String> descList) {
    }

    /**
     * 跳转至写卡片页
     */
    public void gotoWriteNewCardActivity(ArticleItem item) {
    }

    private void parseShareMessage(String message) {
        if (detail == null) return;
        if (TextUtils.isEmpty(message)) return;
        String[] arr = message.split(", ");
        if (arr == null || arr.length == 0) return;
        for (String str : arr) {
            if (TextUtils.isEmpty(str)) continue;
            String[] items = str.split("=");
            if (items == null || items.length != 2) continue;
            String before = items[0];
            String value = items[1];
            if (TextUtils.equals(before, "title")) {
                detail.setTitle(value);
            } else if (TextUtils.equals(before, "thumb")) {
                Picture picture = new Picture();
                picture.setUrl(value);
                detail.getThumbList().add(picture);
            } else if (TextUtils.equals(before, "link")) {
                detail.setWeburl(value);
            } else if (TextUtils.equals(before, "desc")) {
                detail.setDesc(value);
            }
        }

    }

    /**
     * 改变字体
     */
    public void changeFont() {
        if (!loadOk) {
            isChangeStatus = true;
            return;
        } else if (ConstData.getAppId() != 20 && ConstData.getAppId() != 1) {
            isChangeStatus = true;
            if (DataHelper.getFontSize(mContext) == 1) {
                if (this.getSettings().getTextSize() != TextSize.NORMAL) {
                    this.getSettings().setTextSize(TextSize.NORMAL);
                }
            } else {
                if (this.getSettings().getTextSize() != TextSize.LARGER) {
                    this.getSettings().setTextSize(TextSize.LARGER);
                }
            }
            return;
        }
        String url = "javascript:setFontSize(" + DataHelper.getFontSize(mContext) + ")";
        isChangeStatus = true;
        this.loadUrl(url);
    }

    /**
     * 改变行间距
     */
    public void changeLineHeight() {
        if (!loadOk || (ConstData.getAppId() != 20 && ConstData.getAppId() != 1)) {
            isChangeStatus = true;
            return;
        }
        String url = "javascript:setLineHeight(" + DataHelper.getLineHeight(mContext) + ");";
        isChangeStatus = true;
        this.loadUrl(url);
    }

    /**
     * push动画
     */
    public void push() {
        if (!loadOk || hasPush) {
            isChangeStatus = true;
            return;
        }
        // System.out.println(detail.getTitle() + "===push");
        //        String url = "javascript:iSlate.push()";
        isChangeStatus = true;
        hasPush = true;
        //        this.loadUrl(url);
    }

    /**
     * pop动画
     */
    public void pop() {
        if (!loadOk) {
            isChangeStatus = true;
            return;
        }
        // System.out.println(detail.getTitle() + "===pop");
        hasPush = false;
        //        String url = "javascript:iSlate.pop()";
        isChangeStatus = true;
        //        this.loadUrl(url);
    }

    /**
     * 长按选中段落
     */
    private void onLongClick(int x, int y) {
        if (detail != null && detail.getProperty().getHavecard() == 1) {
//            String makeCard = ModernMediaTools.getMakeCard(mContext, x, y);
//            String url = "javascript:window.make.make(" + makeCard + ")";
//            this.loadUrl(url);

            if (checkLogin()) {
                String arg = ModernMediaTools.getMakeCard(mContext, x, y);
                String url = "javascript:window.test.test(" + arg + ")";
                this.loadUrl(url);
            }


        }
    }
    /**
     * 判断是否登录
     *
     * @param
     * @return
     */
    private boolean checkLogin() {


        if (SlateDataHelper.getUserLoginInfo(mContext) == null) {
//            UserPageTransfer.gotoLoginActivityRequest(mContext, -1);
            Intent intent = new Intent();
            intent.setAction("cn.com.modernmediausermodel.login");
            mContext.sendBroadcast(intent);
            return false;
        }
        return true;
    }
    /**
     * 获取选中图片的src
     *
     * @param x
     * @param y
     */
    private void getImageSrc(int x, int y) {
        if (ConstData.getInitialAppId() != 20 && ConstData.getAppId() != 1) return;
        //		this.loadUrl("javascript:window.getSrc.getSrc("
        //				+ ModernMediaTools.getImageSrc(mContext, x, y) + ")");

        gotoGalleryActivity(urlList, "", descList);
    }

    /**
     * 获取分享信息
     */

    private void getShareMessage() {
        this.loadUrl("javascript:window.getShareMsg.getShareMsg(" + ModernMediaTools.getMeta() + ")");
    }

    /**
     * 把段落转成文章
     *
     * @param json
     * @return
     */
    private ArticleItem getCard(String json) {
        if (TextUtils.isEmpty(json)) return null;
        try {
            JSONObject obj = new JSONObject(json);
            if (JSONObject.NULL.equals(obj) || obj == null) {
                return null;
            }
            ArticleItem item = new ArticleItem();
            item.setDesc(obj.optString("content"));
            item.setArticleId(obj.optInt("articleid"));
            return item;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    /**
     * 显示状态
     *
     * @param type
     */
    private void showErrorType(final int type) {
        isError = type == 2;
        if (listener != null) handler.post(new Runnable() {

            @Override
            public void run() {
                listener.showStyle(type);
            }
        });
    }

    public void setSlateWeb(boolean isSlateWeb) {
        this.isSlateWeb = isSlateWeb;
    }


    public void setPushArticle(boolean isPushArticle) {
        this.isPushArticle = isPushArticle;
    }

    public void setShowColumn(boolean isShowColumn) {
        this.isShowColumn = isShowColumn;
    }

    public void setLoadListener(WebViewLoadListener listener) {
        this.mLoadListener = listener;
    }

    /**
     * 栏目内容是网页时，需判断是否是起始栏目内容，若不是，点击返回键时则返回到上一级内容，直至起始栏目内容
     *
     * @return
     */
    public boolean doGoBack() {
        boolean goBack = true;
        int steps = computeBackSteps();
        if (steps < 0 && canGoBackOrForward(steps)) {
            goBackOrForward(steps);
        } else {
            goBack = false;
        }
        return goBack;
    }

    /**
     * 计算应该回退的步数
     *
     * @return
     */
    public int computeBackSteps() {
        WebBackForwardList list = copyBackForwardList();
        int steps = 0;
        int current = list.getCurrentIndex();
        int index = current;
        if (list.getItemAtIndex(current).getUrl().startsWith(ERROR_WEB)) { // 当前项是错误页面
            while (index >= 0) {
                WebHistoryItem item = list.getItemAtIndex(index);
                if (item.getUrl().startsWith(ERROR_WEB)) {
                    steps -= 2;
                    index -= 2;
                } else {
                    index -= 1;
                }
            }
        } else {
            steps -= 1;
        }
        PrintHelper.print("steps:" + steps);
        return steps;
    }

    /**
     * 当webview没有加载到html时，通过http获取
     */
    private void getHtmlIfNull() {
        if (detail == null) {
            showErrorType(2);
            return;
        }
        String uri = "";
        if (ParseUtil.listNotNull(detail.getPageUrlList())) {
            uri = detail.getPageUrlList().get(0).getUrl();
        }
        if (TextUtils.isEmpty(uri)) {
            showErrorType(2);
            return;
        }
        final String url = uri;
        HttpURLConnection conn = null;
        URL mUrl = null;
        try {
            mUrl = new URL(uri);
            Log.e("流量bug查询**", "CommonWebView:getHtmlIfNull()" + "-----" + url);
            conn = (HttpURLConnection) mUrl.openConnection();
            conn.setConnectTimeout(10 * 10000);
            conn.setReadTimeout(10 * 10000);

            // 添加头部信息
            Map<String, String> headerMap = ModernMediaTools.getHttpHeaders(mContext);

            Iterator<String> iterator = headerMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = headerMap.get(key);
                if (!TextUtils.isEmpty(key)) {
                    conn.setRequestProperty(key, value);
                }
            }

            int status = conn.getResponseCode();
            if (status == 200) {
                InputStream is = conn.getInputStream();
                if (is == null) {
                    showErrorType(2);
                    return;
                }
                final String data = receiveData(is);
                if (TextUtils.isEmpty(data)) {
                    showErrorType(2);
                    return;
                }
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        me.loadDataWithBaseURL(url, data, "text/html", HTTP.UTF_8, null);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorType(2);
        } finally {
            if (conn != null) conn.disconnect();
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
            if (result == null) return null;
            return new String(result);
        } finally {
            if (is != null) is.close();
            if (baos != null) baos.close();
        }
    }


}