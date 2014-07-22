package cn.com.modernmedia.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

/**
 * 一次滑动一张图片的gallery
 * 
 * @author ZhuQiao
 * 
 */
public class PageGallery extends Gallery {
	private boolean isScroll = false;// 图片是否在滑动

	public PageGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public boolean isScroll() {
		return isScroll;
	}

	public void setScroll(boolean isScroll) {
		this.isScroll = isScroll;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1 == null || e2 == null) {
			return super.onFling(e1, e2, velocityX, velocityY);
		}
		float x = e1.getX();
		float y = e2.getX();
		int kEvent;
		if (y >= x) {
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {
			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		return onKeyDown(kEvent, new KeyEvent(e2.getAction(), kEvent));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		isScroll = true;
		if (event.getAction() == MotionEvent.ACTION_CANCEL)
			isScroll = false;
		return super.onTouchEvent(event);
	}

}
