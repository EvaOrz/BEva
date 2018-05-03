package cn.com.modernmedia.vrvideo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.asha.vrlib.MDVRLibrary;

import cn.com.modernmedia.R;

/**
 * using MD360Renderer
 * <p>
 * Created by hzqiujiadi on 16/1/22.
 * hzqiujiadi ashqalcn@gmail.com
 */
public abstract class MD360PlayerActivity extends Activity implements View.OnClickListener {

    public ImageView playOrPause;
    public SeekBar volumn;
    public RelativeLayout menu;
    private int ifshowMenu = 0;// 防menu狂点handler混乱flag

    public static void startVideo(Context context, Uri uri) {
        start(context, uri, VRVideoPlayerActivity.class);
    }

    public static void startBitmap(Context context, Uri uri) {
        start(context, uri, BitmapPlayerActivity.class);
    }

    private static void start(Context context, Uri uri, Class<? extends Activity> clz) {
        Intent i = new Intent(context, clz);
        i.setData(uri);
        context.startActivity(i);
    }

    private MDVRLibrary mVRLibrary;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // set content view
        setContentView(R.layout.activity_md_multi);

        // init VR Library
        mVRLibrary = createVRLibrary();

        // touch event
        findViewById(R.id.touchFix).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mVRLibrary.handleTouchEvent(event);
            }
        });

        // interactive mode switcher
        final ImageView interactiveModeSwitcher = (ImageView) findViewById(R.id.vr_shuangping);
        interactiveModeSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVRLibrary.switchInteractiveMode(MD360PlayerActivity.this);
                updateInteractiveModeText(interactiveModeSwitcher);
            }
        });
        updateInteractiveModeText(interactiveModeSwitcher);

        // display mode switcher
        final ImageView displayModeSwitcher = (ImageView) findViewById(R.id.vr_zhongli);
        displayModeSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVRLibrary.switchDisplayMode(MD360PlayerActivity.this);
                updateDisplayModeText(displayModeSwitcher);
            }
        });
        updateDisplayModeText(displayModeSwitcher);

        menu = (RelativeLayout) findViewById(R.id.vr_menu);
        playOrPause = (ImageView) findViewById(R.id.vr_play_pause);
        volumn = (SeekBar) findViewById(R.id.vr_volumn);


        findViewById(R.id.surface_view1).setOnClickListener(this);
        findViewById(R.id.surface_view2).setOnClickListener(this);
        shownMenu();
    }

    private void updateDisplayModeText(ImageView button) {
        String text = null;
        switch (mVRLibrary.getDisplayMode()) {
            case MDVRLibrary.DISPLAY_MODE_NORMAL://单屏
                text = "NORMAL";
                button.setImageResource(R.drawable.vr_danping);
                break;
            case MDVRLibrary.DISPLAY_MODE_GLASS:// 双屏
                button.setImageResource(R.drawable.vr_shuangping);
                text = "GLASS";
                break;
        }
        //        if (!TextUtils.isEmpty(text)) button.setText(text);
    }

    /**
     * 更新模式状态
     *
     * @param button
     */
    private void updateInteractiveModeText(ImageView button) {
        String text = null;
        switch (mVRLibrary.getInteractiveMode()) {
            case MDVRLibrary.INTERACTIVE_MODE_MOTION:
                button.setImageResource(R.drawable.vr_zhongli);
                text = "MOTION";
                break;
            case MDVRLibrary.INTERACTIVE_MODE_TOUCH:
                button.setImageResource(R.drawable.vr_chumo);
                text = "TOUCH";
                break;
        }
        //        if (!TextUtils.isEmpty(text)) button.setText(text);
    }

    /**
     * 隐藏menu
     */
    public void unshownMenu() {
        if (menu.isShown()) {
            menu.setVisibility(View.GONE);
        }
    }

    public void shownMenu() {
        menu.setVisibility(View.VISIBLE);
        Message m = new Message();
        m.arg1 = ifshowMenu;
        m.what = 300;
        handler.sendMessageDelayed(m, 5000);
        ifshowMenu++;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 + 1 == ifshowMenu) {
                unshownMenu();
            }

        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.surface_view1 || view.getId() == R.id.surface_view2) {
            if (menu.isShown())
                unshownMenu();
            else
                shownMenu();
        }
    }

    abstract protected MDVRLibrary createVRLibrary();

    @Override
    protected void onResume() {
        super.onResume();
        mVRLibrary.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVRLibrary.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVRLibrary.onDestroy();
    }

    protected Uri getUri() {
        Intent i = getIntent();
        if (i == null || i.getData() == null) {
            return null;
        }
        return i.getData();
    }

    public void cancelBusy() {
        findViewById(R.id.progress).setVisibility(View.GONE);

    }
}