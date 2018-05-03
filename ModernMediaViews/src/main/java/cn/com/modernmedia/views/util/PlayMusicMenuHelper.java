package cn.com.modernmedia.views.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.MusicService.MusicBinder;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.widget.GifView;

/*
 * 全局音乐播放munu Helper
 */
public class PlayMusicMenuHelper implements OnClickListener {
	private Context mContext;
	private ArticleItem currPlayerItem;// view对应的数据原型
	private static View view;
	private GifView status;
	private ImageView stop, playOrPause;
	private TextView title;
	private MusicBinder musicPlayBinder;// 音乐播放控制binder

	public PlayMusicMenuHelper(Context context, LinearLayout parent) {
		this.mContext = context;
		view = LayoutInflater.from(context).inflate(R.layout.music_menu_view,
				parent, false);
		initView();
		initdata();
	}

	public void initdata() {
		if (CommonApplication.musicService == null)
			return;
		musicPlayBinder = CommonApplication.musicService.musicPlayBinder;
		if (musicPlayBinder == null)
			return;
		currPlayerItem = musicPlayBinder.getCurrentArt();
		if (currPlayerItem == null)
			return;
		startMusic();
	}

	private void initView() {
		status = (GifView) view.findViewById(R.id.music_menu_status);
		stop = (ImageView) view.findViewById(R.id.music_menu_stop);
		playOrPause = (ImageView) view.findViewById(R.id.music_menu_play_pause);
		title = (TextView) view.findViewById(R.id.music_menu_title);
		status.setOnClickListener(this);
		stop.setOnClickListener(this);
		playOrPause.setOnClickListener(this);

	}

	public void setData(ArticleItem amo) {
		this.currPlayerItem = amo;
	}

	public View getMenuView() {
		return view;
	}

	/**
	 * 开始播放
	 */
	public void startMusic() {
		title.setText(currPlayerItem.getTitle());
		title.findFocus();// 获得焦点
		status.setMovieResource(R.raw.music_playing);
	}

	public void playOrPauseMusic() {
		if (musicPlayBinder == null)
			return;
		if (musicPlayBinder.isPlaying()) {
			musicPlayBinder.pause();
			playOrPause.setImageResource(R.drawable.music_pause);
		} else {
			musicPlayBinder.resume();
			playOrPause.setImageResource(R.drawable.music_play);
		}
	}

	public void stopMusic() {
		if (musicPlayBinder == null)
			return;
		musicPlayBinder.stop();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.music_menu_status) {// gif图

		} else if (v.getId() == R.id.music_menu_stop)
			stopMusic();
		else if (v.getId() == R.id.music_menu_play_pause) {
			playOrPauseMusic();
		}
	}
	// WindowManager wm = (WindowManager) this
	// .getSystemService(Context.WINDOW_SERVICE);
	// WindowManager.LayoutParams params = new WindowManager.LayoutParams();
	//
	// params.gravity = Gravity.LEFT | Gravity.BOTTOM; // 调整悬浮窗口至右侧中间
	// // 以屏幕左上角为原点，设置x、y初始值
	// params.x = 40;
	// params.y = 40;
	// // 设置悬浮窗口长宽数据
	// params.width = WindowManager.LayoutParams.WRAP_CONTENT;
	// ;
	// params.height = WindowManager.LayoutParams.WRAP_CONTENT;
	// ;
	//
	// // 设置window type
	// params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
	// /*
	// * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE;
	// 那么优先级会降低一些,
	// * 即拉下通知栏不可见
	// */
	//
	// // params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
	//
	// /*
	// * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。 |
	// * LayoutParams.FLAG_NOT_TOUCHABLE;
	// */
	// params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
	// | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

}
