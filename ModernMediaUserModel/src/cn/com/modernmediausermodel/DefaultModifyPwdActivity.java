package cn.com.modernmediausermodel;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.api.UserModelInterface;
import cn.com.modernmediausermodel.listener.RequestListener;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.model.User.Error;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 默认修改密码页面
 * 
 * @author ZhuQiao
 * 
 */
public class DefaultModifyPwdActivity extends BaseActivity implements
		OnClickListener {
	private Context mContext;
	private EditText mOldPwdEdit;
	private EditText mNewPwdEdit;
	private ImageView mClearImage;
	private ImageView mForgetImage;
	private ImageView mCloseImage;
	private Button mSureBtn;
	private UserModelInterface mUserInterface;
	private Animation shakeAnim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(-1);
	}

	@Override
	public void setContentView(int layoutResID) {
		if (layoutResID == -1) {
			layoutResID = R.layout.modify_pwd_activity;
			super.setContentView(layoutResID);
			init();
		} else {
			super.setContentView(layoutResID);
		}
	}

	private void init() {
		shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
		mUserInterface = UserModelInterface.getInstance(mContext);
		mOldPwdEdit = (EditText) findViewById(R.id.modify_pwd_old_edit);
		mNewPwdEdit = (EditText) findViewById(R.id.modify_pwd_new_edit);
		mClearImage = (ImageView) findViewById(R.id.modify_pwd_img_clear);
		mForgetImage = (ImageView) findViewById(R.id.modify_pwd_img_forget);
		mCloseImage = (ImageView) findViewById(R.id.modify_pwd_close);
		mSureBtn = (Button) findViewById(R.id.modify_sure);

		mClearImage.setOnClickListener(this);
		mForgetImage.setOnClickListener(this);
		mCloseImage.setOnClickListener(this);
		mSureBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO switch报错？？
		int id = v.getId();
		if (id == R.id.modify_pwd_img_clear) {
			doClear();
		} else if (id == R.id.modify_pwd_img_forget) {
			User user = UserDataHelper.getUserLoginInfo(mContext);
			if (user != null && !TextUtils.isEmpty(user.getUserName())) {
				doForgetPwd(user.getUserName());
			}
		} else if (id == R.id.modify_pwd_close) {
			doClose();
		} else if (id == R.id.modify_sure) {
			if (mOldPwdEdit != null && mNewPwdEdit != null) {
				String old = mOldPwdEdit.getText().toString();
				String ne = mNewPwdEdit.getText().toString();
				if (UserTools.checkString(old, mOldPwdEdit, shakeAnim)
						&& UserTools.checkString(ne, mNewPwdEdit, shakeAnim))
					doModifyPwd(old, ne);
			}
		}
	}

	/**
	 * 清除旧密码
	 */
	protected void doClear() {
		if (mOldPwdEdit != null)
			mOldPwdEdit.setText("");
	}

	/**
	 * 忘记密码
	 */
	protected void doForgetPwd(final String userName) {
		if (UserTools.checkIsEmail(mContext, userName)) {
			showLoadingDialog(true);
			mUserInterface.getPassword(userName, new RequestListener() {

				@Override
				public void onSuccess(Entry entry) {
					showLoadingDialog(false);
					String msg = String.format(getString(
							R.string.msg_find_pwd_success, userName));
					UserTools.showDialogMsg(mContext,
							getString(R.string.reset_password), msg);
				}

				@Override
				public void onFailed(Entry error) {
					showLoadingDialog(false);
					if (error instanceof User.Error)
						showToast(((Error) error).getDesc());
				}
			});
		}
	}

	/**
	 * 返回
	 */
	protected void doClose() {
		finish();
	}

	/**
	 * 修改密码
	 */
	protected void doModifyPwd(String oldPwd, String newPassword) {
		User user = UserDataHelper.getUserLoginInfo(mContext);
		if (UserTools.checkPasswordFormat(mContext, newPassword)
				&& user != null) {
			showLoadingDialog(true);
			mUserInterface.modifyPassword(user.getUid(), user.getToken(),
					user.getUserName(), oldPwd, newPassword,
					new RequestListener() {

						@Override
						public void onSuccess(Entry entry) {
							showLoadingDialog(false);
							User resUser = (User) entry;
							// 修改成功
							afterModifySuccess(resUser);
						}

						@Override
						public void onFailed(Entry error) {
							showLoadingDialog(false);
							if (error instanceof User.Error)
								showToast(((Error) error).getDesc());
						}
					});
		}
	}

	protected void afterModifySuccess(User user) {
		showToast(R.string.msg_modify_success);
		finish();
	}

	@Override
	public void reLoadData() {
	}

	@Override
	public String getActivityName() {
		return DefaultModifyPwdActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}
}
