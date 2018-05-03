package cn.com.modernmedia.api;

import android.util.Log;

import org.json.JSONObject;

import cn.com.modernmedia.model.Version;

/**
 * 判断app版本接口
 * <p>
 * appid：应用标识
 * type：终端类型（ios|android）
 * version：当前app版本
 * src：下载渠道
 *
 * @author ZhuQiao
 */
public class CheckVersionOperate extends BaseOperate {
    private String url = "";
    private Version version;

    protected CheckVersionOperate(String v) {
        url = UrlMaker.checkVersion(v);
        version = new Version();
        setShowToast(false);
    }

    protected Version getVersion() {
        return version;
    }

    @Override
    protected String getUrl() {
        return url;
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        if (jsonObject == null) return;
        Log.e("CheckVersionOperate", jsonObject.toString());
        JSONObject data = jsonObject.optJSONObject("data");
        version.setVersion(data.optInt("cur_ver", -1));
        version.setSrc(data.optString("src", ""));
        version.setChangelog(data.optString("feature", ""));
        version.setDownload_url(data.optString("download", ""));
    }

    @Override
    protected void saveData(String data) {
    }

    @Override
    protected String getDefaultFileName() {
        return "";
    }

}
