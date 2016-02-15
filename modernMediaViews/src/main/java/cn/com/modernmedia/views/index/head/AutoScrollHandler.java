package cn.com.modernmedia.views.index.head;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.views.ViewsApplication;

public abstract class AutoScrollHandler extends Handler {
	private static final int CHANGE = 1;
	private static final int CHANGE_DELAY = 5000;
	private Context mContext;

	private boolean isAuto;

	public AutoScrollHandler(Context context) {
		super();
		this.mContext = context;
		ViewsApplication.autoScrollObservable.addHandler(this);
		isAuto = true;
	}

	@Override
	public void handleMessage(Message msg) {
		if (msg.what == CHANGE && isAuto) {
			if (DataHelper.getIndexHeadAutoLoop(mContext))
				next();
			startChange();
		}
	}

	public synchronized void startChange() {
		removeCallbacksAndMessages(null);
		isAuto = true;
		if (isAuto) {
			sendEmptyMessageDelayed(CHANGE, CHANGE_DELAY);
		}
	}

	public synchronized void stopChange() {
		removeCallbacksAndMessages(null);
		isAuto = false;
	}

	public boolean isAuto() {
		return isAuto;
	}

	public abstract void next();
}
