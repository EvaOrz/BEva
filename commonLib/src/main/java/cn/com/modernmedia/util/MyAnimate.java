package cn.com.modernmedia.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * 动画
 * 
 * @author ZhuQiao
 * 
 */
public class MyAnimate {
	/**
	 * 位移动画
	 * 
	 * @param view
	 */
	public static void startTranslateAnimation(View view, int fromX, int toX) {
		TranslateAnimation ta = new TranslateAnimation(Animation.ABSOLUTE,
				fromX, Animation.ABSOLUTE, toX, Animation.ABSOLUTE, 0,
				Animation.ABSOLUTE, 0);
		ta.setDuration(250);
		ta.setFillAfter(true);
		view.startAnimation(ta);
	}
}
