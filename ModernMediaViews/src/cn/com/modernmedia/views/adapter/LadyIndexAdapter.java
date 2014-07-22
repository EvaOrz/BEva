package cn.com.modernmedia.views.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.adapter.ViewHolder;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.model.IndexListParm;

/**
 * iLady类型首页适配器
 * 
 * @author user
 * 
 */
public class LadyIndexAdapter extends BaseIndexAdapter {
	private int titleFrameWidth, titleFrameHeight;

	public LadyIndexAdapter(Context context, IndexListParm parm) {
		super(context, parm);
		initImageHeight(0);
		titleFrameWidth = CommonApplication.width * 470 / 640;
		titleFrameHeight = CommonApplication.width * 130 / 640;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		boolean convertViewIsNull = convertView == null;
		final ArticleItem item = getItem(position);
		ViewHolder holder = ViewHolder.get(mContext, convertView,
				R.layout.index_item_lady);
		ImageView imageView = holder.getView(R.id.lady_index_item_image);
		TextView title = holder.getView(R.id.lady_index_item_title);
		TextView desc = holder.getView(R.id.lady_index_item_desc);
		View frame = holder.getView(R.id.lady_index_item_rl);
		ImageView advImage = holder.getView(R.id.lady_index_item_adv_img);
		View titleFrame = holder.getView(R.id.lady_index_item_title_frame);
		View margin = holder.getView(R.id.lady_index_item_magin);

		if (convertViewIsNull) {
			frame.getLayoutParams().height = imageHeight;
			titleFrame.getLayoutParams().width = titleFrameWidth;
			titleFrame.getLayoutParams().height = titleFrameHeight;
			setViewBack(imageView);
			setViewBack(advImage);
		}
		setPlaceHolder(imageView);
		imageView.setScaleType(ScaleType.CENTER);
		setPlaceHolder(advImage);
		advImage.setScaleType(ScaleType.CENTER);

		margin.setVisibility(parm.getItem_show_margin() == 1 ? View.VISIBLE
				: View.GONE);
		if (item.isAdv()) {
			advImage.setVisibility(View.VISIBLE);
			frame.setVisibility(View.GONE);
			advImage.getLayoutParams().width = CommonApplication.width;
			AdvSource advPic = item.getAdvSource();
			if (advPic != null && advPic.getWidth() > 0) {
				advImage.getLayoutParams().height = advPic.getHeight()
						* CommonApplication.width / advPic.getWidth();
				CommonApplication.finalBitmap
						.display(advImage, advPic.getUrl());
			}
		} else {
			advImage.setVisibility(View.GONE);
			frame.setVisibility(View.VISIBLE);
			downImage(item, imageView);
			titleFrame.setVisibility(View.VISIBLE);
			setDesc(desc, item);
			if (TextUtils.isEmpty(item.getTitle())) {
				title.setVisibility(View.GONE);
			} else {
				title.setVisibility(View.VISIBLE);
				title.setText(item.getTitle());
			}
		}
		return holder.getConvertView();
	}

	private void setDesc(TextView desc, ArticleItem item) {
		if (TextUtils.isEmpty(item.getDesc())) {
			desc.setVisibility(View.GONE);
		} else {
			desc.setVisibility(View.VISIBLE);
			desc.setText(item.getDesc());
		}
	}
}
