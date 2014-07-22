package cn.com.modernmediasolo.widget;

import android.content.Context;
import android.util.AttributeSet;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.Atlas;
import cn.com.modernmedia.widget.CommonAtlasView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediasolo.api.SoloOperateController;

public abstract class SoloAltasView extends CommonAtlasView {
	private Context mContext;

	public SoloAltasView(Context context) {
		this(context, null);
	}

	public SoloAltasView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override
	protected void getSoloArticleById(FavoriteItem item) {
		SoloOperateController.getInstance(mContext).getSoloArticleById(item,
				new FetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						if (entry instanceof Atlas) {
							setValuesForWidget((Atlas) entry);
							disProcess();
						} else {
							showError();
						}
					}
				});
	}

}
