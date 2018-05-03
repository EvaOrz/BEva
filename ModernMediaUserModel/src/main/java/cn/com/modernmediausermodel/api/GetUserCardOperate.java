package cn.com.modernmediausermodel.api;

import android.content.Context;

import org.json.JSONObject;

import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.db.UserCardInfoDb;
import cn.com.modernmediausermodel.model.Card;

/**
 * 用户卡片列表
 *
 * @author user
 */
public class GetUserCardOperate extends CardBaseOperate {
    private String uid;
    private String customerId = "";// 当前登录用户ID
    private UserCardInfoDb db;

    public GetUserCardOperate(Context context, String uid, String timelineId,
                              boolean isGetNewData) {
        super(timelineId, isGetNewData, context);
        this.uid = uid;
        db = UserCardInfoDb.getInstance(context);
        User user = SlateDataHelper.getUserLoginInfo(context);
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
            db.addCardItem(card, uid);
        }
    }

    @Override
    protected Card getCardFromDb() {
        return db.getCard(timelineId, uid);
    }
}
