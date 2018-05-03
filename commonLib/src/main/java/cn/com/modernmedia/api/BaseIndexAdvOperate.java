package cn.com.modernmedia.api;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmediaslate.unit.ParseUtil;

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
	 * 当前解析到的列表的位置
	 */
	protected int currentPosition2 = 0;
	/**
	 * 商周首页section列表的位置
	 */
	protected int currentPositionInSection = 0;
	protected int currentSection = -1;
	/**
	 * 当前解析到的文章的位置(去掉scrollhide=1的)
	 */
	protected int currentArticlePosition = 0;
	protected List<String> impressionUrlList = new ArrayList<String>();// 列表所有广告（一次性展示）

	/**
	 * key:position
	 */
	protected Map<String, List<AdvItem>> indexAdvMap = new HashMap<String, List<AdvItem>>();
	protected List<AdvItem> articleAdvList = new ArrayList<AdvItem>();

	/**
	 * 获得需要的广告
	 * 
	 * @param tagName
	 *            当前文章所在的期
	 * @param position
	 *            当前栏目再catList中所属的位置
	 */
	protected void initAdv(String tagName, int position) {
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
			// TODO 比较catId是否符合
			if (!AdvTools.containThisCat(position, item, tagName))
				continue;
			String sort = item.getSort();
			if (TextUtils.isEmpty(sort)) {
				continue;
			} else if (!sort.contains("*") && ParseUtil.stoi(sort, -1) == -1) {
				// TODO 如果sort不包含*,而又不能解析成整数，那么说明sort有错误，返回
				continue;
			} else {
				String posId = item.getPosId();
				if (!indexAdvMap.containsKey(posId)) {
					indexAdvMap.put(posId, new ArrayList<AdvItem>());
				}
				indexAdvMap.get(posId).add(item);
			}
		}
	}

	/**
	 * 初始化文章广告
	 * 
	 * @param tagName
	 */
	protected void initArticleAdv(String tagName, int position) {
		// TODO 添加广告
		if (CommonApplication.advList == null)
			return;
		Map<Integer, List<AdvItem>> advMap = CommonApplication.advList
				.getAdvMap();
		if (!ParseUtil.mapContainsKey(advMap, AdvList.BETWEEN_ARTICLE))
			return;
		List<AdvItem> list = advMap.get(AdvList.BETWEEN_ARTICLE);
		if (!ParseUtil.listNotNull(list))
			return;
		for (AdvItem item : list) {
			if (item == null)
				continue;
			// TODO 比较catId是否符合
			if (!AdvTools.containThisCat(position, item, tagName))
				continue;
			String sort = item.getSort();
			if (TextUtils.isEmpty(sort)) {
				continue;
			} else if (!sort.contains("*") && ParseUtil.stoi(sort, -1) == -1) {
				// TODO 如果sort不包含*,而又不能解析成整数，那么说明sort有错误，返回
				continue;
			} else {
				articleAdvList.add(item);
			}
		}
	}

	/**
	 * 获取当前posid匹配当前位置上的广告列表
	 * 
	 * @param posId
	 *            1.焦点图；2.列表
	 * @return
	 */
	protected List<ArticleItem> getAdvsMatchThisPosition(String posId) {
		int currIndex = currentPosition1;
		if (posId.equals("2"))
			currIndex = currentPosition2;
		else if (posId.equals("3"))
			currIndex = currentPositionInSection;
		List<ArticleItem> list = new ArrayList<ArticleItem>();
		if (!indexAdvMap.containsKey(posId))
			return list;
		List<AdvItem> advList = indexAdvMap.get(posId);
		for (AdvItem item : advList) {
			String sort = item.getSort();
			if (posId.equals("3")) {
				// TODO 商周首页
				if (item.getSection() != currentSection) {
					continue;
				}
			}
			if (sort.contains("*")) {
				if (AdvTools.containPositionByStar(sort, currIndex)) {
					list.add(item.convertToArticleItem());
					if (!posId.equals("1"))
						addimpressionUrl(item);
				}
			} else {
				int index = ParseUtil.stoi(sort, -1);
				if (index == currIndex) {
					list.add(item.convertToArticleItem());
					if (!posId.equals("1"))
						addimpressionUrl(item);
				}
			}
		}
		return list;
	}

	/**
	 * 获取比原始列表数量大的广告列表
	 * 
	 * @param posId
	 *            1.焦点图；2.列表
	 * @return
	 */
	protected List<ArticleItem> getAdvsLargerThisPosition(String posId) {
		int currIndex = currentPosition1;
		if (posId.equals("2"))
			currIndex = currentPosition2;
		else if (posId.equals("3"))
			currIndex = currentPositionInSection;
		List<ArticleItem> list = new ArrayList<ArticleItem>();
		if (!indexAdvMap.containsKey(posId))
			return list;
		List<AdvItem> advList = indexAdvMap.get(posId);
		for (AdvItem item : advList) {
			String sort = item.getSort();
			if (posId.equals("3")) {
				// TODO 商周首页
				if (item.getSection() != currentSection) {
					continue;
				}
			}
			if (!sort.contains("*")) {
				int index = ParseUtil.stoi(sort, -1);
				// TODO 解析到最后一个item时已经自动++了，所有需要==
				if (index >= currIndex) {
					list.add(item.convertToArticleItem());
					if (!posId.equals("1"))
						addimpressionUrl(item);
				}
			}
		}
		return list;
	}

	/**
	 * 获取文章匹配当前位置上的广告列表
	 * 
	 */
	protected List<ArticleItem> getArticleAdvsMatchThisPosition() {
		List<ArticleItem> list = new ArrayList<ArticleItem>();
		if (articleAdvList.isEmpty())
			return list;
		for (AdvItem item : articleAdvList) {
			String sort = item.getSort();
			if (sort.contains("*")) {
				if (AdvTools
						.containPositionByStar(sort, currentArticlePosition)) {
					list.addAll(item.convertToArticleItemList());
				}
			} else {
				int index = ParseUtil.stoi(sort, -1);
				if (index == currentArticlePosition) {
					list.addAll(item.convertToArticleItemList());
				}
			}
		}
		return list;
	}

	/**
	 * 获取比原始文章数量大的广告列表
	 * 
	 * @return
	 */
	protected List<ArticleItem> getArticleAdvsLargerThisPosition() {
		List<ArticleItem> list = new ArrayList<ArticleItem>();
		if (articleAdvList.isEmpty())
			return list;
		for (AdvItem item : articleAdvList) {
			String sort = item.getSort();
			if (!sort.contains("*")) {
				int index = ParseUtil.stoi(sort, -1);
				if (index >= currentArticlePosition) {
					list.addAll(item.convertToArticleItemList());
				}
			}
		}
		return list;
	}

	private void addimpressionUrl(AdvItem item) {
		impressionUrlList.add(item.getTracker().getImpressionUrl());
	}

	protected void reSetPosition() {
		currentPosition1 = 0;
		currentPosition2 = 0;
		currentPositionInSection = 0;
		currentSection = -1;
		currentArticlePosition = 0;
		impressionUrlList.clear();
	}
}
