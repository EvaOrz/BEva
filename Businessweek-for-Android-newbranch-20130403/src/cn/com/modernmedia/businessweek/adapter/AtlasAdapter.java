package cn.com.modernmedia.businessweek.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import cn.com.modernmedia.api.ImageDownloader;
import cn.com.modernmedia.businessweek.MyApplication;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.model.Atlas.AtlasPicture;

/**
 * ÕººØ  ≈‰∆˜
 * 
 * @author ZhuQiao
 * 
 */
public class AtlasAdapter extends PagerAdapter {
	private Context mContext;
	private LayoutInflater inflater;
	private List<AtlasPicture> list = new ArrayList<AtlasPicture>();
	private Map<String, View> map = new HashMap<String, View>();
	private ImageDownloader downloader = MyApplication.getImageDownloader();

	public AtlasAdapter(Context context) {
		mContext = context;
		inflater = LayoutInflater.from(mContext);
	}

	public void setData(List<AtlasPicture> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		AtlasPicture picture = list.get(position);
		View view;
		String url = picture.getUrl();
		if (map.containsKey(url)) {
			view = map.get(url);
		} else {
			view = inflater.inflate(R.layout.atlas_picture, null);
			ImageView imageView = (ImageView) view
					.findViewById(R.id.atlas_picture);
			imageView.setBackgroundResource(R.drawable.placeholder);
			downloader.download(url, imageView);
			map.put(url, view);
			container.addView(view);
		}
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		String url = list.get(position).getUrl();
		if (!map.isEmpty() && map.containsKey(url)) {
			map.remove(url);
			container.removeView((View) object);
			System.gc();
			Runtime.getRuntime().gc();
		}
	}

}
