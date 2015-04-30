package cn.com.modernmedia.views.solo;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.index.IndexViewPagerItem;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 独立栏目/子栏目导航栏基类
 * 
 * @author user
 * 
 */
public class BaseChildCatHead {
	public static final int DURATION = 250;
	protected Context mContext;
	protected HorizontalScrollView view;
	protected IndexViewPagerItem indexViewPagerItem;
	protected ViewGroup frame;
	protected Template template = new Template();
	/**
	 * 由于可能是独立栏目，并且绑定在列表上，导致每次切换的时候都还原，所以设置成静态变量
	 */
	public static int selectPosition = -1;

	public BaseChildCatHead(Context context,
			IndexViewPagerItem indexViewPagerItem, Template template) {
		mContext = context;
		this.indexViewPagerItem = indexViewPagerItem;
		this.template = template;
	}

	/**
	 * 设置子栏目信息
	 * 
	 * @param parentId
	 */
	public void setChildValues(TagInfoList childInfoList, String currTagName) {
		initToolBar(childInfoList.getList(), currTagName);
	}

	/**
	 * 设置独立栏目信息
	 * 
	 * @param list
	 */
	public void setSoloValues(TagInfoList childInfoList) {
	}

	/**
	 * 建造toolbar
	 * 
	 * @param list
	 */
	protected void initToolBar(final List<TagInfo> list, final String currTag) {
		if (!ParseUtil.listNotNull(list))
			return;
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				setSelectedItemForChild(currTag);
			}
		}, DURATION);
		if (indexViewPagerItem != null && view != null) { // 定位到上次选中的位置
			view.post(new Runnable() {

				@Override
				public void run() {
					view.scrollTo(indexViewPagerItem.getCatHeadOffset(), 0);
				}
			});
		}
	}

	protected void clickItem(final TagInfo tagInfo, final int position) {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				click(tagInfo, position);
			}
		}, DURATION);
	}

	private void click(TagInfo tagInfo, int position) {
		if (mContext instanceof ViewsMainActivity) {
			((ViewsMainActivity) mContext).getScrollView().IndexClick();
			// 如果点击的是当前显示的cat,不重新 获取数据
			if (selectPosition == position) {
				return;
			}
			selectPosition = position;
			V.setIndexTitle(mContext, tagInfo);
			// TODO 独立栏目只走catClickListener
			if (indexViewPagerItem != null) {
				int offset = view == null ? 0 : view.getScrollX();
				indexViewPagerItem.fecthSpecificChildData(position, offset);
			} else if (ViewsApplication.catClickListener != null) {
				ViewsApplication.catClickListener.onClick(position, tagInfo);
			}
			setSelectedItemForChild(tagInfo.getTagName());
		}
	}

	/**
	 * 设置子栏目选中状态
	 * 
	 * @param tagInfo
	 */
	protected void setSelectedItemForChild(String currTagName) {
		if (frame == null || frame.getChildCount() == 0)
			return;
		for (int i = 0; i < frame.getChildCount(); i++) {
			View child = frame.getChildAt(i);
			if (TextUtils.isEmpty(currTagName)) {
				if (i == 0) {
					setStatusBySelect(child, true, 0);
				}
			} else if (child.getTag() instanceof TagInfo) {
				if (((TagInfo) child.getTag()).getTagName().equals(currTagName)) {
					setStatusBySelect(child, true, i);
					break;
				}
			}
		}
	}

	/**
	 * 设置独立栏目选中状态
	 * 
	 * @param soloColumnChild
	 */
	protected void setSelectedItemForSolo(TagInfo tagInfo) {
	}

	public void setView(HorizontalScrollView view) {
		this.view = view;
	}

	public View fetchView() {
		return view;
	}

	/**
	 * 设置选中状态
	 * 
	 * @param view
	 * @param select
	 * @param index
	 *            选中的位置
	 */
	public void setStatusBySelect(View view, boolean select, int index) {
	}

}
