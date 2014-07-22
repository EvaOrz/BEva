package cn.com.modernmedia.views.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.adapter.ViewHolder;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.model.IndexListParm;
import cn.com.modernmedia.views.util.V;

/**
 * 艺术新闻类型首页适配器
 * 
 * @author user
 * 
 */
public class TancIndexAdapter extends BaseIndexAdapter {
	private static final int MAX = 200 * CommonApplication.height / 1280;
	private View firstItemView;
	private int itemStartY = 0;
	private int alpha = 0;

	public TancIndexAdapter(Context context, IndexListParm parm) {
		super(context, parm);
		initImageHeight(mContext.getResources().getDimensionPixelSize(R.dimen.dp10));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		boolean convertViewIsNull = convertView == null;
		final ArticleItem item = getItem(position);
		ViewHolder holder = ViewHolder.get(mContext, convertView,
				R.layout.index_item_tanc);
		View frame = holder.getView(R.id.tanc_index_item_rl);
		View advFrame = holder.getView(R.id.tanc_ndex_item_adv_ll);
		ImageView img = holder.getView(R.id.tanc_index_item_img);
		ImageView advImg = holder.getView(R.id.tanc_index_item_adv_img);
		View line = holder.getView(R.id.tanc_index_item_line);
		View advLine = holder.getView(R.id.tanc_index_item_adv_line);
		View row = holder.getView(R.id.tanc_index_item_row);
		TextView title = holder.getView(R.id.tanc_index_item_title);
		TextView desc = holder.getView(R.id.tanc_index_item_desc);

		if (convertViewIsNull) {
			img.getLayoutParams().height = imageHeight;
			V.setImage(line, parm.getDivider());
			V.setImage(advLine, parm.getDivider());
			V.setImage(row, parm.getRow());
		} else {
			desc.setText("");
			setPlaceHolder(img);
			img.setScaleType(ScaleType.CENTER);
			setPlaceHolder(advImg);
			advImg.setScaleType(ScaleType.CENTER);
		}

		if (item.isAdv()) {
			advFrame.setVisibility(View.VISIBLE);
			frame.setVisibility(View.GONE);
			advImg.getLayoutParams().width = CommonApplication.width;
			AdvSource advPic = item.getAdvSource();
			if (advPic != null && advPic.getWidth() > 0) {
				advImg.getLayoutParams().height = advPic.getHeight()
						* CommonApplication.width / advPic.getWidth();
				CommonApplication.finalBitmap.display(advImg, advPic.getUrl());
			}
		} else {
			advFrame.setVisibility(View.GONE);
			frame.setVisibility(View.VISIBLE);
			if (hasInitDesc(position)) {
				setDesc(desc, item, position);
			}
			if (!isScroll) {
				if (!hasInitDesc(position)) {
					setDesc(desc, item, position);
				}
				downImage(item, img);
			}
			if (TextUtils.isEmpty(item.getTitle())) {
				title.setVisibility(View.GONE);
			} else {
				title.setVisibility(View.VISIBLE);
				title.setText(item.getTitle());
			}
			if (isReaded(item.getArticleId()))
				title.setTextColor(Color.parseColor("#A0A1A0"));
			else
				title.setTextColor(Color.parseColor("#323232"));
			if (position == 0) {
				firstItemView = holder.getConvertView();
			}
		}
		return holder.getConvertView();
	}

	private void setDesc(TextView desc, ArticleItem item, int position) {
		if (TextUtils.isEmpty(item.getDesc())) {
			desc.setVisibility(View.GONE);
		} else {
			desc.setVisibility(View.VISIBLE);
			desc.setText(item.getDesc());
		}
		addDesc(position);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			isScroll = false;
			this.notifyDataSetChanged();
		} else if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
			isScroll = false;
		} else if (scrollState == SCROLL_STATE_FLING) {
			isScroll = true;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (itemStartY == 0) {
			itemStartY = getTop();
		}
		int scroll = Math.abs(getTop() - itemStartY);
		scroll = scroll > MAX ? MAX : scroll;
		alpha = scroll * 255 / MAX;
		((ViewsMainActivity) mContext).setShadowAlpha(alpha);
	}

	private int getTop() {
		if (firstItemView != null) {
			int arr[] = new int[2];
			firstItemView.getLocationOnScreen(arr);
			return arr[1];
		}
		return 0;
	}
}
