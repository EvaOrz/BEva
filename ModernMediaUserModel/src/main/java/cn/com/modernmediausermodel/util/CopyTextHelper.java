package cn.com.modernmediausermodel.util;

import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import cn.com.modernmediausermodel.R;

/**
 * 辅助类，用于复制Edittext的文本，改动时还原成可复制状态
 * 
 * @author jiancong
 * 
 */
public class CopyTextHelper {
	private EditText mEditText;
	private TextView mTextView; // 显示复制状态（复制文字、已复制)
	private boolean isCopied = false;
	private Context mContext;

	public CopyTextHelper(Context context, EditText edit, TextView text) {
		this.mContext = context;
		this.mEditText = edit;
		this.mTextView = text;
		init();
	}

	private void init() {
		if (mEditText == null || mTextView == null) {
			throw new IllegalArgumentException("edittext or textview is null!");
		}
		mEditText.addTextChangedListener(new MyTextWatcher());
		mEditText.requestFocus();
		setTextVisible(mEditText.getText().toString());
		mTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 复制文字状态时才可以点击执行
				doCopy(true);
			}
		});
	}

	private void setTextVisible(CharSequence s) {
		int visibility = TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE;
		mTextView.setVisibility(visibility);
	}

	/**
	 * 是否复制文本到剪贴板,并显示相应的状态
	 * 
	 * @param copy
	 *            true：复制文本，显示状态变为已复制；false:不复制，显示状态为复制文字
	 */
	public void doCopy(boolean copy) {
		ClipboardManager board = (ClipboardManager) mContext
				.getSystemService(Context.CLIPBOARD_SERVICE);
		if (copy) { // 复制
			String content = mEditText.getText().toString();
			if (TextUtils.isEmpty(content)) {
				return;
			}
			board.setText(content);
			mEditText.selectAll();
			mTextView.setText(R.string.copy_text_success);
			mTextView.setTextColor(mContext.getResources().getColor(
					android.R.color.darker_gray));
			isCopied = true;
			mTextView.setClickable(false);
		} else {
			mTextView.setText(R.string.copy_text);
			mTextView.setTextColor(mContext.getResources().getColor(
					R.color.follow_all));
			mEditText.setSelection(0);
			isCopied = false;
			mTextView.setClickable(true);
		}

	}
	
	class MyTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			setTextVisible(s);
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (isCopied)
				doCopy(false);
		}

		@Override
		public void afterTextChanged(Editable s) {
			setTextVisible(mEditText.getText().toString());
		}
	}

}
