package cn.com.modernmedia.api;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.ParseUtil;

@SuppressLint("UseSparseArrays")
/**
 * 在解析json的时候，把当前位置上的广告添加进去(广告的位置是相对于本来正常数据的位置，即排除广告后的顺序)
 * @author user
 *
 */
public abstract class BaseIndexAdvOperate extends BaseOperate {
	/**
	 * 当前解析到的焦点图的位置
	 */
	protected int currentPosition1 = 0;
	/**
	 * 当前解析到的列表图的位置
	 */
	protected int currentPosition2 = 0;
	protected List<String> impressionUrlList = new ArrayList<String>();
	/**
	 * 焦点图广告 key:sort // *
	 * 广告位置排序，如果是带*号的，在解析JSON的时候先排,如果是具体的数字，那么按数字大小排序，最后在解析JSON完成之后 // *
	 * ；即先按*添加，数字正序排到最后
	 * 
	 * // * @cause
	 * 如果先排具体的位置，再添加带*的，插入带*的就会把原来的顺序打乱了，导致原来排的具体位置出现偏移，所以等排完带*的内容之后统一排
	 */
	protected TreeMap<String, List<ArticleItem>> position1Map = new TreeMap<String, List<ArticleItem>>(
			new Comparator<String>() {

				/**
				 * 
				 * @param o1
				 *            比较值(后面的数)
				 * @param o2
				 *            被比较值(前面的数)
				 * @return
				 */
				@Override
				public int compare(String o1, String o2) {
					if (o1.contains("*") && o2.contains("*")) {
						return o1.compareTo(o2);
					}
					if (o1.contains("*")) {
						return -1;
					} else if (o2.contains("*")) {
						return 1;
					}
					// 升序
					return ParseUtil.stoi(o1) - (ParseUtil.stoi(o2));
				}
			});
	/**
	 * 列表广告 key:sort
	 */
	protected TreeMap<String, List<ArticleItem>> position2Map = new TreeMap<String, List<ArticleItem>>(
			new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					if (o1.contains("*") && o2.contains("*")) {
						return o1.compareTo(o2);
					}
					if (o1.contains("*")) {
						return -1;
					} else if (o2.contains("*")) {
						return 1;
					}
					// 升序
					return ParseUtil.stoi(o1) - (ParseUtil.stoi(o2));
				}
			});

	/**
	 * 列表广告 key:section
	 */
	protected TreeMap<Integer, List<AdvItem>> bbIndexAdvMap = new TreeMap<Integer, List<AdvItem>>(
			new Comparator<Integer>() {

				@Override
				public int compare(Integer o1, Integer o2) {
					// 升序
					return o1 - o2;
				}
			});

	/**
	 * 获得需要的广告
	 * 
	 * @param issueId
	 *            当前文章所在的期
	 * @param catId
	 *            当前栏目
	 * @param position
	 *            当前栏目再catList中所属的位置
	 */
	protected void initAdv(String issueId, String catId, int position) {
		// TODO 添加广告
		if (CommonApplication.advList == null)
			return;
		Map<Integer, List<AdvItem>> advMap = CommonApplication.advList
				.getAdvMap();
		if (!ParseUtil.mapContainsKey(advMap, AdvList.IN_CAT))
			return;
		List<AdvItem> list = advMap.get(AdvList.IN_CAT);
		if (!ParseUtil.listNotNull(list))
			return;
		for (AdvItem item : list) {
			if (item == null)
				continue;
			// TODO 比较issueId是否符合
			if (!AdvTools.containThisIssue(item, issueId))
				continue;
			// TODO 比较catId是否符合
			if (!AdvTools.containThisCat(position, item, catId))
				continue;
			String sort = item.getSort();
			if (TextUtils.isEmpty(sort)) {
				return;
			} else if (!sort.contains("*") && ParseUtil.stoi(sort, -1) == -1) {
				// TODO 如果sort不包含*,而又不能解析成整数，那么说明sort有错误，返回
				return;
			} else {
				String posId = item.getPosId();
				ArticleItem articleItem = item.convertToArticleItem();
				if (posId.equals("1") && position1Map != null) {
					// 焦点图
					if (!position1Map.containsKey(sort)) {
						position1Map.put(sort, new ArrayList<ArticleItem>());
					}
					// TODO 如果广告有具体的位置，比如2，那么同一位置只能插入一条广告
					if (!sort.contains("*")) {
						position1Map.get(sort).clear();
					}
					if (position1Map.get(sort) != null)
						position1Map.get(sort).add(articleItem);
				} else if (posId.equals("2") && position2Map != null) {
					// 列表
					if (!position2Map.containsKey(sort)) {
						position2Map.put(sort, new ArrayList<ArticleItem>());
					}
					// TODO 如果广告有具体的位置，比如2，那么同一位置只能插入一条广告
					if (!sort.contains("*")) {
						position2Map.get(sort).clear();
					}
					if (position2Map.get(sort) != null)
						position2Map.get(sort).add(articleItem);
					// TODO 商周首页
					int section = item.getSection();
					if (item.getCatId().equals("0")
							&& AdvTools.containThisCat(0, item, "0")
							&& ConstData.getAppId() == 1 && section >= 0) {
						// 商周首页
						if (!bbIndexAdvMap.containsKey(section)) {
							bbIndexAdvMap
									.put(section, new ArrayList<AdvItem>());
						}
						bbIndexAdvMap.get(section).add(item);
					}
				}
			}
		}
	}

	/**
	 * 获取焦点图具体位置上的广告
	 * 
	 * @param position
	 * @return
	 */
	protected List<ArticleItem> getTitleAdvsByPosition(int positon) {
		List<ArticleItem> list = new ArrayList<ArticleItem>();
		if (!position1Map.isEmpty()) {
			for (String key : position1Map.keySet()) {
				if (key.contains("*")
						&& AdvTools.containPositionByStar(key, positon)) {
					// TODO 添加带*的广告
					list.addAll(position1Map.get(key));
				} else if (key.equals(String.valueOf(positon))) {
					// TODO 添加具体位置的广告
					list.addAll(position1Map.get(key));
				}
			}
		}
		return list;
	}

	/**
	 * 获取焦点图出现在末尾位置上的广告
	 * 
	 * @param position
	 * @return
	 */
	protected List<ArticleItem> getTitleAdvsByEndPosition(int positon) {
		List<ArticleItem> list = new ArrayList<ArticleItem>();
		if (position1Map.containsKey(String.valueOf(positon))) {
			list.addAll(position1Map.get(String.valueOf(positon)));
		}
		return list;
	}

	/**
	 * 获取列表图具体位置上的广告
	 * 
	 * @param list
	 * @param position
	 */
	protected List<ArticleItem> getListAdvsByPosition(int position) {
		List<ArticleItem> list = new ArrayList<ArticleItem>();
		if (!position2Map.isEmpty()) {
			for (String key : position2Map.keySet()) {
				if (key.contains("*")
						&& AdvTools.containPositionByStar(key, position)) {
					list.addAll(position2Map.get(key));
					addimpressionUrl(position2Map.get(key));
				} else if (key.equals(String.valueOf(position))) {
					list.addAll(position2Map.get(key));
					addimpressionUrl(position2Map.get(key));
				}
			}
		}
		return list;
	}

	/**
	 * 获取焦点图出现在末尾位置上的广告
	 * 
	 * @param position
	 * @return
	 */
	protected List<ArticleItem> getListAdvsByEndPosition(int positon) {
		String key = String.valueOf(positon);
		List<ArticleItem> list = new ArrayList<ArticleItem>();
		if (position2Map.containsKey(key)) {
			list.addAll(position2Map.get(key));
			addimpressionUrl(position2Map.get(key));
		}
		return list;
	}

	/**
	 * 获取商周首页列表广告
	 * 
	 * @param catSection
	 * @param positionInCat
	 * @return
	 */
	protected List<ArticleItem> getBBIndexListAdv(int catSection,
			int positionInCat) {
		List<ArticleItem> list = new ArrayList<ArticleItem>();
		if (!bbIndexAdvMap.isEmpty()) {
			for (int section : bbIndexAdvMap.keySet()) {
				if (section != catSection) {
					continue;
				}
				List<AdvItem> advList = bbIndexAdvMap.get(section);
				for (AdvItem item : advList) {
					String sort = item.getSort();
					if (sort.contains("*")
							&& AdvTools.containPositionByStar(sort,
									positionInCat)) {
						// TODO 添加带*的广告
						list.add(item.convertToArticleItem());
					} else if (sort.equals(String.valueOf(positionInCat))) {
						// TODO 添加具体位置的广告
						list.add(item.convertToArticleItem());
					}
				}
			}
		}
		return list;
	}

	private void addimpressionUrl(List<ArticleItem> list) {
		for (ArticleItem item : list) {
			if (item.getAdvTracker() != null)
				impressionUrlList.add(item.getAdvTracker().getImpressionUrl());
		}
	}

	protected void reSetPosition() {
		currentPosition1 = 0;
		currentPosition2 = 0;
		impressionUrlList.clear();
	}
}
