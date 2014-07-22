package cn.com.modernmedia.views.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.IndexArticle;
import cn.com.modernmedia.model.IndexArticle.Today;
import cn.com.modernmedia.model.Weather;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.adapter.BaseIndexAdapter;
import cn.com.modernmedia.views.model.IndexListParm;
import cn.com.modernmedia.views.solo.BaseChildCatHead;
import cn.com.modernmedia.views.solo.BusChildCatHead;
import cn.com.modernmedia.views.solo.WeeklyChildCatHead;
import cn.com.modernmedia.views.util.CityLocation;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.widget.PullToRefreshListView;
import cn.com.modernmediaslate.model.Entry;

/**
 * 首页列表
 * 
 * @author user
 * 
 */
public class IndexListView implements FetchEntryListener {
	private Context mContext;
	private PullToRefreshListView listView;
	private BaseIndexAdapter adapter;// 列表适配器
	private IndexHeadView headView;// 焦点图
	private BaseChildCatHead childHead;// 栏目导航栏
	private IndexListParm parm;
	private Entry entry;
	private CityLocation cityLocation;

	private List<View> selfScrollViews = new ArrayList<View>();

	public IndexListView(Context context) {
		this(context, -1, null);
	}

	public IndexListView(Context context, int parentId,
			IndexViewPagerItem indexViewPagerItem) {
		mContext = context;
		init(parentId, indexViewPagerItem);
	}

