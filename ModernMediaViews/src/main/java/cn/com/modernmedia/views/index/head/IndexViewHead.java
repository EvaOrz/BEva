package cn.com.modernmedia.views.index.head;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.List;

import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.views.ViewsMainActivity.LifeCycle;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmedia.views.xmlparse.FunctionXML;
import cn.com.modernmedia.views.xmlparse.XMLDataSetForHead;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmedia.widget.FullVideoView;
import cn.com.modernmedia.widget.LoopPagerAdapter;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.jzvd.JZVideoPlayer;

/**
 * 首页焦点图
 *
 * @author zhuqiao
 */
public class IndexViewHead extends BaseIndexHeadView {
    private XMLParse parse;
    private XMLDataSetForHead dataSet;


    private Handler handler = new Handler();

    public IndexViewHead(Context context, Template template) {
        super(context, template);
    }

    @Override
    protected void init() {
        parse = new XMLParse(mContext, null);
        addView(parse.inflate(template.getHead().getData(), null, template.getHost()));
        dataSet = parse.getDataSetForHead();
        viewPager = dataSet.getViewPager();
    }

    @Override
    protected void showHead() {
        dataSet.showHead();
    }

    @Override
    protected void unShowHead() {
        dataSet.unShowHead();
    }

    /**
     * 更新title
     *
     * @param item
     */
    @Override
    protected void updateTitle(ArticleItem item) {
        dataSet.update(item);
    }

    /**
     * 初始化dotll
     *
     * @param itemList
     * @param dots
     */
    @Override
    protected void initDot(List<ArticleItem> itemList, List<ImageView> dots) {
        dataSet.dot(itemList, dots);
    }

    @Override
    protected void setDataToGallery(List<ArticleItem> list) {
        super.setDataToGallery(list);
        dataSet.initAnim(list);
    }

    @Override
    public void updateDes(int position) {
        super.updateDes(position);
        dataSet.anim(mList, position);
        waitToCheck(position, viewPager.getRealCurrentItem());
    }

    @Override
    public void updatePage(int state) {
        super.updatePage(state);
        if (state == ViewPager.SCROLL_STATE_DRAGGING) {
            // 滑动时只能(彻底释放)暂停之前head部分的播放
            waitToPause(viewPager.getCurrentItem());
        } else if (state == ViewPager.SCROLL_STATE_IDLE) {
        } else if (state == ViewPager.SCROLL_STATE_SETTLING) {
        }
    }

    /**
     * @param cycle
     */
    public void life(LifeCycle cycle) {
        if (cycle == LifeCycle.PAUSE) {
            stopRefresh();
        } else if (cycle == LifeCycle.RESUME) {
            startRefresh();
        }
    }

    private void waitToPause(final int position) {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
//                Log.e("waitToPause", position + "");
                if (mList == null || mList.size() <= position) return;
                checkHasAdvVideo(position, false);
                if (checkArticleVideoIsPlay(position) == 1) {// 正在播放
                    JZVideoPlayer.goOnPlayOnPause();
                    startRefresh();
                }
            }
        }, 500);
    }

    /**
     * 等待一定时间，等adapter的setPrimaryItem执行
     */
    private void waitToCheck(final int position, final int realPosition) {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
//                Log.e("waitToCheck", position + "");
                boolean has = checkHasAdvVideo(position, true) || checkArticleVideoIsPlay(position) == 1;
                if (has) {
                    stopRefresh();
                    autoScrollHandler.advUnShow();
                } else {
                    startRefresh();
                    if (hasAdvImg(position)) {
                        autoScrollHandler.advUnShow();
                    } else {
                        autoScrollHandler.advShow();
                    }
                }

            }
        }, 500);
    }

    /**
     * 是否有图片广告
     *
     * @return
     */
    private boolean hasAdvImg(int position) {
        if (mList == null || mList.size() <= position) return false;
        final ArticleItem item = mList.get(position);
        if (item.getAdvSource() == null) return false;
        if (TextUtils.isEmpty(item.getAdvSource().getUrl())) return false;
        return true;
    }

    /**
     * 检查是否是有视频广告
     *
     * @param position
     * @param start
     * @return
     */
    private boolean checkHasAdvVideo(int position, boolean start) {
        if (mList == null || mList.size() <= position) return false;
        final ArticleItem item = mList.get(position);
        if (item.getAdvSource() == null) return false;
        if (TextUtils.isEmpty(item.getAdvSource().getVideolink())) return false;

        if (viewPager == null || !(viewPager.getAdapter() instanceof LoopPagerAdapter))
            return false;
        LoopPagerAdapter loopPagerAdapter = (LoopPagerAdapter) viewPager.getAdapter();
        if (!(loopPagerAdapter.getPagerAdapter() instanceof IndexHeadPagerAdapter)) return false;
        IndexHeadPagerAdapter adapter = (IndexHeadPagerAdapter) loopPagerAdapter.getPagerAdapter();

        final HashMap<String, View> viewMap = adapter.getCurrViewMap();
        if (viewMap == null) return false;
        if (!ParseUtil.mapContainsKey(viewMap, FunctionXML.VIDEO_VIEW)) return false;
        View view = viewMap.get(FunctionXML.VIDEO_VIEW);
        if (!(view instanceof FullVideoView)) return false;
        ((FullVideoView) view).setOnHeadRefreshListener(onHeadRefreshListener);
        if (start)
            //视频广告自动播放
            ((FullVideoView) view).startVideo();
        else
            //视频广告停止
            ((FullVideoView) view).goOnPlayOnPause();
        return true;

    }

    private FullVideoView.OnHeadRefreshListener onHeadRefreshListener = new FullVideoView.OnHeadRefreshListener() {
        @Override
        public void onRefresh(boolean isStartRefresh) {
//            Log.e("sssss", isStartRefresh ? "true" : "false");
            if (isStartRefresh) startRefresh();
            else stopRefresh();
        }
    };

    /**
     * articleItem.getPicList().get(0).getVideolink()
     *
     * @param position
     * @return
     */
    private int checkArticleVideoIsPlay(int position) {
        if (position < 0 || mList == null || mList.size() <= position) return 0;
        final ArticleItem item = mList.get(position);
        if (TextUtils.isEmpty(item.getPicList().get(0).getVideolink())) return 0;

        if (viewPager == null || !(viewPager.getAdapter() instanceof LoopPagerAdapter)) return 0;
        LoopPagerAdapter loopPagerAdapter = (LoopPagerAdapter) viewPager.getAdapter();
        if (!(loopPagerAdapter.getPagerAdapter() instanceof IndexHeadPagerAdapter)) return 0;
        IndexHeadPagerAdapter adapter = (IndexHeadPagerAdapter) loopPagerAdapter.getPagerAdapter();

        final HashMap<String, View> viewMap = adapter.getCurrViewMap();
        if (viewMap == null) return 0;
        if (!ParseUtil.mapContainsKey(viewMap, FunctionXML.VIDEO_VIEW)) return 0;
        View view = viewMap.get(FunctionXML.VIDEO_VIEW);
        if (!(view instanceof FullVideoView)) return 0;
        ((FullVideoView) view).setOnHeadRefreshListener(onHeadRefreshListener);
        boolean flag = ((FullVideoView) view).getIsPlaying();
//        Log.e("ssssss", flag ? item.getTitle() : "false1");
        return flag ? 1 : 2;
    }
}
