package cn.com.modernmedia.businessweek.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.modernmedia.api.ImageDownloader;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.MyApplication;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.ListScrollStateListener;
import cn.com.modernmedia.listener.NotifyAdapterListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ParseUtil;

/**
 * 首页listview适配器
 * 
 * @author ZhuQiao
 * 
 */
public class IndexAdapter extends ArrayAdapter<ArticleItem> {
	private IndexAdapter me;
	private Context mContext;
	private LayoutInflater inflater;
	private ImageDownloader downloader = MyApplication.getImageDownloader();
	private String moreCat;
	private List<Integer> descList = new ArrayList<Integer>();// 直接加载desc比较卡
	private boolean isScroll = false;
	private ListScrollStateListener listener = new ListScrollStateListener() {

		@Override
		public void scrolling() {
			isScroll = true;
		}

		@Override
		public void scrollIdle() {
			isScroll = false;
			me.notifyDataSetChanged();
		}
	};

	private NotifyAdapterListener adapterListener = new NotifyAdapterListener() {

		@Override
		public void notifyReaded() {
			me.notifyDataSetChanged();
		}

		@Override
		public void nofitySelectItem(Object args) {
		}

		@Override
		public void notifyChanged() {
		}
	};

	public IndexAdapter(Context context) {
		super(context, 0);
		me = this;
		mContext = context;
		inflater = LayoutInflater.from(context);
		moreCat = context.getString(R.string.more_cat);
		if (context instanceof MainActivity) {
			((MainActivity) context).addListener(adapterListener);
		}
	}

	public void setData(List<ArticleItem> list) {
		descList.clear();
		synchronized (list) {
			for (ArticleItem item : list) {
				add(item);
			}
		}
	}

	public ListScrollStateListener getListener() {
		return listener;
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
			holder.more.setText(ParseUtil.parseString(moreCat,
					DataHelper.columnTitleMap.get(item.getCatId())));
		} else {
			holder.moreRl.setVisibility(View.GONE);
		}
		boolean hasInitDesc = descList.contains(position);
		if (hasInitDesc) {
			holder.desc.setText(item.getDesc());
		}
		if (!isScroll) {
			if (item.getPictureList() != null
					&& !item.getPictureList().isEmpty())
				if (!hasInitDesc) {
					holder.desc.setText(item.getDesc());
					descList.add(position);
				}
			downloader.download(item.getPictureList().get(0), holder.image);
		}
		holder.title.setText(item.getTitle());
		if (ReadDb.getInstance(mContext).isReaded(item.getArticleId())) {
			holder.title.setTextColor(Color.parseColor("#ff787878"));
		} else {
			holder.title.setTextColor(Color.BLACK);
		}
		setButtonImg(holder.right, item);
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
					((MainActivity) mContext).showChildCat(item.getCatId());
					((MainActivity) mContext).notifyColumnAdapter(item
							.getCatId());
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

	/**
	 * 设置文章右边icon
	 * 
	 * @param imageView
	 * @param catId
	 *            栏目id
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
		else if (catId >= 110 && catId <= 114)
			imageView.setImageResource(R.drawable.arrow_32);
		else if (catId == 99)
			imageView.setImageResource(R.drawable.arrow_14);
		else {
			imageView.setImageResource(R.drawable.arrow_xx);
			if (!DataHelper.columnRowMap.isEmpty()
					&& DataHelper.columnRowMap.containsKey(catId))
				downloader.download(DataHelper.columnRowMap.get(catId),
						imageView);
		}
	}

}
