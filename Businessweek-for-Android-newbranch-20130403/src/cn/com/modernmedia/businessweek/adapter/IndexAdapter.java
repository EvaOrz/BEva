package cn.com.modernmedia.businessweek.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.modernmedia.api.ImageDownloader;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.DataHelper;

/**
 *  ◊“≥listview  ≈‰∆˜
 * 
 * @author ZhuQiao
 * 
 */
public class IndexAdapter extends ArrayAdapter<ArticleItem> {
	private Context mContext;
	private LayoutInflater inflater;
	private ImageDownloader downloader = ImageDownloader.getInstance();

	public IndexAdapter(Context context) {
		super(context, 0);
		mContext = context;
		inflater = LayoutInflater.from(context);
	}

	public void setData(List<ArticleItem> list) {
		synchronized (list) {
			for (ArticleItem item : list) {
				if (DataHelper.columnTitleMap != null
						&& DataHelper.columnTitleMap.containsKey(item
								.getCatId())) {
					add(item);
				}
			}
		}
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
			convertView.setBackgroundColor(Color.WHITE);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.image.setImageResource(R.drawable.general_placeholder);
		}
		final ViewHolder viewHolder = holder;
		viewHolder.titleBar.setVisibility(item.isShowTitleBar() ? View.VISIBLE
				: View.GONE);
		viewHolder.desc.postDelayed(new Runnable() {

			@Override
			public void run() {
				viewHolder.desc.setText(item.getDesc());
			}
		}, 50);
		viewHolder.title.setText(item.getTitle());
		if (ReadDb.getInstance(mContext).isReaded(item.getArticleId())) {
			viewHolder.title.setTextColor(Color.parseColor("#ff787878"));
		} else {
			viewHolder.title.setTextColor(Color.BLACK);
		}
		setButtonImg(viewHolder.right, item);
		if (item.getPictureList() != null && !item.getPictureList().isEmpty())
			downloader.download(item.getPictureList().get(0), viewHolder.image);
		if (DataHelper.columnColorMap != null
				&& DataHelper.columnColorMap.containsKey(item.getCatId())) {
			viewHolder.titleBar.setBackgroundColor(DataHelper.columnColorMap
					.get(item.getCatId()));
		}
		if (DataHelper.columnTitleMap != null
				&& DataHelper.columnTitleMap.containsKey(item.getCatId())) {
			viewHolder.column.setText(DataHelper.columnTitleMap.get(item
					.getCatId()));
		}
		return convertView;
	}

	private class ViewHolder {
		RelativeLayout titleBar;
		TextView column, title, desc;
		ImageView image, right;
	}


	/**
	 * …Ë÷√Œƒ’¬”“±ﬂicon
	 * 
	 * @param imageView
	 * @param catId
	 *            ¿∏ƒøid
	 */
	private void setButtonImg(ImageView imageView, ArticleItem item) {
		int catId = item.getCatId();
		if (catId == 11)
			imageView.setImageResource(R.drawable.arrow_11);
		else if (catId == 12)
			imageView.setImageResource(R.drawable.arrow_12);
		else if (catId == 13)
			imageView.setImageResource(R.drawable.arrow_13);
		else if (catId == 14)
			imageView.setImageResource(R.drawable.arrow_14);
		else if (catId == 16)
			imageView.setImageResource(R.drawable.arrow_16);
		else if (catId == 18)
			imageView.setImageResource(R.drawable.arrow_18);
		else if (catId == 19)
			imageView.setImageResource(R.drawable.arrow_19);
		else if (catId == 20)
			imageView.setImageResource(R.drawable.arrow_20);
		else if (catId == 21)
			imageView.setImageResource(R.drawable.arrow_21);
		else if (catId == 32)
			imageView.setImageResource(R.drawable.arrow_32);
		else if (catId == 37)
			imageView.setImageResource(R.drawable.arrow_37);
		else {
			imageView.setImageResource(R.drawable.arrow_xx);
			if (!DataHelper.columnRowMap.isEmpty()
					&& DataHelper.columnRowMap.containsKey(catId))
				ImageDownloader.getInstance().download(
						DataHelper.columnRowMap.get(catId), imageView);
		}
	}
}
