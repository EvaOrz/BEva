package cn.com.modernmediausermodel.api;

import org.json.JSONObject;

import android.content.Context;
import cn.com.modernmediausermodel.db.RecommendCardDb;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserDataHelper;

/**
 * 广场
 * 
 * @author user
 * 
 */
public class GetRecommendCardOperate extends CardBaseOperate {
	private RecommendCardDb db;
	private Context mContext;

	public GetRecommendCardOperate(Context context, String timelineId,
			boolean isGetNewData) {
		super(timelineId, isGetNewData, context);
		db = RecommendCardDb.getInstance(context);
		mContext = context;
	}

	@Override
	protected String getUrl() {
		String url = UrlMaker.getRecommentCard() + getParam;
		User user = UserDataHelper.getUserLoginInfo(mContext);
		if (user != null) { // 未登录
			url += "/customer_uid/" + user.getUid();
		}
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		super.handler(jsonObject);
		Card card = getCard();
		if (card instanceof Card) {
			if (timelineId.equals("0")) {
				db.clearTable();
			}
			db.addCardItem(getCard());
		}
	}

	@Override
	protected Card getCardFromDb() {
		return db.getCard(timelineId);
	}

}
