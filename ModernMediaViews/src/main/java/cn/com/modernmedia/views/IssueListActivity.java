package cn.com.modernmedia.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.List;

import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.api.GetTagInfoOperate;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.column.book.BookColumnActivity;
import cn.com.modernmedia.widget.newrefresh.PullToRefreshLayout;
import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.DateFormatTool;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * Created by Eva. on 2017/8/8.
 */

public class IssueListActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout contain;
    private NoScrollGridView gridView;
    private PullToRefreshLayout pullToRefreshLayout;
    private IssueLAdapter issueLAdapter;

    private OperateController operateController;
    private List<TagInfo> wangqiData, hotData;
    private TextView date, key;
    private boolean isDate = true;// 时间段
    private String hotTop = "", dateTop = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_list);
        operateController = OperateController.getInstance(this);
        initView();
        if (getIntent().getSerializableExtra("wangqi_datas") != null && getIntent().getSerializableExtra("wangqi_datas") instanceof TagInfoList) {
            wangqiData = ((TagInfoList) getIntent().getSerializableExtra("wangqi_datas")).getList();
            dateTop = wangqiData.get(wangqiData.size() - 1).getOffset();
        }

        if (getIntent().getSerializableExtra("hot_datas") != null && getIntent().getSerializableExtra("hot_datas") instanceof TagInfoList) {
            hotData = ((TagInfoList) getIntent().getSerializableExtra("hot_datas")).getList();
            hotTop = hotData.get(hotData.size() - 1).getOffset();
        }
        if (ParseUtil.listNotNull(wangqiData) && ParseUtil.listNotNull(hotData)) {
            changeOp();
        }
    }

    private void initView() {
        findViewById(R.id.issue_back).setOnClickListener(this);
        key = findViewById(R.id.issue_keywords);
        key.setOnClickListener(this);
        date = findViewById(R.id.issue_dates);
        date.setOnClickListener(this);

        contain = findViewById(R.id.wangqi_scroll);
        pullToRefreshLayout = findViewById(R.id.refresh_view);
        pullToRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                if (isDate) {
                    dateTop = "";
                    loadMoreDateDatas(false);
                } else {
                    hotTop = "";
                    loadMoreHotDatas(false);
                }
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                if (isDate) {
                    loadMoreDateDatas(true);
                } else {
                    loadMoreHotDatas(true);
                }

            }
        });
        gridView = new NoScrollGridView(this);
        contain.addView(gridView);
        gridView.setNumColumns(2);
        issueLAdapter = new IssueLAdapter(this);
        gridView.setAdapter(issueLAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isDate) {
                    Intent i = new Intent(IssueListActivity.this, IssueListDetailActivity.class);
                    i.putExtra("issue_taginfo", wangqiData.get(position));
                    startActivity(i);
                } else {
                    Intent i = new Intent(IssueListActivity.this, BookColumnActivity.class);
                    i.putExtra("is_tekan", 1);
                    i.putExtra("book_deatail", hotData.get(position));
                    startActivity(i);
                }

            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:// 关键词
                    issueLAdapter.setData(hotData, 1);
                    break;
                case 1:// 时间段
                    issueLAdapter.setData(wangqiData, 0);
                    break;
            }
        }
    };

    /**
     * 加载更多热门
     */
    private void loadMoreHotDatas(final boolean isLoadMore) {
        operateController.getTagInfo("", "", "", hotTop, GetTagInfoOperate.TAG_TYPE.SEARCH_HOTS, SlateBaseOperate.FetchApiType.USE_HTTP_FIRST, new FetchEntryListener() {

            @Override
            public void setData(Entry entry) {

                if (entry != null && entry instanceof TagInfoList) {

                    if (isLoadMore) {
                        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    } else {
                        hotData.clear();
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    }
                    hotData.addAll(((TagInfoList) entry).getList());
                    if (hotData.size() > 0) {
                        hotTop = hotData.get(hotData.size() - 1).getOffset();
                    }
                    handler.sendEmptyMessage(0);
                }
            }
        });
    }

    /**
     * 加载更多时间段
     */
    private void loadMoreDateDatas(final boolean isLoadMore) {
        // 往期
        operateController.getLastIssueList(dateTop, new FetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (entry != null && entry instanceof TagInfoList) {
                    if (isLoadMore) {
                        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    } else {
                        wangqiData.clear();
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    }
                    wangqiData.addAll(((TagInfoList) entry).getList());
                    if (wangqiData.size() > 0) {
                        dateTop = wangqiData.get(wangqiData.size() - 1).getOffset();
                    }
                    handler.sendEmptyMessage(1);
                }
            }
        });
    }


    private void changeOp() {
        if (isDate) {
            date.setTextColor(getResources().getColor(R.color.black_bg));
            key.setTextColor(getResources().getColor(R.color.grgray));
            issueLAdapter.setData(wangqiData, 0);

        } else {
            key.setTextColor(getResources().getColor(R.color.black_bg));
            date.setTextColor(getResources().getColor(R.color.grgray));
            issueLAdapter.setData(hotData, 1);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.issue_back) {
            finish();
        } else if (v.getId() == R.id.issue_keywords) {
            isDate = false;
            changeOp();
        } else if (v.getId() == R.id.issue_dates) {
            isDate = true;
            changeOp();
        }
    }

    @Override
    public void reLoadData() {

    }

    @Override
    public String getActivityName() {
        return IssueListActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return IssueListActivity.this;
    }


    /**
     */
    public class IssueLAdapter extends ArrayAdapter<TagInfo> {
        private Context mContext;
        private int type = 0;// 0：时间段；1：关键词

        public IssueLAdapter(Context cc) {
            super(cc, 0);
            this.mContext = cc;
        }

        public void setData(List<TagInfo> list, int type) {
            clear();
            this.type = type;
            for (TagInfo history : list) {
                add(history);
            }
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout vv = new LinearLayout(mContext);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.bottomMargin = 20;
            layoutParams.topMargin = 20;
            layoutParams.weight = 1;
            layoutParams.gravity = Gravity.CENTER;
            TextView view = new TextView(mContext);
            TagInfo tagInfo = getItem(position);
            if (type == 0) {
                String ss = DateFormatTool.format(tagInfo.getIssueProperty().getStartTime() *
                        1000, "yyyy.MM.dd") + " - " + DateFormatTool.format(tagInfo
                        .getIssueProperty().getEndTime() * 1000, "yyyy.MM.dd");
                view.setText(ss);
            } else {
                view.setText(tagInfo.getCatName());
            }

            view.setMaxLines(1);
            view.setEllipsize(TextUtils.TruncateAt.END);
            view.setTextColor(getResources().getColor(R.color.black_bg));
            view.setBackgroundResource(R.drawable.shape_search);
            view.setGravity(Gravity.CENTER);
            view.setPadding(0, 20, 0, 20);
            vv.addView(view, layoutParams);
            return vv;
        }
    }

    public class NoScrollGridView extends GridView {
        public NoScrollGridView(Context context) {
            super(context);
        }

        public NoScrollGridView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        }
    }
}


