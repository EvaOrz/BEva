package cn.com.modernmedia.businessweek;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonSplashActivity;
import cn.com.modernmedia.util.LogHelper;

/**
 * 应用启动页
 *
 * @author ZhuQiao
 */
public class SplashScreenActivity extends CommonSplashActivity {

    @Override
    protected void setContentViewById() {
        setContentView(R.layout.splash_screen);
        findViewById(R.id.splash_view).setBackgroundColor(Color.BLACK);
        ImageView huawei = (ImageView)findViewById(R.id.huawei_logo);
        if (CommonApplication.CHANNEL.equals("huawei")){
            huawei.setVisibility(View.VISIBLE);
        }else {
            huawei.setVisibility(View.GONE);
        }
        LogHelper.openApp(this);
    }

    @Override
    public String getActivityName() {
        return SplashScreenActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
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
