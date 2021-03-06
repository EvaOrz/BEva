package cn.com.modernmedia.views.index;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.adapter.IndexViewPagerAdapter;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.widget.VerticalViewPager;
import cn.com.modernmedia.widget.CircularViewPager;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.jzvd.JZVideoPlayer;

/**
 * 可滑动的首页
 *
 * @author user
 */
public class IndexViewPager extends CircularViewPager<TagInfo> implements NotifyArticleDesListener {
    private Context mContext;
    private List<TagInfo> catItems = new ArrayList<TagInfo>();// 栏目列表
    private IndexViewPagerAdapter adapter;
    private View top_menu;

    public IndexViewPager(Context context) {
        this(context, null);
    }

    public IndexViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.top_menu = top_menu;
        init();
    }

    private void init() {
        this.setOffscreenPageLimit(1);
        setListener(this);
        // 修改viewpager滑动响应的最小距离
        try {
            Field touchSlop = ViewPager.class.getDeclaredField("mTouchSlop");
            touchSlop.setAccessible(true);
            touchSlop.setInt(this, 20);
            touchSlop.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化栏目列表
     */
    private void initCatItems() {
        catItems.clear();
        catItems.addAll(AppValue.bookColumnList.getList());
        if (CommonApplication.advList != null){
            /**
             * 加入栏目间广告
             */
            Map<Integer, List<AdvList.AdvItem>> advMap = CommonApplication.advList.getAdvMap();
            if (ParseUtil.mapContainsKey(advMap, AdvList.BETWEEN_CAT)) {
                List<AdvList.AdvItem> list = advMap.get(AdvList.BETWEEN_CAT);
                for (AdvList.AdvItem item : list) {
                    if (item == null) continue;
                    String sort = item.getSort();
                    if (TextUtils.isEmpty(sort)) {
                        continue;
                    } else if (!sort.contains("*") && ParseUtil.stoi(sort, -1) == -1) {
                        // TODO 如果sort不包含*,而又不能解析成整数，那么说明sort有错误，返回
                        continue;
                    } else {
                        catItems.add(ParseUtil.stoi(sort, -1),item.convertToTagInfo());
                    }
                }
            }
        }

        if (CommonApplication.mConfig.getHas_single_view() == 1) {
            if (catItems.size() > 0) catItems.remove(0);
        }
    }

    /**
     * 设置栏目信息
     */
    public void setCatList() {
        initCatItems();
        setDataForPager(catItems);
        setTitle(getCurrentItem());
    }

    /**
     * 直接定位到某一个栏目
     *
     * @param tagName
     * @param isUri   是否来自uri;如果是，当找不到的时候需要添加这个栏目，否则，显示第一个栏目
     */
    public void checkPosition(String tagName, boolean isUri) {
        if (!ParseUtil.listNotNull(catItems)) return;
        int position = -1;
        for (int i = 0; i < catItems.size(); i++) {
            TagInfo item = catItems.get(i);
            if (TextUtils.equals(item.getTagName(), tagName)) {
                position = i;
                break;
            }
        }
        /**
         * 商周4.0+
         * 找不到栏目，跳转新栏目首页
         */
        if (position == -1 && isUri) {
            String broadcastIntent = "cn.com.modernmedia.views.column.book.BookColumnActivity";
            Intent intent = new Intent(broadcastIntent);
            intent.putExtra("tagname", tagName);
            mContext.sendBroadcast(intent);

            //            new UriParseToIndex(mContext, new UriParseToIndexListener() {
            //
            //                @Override
            //                public void fetchTagInfo(TagInfo info) {
            //                    if (info != null) {
            //                        initCatItems();
            //                        catItems.add(0, info);
            //                        setDataForPager(catItems);
            //                        V.setIndexTitle(mContext, info);
            //                    }
            //                }
            //            }).findTagWhenDidnotFind(tagName);
        } else {
            position = position == -1 ? 0 : position;
            this.setCurrentItem(position, false);
            setTitle(position);
        }
    }

    @Override
    public MyPagerAdapter<TagInfo> fetchAdapter(Context context, List<TagInfo> list) {
        return adapter = new IndexViewPagerAdapter(mContext, list);
    }

    @Override
    public void updateDes(int position) {
        // 切换tab时  暂停之前的视频
        JZVideoPlayer.goOnPlayOnPause();
        setTitle(position);
    }

    private void setTitle(int position) {
        if (ParseUtil.listNotNull(catItems) && catItems.size() > position) {
            TagInfo item = catItems.get(position);
            ((CommonMainActivity) mContext).notifyColumnAdapter(item.getTagName());
            V.setIndexTitle(mContext, item);
            if (((CommonMainActivity) mContext).getIndexView() != null && ((CommonMainActivity) mContext).getIndexView() instanceof IndexView)
                ((IndexView) ((CommonMainActivity) mContext).getIndexView()).setTopMenuSelect(item.getTagName());
        }
    }

    @Override
    public void updatePage(int state) {
        if (SlateApplication.mConfig.getNav_hide() == 1) ViewsApplication.navObservable.setData();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (adapter != null && adapter.getSelfScrollViews() != null) {
            if (intercept(ev)) {
                return false;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean intercept(MotionEvent ev) {
        for (View view : adapter.getSelfScrollViews()) {
            if (view instanceof VerticalViewPager) {
                // TODO 图集
                return adjustAngle(ev);
            }
            Rect rect = new Rect();
            // 获取该gallery相对于全局的坐标点
            view.getGlobalVisibleRect(rect);
            if (rect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                return true;
            }
        }
        return false;
    }

    private float angleX, angleY;// 比较Y轴滑动角度，如果低于30，则执行横屏滑动
    private double angle;

    /**
     * 滑动的角度是否大于最小值
     *
     * @param ev
     * @return
     */
    private boolean adjustAngle(MotionEvent ev) {
        float y = ev.getRawY();
        float x = ev.getRawX();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // TODO 滑动时监听不到action_down
            angleX = x;
            angleY = y;
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            angle = Math.atan(Math.abs(y - angleY) / Math.abs(x - angleX)) / Math.PI * 180;
            angleX = x;
            angleY = y;
            if (angle > 30) {
                return true;
            }
        }
        return false;
    }

    public boolean doGoBack() {
        if (adapter == null) return false;
        IndexViewPagerItem item = adapter.getCurrentPagerItem();
        return item == null ? false : item.doGoBack();
    }

    public TagIndexListView getCurrListView() {
        if (adapter == null) return null;
        IndexViewPagerItem item = adapter.getCurrentPagerItem();
        if (item != null) {
            return item.getIndexListView();
        }
        return null;
    }

}
