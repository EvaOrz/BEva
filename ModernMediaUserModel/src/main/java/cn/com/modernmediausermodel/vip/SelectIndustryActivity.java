package cn.com.modernmediausermodel.vip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.umeng.analytics.MobclickAgent;

import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediausermodel.R;

/**
 * 录入vip 选择行业
 *
 * @author: zhufei
 */
public class SelectIndustryActivity extends SlateBaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industry);
        ListView listView = (ListView) findViewById(R.id.industry_listview);
        Intent intent = getIntent();
        VipInfo.VipCategory industryList = (VipInfo.VipCategory) intent.getSerializableExtra("industry");
        IndustryAdapter adapter = new IndustryAdapter(this, industryList);
        findViewById(R.id.industry_back).setOnClickListener(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                VipInfo.VipCategory vipCategory = (VipInfo.VipCategory) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putString("industry_category", vipCategory.category);
                intent.putExtras(bundle);
                SelectIndustryActivity.this.setResult(RESULT_OK, intent);
                SelectIndustryActivity.this.finish();
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.industry_back) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            intent.putExtras(bundle);
            SelectIndustryActivity.this.setResult(RESULT_OK, intent);
            SelectIndustryActivity.this.finish();
        }
    }

    @Override
    public String getActivityName() {
        return SelectIndustryActivity.class.getName();
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
