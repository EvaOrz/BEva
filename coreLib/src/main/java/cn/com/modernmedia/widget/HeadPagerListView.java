package cn.com.modernmedia.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 
 * listview的headview是可以左右滑动的，当左右滑动时，拦截了listview本身的上下滑动
 * 
 * @author ZhuQiao
 * 
 */
public class HeadPagerListView extends ListView {
	private List<View> scrollViewList;
	private boolean check_angle;// 是否需要判断角度

	public HeadPagerListView(Context context) {
		this(context, null);
	}

	public HeadPagerListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		scrollViewList = new ArrayList<View>();
	}

	public void setScrollView(View scrollView) {
		if (scrollViewList == null)
			scrollViewList = new ArrayList<View>();
		scrollViewList.add(scrollView);
	}

	/**
	 * 给listview设置参数
	 * 
	 * @param dividerNull
	 *            是否设置divider
	 * @param footerDividersEnabled
	 *            是否给listview设置footerview divider
	 * @param headerDividersEnabled
	 *            是否给listview设置headview divider
	 */
	public void setParam(boolean dividerNull, boolean footerDividersEnabled,
			boolean headerDividersEnabled) {
		if (dividerNull) {
			setDivider(null);
		}
		setCacheColorHint(Color.TRANSPARENT);
		setFadingEdgeLength(0);
		setFooterDividersEnabled(footerDividersEnabled);
		setHeaderDividersEnabled(headerDividersEnabled);
	}

	public void setCheck_angle(boolean check_angle) {
		this.check_angle = check_angle;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (check_angle) {
			if (adjustAngle(ev)) {
				return super.onInterceptTouchEvent(ev);
			}
			if (checkScroll(ev) && ev.getAction() == MotionEvent.ACTION_MOVE) {
				return false;
			}
		} else if (checkScroll(ev))
			return false;
		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * 检测是否在子滑动view里
	 * 
	 * @param ev
	 * @return
	 */
	private boolean checkScroll(MotionEvent ev) {
		boolean inChild = false;
		if (ParseUtil.listNotNull(scrollViewList)) {
			for (View scrollView : scrollViewList) {
				Rect rect = new Rect();
				scrollView.getGlobalVisibleRect(rect);
				if (rect.contains((int) ev.getX(), (int) ev.getY())) {
					inChild = true;
					break;
				}
			}
		}
		return inChild;
	}

	private float angleX, angleY;
	private double angle;
	private static final double MIN_ANGLE = 30;

	/**
	 * 滑动的角度是否大于最小值
	 * 
	 * @param ev
	 * @return
	 */
	private boolean adjustAngle(MotionEvent ev) {
		float y = ev.getRawY();
		float x = ev.getRawX();
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			angleX = x;
			angleY = y;
		} else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			angle = Math.atan(Math.abs(x - angleX) / Math.abs(y - angleY))
					/ Math.PI * 180;
			angleX = x;
			angleY = y;
			if (angle < MIN_ANGLE) {
				return true;
			}
		}
		return false;
	}
}
