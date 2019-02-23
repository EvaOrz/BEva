package cn.com.modernmedia.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.UrlMaker;
import cn.com.modernmedia.common.SharePopWindow;
import cn.com.modernmedia.db.NewFavDb;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.IndexProperty;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.views.fav.BindFavToUserImplement;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmedia.views.xmlparse.article.XMLDataSetForPushArticle;
import cn.com.modernmedia.widget.ArticleDetailItem;
import cn.com.modernmediaslate.unit.TimeCollectUtil;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 显示从parse来的文章
 *
 * @author jiancong
 */
public class PushArticleActivity extends ArticleActivity {
    private FrameLayout contentView;
    private boolean isFav = false;
    private boolean isNeedShare = true;

    private XMLDataSetForPushArticle dataSet;
    private ArticleDetailItem articleDetailItem;
    private ArticleItem articleItem = new ArticleItem();
    //手指上下滑动时的最小速度
    private static final int YSPEED_MIN = 1000;

    //手指向右滑动时的最小距离
    private static final int XDISTANCE_MIN = 150;

    //手指向上滑或下滑时的最小距离
    private static final int YDISTANCE_MIN = 100;

    //记录手指按下时的横坐标。
    private float xDown;

    //记录手指按下时的纵坐标。
    private float yDown;

    //记录手指移动时的横坐标。
    private float xMove;

    //记录手指移动时的纵坐标。
    private float yMove;

    //用于计算手指滑动的速度。
    private VelocityTracker mVelocityTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transferArticle = null;// 阻止CommonArticleActivity往下执行
        initDataFromBundle();
        setContentView(R.layout.push_article_activity);

        TimeCollectUtil.getInstance().savePageTime(TimeCollectUtil.EVENT_OPEN_PUSH, true);

        init();
    }

    /**
     * 初始化从上一个页面传递过来的数据
     */
    private void initDataFromBundle() {
        if (getIntent() == null || getIntent().getExtras() == null) {
            return;
        }
        String id = getIntent().getExtras().getString(TagProcessManage.KEY_PUSH_ARTICLE_ID);

        articleItem = (ArticleItem) getIntent().getExtras().getSerializable(TagProcessManage.KEY_PUSH_ARTICLE);
        if (articleItem == null) {
            articleItem = new ArticleItem();
        }
        isFav = getIntent().getBooleanExtra("is_fav", false);
        isNeedShare = getIntent().getBooleanExtra("is_need_share", true);

        if (TextUtils.isEmpty(id)) return;
        articleItem.setArticleId(Integer.valueOf(id));
        int level = getIntent().getExtras().getInt(TagProcessManage.KEY_PUSH_ARTICLE_LEVEL);

        // 文章level
        IndexProperty indexProperty = new IndexProperty();
        indexProperty.setLevel(level);
        indexProperty.setType(1);
        articleItem.setWeburl(UrlMaker.getPushArticle(id));
        articleItem.setProperty(indexProperty);

        // 统计push文章打开
        LogHelper.logOpenArticleFromPush(this, articleItem.getArticleId() + "", articleItem.getTitle());
    }

    @Override
    protected void changeFavBtn(boolean isFaved) {
        dataSet.changeFavBtn(isFaved);
    }

    @Override
    protected void init() {
        db = NewFavDb.getInstance(this);
        setBindFavToUserListener(new BindFavToUserImplement(this));
        contentView = findViewById(R.id.push_article_content);
        RelativeLayout navBar = findViewById(R.id.push_article_toolbar);
        template = ParseProperties.getInstance(this).parseArticle();
        XMLParse parse = new XMLParse(this, null);
        View view = parse.inflate(template.getNavBar().getData(), null, "");
        navBar.addView(view);
        dataSet = parse.getDataSetForPushArticle();
        dataSet.setData();
        dataSet.hideKeyButton(isFav, false, isNeedShare, false);
        if (isFav) {
            changeFavBtn(true);
        }

        // 导航栏的顶部和文章页的顶部对齐
        if (template.getIsAlignToNav() == 0) {
            LayoutParams lp = (LayoutParams) contentView.getLayoutParams();
            lp.topMargin = getResources().getDimensionPixelSize(R.dimen.article_bar_height);
        }

        View detailView = fetchView(articleItem);

        if (detailView instanceof ArticleDetailItem) {
            articleDetailItem = (ArticleDetailItem) detailView;
            articleDetailItem.setData(articleItem);
            if (articleDetailItem.getWebView() != null)
                articleDetailItem.getWebView().setPushArticle(true);
            contentView.addView(articleDetailItem, new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (FrameLayout.LayoutParams.MATCH_PARENT)));
        }

        // TODO
        TimeCollectUtil.getInstance().savePageTime("OpenPush", false);
    }


    @Override
    public void addFav() {
        if (articleItem == null) return;
        ModernMediaTools.addFav(this, articleItem, Tools.getUid(this), bindFavToUserListener);
        if (db.containThisFav(articleItem.getArticleId(), Tools.getUid(this))) {
            changeFavBtn(true);
        } else {
            changeFavBtn(false);
        }
    }

    /**
     * 点击分享按钮
     */
    @Override
    public void showShare() {
        if (articleItem == null) return;
        new SharePopWindow(this, articleItem);
    }

    @Override
    public void reLoadData() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        if (!TagProcessManage.getInstance(this).isAppIsRunning() && CommonApplication.splashCls != null) {
            Intent intent = new Intent(this, CommonApplication.splashCls);
            startActivity(intent);
            overridePendingTransition(cn.com.modernmedia.R.anim.alpha_out, cn.com.modernmedia.R.anim.hold);
        }

        super.finish();

        if (articleDetailItem != null && articleDetailItem.getWebView() != null)
            articleDetailItem.getWebView().pop();
        overridePendingTransition(R.anim.hold, R.anim.down_out);
    }

    @Override
    public String getActivityName() {
        return PushArticleActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        createVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = event.getRawX();
                yDown = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = event.getRawX();
                yMove = event.getRawY();
                //滑动的距离
                int distanceX = (int) (xMove - xDown);
                int distanceY = (int) (yMove - yDown);
                //获取顺时速度
                int ySpeed = getScrollVelocity();
                //关闭Activity需满足以下条件：
                //1.x轴滑动的距离>XDISTANCE_MIN
                //2.y轴滑动的距离在YDISTANCE_MIN范围内
                //3.y轴上（即上下滑动的速度）<XSPEED_MIN，如果大于，则认为用户意图是在上下滑动而非左滑结束Activity
                if (distanceX > XDISTANCE_MIN && (distanceY < YDISTANCE_MIN && distanceY > -YDISTANCE_MIN) && ySpeed < YSPEED_MIN) {
                    finish();
                }
                break;
            case MotionEvent.ACTION_UP:
                recycleVelocityTracker();
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 创建VelocityTracker对象，并将触摸界面的滑动事件加入到VelocityTracker当中。
     *
     * @param event
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getYVelocity();
        return Math.abs(velocity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, iUiListener);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_API) {
                if (resultCode == Constants.REQUEST_QQ_SHARE || resultCode == Constants.REQUEST_QZONE_SHARE || resultCode == Constants.REQUEST_OLD_SHARE) {
                    Tencent.handleResultData(data, iUiListener);
                }
            }
        }
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
