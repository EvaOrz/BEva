package cn.com.modernmediausermodel.widget;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.MyCoinUseNoticeActivity;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.listener.CardViewListener;
import cn.com.modernmediausermodel.util.UserPageTransfer;

/**
 * 积分商城使用须知
 * 
 * @author jiancong
 * 
 */
public class MyCoinUseNoticeView implements CardViewListener {
	private Context mContext;
	private View view;
	private Button ok;
	private View ufoImage, ufoShip, ufoLight;
	private boolean onClick;
	private User user;

	public MyCoinUseNoticeView(Context context) {
		this.mContext = context;
		init();
	}

	private void init() {
		view = LayoutInflater.from(mContext).inflate(
				R.layout.activity_my_coin_notice, null);
		ufoImage = view.findViewById(R.id.my_coin_notice_ufo);
		ufoShip = view.findViewById(R.id.my_coin_notice_ship);
		ufoLight = view.findViewById(R.id.my_coin_notice_light);
		user = SlateDataHelper.getUserLoginInfo(mContext);
		if (user != null) {
			TextView name = (TextView) view
					.findViewById(R.id.my_coin_notice_user_name);
			name.setText(String.format(
					mContext.getString(R.string.welcom_user),
					user.getNickName()));
		}
		// 点击按钮，弹出对话框，用户可以选择修改邮箱或者进入金币页面
		ok = (Button) view.findViewById(R.id.my_coin_notice_ok);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doShipAnim();
			}
		});
		doLightAnim();
	}

	/**
	 * 执行灯光动画
	 */
	private void doLightAnim() {
		AlphaAnimation animation = new AlphaAnimation(0, 1);
		animation.setFillAfter(true);
		animation.setDuration(300);
		animation.setRepeatCount(3);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (!onClick)
					doUfoAnim();
			}
		});
		ufoLight.startAnimation(animation);
	}

	/**
	 * 执行下沉动画
	 */
	private void doUfoAnim() {
		ufoLight.clearAnimation();
		ufoLight.setVisibility(View.GONE);
		ufoShip.setVisibility(View.GONE);
		ufoImage.setVisibility(View.VISIBLE);
		TranslateAnimation animation = new TranslateAnimation(0, 0, 0,
				50 * SlateApplication.height / 1024);
		animation.setFillAfter(false);
		animation.setRepeatCount(5);
		animation.setRepeatMode(Animation.REVERSE);
		animation.setDuration(500);
		ufoImage.startAnimation(animation);
	}

	/**
	 * 执行飞走动画
	 */
	private void doShipAnim() {
		onClick = true;
		ufoImage.clearAnimation();
		ufoLight.clearAnimation();
		ufoShip.clearAnimation();
		ufoImage.setVisibility(View.INVISIBLE);
		ufoLight.setVisibility(View.GONE);
		ufoShip.setVisibility(View.VISIBLE);

		int upDuration = 200;
		int leftDuration = 200;
		int rightDuration = 500;

		AnimationSet set = new AnimationSet(false);
		TranslateAnimation goUp = new TranslateAnimation(0, 0, 0, -mContext
				.getResources().getDimensionPixelSize(
						R.dimen.my_coin_notice_ok_margin_top) / 2);
		goUp.setFillAfter(true);
		goUp.setDuration(upDuration);
		goUp.setInterpolator(new DecelerateInterpolator());
		TranslateAnimation goLeft = new TranslateAnimation(0,
				-SlateApplication.width / 4, 0, 0);
		goLeft.setFillAfter(true);
		goLeft.setDuration(leftDuration);
		goLeft.setStartOffset(upDuration);
		goLeft.setInterpolator(new DecelerateInterpolator());
		TranslateAnimation goRight = new TranslateAnimation(0,
				SlateApplication.width, 0, 0);
		goRight.setFillAfter(true);
		goRight.setDuration(rightDuration);
		goRight.setStartOffset(upDuration + leftDuration);
		goRight.setInterpolator(new AccelerateInterpolator());
		set.addAnimation(goUp);
		set.addAnimation(goLeft);
		set.addAnimation(goRight);
		set.setFillAfter(true);
		set.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				showSelectDialog();

			}
		});
		ufoShip.startAnimation(set);
	}

	private void showSelectDialog() {
		Builder builder = new AlertDialog.Builder(mContext);
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.popup_window_confirm_email, null);
		int padding = mContext.getResources().getDimensionPixelSize(
				R.dimen.dp20);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				SlateApplication.width - 2 * padding, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		view.setLayoutParams(params);
		builder.setView(view);
		final Dialog dialog = builder.create();
		dialog.show();
		// 初始化各个控件
		TextView email = (TextView) view
				.findViewById(R.id.confirm_email_window_address);
		View gotoShop = view.findViewById(R.id.confirm_email_window_goto_shop);
		View gotoModify = view
				.findViewById(R.id.confirm_email_window_modify_email);
		View cancel = view.findViewById(R.id.confirm_email_window_cancel);
		// 显示邮箱地址，默认是用户名
		if (user != null)
			email.setText(user.getUserName());
		// 设置事件处理
		gotoShop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog != null)
					dialog.dismiss();
				// 前往商城
				UserPageTransfer.gotoMyCoinActivity(mContext, true, true);
			}
		});
		gotoModify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog != null)
					dialog.dismiss();
				// 前往修改邮箱页面
				UserPageTransfer.gotoModifyEmailActivity(mContext);
				((MyCoinUseNoticeActivity) mContext).finish();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog != null)
					dialog.dismiss();
				ufoImage.clearAnimation();
				ufoLight.clearAnimation();
				ufoShip.clearAnimation();
				ufoImage.setVisibility(View.VISIBLE);
				ufoLight.setVisibility(View.GONE);
				ufoShip.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void showTitleBar(boolean show) {
		view.findViewById(R.id.my_coin_notice_bar_layout).setVisibility(
				show ? View.VISIBLE : View.GONE);
	}

	@Override
	public View fetchView() {
		return view;
	}

	public View getBackBtn() {
		return view.findViewById(R.id.my_coin_notice_button_back);
	}
}
