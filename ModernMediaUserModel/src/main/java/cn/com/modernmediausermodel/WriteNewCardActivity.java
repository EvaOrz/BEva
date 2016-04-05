package cn.com.modernmediausermodel;

import java.util.Calendar;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.util.sina.SinaAPI;
import cn.com.modernmedia.util.sina.SinaAuth;
import cn.com.modernmedia.util.sina.SinaRequestListener;
import cn.com.modernmedia.util.sina.UserModelAuthListener;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Card.CardItem;
import cn.com.modernmediausermodel.util.CopyTextHelper;
import cn.com.modernmediausermodel.util.UserCentManager;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 新建笔记页
 * 
 * @author zhuqiao
 * 
 */
public class WriteNewCardActivity extends SlateBaseActivity implements
		OnClickListener {
	public static final String KEY_FROM = "intent_from";
	public static final String KEY_DATA = "share_data";
	public static final String VALUE_SHARE = "share_data";

	private Context mContext;
	private Button cancelBtn, completeBtn;
	private ImageView shareBtn;
	private EditText contentEdit;
	private boolean isShareToWeibo = false;
	private SinaAuth weiboAuth;
	private SinaAPI sinaAPI;
	private String articleId; // 文章摘要分享时使用
	private TextView weiboText, copyText, copyArticleText;
	private CopyTextHelper mCopyHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_card);
		mContext = this;
		sinaAPI = SinaAPI.getInstance(this);
		init();
		initDataFromBundle(getIntent());

	}

	private void initDataFromBundle(Intent intent) {
		if (intent != null && intent.getExtras() != null) {
			if (VALUE_SHARE.equals(intent.getStringExtra(KEY_FROM))) {
				String content = intent.getStringExtra(KEY_DATA);
				if (!TextUtils.isEmpty(content)) {
					content = content.trim();
					if (content.contains("{:") && content.contains(":}")
							&& content.indexOf("{:") == 0) { // 分享内容为文章摘要
						articleId = content.substring(2, content.indexOf(":}"));
						content = content.replace("{:" + articleId + ":}", "");
					}
					contentEdit.setText(content);
					if (!TextUtils.isEmpty(articleId)) {
						showMessage(content);
					}
					// contentEdit.setSelection(content.length());
				}
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		initDataFromBundle(intent);
	}

	private void init() {
		cancelBtn = (Button) findViewById(R.id.write_card_cancel);
		completeBtn = (Button) findViewById(R.id.write_card_complete);
		contentEdit = (EditText) findViewById(R.id.write_card_content);
		shareBtn = (ImageView) findViewById(R.id.share_to_weibo);
		weiboText = (TextView) findViewById(R.id.write_card_text_share_weibo);
		copyText = (TextView) findViewById(R.id.write_card_copy);
		copyArticleText = (TextView) findViewById(R.id.write_card_copy_article);
		cancelBtn.setOnClickListener(this);
		completeBtn.setOnClickListener(this);
		shareBtn.setOnClickListener(this);
		// 分享到微博栏，当应用不支持微博时，隐藏
		if (SlateApplication.mConfig.getHas_sina() != 1) {
			// weiBoLayout.setVisibility(View.GONE);
			weiboText.setVisibility(View.GONE);
			shareBtn.setVisibility(View.GONE);
		} else {
			weiboAuth = new SinaAuth(this);
			User user = SlateDataHelper.getUserLoginInfo(this);
			if (user != null && !TextUtils.isEmpty(sinaAPI.getSinaId())) { // 微博绑定状态
				shareBtn.setImageResource(R.drawable.img_weibo_select);
				isShareToWeibo = true;
			}
			weiboAuth.setAuthListener(new UserModelAuthListener() {

				@Override
				public void onCallBack(boolean isSuccess) {
					if (isSuccess) {
						shareBtn.setImageResource(R.drawable.img_weibo_select);
						isShareToWeibo = true;
					} else {
						shareBtn.setImageResource(R.drawable.img_weibo_normal);
					}
				}
			});
		}
		mCopyHelper = new CopyTextHelper(this, contentEdit, copyText);
	}

	private void showMessage(String content) {
		ClipboardManager board = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		board.setText(contentEdit.getText().toString());
		contentEdit.selectAll();
		copyArticleText.setVisibility(View.VISIBLE);
		copyText.setVisibility(View.GONE);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				copyArticleText.setVisibility(View.GONE);
				copyText.setVisibility(View.VISIBLE);
				// 更新显示状态
				mCopyHelper.doCopy(true);
			}
		}, 2000);
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
				if (!weiboAuth.checkIsOAuthed()) {// 没认证
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
		item.setUid(Tools.getUid(this));
		item.setAppId(UserConstData.getInitialAppId());
		item.setTime(Calendar.getInstance().getTimeInMillis() / 1000 + "");
		item.setContents(contentEdit.getText().toString());
		item.setArticleId(ParseUtil.stoi(articleId));

		showLoadingDialog(true);
		UserOperateController.getInstance(this).addCard(item,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						showLoadingDialog(false);
						doAfterAddCard(entry);
					}
				});
	}

	private void shareToWeibo() {
		sinaAPI.sendText(contentEdit.getText().toString(),
				new SinaRequestListener() {

					@Override
					public void onSuccess(String entry) {
						showToast(R.string.Weibo_Share_Success);
					}

					@Override
					public void onFailed(String error) {
						showToast(R.string.Weibo_Share_Error);
					}
				});
	}

	private void doAfterAddCard(Entry entry) {
		if (entry != null && entry instanceof ErrorMsg) {
			ErrorMsg error = (ErrorMsg) entry;
			if (error.getNo() == 0) {
				setResult(RESULT_OK);
				if (isShareToWeibo) {
					shareToWeibo();
				}
				if (SlateApplication.mConfig.getHas_coin() == 1)
					UserCentManager.getInstance(mContext).addCardCoinCent();
				showToast(R.string.card_add_success);
				finish();
			} else {
				showToast(R.string.card_add_failed_toast);
				finish();
			}
		} else
			finish();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.down_out);
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
