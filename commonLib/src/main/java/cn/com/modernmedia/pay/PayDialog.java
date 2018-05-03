package cn.com.modernmedia.pay;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout.LayoutParams;
import cn.com.modernmedia.R;
import cn.com.modernmediaslate.unit.SlateDataHelper;

/**
 * 微信支付宝付款dialog
 * 
 * @author lusiyuan
 *
 */
public class PayDialog implements OnClickListener {
	private Context mContext;
	private AlertDialog mDialog;
	private Window window;
	private String subject, detail;

	public PayDialog(Context context, String subject, String detail) {
		this.mContext = context;
		this.subject = subject;
		this.detail = detail;
		init();
	}

	private void init() {
		mDialog = new AlertDialog.Builder(mContext).create();
		mDialog.show();
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(true);
		window = mDialog.getWindow();
		window.setContentView(R.layout.pay_dialog);

		mDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		window.findViewById(R.id.pay_close).setOnClickListener(this);
		window.findViewById(R.id.pay_for_weixin).setOnClickListener(this);
		window.findViewById(R.id.pay_for_zhifubao).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.pay_for_weixin) {
			((PayActivity) mContext).weixinPay(subject, detail);
			// Toast.makeText(mContext, "暂不支持微信支付。", Toast.LENGTH_SHORT).show();
		} else if (v.getId() == R.id.pay_for_zhifubao) {
			// 3.1.2要求在商品描述上添加用户Uid
			((PayActivity) mContext).zhifubaoPay(subject, detail + "_"
					+ SlateDataHelper.getUid(mContext));
		}
		// else if (v.getId() == R.id.pay_close) {
		mDialog.dismiss();
	}

}
