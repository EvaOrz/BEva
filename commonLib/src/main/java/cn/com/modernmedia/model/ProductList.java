package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmediaslate.model.Entry;

/**
 * 支付商品model
 *
 * @author lusiyuan
 */
public class ProductList extends Entry {
    private static final long serialVersionUID = 1L;
    private List<Product> pros = new ArrayList<Product>();

    public ProductList() {
    }

    public List<Product> getPros() {
        return pros;
    }

    public void setPros(List<Product> pros) {
        this.pros = pros;
    }

    public static class Product extends Entry {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private String pid; // "bbwcsub_oneyearsub",
        private String name;// "优惠订阅 128 元/年",
        private String price;// "128",
        private String unit;// "month",
        private String num; // "12",
        private String type;// : "2"
        private long duration;//32140800
        private List<PayType> payTypeList;

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public Product() {
        }

        public List<PayType> getPayTypeList() {
            return payTypeList;
        }

        public void setPayTypeList(List<PayType> payTypeList) {
            this.payTypeList = payTypeList;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class PayType {
        private String payTypeId;// 支付方式id
        private String payTypeName;// 支付方式描述

        public PayType() {

        }

        public String getPayTypeId() {
            return payTypeId;
        }

        public void setPayTypeId(String payTypeId) {
            this.payTypeId = payTypeId;
        }

        public String getPayTypeName() {
            return payTypeName;
        }

        public void setPayTypeName(String payTypeName) {
            this.payTypeName = payTypeName;
        }
    }

}
