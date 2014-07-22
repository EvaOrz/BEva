package cn.com.modernmediausermodel;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Card.CardItem;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserConstData;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserTools;
import cn.com.modernmediausermodel.util.sina.SinaAPI;
import cn.com.modernmediausermodel.util.sina.SinaAuth;
import cn.com.modernmediausermodel.util.sina.SinaRequestListener;
import cn.com.modernmediausermodel.util.sina.UserModelAuthListener;

public class WriteNewCardActivity extends BaseActivity implements
		OnClickListener {
	public final static String KEY_FROM = "intent_from";
	public final static String KEY_DATA = "share_data";
	public final static String VALUE_SHARE = "share_data";

	private Button cancelBtn, completeBtn;
	private ImageView shareBtn;
	private EditText contentEdit;
	private boolean isShareToWeibo = false;
	private SinaAuth weiboAuth;
	private SinaAPI sinaAPI;
	private boolean isFromShare = false;// 是否分享进来的
	private int appId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_card);
		init();
		initDataFromBundle();
		sinaAPI = SinaAPI.getInstance(this);
	}

	private void initDataFromBundle() {
		if (getIntent() != null && getIntent().getExtras() != null) {
			if (VALUE_SHARE.equals(getIntent().getStringExtra(KEY_FROM))) {
				isFromShare = true;
				String content = getIntent().getStringExtra(KEY_DATA);
				if (!TextUtils.isEmpty(content)) {
					content = content.trim();
					contentEdit.setText(content);
					contentEdit.setSelection(content.length());
				}
				appId = getIntent().getExtras().getInt(ConstData.SHARE_APP_ID);
			}
		}
	}

	private void init() {
		cancelBtn = (Button) findViewById(R.id.write_card_cancel);
		completeBtn = (Button) findViewById(R.id.write_card_complete);
		contentEdit = (EditText) findViewById(R.id.write_card_content);
		shareBtn = (ImageView) findViewById(R.id.share_to_weibo);
		cancelBtn.setOnClickListener(this);
		completeBtn.setOnClickListener(this);
		shareBtn.setOnClickListener(this);
		contentEdit.requestFocus();
		weiboAuth = new SinaAuth(this);
		User user = UserDataHelper.getUserLoginInfo(this);
		if (user != null && !TextUtils.isEmpty(user.getSinaId())) { // 微博登录时同步微博
			shareBtn.setImageResource(R.drawable.img_weibo_select);
			isShareToWeibo = true;
		}
		weiboAuth.setAuthListener(new UserModelAuthListener() {

			@Override
			public void onCallBack(boolean isSuccess) {
				if (isSuccess) {
					shareBtn.setImageResource(R.drawable.img_weibo_select);
				} else {
					shareBtn.setImageResource(R.drawable.img_weibo_normal);
				}
			}
		});
	}

	@Override
	public void reLoadData() {
	}

	@Override
	public String getActivityName() {
		return WriteNewCardActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.write_card_cancel) {
			setResult(RESULT_CANCELED);
			finish();
		} else if (v.getId() == R.id.write_card_complete) {
			if (!TextUtils.isEmpty(contentEdit.getText().toString())) {
				addCard();
			} else {
				showToast(R.string.card_not_allow_null_toast);
			}
		} else if (v.getId() == R.id.share_to_weibo) {
			if (isShareToWeibo) {
				shareBtn.setImageResource(R.drawable.img_weibo_normal);
			} else {
				if (!weiboAuth.checkIsOAuthed()) {
					weiboAuth.oAuth();
				} else {
					shareBtn.setImageResource(R.drawable.img_weibo_select);
				}
			}
			isShareToWeibo = !isShareToWeibo;
		}
	}

	private void addCard() {
		CardItem item = new CardItem();
		item.setUid(UserTools.getUid(this));
		item.setAppId(UserConstData.getInitialAppId());
		item.setTime(Calendar.getInstance().getTimeInMillis() + "");
		item.setContents(contentEdit.getText().toString());

		showLoadingDialog(true);
		UserOperateController.getInstance(this).addCard(item,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						showLoadingDialog(false);
						if (entry instanceof User.Error) {
							User.Error error = (User.Error) entry;
							if (error.getNo() == 0) {
								setResult(RESULT_OK);
								if (isShareToWeibo) {
									shareToWeibo();
								}
								finish();
							} else {
								showToast(getString(R.string.card_add_failed_toast));
								finish();
							}
						}
					}
				});
	}

	private void shareToWeibo() {
		sinaAPI.sendText(contentEdit.getText().toString(),
				new SinaRequestListener() {

					@Override
					public void onSuccess(Entry entry) {
						showToast(R.string.Weibo_Share_Success);
					}

					@Override
					public void onFailed(String error) {
						showToast(R.string.Weibo_Share_Error);
					}
				});
	}

	@Override
	public void finish() {
		super.finish();
		if (isFromShare && appId != ConstData.getInitialAppId()) {
			// TODO 如果是第三方应用分享，清空所有页面
			CommonApplication.exit();
		}
		if (!isFromShare) {
			overridePendingTransition(R.anim.hold, R.anim.down_out);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
