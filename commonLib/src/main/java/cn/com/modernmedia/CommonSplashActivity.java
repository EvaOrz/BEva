package cn.com.modernmedia;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.db.NewFavDb;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.Down;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage.SplashCallback;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.GenericConstant;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.FavObservable;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 进版首页
 *
 * @author ZhuQiao
 */
public abstract class CommonSplashActivity extends BaseActivity {
    private Context mContext;
    protected AdvList advList;
    private Uri fromHtmlArticleUri;// 网页跳转文章页面url参数
    private boolean ifcanGoMain = true;

    private boolean isNomalStart = true;//用于链接点击返回跳出splash画面flag
    /**
     * 需要的权限
     */
    private String[] needpermissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;


        /**
         * 网页跳转测试用
         */
        Intent i_getvalue = getIntent();
        String action = i_getvalue.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = i_getvalue.getData();
            fromHtmlArticleUri = uri;
            Log.e("fromHtmlArticleUri", fromHtmlArticleUri.toString());
        }
        if (!ifHasPermissions()) {
            if (!askPermission()) initParse();
        } else initParse();
    }

    private void initParse() {
        TagProcessManage.getInstance(this).onStart(getIntent(), new SplashCallback() {

            @Override
            public void onParseMsgAnalyed(boolean isPush, String pushArticleUrl, int level, boolean isAppRunning) {
                onParseMsgAnalyedHre(isPush, pushArticleUrl, level, isAppRunning);
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101:
                // 101的第二个权限 是读设备id
                //                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                initParse();
                //                }

                break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * parse消息分析结束
     */
    private void onParseMsgAnalyedHre(boolean isPush, String pushArticleUrl, int level, boolean isAppRunning) {
        Log.e(isPush ? "是push" : "不是push", isAppRunning ? "在运行" : "不在运行");
        if (!isPush || !isAppRunning) {
            // NOTE 不是push或者应用不在运行中,那么照常运行
            // NOTE 如果是push但是应用不在运行,那么之后的cache流程会去验证是否需要弹出push文章
            init();
            TagProcessManage.getInstance(this).fetchFromHttp();
            return;
        }

        if (TextUtils.isEmpty(pushArticleUrl)) {
            // NOTE 如果应用运行中并且不需要显示push文章,那么关闭splash页面
            finish();
            return;
        }

        Log.e("应用运行中并且需要显示push文章", pushArticleUrl);
        // NOTE 应用运行中并且需要显示push文章
        //        setContentView(R.layout.layout_splash_transparent);
        if (level == -1) {
            // 获得栈顶焦点
            //            UriParse.clickSlate(CommonSplashActivity.this, pushArticleUrl, new Entry[]{new ArticleItem()}, null, new Class<?>[0]);
        } else {

            TagProcessManage.getInstance(this).showPushArticleActivity(this, pushArticleUrl, level);

        }
        //        finish();
    }

    @Override
    protected void onResume() {

        if (!TextUtils.isEmpty(SlateApplication.mConfig.getUmeng_key())) {
            MobclickAgent.onResume(this);
        }
        if (!isNomalStart) {
            checkHasHtmlAdv();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!TextUtils.isEmpty(SlateApplication.mConfig.getUmeng_key())) {
            MobclickAgent.onPause(this);
        }
    }

    private void init() {
        setContentViewById();

        CommonApplication.clear();
        down();
        getAdvList();
        // 旧收藏数据迁移
        if (!DataHelper.getDbChanged(mContext)) {
            NewFavDb.getInstance(this).dataTransfer();
            DataHelper.setDbChanged(mContext);
            // 迁移服务器上的数据
            SlateApplication.favObservable.setData(FavObservable.DATA_CHANGE);
        }
    }

    protected abstract void setContentViewById();

    private void initAdv() {
        if (advList == null || !ParseUtil.mapContainsKey(advList.getAdvMap(), AdvList.RU_BAN)) {// 取缓存
            checkHasAdv(null, null);
        } else {// 线上拿
            List<AdvItem> list = advList.getAdvMap().get(AdvList.RU_BAN);
            if (ParseUtil.listNotNull(list)) {
                for (AdvItem item : list) {
                    if (checkPicAdvValid(item)) return;
                }
            }
            checkHasAdv(null, null);
        }
    }

    /**
     * 判断图片广告是否有效
     *
     * @param item
     * @return
     */
    private boolean checkPicAdvValid(AdvItem item) {
        if (!advIsValid(item)) return false;
        if (item.getShowType() != 0)// 不是图片广告
            return false;

        ArrayList<String> picList = new ArrayList<String>();
        for (AdvSource pic : item.getSourceList()) {
            // NOTE 当所有图片都下载成功时，才进入入版广告页
            if (CommonApplication.finalBitmap.getBitmapFromDiskCache(pic.getUrl()) == null) {
                return false;
            }
            picList.add(pic.getUrl());
        }
        checkHasAdv(picList, item);
        return true;
    }

    /**
     * 广告是否有效
     *
     * @param item
     * @return
     */
    protected boolean advIsValid(AdvItem item) {
        // 广告是否过期
        if (AdvTools.advIsExpired(item.getStartTime(), item.getEndTime())) return false;
        // 是否有资源列表
        if (!ParseUtil.listNotNull(item.getSourceList())) return false;
        return true;
    }

    protected void checkHasAdv(final ArrayList<String> picList, final AdvItem item) {
        if (ConstData.getInitialAppId() == 20) {
            if (ParseUtil.listNotNull(picList)) gotoAdvActivity(picList, item);
            return;
        }
        if (fromHtmlArticleUri != null) {
            //            String i = fromHtmlArticleUri.toString().replace("slate1://", "slate://");
            boolean flag = UriParse.clickSlate(CommonSplashActivity.this, fromHtmlArticleUri.toString(), new Entry[]{new ArticleItem()}, null, new Class<?>[0]);
            if (flag) {
                isNomalStart = false;
                finish();
            } else {
                checkHasHtmlAdv();
            }
        } else if (ParseUtil.listNotNull(picList)) {
            gotoAdvActivity(picList, item);
        } else {
            // 没有图片广告，继续判断有没有html广告
            checkHasHtmlAdv();
        }
    }

    /**
     * 判断是否有html入版广告
     */
    private void checkHasHtmlAdv() {
        if (!(advList instanceof AdvList)) {
            gotoMainActivity();
            return;
        }
        if (advList == null || !ParseUtil.mapContainsKey(advList.getAdvMap(), AdvList.RU_BAN)) {
            gotoMainActivity();
            return;
        }
        List<AdvItem> list = advList.getAdvMap().get(AdvList.RU_BAN);
        if (!ParseUtil.listNotNull(list)) {
            gotoMainActivity();
            return;
        }

        for (AdvItem item : list) {
            if (!advIsValid(item)) continue;
            String zip = item.getSourceList().get(0).getUrl();
            if (TextUtils.isEmpty(zip)) continue;
            if (!FileManager.containThisPackageFolder(zip)) continue;
            if (TextUtils.isEmpty(FileManager.getHtmlName(zip))) continue;
            gotoAdvHtmlActivity(item);
            return;
        }
        gotoMainActivity();
    }

    private void getAdvList() {
        OperateController.getInstance(mContext).getAdvList(FetchApiType.USE_CACHE_ONLY, new FetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (entry instanceof AdvList) {
                    advList = (AdvList) entry;
                }
                initAdv();
            }
        });
    }

    public void gotoMainActivity() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (CommonApplication.mainCls == null || !ifcanGoMain) finish();
                Intent intent = new Intent(mContext, CommonApplication.mainCls);
                intent.putExtra(GenericConstant.FROM_ACTIVITY, GenericConstant.FROM_ACTIVITY_VALUE);
                startActivity(intent);
                ifcanGoMain = false;

                finish();
                overridePendingTransition(R.anim.alpha_out, R.anim.hold);
            }
        }, ConstData.SPLASH_DELAY_TIME);

    }

    private void gotoAdvHtmlActivity(AdvItem adv) {
        Intent intent = new Intent(this, AdvActivity.class);
        intent.putExtra(GenericConstant.FROM_ACTIVITY, GenericConstant.FROM_ACTIVITY_VALUE);
        intent.putExtra("IN_APP_ADV", adv);
        startActivity(intent);
        ifcanGoMain = false;
        finish();
        //        overridePendingTransition(R.anim.alpha_out_1s, R.anim.alpha_in_1s);
    }

    protected void gotoAdvActivity(ArrayList<String> picList, AdvItem item) {
        Intent intent = new Intent(mContext, CommonAdvActivity.class);
        intent.putExtra(GenericConstant.FROM_ACTIVITY, GenericConstant.FROM_ACTIVITY_VALUE);
        intent.putExtra(GenericConstant.PIC_LIST, picList);
        intent.putExtra(GenericConstant.ADV_ITEM, item);
        startActivity(intent);
        ifcanGoMain = false;
        finish();
        overridePendingTransition(R.anim.alpha_out, R.anim.hold);
    }

    /**
     * 统计装机量
     */
    private void down() {
        // 初始化友盟
        if (!TextUtils.isEmpty(SlateApplication.mConfig.getUmeng_key())) {
            //            AnalyticsConfig.setAppkey(SlateApplication.mConfig.getUmeng_key());
            // MobclickAgent.setDebugMode(true);
        }

        if (DataHelper.getDown(this)) return;
        OperateController.getInstance(this).getDown(new FetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (entry instanceof Down) {
                    Down down = (Down) entry;
                    if (down.isSuccess()) {
                        DataHelper.setDown(mContext);
                    }
                }
            }
        });
    }


    @Override
    public void finish() {
        super.finish();
        fromHtmlArticleUri = null;
    }

    @Override
    public void reLoadData() {
    }

    /**
     * 是否获取了所有权限
     *
     * @return
     */
    public boolean ifHasPermissions() {
        for (int i = 0; i < needpermissions.length; i++) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, needpermissions[i]);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Log.e("ifHasPermissions", needpermissions[i]);
                return false;
            }
        }
        return true;
    }

    /**
     * 询问权限
     */
    public boolean askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> ll = new ArrayList<>();
            for (int i = 0; i < needpermissions.length; i++) {
                int permissionCheck = ContextCompat.checkSelfPermission(this, needpermissions[i]);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ll.add(needpermissions[i]);
                }
            }
            if (ll.size() > 0) {
                ActivityCompat.requestPermissions(this, ll.toArray(new String[ll.size()]), 101);
                return true;
            }
        }
        return false;

    }

}