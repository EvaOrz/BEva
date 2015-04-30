package cn.com.modernmedia.newtag.mainprocess;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.com.modernmedia.api.UrlMaker;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage.MainProcessParseCallBack;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage.PushCallback;
import cn.com.modernmedia.util.UriParse;

/**
 * 首页推送流程
 * 
 * @author zhuqiao
 * 
 */
public class TagMainProcessParse extends BaseTagMainProcess {
	public static final String KEY_PUSH_ARTICLE_URL = "push_article_url";

	public TagMainProcessParse(Context context,
			MainProcessParseCallBack callBack) {
		super(context, callBack);
	}

	@Override
	public void onStart(Object... objs) {
		if (objs == null || objs.length < 2)
			return;
		Object obj1 = objs[0];
		Object obj2 = objs[1];
		if (!(obj2 instanceof PushCallback))
			return;
		PushCallback pushCallback = (PushCallback) obj2;
		if (!(obj1 instanceof Intent)) {
			pushCallback.afterParseProcess("");
			return;
		}
		Intent intent = (Intent) obj1;
		if (intent == null || intent.getExtras() == null) {
			pushCallback.afterParseProcess("");
			return;
		}
		try {
			parsePushMsg(intent, pushCallback);
		} catch (Exception e) {
			e.printStackTrace();
			pushCallback.afterParseProcess("");
		}
	}

	private void parsePushMsg(Intent intent, PushCallback pushCallback)
			throws Exception {
		String msg = intent.getExtras().getString("com.parse.Data");
		if (TextUtils.isEmpty(msg)) {
			pushCallback.afterParseProcess("");
			return;
		}
		// {"alert":"新接口", "na":"1111-11-11","na_tag":"cat_11-10043332"}
		JSONObject json = new JSONObject(msg);
		String na = json.optString("na_tag", "");

		if (!TextUtils.isEmpty(na)) {
			String[] arr = UriParse.parsePush(na);
			if (arr != null && arr.length == 2) {
				// 需要显示推送文章
				pushCallback.afterParseProcess(UrlMaker.getPushArticle(arr[1]));
				return;
			}
		}

		pushCallback.afterParseProcess("");
	}

	@Override
	protected void toEnd(boolean success) {
	}
}
