package cn.com.modernmedia.newtag.mainprocess;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage.MainProcessParseCallBack;
import cn.com.modernmedia.util.UriParse;

/**
 * 首页推送流程
 * 
 * @author zhuqiao
 * 
 */
public class TagMainProcessParse extends TagBaseMainProcess {

	public TagMainProcessParse(Context context,
			MainProcessParseCallBack callBack) {
		super(context, callBack);
	}

	public void parsePushMsg(Intent intent) {
		try {
			String msg = intent.getExtras().getString("com.parse.Data");
			if (!TextUtils.isEmpty(msg)) {
				JSONObject json = new JSONObject(msg);
				String na = json.optString("na_tag", "");// 新文章
				if (TextUtils.isEmpty(na)) {
					if (callBack != null)
						callBack.afterParse("", "");
					return;
				}

				if (!TextUtils.isEmpty(na)) {
					// 进入文章 ==> 跳转至push文章页
					String[] arr = UriParse.parsePush(na);
					if (arr != null && arr.length == 2) {
						if (callBack != null)
							callBack.afterParse(arr[0], arr[1]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
