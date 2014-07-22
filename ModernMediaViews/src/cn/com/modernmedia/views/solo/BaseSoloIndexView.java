package cn.com.modernmedia.views.solo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.model.SoloColumn.SoloColumnChild;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.index.IndexHeadView;
import cn.com.modernmedia.views.listener.ChildCatClickListener;
import cn.com.modernmedia.views.model.IndexListParm;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.widget.AtlasViewPager;

/**
 * 独立栏目、子栏目基础view
 * 
 * @author user
 * 
 */
public class BaseSoloIndexView implements NotifyArticleDesListener,
		ChildCatClickListener {
	private Context mContext;
	private View view;
	protected FrameLayout frameLayout;
	protected AtlasViewPager viewPager;

	protected BaseChildCatHead catHead;

	protected IndexListParm parm;
	protected int currentHeadSize;
	protected IndexHeadView currentHeadView;
	protected int parentId;
	private int currentPosition;

	public BaseSoloIndexView(Context context) {
		mContext = context;
		parm = ParseProperties.getInstance(mContext).parseIndexList();
		ViewsApplication.catClickListener = this;
		BaseChildCatHead.selectPosition = -1;
	}

	protected void init(int limit) {
		view = LayoutInflater.from(mContext).inflate(R.layout.solo_view, null);
		viewPager = (AtlasViewPager) view.findViewById(R.id.solo_viewpager);
		frameLayout = (FrameLayout) view.findViewById(R.id.child_cat_frame);

		boolean hold = parm.getCat_list_hold() == 1;
		if (!hold) {
			frameLayout.setVisibility(View.GONE);
			catHead = new BaseChildCatHead(mContext, null);
		} else {
			if (parm.getCat_list_type().equals(V.BUSINESS)) {
				catHead = new BusChildCatHead(mContext, null);
				if (hold)
					frameLayout.addView(catHead.fetchView());
				frameLayout.getLayoutParams().height = catHead.getHeight();
			} else if (parm.getCat_list_type().equals(V.IWEEKLY)) {
				catHead = new WeeklyChildCatHead(mContext, null);
				if (hold)
					frameLayout.addView(catHead.fetchView());
				frameLayout.getLayoutParams().height = catHead.getHeight();
			} else {
				catHead = new BaseChildCatHead(mContext, null);
				frameLayout.setVisibility(View.GONE);
			}
		}

		viewPager.setListener(this);
		viewPager.setOffscreenPageLimit(limit);
		if (mContext instanceof CommonMainActivity) {
			((CommonMainActivity) mContext).setScrollView(viewPager);
			((CommonMainActivity) mContext).setScrollView(frameLayout);
		}
	}

	public void setData(int parentId) {
		this.parentId = parentId;
		if (catHead instanceof BusChildCatHead) {
			((BusChildCatHead) catHead).frame.removeAllViews();
			((BusChildCatHead) catHead).tag.setVisibility(View.GONE);
		}
	}

	public void setIntercept(boolean intercept) {
		viewPager.setIntercept(intercept);
	}

	/**
	 * 获取焦点图
	 * 
	 * @return
	 */
	public IndexHeadView getHeadView() {
		return currentHeadView;
	}

	public BaseChildCatHead getCatHead() {
		return catHead;
	}

	/**
	 * 获取焦点图个数
	 * 
	 * @return
	 */
	public int getChildSize() {
		return currentHeadSize;
	}

	@Override
	public void updateDes(int position) {
		currentPosition = position;
	}

	@Override
	public void updatePage(int state) {
	}

	public View fetchView() {
		return view == null ? new View(mContext) : view;
	}

	@Override
	public void onClick(int position, int parentId, SoloColumnChild soloChild) {
		if (viewPager != null && currentPosition != position)
			viewPager.setCurrentItem(position, false);
	}
}
