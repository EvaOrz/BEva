package cn.com.modernmedia.views.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.adapter.ViewHolder;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.model.IndexListParm;
import cn.com.modernmedia.views.util.V;

/**
 * 智慧云类型首页适配器
 * 
 * @author user
 * 
 */
public class ZhihuiyunIndexAdapter extends BaseIndexAdapter {

	public ZhihuiyunIndexAdapter(Context context, IndexListParm parm) {
		super(context, parm);
		initImageHeight(mContext.getResources().getDimensionPixelSize(R.dimen.dp10));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		boolean convertViewIsNull = convertView == null;
		final ArticleItem item = getItem(position);
		ViewHolder holder = ViewHolder.get(mContext, convertView,
				R.layout.index_item_zhihuiyun);
		View frame = holder.getView(R.id.zhihuiyun_index_item_rl);
		View advFrame = holder.getView(R.id.zhihuiyun_ndex_item_adv_ll);
		ImageView img = holder.getView(R.id.zhihuiyun_index_item_img);
		ImageView advImg = holder.getView(R.id.zhihuiyun_index_item_adv_img);
		View line = holder.getView(R.id.zhihuiyun_index_item_line);
		View advLine = holder.getView(R.id.zhihuiyun_index_item_adv_line);
		TextView title = holder.getView(R.id.zhihuiyun_index_item_title);
		TextView desc = holder.getView(R.id.zhihuiyun_index_item_desc);
		TextView day = holder.getView(R.id.zhihuiyun_index_item_date_day);
		TextView monthAndYear = holder
				.getView(R.id.zhihuiyun_index_item_date_month);

		if (convertViewIsNull) {
			img.getLayoutParams().height = imageHeight;
			V.setImage(line, parm.getDivider());
			V.setImage(advLine, parm.getDivider());
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
			// 文章日期(inputtime决定)
			if (!TextUtils.isEmpty(item.getInputtime())) {
				String dateStr = getDate(item.getInputtime());
				if (!TextUtils.isEmpty(dateStr)) {
					String[] date = dateStr.split(":");
					day.setText(date[0]);
					monthAndYear.setText(date[1]);
				}
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

	/**
	 * 获取格式化后的日期
	 * 
	 * @param time
	 * @return
	 */
	private String getDate(String time) {
		try {
			SimpleDateFormat dataFormat = new SimpleDateFormat("d:MMM,yyyy",
					Locale.ENGLISH);
			return dataFormat.format(new Date(ParseUtil.stol(time) * 1000));
		} catch (Exception e) {
			return "";
		}
	}
}
