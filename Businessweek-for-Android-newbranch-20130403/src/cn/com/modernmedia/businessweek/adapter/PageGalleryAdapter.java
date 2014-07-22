package cn.com.modernmedia.businessweek.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import cn.com.modernmedia.api.ImageDownloader;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.model.ArticleItem;

/**
 * gallery  ≈‰∆˜(PageGallery)
 * 
 * @author ZhuQiao
 * 
 */
public class PageGalleryAdapter extends BaseAdapter {
	private Context mContext;
	private ImageDownloader downloader = ImageDownloader.getInstance();
	private List<ArticleItem> list;

	public PageGalleryAdapter(Context context) {
		mContext = context;
	}

	public void setArticlelist(List<ArticleItem> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (list != null) {
			if (list.size() == 1)
				return 1;
		}
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int index = position % list.size();
		ArticleItem item = list.get(index);
		String url = "";
		if (item != null) {
			if (item.getAdv().getAdvProperty().getIsadv() == 1) {
				// π„∏Ê
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
		downloader.download(url, view);
		return view;
	}

}
