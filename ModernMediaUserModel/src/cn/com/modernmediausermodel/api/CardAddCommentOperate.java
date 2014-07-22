package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import cn.com.modernmedia.api.BaseOperate;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.unit.SlatePrintHelper;
import cn.com.modernmediausermodel.model.MultiComment.CommentItem;
import cn.com.modernmediausermodel.model.User.Error;

/**
 * 添加评论operate
 * 
 * @author JianCong
 * 
 */
public class CardAddCommentOperate extends BaseOperate {
	private Error error;
	private ArrayList<NameValuePair> nameValuePairs; // post参数

	public Error getError() {
		return error;
	}

	protected CardAddCommentOperate(CommentItem commentItem) {
		this.error = new Error();
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject postObject = new JSONObject();
		try {
			addPostParams(postObject, "uid", commentItem.getUid());
			addPostParams(postObject, "cardid", commentItem.getCardId() + "");
			addPostParams(postObject, "time", commentItem.getTime());
			addPostParams(postObject, "content", commentItem.getContent());
			// postObject.put("type", commentItem.getType());
			params.add(new BasicNameValuePair("data", postObject.toString()));
			setPostParams(params);
			SlatePrintHelper.print("post values:" + postObject.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getAddComment();
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		JSONObject object = jsonObject.optJSONObject("error");
		if (object != null) {
			error.setNo(object.optInt("code", 0));
			error.setDesc(object.optString("msg", ""));
		}
	}

	@Override
	protected void fetchLocalDataInBadNet(FetchDataListener mFetchDataListener) {
		SlatePrintHelper.print("net error:" + getUrl());
		mFetchDataListener.fetchData(false, null);
	}

	@Override
	protected void saveData(String data) {
	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}

	@Override
	protected ArrayList<NameValuePair> getPostParams() {
		return nameValuePairs;
	}

	protected void setPostParams(ArrayList<NameValuePair> params) {
		this.nameValuePairs = params;
	}

}
