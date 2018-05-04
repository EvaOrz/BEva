package cn.com.modernmedia.views.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.adapter.MyPagerAdapter.OnItemClickListener;
import cn.com.modernmedia.model.ArticleItem.PhonePageList;
import cn.com.modernmedia.views.model.TemplateAtlas;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 图集适配器
 * 
 * @author ZhuQiao
 * 
 */
public class AtlasAdapter extends PagerAdapter {
	private Context mContext;
	private List<PhonePageList> list = new ArrayList<PhonePageList>();
	private Map<String, View> map = new HashMap<String, View>();
	private OnItemClickListener onItemClickListener;
	private TemplateAtlas template;
	private int curr;

	public AtlasAdapter(Context context, TemplateAtlas template) {
		mContext = context;
		this.template = template;
	}

	public void setData(List<PhonePageList> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public int getCurr() {
		return curr;
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
		PhonePageList picture = list.get(position);
		View view;
		String url = picture.getUrl();
		if (map.containsKey(url)) {
			view = map.get(url);
		} else {
			view = fetchView(picture);
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

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		String url = list.get(position).getUrl();
		if (ParseUtil.mapContainsKey(map, url)) {
			map.remove(url);
			container.removeView((View) object);
			CommonApplication.callGc();
		}
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		curr = position;
	}

	private View fetchView(PhonePageList item) {
		if (template == null)
			return new View(mContext);
		XMLParse xmlParse = new XMLParse(mContext, null);
		View view = xmlParse.inflate(template.getPagerItem().getData(), null,
				"");
		xmlParse.setDataForAtlasItem(item);
		return view;
	}
}
