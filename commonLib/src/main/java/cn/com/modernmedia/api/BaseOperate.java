package cn.com.modernmedia.api;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.unit.SlatePrintHelper;

/**
 * 封装服务器返回数据父类
 *
 * @author ZhuQiao
 */
public abstract class BaseOperate extends SlateBaseOperate {

    @Override
    protected CallBackData fetchDataFromSD() {
        CallBackData callBackData = new CallBackData();
        String cache = getDataFromSD(getDefaultFileName());
        if (TextUtils.isEmpty(cache)) {
            SlatePrintHelper.printErr("fetch cache from sd error:" + getUrl());
            callBackData.success = false;
            callBackData.data = null;
        } else {
            PrintHelper.print("fetch cache from sd success:" + getUrl());
            callBackData.success = true;
            callBackData.data = cache;
        }

        return callBackData;
    }

    /**
     * 获取文件数据
     *
     * @param name
     * @return
     */
    private String getDataFromSD(String name) {
        if (TextUtils.isEmpty(name) || !FileManager.containFile(name)) return null;
        String data = FileManager.getApiData(name);
        if (TextUtils.isEmpty(data)) return null;
        try {
            new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
            SlatePrintHelper.printErr(name + "文件被异常修改，无法封装成json数据！！");
            FileManager.deleteFile(name);
            return null;
        }
        return data;
    }

    /**
     * 判断是否为广告（过滤老版本广告）
     *
     * @param obj
     * @return
     */
    protected boolean isAdv(JSONObject obj) {
        if (isNull(obj)) return false;
        JSONObject property = obj.optJSONObject("property");
        if (isNull(property)) return false;
        return property.optInt("isadv", 0) == 1;
    }

    @Override
    protected Map<String, String> getHeader() {

        return ModernMediaTools.getHttpHeaders(mContext);
    }

}
