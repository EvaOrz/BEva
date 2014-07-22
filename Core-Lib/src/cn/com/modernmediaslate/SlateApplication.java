package cn.com.modernmediaslate;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class SlateApplication extends Application {
	public static int width;
	public static int height;
	public static float density;
	public static Map<String, Activity> activityMap = new HashMap<String, Activity>();

	@Override
	public void onCreate() {
		super.onCreate();
		initScreenInfo();
	}

	/**
	 * 初始化页面信息
	 */
	public void initScreenInfo() {
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;

		// 获取屏幕密度
		density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
	}

	public static void addActivity(String name, Activity activity) {
		if (TextUtils.isEmpty(name) || activity == null)
			return;
		if (activityMap.containsKey(name)) {
			if (activityMap.get(name) != null)
				activityMap.get(name).finish();
			activityMap.remove(name);
		}
		activityMap.put(name, activity);
	}

	public static void removeActivity(String name) {
		if (TextUtils.isEmpty(name))
			return;
		if (!activityMap.isEmpty() && activityMap.containsKey(name)) {
			activityMap.remove(name);
		}
	}

	public static void slateExit() {
		if (!activityMap.isEmpty()) {
			for (String key : activityMap.keySet()) {
				Activity activity = activityMap.get(key);
				if (activity != null && !activity.isFinishing()) {
					activity.finish();
				}
			}
		}
		activityMap.clear();
	}
}
