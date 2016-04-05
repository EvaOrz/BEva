package cn.com.modernmediausermodel.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout.LayoutParams;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.UserInfoActivity;
import cn.com.modernmediausermodel.api.BandAccountOperate;

/**
 * 验证邮箱dialog
 * 
 * @author Eva.
 * 
 */
public class ValEmailDialog implements OnClickListener {
	private Context mContext;
	private Dialog mDialog;
	private Window window;
	private String email;

	public ValEmailDialog(Context context, String email) {
		this.mContext = context;
		this.email = email;
		init();
	}

	private void init() {
		mDialog = new Dialog(mContext, R.style.CustomDialog);
		mDialog.show();
		
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.setCancelable(true);
		window = mDialog.getWindow();
		window.setContentView(R.layout.dialog_val_email);

		mDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		
		window.findViewById(R.id.send_email_again).setOnClickListener(this);
		window.findViewById(R.id.change_email).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.send_email_again) {
			((UserInfoActivity) mContext).doBand(email,
					BandAccountOperate.EMAIL);
		} else if (v.getId() == R.id.change_email) {// 更改email
			((UserInfoActivity) mContext)
					.gotoBandDetail(BandAccountOperate.EMAIL);
		}
		mDialog.cancel();
	}
}
