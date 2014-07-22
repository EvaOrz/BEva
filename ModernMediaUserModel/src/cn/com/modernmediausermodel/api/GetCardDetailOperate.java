package cn.com.modernmediausermodel.api;

import cn.com.modernmedia.util.FileManager;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserConstData;
import cn.com.modernmediausermodel.util.UserDataHelper;

/**
 * 获取单张卡片的详情
 * 
 * @author user
 * 
 */
public class GetCardDetailOperate extends CardBaseOperate {
	private String cardId = "";

	public GetCardDetailOperate(String cardId) {
		super("0", false);
		this.cardId = cardId;
	}

	@Override
	protected String getUrl() {
		String url = UrlMaker.getCardDetail(cardId);
		User user = UserDataHelper.getUserLoginInfo(getmContext());
		if (user != null) { // 未登录
			url += "/customer_uid/" + user.getUid();
		}
		return url;
	}

	@Override
	protected void saveData(String data) {
		FileManager.saveApiData(getDefaultFileName(), data);
	}

	@Override
	protected String getDefaultFileName() {
		return UserConstData.getCardDetailFileName(cardId);
	}

}
