package cn.com.modernmediausermodel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.com.modernmediausermodel.util.UserPageTransfer;

/**
 * Created by administrator on 2018/4/25.
 */

public class StartLoginReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("cn.com.modernmediausermodel.login")) {
            UserPageTransfer.gotoLoginActivityRequest(context, -1);
        }
    }
}
