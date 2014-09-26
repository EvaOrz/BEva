package cn.com.modernmedia.views.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.WeeklyLogEvent;
import cn.com.modernmedia.widget.LoadingImage;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * iweekly类型的焦点图适配器
 * 
 * @author user
 * 
 */
public class WeeklyCircularPagerAdapter extends MyPagerAdapter<ArticleItem> {
	private Context mContext;
	private String tag = "";
	private int width = 0, height = 0;

	public WeeklyCircularPagerAdapter(Context context, List<ArticleItem> list,
			String tag, int width, int height) {
		super(context, list);
		mContext = context;
		this.tag = tag;
		this.width = width;
		this.height = height;
	}

	@Override
	public View fetchView(ArticleItem item) {
		LoadingImage loadingImage = new LoadingImage(mContext, width, height);
		loadingImage.setTag(tag);
		if (item != null) {
			if (ParseUtil.listNotNull(item.getPicList())) {
				loadingImage.setUrl(item.getPicList().get(0));
			}
		}
		return loadingImage;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		WeeklyLogEvent.logAndroidColumnHeadviewShowCount();
		return super.instantiateItem(container, position);
	}

}
