package cn.com.modernmedia.api;

import android.content.Context;

import org.json.JSONObject;

import cn.com.modernmedia.model.Down;

/**
 * 统计
 *
 * @author ZhuQiao
 */
public class DownOperate extends BaseOperate {
    private String url = "";
    private Down down;

    protected DownOperate(Context context) {
        url = UrlMaker.download(context);
        down = new Down();
        setShowToast(false);
    }

    protected Down getDown() {
        return down;
    }

    @Override
    protected String getUrl() {
        return url;
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        if (jsonObject == null) return;
        down.setSuccess(jsonObject.optString("result", "false").equals("true"));
    }

    @Override
    protected void saveData(String data) {

    }

    @Override
    protected String getDefaultFileName() {
        return "";
    }

}
