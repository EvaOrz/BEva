package cn.com.modernmedia.model;

import android.text.TextUtils;
import cn.com.modernmedia.model.TagInfoList.AppProperty;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.util.ConstData;

/**
 * 应用使用的变量
 * 
 * @author zhuqiao
 * 
 */
public class AppValue {
	/**
	 * 应用信息
	 */
	public static AppProperty appInfo = new AppProperty();
	/**
	 * 当前issueTag
	 */
	public static String currentIssueTag = "";
	/**
	 * 原始栏目列表
	 */
	public static TagInfoList defaultColumnList = new TagInfoList();
	/**
	 * 封装过的可订阅的栏目列表
	 */
	public static TagInfoList ensubscriptColumnList = new TagInfoList();
	/**
	 * 临时栏目列表（点击确认之前）
	 */
	public static TagInfoList tempColumnList = new TagInfoList();
	/**
	 * 临时订阅列表（新）
	 */
	public static TagInfoList bookColumnList = new TagInfoList();
	/**
	 * 电台列表
	 */
	public static TagInfoList musicColumnLIst = new TagInfoList();
	/**
	 * uri生成的栏目列表信息
	 */
	public static TagInfoList uriTagInfoList = new TagInfoList();

	public static void clear() {
		currentIssueTag = "";
		appInfo = new AppProperty();
		defaultColumnList = new TagInfoList();
		ensubscriptColumnList = new TagInfoList();
		uriTagInfoList = new TagInfoList();
	}

	/**
	 * 更新tag时间
	 * 
	 * @param info
	 */
	public static void updateTagInfoUpdateTime(TagInfo info) {
		updateList(info, defaultColumnList);
		updateList(info, ensubscriptColumnList);
		updateList(info, uriTagInfoList);
	}

	private static void updateList(TagInfo info, TagInfoList list) {
		String tagName = info.getTagName();
		for (TagInfo _info : list.getList()) {
			if (TextUtils.equals(tagName, _info.getTagName())) {
				_info.setColoumnupdatetime(info.getColoumnupdatetime());
				_info.setArticleupdatetime(info.getArticleupdatetime());
				break;
			}
		}
	}

	/**
	 * 获取iweekly跑马灯栏目
	 * 
	 * @return
	 */
	public static TagInfo getMarqueeTagInfo() {
		TagInfo tagInfo = new TagInfo();
		if (ConstData.getAppId() == 20) {
			tagInfo.setTagName("cat_1378");
			tagInfo.setAppId(20);
			tagInfo.setParent("app_20");
		} else if (ConstData.getAppId() == 1) {
			tagInfo.setTagName("cat_32_zuixin");
			tagInfo.setAppId(1);
			tagInfo.setParent("app_1");
		}

		tagInfo.setArticleupdatetime(appInfo.getUpdatetime());
		tagInfo.setColoumnupdatetime(appInfo.getUpdatetime());
		tagInfo.setGroup(3);
		return tagInfo;
	}

}
