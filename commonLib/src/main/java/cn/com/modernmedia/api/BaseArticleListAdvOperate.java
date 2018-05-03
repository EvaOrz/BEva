//package cn.com.modernmedia.api;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Map;
//import java.util.TreeMap;
//
//import android.text.TextUtils;
//import cn.com.modernmedia.CommonApplication;
//import cn.com.modernmedia.model.AdvList;
//import cn.com.modernmedia.model.AdvList.AdvItem;
//import cn.com.modernmedia.model.ArticleItem;
//import cn.com.modernmedia.util.AdvTools;
//import cn.com.modernmediaslate.unit.ParseUtil;
//
///**
// * 文章广告（如果有catId，那么就是这个栏目下的第sort个位置，如果没有catId，那么就是整个articleList里的第sort个位置；
// * 当没有catId时，以all表示，方便排序）
// *
// * 顺序按照本来的articlelist来排序(即刨除广告，所有在解析json的时候根据当前文章的位置在他前面添加广告)
// *
// * @author user
// *
// */
//// XXX添加整体带*的广告->根据栏目添加栏目内带*的广告->根据栏目添加栏目内具体位置的广告->添加整体具体位置的广告
//public abstract class BaseArticleListAdvOperate extends BaseOperate {
//	/**
//	 * key:catId
//	 */
//	public TreeMap<String, TreeMap<String, List<ArticleItem>>> articleAdvMap = new TreeMap<String, TreeMap<String, List<ArticleItem>>>(
//			new Comparator<String>() {
//
//				/**
//				 *
//				 * @param o1
//				 *            比较值(后面的数)
//				 * @param o2
//				 *            被比较值(前面的数)
//				 * @return
//				 */
//				@Override
//				public int compare(String o1, String o2) {
//					if (o1.contains(AdvList.ARTICLE_NULL_CAT)
//							&& o2.contains(AdvList.ARTICLE_NULL_CAT)) {
//						return o1.compareTo(o2);
//					}
//					if (o1.contains(AdvList.ARTICLE_NULL_CAT)) {
//						return 1;
//					} else if (o2.contains(AdvList.ARTICLE_NULL_CAT)) {
//						return -1;
//					}
//					return o1.compareTo(o2);
//				}
//			});
//
//	/**
//	 * 获得需要的广告
//	 *
//	 * @param issueId
//	 *            当前文章所在的期
//	 * @param catList
//	 *            文章中栏目id集合
//	 */
//	public void initAdv(String issueId, List<String> catList) {
//		if (CommonApplication.advList == null)
//			return;
//		Map<Integer, List<AdvItem>> advMap = CommonApplication.advList
//				.getAdvMap();
//		if (!ParseUtil.mapContainsKey(advMap, AdvList.BETWEEN_ARTICLE))
//			return;
//		List<AdvItem> list = advMap.get(AdvList.BETWEEN_ARTICLE);
//		if (!ParseUtil.listNotNull(list))
//			return;
//		for (AdvItem item : list) {
//			if (item == null)
//				continue;
//			String sort = item.getSort();
//			if (TextUtils.isEmpty(sort)) {
//				continue;
//			} else if (!sort.contains("*") && ParseUtil.stoi(sort, -1) == -1) {
//				// TODO 如果sort不包含*,而又不能解析成整数，那么说明sort有错误，返回
//				continue;
//			}
//			String tagName = item.getTagname();
//			if (tagName.contains("*")) {
//				// TODO 如果catId包含*，那么判断有哪些栏目需要添加广告的，批量添加
//				List<String> temp = getAdvCatList(tagName, catList);
//				if (ParseUtil.listNotNull(temp)) {
//					for (String cat_id : temp) {
//						addAdvToMap(cat_id, sort, item);
//					}
//				}
//			} else {
//				addAdvToMap(tagName, sort, item);
//			}
//		}
//	}
//
//	private void addAdvToMap(String catId, String sort, AdvItem item) {
//		if (!articleAdvMap.containsKey(catId)) {
//			articleAdvMap.put(catId,
//			/**
//			 * key:sort 当是同一栏目的广告时，按sort排序；先排*，再按具体数字由小到大排
//			 */
//			new TreeMap<String, List<ArticleItem>>(new Comparator<String>() {
//				@Override
//				public int compare(String o1, String o2) {
//					if (o1.contains("*") && o2.contains("*")) {
//						return o1.compareTo(o2);
//					}
//					if (o1.contains("*")) {
//						return -1;
//					} else if (o2.contains("*")) {
//						return 1;
//					}
//					// 升序
//					return ParseUtil.stoi(o1) - (ParseUtil.stoi(o2));
//				}
//			}));
//		}
//		if (!articleAdvMap.get(catId).containsKey(sort)) {
//			articleAdvMap.get(catId).put(sort, new ArrayList<ArticleItem>());
//		}
//		// TODO 如果广告有具体的位置，比如2，那么同一位置只能插入一条广告
//		if (!sort.contains("*")) {
//			articleAdvMap.get(catId).get(sort).clear();
//		}
//		articleAdvMap.get(catId).get(sort)
//				.addAll(item.convertToArticleItemList());
//	}
//
//	/**
//	 * 当advCatId包含*时，获取所有符合的栏目
//	 *
//	 * @param advCatId
//	 * @param catList
//	 * @return
//	 */
//	public List<String> getAdvCatList(String advCatId, List<String> catList) {
//		if (advCatId.equals("*")) {
//			return catList;
//		}
//		List<String> temp = new ArrayList<String>();
//		int s = 0;
//		String splite[] = advCatId.split("/");
//		if (splite.length == 2) {
//			s = ParseUtil.stoi(splite[1], -1);
//			if (s < 1) {
//				return null;
//			}
//		}
//		for (int i = 0; i < catList.size(); i++) {
//			if (i % s == 0) {
//				temp.add(catList.get(i));
//			}
//		}
//		return temp;
//	}
//
//	/**
//	 * 获得相对于整体文章来说的符合当前位置的广告
//	 *
//	 * @param position
//	 * @return
//	 */
//	public List<ArticleItem> getAdvsRefAllByPosition(int position) {
//		List<ArticleItem> list = new ArrayList<ArticleItem>();
//		if (articleAdvMap.containsKey(AdvList.ARTICLE_NULL_CAT)) {
//			TreeMap<String, List<ArticleItem>> map = articleAdvMap
//					.get(AdvList.ARTICLE_NULL_CAT);
//			for (String key : map.keySet()) {
//				if (key.contains("*")
//						&& AdvTools.containPositionByStar(key, position)) {
//					list.addAll(map.get(key));
//				} else if (key.equals(String.valueOf(position))) {
//					list.addAll(map.get(key));
//				}
//			}
//		}
//		return list;
//	}
//
//	/**
//	 * 获得相对于当前栏目下文章来说的符合当前位置的广告
//	 *
//	 * @param cId
//	 * @param position
//	 * @return
//	 */
//	public List<ArticleItem> getAdvsRefCatByPosition(int cId, int position) {
//		String catId = String.valueOf(cId);
//		List<ArticleItem> list = new ArrayList<ArticleItem>();
//		if (articleAdvMap.containsKey(catId)) {
//			TreeMap<String, List<ArticleItem>> map = articleAdvMap.get(catId);
//			for (String key : map.keySet()) {
//				if (key.contains("*")
//						&& AdvTools.containPositionByStar(key, position)) {
//					list.addAll(map.get(key));
//				} else if (key.equals(String.valueOf(position))) {
//					list.addAll(map.get(key));
//				}
//			}
//		}
//		return list;
//	}
//
//	// /**
//	// * 给栏目下的文章插入带*的广告
//	// *
//	// * @param catId
//	// * @param articleItemList
//	// * @param position
//	// * 文章在当前栏目下所处的位置
//	// */
//	// public void addAdvToCatWithStar(String catId,
//	// List<FavoriteItem> articleItemList, int position) {
//	// if (articleAdvMap.containsKey(catId)) {
//	// TreeMap<String, List<FavoriteItem>> map = articleAdvMap.get(catId);
//	// for (String key : map.keySet()) {
//	// if (key.contains("*")
//	// && AdvTools.containPositionByStar(key, position)) {
//	// articleItemList.addAll(map.get(key));
//	// }
//	// }
//	// }
//	// }
//	//
//	// /**
//	// * 给栏目下的文章插入有具体位置的广告
//	// *
//	// * @param catId
//	// * @param articleItemList
//	// */
//	// public void addAdvToCat(String catId, List<FavoriteItem> articleItemList)
//	// {
//	// if (articleAdvMap.containsKey(catId)) {
//	// // TODO 添加具体位置的
//	// AdvTools.getTempList(articleItemList, articleAdvMap.get(catId), 0);
//	// }
//	// }
//	//
//	// /**
//	// * 添加整体带具体位置的广告
//	// *
//	// * @param articleItemList
//	// * @param position
//	// * 文章所处的位置
//	// */
//	// public void addAdvToAllWithStar(List<FavoriteItem> list, int position) {
//	// if (articleAdvMap.containsKey(AdvList.ARTICLE_NULL_CAT)) {
//	// TreeMap<String, List<FavoriteItem>> map = articleAdvMap
//	// .get(AdvList.ARTICLE_NULL_CAT);
//	// for (String key : map.keySet()) {
//	// if (key.contains("*")
//	// && AdvTools.containPositionByStar(key, position)) {
//	// list.addAll(map.get(key));
//	// }
//	// }
//	// }
//	// }
//	//
//	// /**
//	// * 添加整体带*的广告
//	// *
//	// * @param articleItemList
//	// */
//	// public void addAdvToAll(List<FavoriteItem> articleItemList) {
//	// if (articleAdvMap.containsKey(AdvList.ARTICLE_NULL_CAT)) {
//	// // TODO 添加具体位置的
//	// AdvTools.getTempList(articleItemList,
//	// articleAdvMap.get(AdvList.ARTICLE_NULL_CAT), 0);
//	// }
//	// }
//}
