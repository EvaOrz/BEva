package cn.com.modernmedia.views.index.head;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 首页焦点图
 *
 * @author user
 */
public abstract class BaseIndexHeadView extends BaseView implements NotifyArticleDesListener {
    protected Context mContext;
    protected IndexHeadCircularViewPager viewPager;
    private List<ImageView> dots = new ArrayList<ImageView>();
    protected List<ArticleItem> mList = new ArrayList<ArticleItem>();
    private boolean shouldScroll;// 如果只有一张图，那么不监听滑动
    protected Template template;

    protected AutoScrollHandler autoScrollHandler;

    public BaseIndexHeadView(Context context, Template template) {
        super(context);
        mContext = context;
        this.template = template;
        autoScrollHandler = new AutoScrollHandler(context) {

            @Override
            public void next() {
                if (viewPager != null) viewPager.next();
            }

            @Override
            public void setShowHead() {
                showHead();
            }

            @Override
            public void setunShowHead() {
                unShowHead();
            }
        };
        init();
        initProperties();
    }

    /**
     */
    protected abstract void showHead();

    /**
     */
    protected abstract void unShowHead();

    /**
     * 初始化资源
     */
    protected abstract void init();

    private void initProperties() {
        if (viewPager != null) {
            viewPager.setListener(this);
            viewPager.setTemplate(template);
        }
    }

    @Override
    public void updateDes(int position) {
        if (mList != null && mList.size() > position && mList.size() > 1) {
            ArticleItem item = mList.get(position);
            updateTitle(item);
            for (int i = 0; i < dots.size(); i++) {
                if (i == position) {
                    dots.get(i).setImageResource(R.drawable.dot_active);
                } else {
                    dots.get(i).setImageResource(R.drawable.dot);
                }
            }
            AdvTools.requestImpression(item);
        }
    }

    @Override
    public void updatePage(int state) {
    }

    /**
     * 获取当前list;当是独立栏目时，如果从服务器上返会的list。size为0，而当前headview不为空，那么不删除headview
     *
     * @return
     */
    public List<ArticleItem> getDataList() {
        return mList;
    }

    public void setData(List<ArticleItem> list, TagInfo tagInfo) {
        setDataToGallery(list);
    }

    /**
     * @param list true:更新完的数据，追加在开始添加；false:覆盖本来数据
     */
    protected void setDataToGallery(final List<ArticleItem> list) {
        if (list == null || viewPager == null) return;
        viewPager.setArticleType(ArticleType.Default);
        mList.clear();
        mList.addAll(list);
        if (!ParseUtil.listNotNull(mList)) return;
        if (mContext instanceof CommonMainActivity) {
            if (mList.size() == 0 || mList.size() == 1) {
                shouldScroll = false;
            } else {
                ((CommonMainActivity) mContext).setScrollView(this);
                shouldScroll = true;
            }
        }
        updateTitle(mList.get(0));
        viewPager.setDataForPager(mList);
        initDot(mList, dots);
    }

    protected void addOutline(String outline, String outlineImg) {
    }


    /**
     * 开始自动切换
     */
    public void startRefresh() {
        if (viewPager == null || !ParseUtil.listNotNull(mList)) return;
        if (shouldScroll) autoScrollHandler.startChange();
    }

    /**
     * 停止后台自动切换
     */
    public void stopRefresh() {
        autoScrollHandler.stopChange();
    }

    protected void updateTitle(ArticleItem item) {
    }

    protected void initDot(List<ArticleItem> itemList, List<ImageView> dots) {
    }

    public View getViewPager() {
        return viewPager;
    }

    public boolean isShouldScroll() {
        return shouldScroll;
    }

    public void dismissHead() {
        if (viewPager != null) {
            setPadding(0, -viewPager.getLayoutParams().height, 0, 0);
            invalidate();
        }
    }

    @Override
    protected void reLoad() {
    }

}
