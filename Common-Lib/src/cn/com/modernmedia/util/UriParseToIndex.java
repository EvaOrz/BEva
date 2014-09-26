package cn.com.modernmedia.util;

import android.content.Context;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.Tools;

/**
 * uri跳转到栏目首页
 * 
 * @author zhuqiao
 *
 */
public class UriParseToIndex {
	private Context mContext;
	private UriParseToIndexListener listener;

	public static interface UriParseToIndexListener {
		public void fetchTagInfo(TagInfo info);
	}

	public UriParseToIndex(Context context, UriParseToIndexListener listener) {
		mContext = context;
		this.listener = listener;
	}

	/**
	 * 从已订阅列表中查找是否有这个栏目
	 * 
	 * @param tagName
	 */
	public TagInfo findTagInSubscriptList(String tagName) {
		for (TagInfo tagInfo : AppValue.ensubscriptColumnList.getList()) {
			if (tagInfo.getTagName().equals(tagName)) {
				return tagInfo;
			}
		}
		return null;
	}

	/**
	 * 如果已订阅列表里没有这个栏目,那么需要自己去生成显示
	 * 
	 * @param tagName
	 */
	public void findTagWhenDidnotFind(String tagName) {
		// TODO 从原始栏目列表中寻找这个taginfo
		for (TagInfo tagInfo : AppValue.defaultColumnList.getList()) {
			if (tagInfo.getTagName().equals(tagName)) {
				// TODO 在初始的订阅里找到该tag，那么重新初始化catItems,添加到最后一个
				TagInfo uriTagInfo = tagInfo.copy();// 防止把defaultColumnList里的直接改变状态
				if (uriTagInfo.getHaveChildren() == 1) {
					AppValue.uriTagInfoList.getParentList().add(tagName);
					AppValue.uriTagInfoList.getChildMap().put(
							tagName,
							AppValue.defaultColumnList.getChildMap().get(
									tagName));
				}
				addUriTagInfoToCatItems(uriTagInfo);
				return;
			}
		}
		// TODO 原始栏目列表中没有这个taginfo，那么从接口获取
		getTagInfo(tagName);
	}

	/**
	 * 
	 * 添加taginfo至第一个
	 * 
	 * @param tagInfo
	 */
	private void addUriTagInfoToCatItems(TagInfo tagInfo) {
		tagInfo.setUriTag(true);
		AppValue.uriTagInfoList.getList().add(tagInfo);
		if (listener != null)
			listener.fetchTagInfo(tagInfo);
	}

	/**
	 * 获取tag信息
	 * 
	 * @param info
	 */
	private void getTagInfo(String tagName) {
		Tools.showLoading(mContext, true);
		OperateController.getInstance(mContext).getTagInfo(tagName,
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof TagInfoList) {
							TagInfoList list = (TagInfoList) entry;
							if (ParseUtil.listNotNull(list.getList())) {
								TagInfo tagInfo = list.getList().get(0);
								if (tagInfo.getHaveChildren() == 0) {
									// TODO 如果没有子栏目，那么直接添加
									addUriTagInfoToCatItems(tagInfo);
								} else {
									// TODO 有子栏目，那么获取子栏目列表
									getTagChildInfo(tagInfo);
								}
							} else {
								Tools.showLoading(mContext, false);
							}
						} else {
							Tools.showLoading(mContext, false);
						}
					}
				});
	}

	/**
	 * 获取子栏目信息
	 * 
	 * @param parentInfo
	 */
	private void getTagChildInfo(final TagInfo parentInfo) {
		OperateController.getInstance(mContext).getChildTagInfo(
				parentInfo.getTagName(), new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						Tools.showLoading(mContext, false);
						if (entry instanceof TagInfoList) {
							TagInfoList list = (TagInfoList) entry;
							if (ParseUtil.listNotNull(list.getList())) {
								setChild(list);
								AppValue.uriTagInfoList.getParentList().add(
										parentInfo.getTagName());
								AppValue.uriTagInfoList.getChildMap()
										.put(parentInfo.getTagName(),
												list.getList());
								addUriTagInfoToCatItems(parentInfo);
							}
						}
					}
				});
	}

	private void setChild(TagInfoList list) {
		for (TagInfo info : list.getList()) {
			info.setUriTag(true);
		}
	}

}
