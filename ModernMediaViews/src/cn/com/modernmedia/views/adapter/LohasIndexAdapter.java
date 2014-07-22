package cn.com.modernmedia.views.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.adapter.ViewHolder;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.model.IndexListParm;
import cn.com.modernmedia.views.util.V;

/**
 * iLohas类型首页适配器
 * 
 * @author user
 * 
 */
public class LohasIndexAdapter extends BaseIndexAdapter {
	private int titleBarHeight;

	public LohasIndexAdapter(Context context, IndexListParm parm) {
		super(context, parm);
		initImageHeight(0);
		setImageHeight();
	}

	/**
	 * 设置显示的图片的高度
	 */
	private void setImageHeight() {
		String barHeight = parm.getItem_title_bar_height();
		if (!TextUtils.isEmpty(barHeight)) {
			String[] value = barHeight.split(",");
			if (value != null && value.length == 2) {
				titleBarHeight = ParseUtil.stoi(value[1], 0) * imageHeight
						/ ParseUtil.stoi(value[0], 1);
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		boolean convertViewIsNull = convertView == null;
		final ArticleItem item = getItem(position);
		ViewHolder holder = ViewHolder.get(mContext, convertView,
				R.layout.index_item_lohas);
		View frame = holder.getView(R.id.lohas_index_item_rl);
		ImageView imageView = holder.getView(R.id.lohas_index_item_image);
		ImageView advImage = holder.getView(R.id.lohas_index_item_adv_img);
		View titleFrame = holder.getView(R.id.lohas_index_item_tilte_layout);
		TextView title = holder.getView(R.id.lohas_index_item_title);
		TextView date = holder.getView(R.id.lohas_index_item_date);
		ImageView fav = holder.getView(R.id.lohas_index_item_fav);

		if (convertViewIsNull) {
			frame.getLayoutParams().height = imageHeight;
			titleFrame.getLayoutParams().height = titleBarHeight;
			// TODO title背景色, 当前默认是白色
			V.setImage(titleFrame, parm.getItem_title_bar_bg());
			// image 背景
			setViewBack(imageView);
			setViewBack(advImage);
		}
		setPlaceHolder(imageView);
		imageView.setScaleType(ScaleType.CENTER);
		setPlaceHolder(advImage);
		advImage.setScaleType(ScaleType.CENTER);
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
			if (parm.getItem_show_fav() == 1) { // 显示收藏图片
				fav.setVisibility(View.VISIBLE);
				String uid = ((ViewsMainActivity) mContext).getUid();
				if (isFaved(ModernMediaTools.fecthSlateArticleId(item), uid))
					V.setImage(fav, parm.getItem_faved());
				else
					V.setImage(fav, parm.getItem_fav());
				fav.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (mContext instanceof ViewsMainActivity) {
							((ViewsMainActivity) mContext).addFav(item,
									isSoloAdapter());
						}
					}
				});
			} else {
				fav.setVisibility(View.GONE);
			}
			// 图片标题
			title.setText(item.getTitle());
			// 文章日期(inputtime决定)
			if (!TextUtils.isEmpty(item.getInputtime())) {
				V.setImage(date, parm.getItem_date_bg());
				date.setText(getDate(item.getInputtime()));
				Typeface font = Typeface
						.create(Typeface.SERIF, Typeface.NORMAL);
				date.getPaint().setTypeface(font);
			}
		}
		return holder.getConvertView();
	}

	/**
	 * 获取格式化后的日期
	 * 
	 * @param time
	 * @return
	 */
	private String getDate(String time) {
		try {
			SimpleDateFormat dataFormat = new SimpleDateFormat("MMM\ndd",
					Locale.ENGLISH);
			return dataFormat.format(new Date(ParseUtil.stol(time) * 1000));
		} catch (Exception e) {
			return "";
		}
	}
}
