package cn.com.modernmediausermodel.api;

import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.util.UserConstData;
import cn.com.modernmediausermodel.util.UserFileManager;

/**
 * 获取单张卡片的详情
 * 
 * @author user
 * 
 */
public class GetCardDetailOperate extends CardBaseOperate {
	private String cardId = "";

	public GetCardDetailOperate(String cardId) {
		super("0", false, null);
		this.cardId = cardId;
	}

	@Override
	protected String getUrl() {
		String url = UrlMaker.getCardDetail(cardId);
		User user = SlateDataHelper.getUserLoginInfo(getmContext());
		if (user != null) { // 未登录
			url += "/customer_uid/" + user.getUid();
		}
		return url;
	}

	@Override
	protected void saveData(String data) {
		UserFileManager.saveApiData(getDefaultFileName(), data);
	}

	@Override
	protected String getDefaultFileName() {
		return UserConstData.getCardDetailFileName(cardId);
	}

	@Override
	protected Card getCardFromDb() {
		return null;
	}

}
