package cn.com.modernmediausermodel.api;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.modernmedia.api.BaseOperate;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.model.Card.CardItem;
import cn.com.modernmediausermodel.model.Card.CardPicture;

public class CardBaseOperate extends BaseOperate {
	private Card card;
	protected String getParam; // get的一部分参数

	public Card getCard() {
		return card;
	}

	public CardBaseOperate(String timelineId, boolean isGetNewData) {
		card = new Card();
		if (isGetNewData) {
			getParam = "/top/" + timelineId; // 取最新的数据
		} else {
			getParam = "/bottom/" + timelineId; // 加载更多较早数据，timelineId为0时，加载当前数据
		}
	}

	@Override
	protected String getUrl() {
		return null;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		parseCard(jsonObject);
	}

	@Override
	protected void saveData(String data) {
	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}

	protected void parseCard(JSONObject jsonObject) {
		card.setUid(jsonObject.optString("uid", ""));
		card.setIssueId(jsonObject.optString("issueid", ""));
		card.setArticleId(jsonObject.optInt("articleid", 0));
		card.setCount(jsonObject.optInt("count", 0));
		JSONArray array = jsonObject.optJSONArray("card");
		if (!isNull(array)) {
			int len = array.length();
			for (int i = 0; i < len; i++) {
				JSONObject object = array.optJSONObject(i);
				if (object != null) {
					CardItem cardItem = new Card.CardItem();
					cardItem.setUid(object.optString("uid", ""));
					cardItem.setId(object.optString("id", ""));
					cardItem.setAppId(object.optInt("appid", 0));
					cardItem.setType(object.optInt("type", 0));
					cardItem.setTime(object.optString("time", ""));
					cardItem.setFuid(object.optString("fuid", ""));
					cardItem.setWebUrl(object.optString("weburl", ""));
					cardItem.setCategoryId(object.optInt("category_id", 0));
					cardItem.setTags(object.optString("tags", ""));
					cardItem.setContents(object.optString("contents", ""));
					cardItem.setArticleId(object.optInt("articleid", 0));
					cardItem.setToken(object.optString("token", ""));
					cardItem.setCommentNum(object.optInt("comment_num", 0));
					cardItem.setTimeLineId(object.optString("timelineid"));
					cardItem.setFavNum(object.optInt("fav_num", 0));
					cardItem.setIsDel(object.optInt("isdel", 0));
					cardItem.setIsFav(object.optInt("isfav", 0));
					cardItem.setTitle(object.optString("title", ""));
					cardItem.setMark(object.optString("mark", ""));
					cardItem.setUpdatetime(object.optString("updatetime"));
					JSONArray pictureArray = object.optJSONArray("pic");
					if (!isNull(pictureArray)) {
						int length = pictureArray.length();
						for (int j = 0; j < length; j++) {
							JSONObject obj = pictureArray.optJSONObject(j);
							CardPicture picture = new CardPicture();
							picture.setUrl(obj.optString("url", ""));
							picture.setDesc(obj.optString("desc", ""));
							picture.setTitle(obj.optString("title", ""));
							picture.setWidth(obj.optInt("width", 0));
							picture.setHeight(obj.optInt("height", 0));
							cardItem.getPictures().add(picture);
						}
					}
					JSONArray favUsers = object.optJSONArray("favUsers");
					if (!isNull(favUsers)) {
						parseFavUsers(favUsers, cardItem);
					}
					card.getCardItemList().add(cardItem);
				}
			}
		}

		JSONObject error = jsonObject.optJSONObject("error");
		if (!isNull(error)) {
			card.getError().setNo(error.optInt("code", 0));
			card.getError().setDesc(error.optString("msg"));
		}
	}

	/**
	 * 解析收藏这卡片的用户
	 * 
	 * @param arr
	 * @param item
	 */
	private void parseFavUsers(JSONArray arr, CardItem item) {
		if (isNull(arr))
			return;
		int length = arr.length();
		JSONObject obj;
		for (int i = 0; i < length; i++) {
			obj = arr.optJSONObject(i);
			if (isNull(obj))
				continue;
			item.getFavUsers().add(obj.optString("uid", ""));
		}
	}
}
