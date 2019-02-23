package cn.com.modernmediausermodel.vip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.modernmedia.pay.newlogic.PayHttpsOperate;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.R;

import static cn.com.modernmediausermodel.vip.VipProductPayActivity.ADDRESS;


/**
 * 4.1.0 vip邮寄地址
 *
 * @author: zhufei
 */
public class PostUserOrderInfoActivity extends SlateBaseActivity implements View.OnClickListener {
    private EditText name, phone, address;
    public static final int DELAY_TIME = 100;
    private boolean success = false;
    private TextView city;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADDRESS:
                    User error = (User) msg.obj;
                    if (error.getAddress() != null && !TextUtils.isEmpty(error.getAddress())) {
                        address.setText(error.getAddress());
                        name.setText(error.getRealname());
                        phone.setText(error.getPhone());
                        if (!TextUtils.isEmpty(error.getProvince())) {
                            city.setText(error.getProvince() + " " + error.getCity());
                        } else city.setText(user.getCity());
                    } else {
                        if (!user.getRealname().isEmpty())
                            name.setText(user.getRealname());
                        if (!user.getPhone().isEmpty())
                            phone.setText(user.getPhone());
                    }
                    break;
            }
        }
    };
    private PayHttpsOperate controller;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribe_one_year);
        user = SlateDataHelper.getUserLoginInfo(this);
        if (user == null) return;
        controller = PayHttpsOperate.getInstance(this);
        name = (EditText) findViewById(R.id.order_name_edit);
        phone = (EditText) findViewById(R.id.order_phone_edit);
        city = (TextView) findViewById(R.id.order_city_edit);
        address = (EditText) findViewById(R.id.order_address_edit);
        findViewById(R.id.order_post).setOnClickListener(this);
        findViewById(R.id.order_back).setOnClickListener(this);
        city.setOnClickListener(this);
        //优先联网获取
        getAddressList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        //延迟100毫秒，弹出键盘
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) address.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(address, 0);
            }
        };
        handler.postDelayed(runnable, DELAY_TIME);
    }

    @Override
    public String getActivityName() {
        return PostUserOrderInfoActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.order_post) {//提交
            String order_name = name.getText().toString();
            String order_phone = phone.getText().toString();
            String order_city = city.getText().toString();
            String order_address = address.getText().toString();

            String str = order_name.replaceAll(" ", "");//去掉所有空格
            if (TextUtils.isEmpty(str) || str.length() <= 0
                    || TextUtils.isEmpty(order_phone) || order_phone.length() <= 0
                    || TextUtils.isEmpty(order_city) || order_city.length() <= 0
                    || TextUtils.isEmpty(order_address) || order_address.length() <= 0) {
                showToast(R.string.order_error_null);
                return;
            }
            if (!(order_phone.length() == 11) || !checkIsPhone(order_phone)) {
                showToast(R.string.order_error_phone_number);
                return;
            }
            showLoadingDialog(true);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showLoadingDialog(false);
                }
            }, 900);
            if (!TextUtils.isEmpty(SlateDataHelper.getAddressId(this))) {//修改
                PayHttpsOperate.getInstance(this).addressEdit(order_name, order_phone, order_city, order_address, "", SlateDataHelper.getAddressId(this), new FetchDataListener() {
                    @Override
                    public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                        if (isSuccess) {
                            parseAddress(data);
                        }
                    }
                });
            } else {//添加
                PayHttpsOperate.getInstance(this).addressAdd(order_name, order_phone, order_city, order_address, "", new FetchDataListener() {
                    @Override
                    public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                        if (isSuccess) {
                            parseAddress(data);
                        }
                    }
                });
            }
        } else if (v.getId() == R.id.order_back) {
            finish();
        } else if (v.getId() == R.id.order_city_edit) {
            changeDistrict(this, city);
        }
    }

    //获取用户邮寄地址
    private void getAddressList() {
        showLoadingDialog(true);
        controller.addressList(new FetchDataListener() {
            @Override
            public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                showLoadingDialog(false);
                if (isSuccess) {
                    JSONObject j = null;
                    try {
                        j = new JSONObject(data);
                        JSONObject jsonObj = j.optJSONObject("error");
                        JSONArray array = j.optJSONArray("useraddress");
                        JSONObject obj = array.optJSONObject(0);
                        SlateDataHelper.setAddressId(PostUserOrderInfoActivity.this, obj.optInt("id") + "");
                        user.setRealname(obj.optString("name"));
                        user.setProvince(obj.optString("province"));
                        user.setPhone(obj.optString("phone"));
                        user.setCity(obj.optString("city"));
                        user.setAddress(obj.optString("address"));
                        if (jsonObj.optInt("no") == 200) {
                            Message msg = new Message();
                            msg.what = ADDRESS;
                            msg.obj = user;
                            mHandler.sendMessage(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void parseAddress(String data) {
        try {
            JSONObject obj = new JSONObject(data);
            JSONObject jsonObject = obj.optJSONObject("error");
            JSONArray array = obj.optJSONArray("useraddress");
            JSONObject object = array.optJSONObject(0);
            SlateDataHelper.setAddressId(PostUserOrderInfoActivity.this, obj.optInt("id") + "");
            user.setRealname(object.optString("name"));
            user.setProvince(object.optString("province"));
            user.setPhone(object.optString("phone"));
            user.setCity(object.optString("city"));
            user.setAddress(object.optString("address"));
            if (jsonObject.getInt("no") == 200) {
                success = true;
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doIntent();
                }
            }, 1000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void changeDistrict(Context context, final TextView district_textview) {
        ChangeDistrictDialog mChangeAddressDialog = new ChangeDistrictDialog(context);
        mChangeAddressDialog.setAddress("北京", "朝阳");
        mChangeAddressDialog.show();
        mChangeAddressDialog
                .setAddresskListener(new ChangeDistrictDialog.OnAddressCListener() {

                    @Override
                    public void onClick(String province, String city) {
                        district_textview.setText(province + " " + city);
                    }
                });
    }

    private void doIntent() {
        if (success) {
            Intent i = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("error", user);
            i.putExtras(bundle);
            this.setResult(RESULT_OK, i);
            this.finish();
        } else showToast(R.string.save_fail);
    }

    //手机号数字正则匹配
    public static boolean checkIsPhone(String data) {
        String str1 = "^\\d{11}$";
        Pattern p = Pattern.compile(str1);
        Matcher m = p.matcher(data);
        return m.matches();

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
