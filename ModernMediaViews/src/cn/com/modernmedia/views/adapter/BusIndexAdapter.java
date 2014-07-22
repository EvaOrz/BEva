package cn.com.modernmedia.views.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.adapter.ViewHolder;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.model.IndexListParm;
import cn.com.modernmedia.views.util.V;

/**
 * 商周类型首页适配器
 * 
 * @author user
 * 
 */
public class BusIndexAdapter extends BaseIndexAdapter {

	public BusIndexAdapter(Context context, IndexListParm parm) {
		super(context, parm);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		boolean convertViewIsNull = convertView == null;
		final ArticleItem item = getItem(position);
		ViewHolder holder = ViewHolder.get(mContext, convertView,
				R.layout.index_item_bus, 15);
		View contain = holder.getView(R.id.bus_index_item_contain);
		TextView column = holder.getView(R.id.bus_index_item_column_title);
		TextView title = holder.getView(R.id.bus_index_item_title);
		TextView desc = holder.getView(R.id.bus_index_item_desc);
		TextView more = holder.getView(R.id.bus_index_item_more);
		ImageView image = holder.getView(R.id.bus_index_item_img);
		ImageView right = holder.getView(R.id.bus_index_item_rightRow);
		ImageView advImg = holder.getView(R.id.bus_index_item_adv_img);
		ImageView moreImg = holder.getView(R.id.bus_index_item_more_img);
		ImageView divider = holder.getView(R.id.bus_index_item_divider);
		View moreRl = holder.getView(R.id.bus_index_item_more_rl);
		View content = holder.getView(R.id.bus_index_item_content);

		if (convertViewIsNull) {
			initRes(moreImg, divider, image);
			setItemBg(contain);
		} else {
			desc.setText("");
		}
		setPlaceHolder(image);
		column.setVisibility(item.isShowTitleBar() ? View.VISIBLE : View.GONE);
		if (item.isShowMoreCat()) {
			moreRl.setVisibility(View.VISIBLE);
			more.setText(ParseUtil.parseString(mContext, R.string.more_cat,
					DataHelper.columnTitleMap.get(item.getCatId())));
		} else {
			moreRl.setVisibility(View.GONE);
		}
		// is adv?
		content.setVisibility(item.isAdv() ? View.GONE : View.VISIBLE);
		advImg.setVisibility(item.isAdv() ? View.VISIBLE : View.GONE);
		if (item.isAdv()) {
			advImg.getLayoutParams().width = CommonApplication.width;
			AdvSource advPic = item.getAdvSource();
			if (advPic != null && advPic.getWidth() > 0) {
				advImg.getLayoutParams().height = advPic.getHeight()
						* CommonApplication.width / advPic.getWidth();
				CommonApplication.finalBitmap.display(advImg, advPic.getUrl());
			}
		}
		if (hasInitDesc(position)) {
			desc.setText(item.getDesc());
		}
		if (!isScroll) {
			if (!hasInitDesc(position)) {
				desc.setText(item.getDesc());
				addDesc(position);
			}
			downImage(item, image);
		}
		title.setText(item.getTitle());
		if (isReaded(item.getArticleId())) {
			title.setTextColor(Color.parseColor("#ff787878"));
		} else {
			title.setTextColor(Color.BLACK);
		}
		if (ConstData.getInitialAppId() == 1) {
			V.setIndexItemButtonImg(right, item);
		} else {
			right.setVisibility(View.GONE);
		}
		if (ParseUtil
				.mapContainsKey(DataHelper.columnColorMap, item.getCatId())) {
			column.setBackgroundColor(DataHelper.columnColorMap.get(item
					.getCatId()));
		}
		if (ParseUtil
				.mapContainsKey(DataHelper.columnTitleMap, item.getCatId())) {
			column.setText(DataHelper.columnTitleMap.get(item.getCatId()));
		}
		moreRl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clickMore(item);
			}
		});
		return holder.getConvertView();
	}

	private void initRes(ImageView moreImg, ImageView divider, ImageView image) {
		if (!TextUtils.isEmpty(parm.getMoreinstant())) {
			V.setImage(moreImg, parm.getMoreinstant());
		}
		V.setImage(divider, parm.getDivider());
		setViewBack(image);
	}

	/**
	 * 点击"更多。。"
	 */
	private void clickMore(ArticleItem item) {
		if (mContext instanceof ViewsMainActivity) {
			int catId = item.getCatId();
			if (DataHelper.soloCatMap.containsKey(catId)) {
				if (ConstData.isIndexPager())
					((ViewsMainActivity) mContext)
							.clickItemIfPager(DataHelper.soloCatMap.get(catId));
				else if (CommonApplication.manage != null)
					CommonApplication.manage.getProcess().showSoloChildCat(
							DataHelper.soloCatMap.get(catId), true);
			} else {
				if (ConstData.isIndexPager())
					((ViewsMainActivity) mContext).clickItemIfPager(catId);
				else
					((ViewsMainActivity) mContext).showChildCat(catId);
			}
			if (DataHelper.columnTitleMap != null
					&& DataHelper.columnTitleMap.containsKey(item.getCatId())) {
				((ViewsMainActivity) mContext)
						.setIndexTitle(DataHelper.columnTitleMap.get(item
								.getCatId()));
			}
			((ViewsMainActivity) mContext).notifyColumnAdapter(catId);
			LogHelper.logAndroidTouchMorenews();
		}
	}
}
