package cn.com.modernmedia.pay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.vending.billing.IInAppBillingService;
import com.example.android.trivialdrivesample.util.IabBroadcastReceiver;
import com.example.android.trivialdrivesample.util.IabBroadcastReceiver.IabBroadcastListener;
import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;
import com.example.android.trivialdrivesample.util.Inventory;
import com.example.android.trivialdrivesample.util.Purchase;
import com.example.android.trivialdrivesample.util.SkuDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.R;
import cn.com.modernmediaslate.unit.SlateDataHelper;

/**
 * google支付：号外、商周繁体版
 *
 * @author lusiyuan
 */
public class GooglePayActivity extends BaseActivity implements IabBroadcastListener, OnClickListener {
    // test: three products
    private Button pro1, pro2, pro3;

    // The helper object
    private IabHelper mHelper;
    // Debug tag, for logging
    static final String TAG = "TrivialDrive";
    // Current amount of gas in tank, in units
    int mTank;
    // (arbitrary) request code for the purchase flow请求码
    static final int RC_REQUEST = 10001;
    private boolean iap_is_ok = false;
    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;

    // Tracks the currently owned infinite gas SKU, and the options in the
    // Manage dialog
    private String mInfiniteGasSku = "";

    // Will the subscription auto-renew?
    boolean mAutoRenewEnabled = false;
    String base64EncodedPublicKey;

    // 商品列表
    private String[] skus = {"verycity1year", "verycity6month", "verycity1month"};

    private ArrayList<String> sku_list;
    private ArrayList<String> price_list;// 价格列表
    private ArrayList<String> price_texts;// 价格中文名

