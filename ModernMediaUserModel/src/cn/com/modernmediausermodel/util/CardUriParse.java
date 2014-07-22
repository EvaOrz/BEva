package cn.com.modernmediausermodel.util;

import android.content.Context;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.listener.CardUriListener;

/**
 * 卡片uri解析
 * 
 * @author user
 * 
 */
public class CardUriParse {
	public static final String COMMENTS = "comments";

	private CardUriListener listener = new CardUriListener() {

		@Override
		public void doCardUri(Context context, String link) {
			parse(context, link);
		}
	};

	private static CardUriParse instance = null;

	private CardUriParse() {
		CommonApplication.userUriListener = listener;
	}

	public static CardUriParse getInstance() {
		if (instance == null)
			instance = new CardUriParse();
		return instance;
	}

	/**
	 * 解析
	 * 
	 * @param link
	 */
	private void parse(Context context, String link) {
		String arr[] = link.split("/");
		if (arr == null || arr.length < 2)
			return;
		if (arr[0].equals(COMMENTS)) {
			// 打开某个卡片详情
			UserPageTransfer.gotoCardDetailActivity(context, arr[1], false);
		}
	}
}
