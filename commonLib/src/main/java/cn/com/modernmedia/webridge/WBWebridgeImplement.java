package cn.com.modernmedia.webridge;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import cn.com.modernmedia.common.ShareHelper;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.Picture;
import cn.com.modernmedia.webridge.WBWebridge.AsynExecuteCommandListener;
import cn.com.modernmediaslate.model.User;

/**
 * 根据js发送的command注册的方法
 * 
 * @author user
 * 
 */
public class WBWebridgeImplement implements WBWebridgeListener {

	private Context mContext;

	public WBWebridgeImplement(Context c) {
		mContext = c;
	}

	// ======================js调用的native方法======================

	/**
	 * 分享 (异步) {"command":"share","sequence":2,"params":{"content":
	 * "展望2016：一年大事一览:彭博新闻记者将继续报道2016年的重大事件，让我们按时间顺序一起展望2016热点事件。","thumb":
	 * "http:\/\/s.qiniu.bb.bbwc.cn\/issue_0\/category\/2016\/0104\/5689cda80b488_90x90.jpg","link":"http:\/\/read.bbwc.cn\/dufabs.html"}
	 * }
	 */
	public void share(JSONObject json, AsynExecuteCommandListener listener) {
		if (listener != null) {
			try {
				JSONObject params = json.optJSONObject("params");
				ArticleItem item = new ArticleItem();
				List<Picture> thumbs = new ArrayList<Picture>();

				Picture pic = new Picture();
				pic.setUrl(params.optString("thumb"));
				thumbs.add(pic);

				item.setThumbList(thumbs);
				item.setTitle(params.getString("content"));
				item.setWeburl(params.optString("link"));
				ShareHelper.shareByDefault(mContext, item);
				Log.e("%%%%%%%%%", json.toString());
				json.put("shareResult", true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			listener.onCallBack(json.toString());
		}

	}

}
