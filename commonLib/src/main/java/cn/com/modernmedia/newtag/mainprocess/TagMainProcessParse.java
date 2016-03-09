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
	public static final String KEY_PUSH_ARTICLE_LEVEL = "push_article_level";

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
			pushCallback.afterParseProcess("", 0);
			return;
		}
		Intent intent = (Intent) obj1;
		if (intent == null || intent.getExtras() == null) {
			pushCallback.afterParseProcess("", 0);
			return;
		}
		try {
			parsePushMsg(intent, pushCallback);
		} catch (Exception e) {
			e.printStackTrace();
			pushCallback.afterParseProcess("", 0);
		}
	}

	private void parsePushMsg(Intent intent, PushCallback pushCallback)
			throws Exception {
		String msg = intent.getExtras().getString("com.parse.Data");
		if (TextUtils.isEmpty(msg)) {
			pushCallback.afterParseProcess("", 0);
			return;
		}
		/*
		 * {"alert":"新接口",
		 * "na":"1111-11-11","na_tag":"cat_11-10043332","level":"0"}
		 */
		JSONObject json = new JSONObject(msg);
		String na = json.optString("na_tag", "");
		int level = Integer.valueOf(json.optString("level"));// ibloomberg3.3新增推送付费文章

		if (!TextUtils.isEmpty(na)) {
			String[] arr = UriParse.parsePush(na);
			if (arr != null && arr.length == 2) {
				// 需要显示推送文章
				pushCallback.afterParseProcess(UrlMaker.getPushArticle(arr[1]),
						level);
				return;
			}
		}

		pushCallback.afterParseProcess("", 0);
	}

	@Override
	protected void toEnd(boolean success) {
	}
}
