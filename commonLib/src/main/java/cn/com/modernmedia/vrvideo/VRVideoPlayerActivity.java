package cn.com.modernmedia.vrvideo;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.asha.vrlib.MDVRLibrary;
import com.umeng.analytics.MobclickAgent;

import cn.com.modernmedia.R;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by hzqiujiadi on 16/4/5.
 * hzqiujiadi ashqalcn@gmail.com
 */
public class VRVideoPlayerActivity extends MD360PlayerActivity {

    private MediaPlayerWrapper mMediaPlayerWrapper = new MediaPlayerWrapper();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaPlayerWrapper.init();
        mMediaPlayerWrapper.setPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                playOrPause.setImageResource(R.drawable.vr_play);
                cancelBusy();
            }
        });

        Uri uri = getUri();
        if (uri != null) {
            mMediaPlayerWrapper.openRemoteFile(uri.toString());
            mMediaPlayerWrapper.prepareAsync();
        }

        playOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlayerWrapper.isPlaying()) {
                    mMediaPlayerWrapper.pause();
                    playOrPause.setImageResource(R.drawable.vr_pause);
                } else {
                    mMediaPlayerWrapper.play();
                    playOrPause.setImageResource(R.drawable.vr_play);
                }

            }
        });
        final AudioManager mAudioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumn.setMax(7);
        volumn.setProgress(4);
        mAudioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE * 4, 0);
        volumn.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.e("seekbar", i + "");
                mAudioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE * i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    @Override
    protected MDVRLibrary createVRLibrary() {
        return MDVRLibrary.with(this).displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL).interactiveMode(MDVRLibrary.INTERACTIVE_MODE_MOTION).video(new MDVRLibrary.IOnSurfaceReadyCallback() {
            @Override
            public void onSurfaceReady(Surface surface) {
                mMediaPlayerWrapper.getPlayer().setSurface(surface);
            }
        }).ifNotSupport(new MDVRLibrary.INotSupportCallback() {
            @Override
            public void onNotSupport(int mode) {
                String tip = mode == MDVRLibrary.INTERACTIVE_MODE_MOTION ? "onNotSupport:MOTION" : "onNotSupport:" + String.valueOf(mode);
                Toast.makeText(VRVideoPlayerActivity.this, tip, Toast.LENGTH_SHORT).show();
            }
        }).build(R.id.surface_view1, R.id.surface_view2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayerWrapper.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMediaPlayerWrapper.onStop();
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
