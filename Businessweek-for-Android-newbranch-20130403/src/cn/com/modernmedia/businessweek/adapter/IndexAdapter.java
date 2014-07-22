package cn.com.modernmedia.businessweek.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.modernmedia.adapter.BaseIndexAdapter;
import cn.com.modernmedia.api.ImageDownloader;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.MyApplication;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.unit.BusinessweekTools;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ParseUtil;

/**
 * 首页列表适配器
 * 
 * @author ZhuQiao
 * 
 */
public class IndexAdapter extends BaseIndexAdapter {
	private Context mContext;
	private LayoutInflater inflater;
	private ImageDownloader downloader = MyApplication.getImageDownloader();

	public IndexAdapter(Context context) {
		super(context);
		mContext = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ArticleItem item = getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.index_item, null);
			holder = new ViewHolder();
			holder.titleBar = (RelativeLayout) convertView
					.findViewById(R.id.index_item_titlebar);
			holder.column = (TextView) convertView
					.findViewById(R.id.index_item_column_title);
			holder.title = (TextView) convertView
					.findViewById(R.id.index_item_title);
			holder.desc = (TextView) convertView
					.findViewById(R.id.index_item_desc);
			holder.image = (ImageView) convertView
					.findViewById(R.id.index_item_img);
			holder.right = (ImageView) convertView
					.findViewById(R.id.index_item_rightRow);
			holder.more = (TextView) convertView
					.findViewById(R.id.index_item_more);
			holder.moreRl = (RelativeLayout) convertView
					.findViewById(R.id.index_item_more_rl);
			convertView.setBackgroundColor(Color.WHITE);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.image.setImageResource(R.drawable.general_placeholder);
		}
		holder.titleBar.setVisibility(item.isShowTitleBar() ? View.VISIBLE
				: View.GONE);
		if (item.isShowMoreCat()) {
			holder.moreRl.setVisibility(View.VISIBLE);
			holder.more.setText(ParseUtil.parseString(mContext,
					R.string.more_cat,
					DataHelper.columnTitleMap.get(item.getCatId())));
		} else {
			holder.moreRl.setVisibility(View.GONE);
		}
		if (hasInitDesc(position)) {
			holder.desc.setText(item.getDesc());
		}
		if (!isScroll) {
			if (!hasInitDesc(position)) {
				holder.desc.setText(item.getDesc());
				addDesc(position);
			}
			if (ParseUtil.listNotNull(item.getPictureList()))
				downloader.download(item.getPictureList().get(0), holder.image);
		}
		holder.title.setText(item.getTitle());
		if (ReadDb.getInstance(mContext).isReaded(item.getArticleId())) {
			holder.title.setTextColor(Color.parseColor("#ff787878"));
		} else {
			holder.title.setTextColor(Color.BLACK);
		}
		BusinessweekTools.setIndexItemButtonImg(holder.right, item);
		if (DataHelper.columnColorMap != null
				&& DataHelper.columnColorMap.containsKey(item.getCatId())) {
			holder.titleBar.setBackgroundColor(DataHelper.columnColorMap
					.get(item.getCatId()));
		}
		if (DataHelper.columnTitleMap != null
				&& DataHelper.columnTitleMap.containsKey(item.getCatId())) {
			holder.column
					.setText(DataHelper.columnTitleMap.get(item.getCatId()));
		}
		holder.moreRl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mContext instanceof MainActivity) {
					int catId = item.getCatId();
					if (DataHelper.soloCatMap.containsKey(catId)) {
						((MainActivity) mContext)
								.showSoloChildCat(DataHelper.soloCatMap
										.get(catId));
					} else {
						((MainActivity) mContext).showChildCat(catId);
					}
					if (DataHelper.columnTitleMap != null
							&& DataHelper.columnTitleMap.containsKey(item
									.getCatId())) {
						((MainActivity) mContext)
								.setIndexTitle(DataHelper.columnTitleMap
										.get(item.getCatId()));
					}
					((MainActivity) mContext).notifyColumnAdapter(catId);
					LogHelper.logAndroidTouchMorenews();
				}
			}
		});
		return convertView;
	}

	private class ViewHolder {
		RelativeLayout titleBar, moreRl;
		TextView column, title, desc, more;
		ImageView image, right;
	}

}
