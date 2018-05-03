package cn.com.modernmedia;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import java.io.IOException;

import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.GenericConstant;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmediaslate.unit.TimeCollectUtil;

/**
 * 广告页面
 *
 * @author ZhuQiao
 */
@SuppressLint("SetJavaScriptEnabled")
public class AdvActivity extends BaseActivity {
    public static final int SHOW_WEB = 1;
    public static final int SHOW_VIDEO = 2;
    public static final int GO_TO_MAIN = 3;

    private WebView webView;
    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private AdvItem adv;
    private String fileName;

    private boolean canToMain = true;

    @Override
    protected void onResume() {
        super.onResume();
        //如果不可以tomain,说明是被中断，立刻tomain
        if (!canToMain) gotoMain();
    }

    private void gotoMain() {
        Log.e("AdvActivity", "进入首页");

        Intent intent = new Intent(AdvActivity.this, CommonApplication.mainCls);
        intent.putExtra(GenericConstant.FROM_ACTIVITY, GenericConstant.FROM_ACTIVITY_VALUE);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.alpha_out_1s, R.anim.alpha_in_1s);
    }


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_WEB:

                    webView.loadUrl(fileName);
                    break;

                case SHOW_VIDEO:
                    webView.setVisibility(View.GONE);
                    //                    videoView.setVisibility(View.VISIBLE);
                    //                    videoView.setVideoPath(fileName);
                    //                    videoView.start();
                    surfaceView.setVisibility(View.VISIBLE);
                    try {
                        mediaPlayer.setDataSource(AdvActivity.this, Uri.parse(fileName));

                        mediaPlayer.prepare();
                    } catch (IOException e) {

                    }

                    break;
                case GO_TO_MAIN:
                    if (canToMain) gotoMain();
                    break;

            }
        }

    };

    private void choose() {
        final String url = adv.getSourceList().get(0).getUrl();
        final String foldName = ModernMediaTools.getPackageFolderName(url);
        // 根目录
        String baseUrl = "file://" + FileManager.getPackageNameByUrl(foldName) + "/";
        String htmlFile = FileManager.getHtmlName(url);
        String videoFile = FileManager.getVideoName(url);
        if (videoFile != null) {// 播放视频
            fileName = baseUrl + videoFile;
            handler.sendEmptyMessage(SHOW_VIDEO);
        } else {//展示网页
            fileName = baseUrl + htmlFile;
            handler.sendEmptyMessage(SHOW_WEB);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        canToMain = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 启动硬件加速
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        setContentView(R.layout.adv_activity);

        surfaceView = (SurfaceView) findViewById(R.id.adv_videoview);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // 首先取得video的宽和高
                int vWidth = mp.getVideoWidth();
                int vHeight = mp.getVideoHeight();

                // 如果video的宽或者高超出了当前屏幕的大小，则要进行缩放
                float wRatio = (float) vWidth / (float) CommonApplication.width;
                float hRatio = (float) vHeight / (float) CommonApplication.height;

                // 选择大的一个进行缩放
                float ratio = Math.max(wRatio, hRatio);
                vWidth = (int) Math.ceil((float) vWidth / ratio);
                vHeight = (int) Math.ceil((float) vHeight / ratio);

                // 设置surfaceView的布局参数
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(vWidth, vHeight);
                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                surfaceView.setLayoutParams(lp);
                SurfaceHolder holder = surfaceView.getHolder();
                mp.setDisplay(holder);
                mp.start();
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mp.reset();// 4.3.2解决bug用
                mp.release();
                handler.sendEmptyMessage(GO_TO_MAIN);
                return false;
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                handler.sendEmptyMessage(GO_TO_MAIN);
            }
        });

        webView = (WebView) findViewById(R.id.adv_webview);
        WebSettings s = webView.getSettings();
        s.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        s.setLoadWithOverviewMode(true);// 缩放至屏幕大小
        s.setSupportMultipleWindows(false);
        s.setSupportZoom(false);
        s.setPluginState(WebSettings.PluginState.ON);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);// 设置可以访问文件
        s.setJavaScriptEnabled(true);
        s.setJavaScriptCanOpenWindowsAutomatically(false);
        s.setMediaPlaybackRequiresUserGesture(true);
        s.setLoadsImagesAutomatically(true);// 可以自动加载图片
        s.setRenderPriority(WebSettings.RenderPriority.HIGH); // 提高渲染的优先级

        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                // TODO Auto-generated method stub
            }
        });
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });
        webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);

        // 跳出
        findViewById(R.id.adv_imgo).setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View v) {
                                                               handler.sendEmptyMessage(GO_TO_MAIN);
                                                           }
                                                       }

        );
        new Thread() {

            @Override
            public void run() {
                init();
            }
        }.start();

        TimeCollectUtil.getInstance().

                savePageTime(TimeCollectUtil.EVENT_HTML_ADV, true);
    }

    private void init() {
        int duration = ConstData.SPLASH_DELAY_TIME;
        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().getSerializable("IN_APP_ADV") instanceof AdvItem) {
                adv = (AdvItem) getIntent().getExtras().getSerializable("IN_APP_ADV");
                duration = adv.getAutoClose() * 1000;
                if (duration == 0) duration = 4 * 1000;

                choose();
            }
        }
        Log.e("splash duration", duration + "");
        handler.sendEmptyMessageDelayed(GO_TO_MAIN, duration);
    }


    @Override
    public String getActivityName() {
        return AdvActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void reLoadData() {
    }


    @Override
    protected void onStop() {
        handler.sendEmptyMessage(GO_TO_MAIN);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
        TimeCollectUtil.getInstance().savePageTime(TimeCollectUtil.EVENT_HTML_ADV, false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            handler.sendEmptyMessage(GO_TO_MAIN);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
