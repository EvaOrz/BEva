package cn.com.modernmedia.views.index;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.model.IndexListParm;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.widget.ImageCircularViewPager;

/**
 * 商周类型的焦点图
 * 
 * @author user
 * 
 */
public class BusHeadView extends IndexHeadView {

	public BusHeadView(Context context, IndexListParm parm) {
		super(context, parm);
	}

	@Override
	protected void init() {
		addView(LayoutInflater.from(mContext).inflate(R.layout.index_head_bus,
				null));
		viewPager = (ImageCircularViewPager) findViewById(R.id.bus_index_gallery);
		dotLl = (LinearLayout) findViewById(R.id.bus_index_gallery_dot);
		title = (TextView) findViewById(R.id.bus_index_head_title);
		View bar = (RelativeLayout) findViewById(R.id.bus_index_head_bar);
		V.setImage(bar, parm.getHead_title_bg());
		bar.setVisibility(parm.getHead_show_title() == 1 ? View.VISIBLE
				: View.GONE);
	}
}
