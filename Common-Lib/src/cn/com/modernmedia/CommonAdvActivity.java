package cn.com.modernmedia;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewFlipper;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.GenericConstant;

/**
 * 广告页面
 * 
 * @author ZhuQiao
 * 
 */
public class CommonAdvActivity extends BaseActivity {
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
	private String effect;
	private String impressionUrl;
	private AlphaAnimation alphaOut;
	private AlphaAnimation alphaIn;
	private int current;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 100) {
				switchActivity();
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adv);
		init();
	}

	private void init() {
		imageView = (ImageView) findViewById(R.id.adv_image);
		flipper = (ViewFlipper) findViewById(R.id.adv_flipper);
		picList = getIntent().getExtras().getStringArrayList(
				GenericConstant.PIC_LIST);
		effect = getIntent().getExtras().getString(GenericConstant.EFFECTS);
		impressionUrl = getIntent().getExtras().getString(
				GenericConstant.IMPRESSION_URL);
		AdvTools.requestImpression(impressionUrl);
		if (TextUtils.isEmpty(effect) || !EFFECT_LIST.contains(effect))
			effect = EFFECT_LIST.get(0);
		alphaOut = new AlphaAnimation(0.5f, 1.0f);
		alphaOut.setFillAfter(true);
		alphaOut.setDuration(1000);
		alphaOut.setInterpolator(new LinearInterpolator());
		alphaIn = new AlphaAnimation(.2f, 0.0f);
		alphaIn.setDuration(1000);
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
					switchActivity();
				}
			}
		});
		doEffect();
	}

	private void doEffect() {
		// imageView.setImageResource(R.drawable.ca9601);
		if (effect.equals(EFFECT_LIST.get(0))) {
			imageView.setImageBitmap(CommonApplication.finalBitmap
					.getBitmapFromDiskCache(picList.get(0)));
			gotoMainActivity();
		} else if (effect.equals(EFFECT_LIST.get(1))) {
			imageView.setImageBitmap(CommonApplication.finalBitmap
					.getBitmapFromDiskCache(picList.get(0)));
			ScaleAnimation scaleAnimation = new ScaleAnimation(1.2f, 1f, 1.2f,
					1f, Animation.RELATIVE_TO_SELF, .5f,
					Animation.RELATIVE_TO_SELF, .5f);
			scaleAnimation.setDuration(2000);
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
				gotoMainActivity();
		}
	}

	private void doFlipper() {
		flipper.setInAnimation(alphaOut);
		flipper.setOutAnimation(alphaIn);
		flipper.showNext();
	}

	/**
	 * 显示完入版广告后进入首页
	 */
	private void gotoMainActivity() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				switchActivity();
			}
		}, ConstData.SPLASH_DELAY_TIME);
	};

	private void switchActivity() {
		if (CommonApplication.mainCls == null)
			return;
		Intent intent = new Intent(CommonAdvActivity.this,
				CommonApplication.mainCls);
		intent.putExtra(GenericConstant.FROM_ACTIVITY,
				GenericConstant.FROM_ACTIVITY_VALUE);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.alpha_out_1s, R.anim.alpha_in_1s);
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

}
