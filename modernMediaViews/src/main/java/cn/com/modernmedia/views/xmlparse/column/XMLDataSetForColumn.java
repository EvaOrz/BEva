package cn.com.modernmedia.views.xmlparse.column;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.column.ColumnAdapter;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.xmlparse.BaseXMLDataSet;
import cn.com.modernmedia.views.xmlparse.FunctionXML;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 栏目列表设置数据
 * 
 * @author zhuqiao
 * 
 */
public class XMLDataSetForColumn extends BaseXMLDataSet {
	private ColumnAdapter adapter;
	public static ClickFirstColumnListener clickFirstColumnListener;

	public static interface ClickFirstColumnListener {
		public void click();
	}

	public XMLDataSetForColumn(Context context, HashMap<String, View> map,
			List<View> clickViewList, List<View> ninePatchViewList,
			ColumnAdapter adapter) {
		super(context, map, clickViewList, ninePatchViewList);
		this.adapter = adapter;
	}

	public void setData(TagInfo info, int position) {
		if (map == null || map.isEmpty() || info == null || adapter == null)
			return;
		int id = info.getAdapter_id();
		title(info, id, position);
		eTitle(info, id);
		img(info, id);
		subscriptImg(id);
		subscrip(id);
		content(id, position);
		divider(id, position);
		newDot(info, position);
		row(id, position);
		ninePatch();
		if (ParseUtil.listNotNull(clickViewList)) {
			for (View view : clickViewList) {
				onClick(view, info, position);
			}
		}
	}

	/**
	 * 名称
	 * 
	 * @param info
	 * @param id
	 */
	private void title(TagInfo info, int id, int position) {
		if (!map.containsKey(FunctionXML.TITLE))
			return;
		View view = map.get(FunctionXML.TITLE);
		if (!(view instanceof TextView))
			return;
		TextView title = (TextView) view;
		if (id == 1) {
			title.setText(R.string.about);
		} else if (id == 2) {
			title.setText(R.string.app_recommend);
		} else {
			title.setText(info.getColumnProperty().getCname());
		}
		if (id != 0)
			return;
		boolean select = position == adapter.getSelectPosition();
		if (select) {
			if (title.getTag(R.id.select_color) instanceof String) {
				title.setTextColor(Color.parseColor(title.getTag(
						R.id.select_color).toString()));
			}
		} else {
			if (title.getTag(R.id.unselect_color) instanceof String) {
				title.setTextColor(Color.parseColor(title.getTag(
						R.id.unselect_color).toString()));
			}
		}
	}

	/**
	 * 名称
	 * 
	 * @param info
	 * @param id
	 */
	private void eTitle(TagInfo info, int id) {
		if (!map.containsKey(FunctionColumn.E_NAME) || id != 0)
			return;
		View view = map.get(FunctionColumn.E_NAME);
		if (!(view instanceof TextView))
			return;
		TextView title = (TextView) view;
		title.setText(info.getColumnProperty().getEname());
	}

	/**
	 * 栏目图片
	 * 
	 * @param info
	 * @param id
	 */
	private void img(TagInfo info, int id) {
		if (!map.containsKey(FunctionXML.IMAGE))
			return;
		View view = map.get(FunctionXML.IMAGE);
		if (!(view instanceof ImageView))
			return;
		ImageView icon = (ImageView) view;
		if (id == 1 || id == 2) {
			if (ConstData.isGoodLife()) {
				icon.setBackgroundColor(id == 1 ? Color.parseColor("#70472C")
						: Color.parseColor("#6BCDFF"));
			} else {
				icon.setScaleType(ScaleType.CENTER);
				icon.setImageBitmap(V.getCircleBitmap(mContext, Color.BLUE));
			}
		} else {
			if (ConstData.getAppId() == 20 || ConstData.getAppId() == 37) {// iweekly\verycity
				V.setImageForWeeklyCat(mContext, info, icon);
			} else if (ConstData.getAppId() == 105) {
				V.setWeZeitColumnIcon(mContext, info, icon);
			} else if (ConstData.isGoodLife()) {
				V.catPicShowColor(mContext, info, icon);
			} else {
				V.downCatPic(mContext, info, icon, false);
			}
		}
	}

	/**
	 * 订阅图片
	 * 
	 * @param id
	 */
	private void subscriptImg(int id) {
		if (!map.containsKey(FunctionColumn.SUBSCRIPT_IMG))
			return;
		View view = map.get(FunctionColumn.SUBSCRIPT_IMG);
		view.setVisibility(id == 3 ? View.VISIBLE : View.GONE);
	}

