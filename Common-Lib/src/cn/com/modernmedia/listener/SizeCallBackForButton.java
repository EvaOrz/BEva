package cn.com.modernmedia.listener;

import android.view.View;

public class SizeCallBackForButton implements SizeCallBack {
	private View menu;
	private int menuWidth;

	public SizeCallBackForButton(View menu) {
		super();
		this.menu = menu;
	}

	@Override
	public void onGlobalLayout(int enlarge) {
		this.menuWidth = this.menu.getMeasuredWidth() + enlarge;
	}

	@Override
	public void getViewSize(int idx, int width, int height, int[] dims) {
		// TODO Auto-generated method stub
		dims[0] = width;
		dims[1] = height;

		/* 视图不是中间视图 */
		if (idx != 1) {
			dims[0] = width - this.menuWidth;
		}
	}

}
