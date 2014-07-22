package cn.com.modernmediasolo.api;

import java.util.List;

import android.content.Context;
import cn.com.modernmedia.api.GetCatIndexOperate;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmediasolo.SoloApplication;
import cn.com.modernmediasolo.db.SoloDb;
import cn.com.modernmediasolo.db.SoloFocusDb;

/**
 * 独立栏目首页
 * 
 * @author ZhuQiao
 * 
 */
public class GetSoloCatIndexOperate extends GetCatIndexOperate {
	private Context mContext;
	private boolean mFromNet;
	private boolean hasClearTable;
	private SoloFocusDb focusDb;

	public GetSoloCatIndexOperate(Context context, String catId,
			String fromOffset, String toOffset, int position) {
		super(context, catId, fromOffset, toOffset, SoloApplication.soloColumn,
				position);
		mContext = context;
		mFromNet = true;
		hasClearTable = false;
		focusDb = SoloFocusDb.getInstance(context);
	}

	@Override
	public void addSoloDb(int catId, List<ArticleItem> list) {
		if (mFromNet)
			SoloDb.getInstance(mContext).addSoloItems(catId, list);
	}

	@Override
	public void addSoloFoucsDb(int catId, List<ArticleItem> list) {
		if (mFromNet) {
			if (!hasClearTable) {
				focusDb.clearTable();
				hasClearTable = true;
			}
			focusDb.addSoloFoucsItems(catId, list);
		}
	}

}
