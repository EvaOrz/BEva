package cn.com.modernmedia.businessweek.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.com.modernmedia.R;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.SlateDataHelper;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信支付回调activity
 * 
 * @author lusiyuan
 * 
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		api = WXAPIFactory.createWXAPI(this,
				SlateApplication.mConfig.getWeixin_app_id());
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
	public void onResp(BaseResp resp) {
		// resp.errCode == 0为支付成功，为-2未取消支付
		if (resp != null && resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			Log.e("微信支付code:", resp.errCode + "" + resp.errStr);
			if (resp.errCode == 0) {
				// 存储支付成功后用户的level
				SlateDataHelper.setIssueLevel(WXPayEntryActivity.this, "1");
				notifyServer();
				/**
				 * 显示支付成功dialog
				 * 
				 */
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.pay_success)
						.setCancelable(false)
						.setPositiveButton(R.string.sure,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										WXPayEntryActivity.this.finish();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			} else {// 未支付成功，置空订单
				SlateDataHelper.setOrder(WXPayEntryActivity.this, "weixin",
						null);
				WXPayEntryActivity.this.finish();
			}

		}
	}

	private void notifyServer() {
		if (SlateDataHelper.getOrder(this, "weixin") != null) {
			String jsonString = SlateDataHelper.getOrder(this, "weixin");
			OperateController.getInstance(this).notifyServerPayResult(this,
					"weixin", jsonString, new FetchEntryListener() {

						@Override
						public void setData(Entry entry) {

						}
					});
		}
	}
}