package cn.com.modernmediausermodel.vip;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.model.VipGoodList.VipGood;
import cn.com.modernmedia.pay.newlogic.PayHttpsOperate;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediausermodel.R;


/**
 * vip套餐升级
 * Created by Eva. on 2017/6/2.
 */

public class TaocanUpActivity extends BaseActivity implements View.OnClickListener {

    private ListView listView;
    private TaocanAdapter taocanAdapter;
    private PayHttpsOperate payHttpsOperate;
    private List<VipGood> pros = new ArrayList<>();
    private VipGood currentPro,toPro;
    private TextView my1, my2, to1, to2, total1, total2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tancan_up);
        initView();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    taocanAdapter.notifyDataSetChanged();
                    if (pros.size() > 0) {
                        toPro = pros.get(0);
                        taocanAdapter.checkPosition(0);
                    }

                    my1.setText(currentPro.getGoodName());
                    my2.setText(currentPro.getDurationLeft() + currentPro.getDurationUnit());
                    handler.sendEmptyMessage(0);
                    break;
                default:
                    int i = msg.what;
                    to1.setText(pros.get(i).getGoodName());
                    to2.setText(pros.get(i).getDurationAdd() + pros.get(i).getDurationUnit());
                    total1.setText(pros.get(i).getDurationTotal() + pros.get(i).getDurationUnit());
                    total2.setText("¥" + Double.valueOf(pros.get(i).getPirce()) / 100);
                    break;
            }
        }
    };


    private void initData() {
        showLoadingDialog(true);
        payHttpsOperate.vip2vipUpList(new FetchDataListener() {
            @Override
            public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                showLoadingDialog(false);
                if (isSuccess) {
                    try {
                        JSONObject j = new JSONObject(data);
                        currentPro = payHttpsOperate.parseVipGood(j.optJSONObject("good"));
                        payHttpsOperate.parseJson(j.optJSONArray("exchangeGood"), pros);

                        if (currentPro == null || !ParseUtil.listNotNull(pros)) {
                            showToast("获取升级套餐失败。");
                            finish();
                        } else handler.sendEmptyMessage(-1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject object = new JSONObject(data);
                        showToast(object.optString("desc"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initView() {
        payHttpsOperate = PayHttpsOperate.getInstance(this);
        findViewById(R.id.vip2vip_back).setOnClickListener(this);
        findViewById(R.id.taocan_notice1).setOnClickListener(this);
        findViewById(R.id.taocan_sure).setOnClickListener(this);
        listView = (ListView) findViewById(R.id.vipup_list);
        taocanAdapter = new TaocanAdapter(this);
        listView.setAdapter(taocanAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                taocanAdapter.checkPosition(position);
                toPro = pros.get(position);
                handler.sendEmptyMessage(position);
            }
        });

        my1 = (TextView) findViewById(R.id.taocan_my_1);
        my2 = (TextView) findViewById(R.id.taocan_my_2);
        to1 = (TextView) findViewById(R.id.taocan_to_1);
        to2 = (TextView) findViewById(R.id.taocan_to_2);
        total1 = (TextView) findViewById(R.id.taocan_total_1);
        total2 = (TextView) findViewById(R.id.taocan_total_2);
        to2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vip2vip_back) {
            finish();
        } else if (v.getId() == R.id.taocan_sure) {// 确认升级 去付款
            Intent i = new Intent(TaocanUpActivity.this, VipProductPayActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("product", toPro);
            i.putExtras(b);
            startActivity(i);
        } else if (v.getId() == R.id.taocan_to_2) {
            showNoticePop(R.drawable.vip_notice1);
        } else if (v.getId() == R.id.taocan_notice1) {
            showNoticePop(R.drawable.vip_notice2);
        }


    }


    @Override
    public Activity getActivity() {
        return null;
    }


    @Override
    public void reLoadData() {

    }

    @Override
    public String getActivityName() {
        return null;
    }

    /**
     * 可选套餐adapter
     */
    public class TaocanAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private int pos;

        public TaocanAdapter(Context context) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        public void checkPosition(int pos) {
            this.pos = pos;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return pros.size();
        }

        @Override
        public VipGood getItem(int position) {
            return pros.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final VipGood vipGood = pros.get(position);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_vip2vip, null);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.vip_2vip_title);
                holder.price = (TextView) convertView.findViewById(R.id.vip_2vip_price);
                holder.radioButton = (RadioButton) convertView.findViewById(R.id.vip_2vip_radio);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(vipGood.getGoodName());
            holder.price.setText("(" + vipGood.getShowPrice() + ")");
            if (position == pos) {
                holder.radioButton.setChecked(true);
            } else {
                holder.radioButton.setChecked(false);
            }
            return convertView;
        }

        private class ViewHolder {
            public TextView title;
            public TextView price;
            public RadioButton radioButton;
        }
    }

    /**
     * 显示notice pop
     */
    private void showNoticePop(int res) {

        final Dialog mDialog = new Dialog(this, R.style.CustomDialog);
        mDialog.show();
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        Window window = mDialog.getWindow();
        ImageView img = new ImageView(this);
        img.setPadding(50, 0, 50, 0);
        img.setImageResource(res);

        ViewGroup.LayoutParams pa = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        window.setContentView(img, pa);

        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });


    }
}
