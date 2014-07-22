package cn.com.modernmedia.views.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.adapter.ViewHolder;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.listener.NotifyLastestChangeListener;
import cn.com.modernmedia.views.model.IndexListParm;
import cn.com.modernmedia.views.util.V;

/**
 * iweekly类型首页适配器
 * 
 * @author user
 * 
 */
public class WeeklyIndexAdapter extends BaseIndexAdapter {
	private DateFormat format = new SimpleDateFormat("M/d");

	public WeeklyIndexAdapter(Context context, IndexListParm parm) {
		super(context, parm);
		ViewsApplication.addListener("WeeklyIndexAdapter",
				new NotifyLastestChangeListener() {

					@Override
					public void changeCount() {
						notifyDataSetChanged();
					}
				});
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		boolean convertViewIsNull = convertView == null;
		final ArticleItem item = getItem(position);
		ViewHolder holder = ViewHolder.get(mContext, convertView,
				R.layout.index_item_weekly);
		View contain = holder.getView(R.id.weekly_index_item_contain);
		TextView date = holder.getView(R.id.weekly_index_item_date);
		ImageView divider = holder.getView(R.id.weekly_index_item_divider);
		ImageView image = holder.getView(R.id.weekly_index_item_image);
		ImageView video = holder.getView(R.id.weekly_index_item_video);
		TextView title = holder.getView(R.id.weekly_index_item_title);
		TextView desc = holder.getView(R.id.weekly_index_item_desc);
		View imageFrame = holder.getView(R.id.weekly_index_item_fl);

		if (convertViewIsNull) {
			V.setImage(divider, parm.getDivider());
			setViewBack(imageFrame);
			setItemBg(contain);
		} else {
			setPlaceHolder(image);
			desc.setText("");
		}
		if (item != null) {
			video.setVisibility(item.getProperty().getType() == 3 ? View.VISIBLE
					: View.GONE);
			title.setText(item.getTitle());
			if (item.isDateFirst()) {
				date.setVisibility(View.VISIBLE);
				divider.setVisibility(View.VISIBLE);
				date.setText(format.format(new Date(ParseUtil.stol(item
						.getInputtime()) * 1000L)));
			} else {
				date.setVisibility(View.GONE);
				divider.setVisibility(View.GONE);
			}
			if (isReaded(item.getArticleId())) {
				title.setTextColor(Color.BLACK);
			} else {
				title.setTextColor(Color.parseColor("#ffb83126"));
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
		}
		return holder.getConvertView();
	}

	@Override
	protected boolean isReaded(int articleId) {
		if (ViewsApplication.lastestArticleId == null) {
			return true;
		} else {
			HashMap<Integer, Integer> map = ViewsApplication.lastestArticleId
					.getUnReadedId();
			if (map.containsKey(articleId)) {
				return false;
			}
		}
		return true;
	}
}
