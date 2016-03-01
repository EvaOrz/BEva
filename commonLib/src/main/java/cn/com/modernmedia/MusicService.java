package cn.com.modernmedia;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmediaslate.unit.DateFormatTool;

/**
 * 音乐播放service
 * 
 * @author lusiyuan
 *
 */
public class MusicService extends Service {
	// 全局音乐播放器
	public static MediaPlayer mediaPlayer;

	// Binder
	public MusicBinder musicPlayBinder = new MusicBinder();

	public boolean isPlaying = false, isLoop = false;
	public ArticleItem currentArticleItem;

	// 专辑播放列表数据储存
	public static List<ArticleItem> datas;

	@Override
	public IBinder onBind(Intent intent) {
		CommonApplication.musicService = this;
		if (mediaPlayer != null) {
			mediaPlayer.release();
			isPlaying = false;
			mediaPlayer = null;
		}
		currentArticleItem = (ArticleItem) intent
				.getSerializableExtra("play_model");
		if (currentArticleItem != null)
			musicPlayBinder.prepare(currentArticleItem.getAudioList().get(0)
					.getUrl());
		return musicPlayBinder;
	}

	/**
	 * 播放控制Binder
	 * 
	 * @author lusiyuan
	 * 
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
				mediaPlayer = new MediaPlayer();
			}
			try {
				mediaPlayer.setDataSource(path);
				mediaPlayer.prepareAsync();
				mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
					@SuppressLint("UseValueOf")
					@Override
					public void onPrepared(MediaPlayer mp) {
						int duration = mp.getDuration();
						currentArticleItem.getAudioList().get(0)
								.setDuration(DateFormatTool.getTime(duration));
						// 开始播放向页面发送播放message
						Message m = new Message();
						m.arg1 = duration;
						m.what = MusicActivity.MSG_START_PLAY;
						sendMessage(m);
					};
				});

				// mediaPlayer控视频播放完成出发的事件
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						isPlaying = false;
						isCompleted = true;
						isLoop = false;
						Message msg = new Message();
						msg.what = MusicActivity.MSG_COMPLETE;
						sendMessage(msg);
						notifyActivity();
						Log.e("setOnCompletionListener", "完成播放");
					}
				});
				mediaPlayer
						.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

							@Override
							public void onBufferingUpdate(MediaPlayer arg0,
									int arg1) {
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
		 * 开始播放
		 */
		public void start() {
			if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
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
				mediaPlayer = null;
				isPlaying = false;
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
			if (mediaPlayer != null)
				mediaPlayer.setLooping(b); // 设定循环模式
			Log.e("改变循环状态", b ? "循环" : "不循环");
		}

		/**
		 * 1：单曲循环 0：顺序播放
		 * 
		 * @return
		 */
		public int getCurrentMode() {
			if (isLoop)
				return 1;
			else
				return 0;
		}

		/**
		 * 通知MainActivity，播放情况
		 */
		@SuppressLint("UseValueOf")
		public void notifyActivity() {
			Message msg = new Message();
			msg.obj = isPlaying;
			msg.what = 0;
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

		public void reset() {
			if (mediaPlayer != null) {
				if (isPlaying) {
					mediaPlayer.stop();
				}
				isPlaying = false;
				isCompleted = false;
				mediaPlayer.release();
				mediaPlayer = new MediaPlayer();
				cancelNotification();
			}
		}

		/**
		 * 注册回调接口的方法，供外部调用
		 * 
		 * @param onProgressListener
		 */
		public void setOnProgressListener(OnProgressListener p) {
			this.onProgressListener = p;
		}

		// 循环
		Handler handler = new Handler();
		Runnable runnable = new Runnable() {
			public void run() {
				// if (mediaPlayer != null && isPlaying)// 如果在播放，屏幕常亮
				// mediaPlayer.setScreenOnWhilePlaying(true);
				toUpdateProgress();
			}
		};

		private void toUpdateProgress() {
			if (mediaPlayer != null && isPlaying) {
				int progress = mediaPlayer.getCurrentPosition();
				onProgressListener.OnProgressChangeListener(progress);
				handler.postDelayed(runnable, 100);
			}
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
	 * 进度回调接口
	 * 
	 * @author lusiyuan
	 * 
	 */
	public interface OnProgressListener {
		public void OnProgressChangeListener(int progress);
	}

	private void sendMessage(Message msg) {
		CommonApplication.musicActivity.handler.sendMessage(msg);
	}

	/**
	 * 取消notification
	 */
	public void cancelNotification() {
		NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notiManager.cancel(0);
	}

}
