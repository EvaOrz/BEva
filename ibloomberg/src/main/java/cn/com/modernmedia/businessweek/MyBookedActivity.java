package cn.com.modernmedia.businessweek;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.model.ShangchengIndex.ShangchengIndexItem;
import cn.com.modernmedia.pay.newlogic.PayHttpsOperate;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 我的订阅页面
 * Created by Eva. on 2018/1/9.
 */

public class MyBookedActivity extends BaseActivity implements View.OnClickListener {
    private List<ShangchengIndexItem> datas = new ArrayList<>();
    private MybookAdapter mybookAdapter;
    private ListView listView;
    private LinearLayout noDataLayout;
    private PayHttpsOperate payHttpsOperate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booked);
        payHttpsOperate = PayHttpsOperate.getInstance(this);
        initView();
        initData(true);
    }

    private void initData(final boolean isInit) {
        if (isInit) showLoadingDialog(true);
        else showToast(R.string.recover_ing);
        payHttpsOperate.getMyBooks(this, new FetchDataListener() {
            @Override
            public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                if (isInit) showLoadingDialog(false);
                if (isSuccess) {

                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        if (jsonObject != null) {
                            JSONArray array = jsonObject.optJSONArray("order");
                            if (array != null && array.length() > 0) {
                                datas.clear();
                                for (int i = 0; i < array.length(); i++) {
                                    datas.add(payHttpsOperate.parseShangchengIndexItem(array.optJSONObject(i)));
                                }
                                if (!isInit) showToast(R.string.recover_success);
                            } else {
                                if (!isInit) showToast(R.string.no_recover);
                            }
                        } else {
                            if (!isInit) showToast(R.string.recover_faild);
                        }

                    } catch (JSONException e) {
                        if (!isInit) showToast(R.string.recover_faild);
                    }
                } else {
                    if (!isInit) showToast(R.string.recover_faild);
                }
                handler.sendEmptyMessage(0);
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (ParseUtil.listNotNull(datas)) {
                        noDataLayout.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        mybookAdapter.notifyDataSetChanged();
                    } else {
                        noDataLayout.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };


    private void initView() {
        findViewById(R.id.my_book_back).setOnClickListener(this);
        findViewById(R.id.my_book_recover).setOnClickListener(this);
        listView = findViewById(R.id.my_book_listiew);
        noDataLayout = findViewById(R.id.my_book_nomore);
        mybookAdapter = new MybookAdapter(this, datas);
        listView.setAdapter(mybookAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_book_back:
                finish();
                break;
            case R.id.my_book_recover:
                initData(false);
                break;
        }
    }

    @Override
    public void reLoadData() {

    }

    @Override
    public String getActivityName() {
        return MyBookedActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return MyBookedActivity.this;
    }
}
