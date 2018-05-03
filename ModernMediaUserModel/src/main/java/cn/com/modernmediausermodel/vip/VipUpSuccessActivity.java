package cn.com.modernmediausermodel.vip;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.utils.Utils;

import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.R;


/**
 * 升级VIP 成功界面
 *
 * @author: zhufei
 */
public class VipUpSuccessActivity extends SlateBaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vipup_success);
        TextView endtime = (TextView) findViewById(R.id.vip_up_endtime);
        TextView bar_title = (TextView) findViewById(R.id.vip_up_bar_title);
        TextView content = (TextView) findViewById(R.id.vip_up_content);
        bar_title.setText(getString(R.string.vip_up_title));
        content.setText(getString(R.string.vip_up_content));
        User user = SlateDataHelper.getUserLoginInfo(this);
        if (user != null) {
            endtime.setText(String.format(getString(R.string.vip_show_endtime), Utils.strToDate(SlateDataHelper.getUserLoginInfo(this).getVip_end_time())));
        }

        SlateApplication.loginStatusChange = true;
        findViewById(R.id.vip_up_code_complete).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vip_up_code_complete) {//完成
            this.setResult(4022);
            this.finish();
        }
    }

    @Override
    public String getActivityName() {
        return VipUpSuccessActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
