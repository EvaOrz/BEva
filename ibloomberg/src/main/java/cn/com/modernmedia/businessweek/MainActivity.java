package cn.com.modernmedia.businessweek;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.api.UrlMaker;
import cn.com.modernmedia.breakpoint.BreakPointUtil;
import cn.com.modernmedia.breakpoint.DownloadPackageCallBack;
import cn.com.modernmedia.businessweek.jingxuan.ShangchengTabView;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.SizeCallBackForButton;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.SubscribeOrderList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.TagArticleListDb;
import cn.com.modernmedia.newtag.db.TagIndexDb;
import cn.com.modernmedia.newtag.mainprocess.DownloadAvdRes;
import cn.com.modernmedia.pay.newlogic.PayHttpsOperate;
import cn.com.modernmedia.push.NewPushManager;
import cn.com.modernmedia.util.BitmapUtil;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.EnsubscriptHelper;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.PageTransfer;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.column.NewColumnView;
import cn.com.modernmedia.views.column.book.BookActivity;
import cn.com.modernmedia.views.index.IndexView;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.CommonWebView;
import cn.com.modernmedia.widget.MainHorizontalScrollView;
import cn.com.modernmedia.widget.MainHorizontalScrollView.FecthViewSizeListener;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.UserApplication;
import cn.com.modernmediausermodel.util.UserCentManager;
import cn.jzvd.JZVideoPlayer;


/**
 * 首页
 *
 * @author ZhuQiao
 */
public class MainActivity extends CommonMainActivity {

    public static final int SELECT_COLUMN_REQUEST_CODE = 200;
    public static final int SELECT_CHILD_COLUMN_LOGIN_REQUEST_CODE = 203;

    public static final int SECOND = 10 * 1000;
    public static final int READ_SECOND = 10 * 60 * 1000;


    protected NewColumnView columnView;// 栏目列表侧滑栏
    private LinearLayout container;// 首页tab容器
    private TextView webTitle; // 即时、财富顶部title显示
    private ImageView zhiboAdvlayout;// 24小时直播顶部广告位
    // 推荐tab
    protected IndexView indexView;
    // 直播tab
    private View liveTabView;
    private LinearLayout con;
    private CommonWebView liveWeb;
    //    // 视频tab
    //    private VideoTabView videoTabView;


    //专题、往期、专刊tab
    private ShangchengTabView shangchengTabView;

    // 个人中心tab
    private UserCenterTabView userTabView;

    private LifecycleObservable lifecycleObservable = new LifecycleObservable();

    AudioManager audioManager;
    private int volumeInApp;// 进入应用时的音量

    // 底部导航
    private RadioGroup radioGroup;
    private RadioButton radioButton_news;// 首页 推荐
    private RadioButton radioButton_live;// 首页 即时
    private RadioButton radioButton_caifu;// 首页 财富
    private RadioButton radioButton_speial;// 首页 专题
    private RadioButton radioButton_mine;// 首页 我
    private Drawable mine1, mine2;
    private boolean isNews = true;
    private boolean isLive = false;

