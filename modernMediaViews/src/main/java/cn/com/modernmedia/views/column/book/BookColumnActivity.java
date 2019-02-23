package cn.com.modernmedia.views.column.book;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.SubscribeOrderList.SubscribeColumn;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.UserSubscribeListDb;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.index.IndexViewPagerItem;
import cn.com.modernmedia.views.listener.LoadCallBack;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.LoginActivity;
import cn.com.modernmediausermodel.vip.VipOpenActivity;

/**
 * 独立标签详情页面（带订阅）
 * <p/>
 * 3.3.0特刊入口不带订阅功能
 *
 * @author lusiyuan
 */
public class BookColumnActivity extends BaseActivity implements OnClickListener {
    private TextView title, book;
    private TagInfo tagInfo;
    private LinearLayout container;
    private IndexViewPagerItem indexViewPagerItem;

    private OperateController operateController;
    // 1:特刊栏目首页
    private int isTekan = 0;
    private ImageView payBanner;// 特刊页面付费遮挡

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_column_detail_for_book);
        operateController = OperateController.getInstance(this);
        tagInfo = (TagInfo) getIntent().getSerializableExtra("book_deatail");
        isTekan = getIntent().getIntExtra("is_tekan", 0);
        if (tagInfo != null) LogHelper.showSubcribeColumn(this, tagInfo.getTagName());// flurry
        initView();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (tagInfo.getEnablesubscribe() == 1) {
                        book.setVisibility(View.VISIBLE);
                        book.setOnClickListener(BookColumnActivity.this);
                    } else {
                        book.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 1:
                    payBanner.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    payBanner.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private void initData() {
        if (tagInfo != null) {
            title.setText(tagInfo.getColumnProperty().getCname());
            indexViewPagerItem.fetchData("", false, false, new LoadCallBack() {
                @Override
                public void onRefreshed(boolean success) {
                    tagInfo = indexViewPagerItem.getTagInfo();
                    handler.sendEmptyMessage(0);
                }

                @Override
                public void onLoaded(boolean success) {

                }
            }, null);
            container.removeAllViews();
            container.addView(indexViewPagerItem.fetchView());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        checkPayStatus();
    }

    private void initView() {
        book = (TextView) findViewById(R.id.detail_book);
        if (isTekan == 0) {
            book.setOnClickListener(this);
        } else book.setVisibility(View.INVISIBLE);

        payBanner = (ImageView) findViewById(R.id.pay_bannar_view);
        payBanner.setOnClickListener(this);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pay_bannar_view);
        int bwidth = bitmap.getWidth();
        int bHeight = bitmap.getHeight();

        int height = getResources().getDisplayMetrics().widthPixels * bHeight / bwidth;
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, height);

        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        payBanner.setLayoutParams(lp);
        findViewById(R.id.detail_back).setOnClickListener(this);

        title = (TextView) findViewById(R.id.detail_title);
        indexViewPagerItem = new IndexViewPagerItem(this, tagInfo, null);
        // indexListView = new TagIndexListView(this);
        container = (LinearLayout) findViewById(R.id.detail_list);
    }

    /**
     * 单个订阅操作
     */
    private void book() {
        LogHelper.subcribeColumn(this, tagInfo.getTagName());// flurry

        if (SlateDataHelper.getUserLoginInfo(this) != null) {
            for (TagInfo in : AppValue.bookColumnList.getList()) {
                if (in.getTagName().equals(tagInfo.getTagName())) {
                    return;
                }
            }
            SubscribeColumn cs = new SubscribeColumn(tagInfo.getTagName(), "", 0);
            OperateController.getInstance(this).saveSubscribeColumnSingle(Tools.getUid(this), SlateDataHelper.getToken(this), cs, new FetchEntryListener() {

                @Override
                public void setData(Entry entry) {

                    if (entry != null && entry instanceof ErrorMsg) {
                        ErrorMsg errorMsg = (ErrorMsg) entry;
                        if (errorMsg.getNo() == 0) {

                            AppValue.bookColumnList.getList().add(tagInfo);

                            // 更新数据库
                            UserSubscribeListDb.getInstance(BookColumnActivity.this).clearTable(Tools.getUid(BookColumnActivity.this));
                            UserSubscribeListDb.getInstance(BookColumnActivity.this).addEntry(AppValue.bookColumnList);
                            // 通知主页面更新topMenu
                            SlateApplication.loginStatusChange = true;
                            book.setText("已订阅");
                        } else showToast(errorMsg.getDesc());
                    }

                }
            });
        } else {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void reLoadData() {

    }

    @Override
    public String getActivityName() {
        return BookColumnActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return BookColumnActivity.this;
    }

    public void checkPayStatus() {
        Log.e("BookColumnActivity", "checkPayStatus");

        if (tagInfo.getIsPay() == 1 && !(SlateDataHelper.getLevelByType(this, 1))) {//
            // 如果是商周，并且需要付费
            handler.sendEmptyMessage(1);

        } else {
            handler.sendEmptyMessage(2);
        }
    }

    private void goPayActivity() {
        //        startActivity(new Intent(this, NewPayActivity.class));
        Intent i;
        if (SlateDataHelper.getUserLoginInfo(this) == null) {//未登录
            i = new Intent(this, LoginActivity.class);
        } else {//普通用户 非付费用户,非VIP
            i = new Intent(this, VipOpenActivity.class);
        }

        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.detail_book) {// 订阅
            book();
        } else if (v.getId() == R.id.detail_back) {
            finish();
        } else if (v.getId() == R.id.pay_bannar_view) {// 支付
            goPayActivity();
        }
    }


    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
