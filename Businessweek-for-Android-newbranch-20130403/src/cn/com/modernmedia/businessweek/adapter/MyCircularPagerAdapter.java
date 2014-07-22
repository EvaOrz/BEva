package cn.com.modernmedia.businessweek.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.businessweek.MyApplication;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.model.ArticleItem;

public class MyCircularPagerAdapter extends MyPagerAdapter {
	private Context mContext;

	public MyCircularPagerAdapter(Context context, List<ArticleItem> list) {
		super(context, list);
		mContext = context;
	}

	@Override
	public View fetchView(ArticleItem item) {
		String url = "";
		if (item != null) {
			if (item.getAdv().getAdvProperty().getIsadv() == 1) {
				// ¹ã¸æ
				url = item.getAdv().getColumnAdv().getUrl();
			}
			if (TextUtils.isEmpty(url)) {
				List<String> list = item.getPictureList();
				if (list != null && !list.isEmpty()) {
					url = list.get(0);
				}
			}
		}

		ImageView view = new ImageView(mContext);
		view.setLayoutParams(new Gallery.LayoutParams(
				android.widget.Gallery.LayoutParams.FILL_PARENT,
				android.widget.Gallery.LayoutParams.FILL_PARENT));
		view.setScaleType(ScaleType.CENTER);
		view.setBackgroundResource(R.drawable.placeholder);
		MyApplication.getImageDownloader().download(url, view);
		return view;
	}

}