    private SensorManager sensorManager;
    private JZVideoPlayer.JZAutoFullscreenListener sensorEventListener;
    private List<RadioModel> radioModels = new ArrayList<>();

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (1 == msg.what) {
                checkIssueLevel();
            }
        }
    };
    private Timer timer = new Timer(true);
    private TimerTask task;

    public enum LifeCycle {
        RESUME, PAUSE, STOP;
    }

    public class LifecycleObservable extends Observable {

        public void setData(LifeCycle lifeCycle) {
            setChanged();
            notifyObservers(lifeCycle);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uploadDeviceInfoForPush();

        ViewsApplication.readedArticles = ReadDb.getInstance(this).getAllReadArticle();
        lifecycleObservable.deleteObservers();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeInApp = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        UserApplication.userObservable.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                if (userTabView != null) userTabView.reLoad();
            }
        });
        checkUriParse(getIntent());
        downloadBottom();
        //每10分钟检查用户阅读权限
        task = new TimerTask() {
            @Override
            public void run() {
                if (isRunningForeground()) {
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            }
        };
        timer.schedule(task, SECOND, READ_SECOND);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorEventListener = new JZVideoPlayer.JZAutoFullscreenListener();
    }


    public boolean isRunningForeground() {
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        return currentPackageName != null && currentPackageName.equals(getPackageName());
    }

    /**
     * 获取此uid下未读的广告列表
     *
     * @return
     */
    public List<AdvItem> getUnReadedAdvList(List<AdvItem> list) {
        if (!ParseUtil.listNotNull(list)) return list;

        String readedString = DataHelper.getShowZhannei(this, SlateDataHelper.getUid(this));
        if (TextUtils.isEmpty(readedString)) return list;
        String[] readArray = readedString.split(",");
        if (readArray.length == 0) return list;
        List<AdvItem> lll = new ArrayList<>();
        for (AdvItem a : list) {
            boolean isReaded = false;
            for (String ss : readArray) {
                if ((a.getAdvId() + "").equals(ss)) {
                    isReaded = true;
                }
            }
            if (!isReaded) lll.add(a);

        }
        return lll;
    }

    /**
     * 获取匹配的站内信列表
     *
     * @return
     */
    public List<AdvItem> getZhanneiadvList(String tagString) {
        List<AdvItem> list = new ArrayList<>();
        if (CommonApplication.advList == null || !ParseUtil.listNotNull(CommonApplication.advList.getAdvMap().get(AdvList.ZHANNEI_ADV)))
            return list;

        if (TextUtils.isEmpty(tagString)) {
            List<AdvItem> advItems = CommonApplication.advList.getAdvMap().get(AdvList.ZHANNEI_ADV);
            for (AdvItem advItem : advItems) {
                if (TextUtils.isEmpty(advItem.getTargetTag())) {
                    list.add(advItem);
                }
            }
            return list;
        }

        String[] tags = tagString.split(",");
        for (AdvItem advItem : CommonApplication.advList.getAdvMap().get(AdvList.ZHANNEI_ADV)) {
            // 不打标签 -- 显示
            if (TextUtils.isEmpty(advItem.getTargetTag())) list.add(advItem);
            else {
                for (String tt : tags) {
                    if (advItem.getTargetTag().equals(tt)) {
                        list.add(advItem);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 检查站内信
     */
    private void checkAdvTag() {

        PayHttpsOperate.getInstance(this).getAdvTagList(new FetchDataListener() {

            @Override
            public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                Log.e("checkAdvTag", data);
                List<AdvItem> list = new ArrayList<AdvItem>();

                // 没取到匹配的标签，直接返回【未读】的【站内信】
                if (!isSuccess || TextUtils.isEmpty(data)) {
                    list.addAll(getUnReadedAdvList(getZhanneiadvList(null)));
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        /**
                         * 存储站内信taglist
                         */
                        AppValue.advTagList = jsonObject.optString("tag");
                        checkZhannei();
                        userTabView.checkZhannei();
                    } catch (JSONException e) {

                    }
                }


            }
        });
    }

    /**
     * 是否显示站内信layout
     *
     * @return
     */
    public void checkZhannei() {
        if (ParseUtil.listNotNull(getUnReadedAdvList(getZhanneiadvList(AppValue.advTagList)))) {
            changeMineRadioUnReaded(true);
        } else changeMineRadioUnReaded(false);
    }

    /**
     * 下载底部菜单选项
     */

    private void downloadBottom() {
        DownloadAvdRes downloadAvdRes = new DownloadAvdRes(this);
        //
        if (TextUtils.isEmpty(AppValue.appInfo.getBottomBarPkg())) {
            checkAdvTag();
            return;
        }
        downloadAvdRes.downloadBusinessB(AppValue.appInfo.getBottomBarPkg(), new BreakPointUtil(this, new DownloadPackageCallBack() {
            @Override
            public void onSuccess(String tagName, String folderName) {
                String fold = FileManager.getPackageNameByUrl(folderName) + "/";
                Log.e("sssss", fold);
                checkRadioButtomStyle(fold);
                initRadio(fold);
                checkAdvTag();
            }

            @Override
            public void onPause(String tagName) {

            }

            @Override
            public void onFailed(String tagName) {

            }

            @Override
            public void onProcess(String tagName, long complete, long total) {

            }
        }));
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        NewPushManager.getInstance(this).onresume(this);
        // 重力感应传感
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (SlateApplication.loginStatusChange) {
            Log.e("登录状态变化", "登录状态变化");
            String uid = Tools.getUid(this);

            if (TextUtils.isEmpty(uid) || TextUtils.equals(uid, SlateApplication.UN_UPLOAD_UID)) {
                radioGroup.check(R.id.bottom_nav_news);
                changeTab(0);
                refreshSubscript("", -1);
            } else getUserSubscript("", -1);
            checkZhannei();
            checkAdvTag();
            userTabView.reLoad();
            shangchengTabView.reLoad();
            SlateApplication.loginStatusChange = false;
        }
        // 用户中心页数据变化时，刷新页面？？

        if (userTabView != null && radioGroup.getCheckedRadioButtonId() == R.id.bottom_nav_mine) {
            userTabView.reLoad();
        }

        lifecycleObservable.setData(LifeCycle.RESUME);
    }

    /**
     * 刷新订阅列表
     *
     * @param currTag 父栏目tagname
     */
    private void refreshSubscript(String currTag, int code) {
        TagIndexDb.getInstance(this).clearSubscribeTopArticle();
        TagArticleListDb.getInstance(this).clearSubscribeTopArticle();
        EnsubscriptHelper.addEnsubscriptColumn(this);//

        setDataForColumn();
        if (ViewsApplication.columnChangedListener != null)
            ViewsApplication.columnChangedListener.changed();

        if (SlateApplication.mConfig.getIs_index_pager() == 1) {
            showIndexPager();
            if (!TextUtils.isEmpty(currTag)) {
                clickItemIfPager(currTag, false);
            }
        }
    }

    @Override
    public BaseView getIndexView() {
        return indexView;
    }

    @Override
    public void setIndexTitle(String name) {
        indexView.setTitle(name);
        String newName = name.replaceAll("、", "");
        // 设置底部导航文字
        radioButton_news.setText(newName);
    }


    @Override
    public void showIndexPager() {
        ViewsApplication.autoScrollObservable.clearAll();
        indexView.setDataForIndexPager();
    }

    @Override
    public void notifyColumnAdapter(String tagName) {
        super.notifyColumnAdapter(tagName);
        if (ViewsApplication.mConfig.getIs_navbar_bg_change() == 1 && indexView.getNav() != null && ParseUtil.mapContainsKey(DataHelper.columnColorMap, tagName)) {
            indexView.getNav().setBackgroundColor(DataHelper.columnColorMap.get(tagName));

        }
    }

    /**
     * 首页滑屏，直接定位到具体的栏目
     *
     * @param tagName
     * @param isUri   是否来自uri;如果是，当找不到的时候需要添加这个栏目，否则，显示第一个栏目
     */
    @Override
    public void clickItemIfPager(String tagName, boolean isUri) {
        indexView.checkPositionIfPager(tagName, isUri);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, iUiListener);
        if (resultCode == RESULT_OK) {
            if (requestCode == PageTransfer.REQUEST_CODE && CommonApplication.mConfig.getHas_coin() == 1) {

                /**
                 * Boom: 频繁请求文章获取金币规则-- 添加金币
                 */
                // UserCentManager.getInstance(this)
                // .addArticleCoinCent(null, true);
            } else if (requestCode == BOOK_ACTIVITY) {
                refreshSubscript("", -1);
            } else if (requestCode == Constants.REQUEST_API) {
                if (resultCode == Constants.REQUEST_QQ_SHARE || resultCode == Constants.REQUEST_QZONE_SHARE || resultCode == Constants.REQUEST_OLD_SHARE) {
                    Tencent.handleResultData(data, iUiListener);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 腾讯分享回调
     */
    public IUiListener iUiListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            showToast(R.string.share_by_email);
        }

        @Override
        public void onError(UiError uiError) {

        }

        @Override
        public void onCancel() {

        }
    };


    public void getUserSubscript(final String currTag, final int code) {
        showLoadingDialog(true);
        OperateController.getInstance(this).getSubscribeOrderList(Tools.getUid(this), SlateDataHelper.getToken(this), FetchApiType.USE_HTTP_FIRST, new FetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                showLoadingDialog(false);
                if (entry instanceof SubscribeOrderList) {
                    refreshSubscript(currTag, code);
                }
            }
        });
    }

    /**
     * 给scrollview设置正在下拉刷新
     *
     * @param isPulling
     */
    public void setPulling(boolean isPulling) {
        scrollView.setPassToUp(isPulling);
    }

    @Override
    public String getActivityName() {
        return MainActivity.class.getName();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (indexView.doGoBack()) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void init() {
        setContentView(R.layout.activity_businessweek_main);
        container = (LinearLayout) findViewById(R.id.main_container);
        columnView = (NewColumnView) findViewById(R.id.main_column);
        scrollView = (MainHorizontalScrollView) findViewById(R.id.mScrollView);
        indexView = new IndexView(this);

        liveTabView = LayoutInflater.from(this).inflate(R.layout.main_live_view, null);
        liveTabView.setLayoutParams(new LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.MATCH_PARENT));
        con = (LinearLayout) liveTabView.findViewById(R.id.live_webView);
        webTitle = liveTabView.findViewById(R.id.live_title);
        zhiboAdvlayout = (ImageView) liveTabView.findViewById(R.id.zhibo_adv);

        liveWeb = new CommonWebView(this, true);
        con.addView(liveWeb, new LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.MATCH_PARENT));

        shangchengTabView = new ShangchengTabView(this);
        userTabView = new UserCenterTabView(this);

        View leftView = LayoutInflater.from(this).inflate(R.layout.scroll_left, null);
        leftView.setTag(MainHorizontalScrollView.LEFT_ENLARGE_WIDTH);
        View rightView = LayoutInflater.from(this).inflate(R.layout.scroll_right, null);
        rightView.setTag(MainHorizontalScrollView.RIGHT_ENLARGE_WIDTH);
        final View[] children = new View[]{leftView, indexView, rightView};
        scrollView.initViews(children, new SizeCallBackForButton(indexView.getTopMenuColumnViewButton()), columnView, new View(this));
        scrollView.setIntercept(CommonApplication.mConfig.getIs_index_pager() == 1);

        scrollView.setButtons(indexView.getTopMenuColumnViewButton(), indexView.getTopMenuAddViewButton());
        scrollView.setViewListener(new FecthViewSizeListener() {

            @Override
            public void fetchViewWidth(int width) {
                if (columnView != null) {
                    columnView.setViewWidth(width);
                }
            }
        });

        // 底部导航
        radioGroup = (RadioGroup) findViewById(R.id.main_bottom_nav).findViewById(R.id.bottom_nav_rg);
        radioButton_news = (RadioButton) findViewById(R.id.main_bottom_nav).findViewById(R.id.bottom_nav_news);
        radioButton_live = (RadioButton) findViewById(R.id.main_bottom_nav).findViewById(R.id.bottom_nav_live);
        radioButton_caifu = (RadioButton) findViewById(R.id.main_bottom_nav).findViewById(R.id.bottom_nav_caifu);
        radioButton_speial = (RadioButton) findViewById(R.id.main_bottom_nav).findViewById(R.id.bottom_nav_special);
        radioButton_mine = (RadioButton) findViewById(R.id.main_bottom_nav).findViewById(R.id.bottom_nav_mine);

        radioButton_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNews) {
                    changeTab(0);
                    LogHelper.checkBottomNavNews(MainActivity.this);
                } else {//二次点击回到推荐

                    gotoIndex(false);
                }

            }
        });
        radioButton_live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveChecked();
            }
        });
        radioButton_caifu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTab(2);
            }
        });
        radioButton_speial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTab(3);
                LogHelper.checkBottomNavSpecial(MainActivity.this);
            }
        });
        radioButton_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTab(4);
                LogHelper.checkBottomNavMine(MainActivity.this);
            }
        });

    }


    private void initRadio(String foldername) {
        if (radioModels.size() != 5) return;
        Drawable news1 = getDrawableFromFileUrl(foldername + radioModels.get(0).getPictureUrl());
        Drawable news2 = getDrawableFromFileUrl(foldername + radioModels.get(0).getPictureUrlSelected());
        Drawable live1 = getDrawableFromFileUrl(foldername + radioModels.get(1).getPictureUrl());
        Drawable live2 = getDrawableFromFileUrl(foldername + radioModels.get(1).getPictureUrlSelected());
        Drawable video1 = getDrawableFromFileUrl(foldername + radioModels.get(2).getPictureUrl());
        Drawable video2 = getDrawableFromFileUrl(foldername + radioModels.get(2).getPictureUrlSelected());
        Drawable jing1 = getDrawableFromFileUrl(foldername + radioModels.get(3).getPictureUrl());
        Drawable jing2 = getDrawableFromFileUrl(foldername + radioModels.get(3).getPictureUrlSelected());
        mine1 = getDrawableFromFileUrl(foldername + radioModels.get(4).getPictureUrl());
        mine2 = getDrawableFromFileUrl(foldername + radioModels.get(4).getPictureUrlSelected());

        StateListDrawable news = new StateListDrawable();
        news.addState(new int[]{android.R.attr.state_checked}, news2);
        news.addState(new int[]{}, news1);
        news.setBounds(0, 0, news.getMinimumWidth(), news.getMinimumHeight());
        StateListDrawable live = new StateListDrawable();
        live.addState(new int[]{android.R.attr.state_checked}, live2);
        live.addState(new int[]{}, live1);
        live.setBounds(0, 0, live.getMinimumWidth(), live.getMinimumHeight());
        StateListDrawable video = new StateListDrawable();
        video.addState(new int[]{android.R.attr.state_checked}, video2);
        video.addState(new int[]{}, video1);
        video.setBounds(0, 0, video.getMinimumWidth(), video.getMinimumHeight());
        StateListDrawable jing = new StateListDrawable();
        jing.addState(new int[]{android.R.attr.state_checked}, jing2);
        jing.addState(new int[]{}, jing1);
        jing.setBounds(0, 0, jing.getMinimumWidth(), jing.getMinimumHeight());
        StateListDrawable mine = new StateListDrawable();
        mine.addState(new int[]{android.R.attr.state_checked}, mine2);
        mine.addState(new int[]{}, mine1);
        mine.setBounds(0, 0, mine.getMinimumWidth(), mine.getMinimumHeight());

        radioButton_news.setCompoundDrawables(null, news, null, null);
        radioButton_news.setText(radioModels.get(0).getTitle());
        radioButton_live.setCompoundDrawables(null, live, null, null);
        radioButton_live.setText(radioModels.get(1).getTitle());
        radioButton_live.setTag(radioModels.get(1).getLinkUrl());
        Log.e("radioButton_live", radioModels.get(1).getLinkUrl());
        radioButton_speial.setCompoundDrawables(null, jing, null, null);
        radioButton_speial.setText(radioModels.get(3).getTitle());
        radioButton_mine.setCompoundDrawables(null, mine, null, null);
        radioButton_mine.setText(radioModels.get(4).getTitle());

    }

    /**
     * 个人中心按钮标红
     *
     * @param ifNew
     */
    public void changeMineRadioUnReaded(boolean ifNew) {

        if (ifNew) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    StateListDrawable mine11 = new StateListDrawable();
                    Drawable mine3 = getResources().getDrawable(R.drawable.wor1);//选中
                    Drawable mine4 = getResources().getDrawable(R.drawable.wor12);//选中
                    mine11.addState(new int[]{android.R.attr.state_checked}, mine3);
                    mine11.addState(new int[]{}, mine4);
                    mine11.setBounds(0, 0, mine11.getMinimumWidth(), mine11.getMinimumHeight());
                    radioButton_mine.setCompoundDrawables(null, mine11, null, null);
                }
            });
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    StateListDrawable mine = new StateListDrawable();
                    if (mine1 == null) {
                        mine1 = getResources().getDrawable(R.drawable.wo2x);
                    }
                    if (mine2 == null) {
                        mine2 = getResources().getDrawable(R.drawable.wo12x);
                    }
                    mine.addState(new int[]{android.R.attr.state_checked}, mine2);
                    mine.addState(new int[]{}, mine1);
                    mine.setBounds(0, 0, mine.getMinimumWidth(), mine.getMinimumHeight());
                    radioButton_mine.setCompoundDrawables(null, mine, null, null);
                }
            });
        }

    }

    private Drawable getDrawableFromFileUrl(String name) {
        Bitmap bitmap = BitmapUtil.getBitmapByPath(name, 50, 50);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        drawable.setTargetDensity(getResources().getDisplayMetrics());

        return drawable;
    }

    /**
     * 读取zip包，获取样式
     */
    private void checkRadioButtomStyle(String folderName) {
        String zip = AppValue.appInfo.getBottomBarPkg();
        if (TextUtils.isEmpty(zip)) return;
        if (!FileManager.containThisPackageFolder(zip)) return;
        List<String> nameList = FileManager.getFoldChildNames(zip);
        if (!ParseUtil.listNotNull(nameList)) return;
        File f = new File(folderName + "botBarCfg.json");
        if (!f.exists()) return;
        try {
            JSONArray j = new JSONArray(FileManager.getJsonData(f));
            if (j == null || j.length() == 0) return;
            radioModels.clear();
            for (int i = 0; i < j.length(); i++) {
                RadioModel r = new RadioModel(j.getJSONObject(i));
                radioModels.add(r);
            }
        } catch (JSONException e) {

        }

    }

    @Override
    public void gotoIndex(boolean isColumn) {
        if (isColumn) {
            changeTab(0);
            getScrollView().clickButton(true);
        }
        indexView.checkPositionIfPager("cat_15", false);//cat_15 推荐栏目
        // 重置top_menu选项position
        indexView.setTopMenuSelect("cat_15");
        handler.post(new Runnable() {
            @Override
            public void run() {
                indexView.doGoTop();
            }
        });
        LogHelper.checkBottomNavNews(MainActivity.this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkUriParse(intent);
    }

    /**
     * 检查跳转【精选】页面的tab
     */
    private void checkUriParse(Intent i) {
        if (i != null) {
            final int type = i.getIntExtra("from_slate", -1);
            if (type > 0 || type == 0) handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    changeTab(type);
                    if (type == 3) {
                        radioButton_speial.setChecked(true);
                    } else if (type == 2) {
                        radioButton_caifu.setChecked(true);
                    }
                }
            }, 500);

        }
    }

    public void changeTab(int position) {
        container.removeAllViews();
        JZVideoPlayer.goOnPlayOnPause();
        switch (position) {
            case 0:
                isNews = true;
                isLive = false;
                container.addView(scrollView);
                break;
            case 1:
                isNews = false;
                isLive = true;
                container.addView(liveTabView);
                webTitle.setText(R.string.live_title);
                // TODO 跑马灯链接
                if (CommonApplication.advList != null && ParseUtil.mapContainsKey(CommonApplication.advList.getAdvMap(), AdvList.LIVE_TITLE)) {
                    List<AdvList.AdvItem> list = CommonApplication.advList.getAdvMap().get(AdvList.LIVE_TITLE);
                    if (ParseUtil.listNotNull(list)) {
                        zhiboAdvlayout.setVisibility(View.VISIBLE);
                        CommonApplication.finalBitmap.display(zhiboAdvlayout, list.get(0).getSourceList().get(0).getUrl());
                    }
                }
                if (radioButton_live.getTag() != null) {
                    liveWeb.loadUrl((String) radioButton_live.getTag());
                } else liveWeb.loadUrl(liveUrl);
                UserCentManager.getInstance(MainActivity.this).addLoginCoinCent();
                break;
            case 2:
                isNews = false;
                isLive = false;
                container.addView(liveTabView);
                webTitle.setText(R.string.nav_caifu);
                liveWeb.loadUrl(UrlMaker.getCaiFuUrl(MainActivity.this));
                break;
            case 3:
                isNews = false;
                isLive = false;
                shangchengTabView.loadData();
                container.addView(shangchengTabView);

                break;
            case 4:
                isNews = false;
                isLive = false;
                container.addView(userTabView);
                break;

        }
    }

    @Override
    public void gotoMarquee() {
        super.gotoMarquee();
        LogHelper.marqueeTouchMore(this);
        radioButton_live.setChecked(true);
        liveChecked();
    }

    /**
     * 直播点击事件
     */
    private void liveChecked() {
        if (!isLive) {//第一次点击
            changeTab(1);
            LogHelper.checkBottomNavLive(this);
        } else {//直播二次点击刷新
            changeTab(1);
            LogHelper.checkBottomNavLive(this);
            Toast.makeText(this, R.string.fecthing_ok, Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    protected void setDataForColumn() {
        TagInfoList columnTags = new TagInfoList();
        List<TagInfo> list = new ArrayList<TagInfo>();
        for (int i = 0; i < AppValue.ensubscriptColumnList.getList().size(); i++) {
            if (AppValue.ensubscriptColumnList.getList().get(i).getIsFix() == 1) {
                list.add(AppValue.ensubscriptColumnList.getList().get(i));
            }
        }
        columnTags.setList(list);
        columnView.setData(columnTags);
        // 塞top_menu数据
        indexView.setTopMenuData(AppValue.bookColumnList);
    }

    @Override
    public MainHorizontalScrollView getScrollView() {
        return scrollView;
    }

    @Override
    protected void exitApp() {
        super.exitApp();
        if (timer != null) {
            timer.cancel();
        }
        ViewsApplication.exit();
        if (audioManager != null) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeInApp, 0);
        }

    }

    @Override
    public String[] getFragmentTags() {
        return null;
    }

    @Override
    protected void notifyRead() {

        ViewsApplication.readedArticles = ReadDb.getInstance(this).getAllReadArticle();
        List<Integer> list = new ArrayList<Integer>();
        if (ViewsApplication.lastestArticleId != null) {
            for (Integer id : ViewsApplication.lastestArticleId.getUnReadedId()) {
                if (ViewsApplication.readedArticles.contains(id)) {
                    list.add(id);
                }
            }
        }
        if (!list.isEmpty()) {
            for (Integer id : list) {
                Map<String, ArrayList<Integer>> map = ViewsApplication.lastestArticleId.getUnReadedArticles();
                for (String key : map.keySet()) {
                    ArrayList<Integer> articleIds = map.get(key);
                    if (ParseUtil.listNotNull(articleIds)) {
                        if (articleIds.contains(id)) articleIds.remove(id);
                    }
                }
                if (ViewsApplication.lastestArticleId != null)
                    ViewsApplication.lastestArticleId.getUnReadedId().remove(id);
            }
            ViewsApplication.notifyLastest();
        }
        super.notifyRead();
    }

    /**
     * Push需要：上传device信息
     */
    private void uploadDeviceInfoForPush() {
        if (DataHelper.isPushServiceEnable(this)) NewPushManager.getInstance(this).register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);

        sensorManager.unregisterListener(sensorEventListener);
        JZVideoPlayer.goOnPlayOnPause();
        // 推送在暂停mainactivity时不用pause，只在退出app时需要pause
        //        NewPushManager.getInstance(this).onpause(this);
    }

    /**
     * 跳转至选择栏目页
     */
    public void gotoSelectColumnActivity() {
        Intent intent = new Intent(this, BookActivity.class);
        startActivityForResult(intent, BOOK_ACTIVITY);
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

}
