package cn.com.modernmedia.views.solo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.index.head.BaseIndexHeadView;
import cn.com.modernmedia.views.listener.ChildCatClickListener;
import cn.com.modernmedia.views.model.Template;
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

	protected int currentHeadSize;
	protected BaseIndexHeadView currentHeadView;
	private int currentPosition;
	protected TagInfoList childInfoList;
	private boolean hasInit = false;// 不用每个子栏目获得模板都刷新一下

	public BaseSoloIndexView(Context context) {
		mContext = context;
		ViewsApplication.catClickListener = this;
		BaseChildCatHead.selectPosition = -1;
	}

	protected void init(int limit) {
		view = LayoutInflater.from(mContext).inflate(R.layout.solo_view, null);
		viewPager = (AtlasViewPager) view.findViewById(R.id.solo_viewpager);
		frameLayout = (FrameLayout) view.findViewById(R.id.child_cat_frame);

		viewPager.setListener(this);
		viewPager.setOffscreenPageLimit(limit);
		if (mContext instanceof CommonMainActivity) {
			((CommonMainActivity) mContext).setScrollView(viewPager);
			((CommonMainActivity) mContext).setScrollView(frameLayout);
		}
	}

	/**
	 * 初始化配置信息
	 */
	public void initProperties(Template template, boolean isChild) {
		if (hasInit)
			return;
		hasInit = true;
		frameLayout.removeAllViews();
		catHead = new ChildCatHead(mContext, null, template);
		boolean hold = template.getCatHead().getCat_list_hold() == 1;
		if (!hold) {
			frameLayout.setVisibility(View.GONE);
		} else {
			if (hold)
				frameLayout.addView(catHead.fetchView(),
						new FrameLayout.LayoutParams(
								FrameLayout.LayoutParams.MATCH_PARENT,
								FrameLayout.LayoutParams.WRAP_CONTENT));
		}

		if (catHead != null)
			if (isChild)
				catHead.setChildValues(childInfoList, "");
			else
				catHead.setSoloValues(childInfoList);
	}

	public void setData(TagInfoList childInfoList) {
		this.childInfoList = childInfoList;
	}

	public void setIntercept(boolean intercept) {
		viewPager.setIntercept(intercept);
	}

	/**
	 * 获取焦点图
	 * 
	 * @return
	 */
	public BaseIndexHeadView getHeadView() {
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
	public void onClick(int position, TagInfo info) {
		if (viewPager != null && currentPosition != position)
			viewPager.setCurrentItem(position, false);
	}
}
