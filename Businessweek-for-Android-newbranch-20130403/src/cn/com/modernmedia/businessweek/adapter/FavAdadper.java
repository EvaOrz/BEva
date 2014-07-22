package cn.com.modernmedia.businessweek.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.model.ArticleList.ArticleDetail;
import cn.com.modernmedia.util.LogHelper;

/**
 *  ’≤ÿ  ≈‰∆˜
 * 
 * @author ZhuQiao
 * 
 */
public class FavAdadper extends ArrayAdapter<ArticleDetail> {
	private Context mContext;
	private LayoutInflater inflater;

	public FavAdadper(Context context) {
		super(context, 0);
		mContext = context;
		inflater = LayoutInflater.from(context);
	}

	public void setData(List<ArticleDetail> list) {
		synchronized (list) {
			for (ArticleDetail item : list) {
				add(item);
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ArticleDetail detail = getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.fav_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView
					.findViewById(R.id.fav_item_name);
			holder.margin = (LinearLayout) convertView
					.findViewById(R.id.fav_item_margin);
			holder.margin.setBackgroundColor(Color.BLACK);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (detail != null) {
			holder.title.setText(detail.getTitle());
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mContext instanceof MainActivity) {
					LogHelper.logOpenArticleFromFavoriteArticleList(mContext,
							detail.getArticleId() + "", detail.getCatId() + "");
					((MainActivity) mContext).gotoArticleActivity(
							detail.getArticleId(), false);
				}
			}
		});
		return convertView;
	}

	private class ViewHolder {
		TextView title;
		LinearLayout margin;
	}
}
