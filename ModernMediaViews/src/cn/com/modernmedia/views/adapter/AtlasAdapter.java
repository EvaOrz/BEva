package cn.com.modernmedia.views.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.adapter.MyPagerAdapter.OnItemClickListener;
import cn.com.modernmedia.model.Atlas.AtlasPicture;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.model.AtlasParm;
import cn.com.modernmedia.views.util.V;

/**
 * 图集适配器
 * 
 * @author ZhuQiao
 * 
 */
public class AtlasAdapter extends PagerAdapter {
	private Context mContext;
	private LayoutInflater inflater;
	private List<AtlasPicture> list = new ArrayList<AtlasPicture>();
	private Map<String, View> map = new HashMap<String, View>();
	private OnItemClickListener onItemClickListener;
	private AtlasParm parm;

	public AtlasAdapter(Context context, AtlasParm parm) {
		mContext = context;
		this.parm = parm;
		inflater = LayoutInflater.from(mContext);
	}

	public void setData(List<AtlasPicture> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
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
	public Object instantiateItem(ViewGroup container, final int position) {
		AtlasPicture picture = list.get(position);
		View view;
		String url = picture.getUrl();
		if (map.containsKey(url)) {
			view = map.get(url);
		} else {
			view = inflater.inflate(R.layout.atlas_item, null);
			ImageView imageView = (ImageView) view
					.findViewById(R.id.atlas_picture);
			initImage(imageView);
			imageView.setScaleType(ScaleType.CENTER);
			CommonApplication.finalBitmap.display(imageView, url);
			map.put(url, view);
			container.addView(view);
		}
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onItemClickListener != null)
					onItemClickListener.onItemClick(v, position);
			}
		});
		return view;
	}

	private void initImage(ImageView imageView) {
		if (parm == null)
			return;
		V.setImage(imageView, parm.getPlaceholder());
		if (parm.getType().equals(V.ILADY) && parm.getHeight() > 0) {
			// TODO 优家类型，高=3*parent/4,宽等比算
			int height = (int) (CommonApplication.height * AtlasParm.LADY_HEIGHT);
			imageView.getLayoutParams().width = height * parm.getWidth()
					/ parm.getHeight();
		}
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		String url = list.get(position).getUrl();
		if (ParseUtil.mapContainsKey(map, url)) {
			map.remove(url);
			container.removeView((View) object);
			CommonApplication.callGc();
		}
	}

}
