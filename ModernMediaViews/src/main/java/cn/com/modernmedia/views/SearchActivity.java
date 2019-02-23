package cn.com.modernmedia.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.GetTagInfoOperate;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage;
import cn.com.modernmedia.views.adapter.TagSearchHistoryAdapter;
import cn.com.modernmedia.views.adapter.TagSearchResultAdapter;
import cn.com.modernmedia.views.column.book.BookColumnActivity;
import cn.com.modernmedia.views.widget.AutoTextGroupView;
import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.DateFormatTool;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.util.UserDataHelper;

/**
 * 新搜索页面
 * Created by Eva. on 2017/7/24.
 */

public class SearchActivity extends BaseActivity implements View.OnClickListener {

    private EditText searchEdit;
    private TextView do_cancel, do_search;
    private LinearLayout hisLayout, hotLayout;
    private OperateController operateController;
    private ViewGroup.MarginLayoutParams lp;
    private ImageView noResult;
    //历史
    private ListView hisListView;
    private List<String> historyData = new ArrayList<String>();
    private TagSearchHistoryAdapter hisAdapter;

    //搜索结果列表
    private ListView resultView;
    private List<ArticleItem> resultData = new ArrayList<>();
    private TagSearchResultAdapter resultAdapter;

    //热门
    private AutoTextGroupView hotView;
    private TagInfoList wangqiDatas = new TagInfoList(), hotDatas = new TagInfoList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initData();
    }


    private void initView() {
        searchEdit = (EditText) findViewById(R.id.search_edit);
        //监听回车键
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                showCancel();
                hideKeyboard();
                String msg = v.getText().toString();
                historyData = UserDataHelper.saveSearchHistory(SearchActivity.this, msg);
                Collections.reverse(historyData);
                doSearch(msg);
                return false;
            }
        });
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    do_cancel.setVisibility(View.VISIBLE);
                    do_search.setVisibility(View.GONE);
                    handler.sendEmptyMessage(3);
                } else {
                    do_cancel.setVisibility(View.GONE);
                    do_search.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        hisLayout = (LinearLayout) findViewById(R.id.his_layout);
        hotLayout = (LinearLayout) findViewById(R.id.hot_layout);
        do_cancel = (TextView) findViewById(R.id.search_cancel);
        do_search = (TextView) findViewById(R.id.search_done);
        do_cancel.setOnClickListener(this);
        do_search.setOnClickListener(this);
        findViewById(R.id.s_hot_more).setOnClickListener(this);
        findViewById(R.id.s_his_more).setOnClickListener(this);
        noResult = findViewById(R.id.search_no_tip);
        resultView = (ListView) findViewById(R.id.search_result_listview);
        resultView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArticleItem item = resultData.get(position);
                Intent intent = new Intent(SearchActivity.this, CommonApplication.pushArticleCls);
                intent.putExtra(TagProcessManage.KEY_PUSH_ARTICLE_ID, item.getArticleId() + "");
                intent.putExtra(TagProcessManage.KEY_PUSH_ARTICLE_LEVEL, item.getProperty().getLevel());
                startActivity(intent);
            }
        });
        resultAdapter = new TagSearchResultAdapter(this);
        resultView.setAdapter(resultAdapter);

        hisListView = (ListView) findViewById(R.id.search_history_listview);
        hisListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doSearch(historyData.get(position));
            }
        });
        hisAdapter = new TagSearchHistoryAdapter(this);
        hisListView.setAdapter(hisAdapter);
        hotView = (AutoTextGroupView) findViewById(R.id.search_hot_view);

        lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 10, 10, 10);
    }


    /**
     * 初始化标签textview
     *
     * @return
     */
    private TextView getTextView(final TagInfo tagInfo, final boolean isIssue) {
        TextView view = new TextView(SearchActivity.this);
        view.setTag(tagInfo);
        if (isIssue) {
            String ss = DateFormatTool.format(tagInfo.getIssueProperty().getStartTime() * 1000, "yyyy.MM.dd") + " - " + DateFormatTool.format(tagInfo.getIssueProperty().getEndTime() * 1000, "yyyy.MM.dd");
            view.setText(ss);

        } else {
            view.setText(tagInfo.getCatName());

        }
        view.setTextColor(getResources().getColor(R.color.black_bg));
        view.setBackgroundResource(R.drawable.shape_search);
        view.setTextSize(12);
        view.setGravity(Gravity.CENTER);
        view.setPadding(20, 10, 20, 10);

        // 点击跳转
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Object o = v.getTag();
                if (o != null && o instanceof TagInfo) {
                    if (isIssue) {
                        Intent i = new Intent(SearchActivity.this, IssueListDetailActivity.class);
                        i.putExtra("issue_taginfo", tagInfo);
                        startActivity(i);
                    } else {
                        goZhuanti(tagInfo);

                    }
                }
            }
        });
        return view;
    }

    private void goZhuanti(TagInfo tag) {
        Intent i = new Intent(this, BookColumnActivity.class);
        i.putExtra("is_tekan", 1);
        i.putExtra("book_deatail", tag);
        startActivity(i);
    }


    private void initData() {
        historyData = UserDataHelper.loadSearchHistory(this);
        Collections.reverse(historyData);
        handler.sendEmptyMessage(1);

        operateController = OperateController.getInstance(this);
        if (SlateDataHelper.getLevelByType(this, 1)) {
            operateController.getTagInfo("", "", "", "", GetTagInfoOperate.TAG_TYPE.SEARCH_HOTS, SlateBaseOperate.FetchApiType.USE_HTTP_FIRST, new FetchEntryListener() {

                @Override
                public void setData(Entry entry) {

                    if (entry != null && entry instanceof TagInfoList) {
                        hotDatas = (TagInfoList) entry;
                        handler.sendEmptyMessage(0);
                    }
                }
            });
            // 往期
            operateController.getLastIssueList("", new FetchEntryListener() {

                @Override
                public void setData(Entry entry) {
                    if (entry != null && entry instanceof TagInfoList) {
                        wangqiDatas = (TagInfoList) entry;
                        handler.sendEmptyMessage(0);
                    }
                }
            });
        }


    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:// 热门数据
                    hotLayout.setVisibility(View.VISIBLE);
                    hotView.removeAllViews();
                    if (ParseUtil.listNotNull(hotDatas.getList()) && ParseUtil.listNotNull(wangqiDatas.getList())) {
                        for (int i = 0; i < hotDatas.getList().size(); i++) {
                            if (i < 10)
                                hotView.addView(getTextView(hotDatas.getList().get(i), false), lp);
                        }
                        for (int i = 0; i < wangqiDatas.getList().size(); i++) {
                            if (i < 4) {
                                hotView.addView(getTextView(wangqiDatas.getList().get(i), true), lp);
                            }
                        }
                    }

                    break;

                case 1:// 历史数据
                    noResult.setVisibility(View.GONE);
                    if (ParseUtil.listNotNull(historyData)) {
                        hisLayout.setVisibility(View.VISIBLE);
                        hisAdapter.setData(historyData);
                        resultView.setVisibility(View.GONE);
                    } else {
                        hisLayout.setVisibility(View.GONE);
                    }
                    break;

                case 2:// 显示搜索结果
                    if (ParseUtil.listNotNull(resultData)) {
                        hotLayout.setVisibility(View.GONE);
                        hisLayout.setVisibility(View.GONE);
                        noResult.setVisibility(View.GONE);
                        resultView.setVisibility(View.VISIBLE);
                        resultAdapter.setData(resultData);
                    } else {
                        hotLayout.setVisibility(View.GONE);
                        hisLayout.setVisibility(View.GONE);
                        noResult.setVisibility(View.VISIBLE);
                        resultView.setVisibility(View.GONE);
                    }
                    break;
                case 3:// 重置搜索页面
                    if (SlateDataHelper.getLevelByType(SearchActivity.this, 1))
                        hotLayout.setVisibility(View.VISIBLE);
                    hisLayout.setVisibility(View.VISIBLE);
                    noResult.setVisibility(View.GONE);
                    resultView.setVisibility(View.GONE);
                    resultAdapter.clear();
                    break;

            }
        }
    };

    /**
     * 搜索
     *
     * @param msg
     */
    public void doSearch(String msg) {
        showLoadingDialog(true);
        operateController.getResult(msg, 0, 0, "", new FetchEntryListener() {
            @Override
            public void setData(Entry entry) {
                showLoadingDialog(false);
                if (entry instanceof TagArticleList) {
                    TagArticleList list = (TagArticleList) entry;
                    resultData.clear();
                    resultData.addAll(list.getArticleList());
                    handler.sendEmptyMessage(2);
                }
            }
        });
    }


    @Override
    public String getActivityName() {
        return SearchActivity.class.getName();
    }

    @Override
    public void reLoadData() {

    }

    @Override
    public Activity getActivity() {
        return SearchActivity.this;
    }


    /**
     * 显示取消按钮
     */
    private void showCancel() {
        do_cancel.setVisibility(View.VISIBLE);
        do_search.setVisibility(View.GONE);
    }


    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_done) {//搜索
            String msg = searchEdit.getText().toString();
            String str = msg.replaceAll(" ", "");//去掉所有空格
            if (!TextUtils.isEmpty(str)) {
                historyData = UserDataHelper.saveSearchHistory(this, msg);
                Collections.reverse(historyData);
                doSearch(str);
            } else {//关键词是空格 显示无匹配
                searchEdit.setText("");
            }
            hideKeyboard();
            showCancel();
        } else if (v.getId() == R.id.search_cancel) {//取消
            finish();
        } else if (v.getId() == R.id.s_his_more) {//清除历史记录
            UserDataHelper.cleanSearchHistory(this);
            historyData.clear();
            handler.sendEmptyMessage(1);
        } else if (v.getId() == R.id.s_hot_more) {// 往期列表页
            Intent i = new Intent(SearchActivity.this, IssueListActivity.class);
            i.putExtra("wangqi_datas", wangqiDatas);
            i.putExtra("hot_datas", hotDatas);
            startActivity(i);
        }


    }
}
