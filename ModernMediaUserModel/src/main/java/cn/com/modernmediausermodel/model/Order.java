package cn.com.modernmediausermodel.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmediaslate.model.Entry;

/**
 * 积分商城 兑换按钮状态
 *
 * @author: zhufei
 */
public class Order extends Entry {
    private static final long serialVersionUID = 1L;
    private List<OrderItem> list = new ArrayList<OrderItem>();

    public List<OrderItem> getList() {
        return list;
    }

    public void setList(List<OrderItem> list) {
        this.list = list;
    }

    public static class OrderItem extends Entry {
        private static final long serialVersionUID = 1L;
        private int goodsid;

        public int getGoodsid() {
            return goodsid;
        }

        public void setGoodsid(int goodsid) {
            this.goodsid = goodsid;
        }
    }
}
