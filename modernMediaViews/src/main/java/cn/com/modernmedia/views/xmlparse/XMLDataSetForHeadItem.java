package cn.com.modernmedia.views.xmlparse;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.Picture;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.WeeklyLogEvent;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.widget.LoadingImage;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 为headview item设置数据
 * 
 * @author zhuqiao
 * 
 */
public class XMLDataSetForHeadItem extends BaseXMLDataSet {
	private int position;

	public XMLDataSetForHeadItem(Context context, HashMap<String, View> map,
			List<View> clickViewList, List<View> ninePatchViewList) {
		super(context, map, clickViewList, ninePatchViewList);
	}

	/**
	 * 设置数据
	 * 
	 * @param item
	 * @param position
	 * @param articleType
	 */
	public void setData(ArticleItem item, int position, ArticleType articleType) {
		this.position = position;
		if (map == null || map.isEmpty() || item == null)
			return;
		image(item);
		title(item, null);
		desc(item, 0, null);
		video(item);
		audio();
		outline(item, 0, null);
		tag(item, 0, null);
		date(item);
		fav(item);
		subTitle(item, 0, null);
		createUser(item, 0, null);
		modifyUser(item, 0, null);
		ninePatch();
		registerClick(item, articleType);
	}

	/**
	 * 图片
	 * 
	 * @param item
	 */
	private void image(ArticleItem item) {
		if (!ParseUtil.mapContainsKey(map, FunctionXML.IMAGE))
			return;
		View view = map.get(FunctionXML.IMAGE);

		String url = "";
		if (item != null) {
			if (item.getAdvSource() != null) {
				// 广告
				url = item.getAdvSource().getUrl();
			}
			if (TextUtils.isEmpty(url)) {
				List<Picture> list = item.getPicList();
				if (ParseUtil.listNotNull(list)) {
					url = list.get(0).getUrl();
				}
			}
		}

		if (view instanceof ImageView) {
			img(url, (ImageView) view);
		} else if (view instanceof LoadingImage) {
			img(url, (LoadingImage) view);
		}
	}

	private void img(String url, ImageView imageView) {
		if (imageView.getTag(R.id.img_placeholder) instanceof String) {
			// TODO 占位图
			imageView.setScaleType(ScaleType.CENTER);
			V.setImage(imageView, imageView.getTag(R.id.img_placeholder)
					.toString());
		}
		CommonApplication.finalBitmap.display(imageView, url);
	}

	private void img(String url, LoadingImage loadingImage) {
		loadingImage.setUrl(url);
	}

	@Override
	protected void log(ArticleItem item) {
		super.log(item);
		LogHelper.logAndroidTouchHeadline(mContext, position);
		if (ConstData.getInitialAppId() == 20) {
			WeeklyLogEvent.logAndroidColumnHeadviewClickCount(mContext);
		}
	}

}
