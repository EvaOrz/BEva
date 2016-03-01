package cn.com.modernmedia.pay;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.R;
import cn.com.modernmedia.adapter.ProductAdapter;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ProductList;
import cn.com.modernmedia.model.ProductList.Product;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DESCoder;
import cn.com.modernmedia.util.weixin.MD5;
import cn.com.modernmedia.util.weixin.Util;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.SlateDataHelper;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 支付入口页面（商周用）
 * 
 * @author lusiyuan
 * 
 */
public class PayActivity extends BaseActivity implements OnClickListener {
	private ListView products;
	private ProductAdapter adapter;// products adapter
	private List<Product> pros = new ArrayList<Product>();

	// 商户PID
	public static final String PARTNER = "2088021523775321";
	// 商户收款账号
	public static final String SELLER = "will_wang@modernmedia.com.cn";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAM0bPeo06zpONhc6ompcBi3h1Re5YmkyvWclpPWIFgyNKtn/WQ5qxYc7h9RuySQkKm0i9fXWDcQuhnThGr96L3k36IObS1XaClz9WAfTAVLi9Xw4vRMqS6JCp9UQzIwd2CLKExI0BSmVxKZWoq8yvX6U58SwdAtgkXESO2bhGazdAgMBAAECgYBExG502PtJGDHwhdswl9wGhCIjCyfgp39zVt7A57ikyqvkXUWpnMjPd3kqE17i/DExWDhpDTSeYw73nwWNz1Sc0FZPgLCCknhNKm6zSrbf8VaKbXoROe3iTu1ucpvH39bpEFYZ2yFPrH4dWIFRGozp1M1AbXPoOJxJtRRR7CY6LQJBAP3/Bt7ZfzNdUjzEeJ8066Z4N/ZASUVeTY7JibJYZh3t/Ydb+nr/YBUIdkwUm5UVDVB61yRYwhQxoxmg2QdbYxMCQQDOuXoLeJHLv/SMl7vI2uaTVuFkWaReFL4G2+nOTCJqanSWS44IU6OeAQiG0C/hxH4n/zYBloToo33AhGID6r5PAkAwTkBQQa0fZ7AsPnFyVe47SsHZ44AL4VN+xHWbpZRGPOzqwWNx4P+1AFb/QSwVvls54yLnlrnSfV43kY+1BnxhAkBpbg7gas1wrKV8TqZm+b0+x8CL/Wvmz41a0i2cGRg0TbbIMCBv/rgjjUNb/jFtY1kz7OUOSkXeoAMyfFHAzafhAkAX/MqmSOunB5faVXia+22er7CDycWJzKqjX14NW1iP4H3J6uXaXAjde8NfZ/J/08oIcfxbibo9xrNUY4eDPSyZ";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDNGz3qNOs6TjYXOqJqXAYt4dUXuWJpMr1nJaT1iBYMjSrZ/1kOasWHO4fUbskkJCptIvX11g3ELoZ04Rq/ei95N+iDm0tV2gpc/VgH0wFS4vV8OL0TKkuiQqfVEMyMHdgiyhMSNAUplcSmVqKvMr1+lOfEsHQLYJFxEjtm4Rms3QIDAQAB";

	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;

	private String fee;// 支付价格

