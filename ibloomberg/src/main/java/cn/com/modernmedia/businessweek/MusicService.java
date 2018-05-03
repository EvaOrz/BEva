package cn.com.modernmedia.businessweek;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import cn.com.modernmedia.businessweek.jingxuan.fm.FmView;
import cn.com.modernmedia.model.ArticleItem;

/**
 * 音乐播放service
 *
 * @author lusiyuan
 */
public class MusicService extends Service {
    // 全局音乐播放器
    public static MediaPlayer mediaPlayer;

    // Binder
    public MusicBinder musicPlayBinder = new MusicBinder();

    public boolean isPlaying = false, isLoop = false;
    public ArticleItem currentArticleItem;


    public static List<ArticleItem> datas;

    private boolean isReleased = false;// 未释放

    @Override
    public IBinder onBind(Intent intent) {
        MyApplication.musicService = this;

        if (mediaPlayer != null) {
            mediaPlayer.release();
            isPlaying = false;
            mediaPlayer = null;
            isReleased = true;
        }
        currentArticleItem = (ArticleItem) intent.getSerializableExtra("play_model");
        if (currentArticleItem != null)
            musicPlayBinder.prepare(currentArticleItem.getAudioList().get(0).getUrl());
        return musicPlayBinder;
    }


    /**
     * 播放控制Binder
     *
     * @author lusiyuan
     */
    public class MusicBinder extends Binder {

        public boolean isCompleted = false;
        /**
         * 更新进度的回调接口
         */
        private OnProgressListener onProgressListener;

        /**
         * 重新启动页面，向页面传递当前播放数据
         */
        public ArticleItem getCurrentArt() {
            return currentArticleItem;
        }

        public void prepare(String path) {

            if (mediaPlayer == null && !isPlaying) { // 新播放音频
                //                MediaController mediaController = new MediaController(MusicService.this);
                mediaPlayer = new MediaPlayer();

                isReleased = false;
            }

            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 开始播放向页面发送播放message
                        Message m = new Message();
                        m.what = FmView.MSG_START_PLAY;
                        onProgressListener.onMessageListener(m);
                    }

                    ;
                });
                mediaPlayer.prepareAsync();

                // mediaPlayer控视频播放完成出发的事件
                mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        isPlaying = false;
                        isCompleted = true;
                        isLoop = false;
                        Message msg = new Message();
                        msg.what = FmView.MSG_COMPLETE;
                        onProgressListener.onMessageListener(msg);
                        notifyActivity();
                    }
                });
                mediaPlayer.setOnInfoListener(new OnInfoListener() {

                    @Override
                    public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
                        return false;
                    }
                });
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected boolean isCompleted() {
            return this.isCompleted;
        }

        /**
         * 开始播放,申请音频焦点
         */
        public void start() {
            if (mediaPlayer != null && !isPlaying) {
                mediaPlayer.start();
                isPlaying = true;
                notifyActivity();
                handler.post(runnable);
            }
        }

        /**
         * 别唱了
         */
        public void pause() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPlaying = false;
                notifyActivity();
            }
        }

        public void resume() {
            if (mediaPlayer != null && !isPlaying) {
                Log.e("resume()", "点击重新播放");
                mediaPlayer.start();
                handler.post(runnable);
                isPlaying = true;
                notifyActivity();
            }
        }

        public void stop() {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                isReleased = true;
                mediaPlayer = null;
                isPlaying = false;
                notifyActivity();
            }
        }

        public boolean isPlaying() {
            return isPlaying;
        }

        /**
         * 后一首
         */
        public void toNext() {
        }

        /**
         * 前一首
         */
        public void toPrevious() {
        }

        /**
         * 改变播放模式
         */
        public void changeMode(boolean b) {
            isLoop = b;
            if (mediaPlayer != null) mediaPlayer.setLooping(b); // 设定循环模式
            Log.e("改变循环状态", b ? "循环" : "不循环");
        }

        /**
         * 1：单曲循环 0：顺序播放
         *
         * @return
         */
        public int getCurrentMode() {
            if (isLoop) return 1;
            else return 0;
        }

        /**
         * 通知MainActivity，播放情况
         */
        @SuppressLint("UseValueOf")
        public void notifyActivity() {
            //            Message msg = new Message();
            //            msg.obj = isPlaying;
            //            msg.what = 0;
            Intent intent = new Intent("android.iweekly.intent.action.Music_change");
            intent.putExtra("playing_status", isPlaying);
            intent.putExtra("playing_item", currentArticleItem);
            sendBroadcast(intent);

        }

        /**
         * 有人拖动Seekbar了，要告诉service去改变播放的位置
         *
         * @param progress
         */
        public void changeProgress(int progress) {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(progress);
            }
        }

        /**
         * 注册回调接口的方法，供外部调用
         *
         * @param p
         */
        public void setOnProgressListener(OnProgressListener p) {
            this.onProgressListener = p;
        }

        // 循环
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                toUpdateProgress();
            }
        };

        private void toUpdateProgress() {
            if (mediaPlayer != null && !isReleased && isPlaying) {
                int progress = mediaPlayer.getCurrentPosition();
                onProgressListener.OnProgressChangeListener(progress);
                handler.postDelayed(runnable, 100);
            }
        }

        public void setIsChanging() {
            isReleased = true;
        }

        /**
         * 获取电台某栏目首页
         */
        public void setArticleItems(List<ArticleItem> a) {
            datas = a;
        }

        public List<ArticleItem> getArticleItems() {
            return datas;
        }

    }


    /**
     * 取消notification
     */
    public void cancelNotification() {
        NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notiManager.cancel(0);
    }

    /**
     * 进度回调接口
     *
     * @author lusiyuan
     */
    public interface OnProgressListener {
        public void OnProgressChangeListener(int progress);

        public void onMessageListener(Message message);

    }

}
