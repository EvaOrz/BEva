package cn.com.modernmediausermodel.vip;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediausermodel.api.UrlMaker;

/**
 * vip基本信息 行业，职位，年收入
 *
 * @author: zhufei
 */
public class GetVipInfoOperate extends SlateBaseOperate {
    private VipInfo info;

    public GetVipInfoOperate() {
        info = new VipInfo();
    }

    @Override
    protected String getUrl() {
        return UrlMaker.getVipInfo();
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        if (isNull(jsonObject) || !TextUtils.equals(jsonObject.optString("code"), "200")) {
            return;
        }

        JSONObject dataObj = jsonObject.optJSONObject("data");
        if (isNull(dataObj))
            return;

        info.updatetime = dataObj.optString("updatetime");
        parseLevel(dataObj.optJSONArray("vip_level"));
        parseCategory(dataObj);
    }

    private void parseCategory(JSONObject dataObj) {
        Iterator<String> keys = dataObj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (TextUtils.equals(key, "vip_level") || TextUtils.equals(key, "updatetime"))
                continue;

            JSONObject obj = dataObj.optJSONObject(key);
            if (isNull(obj))
                continue;

            VipInfo.VipCategory category = parseCatDetail(obj);
            parseChild(obj.optJSONArray("child"), category, false);
            info.categoryMap.put(category.cate_id, category);
        }
    }

    private void parseChild(JSONArray array, VipInfo.VipCategory category, boolean isMinChaild) {
        if (isNull(array))
            return;

        int length = array.length();
        JSONObject obj;
        VipInfo.VipChildCategory childCategory;
        for (int i = 0; i < length; i++) {
            obj = array.optJSONObject(i);
            if (isNull(obj))
                continue;

            childCategory = parseCatDetail(obj);
            parseChild(obj.optJSONArray("child"), childCategory, true);

            if (isMinChaild)// 最底层
                ((VipInfo.VipChildCategory) category).secondCategoryList.add(childCategory);
            else
                category.firstCategoryList.add(childCategory);
        }
    }

    private VipInfo.VipChildCategory parseCatDetail(JSONObject obj) {
        VipInfo.VipChildCategory category = new VipInfo.VipChildCategory();
        category.cate_id = obj.optString("cate_id");
        category.category = obj.optString("category");
        category.pid = obj.optString("pid");
        category.status = obj.optString("status");
        category.order = obj.optString("order");

        return category;
    }

    /**
     * 解析vip_level
     *
     * @param array
     */
    private void parseLevel(JSONArray array) {
        if (isNull(array))
            return;

        int length = array.length();
        JSONObject obj;
        VipInfo.VipLevel level;
        for (int i = 0; i < length; i++) {
            obj = array.optJSONObject(i);
            if (isNull(obj))
                continue;

            level = new VipInfo.VipLevel();
            level.level = obj.optInt("level");
            level.name = obj.optString("name");
            info.vipLevelList.add(level);
        }
    }

    @Override
    protected void saveData(String data) {

    }

    @Override
    protected String getDefaultFileName() {
        return null;
    }

    public VipInfo getVipInfo() {
        return info;
    }
}
