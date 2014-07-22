package cn.com.modernmedia.views.index;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.model.Cat;
import cn.com.modernmedia.model.Cat.CatItem;
import cn.com.modernmedia.model.SoloColumn;
import cn.com.modernmedia.model.SoloColumn.SoloColumnItem;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.adapter.IndexViewPagerAdapter;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.widget.VerticalFlowGallery;
import cn.com.modernmedia.widget.CircularViewPager;
import cn.com.modernmediaslate.model.Entry;

/**
 * 可滑动的首页
 * 
 * @author user
 * 
 */
public class IndexViewPager extends CircularViewPager<SoloColumnItem> implements
		NotifyArticleDesListener {
	private Context mContext;
	private List<SoloColumnItem> catItems = new ArrayList<SoloColumnItem>();// 栏目列表
	private Cat cat;// 用来做获取当前选中的栏目的位置使用;独立栏目用solocolumn
	private IndexViewPagerAdapter adapter;

	public IndexViewPager(Context context) {
		this(context, null);
	}

	public IndexViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		this.setOffscreenPageLimit(1);
		setListener(this);
	}

	/**
	 * 设置栏目信息
	 * 
	 * @param cat
	 */
	public void setCatList(Entry entry) {
		if (entry instanceof Cat) {
			catItems = ((Cat) entry).getSoloList();
			cat = (Cat) entry;
		} else if (entry instanceof SoloColumn) {
			catItems = ((SoloColumn) entry).getList();
		}
		setDataForPager(catItems);
	}

	/**
	 * 直接定位到某一个栏目
	 * 
	 * @param id
	 */
	public void checkPosition(int id) {
		if (!ParseUtil.listNotNull(catItems))
			return;
		int position = -1;
		for (int i = 0; i < catItems.size(); i++) {
			SoloColumnItem item = catItems.get(i);
			int catId = V.getSoloParentId(mContext, item);
			catId = catId == -1 ? item.getId() : catId;
			if (catId == id) {
				position = i;
				break;
			}
		}
		if (position == 0) {
			position = catItems.size() - 2;
		}
		position = position == -1 ? 1 : position;
		this.setCurrentItem(position, false);
	}

	@Override
	public MyPagerAdapter<SoloColumnItem> fetchAdapter(Context context,
			List<SoloColumnItem> list) {
		return adapter = new IndexViewPagerAdapter(mContext, cat, list);
	}

	@Override
	public void updateDes(int position) {
		if (ParseUtil.listNotNull(catItems) && catItems.size() > position) {
			CatItem item = catItems.get(position);
			V.setIndexTitle(mContext, item);
			((CommonMainActivity) mContext).notifyColumnAdapter(item.getId());
		}
	}

	@Override
	public void updatePage(int state) {
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (adapter != null && adapter.getSelfScrollViews() != null) {
			if (intercept(ev)) {
				return false;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	private boolean intercept(MotionEvent ev) {
		for (View view : adapter.getSelfScrollViews()) {
			if (view instanceof VerticalFlowGallery) {
				// TODO 图集
				return adjustAngle(ev);
			}
			Rect rect = new Rect();
			// 获取该gallery相对于全局的坐标点
			view.getGlobalVisibleRect(rect);
			if (rect.contains((int) ev.getX(), (int) ev.getY())) {
				return true;
			}
		}
		return false;
	}

	private float angleX, angleY;// 比较Y轴滑动角度，如果低于30，则执行横屏滑动
	private double angle;

	/**
	 * 滑动的角度是否大于最小值
	 * 
	 * @param ev
	 * @return
	 */
	private boolean adjustAngle(MotionEvent ev) {
		float y = ev.getRawY();
		float x = ev.getRawX();
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			// TODO 滑动时监听不到action_down
			angleX = x;
			angleY = y;
		} else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			angle = Math.atan(Math.abs(y - angleY) / Math.abs(x - angleX))
					/ Math.PI * 180;
			angleX = x;
			angleY = y;
			if (angle > 30) {
				return true;
			}
		}
		return false;
	}

}
