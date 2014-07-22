package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.unit.SlatePrintHelper;
import cn.com.modernmediausermodel.model.Card.CardItem;
import cn.com.modernmediausermodel.model.MultiComment;
import cn.com.modernmediausermodel.model.MultiComment.Comment;
import cn.com.modernmediausermodel.model.MultiComment.CommentItem;

public class GetCardCommentsOperate extends SlateBaseOperate {

	private ArrayList<CardItem> cardItemList;
	private MultiComment comments;
	private int commentId;
	private boolean isGetNewData;

	public GetCardCommentsOperate(ArrayList<CardItem> cardItemList,
			int commentId, boolean isGetNewData) {
		this.commentId = commentId;
		this.isGetNewData = isGetNewData;
		this.cardItemList = cardItemList;
		comments = new MultiComment();
	}

	public MultiComment getComments() {
		return comments;
	}

	@Override
	protected String getUrl() {
		String cardId = "";
		StringBuilder builder = new StringBuilder();
		for (CardItem item : cardItemList) {
			builder.append(item.getId()).append(",");
		}
		String str = builder.toString();
		if (str.length() > 0) {
			cardId = str.substring(0, str.length() - 1);
		}
		String getParam = "";
		if (isGetNewData) { // 取得最新得数据（下拉刷新用）
			getParam = "/top/" + commentId;
		} else {
			getParam = "/bottom/" + commentId;
		}
		return UrlMaker.getCardComments() + "/cardid/" + cardId + getParam;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		JSONArray array = jsonObject.optJSONArray("commentlist");
		if (array != null) {
			int len = array.length();
			for (int i = 0; i < len; i++) {
				JSONObject object = array.optJSONObject(i);
				if (!isNull(object)) {
					comments.getCommentList().add(parseComment(object));
				} else {
					comments.getCommentList().add(new Comment());
				}
			}
		}
	}

	@Override
	protected void saveData(String data) {
	}

	@Override
	protected String getDefaultFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void fetchLocalDataInBadNet(FetchDataListener mFetchDataListener) {
		SlatePrintHelper.print("net error:" + getUrl());
		mFetchDataListener.fetchData(false, null);
	}

	private Comment parseComment(JSONObject jsonObject) {
		Comment comment = new Comment();
		comment.setUid(jsonObject.optString("uid", ""));
		comment.setPageCount(jsonObject.optInt("pagecount", 0));
		JSONArray array = jsonObject.optJSONArray("comment");
		if (array != null) {
			int len = array.length();
			for (int i = 0; i < len; i++) {
				CommentItem commentItem = new CommentItem();
				JSONObject object = array.optJSONObject(i);
				commentItem.setCardId(object.optString("cardid", ""));
				commentItem.setUid(object.optString("uid", ""));
				commentItem.setTime(object.optString("time", ""));
				commentItem.setContent(object.optString("content", ""));
				commentItem.setToken(object.optString("token", ""));
				commentItem.setId(object.optInt("id", 0));
				commentItem.setIsdel(object.optInt("isdel", 0));
				comment.getCommentItemList().add(commentItem);
			}
		}
		JSONObject error = jsonObject.optJSONObject("error");
		if (!isNull(error)) {
			comment.getError().setNo(error.optInt("code", 0));
			comment.getError().setDesc(error.optString("msg"));
		}
		return comment;
	}

}
