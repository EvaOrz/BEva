package cn.com.modernmedia.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.api.GetUserSubscribeListOpertate;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.SubscribeOrderList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.UserSubscribeListDb;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;

public class EnsubscriptHelper {
	/**
	 * 父栏目列表；添加完所有栏目之后，根据子栏目重新判断父栏目是否已订阅
	 */
	private static List<TagInfo> parentTagInfoList = new ArrayList<TagInfo>();

	/**
	 * 添加已订阅栏目
	 * 
	 * @param context
	 * @param uid
	 */
	public static void addEnsubscriptColumn(Context context, String uid,
			String token) {
		initSubscriptList();
		if (AppValue.appInfo.getHaveSubscribe() == 0
				|| SlateApplication.mConfig.getHas_subscribe() == 0) {
			// TODO 不支持订阅
			unSupportSubscript();
		} else if (TextUtils.equals(uid, SlateApplication.UN_UPLOAD_UID)) {
			// TODO 未登录
			supportSubscriptAndUnLogin();
		} else {
			SubscribeOrderList subscribeOrderList = new SubscribeOrderList();
			Entry entry = UserSubscribeListDb.getInstance(context).getEntry(
					new GetUserSubscribeListOpertate(uid, token), uid);
			if (entry instanceof SubscribeOrderList) {
				subscribeOrderList = (SubscribeOrderList) entry;
			}
			/**
			 * 用户已订阅列表
			 */
			List<String> subscribeTagList = subscribeOrderList.getTagNameList();
			if (ParseUtil.listNotNull(subscribeTagList)) {
				hasSubscribeTagListByUser(subscribeTagList);
			} else {
				noBindSubscribeTagListByUser();
			}
		}
		// TODO 最后判断父栏目是否已订阅
		checkParentTagIsSubscribe();
	}

	/**
	 * 初始化
	 */
	private static void initSubscriptList() {
		AppValue.ensubscriptColumnList = AppValue.defaultColumnList.copy();
		AppValue.ensubscriptColumnList.getChildMap().clear();
		AppValue.ensubscriptColumnList.getParentList().clear();
		parentTagInfoList.clear();
	}

	/**
	 * 不支持订阅
	 */
	private static void unSupportSubscript() {
		for (TagInfo tagInfo : AppValue.ensubscriptColumnList.getList()) {
			// TODO 如果defaultsubscribe=1并且appid为当前应用的appid,那么为已订阅
			if (tagInfo.getAppId() == ConstData.getInitialAppId()
					&& tagInfo.getColumnProperty().getNoColumn() == 0
					&& tagInfo.getEnablesubscribe() == 1
					&& (tagInfo.getDefaultsubscribe() == 1 || tagInfo
							.getIsFix() == 1)) {
				tagInfo.setHasSubscribe(1);
			} else {
				tagInfo.setHasSubscribe(0);
			}
			addParentAndChildTagInfo(tagInfo);
		}
	}

	/**
	 * 支持订阅但未登录
	 */
	private static void supportSubscriptAndUnLogin() {
		for (TagInfo tagInfo : AppValue.ensubscriptColumnList.getList()) {
			// TODO 如果defaultsubscribe=1，那么为已订阅
			if (tagInfo.getColumnProperty().getNoColumn() == 0
					&& tagInfo.getEnablesubscribe() == 1
					&& (tagInfo.getDefaultsubscribe() == 1 || tagInfo
							.getIsFix() == 1)) {
				tagInfo.setHasSubscribe(1);
			} else {
				tagInfo.setHasSubscribe(0);
			}
			addParentAndChildTagInfo(tagInfo);
		}
	}

	/**
	 * 用户有订阅列表
	 * 
	 * @param subscribeTagList
	 *            用户已订阅列表
	 */
	private static void hasSubscribeTagListByUser(List<String> subscribeTagList) {
		for (TagInfo tagInfo : AppValue.ensubscriptColumnList.getList()) {
			// TODO (可能一个栏目出现多次，通过tagname和parent组合起来判断具体的栏目)
			String tag_parent_name = tagInfo.getTagName() + "&";
			if (tagInfo.getTagLevel() == 1) {
				tag_parent_name += tagInfo.getTagName();
			} else if (tagInfo.getTagLevel() == 2) {
				tag_parent_name += tagInfo.getParent();
			}
			if (tagInfo.getColumnProperty().getNoColumn() == 1
					|| tagInfo.getEnablesubscribe() == 0) {
				tagInfo.setHasSubscribe(0);
			} else if (subscribeTagList.contains(tag_parent_name)) {
				tagInfo.setHasSubscribe(1);
			} else if (tagInfo.getIsFix() == 1) {
				tagInfo.setHasSubscribe(1);
			} else {
				tagInfo.setHasSubscribe(0);
			}
			addParentAndChildTagInfo(tagInfo);
		}
	}

	/**
	 * 用户没有绑定过订阅列表
	 */
	private static void noBindSubscribeTagListByUser() {
		// TODO 没有绑定数据，说明以前没有手动订阅过
		for (TagInfo tagInfo : AppValue.ensubscriptColumnList.getList()) {
			// TODO 如果isdefault=1，那么为已订阅
			if (tagInfo.getColumnProperty().getNoColumn() == 0
					&& tagInfo.getEnablesubscribe() == 1
					&& (tagInfo.getDefaultsubscribe() == 1 || tagInfo
							.getIsFix() == 1)) {
				tagInfo.setHasSubscribe(1);
			} else {
				tagInfo.setHasSubscribe(0);
			}
			addParentAndChildTagInfo(tagInfo);
		}
	}

	/**
	 * 添加父栏目以及子栏目
	 * 
	 * @param tagInfo
	 */
	private static void addParentAndChildTagInfo(TagInfo tagInfo) {
		AppValue.ensubscriptColumnList.addChild(tagInfo);
		if (tagInfo.getTagLevel() == 1 && tagInfo.getHaveChildren() == 1) {
			parentTagInfoList.add(tagInfo);
		}
	}

	/**
	 * 判断父栏目是否已订阅
	 */
	private static void checkParentTagIsSubscribe() {
		if (!ParseUtil.listNotNull(parentTagInfoList))
			return;
		for (TagInfo parentTag : parentTagInfoList) {
			if (parentTag.getHasSubscribe() == 1) {
				parentTag.setHasSubscribe(AppValue.ensubscriptColumnList
						.hasChild(parentTag.getTagName()) ? 1 : 0);
			}
		}
	}
}
