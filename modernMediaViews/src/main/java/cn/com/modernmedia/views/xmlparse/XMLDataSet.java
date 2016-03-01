package cn.com.modernmedia.views.xmlparse;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.index.adapter.BaseIndexAdapter;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.unit.DateFormatTool;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 设置数据
 * 
 * @author zhuqiao
 * 
 */
public class XMLDataSet extends BaseXMLDataSet {
	private BaseIndexAdapter adapter;

	public XMLDataSet(Context context, HashMap<String, View> map,
			List<View> clickViewList, List<View> ninePatchViewList,
			BaseIndexAdapter adapter) {
		super(context, map, clickViewList, ninePatchViewList);
		this.adapter = adapter;
	}

	/**
	 * 设置数据
	 * 
	 * @param item 
	 */
	public void setData(ArticleItem item, int position, ArticleType articleType) {
		if (map == null || map.isEmpty() || item == null || adapter == null)
			return;
		if (item.isShowTitleBar() && item.getPosition().getStyle() == 0
				&& map.containsKey(FunctionXML.TITLEBAR)) {
			View view = map.get(FunctionXML.TITLEBAR);
			if (view.getTag(R.id.date_format) instanceof String) {
				// TODO 显示日期
				titlebarDate(item);
			} else {
				titleBar(item);
			}
		}
		img(item);
		title(item, adapter);
		desc(item, position, adapter);
		adv(item);
		video(item);
		videoview(item, adapter);
		audio();
		outline(item, position, adapter);
		tag(item, position, adapter);
		row(item);
		date(item);
		fav(item);
		subTitle(item, position, adapter);
		createUser(item, position, adapter);
		modifyUser(item, position, adapter);
		ninePatch();
		imageforGroup(item);// 组图模式初始化
		registerClick(item, articleType);
	}

	/**
	 * 组图模式，三张图，style=2
	 */
	private void imageforGroup(ArticleItem item) {
		if (!(map.containsKey(FunctionXML.IMAGE_1)
				&& map.containsKey(FunctionXML.IMAGE_2) && map
					.containsKey(FunctionXML.IMAGE_3)))
			return;
		View view1 = map.get(FunctionXML.IMAGE_1);
		View view2 = map.get(FunctionXML.IMAGE_2);
		View view3 = map.get(FunctionXML.IMAGE_3);
		if (!(view1 instanceof ImageView && view2 instanceof ImageView && view3 instanceof ImageView))
			return;
		ImageView imageView1 = (ImageView) view1;
		ImageView imageView2 = (ImageView) view2;
		ImageView imageView3 = (ImageView) view3;
		Log.e("组图模式第一张图", item.getPicList().get(0).getUrl());
		V.setImage(imageView1, item.getPicList().get(0).getUrl());
		V.setImage(imageView2, item.getPicList().get(1).getUrl());
		V.setImage(imageView3, item.getPicList().get(2).getUrl());
	}

	/**
	 * 栏目标题栏
	 */
	private void titleBar(ArticleItem item) {
		if (!(map.containsKey(FunctionXML.TITLEBAR)
				&& map.containsKey(FunctionXML.TITLEBAR_1) && map
					.containsKey(FunctionXML.TITLEBAR_2)))
			return;
		View view = map.get(FunctionXML.TITLEBAR);
		View view1 = map.get(FunctionXML.TITLEBAR_1);
		View view2 = map.get(FunctionXML.TITLEBAR_2);

		if (!(view instanceof TextView))
			return;
		TextView textView = (TextView) view;
		if (item.getInputtime().equals(
				mContext.getString(R.string.theme_magazine))) {
			textView.setBackgroundColor(Color.RED);
			textView.setText(item.getInputtime());
			return;
		}
		// view.setBackgroundColor(item.getGroupdisplaycolor());
		view1.setBackgroundColor(item.getGroupdisplaycolor());
		view2.setBackgroundColor(item.getGroupdisplaycolor());
		textView.setText(item.getGroupdisplayname());
		textView.setTextColor(item.getGroupdisplaycolor());
	}

