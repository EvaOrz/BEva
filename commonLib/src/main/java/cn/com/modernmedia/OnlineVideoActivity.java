package cn.com.modernmedia;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import cn.com.modernmedia.widget.FullVideoView;
import cn.jzvd.JZVideoPlayer;


/**
 * Created by Eva. on 16/8/16.
 * 直播测试地址
 */
public class OnlineVideoActivity extends BaseActivity {

    /**
     * TODO: Set the path variable to a streaming video URL or a local media file
     * path.
     */
    private String path = "";
    private FullVideoView mVideoView;// 全屏页面不需要判断权限，所以不继承FullVideoView

    public interface OnBackButtonClickListener {
        public void onBackClick();
    }

    /**
     * TODO: Set the path variable to a streaming video URL or a local media file
     * path.
     */

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);


        setContentView(R.layout.activity_online_video);
        mVideoView = findViewById(R.id.surface_view);

        path = getIntent().getStringExtra("vpath");
        mVideoView.setUp(path, JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN, new OnBackButtonClickListener() {
            @Override
            public void onBackClick() {
                finish();
            }
        });
        mVideoView.fullscreenButton.setVisibility(View.GONE);// 禁止横竖屏切换
        mVideoView.startVideo();

    }


    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.goOnPlayOnPause();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public String getActivityName() {
        return OnlineVideoActivity.class.getName();
    }

    @Override
    public void reLoadData() {

    }

}
