package cn.com.modernmediausermodel.widget;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.UserInfoActivity;

/**
 * 修改签名dialog
 * 
 * @author Eva.
 * 
 */
public class SignDialog implements OnClickListener {
	private Context mContext;
	private Dialog mDialog;
	private Window window;
	private EditText edit;
	private TextView title;
	private int type;// 1：昵称；2：签名

	public SignDialog(Context context, int type) {
		this.mContext = context;
		this.type = type;
		init();
	}

	private void init() {
		mDialog = new Dialog(mContext, R.style.CustomDialog);
		mDialog.show();
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);// 屏蔽dialog外焦点，弹出键盘
		window = mDialog.getWindow();
		window.setContentView(R.layout.dialog_motify_sign);

		mDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		edit = (EditText) window.findViewById(R.id.motify_sign_edit);
		title = (TextView) window.findViewById(R.id.motify_sign_title);
		if (type == 1) {
			title.setText(R.string.motify_nickname);
			edit.setHint(R.string.userinfo_nickname);
		} else if (type == 2) {
			title.setText(R.string.motify_sign);
			edit.setHint(R.string.motify_sign);
		}
		window.findViewById(R.id.motify_sign_sure).setOnClickListener(this);
		window.findViewById(R.id.motify_sign_cancle).setOnClickListener(this);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				showKeyboard();
			}
		}, 200);
	}

	/**
	 * 显示软键盘
	 */
	private void showKeyboard() {
		if (edit != null) {
			// 设置可获得焦点
			edit.setFocusable(true);
			edit.setFocusableInTouchMode(true);
			// 请求获得焦点
			edit.requestFocus();
			// 调用系统输入法
			InputMethodManager inputManager = (InputMethodManager) edit
					.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.showSoftInput(edit, 0);
		}
	}

	@Override
	public void onClick(View v) {
		String desc = edit.getEditableText().toString();
		if (v.getId() == R.id.motify_sign_sure) {
			if (!TextUtils.isEmpty(desc)
					&& mContext instanceof UserInfoActivity) {
				if (type == 1) // 昵称
					SlateDataHelper.setNickname(mContext, desc);
				else if (type == 2)
					SlateDataHelper.setDesc(mContext, desc);
				((UserInfoActivity) mContext).modifyUserInfo(
						SlateDataHelper.getUserLoginInfo(mContext), "", "");

			}
		}
		mDialog.cancel();
	}
}
