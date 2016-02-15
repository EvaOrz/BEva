package cn.com.modernmediausermodel;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.MultiComment.CommentItem;
import cn.com.modernmediausermodel.util.CopyTextHelper;

public class WriteCommentActivity extends SlateBaseActivity implements
		OnClickListener {
	public static final String RETURN_DATA = "comment";
	public static final String KEY_CARD_ID = "card_id";
	public static final String KEY_IS_SHOW_TOAST = "is_show_toast";

	private Button cancelBtn, completeBtn;
	private EditText contentEdit;
	private String cardId;
	private boolean isShowToast = true;
	private TextView copyText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_comment);
		initDataFromBundle();
		init();
	}

	private void initDataFromBundle() {
		if (getIntent().getExtras() != null) {
			this.cardId = getIntent().getExtras().getString(KEY_CARD_ID);
			this.isShowToast = getIntent().getExtras().getBoolean(
					KEY_IS_SHOW_TOAST);
		}
	}

	private void init() {
		cancelBtn = (Button) findViewById(R.id.write_comment_cancel);
		completeBtn = (Button) findViewById(R.id.write_comment_complete);
		contentEdit = (EditText) findViewById(R.id.write_comment_content);
		copyText = (TextView) findViewById(R.id.write_comment_copy);
		cancelBtn.setOnClickListener(this);
		completeBtn.setOnClickListener(WriteCommentActivity.this);
		contentEdit.requestFocus();

		new CopyTextHelper(this, contentEdit, copyText);
	}

	@Override
	public String getActivityName() {
		return WriteCommentActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.write_comment_cancel) {
			setResult(RESULT_CANCELED);
			finish();
		} else if (v.getId() == R.id.write_comment_complete) {

			if (!TextUtils.isEmpty(contentEdit.getText().toString())) {
				addCardComment();
			} else {
				showToast(getString(R.string.comment_not_allow_null_toast));
			}
		}
	}

	public void addCardComment() {
		// 创建属于当前卡片的一个评论对象
		final CommentItem item = new CommentItem();
		item.setContent(contentEdit.getText().toString());
		item.setUid(Tools.getUid(this));
		item.setCardId(cardId);
		item.setTime(Calendar.getInstance().getTimeInMillis() / 1000 + "");
		showLoadingDialog(true);
		UserOperateController.getInstance(this).addCardComment(item,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						showLoadingDialog(false);
						if (entry instanceof ErrorMsg) {
							ErrorMsg error = (ErrorMsg) entry;
							if (error.getNo() == 0) {
								// 发表成功
								if (isShowToast) {
									showToast(R.string.write_success);
								}
								Intent intent = new Intent();
								intent.putExtra(RETURN_DATA, item);
								setResult(RESULT_OK, intent);
								finish();
							} else {
								// 发表失败
								if (isShowToast) {
									showToast(R.string.write_failed);
								}
							}
						}
					}
				});
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.down_out);
	}
}
