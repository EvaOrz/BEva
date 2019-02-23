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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalBitmap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.UrlMaker;
import cn.com.modernmedia.model.VipGoodList;
import cn.com.modernmedia.pay.Base64;
import cn.com.modernmedia.pay.PayResult;
import cn.com.modernmedia.pay.newlogic.PayHttpsOperate;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.weixin.MD5;
import cn.com.modernmedia.widget.EvaSwitchBar;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.LoginActivity;
import cn.com.modernmediausermodel.R;


/**
 * VIP 在线支付页面
 * <p>
 * 应用内：带着product信息跳转、只有uid的slate协议跳转
 * 应用外：universe link 需要通过pid获取product信息
 *
 * @author: zhufei
 */
public class VipProductPayActivity extends SlateBaseActivity implements View.OnClickListener, EvaSwitchBar.OnChangeListener {
    private static final int WEIXIN_PAY = 1;
    private static final int ZHIFUBAO_PAY = 2;

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;
    protected static final int ADDRESS = 3;
    private static final int PID = 4;
    private int pay_style = 0;
    private RadioButton weixin_pay, zhifubao_pay;
    private TextView pay_time;
    private PayHttpsOperate controller;
    private static VipGoodList.VipGood product;
    private String currentPid;
    private static User user;
    private JSONObject object, ajsonObject;
    private EditText nameEdit, phoneEdit;
    private boolean gotoPay = false;// onresume页面是否去pay

    // 微信参数
    public static final String APP_KEY = "b2eujfhVFiCRQhbnmrYcdkGPWvv3mZen";// PaySignKey（API密钥）
    private PayReq weixinReq;
    private final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
    private static String outNo;

    private TextView vip_post_detail, vip_address_detail;
    private static Boolean needCheckAdress = false;  //需要检查邮寄地址
    protected static final int REQ_CODE = 102;
    private RelativeLayout postcard_relative, address_relative, weixin_relative, zhifubao_relative;
    private TextView pay_money, pay_name, weixin_text, zhifubao_text;
    private ImageView weixin_recommend, zhifubao_recommend;

