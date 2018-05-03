package cn.com.modernmedia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import cn.com.modernmedia.listener.NotifyAdapterListener;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.newtag.mainprocess.DownloadAvdRes;
import cn.com.modernmedia.newtag.mainprocess.MainProcessObservable;
import cn.com.modernmedia.newtag.mainprocess.MainProcessObservable.ObserverItem;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage;
import cn.com.modernmedia.pay.newlogic.PayHttpsOperate;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.PageTransfer;
import cn.com.modernmedia.util.UpdateManager;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.MainHorizontalScrollView;
import cn.com.modernmediaslate.unit.SlateDataHelper;

/**
 * 公共首页
 *
 * @author ZhuQiao
 */
public abstract class CommonMainActivity extends BaseFragmentActivity implements Observer {
    private long lastClickTime = 0;
    protected int pushArticleId = -1;
    protected MainHorizontalScrollView scrollView;
    private List<NotifyAdapterListener> listenerList = new ArrayList<NotifyAdapterListener>();
    public static String liveUrl = "";// 商周直播页url
    public static final int BOOK_ACTIVITY = 204;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonApplication.mainProcessObservable.addObserver(this, getActivityName());
        init();
        checkBbwc();
        checkIssueLevel();
        downLoadAdvRes();
        startMainProcess(getIntent());
    }

    protected void startMainProcess(Intent intent) {
        if (intent != null) {
            TagProcessManage.getInstance(this).onStart(intent, null);
        }
    }

    /**
     * 初始化资源
     */
    protected abstract void init();

    @Override
    public void update(Observable observable, Object data) {
        if (!(data instanceof ObserverItem)) return;
        ObserverItem item = (ObserverItem) data;
        switch (item.flag) {
            case MainProcessObservable.SET_DATA_TO_COLUMN:
                setDataForColumn();
                break;
            case MainProcessObservable.SET_DATA_TO_SHIYE:
                // TODO time collect
                break;
            case MainProcessObservable.SHOW_INDEX_PAGER:
                showIndexPager();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化栏目列表
     */
    protected abstract void setDataForColumn();

    /**
     * 栏目列表跳转订阅页面
     */
    public abstract void gotoSelectColumnActivity();

    /**
     * 显示首页滑屏view
     */
    protected abstract void showIndexPager();

    public abstract MainHorizontalScrollView getScrollView();

    /**
     * indexview 显示loading
     *
     * @param flag 0.不显示，1.显示loading，2.显示error
     */
    public void checkIndexLoading(int flag) {
        if (getIndexView() != null) {
            if (flag == 0) getIndexView().disProcess();
            else if (flag == 1) getIndexView().showLoading();
            else if (flag == 2) getIndexView().showError();
        }
    }

    public abstract BaseView getIndexView();

    /**
     * 设置首页title
     *
     * @param name
     */
    public abstract void setIndexTitle(String name);

    protected void notifyRead() {
        for (NotifyAdapterListener listener : listenerList) {
            listener.notifyReaded();
        }
    }

    ;

    public void setPushArticleId(int pushArticleId) {
        this.pushArticleId = pushArticleId;
    }

    /**
     * 给scrollview设置需要拦截的子scrollview
     *
     * @param view
     */
    public void setScrollView(View view) {
        scrollView.addNeedsScrollView(view);
    }

    /**
     * 跳转直播页
     */
    public void gotoMarquee() {
    }

    /**
     * 当切换栏目时，清除(栏目是viewpager时不考虑，因为scroll本身就不可滑动)
     */
    public void clearScrollView() {
        scrollView.clearNeedsScrollView();
    }

    public void addListener(NotifyAdapterListener listener) {
        listenerList.add(listener);
    }

    /**
     * 通知更新栏目列表选中状态
     *
     * @param tagName
     */
    public void notifyColumnAdapter(String tagName) {
        for (NotifyAdapterListener listener : listenerList) {
            listener.nofitySelectItem(tagName);
        }
    }

    /**
     * 跳转到某个栏目首页
     *
     * @param tagName 栏目名称
     * @param isUri   是否来自uri;如果是，当找不到的时候需要添加这个栏目，否则，显示第一个栏目
     */
    public void clickItemIfPager(String tagName, boolean isUri) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            long clickTime = System.currentTimeMillis() / 1000;
            if (clickTime - lastClickTime >= 3) {
                lastClickTime = clickTime;
                showToast(R.string.exit_app);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void reLoadData() {
        TagProcessManage.getInstance(this).reLoadData();
    }

    /**
     * check 用户付费情况
     */
    public void checkIssueLevel() {
        /**
         * 登陆后获取用户付费情况
         */
        if (SlateDataHelper.getUserLoginInfo(this) != null) {
            // 有未提交订单(支付宝),更新状态,使用本地订单
            if (SlateDataHelper.getOrder(this, PayHttpsOperate.NEW_ALI_KEY) != null) {
                PayHttpsOperate.getInstance(this).notifyServer(getOrderStatus(SlateDataHelper.getOrder(this, PayHttpsOperate.NEW_ALI_KEY)), PayHttpsOperate.NEW_ALI_KEY);
            }
            // 有未提交订单(微信),更新状态,使用本地订单
            else if (SlateDataHelper.getOrder(this, PayHttpsOperate.NEW_WEIXIN_KEY) != null) {
                PayHttpsOperate.getInstance(this).notifyServer(getOrderStatus(SlateDataHelper.getOrder(this, PayHttpsOperate.NEW_WEIXIN_KEY)), PayHttpsOperate.NEW_WEIXIN_KEY);
            } else {
                Log.e("获取线上状态", "CommonMainActivity");
                // 没有未提交订单，取线上状态
                PayHttpsOperate.getInstance(this).getUserPermission();
            }

        }
    }

    private String getOrderStatus(String json) {
        String status = "";
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (null != jsonObject) {
                status = jsonObject.optString("status");
            }
        } catch (JSONException e) {
        }
        return status;
    }

    /**
     * 更新版本
     */
    private void checkBbwc() {
        //googleplay 市场禁止检测版本更新
        if (CommonApplication.CHANNEL.equals("googleplay")) {
            return;
        }
        if (DataHelper.getRefuseNoticeVersion(this)) {
            return;
        }

        UpdateManager manager = new UpdateManager(this, new UpdateManager.CheckVersionListener() {

            @Override
            public void checkEnd() {
            }
        });
        manager.checkVersion();
    }

    /**
     * 下载广告资源
     */
    private void downLoadAdvRes() {
        DownloadAvdRes downloadAvdRes = new DownloadAvdRes(this);
        // 下载splash图
        downloadAvdRes.downloadSplash(AppValue.appInfo.getSplash());
        // 下载广告资源
        downloadAvdRes.downloadRunBan();
    }

    @Override
    protected void exitApp() {
        super.exitApp();
        TagProcessManage.getInstance(this).destory();
        CommonApplication.exit();
        liveUrl = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PageTransfer.REQUEST_CODE) {
                notifyRead();
            }
        }
        // /** 使用SSO授权必须添加如下代码 */
        // SsoHandler ssoHandler = mController.getConfig().getSsoHandler(
        // requestCode);
        // if (ssoHandler != null) {
        // ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        // }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 回到推荐首页
     */
    public abstract void gotoIndex(boolean isColumn);


}
