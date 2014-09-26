package cn.com.modernmedia.views.xmlparse;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.db.NewFavDb;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.fav.BindFavToUserImplement;
import cn.com.modernmedia.views.index.adapter.BaseIndexAdapter;
import cn.com.modernmedia.views.model.Template.GridItem;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.unit.DateFormatTool;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * grid类型列表设置数据
 * 
 * @author zhuqiao
 * 
 */
public class XMLDataSetForGrid extends BaseXMLDataSet {
	private HashMap<String, List<GridItem>> gridMap;
	private BaseIndexAdapter adapter;
	private List<GridItem> clickViewList;

	private List<ArticleItem> list;
	private int position, oneLineCount;

	public XMLDataSetForGrid(Context context,
			HashMap<String, List<GridItem>> gridMap,
			List<GridItem> clickViewList, List<View> ninePatchViewList,
			BaseIndexAdapter adapter) {
		super(context, null, null, ninePatchViewList);
		this.gridMap = gridMap;
		this.clickViewList = clickViewList;
		this.adapter = adapter;
	}

	/**
	 * 设置数据
	 * 
	 * @param item
	 */
	public void setData(List<ArticleItem> list, int position, int oneLineCount,
			ArticleType articleType) {
		if (gridMap == null || gridMap.isEmpty()
				|| !ParseUtil.listNotNull(list) || adapter == null)
			return;
		this.list = list;
		this.position = position;
		this.oneLineCount = oneLineCount;

		frameContent();
		titleBar();
		img();
		title();
		titlebarDate();
		desc();
		adv();
		video();
		outline();
		tag();
		date();
		fav();
		subTitle();
		createUser();
		modifyUser();
		ninePatch();
		if (ParseUtil.listNotNull(clickViewList)) {
			for (GridItem gridItem : clickViewList) {
				int _p = position * oneLineCount + gridItem.getIndex();
				if (list.size() > _p)
					registerClick(gridItem.getView(), list.get(_p), articleType);
			}
		}
	}

	/**
	 * 内容frame
	 */
	private void frameContent() {
		if (!gridMap.containsKey(FunctionXML.FRAME_CONTENT))
			return;
		List<GridItem> gridList = gridMap.get(FunctionXML.FRAME_CONTENT);
		if (!ParseUtil.listNotNull(gridList))
			return;
		for (GridItem gridItem : gridList) {
			int _p = position * oneLineCount + gridItem.getIndex();
			gridItem.getView().setVisibility(
					list.size() > _p ? View.VISIBLE : View.INVISIBLE);
		}
	}

	/**
	 * 栏目标题栏
	 */
	private void titleBar() {
		if (!gridMap.containsKey(FunctionXML.TITLEBAR))
			return;
		List<GridItem> gridList = gridMap.get(FunctionXML.TITLEBAR);
		if (!ParseUtil.listNotNull(gridList))
			return;
		for (GridItem gridItem : gridList) {
			int _p = position * oneLineCount + gridItem.getIndex();
			if (list.size() <= _p)
				continue;
			ArticleItem item = list.get(_p);
			View view = gridItem.getView();
			view.setVisibility(item.isShowTitleBar() ? View.VISIBLE : View.GONE);
			if (view.getTag(R.id.date_format) instanceof String)
				continue;
			if (ParseUtil.mapContainsKey(DataHelper.columnColorMap,
					item.getGroupname())) {
				view.setBackgroundColor(DataHelper.columnColorMap.get(item
						.getGroupname()));
			}
			if (view instanceof TextView) {
				if (ParseUtil.mapContainsKey(DataHelper.columnTitleMap,
						item.getGroupname())) {
					((TextView) view).setText(DataHelper.columnTitleMap
							.get(item.getGroupname()));
				}
			}
		}
	}

