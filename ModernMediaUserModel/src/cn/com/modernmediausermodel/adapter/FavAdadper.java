package cn.com.modernmediausermodel.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.PageTransfer;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediausermodel.FavoritesActivity;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserConstData;
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
	private Issue issue;

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

	public void setIssue(Issue issue) {
		this.issue = issue;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final FavoriteItem detail = getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.favorites_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView
					.findViewById(R.id.favorites_item_name);
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
				if (mContext instanceof FavoritesActivity) {
					LogHelper.logOpenArticleFromFavoriteArticleList(mContext,
							detail.getId() + "", detail.getCatid() + "");
					// 取得当前用户ID。若未取得，默认为‘0’
					User user = UserDataHelper.getUserLoginInfo(mContext);
					String uid = user == null ? ConstData.UN_UPLOAD_UID : user
							.getUid();
					TransferArticle article = new TransferArticle(issue, detail
							.getId(), detail.getCatid(), ArticleType.Fav, uid,
							null);
					gotoArticleActivity(article);
				}
			}
		});
		return convertView;
	}

	private class ViewHolder {
		TextView title;
	}

	public void gotoArticleActivity(TransferArticle transferArticle) {
		if (UserConstData.getArticleClass() == null) {
			return;
		}
		PageTransfer.gotoArticleActivity(mContext,
				UserConstData.getArticleClass(), transferArticle);
	}

}
