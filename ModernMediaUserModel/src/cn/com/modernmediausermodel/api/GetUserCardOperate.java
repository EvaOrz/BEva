package cn.com.modernmediausermodel.api;

import org.json.JSONObject;

import android.content.Context;
import cn.com.modernmediausermodel.db.UserCardInfoDb;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserDataHelper;

/**
 * 用户卡片列表
 * 
 * @author user
 * 
 */
public class GetUserCardOperate extends CardBaseOperate {
	private String uid;
	private String timelineId = "";
	private String customerId = "";// 当前登录用户ID
	private UserCardInfoDb db;

	public GetUserCardOperate(Context context, String uid, String timelineId,
			boolean isGetNewData) {
		super(timelineId, isGetNewData);
		this.uid = uid;
		this.timelineId = timelineId;
		db = UserCardInfoDb.getInstance(context);
		User user = UserDataHelper.getUserLoginInfo(context);
		if (user != null) {
			this.customerId = user.getUid();
		}
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getUserCard() + "/uid/" + uid + "/customer_uid/"
				+ customerId + getParam;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		super.handler(jsonObject);
		Card card = getCard();
		if (card instanceof Card) {
			if (timelineId.equals("0")) {
				db.clearTable(uid);
			}
			db.addCardItem(getCard(), uid);
		}
	}
}