	/**
	 * 订阅frame
	 * 
	 * @param id
	 */
	private void subscrip(int id) {
		if (!map.containsKey(FunctionColumn.SUBSCRIPT_FRAME))
			return;
		View view = map.get(FunctionColumn.SUBSCRIPT_FRAME);
		if (id == 3 || id == 4) {
			view.setVisibility(View.VISIBLE);
			if (id == 4)
				view.setBackgroundColor(Color.TRANSPARENT);
		} else {
			view.setVisibility(View.GONE);
		}
	}

	/**
	 * 内容frame
	 */
	private void content(int id, int position) {
		if (!map.containsKey(FunctionXML.FRAME_CONTENT))
			return;
		View view = map.get(FunctionXML.FRAME_CONTENT);
		view.setVisibility(id == 3 || id == 4 ? View.GONE : View.VISIBLE);
		boolean select = position == adapter.getSelectPosition();
		if (select) {
			if (view.getTag(R.id.select_bg) instanceof String) {
				V.setViewBack(view, view.getTag(R.id.select_bg).toString());
			}
		} else {
			if (view.getTag(R.id.unselect_bg) instanceof String) {
				V.setViewBack(view, view.getTag(R.id.unselect_bg).toString());
			}
		}
	}

	/**
	 * 分割线
	 * 
	 * @param id
	 */
	private void divider(int id, int position) {
		if (!map.containsKey(FunctionColumn.DIVIDER))
			return;
		View view = map.get(FunctionColumn.DIVIDER);
		view.setVisibility(View.VISIBLE);
		if (id == 4) {
			view.setVisibility(View.GONE);
		} else if (ConstData.getAppId() == 20
				&& AppValue.appInfo.getHaveSubscribe() == 1
				&& SlateApplication.mConfig.getHas_subscribe() == 1) {
			if (id == 3 || position == adapter.getThisAppItemCount() - 1) {
				view.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 未读
	 */
	private void newDot(TagInfo info, int position) {
		if (!map.containsKey(FunctionColumn.NEW_IMG))
			return;
		View view = map.get(FunctionColumn.NEW_IMG);
		view.setVisibility(View.GONE);
		if (info.getAppId() != 20) {
			return;
		}
		if (position > 0) {
			if (CommonApplication.lastestArticleId == null) {
				return;
			}
			Map<String, ArrayList<Integer>> map = CommonApplication.lastestArticleId
					.getUnReadedArticles();
			String tagName = info.getTagName();
			if (ParseUtil.mapContainsKey(map, tagName)
					&& map.get(tagName) != null && map.get(tagName).size() > 0) {
				view.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 箭头
	 * 
	 * @param item
	 */
	private void row(int id, int position) {
		if (id == 1 || id == 2)
			return;
		if (!map.containsKey(FunctionXML.ROW))
			return;
		View view = map.get(FunctionXML.ROW);
		if (!(view instanceof ImageView))
			return;
		boolean select = position == adapter.getSelectPosition();
		if (select) {
			if (view.getTag(R.id.select_bg) instanceof String) {
				V.setImage(view, view.getTag(R.id.select_bg).toString());
			}
		} else {
			if (view.getTag(R.id.unselect_bg) instanceof String) {
				V.setImage(view, view.getTag(R.id.unselect_bg).toString());
			}
		}
	}

	/**
	 * 注册点击事件
	 * 
	 * @param view
	 */
	private void onClick(View view, final TagInfo info, final int position) {
		if (!isColumn(info))
			return;
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				click(info, position);
			}
		});
	}

	private void click(final TagInfo item, final int position) {
		LogHelper.logOpenColumn(mContext, item.getTagName());
		if (mContext instanceof CommonMainActivity) {
			if (ViewsApplication.itemIsSelectedListener != null)
				ViewsApplication.itemIsSelectedListener.doIsSelected(false);
			if (CommonApplication.mConfig.getHas_single_view() != 1
					|| position != 0)
				V.setIndexTitle(mContext, item);
			((CommonMainActivity) mContext).getScrollView().IndexClick();
			if (CommonApplication.mConfig.getHas_single_view() == 1
					&& position == 0 && clickFirstColumnListener != null) {
			} else if (adapter.getSelectPosition() == position) {
				return;
			}
			adapter.setSelectPosition(position);
			adapter.notifyDataSetChanged();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					if (CommonApplication.mConfig.getHas_single_view() == 1) {
						if (position == 0) {
							if (clickFirstColumnListener != null)
								clickFirstColumnListener.click();
						} else {
							((ViewsMainActivity) mContext).clickItemIfPager(
									item.getTagName(), false);
						}
						return;
					}
					if (CommonApplication.mConfig.getIs_index_pager() == 1) {
						((ViewsMainActivity) mContext).clickItemIfPager(
								item.getTagName(), false);
					}
				}
			}, 200);
		}
	}

	private boolean isColumn(TagInfo item) {
		return item.getAdapter_id() != 1 && item.getAdapter_id() != 2;
	}
}
