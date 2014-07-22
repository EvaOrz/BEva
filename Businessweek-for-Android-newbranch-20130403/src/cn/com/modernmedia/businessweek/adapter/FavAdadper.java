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
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserDataHelper;

/**
 * 收藏列表适配器
 * 
 * @author ZhuQiao
 * 
 */
public class FavAdadper extends ArrayAdapter<FavoriteItem> {
	private Context mContext;
	private LayoutInflater inflater;

	public FavAdadper(Context context) {
		super(context, 0);
		mContext = context;
		inflater = LayoutInflater.from(context);
	}

	public void setData(List<FavoriteItem> list) {
		synchronized (list) {
			for (FavoriteItem item : list) {
				add(item);
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		final FavoriteItem detail = getItem(position);
//		ViewHolder holder = null;
//		if (convertView == null) {
//			convertView = inflater.inflate(R.layout.fav_item, null);
//			holder = new ViewHolder();
//			holder.title = (TextView) convertView
//					.findViewById(R.id.fav_item_name);
//			holder.margin = (LinearLayout) convertView
//					.findViewById(R.id.fav_item_margin);
//			holder.margin.setBackgroundColor(Color.BLACK);
//			convertView.setTag(holder);
//		} else {
//			holder = (ViewHolder) convertView.getTag();
//		}
//		if (detail != null) {
//			holder.title.setText(detail.getTitle());
//		}
//		convertView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (mContext instanceof MainActivity) {
//					LogHelper.logOpenArticleFromFavoriteArticleList(mContext,
//							detail.getId() + "", detail.getCatid() + "");
//					// 取得当前用户ID。若未取得，默认为‘0’
//					User user = UserDataHelper.getUserLoginInfo(mContext);
//					String uid = user == null ? ConstData.UN_UPLOAD_UID : user
//							.getUid();
//					TransferArticle article = new TransferArticle(null, detail
//							.getId(), detail.getCatid(), ArticleType.Fav, uid,
//							null);
//					((MainActivity) mContext).gotoArticleActivity(article);
//				}
//			}
//		});
		return convertView;
	}

	private class ViewHolder {
		TextView title;
		LinearLayout margin;
	}
}
