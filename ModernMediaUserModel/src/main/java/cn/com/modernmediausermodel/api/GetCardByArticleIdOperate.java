package cn.com.modernmediausermodel.api;

import android.content.Context;

import org.json.JSONObject;

import cn.com.modernmediausermodel.db.CardListByArtilceIdDb;
import cn.com.modernmediausermodel.model.Card;

/**
 * 文章摘要卡片获取类
 *
 * @author jiancong
 */
public class GetCardByArticleIdOperate extends CardBaseOperate {

    private CardListByArtilceIdDb db;
    private String url;

    public GetCardByArticleIdOperate(Context context, String articleId,
                                     String issueId, String uid) {
        super("", false, context);
        db = CardListByArtilceIdDb.getInstance(context);
        url = UrlMaker.getCardByArticleId(articleId, issueId, uid);
    }

    @Override
    protected String getUrl() {
        return url;
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        super.handler(jsonObject);
    }

    @Override
    protected Card getCardFromDb() {
        Card card = getCard();
        if (card instanceof Card) {
            db.clearTable();
            db.addCardItem(card);
        }
        return card;
    }
}
