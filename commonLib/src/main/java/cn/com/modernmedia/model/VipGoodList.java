package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmediaslate.model.Entry;

/**
 * VIP套餐商品4.1.0
 *
 * @author: zhufei
 */

public class VipGoodList extends Entry {
    private static final long serialVersionUID = 1L;
    private List<VipGood> list = new ArrayList<>();

    public List<VipGood> getList() {
        return list;
    }

    public void setList(List<VipGood> list) {
        this.list = list;
    }

    public static class VipGood extends Entry {
        private static final long serialVersionUID = 1L;
        private String goodId = "";      //商品ID
        private String goodName = "";    //商品名称
        private String goodType = "";    //商品类型：订阅、消耗、永久
        private int pirce;       //商品价格：单位:分(人民币)
        private String showPrice = "";   //显示价格。例如：1元/年
        private String desc = "";        //商品描述
        private String num = "";         //商品数量
        private String unit = "";        //商品单位
        private String expireTime = "";  //有效期。商品类型为订阅时，表示有效截止时间点

        private int needAddress = 0; //是否必须填写配送信息 1需要，0不需要
        private int show_address_input = 0; //是否显示配送地址录入界面,
        private int show_notice_input = 0; //是否显示通知录入界面,
        private int neednotice = 0; //通知信息是否必填项

        private int isExchange;// 0 不显示：1：显示【套餐升级】
        private int durationLeft; // 套餐剩余天数
        private int durationTotal;//套餐总
        private int durationAdd;
        private String durationUnit = "";// 套餐剩余单位
        private List<VipPayType> payTypeLists = new ArrayList<>();
        private List<Fun> funList = new ArrayList<>();

        public int getNeedAddress() {
            return needAddress;
        }

        public void setNeedAddress(int needAddress) {
            this.needAddress = needAddress;
        }

        public int getShow_address_input() {
            return show_address_input;
        }

        public void setShow_address_input(int show_address_input) {
            this.show_address_input = show_address_input;
        }

        public int getShow_notice_input() {
            return show_notice_input;
        }

        public void setShow_notice_input(int show_notice_input) {
            this.show_notice_input = show_notice_input;
        }

        public int getNeednotice() {
            return neednotice;
        }

        public void setNeednotice(int neednotice) {
            this.neednotice = neednotice;
        }

        public int getDurationLeft() {
            return durationLeft;
        }

        public void setDurationLeft(int durationLeft) {
            this.durationLeft = durationLeft;
        }

        public int getDurationTotal() {
            return durationTotal;
        }

        public void setDurationTotal(int durationTotal) {
            this.durationTotal = durationTotal;
        }

        public int getDurationAdd() {
            return durationAdd;
        }

        public void setDurationAdd(int durationAdd) {
            this.durationAdd = durationAdd;
        }

        public String getDurationUnit() {
            return durationUnit;
        }

        public void setDurationUnit(String durationUnit) {
            this.durationUnit = durationUnit;
        }

        public String getGoodId() {
            return goodId;
        }

        public void setGoodId(String goodId) {
            this.goodId = goodId;
        }

        public String getGoodName() {
            return goodName;
        }

        public void setGoodName(String goodName) {
            this.goodName = goodName;
        }

        public String getGoodType() {
            return goodType;
        }

        public void setGoodType(String goodType) {
            this.goodType = goodType;
        }

        public int getPirce() {
            return pirce;
        }

        public void setPirce(int pirce) {
            this.pirce = pirce;
        }

        public String getShowPrice() {
            return showPrice;
        }

        public void setShowPrice(String showPrice) {
            this.showPrice = showPrice;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public List<Fun> getFunList() {
            return funList;
        }

        public void setFunList(List<Fun> funList) {
            this.funList = funList;
        }

        public List<VipPayType> getPayTypeLists() {
            return payTypeLists;
        }

        public void setPayTypeLists(List<VipPayType> payTypeLists) {
            this.payTypeLists = payTypeLists;
        }

        public String getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(String expireTime) {
            this.expireTime = expireTime;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }



        public int getIsExchange() {
            return isExchange;
        }

        public void setIsExchange(int isExchange) {
            this.isExchange = isExchange;
        }
    }

    public static class VipPayType extends Entry {
        private static final long serialVersionUID = 1L;
        private String payTypeId = "";           //支付方式ID
        private String payTypeName = "";         //支付方式名称
        private String isRecommend = "";         //是否为推荐支付方式
        private String recommendIconUrl = "";    //支付方式推送的ICON。http://s1.cdn.bbwc.cn/statics/images/pay/tuijian@3x.png

        public String getPayTypeId() {
            return payTypeId;
        }

        public void setPayTypeId(String payTypeId) {
            this.payTypeId = payTypeId;
        }

        public String getRecommendIconUrl() {
            return recommendIconUrl;
        }

        public void setRecommendIconUrl(String recommendIconUrl) {
            this.recommendIconUrl = recommendIconUrl;
        }

        public String getIsRecommend() {
            return isRecommend;
        }

        public void setIsRecommend(String isRecommend) {
            this.isRecommend = isRecommend;
        }

        public String getPayTypeName() {
            return payTypeName;
        }

        public void setPayTypeName(String payTypeName) {
            this.payTypeName = payTypeName;
        }
    }

    public static class Fun extends Entry {
        private static final long serialVersionUID = 1L;
        private String funId = "";           //功能id
        private String funName = "";         //功能名称
        private String desc = "";            //功能描述
        private VipIcon vipIcon;     //icon
        private String num = "";             //功能数量
        private String showName = "";        //功能的显示名称
        private String type = "";            //功能类型。虚拟、实物

        public String getFunId() {
            return funId;
        }

        public void setFunId(String funId) {
            this.funId = funId;
        }

        public String getFunName() {
            return funName;
        }

        public void setFunName(String funName) {
            this.funName = funName;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getShowName() {
            return showName;
        }

        public void setShowName(String showName) {
            this.showName = showName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public VipIcon getVipIcon() {
            return vipIcon;
        }

        public void setVipIcon(VipIcon vipIcon) {
            this.vipIcon = vipIcon;
        }
    }

    public static class VipIcon extends Entry {
        private static final long serialVersionUID = 1L;
        private String normal = "";//"普通 icon地址：未过期的场景使用这个地址",
        private String selected = "";//"暂时无用"
        private String style1_normal = "";//"特殊 icon地址: 我的VIP中过期的权限"

        public String getNormal() {
            return normal;
        }

        public void setNormal(String normal) {
            this.normal = normal;
        }

        public String getStyle1_normal() {
            return style1_normal;
        }

        public void setStyle1_normal(String style1_normal) {
            this.style1_normal = style1_normal;
        }

        public String getSelected() {
            return selected;
        }

        public void setSelected(String selected) {
            this.selected = selected;
        }
    }
}
