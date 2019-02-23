package cn.com.modernmediausermodel.vip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.com.modernmedia.model.VipGoodList;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.R;

/**
 * VIP套餐内容
 *
 * @author: zhufei
 */

public class VipPlanDetailActivity extends SlateBaseActivity implements View.OnClickListener {

    private VipGoodList.VipGood vipGood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_vipplan);
        SlateApplication.getInstance().addActivity(this);
        findViewById(R.id.vip_plan_back).setOnClickListener(this);
        TextView textView = (TextView) findViewById(R.id.vip_plan_title);
        ListView listView = (ListView) findViewById(R.id.vip_plan_listView);
        Button pay = (Button) findViewById(R.id.vip_plan_btn);

        User user = SlateDataHelper.getUserLoginInfo(this);
        Intent intent = getIntent();
        if (intent.getExtras() == null) return;
        Bundle bundle = intent.getExtras();
        vipGood = (VipGoodList.VipGood) bundle.getSerializable("product");
        int right = bundle.getInt("right", 0);
        if (right == 10) {
            pay.setVisibility(View.GONE);
        }
        VipProductAdapter adapter = new VipProductAdapter(this,right);
        listView.setAdapter(adapter);
        if (vipGood != null) {
            adapter.setVipGood(vipGood, user);
            pay.setOnClickListener(this);
            if (!vipGood.getShowPrice().isEmpty()) {
                pay.setText(vipGood.getShowPrice());
            } else {
                pay.setText(getString(R.string.vip_pay_btn));
            }
            if (!vipGood.getGoodName().isEmpty()) {
                textView.setText(String.format(getString(R.string.vip_plan_detail), vipGood.getGoodName()));
            } else {
                textView.setText(getString(R.string.vip_open_package));
            }
        }
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

    @Override
    public String getActivityName() {
        return VipPlanDetailActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vip_plan_back) {
            finish();
        } else if (v.getId() == R.id.vip_plan_btn) {
            Intent i = new Intent(this, VipProductPayActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("product", vipGood);
            i.putExtras(b);
            startActivity(i);
        }
    }
}