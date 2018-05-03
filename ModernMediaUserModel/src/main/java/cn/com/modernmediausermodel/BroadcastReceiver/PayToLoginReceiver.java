package cn.com.modernmediausermodel.BroadcastReceiver;

import cn.com.modernmediausermodel.LoginActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PayToLoginReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(
				"cn.com.modernmediausermodel.LoginActivity")) {
			Intent i = new Intent(context, LoginActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}

}
