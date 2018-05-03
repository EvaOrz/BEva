package cn.com.modernmedia.businessweek;

import org.json.JSONObject;

/**
 * 底部radio按钮
 * Created by Eva. on 17/1/6.
 */

public class RadioModel {
    private String title = "";
    private String pictureUrl = "";
    private String pictureUrlSelected = "";
    private int redDotNumber = 0;
    private String actionUrl = "";
    private String responseClassName = "";
    private String linkUrl = "";

    public RadioModel(JSONObject j) {
        setTitle(j.optString("title"));
        setPictureUrl(j.optString("pictureUrl"));
        setPictureUrlSelected(j.optString("pictureUrlSelected"));
        setActionUrl(j.optString("actionUrl"));
        setRedDotNumber(j.optInt("redDotNumber"));
        setLinkUrl(j.optString("linkUrl"));
        setResponseClassName(j.optString("responseClassName"));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPictureUrlSelected() {
        return pictureUrlSelected;
    }

    public void setPictureUrlSelected(String pictureUrlSelected) {
        this.pictureUrlSelected = pictureUrlSelected;
    }

    public int getRedDotNumber() {
        return redDotNumber;
    }

    public void setRedDotNumber(int redDotNumber) {
        this.redDotNumber = redDotNumber;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getResponseClassName() {
        return responseClassName;
    }

    public void setResponseClassName(String responseClassName) {
        this.responseClassName = responseClassName;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
}
