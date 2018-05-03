package cn.com.modernmedia.businessweek.jingxuan.fm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.businessweek.MusicService;
import cn.com.modernmedia.businessweek.MusicService.MusicBinder;
import cn.com.modernmedia.businessweek.MusicService.OnProgressListener;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.jingxuan.ShangchengListActivity;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.widget.newrefresh.PullToRefreshLayout;
import cn.com.modernmedia.widget.newrefresh.PullableListView;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.DateFormatTool;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;


/**
 * Created by Eva. on 17/5/17.
 */

public class FmView implements View.OnClickListener {

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
     * 刷新列表选中状态
     */
    public static final int MSG_REFRESH_LIST = 6;
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


    private View view;
    private Context context;

    private OperateController operateController;
    private String fmTop = "";
    // view define
    private PullToRefreshLayout pullToRefreshLayout;
    private PullableListView listView;
    private TextView timeBox;// 已播放时间显示
    private TextView wholeTimeBox;// 长度显示
    private ImageView playOrPause;
    private SeekBar seekBar;// 播放进度条
    private MusicItemAdapter adapter;
    private boolean isHaveService = false;
    private MusicService.MusicBinder musicPlayBinder;
    private ImageView imgBox;
    public String fmTagName = "cat_1712";//FM
    private ArticleItem currentPlayArt;// 正在播放的音频model
    private int currentPos = -1;// 正在播放的音频在列表中位置
    public int progress;// 播放进度/ 播放长度
    private boolean ifFromUser = false;// 进度条拖动 是否来自用户
    private boolean isAutoChange = false;// 手动｜｜自动切歌
    private int musicIng = 0;// 初始化需要播放的fm的articleId

    // data
    private List<ArticleItem> articleData = new ArrayList<ArticleItem>();
    private List<PlayIngStatusModel> fmModels = new ArrayList<>();
    private int showCount;// 是否显示播放次数

