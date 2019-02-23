package cn.com.modernmediausermodel.vip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.VipGoodList;
import cn.com.modernmedia.pay.newlogic.PayHttpsOperate;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;


/**
 * vip交易详情界面
 *
 * @author: zhufei
 */
public class VipPayResultActivity extends SlateBaseActivity implements View.OnClickListener {
    private ImageView pay_result_img, back;
    private TextView pay_result, pay_money, pay_style, pay_product, pay_id, pay_time, pay_complete;
    private int payStatus;// 支付状态 10：成功 11 失败
    private String zhifubao_status;
    private User user;
    private LinearLayout address_info_linear;
    private TextView name, phone, address;
    private boolean pay_success = false;
    private boolean postCard = false;
    public static final int CODE_RESULT = 10;

    private VipGoodList.VipGood product;
    private String tradeNum, payType;// 订单号|支付方式


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paycallback);
        SlateApplication.getInstance().addActivity(this);
        user = SlateDataHelper.getUserLoginInfo(this);

        initView();
        initData();
    }

    private void initData() {
        Intent i = getIntent();
        if (i == null) return;
        if (i.getExtras() != null) {
            Bundle bundle = i.getExtras();
            product = (VipGoodList.VipGood) bundle.getSerializable("product");
            if (product != null) {
                tradeNum = bundle.getString("oid");
                payType = bundle.getString("style");
                pay_money.setText(VipProductPayActivity.formatPrice(product.getPirce()));
                pay_product.setText(product.getGoodName());
                pay_style.setText(payType);
                pay_id.setText(tradeNum);
                pay_time.setText(bundle.getString("time"));
                postCard = bundle.getBoolean("postCard");
                if (!TextUtils.isEmpty(bundle.getString("resultStatus"))) {//支付宝结果码，微信为"10"
                    zhifubao_status = bundle.getString("resultStatus");
                    if (zhifubao_status != null) {
                        if (zhifubao_status.equals("9000") || zhifubao_status.equals("0")) {
                            pay_success = true;
                            SlateApplication.loginStatusChange = true;//用于刷新用户界面
                            showPaySuccess();
                            back.setVisibility(View.GONE);
                            getUser();//刷新用户数据
                            if (!TextUtils.isEmpty(bundle.getString("name")))
                                name.setText(bundle.getString("name"));
                            if (!TextUtils.isEmpty(bundle.getString("phone")))
                                phone.setText(bundle.getString("phone"));
                            if (!TextUtils.isEmpty(bundle.getString("address")))
                                address.setText(bundle.getString("address"));
                            if (!TextUtils.isEmpty(name.getText().toString()) && postCard) {//有配送地址,选择配送显示
                                address_info_linear.setVisibility(View.VISIBLE);
                            }
                            // vip信息不完整且 购买的是vip产品
                            if (user.getCompletevip() != 1 && product.getGoodId().equals("app1_vip"))
                                showInfoDialog(VipPayResultActivity.this, CODE_RESULT);
                        } else if (zhifubao_status.equals("6001") || zhifubao_status.equals("-2")) {
                            showPayCancel();
                        } else showPayFail();
                    }
                }
            }
        }
    }

    private void initView() {
        pay_result_img = (ImageView) findViewById(R.id.vip_paycallback_img);//交易结果图片
        pay_result = (TextView) findViewById(R.id.vip_paycallback_result);//交易结果
        pay_money = (TextView) findViewById(R.id.vip_paycallback_money);//交易金额
        pay_style = (TextView) findViewById(R.id.vip_paycallback_style);//付款方式
        pay_product = (TextView) findViewById(R.id.vip_paycallback_product);//商品名称
        pay_id = (TextView) findViewById(R.id.vip_paycallback_id);//订单编号
        pay_time = (TextView) findViewById(R.id.vip_paycallback_time);//创建时间
        pay_complete = (TextView) findViewById(R.id.vip_paycallback_complete);//完成或者关闭
        address_info_linear = (LinearLayout) findViewById(R.id.vip_address_linear);//配送信息
        name = (TextView) findViewById(R.id.vip_paycallback_name);//收件人
        phone = (TextView) findViewById(R.id.vip_paycallback_phone);//手机号
        address = (TextView) findViewById(R.id.vip_paycallback_address);//收件地址
        back = (ImageView) findViewById(R.id.vip_paycallback_back);
        back.setOnClickListener(this);
        findViewById(R.id.vip_paycallback_complete).setOnClickListener(this);
    }


    private void showPaySuccess() {
        pay_result_img.setImageResource(R.drawable.vippaysuccess);
        pay_result.setText(getResources().getString(R.string.vip_pay_success));
        pay_complete.setText(getString(R.string.complete));
    }

    private void showPayFail() {
        pay_result_img.setImageResource(R.drawable.vippayfail);
        pay_result.setText(getResources().getString(R.string.vip_pay_fail));
        pay_complete.setText(getString(R.string.vip_pay_close));
    }

    private void showPayCancel() {
        pay_result_img.setImageResource(R.drawable.vippayfail);
        pay_result.setText(getResources().getString(R.string.vip_pay_cancel));
        pay_complete.setText(getString(R.string.vip_pay_close));
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            /**
             * 正常：取endtime, 异常：取状态
             */
            if (msg.what == 0) {
                showLoadingDialog(false);
                PayHttpsOperate.getInstance(VipPayResultActivity.this).notifyServer(new FetchDataListener() {
                    @Override
                    public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                        if (isSuccess) {
                            Log.e("更新订单状态成功2", "数据--" + data);
                            SlateDataHelper.clearOrder(VipPayResultActivity.this, PayHttpsOperate.NEW_WEIXIN_KEY);
                            payStatus = PayHttpsOperate.getInstance(VipPayResultActivity.this).saveLevel(data);
                            Log.e("pay", "payStatus" + payStatus);
                            handler.sendEmptyMessage(1);
                        }
                    }
                });
            } else if (msg.what == 1) {
                if (payStatus == 10) {// 支付成功
                    SlateApplication.loginStatusChange = true;//用于刷新用户界面
                    showPaySuccess();
                } else if (payStatus == 11) {// 支付失败
                    showPayFail();
                } else {// 支付处理中

                }
            }
        }
    };

    /**
     * {
     * "result":{
     * “success": true/false,
     * "data":{
     * "uid":uid,              // uid
     * "appid":appid,          // appid
     * "pid":pid,              // pid
     * "tradeNum":tradeNum,    // 订单号
     * "tradeName":tradeName,  // 产品名称
     * "tradeType":tradeType,  // 支付方式(1微信,2阿里,5applePay)
     * "tradePrice":tradePrice // 价格
     * }
     * },
     * "error": error
     * }
     */
    private void notifyHtml(int tradeType, String success) {
        if (CommonApplication.asynExecuteCommandListener == null) return;
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("uid", SlateDataHelper.getUid(this));
            data.put("appid", CommonApplication.APP_ID);
            data.put("pid", product.getGoodId());
            data.put("tradeNum", tradeNum);
            data.put("tradeName", product.getGoodName());
            data.put("tradeType", tradeType);
            data.put("tradePrice", product.getPirce());
            result.put("data", data);
            result.put("success", success);
            Log.e("asynExecuteListener", result.toString());

        } catch (JSONException e) {

        }

        CommonApplication.asynExecuteCommandListener.onCallBack(result.toString());
    }


    /*显示微信订单详情*/
    private void showWeiXinOrderDetail() {
        String json = SlateDataHelper.getOrder(VipPayResultActivity.this, PayHttpsOperate.NEW_WEIXIN_KEY);
        if (json != null)

            try {
                JSONObject jsonObject = new JSONObject(json);
                Log.e("pay", "json" + json);
                pay_id.setText(jsonObject.optString("oid"));
                pay_product.setText(jsonObject.optString("product_name"));
                pay_money.setText(VipProductPayActivity.formatPrice(Integer.valueOf(jsonObject.optString("product_price"))));
                pay_style.setText(jsonObject.optString("pay_style"));
                pay_time.setText(jsonObject.optString("time"));
                postCard = jsonObject.optBoolean("postCard");
                if (!TextUtils.isEmpty(jsonObject.optString("name"))) {
                    name.setText(jsonObject.optString("name"));
                }
                if (!TextUtils.isEmpty(jsonObject.optString("phone"))) {
                    phone.setText(jsonObject.optString("phone"));
                }
                if (!TextUtils.isEmpty(jsonObject.optString("address"))) {
                    address.setText(jsonObject.optString("address"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    //  物理返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        doComplete();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vip_paycallback_back) {//返回
            finish();
        } else if (v.getId() == R.id.vip_paycallback_complete) {//完成退出购买流程的页面
            doComplete();
        }
    }

    private void doComplete() {
        // 不是购买vip 直接finish
        if (!product.getGoodId().equals("app1_vip")) {
            SlateApplication.getInstance().exitActivity();
            finish();
            return;
        }
        if (pay_success) {
            pay_success = false;
            Intent i = new Intent(this, MyVipActivity.class);
            startActivity(i);
            // 前页面返回finish当前页面

        }
        SlateApplication.getInstance().exitActivity();
    }

    @Override
    public String getActivityName() {
        return VipPayResultActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        //0为支付成功，-2取消支付，-1出错
        int status = getIntent().getIntExtra("pay_end_from_weixinpay", 100);
        if (zhifubao_status == null) {//从微信回调页返回是null
            showLoadingDialog(true);
            handler.sendEmptyMessage(0);
            showWeiXinOrderDetail();
        }
        if (status == 0) {
            pay_success = true;
            showPaySuccess();
            back.setVisibility(View.GONE);
            getUser();//刷新用户数据
            if (!TextUtils.isEmpty(name.getText().toString()) && postCard) {//有配送地址,选择配送显示
                address_info_linear.setVisibility(View.VISIBLE);
            }
            if (user.getCompletevip() != 1 && product.getGoodId().equals("app1_vip")) {// 网页跳转不需要弹补充信息对话框
                showInfoDialog(VipPayResultActivity.this, CODE_RESULT);
            }
        } else if (status == -1) {
            showPayFail();
        } else if (status == -2) {
            showPayCancel();
        }
        Log.e("支付结果：onResume", status + "");
        notifyHtml(payType.endsWith("支付宝") ? 2 : 1, status == 0 ? "true" : "false");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 显示补充会员对话框
     *
     * @param context
     */
    public static void showInfoDialog(final Context context, final int code) {
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(context);
        normalDialog.setIcon(R.drawable.icon);
        normalDialog.setTitle(context.getString(R.string.vip_info_dialog));
        normalDialog.setNegativeButton(context.getString(R.string.vip_info_write), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(context, VipInfoActivity.class);
                i.putExtra("code", code);
                context.startActivity(i);
                if (code == CODE_RESULT) LogHelper.checkVipUpdateInfo(context);

            }
        });
        normalDialog.setPositiveButton(R.string.vip_info_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LogHelper.checkVipUpdateInfoIgnore(context);

            }
        });
        normalDialog.show();
    }

    private void getUser() {//刷新用户数据
        showLoadingDialog(true);
        UserOperateController.getInstance(VipPayResultActivity.this).getInfoByIdAndToken(user.getUid(), user.getToken(), new UserFetchEntryListener() {
            @Override
            public void setData(Entry entry) {
                showLoadingDialog(false);
                if (entry != null && entry instanceof User) {
                    User tempUser = (User) entry;
                    ErrorMsg error = tempUser.getError();
                    // 取得成功
                    if (error.getNo() == 0 && !TextUtils.isEmpty(tempUser.getUid())) {
                        user = tempUser;
                    }
                    SlateDataHelper.saveUserLoginInfo(VipPayResultActivity.this, user);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
