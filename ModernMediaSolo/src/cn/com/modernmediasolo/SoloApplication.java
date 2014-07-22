package cn.com.modernmediasolo;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmediasolo.db.SoloArticleListDb;
import cn.com.modernmediasolo.db.SoloDb;
import cn.com.modernmediasolo.db.SoloFocusDb;

public class SoloApplication extends CommonApplication {

	@Override
	public void onCreate() {
		super.onCreate();
		hasSolo = true;
	}

	public static void exit() {
		SoloDb.getInstance(mContext).close();
		SoloArticleListDb.getInstance(mContext).close();
		SoloFocusDb.getInstance(mContext).close();
	}
}
