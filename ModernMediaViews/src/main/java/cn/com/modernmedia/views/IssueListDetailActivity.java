package cn.com.modernmedia.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.widget.newrefresh.PullToRefreshLayout;
import cn.com.modernmedia.widget.newrefresh.PullableListView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.DateFormatTool;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * Created by Eva. on 2017/8/9.
 */

public class IssueListDetailActivity extends BaseActivity implements View.OnClickListener {

    private TextView title;
    private TagInfo issueTag;
    private PullToRefreshLayout refreshLayout;
    private PullableListView listView;
    private List<ArticleItem> aDatas = new ArrayList<>();

    private IssueDAdapter issueDAdapter;
    private String top = "";// 加载更多偏移量

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_listd);
        issueTag = (TagInfo) getIntent().getSerializableExtra("issue_taginfo");
        initView();
        initData(false);
    }


    private void initView() {
        findViewById(R.id.issue1_back).setOnClickListener(this);
        title = (TextView) findViewById(R.id.issue1_title);
        refreshLayout = findViewById(R.id.refresh_view);
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                top = "";
                initData(false);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                initData(true);
            }
        });
        listView = findViewById(R.id.issue1_listview);
        issueDAdapter = new IssueDAdapter(this, aDatas);
        listView.setAdapter(issueDAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArticleItem item = aDatas.get(position);
                Intent intent = new Intent(IssueListDetailActivity.this, CommonApplication.pushArticleCls);
                intent.putExtra(TagProcessManage.KEY_PUSH_ARTICLE_ID, item.getArticleId() + "");
                intent.putExtra(TagProcessManage.KEY_PUSH_ARTICLE_LEVEL, item.getProperty().getLevel());
                startActivity(intent);
            }
        });

        String ss = DateFormatTool.format(issueTag.getIssueProperty().getStartTime() * 1000, "yyyy" + ".MM.dd") + " - " + DateFormatTool.format(issueTag.getIssueProperty().getEndTime() * 1000, "yyyy.MM.dd");
        title.setText(ss);

    }

    private void initData(final boolean isLoadMore) {
        showLoadingDialog(true);
        OperateController.getInstance(this).getResult("", issueTag.getIssueProperty().getStartTime(), issueTag.getIssueProperty().getEndTime(), top, new FetchEntryListener() {
            @Override
            public void setData(Entry entry) {
                showLoadingDialog(false);
                if (entry instanceof TagArticleList) {
                    TagArticleList list = (TagArticleList) entry;
                    if (isLoadMore) {
                        if (list.getArticleList().size() == 0) {
                            handler.sendEmptyMessage(2);// 没有更多
                        } else {
                            aDatas.addAll(list.getArticleList());
                            handler.sendEmptyMessage(1);// 加载更多
                        }
                    } else {
                        aDatas.clear();
                        aDatas.addAll(list.getArticleList());
                        handler.sendEmptyMessage(0);// 刷新

                    }
                    // 更新偏移量
                    if (aDatas.size() > 0) {
                        top = aDatas.get(aDatas.size() - 1).getOffset();
                    }


                }
            }
        });

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:// 刷新
                    issueDAdapter.notifyDataSetChanged();
                    refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    break;

                case 1:// 加载更多
                    issueDAdapter.notifyDataSetChanged();
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);

                    break;
                case 2:// 没有更多
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    break;
            }
        }
    };


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.issue1_back) {
            finish();
        }
    }

    @Override
    public void reLoadData() {

    }

    @Override
    public String getActivityName() {
        return IssueListDetailActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return IssueListDetailActivity.this;
    }


    /**
     */
    public class IssueDAdapter extends ArrayAdapter<ArticleItem> {
        private Context mContext;

        private List<ArticleItem> list;

        public IssueDAdapter(Context cc, List<ArticleItem> list) {
            super(cc, 0);
            this.mContext = cc;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public ArticleItem getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ArticleItem item = list.get(position);
            View vv = LayoutInflater.from(mContext).inflate(R.layout.item_issue_dview, null);
            ImageView img = (ImageView) vv.findViewById(R.id.issue_item_img);
            TextView title = (TextView) vv.findViewById(R.id.issue_item_title);
            TextView d1 = (TextView) vv.findViewById(R.id.issue_item_date1);
            TextView d2 = (TextView) vv.findViewById(R.id.issue_item_date2);
            TextView d3 = (TextView) vv.findViewById(R.id.issue_item_date3);

            V.setImage(img, "placeholder");
            if (ParseUtil.listNotNull(item.getThumbList()) && !TextUtils.isEmpty(item.getThumbList().get(0).getUrl()))
                V.setImage(img, item.getThumbList().get(0).getUrl());
            title.setText(item.getTitle());
            String s1 = DateFormatTool.format(item.getInputtime() * 1000, "dd");
            String s2 = DateFormatTool.format(item.getInputtime() * 1000, "MM") + "月";
            d1.setText(s1);
            d2.setText(s2);
            d3.setText(changeweekOne(item.getInputtime() * 1000));
            long pre = position > 0 ? getItem(position - 1).getInputtime() : 0;
            long nex = getItem(position).getInputtime();
            if (!DateFormatTool.isSameDay(pre, nex)) {
                d1.setVisibility(View.VISIBLE);
                d2.setVisibility(View.VISIBLE);
                d3.setVisibility(View.VISIBLE);
            } else {
                d1.setVisibility(View.INVISIBLE);
                d2.setVisibility(View.INVISIBLE);
                d3.setVisibility(View.INVISIBLE);
            }

            return vv;
        }
    }


    /**
     * 输入时间戳变星期
     *
     * @param time
     * @return
     */
    public static String changeweekOne(long time) {
        Date date = new Date(time);
        SimpleDateFormat week = new SimpleDateFormat("E");
        return week.format(date);

    }


}