    public FmView(Context context, String tagName, int musicIng) {
        this.context = context;
        this.fmTagName = tagName;
        this.musicIng = musicIng;
        operateController = OperateController.getInstance(context);
        initView();
        loadFmData(false, true);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 102://fm加载更多
                    pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    break;

                case MSG_START_PLAY: // ---------------－开始播放
                    currentPlayArt = musicPlayBinder.getCurrentArt();// 更新duration
                    if (currentPlayArt.getAudioList() != null && currentPlayArt.getAudioList().size() > 0) {
                        String duration = currentPlayArt.getAudioList().get(0).getDuration();// 单位s
                        if (!TextUtils.isEmpty(duration)) {
                            seekBar.setMax(Integer.valueOf(duration) * 1000);
                            wholeTimeBox.setText(DateFormatTool.getTime(Long.valueOf(duration) * 1000));
                        }
                    }
                    musicPlayBinder.start();
                    playOrPause.setImageResource(R.drawable.music_play);
                    handler.sendEmptyMessage(MSG_REFRESH_LIST);
                    handler.sendEmptyMessage(MSG_INIT_HEAD);
                    //开始播放，播放次数+1
                    addFmTime(1);

                    Log.e("开始播放 currentPos", currentPos + currentPlayArt.getTitle());
                    break;
                case MSG_CHANGE_TIME:// ----------------seekbar拖动
                    seekBar.setProgress(progress);
                    timeBox.setText(DateFormatTool.getTime(seekBar.getProgress()));
                    break;
                case MSG_CHANGE_IMAGE:// ---------------切换图片
                    img(currentPlayArt.getPicList().get(0).getUrl(), imgBox);
                    break;
                case MSG_REFRESH_LIST:
                    adapter.setList(fmModels, showCount);
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
                            //恢复播放，播放次数+1
                            addFmTime(2);
                        }
                    }
                    break;
                case MSG_COMPLETE:// -------------------播放完成
                    // 最后一曲
                    if (currentPos + 1 == articleData.size()) {
                        playOrPause.setImageResource(R.drawable.music_pause);
                        handler.sendEmptyMessage(MSG_REFRESH_LIST);
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
                    } else Toast.makeText(context, "已是列表第一首", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_ALBUM_NEXT:// -----------------下一首
                    if (currentPos + 1 < articleData.size()) {
                        currentPos++;
                        changeMusic(currentPos);
                    } else Toast.makeText(context, "已是列表最后一首", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_RESTART:// 恢复播放
                    setDataForHead();
                    handler.sendEmptyMessage(MSG_START_PLAY);
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.music_play_pause:
                // 如果当前有播放数据，并且需要付费，则点击去简介页面
                if (currentPlayArt != null && currentPlayArt.getProperty().getLevel() > 0 && !SlateDataHelper.getLevelByType(context, currentPlayArt.getProperty().getLevel())) {
                    if (context instanceof ShangchengListActivity) {
                        ((ShangchengListActivity) context).goInfo();
                    }
                } else handler.sendEmptyMessage(MSG_PLAY_PAUSE);
                break;
            case R.id.music_previous:
                handler.sendEmptyMessage(MSG_ALBUM_PRE);
                break;
            case R.id.music_next:
                handler.sendEmptyMessage(MSG_ALBUM_NEXT);
                break;
        }
    }

    /**
     * 加载专刊数据
     */
    private void loadFmData(final boolean isLoadMore, final boolean isFirst) {

        TagInfoList.TagInfo t = new TagInfoList.TagInfo();
        t.setTagName(fmTagName);
        operateController.getTagIndex(t, fmTop, "20", null, SlateBaseOperate.FetchApiType.USE_HTTP_FIRST, new FetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (entry != null && entry instanceof TagArticleList) {
                    TagArticleList articleList = (TagArticleList) entry;
                    showCount = articleList.getShowCount();
                    if (!articleList.getMap().isEmpty()) {
                        /**
                         * 加载更多
                         */
                        if (isLoadMore) {
                            articleData.addAll(articleList.getArticleList());
                            getFmModels();// 数据转化
                            handler.sendEmptyMessage(102);
                            /**
                             * 刷新
                             */
                        } else {
                            articleData.clear();
                            articleData.addAll(articleList.getArticleList());
                            getFmModels();// 数据转化
                            if (!isFirst) {
                                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                            } else {// 初始化第一个播放
                                if (musicIng == 0) {
                                    currentPos = 0;
                                    currentPlayArt = articleData.get(0);
                                    handler.sendEmptyMessage(MSG_INIT_HEAD);
                                } else {
                                    for (int i = 0; i < articleData.size(); i++) {
                                        if (articleData.get(i).getArticleId() == musicIng) {
                                            changeMusic(i);
                                        }
                                    }
                                }

                            }
                            handler.sendEmptyMessage(MSG_REFRESH_LIST);
                        }
                        if (articleData.size() > 0) {
                            fmTop = articleData.get(articleData.size() - 1).getOffset();
                        }

                    }
                }
            }
        });
    }

    //    private void initFmData() {
    //
    //        operateController.getTagInfo("", "", "", "", GetTagInfoOperate.TAG_TYPE.FM, SlateBaseOperate.FetchApiType.USE_HTTP_FIRST, new FetchEntryListener() {
    //
    //            @Override
    //            public void setData(Entry entry) {
    //
    //                if (entry instanceof TagInfoList) {
    //                    TagInfoList list = (TagInfoList) entry;
    //                    if (ParseUtil.listNotNull(list.getList()))
    //                        fmTagName = list.getList().get(0).getTagName();
    //
    //                }
    //                handler.sendEmptyMessage(100);
    //
    //            }
    //        });
    //    }

    private void initView() {
        view = LayoutInflater.from(context).inflate(R.layout.view_fm, null);


        timeBox = (TextView) view.findViewById(R.id.music_play_time);
        imgBox = (ImageView) view.findViewById(R.id.music_img_box);
        wholeTimeBox = (TextView) view.findViewById(R.id.music_whole_time);
        seekBar = (SeekBar) view.findViewById(R.id.playseekbar);
        playOrPause = (ImageView) view.findViewById(R.id.music_play_pause);

        pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);
        listView = (PullableListView) view.findViewById(R.id.fm_listview);
        adapter = new MusicItemAdapter(context, fmModels, showCount);
        listView.setAdapter(adapter);
        pullToRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                Log.e("Fm刷新", "Fm刷新");
                fmTop = "";
                loadFmData(false, false);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                Log.e("fm load more", "fm more");
                loadFmData(true, false);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeMusic(position);
            }
        });

        playOrPause.setOnClickListener(this);
        view.findViewById(R.id.music_previous).setOnClickListener(this);
        view.findViewById(R.id.music_next).setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

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
            public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
                ifFromUser = fromUser;
            }
        });
    }

    /**
     * 主动换歌
     */
    private void changeMusic(int p) {
        if (!ParseUtil.listNotNull(articleData)) return;
        currentPos = p;
        currentPlayArt = articleData.get(p);
        // 没有权限，去简介页面
        if (currentPlayArt.getProperty().getLevel() > 0 && !SlateDataHelper.getLevelByType(context, currentPlayArt.getProperty().getLevel())) {
            if (context instanceof ShangchengListActivity) {
                ((ShangchengListActivity) context).goInfo();
            }

        } else {//不需要付费 或者 有fm阅读权限 直接播放
            getFmModels();//
            // 更新播放状态
            unbindService();
            sendintent();// 主动切歌

        }

    }


    /**
     * articleItem 转化成fmmodel
     * 更新播放状态
     */
    private void getFmModels() {
        fmModels.clear();
        for (int i = 0; i < articleData.size(); i++) {
            PlayIngStatusModel fmModel = new PlayIngStatusModel();
            fmModel.setArticleItem(articleData.get(i));
            if (currentPlayArt != null && currentPlayArt.getArticleId() == articleData.get(i).getArticleId()) {
                fmModel.setIsPlaying(1);
            } else fmModel.setIsPlaying(0);

            fmModels.add(fmModel);
        }
    }


    /**
     * 绑定intent
     */
    private void sendintent() {
        if (currentPlayArt.getAudioList() != null && currentPlayArt.getAudioList().size() > 0) {
            Intent intent = new Intent(context, MusicService.class);
            intent.putExtra("play_model", currentPlayArt);
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            isHaveService = true;// 绑定了service
        } else Tools.showToast(context, "音频数据缺失");

    }

    /**
     * head初始化数据
     */
    private void setDataForHead() {
        if (currentPlayArt.getPicList() != null && currentPlayArt.getPicList().size() > 0)
            img(currentPlayArt.getPicList().get(0).getUrl(), imgBox);

    }


    private void img(String url, final ImageView imageView) {
        SlateApplication.finalBitmap.display(imageView, url);
    }


    /**
     * 切歌重启播放service
     */
    public void unbindService() {
        if (isHaveService && serviceConnection != null)// 如果绑定了service，则解绑
        {
            musicPlayBinder.stop();
            context.unbindService(serviceConnection);

            isHaveService = false;
        }
    }


    /**
     * 监听当前播放进度
     */
    private void listenProgress() {
        if (musicPlayBinder != null)
            musicPlayBinder.setOnProgressListener(new OnProgressListener() {

                @Override
                public void OnProgressChangeListener(int p) {
                    if (p > progress) handler.sendEmptyMessage(MSG_CHANGE_TIME);
                    progress = p;// 更新当前进度
                }

                @Override
                public void onMessageListener(Message message) {
                    handler.sendMessage(message);
                }
            });
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
     * 更新正在播放状态
     *
     * @param c
     */
    public void setPlayFlags(int c) {

    }

    public View fetchView() {
        return view;
    }

    /**
     * @param actiontype 1：开始播放；2：暂停后播放
     */
    public void addFmTime(int actiontype) {
        if (currentPlayArt == null || !ParseUtil.listNotNull(currentPlayArt.getAudioList())) return;
        UserOperateController.getInstance(context).addFmTime(context, currentPlayArt.getArticleId() + "", currentPlayArt.getAudioList().get(0).getResId(), actiontype, 1, new UserFetchEntryListener() {
            @Override
            public void setData(Entry entry) {

            }
        });
    }


}
