package cn.com.modernmediausermodel.api;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlatePrintHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.db.UserListDb;
import cn.com.modernmediausermodel.model.UserCardInfoList;
import cn.com.modernmediausermodel.model.UserCardInfoList.UserCardInfo;
import cn.com.modernmediausermodel.widget.RecommendUserView;

public class GetRecommendUsersOperate extends UserBaseOperate {
    private UserCardInfoList userCardInfoList;
    private String uid;
    private int pageType;
    private String offsetId;
    private UserListDb db;
    private int lastDbId; // 上页数据最后一条数据的关键字

    public UserCardInfoList getUserCardInfoList() {
        return userCardInfoList;
    }

    public GetRecommendUsersOperate(String uid, int pageType, String offsetId,
                                    int lastDbId, Context context) {
        this.userCardInfoList = new UserCardInfoList();
        this.uid = uid;
        this.pageType = pageType;
        this.offsetId = offsetId;
        this.lastDbId = lastDbId;
        db = UserListDb.getInstance(context);
        cacheIsDb = true;
    }

    @Override
    protected String getUrl() {
        String url = "";
        switch (pageType) {
            case RecommendUserView.PAGE_RECOMMEND_FRIEND:
                url = UrlMaker.getReccommendUsers();
                break;
            case RecommendUserView.PAGE_FRIEND:
                url = UrlMaker.getFriends() + "/uid/" + uid;
                break;
            case RecommendUserView.PAGE_FANS:
                url = UrlMaker.getFans() + "/uid/" + uid;
                break;
            default:
                break;
        }
        if (!TextUtils.isEmpty(url)) {
            url += "/customer_uid/" + Tools.getUid(getmContext());
            // 加载更多时，必须带有pages参数
            if (!TextUtils.isEmpty(offsetId)) {
                url += "/pages/0/offsetid/" + offsetId;
            }
        }
        return url;
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        userCardInfoList.setUid(jsonObject.optString("uid", ""));
        userCardInfoList.setOffsetId(jsonObject.optString("offsetid", ""));
        JSONArray array;
        if (pageType == RecommendUserView.PAGE_RECOMMEND_FRIEND) {
            array = jsonObject.optJSONArray("user");
        } else {
            array = jsonObject.optJSONArray("auid");
        }
        if (array != null) {
            int len = array.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = array.optJSONObject(i);
                if (object != null) {
                    UserCardInfo userCardInfo = new UserCardInfo();
                    userCardInfo.setUid(object.optString("uid", ""));
                    userCardInfo.setNickName(object.optString("nickname", ""));
                    userCardInfo.setAvatar(object.optString("avatar", ""));
                    userCardInfo.setFollowNum(object.optInt("follow", 0));
                    userCardInfo.setFansNum(object.optInt("follower", 0));
                    userCardInfo.setCardNum(object.optInt("cardnum", 0));
                    userCardInfo.setIsFollowed(object.optInt("isfollow", 0));
                    userCardInfoList.getList().add(userCardInfo);
                }
            }
            // 存入数据库
            if (offsetId.equals("0")) {
                db.clearTable(pageType + "", uid);
            }
            db.addUsersInfo(userCardInfoList.getList(), pageType + "",
                    userCardInfoList.getOffsetId(), uid);
        }
        JSONObject error = jsonObject.optJSONObject("error");
        if (error != null) {
            userCardInfoList.getError().setNo(error.optInt("code", 0));
            userCardInfoList.getError().setDesc(error.optString("msg"));
        }
    }

    @Override
    protected CallBackData fetchDataFromDB() {
        CallBackData callBackData = new CallBackData();
        UserCardInfoList infoList = db.getUserInfoList(pageType + "", uid,
                lastDbId);
        if (ParseUtil.listNotNull(infoList.getList())) {
            userCardInfoList = infoList;
            callBackData.success = true;
            SlatePrintHelper.print("from db:" + "====" + getUrl());
        }
        return callBackData;
    }

    @Override
    protected void saveData(String data) {
    }

    @Override
    protected String getDefaultFileName() {
        return null;
    }

}
