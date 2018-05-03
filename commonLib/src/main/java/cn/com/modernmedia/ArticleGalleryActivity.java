package cn.com.modernmedia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.bitmap.download.SimpleDownloader;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.adapter.MyPagerAdapter.OnItemClickListener;
import cn.com.modernmedia.common.SharePopWindow;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.Picture;
import cn.com.modernmedia.util.MyAnimate;
import cn.com.modernmedia.widget.MyCircularViewPager;
import cn.com.modernmediaslate.unit.ImgFileManager;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 文章详情图片gallery
 *
 * @author ZhuQiao
 */
public class ArticleGalleryActivity extends BaseActivity implements NotifyArticleDesListener, OnClickListener {
    private Context mContext;
    private MyCircularViewPager viewPager;
    private Button mBlackButton;
    private TextView titleTextView, descTextView;
    private int lineStartX, lineEndX;
    private boolean mShowToolbar = true;
    private RelativeLayout mToolbarLayout;
    private int translateY;
    private List<String> urlList = new ArrayList<String>();
    private String title, desc;
    private int index;// 第一次显示图片位置
    private List<ArticleItem> itemList = new ArrayList<ArticleItem>();
    private int currentPosition;
    private byte[] data;
    private boolean landScape = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_gallery);
        //		if (onSystemDestory(CommonSplashActivity.class)) {
        //			finish();
        //			return;
        //		}
        mContext = this;
        fetchDataFromIntent();
        init();
    }

    private void fetchDataFromIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            urlList = bundle.getStringArrayList("URL_LIST");
            title = bundle.getString("TITLE");
            desc = bundle.getString("DESC");
            index = bundle.getInt("INDEX");
        }
    }

    private void init() {
        translateY = getResources().getDimensionPixelOffset(R.dimen.article_gallery_button_height);
        viewPager = (MyCircularViewPager) findViewById(R.id.article_gallery_viewpager);
        mBlackButton = (Button) findViewById(R.id.article_gallery_right_line);
        mToolbarLayout = ((RelativeLayout) findViewById(R.id.toolbar_layout));
        titleTextView = (TextView) findViewById(R.id.article_gallery_title);
        descTextView = (TextView) findViewById(R.id.article_gallery_desc);

        findViewById(R.id.article_gallery_share).setOnClickListener(this);
        findViewById(R.id.article_gallery_download).setOnClickListener(this);
        findViewById(R.id.article_gallery_close).setOnClickListener(this);
        viewPager.setListener(this);

        setDataForPager();
    }

    @SuppressWarnings("rawtypes")
    private void setDataForPager() {
        if (!ParseUtil.listNotNull(urlList)) return;
        // 把图片url封装成articleitem
        for (int i = 0; i < urlList.size(); i++) {
            ArticleItem item = new ArticleItem();
            Picture picture = new Picture();
            picture.setUrl(urlList.get(i));
            item.getPicList().add(picture);
            item.setTitle(title);
            item.setDesc(desc);
            item.setWeburl(urlList.get(i));
            itemList.add(item);
        }
        viewPager.setDataForPager(itemList, index);
        initRightLine();

        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter != null && adapter instanceof MyPagerAdapter) {
            ((MyPagerAdapter) adapter).setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(View v, int position) {
                    toolBarAnim();
                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            landScape = true;
        } else landScape = false;
    }

    private void initRightLine() {
        if (itemList.size() == 1) {
            MyAnimate.startTranslateAnimation(mBlackButton, 0, CommonApplication.width);
        }
    }

    @Override
    public void updateDes(int position) {
        if (!ParseUtil.listNotNull(itemList) || itemList.size() <= position) return;
        if (landScape) {
            lineEndX = Math.round(CommonApplication.height * (position + 1) / itemList.size());
            MyAnimate.startTranslateAnimation(mBlackButton, lineStartX, lineEndX);
            lineStartX = lineEndX;
            Log.e("line1", lineEndX + "-");
        } else {
            lineEndX = Math.round(CommonApplication.width * (position + 1) / itemList.size());
            MyAnimate.startTranslateAnimation(mBlackButton, lineStartX, lineEndX);
            lineStartX = lineEndX;
            Log.e("line2", lineEndX + "-");
        }
        currentPosition = position;
        titleTextView.setText((position + 1) + "/" + itemList.size());
        String str = urlList.get(position).substring(urlList.get(position).indexOf("#") + 1, urlList.get(position).length());
        try {
            str = URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (str.startsWith("http") || str.startsWith("https")) str = "";
        descTextView.setText(str);
    }

    @Override
    public void updatePage(int state) {

    }

    @Override
    public void reLoadData() {
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.article_gallery_share) {
            int index = viewPager.getCurrentItem();
            if (ParseUtil.listNotNull(itemList) && itemList.size() > index) {
                new SharePopWindow(mContext, itemList.get(index));
            }
        } else if (v.getId() == R.id.article_gallery_download) {//下载图片
            final SimpleDownloader downloader = new SimpleDownloader();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    data = downloader.download(urlList.get(currentPosition));
                    ImgFileManager.saveArticleImage(ArticleGalleryActivity.this, urlList.get(currentPosition), data);
                }
            }).start();
            Tools.showToast(this, "图片已保存到" + ImgFileManager.getArticleBitmapPath(urlList.get(currentPosition)));
        } else if (v.getId() == R.id.article_gallery_close) {
            finish();
        }
    }

    private void toolBarAnim() {
        translateY = mShowToolbar ? Math.abs(translateY) : -Math.abs(translateY);
        TranslateAnimation ta = new TranslateAnimation(0, 0, 0, translateY);
        ta.setDuration(300);
        ta.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mShowToolbar = !mShowToolbar;
                mToolbarLayout.clearAnimation();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mToolbarLayout.getLayoutParams();
                params.bottomMargin = params.bottomMargin - translateY;
                mToolbarLayout.setLayoutParams(params);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mToolbarLayout.startAnimation(ta);
    }

    @Override
    public String getActivityName() {
        return ArticleGalleryActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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

}
