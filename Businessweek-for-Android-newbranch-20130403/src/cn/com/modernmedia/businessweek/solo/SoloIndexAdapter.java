package cn.com.modernmedia.businessweek.solo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.api.ImageDownloader;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.MyApplication;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.unit.BusinessweekTools;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.NotifyAdapterListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediasolo.SoloAdapter;

/**
 * 独立栏目列表适配器
 * 
 * @author ZhuQiao
 * 
 */
public class SoloIndexAdapter extends SoloAdapter {
	private SoloIndexAdapter me;
	private Context mContext;
	private LayoutInflater inflater;
	private ImageDownloader downloader = MyApplication.getImageDownloader();
	private List<Integer> descList = new ArrayList<Integer>();

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

	public SoloIndexAdapter(Context context) {
		super(context);
		me = this;
		mContext = context;
		inflater = LayoutInflater.from(context);
		if (context instanceof MainActivity) {
			((MainActivity) context).addListener(adapterListener);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ArticleItem item = getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.solo_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView
					.findViewById(R.id.solo_item_title);
			holder.desc = (TextView) convertView
					.findViewById(R.id.solo_item_desc);
			holder.image = (ImageView) convertView
					.findViewById(R.id.solo_item_img);
			holder.right = (ImageView) convertView
					.findViewById(R.id.solo_item_rightRow);
			convertView.setBackgroundColor(Color.WHITE);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.image.setImageResource(R.drawable.general_placeholder);
		}
		boolean hasInitDesc = descList.contains(position);
		if (hasInitDesc) {
			holder.desc.setText(item.getDesc());
		}
		if (!isScroll) {
			if (ParseUtil.listNotNull(item.getPictureList()))
				downloader.download(item.getPictureList().get(0), holder.image);
			if (!hasInitDesc) {
				holder.desc.setText(item.getDesc());
				descList.add(position);
			}
		}
		holder.title.setText(item.getTitle());
		if (ReadDb.getInstance(mContext).isReaded(item.getArticleId())) {
			holder.title.setTextColor(Color.parseColor("#ff787878"));
		} else {
			holder.title.setTextColor(Color.BLACK);
		}
		BusinessweekTools.setIndexItemButtonImg(holder.right, item);
		return convertView;
	}

	private class ViewHolder {
		TextView title, desc;
		ImageView image, right;
	}

}
