package cn.com.modernmedia.views.article;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.model.AtlasParm;
import cn.com.modernmedia.widget.AtlasViewPager;

/**
 * iLady类型的图集
 * 
 * @author JianCong
 * 
 */
public class LadyAtlasView extends BaseAtlasView {

	public LadyAtlasView(Context context) {
		super(context);
	}

	@Override
	protected void init() {
		super.init();
		addView(LayoutInflater.from(mContext).inflate(R.layout.atlas_ilady,
				null), new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		title = (TextView) findViewById(R.id.ilady_atlas_title);
		desc = (TextView) findViewById(R.id.ilady_atlas_desc);
		initProcess();
		pager = (AtlasViewPager) findViewById(R.id.ilady_atlas_gallery);
		dotLl = (LinearLayout) findViewById(R.id.ilady_atlas_gallery_dot);
		dotLl.getBackground().setAlpha(204);
		// TODO 优家高为屏幕7/10
		pager.getLayoutParams().height = (int) (CommonApplication.height * AtlasParm.LADY_HEIGHT);
	}
}