    IInAppBillingService mService;

    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_pay);
        // google developer console app_key
        base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvMBmdgVdqPCLVJyxAJiY1L8kU0jChecc+59oZE48JgDo/jeNNfwn5R2qB/GL+zfXKV6/3JPdHQv+F009peRKre6Niq1VgFQ2j6vXTlfy0rocc7tRT2MBcG7W4ZxRMA4+7dXWsClJpBmJ7P6v+aoxm9oXTvhjua13RRo8fRmBNOygXp1IA2IXlrF3erHsJABiwAlK/QncwQIJDB5eOh5VGxXpwrXowrNFMuK+zWNmtX+XyiXrsG3idw+9zLwz/6Ea1WaGRYsi82D0hIcIBd4CnEOt/rBHU1tp1SCMdy+/WpVP2QLdQxZGsr5VZlAKjFdT6dWNEs3veOApLWicbq1DIwIDAQAB";// 此处请填写你应用的appkey
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        // enable debug logging (for a production application, you should set
        // this to false).
        mHelper.enableDebugLogging(false);
        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about
                // updated purchases.
                // We register the receiver here instead of as a <receiver> in
                // the Manifest
                // because we always call getPurchases() at startup, so
                // therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea,
                // but is done here
                // because this is a SAMPLE. Regardless, the receiver must be
                // registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(GooglePayActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we
                // own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
        // init view
        initView();

    }

    private void initView() {
        findViewById(R.id.google_pay_close).setOnClickListener(this);
        // findViewById(R.id.button1).setOnClickListener(this);
        pro1 = (Button) findViewById(R.id.product1);
        pro2 = (Button) findViewById(R.id.product2);
        pro3 = (Button) findViewById(R.id.product3);
        pro1.setOnClickListener(this);
        pro2.setOnClickListener(this);
        pro3.setOnClickListener(this);
        if (isBillingAvailable(this)) {
            handler.sendEmptyMessage(0);
        } else showMessage();
    }

    /**
     * 查询是否可以购买
     *
     * @param context
     * @return
     */
    public boolean isBillingAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        List<ResolveInfo> list = packageManager.queryIntentServices(serviceIntent, 0);
        if (list == null || list.size() == 0) return false;
        else return true;
    }

    // 获取价格
    private void getPrice() {
        final Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", sku_list);
        try {
            Bundle skuDetails = mService.getSkuDetails(3, GooglePayActivity.this.getPackageName(), "subs", querySkus);
            int response = skuDetails.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                if (null != responseList) {
                    for (String thisResponse : responseList) {
                        SkuDetails d = new SkuDetails(thisResponse);
                        for (int i = 0; i < sku_list.size(); i++) {
                            if (sku_list.get(i).equals(d.getSku())) {
                                price_list.add(d.getPrice());
                                price_texts.add(d.getTitle());
                            }
                        }
                        handler.sendEmptyMessage(1);
                    }

                }
            } else {
                Log.e(TAG, "获取产品价格失败");
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            if (result.isFailure()) {
                // Oh noes!
                complain("Error purchasing: " + result);
                return;
            }

            Log.d(TAG, "Purchase successful.");
            // if (purchase.getSku().equals(skus[0])) {
            // showMessage("支付成功", "商品" + skus[0] + "购买成功");
            // } else if (purchase.getSku().equals(skus[1])) {
            // showMessage("支付成功", "商品" + skus[1] + "购买成功");
            // } else if (purchase.getSku().equals(skus[2])) {
            // showMessage("支付成功", "商品" + skus[2] + "购买成功");
            // }
        }
    };

    // Listener that's called when we finish querying the items we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            // First find out which subscription is auto renewing
            Purchase month_1 = inventory.getPurchase(skus[0]);
            Purchase month_6 = inventory.getPurchase(skus[1]);
            Purchase year_1 = inventory.getPurchase(skus[2]);
            if (month_1 != null && month_1.isAutoRenewing()) {
                mInfiniteGasSku = skus[0];
                mAutoRenewEnabled = true;
            } else if (month_6 != null && month_6.isAutoRenewing()) {
                mInfiniteGasSku = skus[1];
                mAutoRenewEnabled = true;
            } else if (year_1 != null && year_1.isAutoRenewing()) {
                mInfiniteGasSku = skus[2];
                mAutoRenewEnabled = true;
            } else {
                mInfiniteGasSku = "";
                mAutoRenewEnabled = false;
            }
            // The user is subscribed if either subscription exists, even if
            // neither is auto
            // renewing
            // if (month_1 != null && verifyDeveloperPayload(month_1)) {
            // showMessage(TAG, "已购买" + skus[0]);
            // } else if (month_6 != null && verifyDeveloperPayload(month_6)) {
            // showMessage(TAG, "已购买" + skus[1]);
            // } else if (year_1 != null && verifyDeveloperPayload(year_1)) {
            // showMessage(TAG, "已购买" + skus[2]);
            // }
        }
    };

    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

		/*
         * TODO: verify that the developer payload of the purchase is correct.
		 * It will be the same one that you sent when initiating the purchase.
		 * 
		 * WARNING: Locally generating a random string when starting a purchase
		 * and verifying it here might seem like a good approach, but this will
		 * fail in the case where the user purchases an item on one device and
		 * then uses your app on a different device, because on the other device
		 * you will not have access to the random string you originally
		 * generated.
		 * 
		 * So a good developer payload has these characteristics:
		 * 
		 * 1. If two different users purchase an item, the payload is different
		 * between them, so that one user's purchase can't be replayed to
		 * another user.
		 * 
		 * 2. The payload must be such that you can verify it even when the app
		 * wasn't the one who initiated the purchase flow (so that items
		 * purchased by the user on one device work on other devices owned by
		 * the user).
		 * 
		 * Using your own server to store and verify developer payloads across
		 * app installations is recommended.
		 */

        return true;
    }

    // @Override
    // protected void onActivityResult(int requestCode, int resultCode, Intent
    // data) {
    // // TODO Auto-generated method stub
    // Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + ","
    // + data);
    //
    // // Pass on the activity result to the helper for handling
    // if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
    // // not handled, so handle it ourselves (here's where you'd
    // // perform any handling of activity results not related to in-app
    // // billing...
    // super.onActivityResult(requestCode, resultCode, data);
    // } else {
    // Log.d(TAG, "onActivityResult handled by IABUtil.");
    // }
    // }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // very important:
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
        if (mService != null) {
            unbindService(mServiceConn);
        }

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    private void showMessage() {
        // 不支持google支付返回
        new AlertDialog.Builder(GooglePayActivity.this).setTitle(R.string.no_google_title).setMessage(R.string.no_google_content).setPositiveButton(R.string.sure, new AlertDialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }

        }).setCancelable(false).show();
    }

    @Override
    public void reLoadData() {

    }

    @Override
    public String getActivityName() {
        return GooglePayActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has
        // changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        mHelper.queryInventoryAsync(mGotInventoryListener);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.google_pay_close) {
            finish();
        } else if (v.getId() == R.id.product1) {
            buy(skus[0]);
        } else if (v.getId() == R.id.product2) {
            buy(skus[1]);
        } else if (v.getId() == R.id.product3) {
            buy(skus[2]);
        }
    }

    private void buy(String product) {
        if (SlateDataHelper.getUserLoginInfo(this) != null) {

			/*
             * TODO: for security, generate your payload here for verification.
			 * See the comments on verifyDeveloperPayload() for more info. Since
			 * this is a SAMPLE, we just use an empty string, but on a
			 * production app you should carefully generate this.
			 */
            // String payload = "";
            //
            // mHelper.launchPurchaseFlow(this, product, RC_REQUEST,
            // mPurchaseFinishedListener, payload);
            try {
                Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), product, "subs", "miamia");
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
            } catch (SendIntentException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            String broadcastIntent = "cn.com.modernmediausermodel.LoginActivity";// 自己自定义
            Intent intent = new Intent(broadcastIntent);
            this.sendBroadcast(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001 && data != null) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    showToast("You have bought the " + sku + ". Excellent choice,adventurer!");
                    SlateDataHelper.setGoogleLevel(this, 1);
                } catch (JSONException e) {
                    showToast("Failed to parse purchase data.");
                    e.printStackTrace();
                }
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    sku_list = new ArrayList<String>();
                    price_list = new ArrayList<String>();
                    price_texts = new ArrayList<String>();
                    // 添加默认值
                    sku_list.add(skus[0]);
                    sku_list.add(skus[1]);
                    sku_list.add(skus[2]);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            if (mService != null) {
                                getPrice();
                            } else {
                                handler.sendEmptyMessage(2);
                                Log.e(TAG, "获取产品价格失败");
                            }
                        }
                    }).start();
                    break;

                case 1:
                    // 显示商品
                    if (price_list.size() == 3) {
                        pro1.setText(price_texts.get(0) + " / " + price_list.get(0));
                        pro2.setText(price_texts.get(1) + " / " + price_list.get(1));
                        pro3.setText(price_texts.get(2) + " / " + price_list.get(2));
                    }
                    break;
                case 2:
                    showMessage();
                    break;
            }

        }
    };

}
