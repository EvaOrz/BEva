package cn.com.modernmedia.views.index.head;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.views.ViewsApplication;

public abstract class AutoScrollHandler extends Handler {
    private static final int CHANGE = 1;
    private static final int SHOW_HEAD = 2;
    private static final int UNSHOW_HEAD = 3;

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
        if (msg.what == CHANGE) {
            if (isAuto) {
                if (DataHelper.getIndexHeadAutoLoop(mContext)) next();
                startChange();
            }
        } else if (msg.what == SHOW_HEAD) {

            setShowHead();
        } else if (msg.what == UNSHOW_HEAD) {
            setunShowHead();
        }
    }

    public synchronized void startChange() {
        removeCallbacksAndMessages(null);
        isAuto = true;
        if (isAuto) {
            sendEmptyMessageDelayed(CHANGE, CHANGE_DELAY);
        }

    }

    public synchronized void advShow() {
        sendEmptyMessage(SHOW_HEAD);
    }
    public synchronized void stopChange() {
        removeCallbacksAndMessages(null);
        isAuto = false;
    }
    public synchronized void advUnShow() {
        sendEmptyMessage(UNSHOW_HEAD);
    }


    public abstract void setShowHead();

    public abstract void setunShowHead();

    public boolean isAuto() {
        return isAuto;
    }

    public abstract void next();
}