    private LinearLayout vip_phone_info;// 是否填写电话信息栏
    private EvaSwitchBar cardSwicth;
    //
    public static String needTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vipproductpay);
        SlateApplication.getInstance().addActivity(this);
        controller = PayHttpsOperate.getInstance(this);
        user = SlateDataHelper.getUserLoginInfo(this);

        Intent intent = getIntent();
        currentPid = intent.getStringExtra("pay_pid");
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.getSerializable("product") != null && bundle.getSerializable("product") instanceof VipGoodList.VipGood)
            product = (VipGoodList.VipGood) bundle.getSerializable("product");
        if (!TextUtils.isEmpty(currentPid)) {// 协议跳转
            getProduct();
        } else {//
            /**
             * universe link
             */
            if (product == null) {
                Bundle bb = intent.getBundleExtra("html_pay");
                if (bb != null) {
                    if (bb.getSerializable("product") != null && bb.getSerializable("product") instanceof VipGoodList.VipGood)
                        product = (VipGoodList.VipGood) bb.getSerializable("product");
                    needTel = bb.getString("needTel");
                }
            }
            /**
             * universe link 需要判断登录状态
             */
            if (user == null) {
                Intent i = new Intent(this, LoginActivity.class);
                i.putExtra("html_pay", bundle);
                startActivityForResult(i,12345);
            }
        }

        initView();
        initPost();
        weixinReq = new PayReq();// 微信req请求
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        user = SlateDataHelper.getUserLoginInfo(this);
        if (TextUtils.isEmpty(currentPid) && user != null && !gotoPay) {
            if (TextUtils.isEmpty(needTel)) getAddressList();
            initData();
        }
        pay_time.setText(getTime(getString(R.string.date_format_time)));//更新下单时间

    }

    /**
     *
     */
    private void getProduct() {
        controller.getProduct(this, currentPid, new FetchDataListener() {
            @Override
            public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                if (isSuccess) {
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        JSONArray array = jsonObject.optJSONArray("good");
                        List<VipGoodList.VipGood> list = new ArrayList<>();
                        list = controller.parseJson(array, list);
                        if (ParseUtil.listNotNull(list)) {
                            product = list.get(0);
                            mHandler.sendEmptyMessage(PID);
                        }
                    } catch (JSONException e) {

                    }

                }
            }
        });
    }

    public static String formatPrice(int msg) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(Double.valueOf(msg) / 100);
    }

    private void initData() {
        if (!user.getSend().isEmpty() && !user.getSend().equals("0") || product.getShow_address_input() == 0) {//已邮寄，不显示 || 不需要显示地址
            address_relative.setVisibility(View.GONE);
            postcard_relative.setVisibility(View.GONE);
        } else {
            if (product.getNeedAddress() == 0) {
                address_relative.setVisibility(View.VISIBLE);
                postcard_relative.setVisibility(View.VISIBLE);
            } else {
                address_relative.setVisibility(View.VISIBLE);
                postcard_relative.setVisibility(View.GONE);
                needCheckAdress = true;
            }

        }
        if (!needCheckAdress) address_relative.setVisibility(View.GONE);
        cardSwicth.setChecked(needCheckAdress);

        object = new JSONObject();
        ajsonObject = new JSONObject();
        try {
            object.put("uid", SlateDataHelper.getUid(this));
            object.put("usertoken", SlateDataHelper.getToken(this));
            object.put("pid", product.getGoodId());
            object.put("appid", ConstData.getInitialAppId() + "");
            object.put("marketkey", CommonApplication.CHANNEL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pay_name.setText(product.getGoodName());
        pay_money.setText(formatPrice(product.getPirce()));
        pay_time.setText(getTime(getString(R.string.date_format_time)));

        initPayType();

    }

    private void initPayType() {
        if (ParseUtil.listNotNull(product.getPayTypeLists())) {//推荐图标

            for (VipGoodList.VipPayType p : product.getPayTypeLists()) {
                //支付过滤
                if (p.getPayTypeId().equals("1")) {// 微信
                    weixin_relative.setVisibility(View.VISIBLE);
                    weixin_text.setText(p.getPayTypeName());
                    if (p.getIsRecommend().equals("1"))
                        FinalBitmap.create(this).display(weixin_recommend, p.getRecommendIconUrl());

                } else if (p.getPayTypeId().equals("2")) { // 支付宝
                    zhifubao_relative.setVisibility(View.VISIBLE);
                    zhifubao_text.setText(p.getPayTypeName());
                    if (p.getIsRecommend().equals("1"))
                        FinalBitmap.create(this).display(zhifubao_recommend, p.getRecommendIconUrl());

                }
            }
        }

        /**
         * 网页跳转支付页面
         */
        if (!TextUtils.isEmpty(needTel)) {
            weixin_relative.setVisibility(View.VISIBLE);
            zhifubao_relative.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vip_pay_back) {
            finish();
        } else if (v.getId() == R.id.vip_pay_btn) {//确认支付
            /**
             * 如果是网页跳转，需要填写姓名和电话的支付方式
             */
            if (TextUtils.isEmpty(needTel)) {
                user.setRealname(nameEdit.getText().toString());
                user.setPhone(nameEdit.getText().toString());
            }

            if (pay_style == 0) {
                showToast(R.string.vip_pay_style_null);
            } else if (needCheckAdress && TextUtils.isEmpty(vip_post_detail.getText().toString())) {
                showToast(R.string.vip_open_address_null);
            } else if (pay_style == WEIXIN_PAY) {
                boolean sIsWXAppInstalledAndSupported = msgApi.isWXAppInstalled() && msgApi.isWXAppSupportAPI();
                if (!sIsWXAppInstalledAndSupported) {
                    showLoadingDialog(false);
                    Tools.showToast(this, R.string.no_weixin);
                    return;
                }
                dopay(WEIXIN_PAY);
            } else if (pay_style == ZHIFUBAO_PAY) {
                check();//查询终端设备是否存在支付宝认证账户
                dopay(ZHIFUBAO_PAY);
            }
        } else if (v.getId() == R.id.vip_pay_weixin_relative) {//微信支付
            pay_style = WEIXIN_PAY;
            weixin_pay.setChecked(true);
            zhifubao_pay.setChecked(false);
            zhifubao_pay.setClickable(false);
        } else if (v.getId() == R.id.vip_pay_zhifubao_relative) {//支付宝支付
            pay_style = ZHIFUBAO_PAY;
            weixin_pay.setChecked(false);
            weixin_pay.setClickable(false);
            zhifubao_pay.setChecked(true);

        } else if (v.getId() == R.id.vip_open_postcard) {
            Intent intent = new Intent(this, PostUserOrderInfoActivity.class);
            startActivityForResult(intent, REQ_CODE);
        }
    }

    /**
     * 9000 订单支付成功
     * 8000 正在处理中
     * 4000 订单支付失败
     * 6001 用户中途取消
     * 6002 网络连接出错
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    Log.e("*******支付宝resultInfo", resultInfo);

                    final String resultStatus = payResult.getResultStatus();
                    Log.e("*******支付宝resultStatus", resultStatus);

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        showLoadingDialog(true);
                        controller.notifyServer(PayHttpsOperate.SUCCESS, PayHttpsOperate.NEW_ALI_KEY, new FetchDataListener() {
                            @Override
                            public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                                if (isSuccess) {
                                    showLoadingDialog(false);
                                    LogHelper.paySuccess(VipProductPayActivity.this);
                                    Log.e("更新订单状态成功", "清除数据" + data);
                                    SlateDataHelper.clearOrder(VipProductPayActivity.this, PayHttpsOperate.NEW_ALI_KEY);
                                    controller.saveLevel(data);
                                    //跳转交易详情界面
                                    payintent(VipProductPayActivity.this, getString(R.string.zhifubaopay), resultStatus);
                                }
                            }
                        });
                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        controller.notifyServer(PayHttpsOperate.CANCEL, PayHttpsOperate.NEW_ALI_KEY);
                        payintent(VipProductPayActivity.this, getString(R.string.zhifubaopay), resultStatus);
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            showToast("支付结果确认中");
                            controller.notifyServer(PayHttpsOperate.PAYING, PayHttpsOperate.NEW_ALI_KEY);
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            controller.notifyServer(PayHttpsOperate.ERROR, PayHttpsOperate.NEW_ALI_KEY);
                            payintent(VipProductPayActivity.this, getString(R.string.zhifubaopay), resultStatus);
                        }
                        showLoadingDialog(false);
                    }
                    break;
                case SDK_CHECK_FLAG:
                    break;
                case ADDRESS:
                    User errorMsg = (User) msg.obj;
                    vip_address_detail.setVisibility(View.VISIBLE);
                    vip_post_detail.setText(errorMsg.getRealname() + " " + errorMsg.getPhone());
                    vip_address_detail.setText(errorMsg.getCity() + errorMsg.getAddress());
                    break;
                case PID:
                    initData();
                    break;
            }
        }

    };

    /**
     * 获取订单信息
     *
     * @param type
     */
    private void doUpload(final int type) {
        try {
            if (needCheckAdress) {
                object.put("address", ajsonObject);
            } else {
                object.put("address", null);
            }
            Log.e("11", object.toString());
            controller.requestHttpPost(UrlMaker.newGetOrder(type), object, new FetchDataListener() {
                @Override
                public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                    showLoadingDialog(false);
                    if (isSuccess) {
                        try {
                            final JSONObject jsonObject = new JSONObject(data);
                            Log.e("获取订单", data);
                            if (jsonObject.optInt("error_no") == 0) {
                                JSONObject order = jsonObject.optJSONObject("data");
                                outNo = order.optString("oid");
                                JSONObject paydata = order.optJSONObject("paydata");
                                if (paydata != null) {
                                    gotoPay = true;// 跳去支付

                                    if (type == WEIXIN_PAY) {
                                        // 预支付订单
                                        weixinPay(paydata.optString("prepayid"));
                                        controller.saveOrder(VipProductPayActivity.this, user, PayHttpsOperate.NEW_WEIXIN_KEY, outNo, "", PayHttpsOperate.PAYING, product, getString(R.string.weixinpay), getTime(getString(R.string.date_format_time)), user.getRealname(), user.getPhone(), user.getCity() + user.getAddress(), needCheckAdress);
                                        /**
                                         * 跳往支付结果页面，状态：订单处理中..
                                         */
                                        //                                        payintent(getString(R.string.weixinpay), "10");
                                    } else if (type == ZHIFUBAO_PAY) {
                                        // 签名
                                        String aliOrderInfo = new String(Base64.decode(paydata.optString("request_str")));
                                        Log.e("支付宝base64jiemi", aliOrderInfo);
                                        zhifubaoPay(aliOrderInfo);
                                        controller.saveOrder(VipProductPayActivity.this, user, PayHttpsOperate.NEW_ALI_KEY, outNo, "", PayHttpsOperate.PAYING);
                                    }
                                } else {
                                    showToast(R.string.order_make_faild);
                                }
                            } else if (jsonObject.optInt("error_no") == 500002) {//判断礼物库存
                                final AlertDialog.Builder normalDialog = new AlertDialog.Builder(VipProductPayActivity.this);
                                normalDialog.setIcon(R.drawable.icon);
                                normalDialog.setTitle(getString(R.string.vip_pay_dialog));
                                normalDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                normalDialog.setPositiveButton(R.string.do_pay, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        JSONObject order = jsonObject.optJSONObject("data");
                                        outNo = order.optString("oid");
                                        JSONObject paydata = order.optJSONObject("paydata");
                                        if (paydata != null) {
                                            if (type == WEIXIN_PAY) {
                                                // 预支付订单
                                                weixinPay(paydata.optString("prepayid"));
                                                controller.saveOrder(VipProductPayActivity.this, user, PayHttpsOperate.NEW_WEIXIN_KEY, outNo, "", PayHttpsOperate.PAYING, product, getString(R.string.weixinpay), getTime(getString(R.string.date_format_time)), user.getRealname(), user.getPhone(), user.getCity() + user.getAddress(), needCheckAdress);
                                                /**
                                                 * 跳往支付结果页面，状态：订单处理中..
                                                 */
                                                //                                                payintent(getString(R.string.weixinpay), "");
                                            } else if (type == ZHIFUBAO_PAY) {
                                                // 签名
                                                String aliOrderInfo = new String(Base64.decode(paydata.optString("request_str")));
                                                Log.e("支付宝base64jiemi", aliOrderInfo);
                                                zhifubaoPay(aliOrderInfo);
                                                controller.saveOrder(VipProductPayActivity.this, user, PayHttpsOperate.NEW_ALI_KEY, outNo, "", PayHttpsOperate.PAYING);
                                            }
                                        } else {
                                            showToast(R.string.order_make_faild);
                                        }
                                    }
                                });
                                normalDialog.show();
                            } else {
                                showToast(jsonObject.optString("error_desc"));
                            }
                        } catch (JSONException e) {
                            showToast(R.string.order_make_faild);
                        }
                    } else {
                        showToast(R.string.order_make_faild);
                    }
                }
            });
        } catch (Exception e) {
            showToast(R.string.order_make_faild);
            Log.e("上传订单错误信息", e.getMessage());
        }

    }

    /**
     * 调起支付
     *
     * @param type
     */
    public void dopay(final int type) {
        showLoadingDialog(true);
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                doUpload(type);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 微信支付：预支付订单号
     *
     * @param prepareId
     */
    private void weixinPay(String prepareId) {
        genPayReq(prepareId);
        // 发起第一步
        msgApi.registerApp(SlateApplication.mConfig.getWeixin_app_id());
        msgApi.sendReq(weixinReq);
    }

    /**
     * 微信支付防重复随机数
     *
     * @return
     */
    private String genNonceStr() {
        Random random = new Random();
        return String.valueOf(random.nextInt(10000));
        // MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
        // .getBytes());
    }

    /**
     * 支付宝支付：订单信息
     */
    public void zhifubaoPay(String orderInfo) {
        // 构造PayTask 对象
        PayTask alipay = new PayTask(VipProductPayActivity.this);
        // 调用支付接口，获取支付结果
        String result = alipay.pay(orderInfo);

        Message msg = new Message();
        msg.what = SDK_PAY_FLAG;
        msg.obj = result;
        mHandler.sendMessage(msg);

    }

    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     */
    public void check() {
        Runnable checkRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(VipProductPayActivity.this);
                // 调用查询接口，获取查询结果
                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();
    }

    private void genPayReq(String prepareId) {
        if (TextUtils.isEmpty(prepareId)) {
            showToast(cn.com.modernmedia.R.string.order_make_faild);
            return;
        }
        weixinReq.appId = SlateApplication.mConfig.getWeixin_app_id();
        weixinReq.partnerId = SlateApplication.mConfig.getWeixin_partner_id();
        weixinReq.prepayId = prepareId;
        weixinReq.packageValue = "Sign=WXPay";
        weixinReq.nonceStr = genNonceStr();
        weixinReq.timeStamp = String.valueOf(genTimeStamp());

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", weixinReq.appId));
        signParams.add(new BasicNameValuePair("noncestr", weixinReq.nonceStr));
        signParams.add(new BasicNameValuePair("package", weixinReq.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", weixinReq.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", weixinReq.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", weixinReq.timeStamp));

        weixinReq.sign = genAppSign(signParams);


    }

    /**
     * 微信app签名生成
     *
     * @param params
     * @return
     */
    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(APP_KEY);

        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return appSign;
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    public static String getTime(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat(date);
        long time = System.currentTimeMillis();
        Date curDate = new Date(time);//获取当前时间
        return formatter.format(curDate);
    }

    public static void payintent(Context c, String style, String resultStatus) {
        Intent intent = new Intent(c, VipPayResultActivity.class);
        if (style.equals("微信")) {
            intent.putExtra("pay_end_from_weixinpay", resultStatus);
        }
        Bundle bundle = new Bundle();
        bundle.putString("resultStatus", resultStatus);
        bundle.putSerializable("product", product);
        bundle.putString("oid", outNo);
        bundle.putString("style", style);
        bundle.putString("time", getTime(c.getResources().getString(R.string.date_format_time)));
        bundle.putBoolean("postCard", needCheckAdress);
        if (!TextUtils.isEmpty(user.getAddress()))
            bundle.putString("address", user.getCity() + user.getAddress());
        if (!TextUtils.isEmpty(user.getRealname())) bundle.putString("name", user.getRealname());
        if (!TextUtils.isEmpty(user.getPhone())) bundle.putString("phone", user.getPhone());
        intent.putExtras(bundle);
        c.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQ_CODE == requestCode && resultCode == RESULT_OK) {
            if (data.getExtras() != null) {
                Bundle bundle = data.getExtras();
                user = (User) bundle.getSerializable("error");
                vip_address_detail.setVisibility(View.VISIBLE);
                vip_post_detail.setText(user.getRealname() + " " + user.getPhone());
                vip_address_detail.setText(user.getCity() + user.getAddress());
                try {//订单地址
                    ajsonObject.put("name", user.getRealname());
                    ajsonObject.put("tel", user.getPhone());
                    ajsonObject.put("address_full", user.getCity() + user.getAddress());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else if (requestCode == 12345 && resultCode == 1234){//未登录，关闭登录页面返回，应当关闭支付页面
            finish();
        }
    }

    @Override
    public String getActivityName() {
        return VipProductPayActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return VipProductPayActivity.this;
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initView() {
        weixin_relative = (RelativeLayout) findViewById(R.id.vip_pay_weixin_relative);
        zhifubao_relative = (RelativeLayout) findViewById(R.id.vip_pay_zhifubao_relative);
        weixin_pay = (RadioButton) findViewById(R.id.vip_pay_weixin);
        zhifubao_pay = (RadioButton) findViewById(R.id.vip_pay_zhifubao);
        pay_money = (TextView) findViewById(R.id.vip_pay_money);
        pay_time = (TextView) findViewById(R.id.vip_pay_time);
        pay_name = (TextView) findViewById(R.id.vip_pay_name);
        weixin_text = (TextView) findViewById(R.id.vip_pay_weiixn_text);
        zhifubao_text = (TextView) findViewById(R.id.vip_pay_zhifubao_text);
        weixin_recommend = (ImageView) findViewById(R.id.vip_pay_weixin_recommend);
        zhifubao_recommend = (ImageView) findViewById(R.id.vip_pay_zhifubao_recommend);
        vip_phone_info = (LinearLayout) findViewById(R.id.vip_phone_info);

        nameEdit = (EditText) findViewById(R.id.vip_name);
        phoneEdit = (EditText) findViewById(R.id.vip_phone);

        weixin_pay.setChecked(true);//默认微信支付
        pay_style = WEIXIN_PAY;
        zhifubao_pay.setClickable(false);
        findViewById(R.id.vip_pay_back).setOnClickListener(this);
        findViewById(R.id.vip_pay_btn).setOnClickListener(this);
        weixin_relative.setOnClickListener(this);
        zhifubao_relative.setOnClickListener(this);

        if (!TextUtils.isEmpty(needTel)) {// 网页跳转需要填写电话号码的
            findViewById(R.id.vip_html_phone_relative).setVisibility(View.VISIBLE);

        } else {
            findViewById(R.id.vip_html_phone_relative).setVisibility(View.GONE);
        }


        ((EvaSwitchBar) findViewById(R.id.vip_phone_switch)).setOnChangeListener(this);
    }

    private void initPost() {
        vip_post_detail = (TextView) findViewById(R.id.vip_pay_detail);
        vip_address_detail = (TextView) findViewById(R.id.vip_address_detail);
        postcard_relative = (RelativeLayout) findViewById(R.id.vip_postcard_relative);
        address_relative = (RelativeLayout) findViewById(R.id.vip_open_postcard);
        address_relative.setOnClickListener(this);
        cardSwicth = (EvaSwitchBar) findViewById(R.id.vip_card_switch);

        cardSwicth.setOnChangeListener(this);
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
                        JSONObject object = array.optJSONObject(0);
                        SlateDataHelper.setAddressId(VipProductPayActivity.this, object.optInt("id") + "");
                        user.setRealname(object.optString("name"));
                        user.setProvince(object.optString("province"));
                        user.setPhone(object.optString("phone"));
                        user.setCity(object.optString("city"));
                        user.setAddress(object.optString("address"));
                        //订单地址
                        ajsonObject.put("name", user.getRealname());
                        ajsonObject.put("tel", user.getPhone());
                        ajsonObject.put("address_full", user.getCity() + user.getAddress());
                        if (200 == jsonObj.optInt("no")) {
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

    @Override
    public void onChange(EvaSwitchBar sb, boolean state) {
        if (sb.getId() == R.id.vip_card_switch) {
            if (user == null) {
                return;

            }
            // 邮寄地址不必要的情况
            if (user.getSend().equals("0") || TextUtils.isEmpty(user.getSend())) {//未邮寄
                if (state) {// 邮寄
                    address_relative.setVisibility(View.VISIBLE);
                } else {// 不邮寄
                    address_relative.setVisibility(View.GONE);
                }
            }
        } else if (sb.getId() == R.id.vip_phone_switch) {
            if (state) {
                vip_phone_info.setVisibility(View.VISIBLE);
            } else {
                vip_phone_info.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showLoadingDialog(false);
        mHandler.removeCallbacksAndMessages(null);
    }
}
