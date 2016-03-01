package cn.com.modernmedia.views.util;

import java.util.HashMap;

import net.tsz.afinal.core.AsyncTask;
import android.content.Context;
import android.database.CursorJoiner.Result;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.VideoView;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.xmlparse.FunctionXML;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediaslate.unit.VideoFileManager;

/**
 * 焦点图、列表播放视频帮助类
 * 
 * @author zhuqiao
 *
 */
public class PlayVideoHelper {
	public static final int IDLE = 0;// 空闲
	public static final int PLAYING = 1;// 播放中
	public static final int PAUSING = 2;// 暂停中

	private Context mContext;
	private int state = IDLE;

	private HashMap<String, View> currPlayerMap;// 模板
	private ArticleItem currPlayerItem;// view对应的数据原型

	public PlayVideoHelper(Context context) {
		mContext = context;
	}

	public void pauseVideo(boolean goneVideoView) {
		pauseVideo(goneVideoView, false);
	}

	/**
	 * 暂停播放
	 */
	private void pauseVideo(boolean goneVideoView, boolean forceStop) {
		// if (!forceStop) {
		// if (state == PAUSING) {
		// return;
		// }
		// }
		if (currPlayerMap != null) {
			VideoView videoView = (VideoView) currPlayerMap
					.get(FunctionXML.VIDEO_VIEW);
			videoView.pause();
			if (goneVideoView) {
				videoView.setVisibility(View.GONE);
			} else
				videoView.setVisibility(View.VISIBLE);
			checkAudio(true);

		}
		state = PAUSING;
		showSwitch();
	}

