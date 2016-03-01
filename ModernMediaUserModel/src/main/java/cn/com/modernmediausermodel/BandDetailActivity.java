package cn.com.modernmediausermodel;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.api.BandAccountOperate;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.VerifyCode;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 绑定手机号、邮箱、收货地址
 * 
 * @author lusiyuan
 * 
 */
public class BandDetailActivity extends SlateBaseActivity implements
		OnClickListener {
	private int type;
	private TextView title, getCode;
	private EditText userName, code;
	private Button complete;
	private LinearLayout verifyLayout;// 收货地址layout
	private boolean canGetVerify = true;// 是否可获取验证码
	private UserOperateController mController;
	private User user;

	private Animation shakeAnim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.band_detail_activity);
		type = getIntent().getIntExtra("band_type", 0);
		user = (User) getIntent().getSerializableExtra("band_user");
		mController = UserOperateController.getInstance(this);
		initView();

	}

	private void initView() {
		shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
		verifyLayout = (LinearLayout) findViewById(R.id.layout_verify);
		title = (TextView) findViewById(R.id.band_title);
		userName = (EditText) findViewById(R.id.forget_phone);
		code = (EditText) findViewById(R.id.forget_verify_edit);
		complete = (Button) findViewById(R.id.forget_complete);
		getCode = (TextView) findViewById(R.id.forget_get_verify);

		if (type == BandAccountOperate.PHONE) {
			title.setText(R.string.band_phone);
			userName.setHint(R.string.phone_number);
		} else if (type == BandAccountOperate.EMAIL) {
			title.setText(R.string.band_email);
			userName.setHint("Email");
			verifyLayout.setVisibility(View.GONE);
		}

		findViewById(R.id.band_back).setOnClickListener(this);
		complete.setOnClickListener(this);
		getCode.setOnClickListener(this);
		findViewById(R.id.forget_phone_clear).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.forget_complete) {// 完成
			if (userName != null) {
				String name = userName.getText().toString();
				String c = code.getText().toString();
				if (type == BandAccountOperate.PHONE
						&& UserTools.checkString(name, userName, shakeAnim)
						&& UserTools.checkString(c, code, shakeAnim)) {
					doBand(name, BandAccountOperate.PHONE, c);
				} else if (type == BandAccountOperate.EMAIL
						&& UserTools.checkString(name, userName, shakeAnim)) {
					doBand(name, BandAccountOperate.EMAIL, null);
				}
			}
		} else if (v.getId() == R.id.forget_get_verify) {// 获取验证码
			if (userName != null) {
				String name = userName.getText().toString();
				if (UserTools.checkIsPhone(BandDetailActivity.this, name)
						&& UserTools.checkString(name, userName, shakeAnim))
					doGetVerifyCode(name);
			}
		} else if (v.getId() == R.id.band_back) {
			finish();
		} else if (v.getId() == R.id.forget_phone_clear) {
			doClear();
		}
	}

	/**
	 * 清除昵称
	 */
	protected void doClear() {
		if (userName != null)
			userName.setText("");
	}

	/**
	 * 绑定
	 * 
	 * @param c
	 *            username
	 * @param bindType
	 *            绑定类型
	 * @param code
	 *            验证码
	 */
	protected void doBand(final String c, final int bindType, String code) {
		if (user == null)
			return;
		mController.bandAccount(user.getUid(), user.getToken(), bindType, c,
				code, new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						ErrorMsg error = null;
						if (entry != null)
							error = (ErrorMsg) entry;
						else
							return;
						if (error.getNo() == 0) {

							if (bindType == BandAccountOperate.PHONE) {// 存储绑定信息
								user.setPhone(c);
								showToast(R.string.band_succeed);
							} else if (bindType == BandAccountOperate.EMAIL) {
								user.setEmail(c);
								showToast(R.string.band_email_succeed);
							}
							SlateDataHelper.saveUserLoginInfo(
									BandDetailActivity.this, user);
							finish();
						} else {
							showToast(error.getDesc());
						}
					}
				});
	}

	/**
	 * 获取手机验证码
	 */
	protected void doGetVerifyCode(String c) {
		if (canGetVerify) {
			canGetVerify = false;
			// 开启倒计时器
			new CountDownTimer(60000, 1000) {
				public void onTick(long millisUntilFinished) {
					getCode.setText(millisUntilFinished / 1000 + "s重新获取");
				}

				public void onFinish() {
					getCode.setText(R.string.get_verify_code);
					canGetVerify = true;
				}
			}.start();
			mController.getVerifyCode(c, new UserFetchEntryListener() {

				@Override
				public void setData(Entry entry) {
					if (entry instanceof VerifyCode) {
						// showToast(entry.toString());
					}
				}
			});
		}
	}

	@Override
	public String getActivityName() {
		return null;
	}

	@Override
	public Activity getActivity() {
		return null;
	}

}
