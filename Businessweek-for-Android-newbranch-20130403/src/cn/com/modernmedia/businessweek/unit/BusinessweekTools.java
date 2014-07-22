package cn.com.modernmedia.businessweek.unit;

import android.content.Context;
import android.widget.ImageView;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.businessweek.MyApplication;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 商周工具类
 * 
 * @author ZhuQiao
 * 
 */
public class BusinessweekTools {
	/**
	 * 设置首页item右边的icon
	 * 
	 * @param imageView
	 * @param item
	 */
	public static void setIndexItemButtonImg(ImageView imageView,
			ArticleItem item) {
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
				MyApplication.getImageDownloader().download(
						DataHelper.columnRowMap.get(catId), imageView);
		}
	}

	public static void clickSlate(BaseView view, Context context,
			ArticleItem item, ArticleType articleType, Class<?>... cls) {
		TransferArticle transferArticle = new TransferArticle(articleType,
				UserTools.getUid(context));
		view.clickSlate(new Entry[] { item, transferArticle }, cls);
	}
}
