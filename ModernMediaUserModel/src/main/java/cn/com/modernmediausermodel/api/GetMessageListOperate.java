package cn.com.modernmediausermodel.api;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediausermodel.model.Message;
import cn.com.modernmediausermodel.model.Message.MessageItem;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 消息列表operate
 * 
 * @author JianCong
 * 
 */
public class GetMessageListOperate extends SlateBaseOperate {
	private Message message;
	private ArrayList<NameValuePair> nameValuePairs; // post参数

	public Message getMessage() {
		return message;
	}

	protected GetMessageListOperate(String uid, int lastId) {
		this.message = new Message();
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject postObject = new JSONObject();
		try {
			postObject.put("uid", uid);
			postObject.put("appid", UserConstData.getInitialAppId());
			postObject.put("lastid", lastId);
			params.add(new BasicNameValuePair("data", postObject.toString()));
			setPostParams(params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getMessageList();
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		if (!isNull(jsonObject)) {
			message.setUid(jsonObject.optString("uid", ""));
			message.setAppId(jsonObject.optInt("appid", 0));
			message.setLastId(jsonObject.optInt("lastid", 0));
			JSONArray array = jsonObject.optJSONArray("lists");
			if (!isNull(array)) {
				parseMessageItem(array);
			}
		}

	}

	private void parseMessageItem(JSONArray array) {
		HashMap<String, MessageItem> messageMap = new HashMap<String, Message.MessageItem>();
		int len = array.length();
		for (int i = 0; i < len; i++) {
			JSONObject object = array.optJSONObject(i);
			if (isNull(object))
				return;
			MessageItem messageItem = new MessageItem();
			messageItem.setUid(object.optString("uid", ""));
			messageItem.setCardid(object.optInt("cardid", 0));

			// messageItem.setNickname(object.optString("nickname", ""));
			// messageItem.setComment(object.optString("comment", ""));
			messageItem.setTime(object.optString("time", ""));
			// 笔记内容
			String content = object.optString("content", "");
			if (!TextUtils.isEmpty(content) && content.length() > 20) {
				content = content.trim().substring(0, 20) + "……";
			}
			messageItem.setContent(content);
			// message类型（0、测试用 1、添加评论 2、新增粉丝 3、自己被推荐 4、自己的卡片被推荐）
			int type = object.optInt("type", 0);
			messageItem.setType(type);

			// 合并通知
			String preKey = "";
			if (type == 2 || type == 3) {
				preKey = message.getUid();
			} else if (type == 1 || type == 4) {
				preKey = messageItem.getCardid() + "";
			}
			String key = preKey + "_" + type;
			if (messageMap.get(key) == null) {
				messageMap.put(key, messageItem);
			}
			MessageItem item = messageMap.get(key);
			if (type == 1) { // 添加评论 （同一笔记的评论通知整合成一条通知，并显示条数）
				item.setCommentNum(item.getCommentNum() + 1);
			} else if (type == 2) { // 新增粉丝
				item.setFansNum(item.getFansNum() + 1);
			} else {
				// type为3、4时，合并成一条数据
			}
		}
		// 将MAP数据转成ArrayList
		message.getMessageList().addAll(messageMap.values());
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
