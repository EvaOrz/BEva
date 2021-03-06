package cn.com.modernmediausermodel.api;

import android.content.Context;

import org.json.JSONObject;

import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.db.RecommendCardDb;
import cn.com.modernmediausermodel.model.Card;

/**
 * 广场
 *
 * @author user
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
        User user = SlateDataHelper.getUserLoginInfo(mContext);
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
//			if (timelineId.equals("0")) {
//				db.clearTable();
//			}
            db.addCardItem(getCard());
        }
    }

    @Override
    protected Card getCardFromDb() {
        return db.getCard(timelineId);
    }
}
