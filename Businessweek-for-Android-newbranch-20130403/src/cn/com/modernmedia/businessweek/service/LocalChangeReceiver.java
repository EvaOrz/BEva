package cn.com.modernmedia.businessweek.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.com.modernmedia.businessweek.MyApplication;

/**
 * ¼àÌý¸Ä±älocal
 * 
 * @author ZhuQiao
 * 
 */
public class LocalChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
			if (!MyApplication.activityMap.isEmpty()) {
				MyApplication.getLocalLanguage();
				MyApplication.exit();
			}
		}
	}

}
