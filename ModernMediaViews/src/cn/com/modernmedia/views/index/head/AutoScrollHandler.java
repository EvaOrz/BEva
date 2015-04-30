package cn.com.modernmedia.views.index.head;

import android.os.Handler;
import android.os.Message;
import cn.com.modernmedia.views.ViewsApplication;

public abstract class AutoScrollHandler extends Handler {
	private static final int CHANGE = 1;
	private static final int CHANGE_DELAY = 5000;// test

	private boolean isAuto;

	public AutoScrollHandler() {
		super();
		ViewsApplication.autoScrollObservable.addHandler(this);
		isAuto = true;
	}

	@Override
	public void handleMessage(Message msg) {
		if (msg.what == CHANGE) {
			next();
			startChange();
		}
	}

	public void startChange() {
		removeCallbacksAndMessages(null);
		if (isAuto)
			sendEmptyMessageDelayed(CHANGE, CHANGE_DELAY);
	}

	public void stopChange() {
		removeCallbacksAndMessages(null);
		isAuto = false;
	}

	public abstract void next();
}