	private void init(int parentId, IndexViewPagerItem indexViewPagerItem) {
		cityLocation = new CityLocation(mContext);
		listView = new PullToRefreshListView(mContext);
		listView.setParam(true, true, true);
		listView.enableAutoFetch(false, false);
		listView.setCheck_angle(true);
		initProperties(parentId, indexViewPagerItem);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				position = position - listView.getHeaderViewsCount();
				if (adapter != null && adapter.getCount() > position) {
					ArticleItem item = adapter.getItem(position);
					V.clickSlate(mContext, item, ArticleType.Default);
					LogHelper.logOpenArticleFromColumnPage(mContext,
							item.getArticleId() + "", item.getCatId() + "");
					if (entry instanceof IndexArticle) {
						findCatPosition(item.getCatId(), item.getArticleId(),
								(IndexArticle) entry);
					}
					AdvTools.requestClick(item);
				}
			}
		});
	}

	/**
	 * 初始化配置信息
	 */
	private void initProperties(int parentId,
			IndexViewPagerItem indexViewPagerItem) {
		parm = ParseProperties.getInstance(mContext).parseIndexList();
		if (((ViewsMainActivity) mContext).getDefaultHeadView() != null)
			listView.addHeaderView(((ViewsMainActivity) mContext)
					.getDefaultHeadView());
		// 栏目导航栏
		if (ParseUtil.mapContainsKey(DataHelper.childMap, parentId)
				&& parm.getCat_list_hold() == 0) {
			if (parm.getCat_list_type().equals(V.BUSINESS)) {
				childHead = new BusChildCatHead(mContext, indexViewPagerItem);
			} else if (parm.getCat_list_type().equals(V.IWEEKLY)) {
				childHead = new WeeklyChildCatHead(mContext, indexViewPagerItem);
			}
		}
		if (childHead != null) {
			childHead.setChildValues(parentId);
			listView.addHeaderView(childHead.fetchView());
			listView.setScrollView(childHead.fetchView());
			selfScrollViews.add(childHead.fetchView());
		}
		// 焦点图
		headView = V.getIndexHeadView(mContext, parm);
		if (headView != null) {
			listView.addHeaderView(headView);
			listView.setScrollView(headView);
			selfScrollViews.add(headView);
		}
		// list adpter
		adapter = V.getIndexAdapter(mContext, parm);
		listView.setAdapter(adapter);
	}

	@Override
	public void setData(Entry entry) {
		this.entry = entry;
		if (entry instanceof IndexArticle) {
			IndexArticle indexArticle = (IndexArticle) entry;
			if (indexArticle.hasData(parm.getHead_position())) {
				showHead(entry);
			} else {
				dissHead();
			}
			if (ConstData.getAppId() == 1)
				setValuesForBusIndex(indexArticle);
			else
				setValueForIndex(indexArticle);
			LogHelper.logAndroidShowHighlights();
			listView.setCatId(0);
			impressAdv(indexArticle.getImpressionUrlList());
		} else if (entry instanceof CatIndexArticle) {
			CatIndexArticle catIndexArticle = (CatIndexArticle) entry;
			if (catIndexArticle.hasData(parm.getHead_position())) {
				showHead(entry);
			} else {
				dissHead();
			}
			setValueForCatIndex(catIndexArticle);
			LogHelper.logAndroidShowColumn(mContext, catIndexArticle.getId()
					+ "");
			listView.setCatId(catIndexArticle.getId());
			impressAdv(catIndexArticle.getImpressionUrlList());

			if (headView instanceof WeeklyHeadView) {
				((WeeklyHeadView) headView).getWeatherReLayout().setVisibility(
						View.GONE);
				int city = cityLocation.getCityByTemplate(entry.getTemplate());
				if (city != -1) {
					getWeather(city);
				}
			}
		}
	}

	/**
	 * 显示焦点图
	 * 
	 * @param entry
	 */
	private void showHead(Entry entry) {
		if (headView != null) {
			headView.setPadding(0, 0, 0, 0);
			headView.invalidate();
			headView.setData(entry);
			if (!headView.isShouldScroll()
					&& selfScrollViews.contains(headView)) {
				selfScrollViews.remove(headView);
			}
		}
	}

	/**
	 * 隐藏焦点图
	 */
	private void dissHead() {
		if (headView != null) {
			headView.setPadding(0, -parm.getHead_height()
					* CommonApplication.width / 720, 0, 0);
			headView.invalidate();
		}
	}

	/**
	 * 商周首页赋值
	 * 
	 * @param indexArticle
	 */
	private void setValuesForBusIndex(IndexArticle indexArticle) {
		List<Today> todayList = indexArticle.getTodayList();
		adapter.clear();
		if (ParseUtil.listNotNull(todayList)) {
			for (Today today : todayList) {
				if (today == null)
					continue;
				List<ArticleItem> itemList = today.getArticleItemList();
				if (ParseUtil.listNotNull(itemList)) {
					adapter.setData(itemList);
				}
			}
			listView.setSelection(0);
		}
	}

	/**
	 * 首页赋值
	 * 
	 * @param indexArticle
	 */
	private void setValueForIndex(IndexArticle indexArticle) {
		List<ArticleItem> list = new ArrayList<ArticleItem>();
		if (TextUtils.isEmpty(parm.getHead_type())) {
			// TODO 没有焦点图
			for (int key : indexArticle.getMap().keySet()) {
				list.addAll(indexArticle.getMap().get(key));
			}
		} else {
			list = indexArticle.getMap().get(parm.getItem_position());
		}
		adapter.clear();
		if (ParseUtil.listNotNull(list)) {
			adapter.setData(list);
			listView.setSelection(0);
		}
	}

	/**
	 * 栏目首页赋值
	 * 
	 * @param indexArticle
	 */
	private void setValueForCatIndex(CatIndexArticle indexArticle) {
		List<ArticleItem> list = new ArrayList<ArticleItem>();
		if (TextUtils.isEmpty(parm.getHead_type())) {
			// TODO 没有焦点图
			for (int key : indexArticle.getMap().keySet()) {
				list.addAll(indexArticle.getMap().get(key));
			}
		} else {
			list = indexArticle.getMap().get(parm.getItem_position());
		}
		adapter.clear();
		if (ParseUtil.listNotNull(list)) {
			adapter.setData(list);
			listView.setSelection(0);
		}
	}

	/**
	 * 统计广告
	 * 
	 * @param impressionUrlList
	 */
	private void impressAdv(List<String> impressionUrlList) {
		if (ParseUtil.listNotNull(impressionUrlList)) {
			for (String str : impressionUrlList) {
				AdvTools.requestImpression(str);
			}
		}
	}

	public View fetchView() {
		return listView;
	}

	/**
	 * 商周统计
	 * 
	 * @param catId
	 * @param articleId
	 * @param indexArticle
	 */
	private void findCatPosition(int catId, int articleId,
			IndexArticle indexArticle) {
		if (ConstData.getInitialAppId() != 1)
			return;
		int section = -1, row = -1;
		List<Today> todatList = indexArticle.getTodayList();
		List<ArticleItem> itemList = null;
		for (int i = 0; i < todatList.size(); i++) {
			Today t = todatList.get(i);
			if (t.getTodayCatId() == catId) {
				section = i;
				itemList = t.getArticleItemList();
				break;
			}
		}
		if (ParseUtil.listNotNull(itemList)) {
			for (int i = 0; i < itemList.size(); i++) {
				if (itemList.get(i).getArticleId() == articleId) {
					row = i;
					break;
				}
			}
		}
		if (section != -1 && row != -1) {
			LogHelper.logAndroidTouchHighlightsTable(section, row);
		}
	}

	public IndexHeadView getHeadView() {
		return headView;
	}

	public List<View> getSelfScrollViews() {
		return selfScrollViews;
	}

	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, Weather> map = new HashMap<Integer, Weather>();// 天气预报

	private void getWeather(final int city) {
		if (!(headView instanceof WeeklyHeadView))
			return;
		if (map.containsKey(city)) {
			((WeeklyHeadView) headView).setValueForWeather(map.get(city));
			return;
		}
		float[] res = cityLocation.getCityLocation(city);
		OperateController.getInstance(mContext).getWeather(res[1], res[0],
				new FetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						if (entry instanceof Weather) {
							map.put(city, (Weather) entry);
							((WeeklyHeadView) headView)
									.setValueForWeather((Weather) entry);
						}
					}
				});
	}
}
