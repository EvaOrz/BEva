package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmediaslate.model.Entry;

/**
 * 商城首页模块item
 * Created by Eva. on 2017/8/16.
 */

public class ShangchengIndex extends Entry {
    private List<ShangchengIndexItem> datas = new ArrayList<>();

    public List<ShangchengIndexItem> getDatas() {
        return datas;
    }

    public void setDatas(List<ShangchengIndexItem> datas) {
        this.datas = datas;
    }

    public static class ShangchengIndexItem extends Entry {
        private String id;
        private String name;
        private int readLevel;
        private String cmsTagId;
        private String icon;
        private String showPrice;
        private long endTime;
        private List<String> picture = new ArrayList<>();
        private int cmsShowStyle;
        private String platformName;
        private String goodId;
        private String descUrl;
        private String protocolList;
        private int level;
        private List<VipGoodList.VipGood> goods = new ArrayList<>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProtocolList() {
            return protocolList;
        }

        public void setProtocolList(String protocolList) {
            this.protocolList = protocolList;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public String getGoodId() {
            return goodId;
        }

        public void setGoodId(String goodId) {
            this.goodId = goodId;
        }

        public String getPlatformName() {
            return platformName;
        }

        public void setPlatformName(String platformName) {
            this.platformName = platformName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getReadLevel() {
            return readLevel;
        }

        public void setReadLevel(int readLevel) {
            this.readLevel = readLevel;
        }

        public String getCmsTagId() {
            return cmsTagId;
        }

        public void setCmsTagId(String cmsTagId) {
            this.cmsTagId = cmsTagId;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getShowPrice() {
            return showPrice;
        }

        public void setShowPrice(String showPrice) {
            this.showPrice = showPrice;
        }

        public List<String> getPicture() {
            return picture;
        }

        public void setPicture(List<String> picture) {
            this.picture = picture;
        }

        public int getCmsShowStyle() {
            return cmsShowStyle;
        }

        public void setCmsShowStyle(int cmsShowStyle) {
            this.cmsShowStyle = cmsShowStyle;
        }

        public String getDescUrl() {
            return descUrl;
        }

        public void setDescUrl(String descUrl) {
            this.descUrl = descUrl;
        }

        public List<VipGoodList.VipGood> getGoods() {
            return goods;
        }

        public void setGoods(List<VipGoodList.VipGood> goods) {
            this.goods = goods;
        }
    }


}
