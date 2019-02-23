package cn.com.modernmedia.views;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.index.IndexViewPagerItem;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 专栏作者主页
 *
 * @author Eva.
 */
public class AuthorCenterActivity extends BaseActivity implements
        OnClickListener {

    private ArticleItem data;
    private LinearLayout container;
    private IndexViewPagerItem indexViewPagerItem;
    private ImageView avatar;
    private TextView name, desc, weixin;

    private OperateController operateController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_center);
        operateController = OperateController.getInstance(this);
        data = (ArticleItem) getIntent().getSerializableExtra("author_info");

        initView();
        initData();
    }

    private void initData() {
        if (indexViewPagerItem != null) {
            indexViewPagerItem.fetchData("", false, false, null, null);
            container.removeAllViews();
            container.addView(indexViewPagerItem.fetchView());
        }
    }

    private void initView() {
        findViewById(R.id.author_center_back).setOnClickListener(this);
        avatar = (ImageView) findViewById(R.id.author_center_avatar);
        name = (TextView) findViewById(R.id.author_center_name);
        desc = (TextView) findViewById(R.id.author_center_desc);
        weixin = (TextView) findViewById(R.id.author_center_weixin);

        findViewById(R.id.author_center_info).setBackgroundColor(
                data.getZhuanlanAuthor().getColor());
        UserTools
                .setAvatar(this, data.getZhuanlanAuthor().getPicture(), avatar);
        name.setText(data.getZhuanlanAuthor().getName());
        desc.setText(data.getZhuanlanAuthor().getDesc());

        TagInfo tagInfo = new TagInfo();
        tagInfo.setTagName(data.getZhuanlanAuthor().getId());

        indexViewPagerItem = new IndexViewPagerItem(this, tagInfo, null);
        container = (LinearLayout) findViewById(R.id.detail_list);
    }

    @Override
    public void reLoadData() {

    }

    @Override
    public String getActivityName() {
        return AuthorCenterActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return AuthorCenterActivity.this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.author_center_back) {
            finish();
        }
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
