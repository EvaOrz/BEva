package cn.com.modernmedia.views;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.RelativeLayout.LayoutParams;

import com.umeng.analytics.MobclickAgent;

import cn.com.modernmedia.views.fav.NewFavView;
import cn.com.modernmediaslate.SlateBaseActivity;

/**
 * 收藏页
 *
 * @author zhuqiao
 */
public class FavoritesActivity extends SlateBaseActivity {
    private Context mContext;
    protected NewFavView mFavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        init();
    }

    private void init() {
        mFavView = new NewFavView(mContext);
        LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        setContentView(mFavView, lp);
    }

    public void onResume() {
        super.onResume();
        if (mFavView != null) mFavView.reLoad();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public String getActivityName() {
        return FavoritesActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

}
