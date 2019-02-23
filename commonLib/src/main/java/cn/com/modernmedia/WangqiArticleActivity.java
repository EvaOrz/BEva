package cn.com.modernmedia;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.modernmedia.adapter.WangqiPopAdapter;
import cn.com.modernmedia.adapter.WangqiViewPagerAdapter;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.CallWebStatusChangeListener;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 往期的文章详情页
 * Created by Eva. on 16/12/27.
 */

public class WangqiArticleActivity extends BaseActivity {
    protected ViewPager viewPager;
    private WangqiViewPagerAdapter adapter;
    private TextView title;
    private PopupWindow menupop;//章节menu
    private View popView;
    private List<ArticleItem> menulist = new ArrayList<ArticleItem>();

    protected List<ArticleItem> list = new ArrayList<ArticleItem>();
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, CallWebStatusChangeListener> listenerMap = new HashMap<Integer, CallWebStatusChangeListener>();
    protected int currentPosition = -1;
    protected View currView;
    private int needDeleteHidden = -1;
    private TagInfoList.TagInfo tagInfo;
    private ListView menuListview;
    private WangqiPopAdapter menuAdapter;
    public static final String COVER_URL_HEAD = "http://adv.bbwc.cn/articles/trend/index.html?url=";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wangqi_article);
        if (getIntent().getSerializableExtra("Wangqi_taginfo") != null) {
            tagInfo = (TagInfoList.TagInfo) getIntent().getSerializableExtra("Wangqi_taginfo");
        }
        initView();

        initProcess();
        fetchData();
    }

    /**
     * 跳转到指定文章页
     *
     * @param position
     */
    public void gotoArticle(int position) {
        viewPager.setCurrentItem(position + 1, true);
        menuAdapter.setSelection(viewPager.getCurrentItem());
        menupop.dismiss();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.wangqi_viewpager);
        viewPager.setOffscreenPageLimit(1);// 限制预加载，只加载下一页
        adapter = new WangqiViewPagerAdapter(WangqiArticleActivity.this, list);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                menuAdapter.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        title = (TextView) findViewById(R.id.wangqi_title);
        title.setText(tagInfo.getIssueProperty().getName());
        findViewById(R.id.wangqi_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // menu
        findViewById(R.id.wangqi_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    popMenu();
            }
        });
        popView = LayoutInflater.from(this).inflate(R.layout.wangqi_pop, null);
        menuListview = (ListView) popView.findViewById(R.id.wangqi_menu_listview);
        menuAdapter = new WangqiPopAdapter(this, menulist);
        menuListview.setAdapter(menuAdapter);
        menuListview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        menuListview.setBackgroundColor(getResources().getColor(R.color.white_bg));
        menuListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoArticle(position);
                Log.e("wangqi_menu_item", position + "");
            }
        });
        // 创建PopupWindow实例,200,150分别是宽度和高度
        menupop = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
    }


    /**
     * 获取文章数据
     */
    private void fetchData() {
        showLoading();
        OperateController.getInstance(this).getTagArticles(this,tagInfo, "", "", null, new
                FetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (entry instanceof TagArticleList) {
                    disProcess();
                    list.clear();
                    addCover();
                    menulist.clear();
                    List<ArticleItem> ll = ((TagArticleList) entry).getArticleList();
                    for (ArticleItem aa : ll) {
                        if (ParseUtil.listNotNull(aa.getPageUrlList())) {
                            ArticleItem.PhonePageList p = aa.getPageUrlList().get(0);
                            p.setUrl(p.getUrl() + "?noshare=1&related=0");
                            list.add(aa);
                            menulist.add(aa);
                        }

                    }
                    handler.sendEmptyMessage(0);
                } else {
                    showError();
                }
            }
        });
    }

    /**
     * 添加封面
     */
    private void addCover() {
        if (tagInfo.getIssueProperty() == null || tagInfo.getIssueProperty().getPictureList().size() < 2)
            return;
        ArticleItem a = new ArticleItem();
        a.setArticleId(-2);// 往期封面标示
        ArticleItem.PhonePageList p = new ArticleItem.PhonePageList();
        p.setUrl(COVER_URL_HEAD + tagInfo.getIssueProperty().getPictureList().get(1));
        List<ArticleItem.PhonePageList> l = new ArrayList<>();
        l.add(p);
        a.setPageUrlList(l);
        list.add(a);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:// 设置数据
                    title.setText(tagInfo.getIssueProperty().getName());
                    adapter.notifyDataSetChanged();
                    menuAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    /**
     * 弹出章节menu
     */
    private void popMenu() {
        //动画效果
        menupop.setAnimationStyle(R.style.AnimationFade);
        //显示位置
        menupop.showAsDropDown(findViewById(R.id.wangqi_toolbar));

        //设置背景半透明
        backgroundAlpha();
        menupop.setOutsideTouchable(true);
        menupop.setFocusable(true);
        menupop.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    menupop.dismiss();
                    return true;
                }
                return false;
            }
        });
        popView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menupop.dismiss();
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha() {
        popView.setBackgroundColor(getResources().getColor(R.color.translucent_black));
    }

    @Override
    public void reLoadData() {

    }

    @Override
    public Activity getActivity() {
        return WangqiArticleActivity.this;
    }

    @Override
    public String getActivityName() {
        return WangqiArticleActivity.class.getName();
    }
}
