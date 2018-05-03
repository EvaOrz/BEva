package cn.com.modernmedia.businessweek.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import cn.com.modernmedia.pay.newlogic.PayHttpsOperate;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.vip.VipProductPayActivity;

/**
 * 微信支付回调activity
 *
 * @author lusiyuan
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, SlateApplication.mConfig.getWeixin_app_id());
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(final BaseResp resp) {
        if (resp != null && resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            Log.e("微信支付code:", resp.errCode + "" + resp.errStr);
            // resp.errCode == 0为支付成功，为-2未取消支付，-1出错
            if (resp.errCode == 0) {
                PayHttpsOperate.getInstance(this).notifyServer(PayHttpsOperate.SUCCESS, PayHttpsOperate.NEW_WEIXIN_KEY, new FetchDataListener() {
                    @Override
                    public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                        if (isSuccess) {
                            Log.e("更新订单状态成功", "清除数据" + data);
                            LogHelper.paySuccess(WXPayEntryActivity.this);
                            SlateDataHelper.clearOrder(WXPayEntryActivity.this, PayHttpsOperate.NEW_ALI_KEY);
                            PayHttpsOperate.getInstance(WXPayEntryActivity.this).saveLevel(data);
                            // 存储支付成功后用户的level
                            //                            SlateDataHelper.setIssueLevel(WXPayEntryActivity.this, "1");
                            //跳转交易详情界面
                            goToPayResult(resp.errCode);

                        }
                    }
                });
            } else if (resp.errCode == -2) {// 取消支付
                PayHttpsOperate.getInstance(this).notifyServer(PayHttpsOperate.CANCEL, PayHttpsOperate.NEW_WEIXIN_KEY);
                goToPayResult(resp.errCode);
            } else {
                PayHttpsOperate.getInstance(this).notifyServer(PayHttpsOperate.ERROR, PayHttpsOperate.NEW_WEIXIN_KEY);
                goToPayResult(resp.errCode);
            }

        }
    }

    private void goToPayResult(int code) {
        //        Intent intent = new Intent(WXPayEntryActivity.this, VipPayResultActivity.class);
        //        intent.putExtra("pay_end_from_weixinpay", code);
        VipProductPayActivity.payintent(this, "微信", code + "");
        WXPayEntryActivity.this.finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


}