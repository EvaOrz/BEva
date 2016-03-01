package cn.com.modernmedia;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cn.com.modernmedia.MusicService.MusicBinder;
import cn.com.modernmedia.MusicService.OnProgressListener;
import cn.com.modernmedia.adapter.MusicItemAdapter;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType;
import cn.com.modernmediaslate.listener.ImageDownloadStateListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.DateFormatTool;

/**
 * 电台页面
 * 
 * @author lusiyuan
 * 
 */
public class MusicActivity extends BaseActivity implements OnClickListener {

	/**
	 * 播放／暂停
	 */
	public static final int MSG_PLAY_PAUSE = 1;
	/**
	 * 开始播放
	 */
	public static final int MSG_START_PLAY = 2;
	/**
	 * 播放完毕
	 */
	public static final int MSG_COMPLETE = 3;
	/**
	 * 改变时间
	 */
	public static final int MSG_CHANGE_TIME = 4;

	/**
	 * 切换图片
	 */
	public static final int MSG_CHANGE_IMAGE = 5;
	/**
	 * 初始化list data
	 */
	public static final int MSG_INIT_LIST = 6;
	/**
	 * 初始化head data
	 */
	public static final int MSG_INIT_HEAD = 7;
	/**
	 * 循环模式
	 */
	public static final int MSG_LOOP = 8;
	/**
	 * 上一首
	 */
	public static final int MSG_ALBUM_PRE = 9;
	/**
	 * 下一首
	 */
	public static final int MSG_ALBUM_NEXT = 10;
	/**
	 * 恢复播放
	 */
	public static final int MSG_RESTART = 11;

	// view define
	private ListView listView;
	private TextView timeBox;// 已播放时间显示
	private TextView wholeTimeBox;// 长度显示
	private TextView currentTitle;// 正在播放的title
	private ImageView playOrPause;
	private SeekBar seekBar;// 播放进度条
	private MusicItemAdapter adapter;
	private boolean isHaveService = false;
	private MusicBinder musicPlayBinder;
	private ImageView imgBox;
	private ImageView loop;// 循环按钮
	private HorizontalScrollView scrollView;// 子栏目menu
	private List<TextView> checkSelectView = new ArrayList<TextView>();// 记录栏目列表

