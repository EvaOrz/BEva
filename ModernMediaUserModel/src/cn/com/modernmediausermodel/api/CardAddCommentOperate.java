package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediausermodel.model.MultiComment.CommentItem;

/**
 * 添加评论operate
 * 
 * @author JianCong
 * 
 */
public class CardAddCommentOperate extends SlateBaseOperate {
	private ErrorMsg error;
	private ArrayList<NameValuePair> nameValuePairs; // post参数

	public ErrorMsg getError() {
		return error;
	}

	protected CardAddCommentOperate(CommentItem commentItem) {
		this.error = new ErrorMsg();
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
