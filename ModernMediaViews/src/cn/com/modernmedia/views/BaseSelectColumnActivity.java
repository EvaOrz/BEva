package cn.com.modernmedia.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.SubscribeOrderList;
import cn.com.modernmedia.model.SubscribeOrderList.SubscribeColumn;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.UserSubscribeListDb;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserDataHelper;

public abstract class BaseSelectColumnActivity extends BaseActivity {
	private String parent = "";

	/**
	 * 订阅栏目
	 */
	public void addColumns(String parent) {
		this.parent = parent;
		List<SubscribeColumn> list = new ArrayList<SubscribeColumn>();
		for (TagInfo info : AppValue.tempColumnList.getList()) {
			// TODO 提交主栏目 (兼容ios，如果是一级栏目，那么parent传他自己)
			if (info.getTagLevel() != 1)
				continue;
			if (info.getColumnProperty().getNoColumn() == 1)
				continue;
			if (info.getHaveChildren() == 1) {
				// TODO 如果是父栏目，那么确认是不是有子栏目被选中
				if (checkParentIsSelect(info, true))
					list.add(new SubscribeColumn(info.getTagName(), info
							.getTagName()));
			} else if (info.getHasSubscribe() == 1) {
				list.add(new SubscribeColumn(info.getTagName(), info
						.getTagName()));
			}
		}
		if (AppValue.tempColumnList.getChildMap().isEmpty()) {
			saveSubscribeColumnList(list);
			return;
		}
		for (String key : AppValue.tempColumnList.getChildMap().keySet()) {
			// TODO 提交子栏目
			List<TagInfo> childList = AppValue.tempColumnList.getChildMap()
					.get(key);
			for (TagInfo info : childList) {
				if (info.getHasSubscribe() == 1)
					list.add(new SubscribeColumn(info.getTagName(), info
							.getParent()));
			}
		}
		saveSubscribeColumnList(list);
	}

	/**
	 * 判断父栏目是否被选中
	 * 
	 * @param info
	 * @param isSaveSubscribe
	 *            true:代表是保存父栏目;false:代表是显示父栏目☑️
	 * 
	 * @return
	 */
	public boolean checkParentIsSelect(TagInfo info, boolean isSaveSubscribe) {
		// TODO 如果是父栏目，那么遍历所有子栏目，只要有一个被选中，那么即为选中
		if (!ParseUtil.mapContainsKey(AppValue.tempColumnList.getChildMap(),
				info.getTagName()))
			return false;
		List<TagInfo> list = AppValue.tempColumnList.getChildMap().get(
				info.getTagName());
		for (TagInfo child : list) {
			if (child.getHasSubscribe() == 1
					&& child.getColumnProperty().getNoColumn() == 0) {
				if (isSaveSubscribe) {
					return true;
				} else {
					if (child.getIsFix() == 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 保存订阅栏目
	 */
	private void saveSubscribeColumnList(final List<SubscribeColumn> list) {
		final User user = UserDataHelper.getUserLoginInfo(this);
		if (user == null)
			return;
		showLoadingDialog(true);
		OperateController.getInstance(this).saveSubscribeColumnList(
				user.getUid(), user.getToken(), list, new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						showLoadingDialog(false);
						if (entry instanceof ErrorMsg) {
							ErrorMsg error = (ErrorMsg) entry;
							if (error.getNo() == 0) {
								afterSave(user.getUid(), list);
							} else {
								showToast(error.getDesc());
							}
						}
					}
				});
	}

	private void afterSave(String uid, List<SubscribeColumn> list) {
		SubscribeOrderList orderList = new SubscribeOrderList();
		orderList.setAppId(ConstData.getInitialAppId());
		orderList.setUid(uid);
		orderList.setColumnList(list);
		UserSubscribeListDb.getInstance(this).clearTable(uid);
		UserSubscribeListDb.getInstance(this).addEntry(orderList);
		AppValue.ensubscriptColumnList = AppValue.tempColumnList.copy();
		Intent intent = new Intent();
		intent.putExtra("PARENT", parent);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void reLoadData() {
	}

}