	// data
	private List<ArticleItem> articleData = new ArrayList<ArticleItem>();
	private List<Integer> flags = new ArrayList<Integer>();// 正在播放flags
	private List<TagInfo> tagInfos;// 栏目列表
	private ArticleItem currentPlayArt;// 正在播放的音频model
	private int currentPos = -1;// 正在播放的音频在列表中位置
	private TagInfo currentTagIndex;
	public int progress;// 播放进度/ 播放长度
	public boolean isLoop = false;
	private boolean isAutoChange = false;// 手动｜｜自动切歌
	private boolean ifFromUser = false;// 进度条拖动 是否来自用户

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music);
		CommonApplication.musicActivity = this;
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		p.height = (int) (d.getHeight() * 0.93); // 高度设置为屏幕的1.0
		p.width = (int) (d.getWidth() * 0.93); // 宽度设置为屏幕的0.8
		p.alpha = 1.0f; // 设置本身透明度
		p.dimAmount = 0.7f; // 设置黑暗度
		getWindow().setAttributes(p);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (initView())
			/* 未播放 */
			if (CommonApplication.musicService == null
					|| MusicService.mediaPlayer == null) {
				clickItem(tagInfos.get(0), 0, false);
				/* 正在播放 */
			} else {
				// initView();
				musicPlayBinder = CommonApplication.musicService.musicPlayBinder;
				listenProgress();// 监听进度
				if (musicPlayBinder != null) {
					// 恢复列表数据
					articleData = musicPlayBinder.getArticleItems();
					handler.sendEmptyMessage(MSG_INIT_LIST);
					// 恢复循环状态
					if (musicPlayBinder.getCurrentMode() == 1) {
						loop.setImageResource(R.drawable.music_loop_single);
						isLoop = true;
					}
					// 获取正在播放数据
					currentPlayArt = musicPlayBinder.getCurrentArt();
					handler.sendEmptyMessage(MSG_RESTART);
					// 恢复top_menu选中
					for (int i = 0; i < tagInfos.size(); i++) {
						if (currentPlayArt.getFromtagname().equals(
								tagInfos.get(i).getTagName()))
							clickItem(tagInfos.get(i), i, true);
					}

					// 恢复正在播放位置
					for (int i = 0; i < articleData.size(); i++) {
						if (currentPlayArt != null
								&& articleData.get(i).getArticleId() == currentPlayArt
										.getArticleId()) {
							currentPos = i;
						}
					}
				}
			}

	}

	/**
	 * 时间字符串 --》 毫秒值
	 * 
	 * @param duration
	 */
	private int getDuration(String duration) {
		int result = 0;
		String[] tt = duration.split(":");
		if (tt.length == 1) {
			result = Integer.valueOf(tt[0]) * 1000;

		} else if (tt.length == 2) {
			result = Integer.valueOf(tt[0]) * 60 * 1000
					+ Integer.valueOf(tt[1]) * 1000;
		} else if (tt.length == 3) {
			result = Integer.valueOf(tt[0]) * 60 * 60 * 1000
					+ Integer.valueOf(tt[1]) * 60 * 1000
					+ Integer.valueOf(tt[3]) * 1000;
		}
		return result;
	}

	private void initData() {
		if (currentTagIndex != null) {
			Log.e("MusicActivity currentTagIndex name",
					currentTagIndex.getTagName());
			OperateController.getInstance(this).getTagIndex(currentTagIndex,
					"", "", null, FetchApiType.USE_HTTP_FIRST,
					new FetchEntryListener() {

						@Override
						public void setData(Entry entry) {
							if (entry instanceof TagArticleList) {
								TagArticleList articleList = (TagArticleList) entry;
								if (!articleList.getMap().isEmpty()) {
									articleData.clear();
									for (ArticleItem a : articleList
											.getArticleList()) {
										articleData.add(a);
									}
									handler.sendEmptyMessage(MSG_INIT_LIST);
									handler.sendEmptyMessage(MSG_INIT_HEAD);
									changeMusic(0);
								}

							}
						}
					});
		}
	}

	public List<Integer> setPlayFlags(int c) {
		flags.clear();
		for (int i = 0; i < articleData.size(); i++) {
			if (i == c)
				flags.add(1);
			else
				flags.add(0);
		}
		return flags;
	}

	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_START_PLAY: // ---------------－开始播放
				currentPlayArt = musicPlayBinder.getCurrentArt();// 更新duration
				if (currentPlayArt.getAudioList() != null
						&& currentPlayArt.getAudioList().size() > 0) {
					seekBar.setMax(getDuration(currentPlayArt.getAudioList()
							.get(0).getDuration()));
					wholeTimeBox.setText(currentPlayArt.getAudioList().get(0)
							.getDuration());
				}
				musicPlayBinder.start();
				playOrPause.setImageResource(R.drawable.music_play);
				adapter = new MusicItemAdapter(MusicActivity.this, articleData,
						setPlayFlags(currentPos));
				listView.setAdapter(adapter);
				Log.e("开始播放 currentPos", currentPos + currentPlayArt.getTitle());
				break;
			case MSG_CHANGE_TIME:// ----------------seekbar拖动
				seekBar.setProgress(progress);
				timeBox.setText(DateFormatTool.getTime(seekBar.getProgress()));
				break;
			case MSG_CHANGE_IMAGE:// ---------------切换图片
				img(currentPlayArt.getPicList().get(0).getUrl(), imgBox);
				break;
			case MSG_INIT_LIST:
				adapter = new MusicItemAdapter(MusicActivity.this, articleData,
						setPlayFlags(-1));
				listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				break;
			case MSG_INIT_HEAD:
				setDataForHead();
				break;
			case MSG_PLAY_PAUSE:
				if (musicPlayBinder == null) {
					sendintent();
				} else {
					if (musicPlayBinder.isPlaying()) {
						musicPlayBinder.pause();
						playOrPause.setImageResource(R.drawable.music_pause);
					} else {
						musicPlayBinder.resume();
						playOrPause.setImageResource(R.drawable.music_play);
					}
				}
				break;
			case MSG_LOOP:// -----------------------循环模式
				if (isLoop) {
					loop.setImageResource(R.drawable.music_loop_order);
				} else {
					loop.setImageResource(R.drawable.music_loop_single);
				}
				isLoop = !isLoop;
				musicPlayBinder.changeMode(isLoop);
				break;
			case MSG_COMPLETE:// -------------------播放完成
				// 最后一曲
				if (currentPos + 1 == articleData.size()) {
					playOrPause.setImageResource(R.drawable.music_pause);
					handler.sendEmptyMessage(MSG_INIT_LIST);
				} else {// 开始下一曲
					if (!isAutoChange) {
						currentPos++;
						changeMusic(currentPos);
					} else {
						isAutoChange = false;
					}
				}
				break;
			case MSG_ALBUM_PRE:// ------------------上一首
				if (currentPos > 0) {
					currentPos--;
					changeMusic(currentPos);
				} else
					showToast("已是列表第一首");
				break;
			case MSG_ALBUM_NEXT:// -----------------下一首
				if (currentPos + 1 < articleData.size()) {
					currentPos++;
					changeMusic(currentPos);
				} else
					showToast("已是列表最后一首");
				break;
			case MSG_RESTART:// 恢复播放
				setDataForHead();
				handler.sendEmptyMessage(MSG_START_PLAY);
				break;
			}
		}
	};

	/**
	 * 主动换歌
	 */
	public void changeMusic(int p) {
		currentPos = p;// *****
		currentPlayArt = articleData.get(p);
		restartService();
		sendintent();// 主动切歌
		isAutoChange = false;
		setDataForHead();
	}

	/**
	 * 切歌重启播放service
	 */
	private void restartService() {
		if (isHaveService && serviceConnection != null)// 如果绑定了service，则解绑
		{
			unbindService(serviceConnection);
			isHaveService = false;
		}
	}

	private void img(String url, final ImageView imageView) {
		SlateApplication.finalBitmap.display(url,
				new ImageDownloadStateListener() {

					@Override
					public void loading() {

					}

					@Override
					public void loadOk(Bitmap bitmap, NinePatchDrawable drawable) {
						FinalBitmap.transforCircleBitmap(bitmap, imageView);
					}

					@Override
					public void loadError() {
					}
				});
	}

	/**
	 * head初始化数据
	 */
	private void setDataForHead() {
		if (currentPlayArt.getPicList() != null
				&& currentPlayArt.getPicList().size() > 0)
			img(currentPlayArt.getPicList().get(0).getUrl(), imgBox);
		// if (currentPlayArt.getAudioList() != null
		// && currentPlayArt.getAudioList().size() > 0) {
		// seekBar.setMax(getDuration(currentPlayArt.getAudioList().get(0)
		// .getDuration()));
		// wholeTimeBox.setText(currentPlayArt.getAudioList().get(0)
		// .getDuration());
		// }

		currentTitle.setText(currentPlayArt.getTitle());
	}

	private boolean initView() {
		timeBox = (TextView) findViewById(R.id.music_play_time);
		imgBox = (ImageView) findViewById(R.id.music_img_box);
		wholeTimeBox = (TextView) findViewById(R.id.music_whole_time);
		loop = (ImageView) findViewById(R.id.music_loop);
		seekBar = (SeekBar) findViewById(R.id.playseekbar);
		playOrPause = (ImageView) findViewById(R.id.music_play_pause);
		currentTitle = (TextView) findViewById(R.id.music_playing_title);
		scrollView = (HorizontalScrollView) findViewById(R.id.music_tag_menu);
		listView = (ListView) findViewById(R.id.music_list);
		adapter = new MusicItemAdapter(this, articleData, setPlayFlags(-1));
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				changeMusic(position);
			}
		});

		scrollView.removeAllViews();
		LinearLayout layout = new LinearLayout(this);
		// setOnClickListener
		findViewById(R.id.music_back).setOnClickListener(this);
		playOrPause.setOnClickListener(this);
		loop.setOnClickListener(this);
		findViewById(R.id.music_previous).setOnClickListener(this);
		findViewById(R.id.music_next).setOnClickListener(this);
		findViewById(R.id.music_menu_left).setOnClickListener(this);
		findViewById(R.id.music_menu_right).setOnClickListener(this);

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar s) {
				if (ifFromUser) {
					int curTime = s.getProgress();
					if (musicPlayBinder != null) {
						musicPlayBinder.changeProgress(curTime);
						musicPlayBinder.resume();
						playOrPause.setImageResource(R.drawable.music_play);
					}
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onProgressChanged(SeekBar arg0, int progress,
					boolean fromUser) {
				ifFromUser = fromUser;
			}
		});

		tagInfos = AppValue.musicColumnLIst.getList();
		if (tagInfos == null || tagInfos.size() == 0) {
			Log.e("AppValue.musicColumnLIst.getList()", "电台数据为空");
			return false;
		}
		for (int i = 0; i < tagInfos.size(); i++) {
			final TagInfo tagInfo = tagInfos.get(i);
			final int position = i;

			LinearLayout cLayout = new LinearLayout(this);
			cLayout.setGravity(Gravity.CENTER_VERTICAL);
			cLayout.setPadding(10, 10, 10, 10);
			TextView child = new TextView(this);
			child.setText(tagInfo.getColumnProperty().getCname());
			child.setPadding(10, 10, 10, 10);
			child.setTextColor(Color.WHITE);
			cLayout.setTag(tagInfo);
			cLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					clickItem((TagInfo) v.getTag(), position, false);
				}
			});

			// ImageView imgChild = new ImageView(this);
			// imgChild.setLayoutParams(new LayoutParams(40, 40));
			// img(DataHelper.columnPicMap.get(tagInfo.getTagName()).get(0),
			// imgChild);
			//
			// cLayout.addView(imgChild);
			cLayout.addView(child);
			layout.addView(cLayout);
			checkSelectView.add(child);
		}
		scrollView.addView(layout);
		return true;
	}

	/**
	 * menu点击事件
	 * 
	 * @param tag
	 * @param position
	 */
	@SuppressWarnings("deprecation")
	private void clickItem(TagInfo tag, int position, boolean isResume) {
		currentTagIndex = tag;
		for (int i = 0; i < checkSelectView.size(); i++) {
			if (i == position)
				checkSelectView.get(i).setBackgroundDrawable(
						getResources().getDrawable(
								R.drawable.round_imageview_red));
			else
				checkSelectView.get(i).setBackgroundDrawable(null);
		}
		if (!isResume)// 点击menu
			initData();
	}

	/**
	 * 绑定intent
	 */
	private void sendintent() {
		if (currentPlayArt.getAudioList() != null
				&& currentPlayArt.getAudioList().size() > 0) {
			Intent intent = new Intent(this, MusicService.class);
			intent.putExtra("play_model", currentPlayArt);
			this.bindService(intent, serviceConnection,
					Context.BIND_AUTO_CREATE);
			isHaveService = true;// 绑定了service
		} else
			showToast("音频数据缺失。");

	}

	/**
	 * Service Binder
	 */
	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			musicPlayBinder = (MusicBinder) service;
			musicPlayBinder.setArticleItems(articleData);
			listenProgress();
		}
	};

	/**
	 * 监听当前播放进度
	 */
	private void listenProgress() {
		if (musicPlayBinder != null)
			musicPlayBinder.setOnProgressListener(new OnProgressListener() {

				@Override
				public void OnProgressChangeListener(int p) {
					if (p > progress)
						handler.sendEmptyMessage(MSG_CHANGE_TIME);
					progress = p;// 更新当前进度
				}
			});
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (isHaveService)// 如果绑定了service，则解绑
		{
			unbindService(serviceConnection);
			isHaveService = false;
		}
	}

	@Override
	public void reLoadData() {

	}

	@Override
	public String getActivityName() {
		return MusicActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return null;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.music_back)
			finish();
		else if (v.getId() == R.id.music_play_pause)
			handler.sendEmptyMessage(MSG_PLAY_PAUSE);
		else if (v.getId() == R.id.music_previous)
			handler.sendEmptyMessage(MSG_ALBUM_PRE);
		else if (v.getId() == R.id.music_next)
			handler.sendEmptyMessage(MSG_ALBUM_NEXT);
		else if (v.getId() == R.id.music_loop)
			handler.sendEmptyMessage(MSG_LOOP);
		else if (v.getId() == R.id.music_menu_left)
			for (int i = 0; i < tagInfos.size(); i++) {
				if (tagInfos.get(i).getTagName()
						.equals(currentTagIndex.getTagName())
						&& i != 0) {
					clickItem(tagInfos.get(i - 1), i - 1, false);
					break;
				}
			}
		else if (v.getId() == R.id.music_menu_right)
			for (int i = 0; i < tagInfos.size(); i++) {
				if (tagInfos.get(i).getTagName()
						.equals(currentTagIndex.getTagName())
						&& i + 1 < tagInfos.size()) {
					clickItem(tagInfos.get(i + 1), i + 1, false);
					break;
				}
			}
	}
}
