package cn.com.modernmedia.views.index;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.mainprocess.SoloProcessHelper;
import cn.com.modernmedia.model.Cat;
import cn.com.modernmedia.model.Cat.CatItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.IndexArticle;
import cn.com.modernmedia.model.SoloColumn.SoloColumnItem;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.adapter.IndexViewPagerAdapter;
import cn.com.modernmedia.views.model.IndexListParm;
import cn.com.modernmedia.views.solo.BaseChildCatHead;
import cn.com.modernmedia.views.solo.BusChildCatHead;
import cn.com.modernmedia.views.solo.SoloIndexView;
import cn.com.modernmedia.views.solo.WeeklyChildCatHead;
import cn.com.modernmedia.views.util.MyLocation;
import cn.com.modernmedia.views.util.MyLocation.FetchLocationListener;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.widget.VerticalGalleryView;
import cn.com.modernmedia.widget.RedProcess;
import cn.com.modernmediaslate.model.Entry;

/**
 * 可滑动的首页view item
 * 
 * @author user
 * 
 */
public class IndexViewPagerItem {
	private Context mContext;
	private RelativeLayout view;
	private FrameLayout headFrame, frame;
	private IndexListView indexListView;// 普通首页
	private SoloIndexView soloIndexView;// 独立栏目
	private VerticalGalleryView galleryView;// 图集

	private IndexListParm parm;
	private SoloColumnItem item;
	private Cat cat;
	private Handler handler = new Handler();
	private IndexViewPagerAdapter adapter;
	private View layout, loading, error;
	private SoloProcessHelper helper;
	private boolean hasSetData;

	public IndexViewPagerItem(Context context, Cat cat, SoloColumnItem item,
			IndexViewPagerAdapter adapter) {
		mContext = context;
		this.adapter = adapter;
		this.item = item;
		this.cat = cat;
		hasSetData = false;
		parm = ParseProperties.getInstance(mContext).parseIndexList();
		BaseChildCatHead.selectPosition = -1;
		init();
	}

