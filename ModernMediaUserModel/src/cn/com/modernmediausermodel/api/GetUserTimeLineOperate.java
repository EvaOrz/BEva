package cn.com.modernmediausermodel.api;

import org.json.JSONObject;

import android.content.Context;
import cn.com.modernmediausermodel.db.TimelineDb;
import cn.com.modernmediausermodel.model.Card;

/**
 * 获取用户首页
 * 
 * @author user
 * 
 */
public class GetUserTimeLineOperate extends CardBaseOperate {
	private String uid;
	private TimelineDb db;

	public GetUserTimeLineOperate(Context context, String uid,
			String timelineId, boolean isGetNewData) {
		super(timelineId, isGetNewData, context);
		this.uid = uid;
		db = TimelineDb.getInstance(context);
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getTimeLine() + "/uid/" + uid + getParam;
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
