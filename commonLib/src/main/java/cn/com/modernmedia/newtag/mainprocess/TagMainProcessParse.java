package cn.com.modernmedia.newtag.mainprocess;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import org.json.JSONObject;

import cn.com.modernmedia.newtag.mainprocess.TagProcessManage.MainProcessParseCallBack;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage.PushCallback;
import cn.com.modernmedia.util.UriParse;

/**
 * 首页推送流程
 *
 * @author zhuqiao
 */
public class TagMainProcessParse extends BaseTagMainProcess {

    public TagMainProcessParse(Context context, MainProcessParseCallBack callBack) {
        super(context, callBack);
    }

    @Override
    public void onStart(Object... objs) {
        if (objs == null || objs.length < 2) return;
        Object obj1 = objs[0];
        Object obj2 = objs[1];
        if (!(obj2 instanceof PushCallback)) return;
        PushCallback pushCallback = (PushCallback) obj2;
        if (!(obj1 instanceof Intent)) {
            pushCallback.afterParseProcess("","", 0);
            return;
        }
        Intent intent = (Intent) obj1;
        //        if (intent == null || intent.getExtras() == null) {
        //            pushCallback.afterParseProcess("", 0);
        //            return;
        //        }
        try {
            String pushMsg = intent.getExtras().getString("com.parse.Data");
            String schemeUrl = intent.getAction();

            if (TextUtils.isEmpty(pushMsg) && TextUtils.isEmpty(schemeUrl)) {
                pushCallback.afterParseProcess("","", 0);
                return;
            } else if (!TextUtils.isEmpty(schemeUrl) && Intent.ACTION_VIEW.equals(schemeUrl)) {

                schemeUrl(intent.getData(), pushCallback);
            } else parsePushMsg(pushMsg, pushCallback);
        } catch (Exception e) {
            e.printStackTrace();
            pushCallback.afterParseProcess("","", 0);
        }
    }

    /**
     * @param fromHtmlArticleUri
     * @param pushCallback
     */
    private void schemeUrl(Uri fromHtmlArticleUri, PushCallback pushCallback) {
        if (fromHtmlArticleUri == null || TextUtils.isEmpty(fromHtmlArticleUri.toString())) {
            pushCallback.afterParseProcess("","", 0);
            return;
        }
        String url = fromHtmlArticleUri.toString();
//        url = url.replace("slate1", "slate");

        pushCallback.afterParseProcess(url,"", -1);
    }

    private void parsePushMsg(String msg, PushCallback pushCallback) throws Exception {

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
                pushCallback.afterParseProcess("",arr[1], level);
                return;
            }
        }

        pushCallback.afterParseProcess("","", 0);
    }

    @Override
    protected void toEnd(boolean success) {
    }
}