	/**
	 * 时间标题栏
	 */
	private void titlebarDate(ArticleItem item) {
		if (!map.containsKey(FunctionXML.TITLEBAR))
			return;
		View view = map.get(FunctionXML.TITLEBAR);
		if (!(view instanceof TextView))
			return;
		TextView textView = (TextView) view;
		if (item.getInputtime().equals(
				mContext.getString(R.string.theme_magazine))) {
			textView.setText(mContext.getString(R.string.theme_magazine));
		} else {
			String language = view.getTag(R.id.date_format_language) == null ? ""
					: view.getTag(R.id.date_format_language).toString();
			textView.setText(DateFormatTool.format(
					ParseUtil.stol(item.getInputtime()) * 1000L,
					view.getTag(R.id.date_format).toString(), language));
		}
	}

	/**
	 * 列表图片
	 */
	private void img(ArticleItem item) {
		if (!map.containsKey(FunctionXML.IMAGE))
			return;
		View view = map.get(FunctionXML.IMAGE);
		if (!(view instanceof ImageView))
			return;
		ImageView imageView = (ImageView) view;
		if (imageView.getTag(R.id.img_placeholder) instanceof String) {
			// TODO 占位图
			imageView.setScaleType(ScaleType.CENTER);
			V.setImage(imageView, imageView.getTag(R.id.img_placeholder)
					.toString());
		} else {
			if (ConstData.getAppId() == 20) {
				imageView.setImageResource(R.drawable.white_bg);
			} else {
				imageView.setImageBitmap(null);
			}
		}
		if (!adapter.isScroll()) {
			// TODO 下载图片
			boolean usePicture = true;
			if (imageView.getTag(R.id.img_use) instanceof String) {
				String use = imageView.getTag(R.id.img_use).toString();
				usePicture = !TextUtils.equals("thumb", use);
			}
			adapter.downImage(item, imageView, usePicture);
		}
	}

	/**
	 * 广告
	 */
	private void adv(ArticleItem item) {
		if (item.isAdv()) {
			// TODO 广告
			// if (map.containsKey(FunctionXML.FRAME_CONTENT)) {
			// map.get(FunctionXML.FRAME_CONTENT).setVisibility(View.GONE);
			// }
			// if (map.containsKey(FunctionXML.ADV_CONTENT)) {
			// map.get(FunctionXML.ADV_CONTENT).setVisibility(View.VISIBLE);
			// }
			if (map.containsKey(FunctionXML.ADV_IMAGE)) {
				View view = map.get(FunctionXML.ADV_IMAGE);
				if (view instanceof ImageView) {
					ImageView imageView = (ImageView) view;
					imageView.getLayoutParams().width = CommonApplication.width;
					AdvSource advPic = item.getAdvSource();
					if (advPic != null && advPic.getWidth() > 0) {
						int height = advPic.getHeight()
								* CommonApplication.width / advPic.getWidth();
						imageView.getLayoutParams().height = height;
						imageView.setScaleType(ScaleType.CENTER);
						V.setImage(imageView, "placeholder");
						CommonApplication.finalBitmap.display(imageView,
								advPic.getUrl());
					}
				}
			}
		} else {
			// // TODO 非广告
			// if (map.containsKey(FunctionXML.FRAME_CONTENT)) {
			// map.get(FunctionXML.FRAME_CONTENT).setVisibility(View.VISIBLE);
			// }【
			// if (map.containsKey(FunctionXML.ADV_CONTENT)) {
			// map.get(FunctionXML.ADV_CONTENT).setVisibility(View.GONE);
			// }
			// if (map.containsKey(FunctionXML.ADV_IMAGE)) {
			// map.get(FunctionXML.ADV_IMAGE).setVisibility(View.GONE);
			// }
		}
	}

	/**
	 * 箭头
	 * 
	 * @param item
	 */
	private void row(ArticleItem item) {
		if (!map.containsKey(FunctionXML.ROW))
			return;
		View view = map.get(FunctionXML.ROW);
		if (!(view instanceof ImageView))
			return;
		V.setIndexItemButtonImg((ImageView) view, item);
	}

}
