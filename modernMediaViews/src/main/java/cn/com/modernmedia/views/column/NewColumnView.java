package cn.com.modernmedia.views.column;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.common.SharePopWindow;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.SearchActivity;
import cn.com.modernmedia.views.model.TemplateColumn;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmedia.views.xmlparse.column.FunctionColumn;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.zxing.activity.CaptureActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.FeedBackActivity;
import cn.com.modernmediausermodel.LoginActivity;
import cn.com.modernmediausermodel.util.UserPageTransfer;

/**
 * 栏目列表页
 *
 * @author zhuqiao
 */
public class NewColumnView extends BaseView implements FetchEntryListener {
    private Context mContext;
    private TemplateColumn template;
    private ScrollView scrollView;
    private LinearLayout headFrame;
    private MyGridView listView;
    private ColumnAdapter adapter;

    private XMLParse headParse;

    /**
     * 确定栏目页列表的footer的某项是否选中，从而改变该项的背景或者字体颜色等
     *
     * @author jiancong
     */
    public interface ColumnFooterItemIsSeletedListener {
        /**
         * @param isSelected true表示其item处于选中状态,false则未选中
         */
        public void doIsSelected(boolean isSelected);
    }

    public NewColumnView(Context context) {
        this(context, null);
    }

    public NewColumnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }


    @SuppressLint("InflateParams")
    private void init() {
        template = ParseProperties.getInstance(mContext).parseColumn();
        addView(LayoutInflater.from(mContext).inflate(R.layout.new_column_view, null));
        scrollView = (ScrollView) findViewById(R.id.column_scroll);
        headFrame = (LinearLayout) findViewById(R.id.column_head);

        //搜索
        findViewById(R.id.column_search).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, SearchActivity.class));
            }
        });
        // 扫一扫
        findViewById(R.id.column_sao).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, CaptureActivity.class));
            }
        });

        // vip
        findViewById(R.id.column_vip).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserPageTransfer.isLogin(mContext)) {
                    if (SlateDataHelper.getVipLevel(mContext) > 0) {//我的VIP
                        UserPageTransfer.gotoMyVipActivity(mContext, false);
                    } else UserPageTransfer.gotoVipActivity(mContext, false);//开通VIP
                } else mContext.startActivity(new Intent(mContext, LoginActivity.class));

            }
        });
        // 反馈
        findViewById(R.id.column_feedback).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserPageTransfer.isLogin(mContext)) {
                    mContext.startActivity(new Intent(mContext, FeedBackActivity.class));
                } else mContext.startActivity(new Intent(mContext, LoginActivity.class));
            }
        });
        //跳转订阅页面
        findViewById(R.id.book_manage).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((CommonMainActivity) mContext).gotoSelectColumnActivity();
            }
        });
        // 分享app
        findViewById(R.id.share_app).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ArticleItem a = new ArticleItem();
                a.setTitle(mContext.getResources().getString(R.string.preference_recommend_subject));
                a.setDesc(mContext.getResources().getString(R.string.preference_recommend_text));
                a.setWeburl("http://app.bbwc.cn/download.html");
                SharePopWindow sharePopWindow = new SharePopWindow(mContext, a);
                sharePopWindow.setIsShareApp(true);
            }
        });

        listView = (MyGridView) findViewById(R.id.column_list);
        listView.setNumColumns(2);
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));

        V.setViewBack(findViewById(R.id.column_contain), template.getBackground());
        initHead();
        initFooter();
        initAdapter();
    }

    /**
     * 初始化headview
     */
    private void initHead() {
        headParse = new XMLParse(mContext, null);
        View headView = headParse.inflate(template.getHead().getData(), null, "");
        /**
         * 4.0改为GridView，不再管headview是否跟随listview滑动
         */
        //        if (template.getHead().getHold() == 1) {
        headFrame.removeAllViews();
        headFrame.addView(headView);
        //        } else {
        //        headView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
        //        listView.addHeaderView(headView);
        //        }


        //回到主页
        headView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CommonMainActivity) mContext).gotoIndex(true);
            }
        });
    }

    /**
     * 初始化adapter
     */
    private void initAdapter() {
        adapter = new ColumnAdapter(mContext, template);
        listView.setAdapter(adapter);
    }

    @Override
    public void setData(Entry entry) {
        adapter.clear();
        TagInfoList tagInfoList = null;
        if (entry instanceof TagInfoList) {
            tagInfoList = (TagInfoList) entry;
            if (ParseUtil.listNotNull(tagInfoList.getList())) {
                // TODO 添加默认栏目

                // 栏目数据
                // List<TagInfo> currAppTagList = tagInfoList.getColumnTagList(
                // false, false);
                adapter.setData(tagInfoList.getList());
                adapter.setThisAppItemCount(tagInfoList.getList().size());
            }
        }
        if (adapter.getCount() > 0) {
            scrollView.smoothScrollTo(0, 0);
            //            scrollView.fullScroll(ScrollView.FOCUS_UP);
        }
        //        if (mContext instanceof CommonMainActivity) {
        //            if (ViewsApplication.itemIsSelectedListener != null)
        //                ViewsApplication.itemIsSelectedListener.doIsSelected(false);
        //        }
    }

    /**
     * 初始化footerview
     */
    private void initFooter() {

    }

    private View getFooterItem(int id) {
        ColumnAdapter adapter = new ColumnAdapter(mContext, template);
        TagInfo info = new TagInfo();
        info.setAdapter_id(id);
        List<TagInfo> list = new ArrayList<TagInfo>();
        list.add(info);
        adapter.setData(list);
        return adapter.getView(0, null, null);
    }

    /**
     * 设置页面宽度
     *
     * @param width
     */
    public void setViewWidth(int width) {
        if (template.getHead().getNeed_calculate_height() == 0) return;
        if (headParse != null && headParse.getMap().containsKey(FunctionColumn.COLUMN_LOG)) {
            View view = headParse.getMap().get(FunctionColumn.COLUMN_LOG);
            view.getLayoutParams().height = (view.getLayoutParams().height * width) / CommonApplication.width;
            // 不需要重新设置view宽度，在MainHorizontalScrollView里已经把columnview的宽度整体设置成可显示的大小了
        }
    }


    @Override
    protected void reLoad() {
    }


}