	private void init() {
		if ((cat == null && CommonApplication.soloColumn == null)
				|| item == null)
			return;
		view = (RelativeLayout) LayoutInflater.from(mContext).inflate(
				R.layout.index_view_pager_item, null);
		view.setTag(this);
		// test 先写死，只要iweekly画报是黑色,其他都是白色背景
		// V.setImage(view, parm.getList_bg());
		if (parm.getProcess_type().equals(V.IWEEKLY)) {
			layout = view.findViewById(R.id.pager_item_layout_weekly);
			loading = view.findViewById(R.id.pager_item_loading_weekly);
			layout.setVisibility(View.VISIBLE);
		} else {
			layout = view.findViewById(R.id.pager_item_layout);
			loading = view.findViewById(R.id.pager_item_loading);
			error = view.findViewById(R.id.pager_item_error);
		}
		headFrame = (FrameLayout) view.findViewById(R.id.pager_item_head_frame);
		frame = (FrameLayout) view.findViewById(R.id.pager_item_frame);
		if (V.getSoloParentId(mContext, item) != -1) {
			// TODO 独立栏目
			soloIndexView = new SoloIndexView(mContext, this);
			frame.addView(soloIndexView.fetchView());
			soloIndexView.setIntercept(true);
		} else {
			if (ParseUtil.mapContainsKey(DataHelper.childMap,
					item.getParentId())
					&& parm.getCat_list_hold() == 1) {
				// TODO 子栏目导航栏固定在顶部
				BaseChildCatHead childHead = null;
				if (parm.getCat_list_type().equals(V.BUSINESS)) {
					childHead = new BusChildCatHead(mContext, this);
				} else if (parm.getCat_list_type().equals(V.IWEEKLY)) {
					childHead = new WeeklyChildCatHead(mContext, this);
				}
				if (childHead != null) {
					childHead.setChildValues(item.getParentId());
					headFrame.addView(childHead.fetchView(), new LayoutParams(
							LayoutParams.FILL_PARENT, childHead.getHeight()));
				}
			}
		}
		if (error != null)
			error.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					reLoad();
				}
			});
		showLoading();
	}

	public void fetchData() {
		if (hasSetData)
			return;
		final int catId = V.getSoloParentId(mContext, item);
		if (catId != -1) {
			// TODO 独立栏目
			getSoloIndex(catId);
		} else {
			reLoad();
		}
	}

	/**
	 * 获取首页
	 */
	private void getIndex() {
		if (adapter.getEntryFromMap(0) != null) {
			setValueForIndex(adapter.getEntryFromMap(0));
			return;
		}
		showLoading();
		OperateController.getInstance(mContext).getIndex(
				CommonApplication.issue, new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof IndexArticle) {
							setValueForIndex(entry);
						} else {
							showError();
						}
					}
				});
	}

	/**
	 * 获取栏目首页
	 */
	private void getCatIndex(final int catId) {
		if ((cat == null && CommonApplication.soloColumn == null)
				|| item == null)
			return;
		if (adapter.getEntryFromMap(catId) != null) {
			setValueForIndex(adapter.getEntryFromMap(catId));
			return;
		}
		showLoading();
		OperateController.getInstance(mContext).getCartIndex(
				CommonApplication.issue, catId + "",
				ModernMediaTools.getCatPosition(catId + "", cat),
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof CatIndexArticle) {
							setValueForIndex(entry);
						} else {
							showError();
						}
					}
				});
	}

	/**
	 * 设置列表/图集数据
	 * 
	 * @param entry
	 */
	private void setValueForIndex(Entry entry) {
		frame.removeAllViews();
		disProcess();
		if (entry instanceof Entry) {
			if (entry.getTemplate().equals("phone-gallery")) {
				// TODO 图集
				view.setBackgroundColor(Color.BLACK);
				galleryView = new VerticalGalleryView(mContext, this);
				frame.addView(galleryView.getGallery());
				galleryView.setData(entry);
			} else {
				// TODO 列表
				int parentId = item.getParentId();
				indexListView = new IndexListView(mContext, parentId, this);
				frame.addView(indexListView.fetchView());
				indexListView.setData(entry);
			}
		}
		if (entry instanceof IndexArticle) {
			adapter.addEntryToMap(0, entry);
			hasSetData = true;
		} else if (entry instanceof CatIndexArticle) {
			adapter.addEntryToMap(((CatIndexArticle) entry).getId(), entry);
			hasSetData = true;
		}
	}

	/**
	 * 获取独立栏目首页
	 * 
	 * @param catId
	 */
	private void getSoloIndex(final int catId) {
		if (soloIndexView == null)
			return;
		helper = new SoloProcessHelper(mContext) {

			@Override
			public void fetchSoloData(String from, String to) {
				Entry entry = adapter.getEntryFromMap(catId);
				if (entry instanceof CatIndexArticle) {
					doAfterFetchData((CatIndexArticle) entry, true, false);
					return;
				}
				showLoading();
				if (from != null && !from.equals("0") && to != null
						&& !to.equals("0")) {
					handler.post(new Runnable() {

						@Override
						public void run() {
							if (getArticleListFromDb()) {
								disProcess();
							} else {
								showError();
							}
						}
					});
				} else {
					getSoloArticleList(catId, false, "0", "0", true);
				}
			}

			@Override
			protected void doAfterFetchData(CatIndexArticle catIndex,
					boolean fromDb, boolean newData) {
				super.doAfterFetchData(catIndex, fromDb, newData);
				if (catIndex instanceof CatIndexArticle) {
					adapter.addEntryToMap(catId, catIndex);
					hasSetData = true;
				}
				disProcess();
			}

			@Override
			protected void fecthDataError(boolean fromDb, boolean newData,
					boolean isPull) {
				super.fecthDataError(fromDb, newData, isPull);
				if (!isPull) {
					showError();
				}
			}

			@Override
			public void showSoloChildCat(int parentId) {
				soloIndexView.setData(parentId);
			}

		};
		helper.showSoloChildCat(catId, false);
	}

	private void reLoad() {
		int catId = V.getSoloParentId(mContext, item);
		if (catId != -1) {
			getSoloIndex(catId);
		} else {
			if (DataHelper.childMap.containsKey(item.getParentId())) {
				// TODO 子栏目
				showCatIndexView();
			} else {
				if (cat != null) {
					if (item.getId() == 0) {
						getIndex();
					} else {
						getCatIndex(item.getId());
					}
				}
			}
		}
	}

	/**
	 * 显示栏目view
	 */
	private void showCatIndexView() {
		if (hasSetData)
			return;
		if (ParseUtil.mapContainsKey(DataHelper.childMap, item.getId())) {
			final List<CatItem> list = DataHelper.childMap.get(item.getId());
			if (ParseUtil.listNotNull(list)) {
				int savedCatId = DataHelper.getChildId(mContext, item.getId()
						+ "");
				if (savedCatId != -1) {
					boolean contain = false;
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getId() == savedCatId) {
							contain = true;
							getChildIndex(list.get(i), i);
							break;
						}
					}
					if (!contain)
						getChildIndex(list.get(0), 0);
				} else if (item.getId() == 195 && ConstData.getAppId() == 20) {
					MyLocation location = new MyLocation(mContext);
					location.setmListener(new FetchLocationListener() {

						@Override
						public void fetchCoordinate(double longitude,
								double latitude) {
						}

						@Override
						public void fetchAddress(String address) {
							boolean contain = false;
							if (!TextUtils.isEmpty(address)) {
								for (int i = 0; i < list.size(); i++) {
									CatItem cat = list.get(0);
									if (address.contains(cat.getTagname())) {
										contain = true;
										getChildIndex(cat, i);
										break;
									}
								}
							}
							if (!contain)
								getChildIndex(list.get(0), 0);
						}
					});
					location.startGetLocation();
				} else {
					getChildIndex(list.get(0), 0);
				}
			}
		} else {
			getCatIndex(item.getId());
		}
	}

	private void getChildIndex(CatItem catItem, int selectPosition) {
		getCatIndex(catItem.getId());
		DataHelper.setChildId(mContext, catItem.getParentId(), catItem.getId());
	}

	public List<View> getHeadView() {
		List<View> list = new ArrayList<View>();
		list.add(headFrame);
		if (indexListView != null) {
			list.addAll(indexListView.getSelfScrollViews());
		} else if (soloIndexView != null
				&& soloIndexView.getCurrentCatItem() != null) {
			// TODO 还需要在自己的viewpager里给外部的首页pager赋值
			list.addAll(soloIndexView.getCurrentCatItem().getSelfScrollViews());
		} else if (galleryView != null) {
			list.add(galleryView.getGallery());
		}
		return list;
	}

	/**
	 * 显示loading图标
	 */
	public void showLoading() {
		layout.setVisibility(View.VISIBLE);
		loading.setVisibility(View.VISIBLE);
		if (loading instanceof RedProcess)
			((RedProcess) loading).start();
		if (error != null)
			error.setVisibility(View.GONE);
	}

	/**
	 * 显示错误提示
	 */
	public void showError() {
		layout.setVisibility(View.VISIBLE);
		if (error != null) {
			loading.setVisibility(View.GONE);
			error.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 隐藏process_layout
	 */
	public void disProcess() {
		layout.setVisibility(View.GONE);
		loading.setVisibility(View.GONE);
		if (loading instanceof RedProcess)
			((RedProcess) loading).stop();
		if (error != null)
			error.setVisibility(View.GONE);
	}

	public SoloProcessHelper getHelper() {
		return helper;
	}

	public void destory() {
		hasSetData = false;
	}

	public View fetchView() {
		return view;
	}

	public void setHasSetData(boolean hasSetData) {
		this.hasSetData = hasSetData;
	}

}