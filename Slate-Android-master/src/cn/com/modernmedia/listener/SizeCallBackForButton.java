package cn.com.modernmedia.listener;

import android.widget.Button;
import cn.com.modernmedia.widget.MainHorizontalScrollView;

public class SizeCallBackForButton implements SizeCallBack {
	private Button menu;
	private int menuWidth;

	public SizeCallBackForButton(Button menu) {
		super();
		this.menu = menu;
	}

	@Override
	public void onGlobalLayout() {
		this.menuWidth = this.menu.getMeasuredWidth()
				+ MainHorizontalScrollView.ENLARGE_WIDTH;
	}

	@Override
	public void getViewSize(int idx, int width, int height, int[] dims) {
		// TODO Auto-generated method stub
		dims[0] = width;
		dims[1] = height;

		/* ��ͼ�����м���ͼ */
		if (idx != 1) {
			dims[0] = width - this.menuWidth;
		}
	}

}
