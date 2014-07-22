package cn.com.modernmedia.views.article;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.widget.AtlasViewPager;

/**
 * 商周类型的图集
 * 
 * @author JianCong
 * 
 */
public class BusAtlasView extends BaseAtlasView {

	public BusAtlasView(Context context) {
		super(context);
	}

	@Override
	protected void init() {
		super.init();
		addView(LayoutInflater.from(mContext).inflate(R.layout.atlas_bus, null),
				new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.FILL_PARENT));
		title = (TextView) findViewById(R.id.bus_atlas_title);
		desc = (TextView) findViewById(R.id.bus_atlas_desc);
		initProcess();
		pager = (AtlasViewPager) findViewById(R.id.bus_atlas_gallery);
		dotLl = (LinearLayout) findViewById(R.id.bus_atlas_gallery_dot);
		int height = atlasParm.getHeight() == 0 ? 526 : atlasParm.getHeight();
		pager.getLayoutParams().height = CommonApplication.width * height / 720;
	}

}
