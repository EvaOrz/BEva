package cn.com.modernmediausermodel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import cn.com.modernmedia.util.sina.SinaAPI;
import cn.com.modernmedia.util.sina.SinaAuth;
import cn.com.modernmedia.util.sina.UserModelAuthListener;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.api.BandAccountOperate;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.util.WeixinLoginUtil;
import cn.com.modernmediausermodel.util.WeixinLoginUtil.WeixinAuthListener;

/**
 * 绑定账号activity
 * 
 * @author lusiyuan
 *
 */
/**
 * 绑定账号activity
 * 
 * @author lusiyuan
 *
 */
public class BandActivity extends SlateBaseActivity implements OnClickListener,
		OnCheckedChangeListener {
	public static int BAND_PHONE = 1;
	public static int BAND_EMAIL = 2;
	public static int BAND_ADDRESS = 3;
	public static int BAND_WEIBO = 4;
	public static int BAND_WEIXIN = 5;
	public static int BAND_QQ = 6;

	private UserOperateController mController;
	private User mUser;
	private TextView bandedPhone, bandedEmail;
	private Switch weibo, weixin, qq;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.band_activity);
		mController = UserOperateController.getInstance(this);
		initView();
		showLoadingDialog(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}

	private void initData() {
		mUser = SlateDataHelper.getUserLoginInfo(this);
		if (mUser == null)
			return;
		mController.getBandStatus(mUser.getUid(), mUser.getToken(),
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						showLoadingDialog(false);
						User u = (User) entry;
						if (u != null) {
							mUser.setBandPhone(u.isBandPhone());
							mUser.setBandEmail(u.isBandEmail());
							mUser.setBandQQ(u.isBandQQ());
							mUser.setBandWeibo(u.isBandWeibo());
							mUser.setBandWeixin(u.isBandWeixin());
							handler.sendEmptyMessage(0);
						}

					}
				});
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (mUser.isBandPhone()) {
					bandedPhone.setText(mUser.getPhone().equals("") ? mUser
							.getUserName() : mUser.getPhone());
				} else
					bandedPhone.setText(R.string.banded);
				if (mUser.isBandEmail())
					bandedEmail.setText(mUser.getEmail().equals("") ? mUser
							.getUserName() : mUser.getEmail());
				else {
					if (mUser.getEmail() != null || mUser.getEmail() != "") {
						bandedEmail.setText(R.string.band_wait_email);
					} else {
						bandedEmail.setText(R.string.banded);
					}
				}

				if (mUser.isBandWeibo()) {
					weibo.setChecked(true);
					weibo.setEnabled(false);
				} else
					weibo.setChecked(false);
				if (mUser.isBandWeixin()) {
					weixin.setChecked(true);
					weixin.setEnabled(false);
				} else
					weixin.setChecked(false);
				if (mUser.isBandQQ()) {
					qq.setChecked(true);
					qq.setEnabled(false);
				} else
					qq.setChecked(false);
				weibo.setOnCheckedChangeListener(BandActivity.this);
				weixin.setOnCheckedChangeListener(BandActivity.this);
				qq.setOnCheckedChangeListener(BandActivity.this);
				break;

			case 1:
				if (mUser.isBandWeibo()) {
					weibo.setChecked(true);
					weibo.setEnabled(false);
				} else
					weibo.setChecked(false);
				break;
			case 2:
				if (mUser.isBandWeixin()) {
					weixin.setChecked(true);
					weixin.setEnabled(false);
				} else
					weixin.setChecked(false);
				break;
			}

		}
	};

	private void initView() {
		weibo = (Switch) findViewById(R.id.weibo_switch);
		weixin = (Switch) findViewById(R.id.weixin_switch);
		qq = (Switch) findViewById(R.id.qq_switch);

		findViewById(R.id.band_back).setOnClickListener(this);
		findViewById(R.id.band_phone).setOnClickListener(this);
		findViewById(R.id.band_email).setOnClickListener(this);
		findViewById(R.id.band_address).setOnClickListener(this);

		bandedPhone = (TextView) findViewById(R.id.phone_banded);
		bandedEmail = (TextView) findViewById(R.id.email_banded);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.band_back) {
			finish();
		} else if (v.getId() == R.id.band_phone) {
			if (!mUser.isBandPhone()) {
				gotoBandDetail(BAND_PHONE);
			}
		} else if (v.getId() == R.id.band_email) {
			if (!mUser.isBandEmail()) {
				gotoBandDetail(BAND_EMAIL);
			}
		} else if (v.getId() == R.id.band_address) {
			gotoBandDetail(BAND_ADDRESS);
		}
	}

	private void gotoBandDetail(int type) {
		Intent i = new Intent(this, BandDetailActivity.class);
		i.putExtra("band_type", type);
		i.putExtra("band_user", mUser);
		startActivity(i);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			showLoadingDialog(true);
			if (buttonView.getId() == R.id.weibo_switch) {// 绑定微博
				doSinaBand();
			} else if (buttonView.getId() == R.id.weixin_switch) {// 绑定微信
				doWeixinlogin();
			} else if (buttonView.getId() == R.id.qq_switch) {// 绑定QQ

			}
		}
	}

	/**
	 * 微博授权
	 */
	private void doSinaBand() {
		// 新浪微博认证
		SinaAuth weiboAuth = new SinaAuth(this);
		if (!weiboAuth.checkIsOAuthed()) {
			weiboAuth.oAuth();
		} else {
			String sinaId = SinaAPI.getInstance(BandActivity.this).getSinaId();
			mUser.setSinaId(sinaId);
			doBand(sinaId, BandAccountOperate.WEIBO);
		}
		weiboAuth.setAuthListener(new UserModelAuthListener() {

			@Override
			public void onCallBack(boolean isSuccess) {
				String sinaId = SinaAPI.getInstance(BandActivity.this)
						.getSinaId();
				mUser.setSinaId(sinaId);
				doBand(sinaId, BandAccountOperate.WEIBO);
			}
		});
	}

	private void doWeixinlogin() {
		WeixinLoginUtil weixinloginUtil = WeixinLoginUtil.getInstance(this);
		weixinloginUtil.loginWithWeixin();
		weixinloginUtil.setLoginListener(new WeixinAuthListener() {

			@Override
			public void onCallBack(boolean isSuccess, User user) {
				if (user != null) {
					mUser.setWeixinId(user.getWeixinId());
					doBand(user.getWeixinId(), BandAccountOperate.WEIXIN);
				}
			}
		});
	}

	/**
	 * 绑定
	 * 
	 * @param c
	 *            username
	 * @param bindType
	 *            绑定类型
	 */
	protected void doBand(final String c, final int bindType) {
		if (mUser == null)
			return;
		mController.bandAccount(mUser.getUid(), mUser.getToken(), bindType, c,
				null, new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry == null || ((ErrorMsg) entry).getNo() == 0) {
							showToast(R.string.band_succeed);
							if (bindType == BandAccountOperate.WEIBO) {// 存储绑定信息
								mUser.setBandWeibo(true);
								handler.sendEmptyMessage(1);
							} else if (bindType == BandAccountOperate.WEIXIN) {
								mUser.setBandWeixin(true);
								handler.sendEmptyMessage(2);
							}
							SlateDataHelper.saveUserLoginInfo(
									BandActivity.this, mUser);

							showLoadingDialog(false);
						} else {
							if (bindType == BandAccountOperate.WEIBO)
								weibo.setChecked(false);
							else if (bindType == BandAccountOperate.WEIXIN)
								weixin.setChecked(false);
							ErrorMsg error = (ErrorMsg) entry;
							if (error.getNo() == 5002) {
								showToast(R.string.uniq_sina);
							} else
								showToast(error.getDesc());

							showLoadingDialog(false);
						}
					}
				});
	}

	@Override
	public String getActivityName() {
		return null;
	}

	@Override
	public Activity getActivity() {
		return BandActivity.this;
	}

}