	// 微信参数
	public static final String APP_KEY = "b2eujfhVFiCRQhbnmrYcdkGPWvv3mZen";// PaySignKey（API密钥）
	private PayReq weixinReq;
	private final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_detail_view);
		initView();
		initProducts();
	}

	/**
	 * 获取商品列表
	 */
	private void initProducts() {
		OperateController.getInstance(this).getProducts(
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						ProductList list = (ProductList) entry;
						if (list != null && list.getPros() != null) {
							pros.clear();
							pros.addAll(list.getPros());
							mHandler.sendEmptyMessage(0);
						}
					}
				});
	}

	private void initView() {
		findViewById(R.id.pay_close).setOnClickListener(this);// 关闭
		products = (ListView) findViewById(R.id.products_listview);
		adapter = new ProductAdapter(this, pros);
		products.setAdapter(adapter);

		products.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Product pro = pros.get(position);
				fee = pro.getPrice();
				isLogin(pro.getName(), pro.getPid());
			}
		});
		weixinReq = new PayReq();// 微信req请求
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG:
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();
				Log.e("*******支付宝*********resultInfo", resultInfo);

				String resultStatus = payResult.getResultStatus();
				Log.e("*******支付宝*********resultStatus", resultStatus);

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					notifyServerAli(resultInfo);
					// 存储支付成功后用户的level
					SlateDataHelper.setIssueLevel(PayActivity.this, "1");
					showPaySuccessDialog();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(PayActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(PayActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();
					}
				}
				break;
			case SDK_CHECK_FLAG:
				Toast.makeText(PayActivity.this, "检查结果为：" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			case 0:
				adapter.notifyDataSetChanged();
				break;

			}
		};
	};

	/**
	 * 显示支付成功dialog
	 * 
	 */
	private void showPaySuccessDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.pay_success)
				.setCancelable(false)
				.setPositiveButton(R.string.sure,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								PayActivity.this.finish();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * * 微信支付三步： 1. 生成预支付订单 2. 生成微信支付参数 3. 调用支付接口
	 */
	private class GetPrepayIdTask extends
			AsyncTask<String, Void, Map<String, String>> {
		String out_trade_no = "";

		// private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			// dialog = ProgressDialog.show(PayActivity.this,
			// getString(R.string.app_tip),
			// getString(R.string.getting_prepayid));
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			genPayReq(result);// 第二步
			// 第三步
			msgApi.registerApp(SlateApplication.mConfig.getWeixin_app_id());
			msgApi.sendReq(weixinReq);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Map<String, String> doInBackground(String... params) {
			String url = String
					.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			out_trade_no = SlateDataHelper.getUid(PayActivity.this)
					+ System.currentTimeMillis() + genNonceStr();
			String entity = genProductArgs(params[0], params[1], out_trade_no);

			// byte[] buf
			String content = Util.httpPost(url, entity);

			// String content = new String(buf);
			Map<String, String> xml = decodeXml(content);
			Log.e("pay_第一步", content + "");
			notifyServer(params[1], out_trade_no);
			return xml;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (SlateDataHelper.getIssueLevel(this).equals("1"))
			finish();
	}

	/**
	 * 存储支付订单，用于WXPayEntryActivity取出notify server
	 */
	private void notifyServer(String product_id, String out_trade_no) {
		JSONObject object = new JSONObject();
		try {
			object.put("openid", "");
			object.put("out_trade_no", out_trade_no);
			object.put("product_id", product_id);
			object.put("clientversion", getClientVersion());
			object.put("app_id", ConstData.getAppId());
			object.put("umeng_channel", CommonApplication.CHANNEL);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		SlateDataHelper.setOrder(this, "weixin", getDESdata(object.toString()));
	}

	public void weixinPay(String body, String pid) {
		// 发起第一步
		GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
		getPrepayId.execute(body, pid);
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * partner="2088101568358171"&seller_id="xxx@alipay.com"
	 * &out_trade_no="0819145 412-6177"
	 * &subject="测试"&body="测试测试 "&total_fee="0.01"
	 * &notify_url="http://notify.msp.hk/notify.htm"
	 * &service="mobile.securi typay.pay"
	 * &payment_type="1"&_input_charset="utf-8"&it_b_pay="30m"&sign=
	 * "lBBK %2F0w5LOajrMrji7DUgEqNjIhQbidR13GovA5r3TgIbNqv231yC1NksLdw%2Ba3JnfH XoXuet6XNNHtn7VE%2BeCoRO1O%2BR1KugLrQEZMtG5jmJIe2pbjm%2F3kb%2F uGkpG%2BwYQYI51%2BhA3YBbvZHVQBYveBqK%2Bh8mUyb7GM1HxWs9k4%3D "
	 * &sign_type="RSA"
	 * 
	 */
	public void zhifubaoPay(String subject, String productId) {
		// 订单
		String orderInfo = getOrderInfo(subject, productId, fee);

		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(PayActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String subject, String body, String price) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
				+ "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check() {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(PayActivity.this);
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

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

	@Override
	public void reLoadData() {

	}

	@Override
	public String getActivityName() {
		return PayActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return PayActivity.this;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.pay_close) {
			finish();
		}
	}

	private boolean isLogin(String subject, String detail) {
		if (SlateDataHelper.getUserLoginInfo(this) != null) {
			new PayDialog(PayActivity.this, subject, detail);
			return true;
		} else {
			String broadcastIntent = "cn.com.modernmediausermodel.LoginActivity";// 自己自定义
			Intent intent = new Intent(broadcastIntent);
			this.sendBroadcast(intent);
		}
		return false;
	}

	/**
	 * 
	 * partner="2088021523775321"&seller_id="will_wang@modernmedia.com.cn"&
	 * out_trade_no
	 * ="102118395754743"&subject="测试的商品"&body="该测试商品的详细描述"&total_fee
	 * ="0.01"&notify_url
	 * ="http://notify.msp.hk/notify.htm"&service="mobile.securitypay.pay"
	 * &payment_type
	 * ="1"&_input_charset="utf-8"&it_b_pay="30m"&return_url="m.alipay.com"
	 * &success="true"&sign_type="RSA"&sign=
	 * "SeboZ5bFmWR/wdNhIn9Vd+ekwYZ20+I6Tu1yxtZZFqj806CJigV8doIGY5udj2z69Ca6h51cX5rd3yjW7+ArW1KCu4R5aUuuPsa11CxRxQZ5CNV0o1lBFu8hjwCyEWdZa7uhEHZjVGX4+beNVj7cUxef7t7v8QAlh/U+LlT+Yso="
	 * 
	 * ------------------------------------------------------------------------
	 * --- 支付成功通知server String discount, String payment_type, String subject,
	 * String trade_no, String buyer_email, String gmt_create, String
	 * notify_type, String quantity, String out_trade_no, String seller_id,
	 * String notify_time, String body, String trade_status, String
	 * is_total_fee_adjust, String total_fee, String gmt_payment, String
	 * seller_email, String price, String buyer_id, String notify_id, String
	 * use_coupon, String sign_type, String sign, String pid, String appid,
	 * String clientversion, String resultStatus, String usertoken
	 */
	private void notifyServerAli(String resultInfo) {
		JSONObject json = new JSONObject();
		try {
			String pamas[] = resultInfo.split("&");
			JSONObject pa = new JSONObject();
			for (int i = 0; i < pamas.length; i++) {
				String j = pamas[i];
				pa.put(j.substring(0, j.indexOf("=")),
						j.substring(j.indexOf("=\"") + 2, j.length() - 1));
			}
			json.put("discount", 1);
			json.put("payment_type", pa.getString("payment_type"));
			json.put("subject", pa.getString("subject"));//
			json.put("trade_no", pa.getString("out_trade_no"));
			json.put("buyer_email", null);
			json.put("gmt_create", null);
			json.put("notify_type", null);
			json.put("quantity", "1");
			json.put("out_trade_no", pa.getString("out_trade_no"));
			json.put("seller_id", SELLER);
			json.put("notify_time", SystemClock.currentThreadTimeMillis());
			json.put("body", pa.getString("body"));

			json.put("trade_status", null);
			json.put("is_total_fee_adjust", null);
			json.put("total_fee", pa.getString("total_fee"));
			json.put("gmt_payment", null);

			json.put("seller_email", pa.getString("seller_id"));
			json.put("price", null);
			json.put("product_id", pa.getString("body"));
			json.put("buyer_id", null);
			json.put("notify_id", null);
			json.put("use_coupon", null);
			json.put("sign_type", pa.getString("sign_type"));
			json.put("sign", pa.getString("sign"));//
			json.put("pid", PARTNER);// 商户id
			json.put("appid", ConstData.getAppId());
			json.put("clientversion", getClientVersion());
			json.put("resultStatus", "9000");
			json.put("usertoken", SlateDataHelper.getToken(this));
			json.put("umeng_channel", CommonApplication.CHANNEL);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		OperateController.getInstance(this).notifyServerPayResult(this,
				"alipay", getDESdata(json.toString()),
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
					}
				});
	}

	/**
	 * 返回DES加密之后的data参数
	 * 
	 * @param jsonString
	 * @return
	 */
	private String getDESdata(String jsonString) {
		String data = "";
		Log.e("*****DES算法之前的jsonString*****", jsonString.toString());
		String token = SlateDataHelper.getToken(this);
		try {
			data = DESCoder.encode(
					token.substring(token.length() - 8, token.length()),
					jsonString.toString());
			Log.e("****DES算法之后的jsonString*****", data);
			// data = Base641.encode(data.getBytes());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 微信支付生成订单
	 * 
	 * @return
	 */
	private String genProductArgs(String body, String pid, String out_trade_no) {

		try {
			String nonceStr = genNonceStr();

			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid",
					SlateApplication.mConfig.getWeixin_app_id()));
			// 可选参数，用来传递token ,附加数据，原样返回给服务器
			// URLEncoder.encode(SlateDataHelper.getToken(this), "UTF-8")
			// attach
			// :{"product_id":"bbwcsub_oneyearsub","clientversion":"","app_id":"1","uid":"","usertoken":""}
			JSONObject attach = new JSONObject();
			attach.put("product_id", pid);
			attach.put("app_id", ConstData.getAppId());
			attach.put("uid", SlateDataHelper.getUid(this));

			packageParams.add(new BasicNameValuePair("attach", Base64
					.encode(attach.toString().getBytes())));
			packageParams.add(new BasicNameValuePair("body", body));// 商品描述
			packageParams.add(new BasicNameValuePair("mch_id",
					SlateApplication.mConfig.getWeixin_partner_id()));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			// 微信回调接口地址
			packageParams.add(new BasicNameValuePair("notify_url",
					"http://www.bbwc.cn/test.php"));
			packageParams.add(new BasicNameValuePair("out_trade_no",
					out_trade_no));
			packageParams.add(new BasicNameValuePair("spbill_create_ip",
					"127.0.0.1"));
			packageParams.add(new BasicNameValuePair("total_fee", (int) (Float
					.valueOf(fee) * 100) + ""));
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));

			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));

			String xmlstring = toXml(packageParams);

			return new String(xmlstring.toString().getBytes(), "ISO8859-1");

		} catch (Exception e) {
			Log.e("WEIXIN_PAY", "genProductArgs fail, ex = " + e.getMessage());
			return null;
		}

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
	 * 微信支付生成签名
	 */
	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(APP_KEY);// API密钥，在商户平台设置

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase();
		Log.e("第一步：微信生成支付签名", packageSign);
		return packageSign;
	}

	/**
	 * 微信支付生成xml表单
	 * 
	 * @param params
	 * @return
	 */
	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");

			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");

		Log.e("第一步： 生成xml表单", sb.toString());
		return sb.toString();
	}

	public Map<String, String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:

					if ("xml".equals(nodeName) == false) {
						// 实例化student对象
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("orion", e.toString());
		}
		return null;

	}

	private void genPayReq(Map<String, String> resultunifiedorder) {
		if (resultunifiedorder == null) {
			return;
		}
		weixinReq.appId = SlateApplication.mConfig.getWeixin_app_id();
		weixinReq.partnerId = SlateApplication.mConfig.getWeixin_partner_id();
		weixinReq.prepayId = resultunifiedorder.get("prepay_id");
		weixinReq.packageValue = "Sign=WXPay";
		weixinReq.nonceStr = genNonceStr();
		weixinReq.timeStamp = String.valueOf(genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", weixinReq.appId));
		signParams.add(new BasicNameValuePair("noncestr", weixinReq.nonceStr));
		signParams
				.add(new BasicNameValuePair("package", weixinReq.packageValue));
		signParams
				.add(new BasicNameValuePair("partnerid", weixinReq.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", weixinReq.prepayId));
		signParams
				.add(new BasicNameValuePair("timestamp", weixinReq.timeStamp));

		weixinReq.sign = genAppSign(signParams);

	}

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

		String appSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase();
		return appSign;
	}

	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 获取客户端版本号
	 * 
	 * @return
	 */
	private String getClientVersion() {
		String version = "";
		// 获取version
		PackageManager manager = this.getPackageManager();
		PackageInfo info;
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		return version;
	}

}