	/**
	 * 开始播放
	 * 
	 * @param map
	 *            存放view的键值对map
	 * @param item
	 *            文章数据原型
	 */
	public void startVideo(final HashMap<String, View> map,
			final ArticleItem item) {
		final VideoView videoView = (VideoView) map.get(FunctionXML.VIDEO_VIEW);
		videoView.setVisibility(View.VISIBLE);
		try {
			final String path = item.getPicList().get(0).getVideolink();
			final String pathx = saveVideo(path) ? VideoFileManager
					.getVideoPath(path) : path;
			videoView.setVideoPath(pathx);

			currPlayerMap = map;
			setVisibilityForOther(true);
			intSwitch(videoView);
			/* 点击视频显示操作按钮，播放中2s后消失，暂停状态持续 */
			videoView.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (state == PLAYING)
						showSwitch();
					return false;
				}
			});

			videoView.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					currPlayerItem = item;
					setVisibilityForOther(false);
					mp.setLooping(true);
					checkAudio(false);
					/**
					 * 3.1.0{wifi下允许自动播放}
					 */
					if (Tools.isWiFiConnected(mContext)
							&& DataHelper.getWiFiAutoPlayVedio(mContext)) {
						videoView.start();
						state = PLAYING;
						showSwitch();
					}

				}
			});
			videoView.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					// 3.1.2线上bug 需要注释这段
					// mp.release();
					// mp = MediaPlayer.create(mContext, what);
					VideoFileManager.deleteVideo(path);
					videoView.setVideoPath(path);
					return true;
				}
			});

			/**
			 * 循环播放
			 */
			videoView.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// boolean i = saveVideo(path);
					// if (i) {
					// videoView.setVideoPath(VideoFileManager
					// .getVideoPath(path));
					// } else
					// videoView.setVideoPath(path);
					// videoView.start();
					// showSwitch(swith, true);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 每次重新开启一个视频的时候，静音
		V.muteAudio(mContext, true, false, true);

	}

	private ImageView intSwitch(final VideoView videoView) {
		if (!(currPlayerMap.get(FunctionXML.VIDEO_SWITCH) instanceof ImageView))
			return null;
		final ImageView swich = (ImageView) currPlayerMap
				.get(FunctionXML.VIDEO_SWITCH);
		swich.setVisibility(View.VISIBLE);
		swich.setImageResource(R.drawable.video_play);
		swich.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (state == PAUSING) {
					resumeVideo();
				} else if (state == IDLE) {// 初始化 --- 开始播放
					videoView.start();
					state = PLAYING;
					showSwitch();
				} else if (state == PLAYING) {// 播放中 --- 暂停
					pauseVideo(false);
				}

			}
		});
		return swich;
	}

	public void showSwitch() {
		if (currPlayerMap != null) {
			final ImageView swich = (ImageView) currPlayerMap
					.get(FunctionXML.VIDEO_SWITCH);
			swich.setVisibility(View.VISIBLE);
			if (state == PLAYING) {// 显示播放
				swich.setImageResource(R.drawable.video_pause);
				/* 两秒后消失 */
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (state == PLAYING)
							swich.setVisibility(View.GONE);
					}
				}, 2000);
			} else {// 暂停
				swich.setImageResource(R.drawable.video_play);
			}
		}
	}

	/**
	 * 继续播放
	 */
	public void resumeVideo() {
		if (currPlayerMap != null) {
			VideoView videoView = (VideoView) currPlayerMap
					.get(FunctionXML.VIDEO_VIEW);
			videoView.setVisibility(View.VISIBLE);
			// 如果是继续播放,那么直接start
			setVisibilityForOther(false);
			try {
				videoView.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			checkAudio(false);
		}
		state = PLAYING;
		showSwitch();
	}

	public void stopVideo() {
		pauseVideo(true, true);
		currPlayerMap = null;
		currPlayerItem = null;
		state = IDLE;
	}

	private void checkAudio(boolean gone) {
		if (!(currPlayerMap.get(FunctionXML.AUDIO_IMG) instanceof ImageView))
			return;
		ImageView audio = (ImageView) currPlayerMap.get(FunctionXML.AUDIO_IMG);
		if (gone) {
			audio.setVisibility(View.GONE);
		} else {
			audio.setVisibility(View.VISIBLE);
			if (V.isMute) {
				audio.setImageResource(R.drawable.mute);
			} else {
				audio.setImageResource(R.drawable.volume);
			}
		}
	}

	private void setVisibilityForOther(boolean show) {
		if (currPlayerMap == null) {
			return;
		}
		if (currPlayerMap.containsKey(FunctionXML.IMAGE)) {
			currPlayerMap.get(FunctionXML.IMAGE).setVisibility(
					show ? View.VISIBLE : View.GONE);
		}
		if (currPlayerMap.containsKey(FunctionXML.ADV_IMAGE)) {
			currPlayerMap.get(FunctionXML.ADV_IMAGE).setVisibility(
					show ? View.VISIBLE : View.GONE);
		}
		if (currPlayerMap.containsKey(FunctionXML.VIDEO_IMG)) {
			if (currPlayerItem != null
					&& currPlayerItem.getProperty().getHasvideo() == 1 && show) {
				currPlayerMap.get(FunctionXML.VIDEO_IMG).setVisibility(
						View.VISIBLE);
			} else {
				currPlayerMap.get(FunctionXML.VIDEO_IMG).setVisibility(
						View.GONE);
			}
		}
	}

	public HashMap<String, View> getCurrPlayerMap() {
		return currPlayerMap;
	}

	public void setCurrPlayerMap(HashMap<String, View> currPlayerMap) {
		this.currPlayerMap = currPlayerMap;
	}

	public ArticleItem getCurrPlayerItem() {
		return currPlayerItem;
	}

	public void setCurrPlayerItem(ArticleItem currPlayerItem) {
		this.currPlayerItem = currPlayerItem;
	}

	public int getState() {
		return state;
	}

	private boolean saveVideo(final String path) {
		Boolean flag = VideoFileManager.getVideoExit(path);
		if (flag)
			return true;
		else {
			Log.e("下载视频广告任务", path);
			AsyncTask<String, Integer, Result> task = new AsyncTask<String, Integer, Result>() {

				@Override
				protected Result doInBackground(String... params) {
					VideoFileManager.saveVideo(path, mContext);
					return null;
				}

				/* UI线程级别操作，TODO未完成下载时的操作 */
				@Override
				protected void onCancelled() {
					super.onCancelled();
					VideoFileManager.deleteVideo(path);
				}

				@Override
				protected void onPostExecute(Result result) {
					super.onPostExecute(result);
				}

			};
			task.execute(new String());
			return false;
		}

	}

}