	/**
	 * 时间标题栏
	 */
	private void titlebarDate() {
		if (!gridMap.containsKey(FunctionXML.TITLEBAR))
			return;
		List<GridItem> gridList = gridMap.get(FunctionXML.TITLEBAR);
		if (!ParseUtil.listNotNull(gridList))
			return;
		for (GridItem gridItem : gridList) {
			int _p = position * oneLineCount + gridItem.getIndex();
			if (list.size() <= _p)
				continue;
			ArticleItem item = list.get(_p);
			View view = gridItem.getView();
			view.setVisibility(item.isShowTitleBar() ? View.VISIBLE : View.GONE);
			if (!(view.getTag(R.id.date_format) instanceof String))
				continue;
			TextView textView = null;
			if (view instanceof TextView)
				textView = (TextView) view;
			if (textView == null)
				continue;
			if (item.isShowTitleBar()) {
				textView.setVisibility(View.VISIBLE);
				textView.setText(DateFormatTool.format(ParseUtil.stol(item
						.getInputtime()) * 1000L, view.getTag(R.id.date_format)
						.toString()));
			} else {
				textView.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 列表图片
	 */
	private void img() {
		if (!gridMap.containsKey(FunctionXML.IMAGE))
			return;
		List<GridItem> gridList = gridMap.get(FunctionXML.IMAGE);
		if (!ParseUtil.listNotNull(gridList))
			return;
		for (GridItem gridItem : gridList) {
			int _p = position * oneLineCount + gridItem.getIndex();
			if (list.size() <= _p)
				continue;
			ArticleItem item = list.get(_p);
			View view = gridItem.getView();
			if (!(view instanceof ImageView))
				continue;
			ImageView imageView = (ImageView) view;
			if (imageView.getTag(R.id.img_placeholder) instanceof String) {
				// TODO 占位图
				imageView.setScaleType(ScaleType.CENTER);
				V.setImage(imageView, imageView.getTag(R.id.img_placeholder)
						.toString());
			} else {
				imageView.setImageBitmap(null);
			}
			if (!adapter.isScroll()) {
				if (item.isAdv()) {
					AdvSource advPic = item.getAdvSource();
					if (advPic != null) {
						CommonApplication.finalBitmap.display(imageView,
								advPic.getUrl());
						continue;
					}
				}
				// TODO 下载图片
				boolean usePicture = false;
				if (imageView.getTag(R.id.img_use) instanceof String) {
					String use = imageView.getTag(R.id.img_use).toString();
					usePicture = !TextUtils.equals("thumb", use);
				}
				adapter.downImage(item, imageView, usePicture);
			}
		}
	}

	/**
	 * 标题
	 */
	private void title() {
		if (!gridMap.containsKey(FunctionXML.TITLE))
			return;
		List<GridItem> gridList = gridMap.get(FunctionXML.TITLE);
		if (!ParseUtil.listNotNull(gridList))
			return;
		for (GridItem gridItem : gridList) {
			int _p = position * oneLineCount + gridItem.getIndex();
			if (list.size() <= _p)
				continue;
			ArticleItem item = list.get(_p);
			View view = gridItem.getView();
			if (!(view instanceof TextView))
				continue;
			TextView title = (TextView) view;
			title.setText(item.getTitle());
			if (V.articleIsReaded(item)) {
				if (title.getTag(R.id.title_readed_color) instanceof String) {
					String color = title.getTag(R.id.title_readed_color)
							.toString();
					if (color.startsWith("#"))
						title.setTextColor(Color.parseColor(color));
				}
			} else {
				if (title.getTag(R.id.title_default_color) instanceof String) {
					String color = title.getTag(R.id.title_default_color)
							.toString();
					if (color.startsWith("#"))
						title.setTextColor(Color.parseColor(color));
				}
			}
		}
	}

	/**
	 * 描述
	 */
	private void desc() {
		if (!gridMap.containsKey(FunctionXML.DESC))
			return;
		List<GridItem> gridList = gridMap.get(FunctionXML.DESC);
		if (!ParseUtil.listNotNull(gridList))
			return;
		for (GridItem gridItem : gridList) {
			int _p = position * oneLineCount + gridItem.getIndex();
			if (list.size() <= _p)
				continue;
			ArticleItem item = list.get(_p);
			View view = gridItem.getView();
			if (!(view instanceof TextView))
				continue;
			TextView desc = (TextView) view;
			desc.setText(item.getDesc());
		}
	}

	/**
	 * 广告 test
	 */
	private void adv() {
		// if (item.isAdv()) {
		// // TODO 广告
		// if (map.containsKey(XMLFunction.FRAME_CONTENT)) {
		// map.get(XMLFunction.FRAME_CONTENT).setVisibility(View.GONE);
		// }
		// if (map.containsKey(XMLFunction.ADV_IMAGE)) {
		// View view = map.get(XMLFunction.ADV_IMAGE);
		// view.setVisibility(View.VISIBLE);
		// if (view instanceof ImageView) {
		// ImageView imageView = (ImageView) view;
		// imageView.getLayoutParams().width = CommonApplication.width;
		// AdvSource advPic = item.getAdvSource();
		// if (advPic != null && advPic.getWidth() > 0) {
		// imageView.getLayoutParams().height = advPic.getHeight()
		// * CommonApplication.width / advPic.getWidth();
		// CommonApplication.finalBitmap.display(imageView,
		// advPic.getUrl());
		// }
		// }
		// }
		// } else {
		// // TODO 非广告
		// if (map.containsKey(XMLFunction.FRAME_CONTENT)) {
		// map.get(XMLFunction.FRAME_CONTENT).setVisibility(View.VISIBLE);
		// }
		// if (map.containsKey(XMLFunction.ADV_IMAGE)) {
		// map.get(XMLFunction.ADV_IMAGE).setVisibility(View.GONE);
		// }
		// }
	}

	/**
	 * 视频图片
	 * 
	 * @param item
	 */
	private void video() {
		if (!gridMap.containsKey(FunctionXML.VIDEO_IMG))
			return;
		List<GridItem> gridList = gridMap.get(FunctionXML.VIDEO_IMG);
		if (!ParseUtil.listNotNull(gridList))
			return;
		for (GridItem gridItem : gridList) {
			int _p = position * oneLineCount + gridItem.getIndex();
			if (list.size() <= _p)
				continue;
			ArticleItem item = list.get(_p);
			gridItem.getView().setVisibility(
					item.getProperty().getType() == 3 ? View.VISIBLE
							: View.GONE);
		}
	}

	/**
	 * outline
	 */
	private void outline() {
		if (!gridMap.containsKey(FunctionXML.OUTLINE))
			return;
		List<GridItem> gridList = gridMap.get(FunctionXML.OUTLINE);
		if (!ParseUtil.listNotNull(gridList))
			return;
		for (GridItem gridItem : gridList) {
			int _p = position * oneLineCount + gridItem.getIndex();
			if (list.size() <= _p)
				continue;
			ArticleItem item = list.get(_p);
			View view = gridItem.getView();
			if (!(view instanceof TextView))
				continue;
			TextView outline = (TextView) view;
			outline.setText(item.getOutline());
		}
	}

	/**
	 * tag
	 */
	private void tag() {
		if (!gridMap.containsKey(FunctionXML.TAG))
			return;
		List<GridItem> gridList = gridMap.get(FunctionXML.TAG);
		if (!ParseUtil.listNotNull(gridList))
			return;
		for (GridItem gridItem : gridList) {
			int _p = position * oneLineCount + gridItem.getIndex();
			if (list.size() <= _p)
				continue;
			ArticleItem item = list.get(_p);
			View view = gridItem.getView();
			if (!(view instanceof TextView))
				continue;
			TextView tag = (TextView) view;
			tag.setText(item.getTag());
		}
	}

	/**
	 * sub_title
	 */
	private void subTitle() {
		if (!gridMap.containsKey(FunctionXML.SUB_TITLE))
			return;
		List<GridItem> gridList = gridMap.get(FunctionXML.SUB_TITLE);
		if (!ParseUtil.listNotNull(gridList))
			return;
		for (GridItem gridItem : gridList) {
			int _p = position * oneLineCount + gridItem.getIndex();
			if (list.size() <= _p)
				continue;
			ArticleItem item = list.get(_p);
			View view = gridItem.getView();
			if (!(view instanceof TextView))
				continue;
			TextView tag = (TextView) view;
			tag.setText(item.getSubtitle());
		}
	}

	/**
	 * create_user
	 */
	private void createUser() {
		if (!gridMap.containsKey(FunctionXML.CREATE_USER))
			return;
		List<GridItem> gridList = gridMap.get(FunctionXML.CREATE_USER);
		if (!ParseUtil.listNotNull(gridList))
			return;
		for (GridItem gridItem : gridList) {
			int _p = position * oneLineCount + gridItem.getIndex();
			if (list.size() <= _p)
				continue;
			ArticleItem item = list.get(_p);
			View view = gridItem.getView();
			if (!(view instanceof TextView))
				continue;
			TextView tag = (TextView) view;
			tag.setText(item.getCreateuser());
		}
	}

	/**
	 * modify_user
	 */
	private void modifyUser() {
		if (!gridMap.containsKey(FunctionXML.MODIFY_USER))
			return;
		List<GridItem> gridList = gridMap.get(FunctionXML.MODIFY_USER);
		if (!ParseUtil.listNotNull(gridList))
			return;
		for (GridItem gridItem : gridList) {
			int _p = position * oneLineCount + gridItem.getIndex();
			if (list.size() <= _p)
				continue;
			ArticleItem item = list.get(_p);
			View view = gridItem.getView();
			if (!(view instanceof TextView))
				continue;
			TextView tag = (TextView) view;
			tag.setText(item.getModifyuser());
		}
	}

	/**
	 * 日期
	 */
	private void date() {
		if (!gridMap.containsKey(FunctionXML.DATE))
			return;
		List<GridItem> gridList = gridMap.get(FunctionXML.DATE);
		if (!ParseUtil.listNotNull(gridList))
			return;
		for (GridItem gridItem : gridList) {
			int _p = position * oneLineCount + gridItem.getIndex();
			if (list.size() <= _p)
				continue;
			ArticleItem item = list.get(_p);
			View view = gridItem.getView();
			if (!(view instanceof TextView))
				continue;
			if (!(view.getTag(R.id.date_format) instanceof String))
				continue;
			TextView date = (TextView) view;
			String language = view.getTag(R.id.date_format_language) == null ? ""
					: view.getTag(R.id.date_format_language).toString();
			date.setText(DateFormatTool.format(
					ParseUtil.stol(item.getInputtime()) * 1000L,
					view.getTag(R.id.date_format).toString(), language));
		}
	}

	/**
	 * 收藏
	 */
	private void fav() {
		if (!gridMap.containsKey(FunctionXML.FAV))
			return;
		List<GridItem> gridList = gridMap.get(FunctionXML.FAV);
		if (!ParseUtil.listNotNull(gridList))
			return;
		for (GridItem gridItem : gridList) {
			int _p = position * oneLineCount + gridItem.getIndex();
			if (list.size() <= _p)
				continue;
			final ArticleItem item = list.get(_p);
			View view = gridItem.getView();
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ModernMediaTools.addFav(mContext, item,
							UserTools.getUid(mContext),
							new BindFavToUserImplement(mContext));
					changeFav(v, item);
				}
			});
		}
	}

	/**
	 * 改变收藏背景图
	 * 
	 * @param view
	 * @param item
	 */
	private void changeFav(View view, ArticleItem item) {
		boolean faved = NewFavDb.getInstance(mContext).containThisFav(
				item.getArticleId(), UserTools.getUid(mContext));
		if (faved) {
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
	private void registerClick(final View view, final ArticleItem item,
			final ArticleType articleType) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				XMLDataSetForGrid.this.onClick(view, item, articleType);
			}
		});
	}
}
