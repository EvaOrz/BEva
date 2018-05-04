package cn.com.modernmedia.views.xmlparse;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 独立栏目/子栏目导航栏设置数据
 * 
 * @author zhuqiao
 * 
 */
public class XMLDataSetForCatHead extends BaseXMLDataSet {
	private List<View> checkSelectView;

	public XMLDataSetForCatHead(Context context, HashMap<String, View> map,
			List<View> checkSelectView, List<View> ninePatchViewList) {
		super(context, map, null, ninePatchViewList);
		this.checkSelectView = checkSelectView;
	}

	/**
	 * 设置数据
	 */
	public void setData(TagInfo tagInfo, int position, int total) {
		if (tagInfo == null)
			return;
		show(tagInfo);
		if (tagInfo.getAdapter_id() == 3)
			return;
		title(tagInfo);
		icon(tagInfo);
		divider(position);
		endDivider(position, total);
		ninePatch();
	}

	/**
	 * 判断是否显示订阅
	 * 
	 * @param tagInfo
	 */
	private void show(TagInfo tagInfo) {
		if (ParseUtil.mapContainsKey(map, FunctionXMLCatHead.CONTENT_FRAME)) {
			map.get(FunctionXMLCatHead.CONTENT_FRAME).setVisibility(
					tagInfo.getAdapter_id() == 3 ? View.GONE : View.VISIBLE);
		}
		if (ParseUtil.mapContainsKey(map, FunctionXMLCatHead.ADD_SUBSCRIBE)) {
			map.get(FunctionXMLCatHead.ADD_SUBSCRIBE).setVisibility(
					tagInfo.getAdapter_id() == 3 ? View.VISIBLE : View.GONE);
		}
	}

	/**
	 * 栏目名称
	 * 
	 * @param item
	 */
	private void title(TagInfo tagInfo) {
		ArticleItem item = new ArticleItem();
		item.setTitle(tagInfo.getColumnProperty().getCname());
		super.title(item, null);
	}

	/**
	 * 栏目icon
	 */
	private void icon(TagInfo tagInfo) {
		if (!ParseUtil.mapContainsKey(map, FunctionXML.IMAGE))
			return;
		View view = map.get(FunctionXML.IMAGE);
		if (!(view instanceof ImageView))
			return;
		ImageView imageView = (ImageView) view;
		V.setImageForWeeklyCat(mContext, tagInfo, imageView);
	}

	/**
	 * 分割线
	 * 
	 * @param position
	 */
	private void divider(int position) {
		if (!ParseUtil.mapContainsKey(map, FunctionXMLCatHead.DIVIDER))
			return;
		View view = map.get(FunctionXMLCatHead.DIVIDER);
		if (position == 0)
			view.setVisibility(View.GONE);
	}

	/**
	 * 分割线
	 * 
	 * @param position
	 */
	private void endDivider(int position, int total) {
		if (!ParseUtil.mapContainsKey(map, FunctionXMLCatHead.END_DIVIDER))
			return;
		View view = map.get(FunctionXMLCatHead.END_DIVIDER);
		if (position == total - 1)
			view.setVisibility(View.VISIBLE);
	}

	/**
	 * 更新选中状态
	 * 
	 * @param select
	 */
	public void select(boolean select) {
		if (!ParseUtil.listNotNull(checkSelectView))
			return;
		for (View view : checkSelectView) {
			if (select) {
				if (view.getTag(R.id.select_bg) instanceof String) {
					V.setViewBack(view, view.getTag(R.id.select_bg).toString());
				}
				if (view instanceof TextView) {
					if (view.getTag(R.id.select_color) instanceof String
							&& view.getTag(R.id.select_color).toString()
									.startsWith("#")) {
						((TextView) view).setTextColor(Color.parseColor(view
								.getTag(R.id.select_color).toString()));
					}
				}
			} else {
				if (view.getTag(R.id.unselect_bg) instanceof String) {
					V.setViewBack(view, view.getTag(R.id.unselect_bg)
							.toString());
				}
				if (view instanceof TextView) {
					if (view.getTag(R.id.unselect_color) instanceof String
							&& view.getTag(R.id.unselect_color).toString()
									.startsWith("#")) {
						((TextView) view).setTextColor(Color.parseColor(view
								.getTag(R.id.unselect_color).toString()));
					}
				}
			}
		}
	}
}
