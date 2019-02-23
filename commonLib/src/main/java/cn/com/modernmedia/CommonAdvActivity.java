package cn.com.modernmedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewFlipper;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.GenericConstant;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 广告页面
 *
 * @author ZhuQiao
 */
public class CommonAdvActivity extends BaseActivity {
    public static final int IBB_DURATION = 1000;
    public static final int ILOHAS_DURATION = 2000;// 乐活默认时间(其实是3s，动画完了还有1s延迟)
    public static final int IWEEKLY_DURATION = 2000;
    private boolean canGoMain = true;// 防止重复进入main
    private long lastTime;

    public static final List<String> EFFECT_LIST = new ArrayList<String>() {
        private static final long serialVersionUID = 1L;

        {
            add(AdvList.IBB);
            add(AdvList.ILOHAS);
            add(AdvList.IWEEKLY);
        }
    };
    private ImageView imageView;
    private ViewFlipper flipper;
    private ArrayList<String> picList;
    private AdvItem advItem;
    private String effect;
    private AlphaAnimation alphaOut;
    private AlphaAnimation alphaIn;
    private int current;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100) {
                gotoMainActivity();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adv);
        if (fetchDataFromIntent() && compareTime())
            init();
        else
            gotoMainActivity();
        if (compareTime()) DataHelper.setAdvTime(this, System.currentTimeMillis());
    }

    /**
     * 入版广告(5分钟内不重复提示)
     *
     * @return true提示；false不提示
     */
    private boolean compareTime() {
        lastTime = DataHelper.getAdvTime(this);
        if (lastTime == 0) {
            return true;
        }
        long minute = (System.currentTimeMillis() - lastTime) / (1000 * 60);
        return minute > 5;
    }

    private boolean fetchDataFromIntent() {
        if (getIntent() == null || getIntent().getExtras() == null) {
            return false;
        }
        picList = getIntent().getExtras().getStringArrayList(
                GenericConstant.PIC_LIST);
        if (!ParseUtil.listNotNull(picList))
            return false;
        Object object = getIntent().getExtras().get(GenericConstant.ADV_ITEM);
        if (object instanceof AdvItem) {
            advItem = (AdvItem) object;
            return true;
        }
        return false;
    }

    private void init() {
        imageView = (ImageView) findViewById(R.id.adv_image);
        flipper = (ViewFlipper) findViewById(R.id.adv_flipper);
        // 跳出
        findViewById(R.id.adv_imgo).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                gotoMainActivity();
            }
        });
        // 商周繁体版入版广告点击
        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (advItem.getSourceList().size() > 0
                        && advItem.getSourceList().get(0).getLinks().size() > 0) {
                    String link = advItem.getSourceList().get(0).getLinks()
                            .get(0);
                    UriParse.clickSlate(CommonAdvActivity.this, link,
                            new Entry[]{new ArticleItem()}, null,
                            new Class<?>[0]);
                }
            }
        });

        AdvTools.requestImpression(advItem.getTracker().getImpressionUrl());
        effect = advItem.getEffects();
        if (TextUtils.isEmpty(effect) || !EFFECT_LIST.contains(effect))
            effect = EFFECT_LIST.get(0);
        alphaOut = new AlphaAnimation(0.5f, 1.0f);
        alphaOut.setFillAfter(true);
        alphaOut.setDuration(getDuration(EFFECT_LIST.get(2)));
        alphaOut.setInterpolator(new LinearInterpolator());
        alphaIn = new AlphaAnimation(.2f, 0.0f);
        alphaIn.setDuration(getDuration(EFFECT_LIST.get(2)));
        alphaIn.setInterpolator(new LinearInterpolator());
        alphaOut.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (current < picList.size() - 1) {
                    doFlipper();
                    current++;
                } else {
                    gotoMainActivity();
                }
            }
        });
        doEffect();
    }

    private void doEffect() {
        if (effect.equals(EFFECT_LIST.get(0))) {
            imageView.setImageBitmap(CommonApplication.finalBitmap
                    .getBitmapFromDiskCache(picList.get(0)));
            doHoldAnim(effect);
        } else if (effect.equals(EFFECT_LIST.get(1))) {
            imageView.setImageBitmap(CommonApplication.finalBitmap
                    .getBitmapFromDiskCache(picList.get(0)));
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.2f, 1f, 1.2f,
                    1f, Animation.RELATIVE_TO_SELF, .5f,
                    Animation.RELATIVE_TO_SELF, .5f);
            scaleAnimation.setDuration(getDuration(effect));
            scaleAnimation.setInterpolator(new LinearInterpolator());
            scaleAnimation.setFillAfter(true);
            scaleAnimation.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    handler.sendEmptyMessageDelayed(100, 1000);
                }
            });
            imageView.startAnimation(scaleAnimation);
        } else if (effect.equals(EFFECT_LIST.get(2))) {
            imageView.setVisibility(View.GONE);
            flipper.setVisibility(View.VISIBLE);
            for (String url : picList) {
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
                imageView.setScaleType(ScaleType.CENTER_INSIDE);
                imageView.setImageBitmap(CommonApplication.finalBitmap
                        .getBitmapFromDiskCache(url));
                flipper.addView(imageView);
            }
            if (flipper.getChildCount() > 0)
                flipper.getChildAt(0).startAnimation(alphaOut);
            else
                doHoldAnim(effect);
        }
    }

    private void doFlipper() {
        flipper.setInAnimation(alphaOut);
        flipper.setOutAnimation(alphaIn);
        flipper.showNext();
    }

    /**
     * 执行hold动画
     */
    private void doHoldAnim(final String effect) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                gotoMainActivity();
            }
        }, getDuration(effect));
    }

    ;

    /**
     * 进入首页
     */
    private void gotoMainActivity() {
        if (CommonApplication.mainCls == null || !canGoMain)
            return;
        canGoMain = false;
        Intent intent = new Intent(this, CommonApplication.mainCls);
        intent.putExtra(GenericConstant.FROM_ACTIVITY,
                GenericConstant.FROM_ACTIVITY_VALUE);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.alpha_out_1s, R.anim.alpha_in_1s);
    }

    /**
     * 获取动画持续时间
     *
     * @param effect
     * @return
     */
    private int getDuration(String effect) {
        int close = advItem.getAutoClose() * 1000;
        if (TextUtils.equals(effect, EFFECT_LIST.get(0))) {
            return close == 0 ? IBB_DURATION : close;
        } else if (TextUtils.equals(effect, EFFECT_LIST.get(1))) {
            return close == 0 ? ILOHAS_DURATION : close;
        }
        return close == 0 ? IWEEKLY_DURATION : close;
    }

    @Override
    public void reLoadData() {
    }

    @Override
    public String getActivityName() {
        return CommonAdvActivity.class.getName();
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

}