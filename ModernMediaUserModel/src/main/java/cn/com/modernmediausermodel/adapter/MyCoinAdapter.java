package cn.com.modernmediausermodel.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmediaslate.adapter.ViewHolder;
import cn.com.modernmediausermodel.NewCoinActivity;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.model.ActionRuleList;
import cn.com.modernmediausermodel.model.Goods;
import cn.com.modernmediausermodel.model.Order;

/**
 * 我的积分
 *
 * @author: zhufei
 */
public class MyCoinAdapter extends BaseAdapter {
    private Context mContext;
    private List<ActionRuleList.ActionRuleItem> mRuleList;// 规则列表
    private List<Goods.GoodsItem> mGoodList;// 商品列表
    private List<Order.OrderItem> mOrderList;// 已兑换列表

    public MyCoinAdapter(Context context) {
        mContext = context;
    }

    public void setRuleList(List<ActionRuleList.ActionRuleItem> ruleList) {
        if (ruleList == null || ruleList.size() == 0)
            return;

        mRuleList = ruleList;
        notifyDataSetChanged();
    }

    public void setGoodList(List<Goods.GoodsItem> goodList) {
        if (goodList == null || goodList.size() == 0)
            return;

        mGoodList = goodList;
        notifyDataSetChanged();
    }

    public void setOrderList(List<Order.OrderItem> orderList) {
        if (orderList == null || orderList.size() == 0)
            return;

        mOrderList = orderList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int ruleCount = mRuleList == null ? 0 : mRuleList.size();
        int goodCount = mGoodList == null ? 0 : mGoodList.size();
        return ruleCount + goodCount;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (mRuleList != null && position < mRuleList.size())
            return 0;// 规则

        return 1;// 兑换
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (type == 0) {
            // 规则
            ViewHolder holder = ViewHolder.get(mContext, convertView, R.layout.item_my_coin_rule);
            View headView = holder.getView(R.id.item_coin_rule_title);
            TextView name = holder.getView(R.id.item_coin_rule_name);
            TextView cent = holder.getView(R.id.item_coin_rule_cent);
            TextView status = holder.getView(R.id.item_coin_rule_status);

            ActionRuleList.ActionRuleItem rule = mRuleList.get(position);
            headView.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            name.setText(rule.getTitle());
            cent.setText("+" + rule.getCent());
            status.setText(rule.getRulestatusmessage());
            if (rule.getRulestatus() == 0) {
                status.setTextColor(mContext.getResources().getColor(R.color.my_coin_green_color));
            } else {
                status.setTextColor(mContext.getResources().getColor(R.color.my_coin_notice_color));
            }

            return holder.getConvertView();
        } else {
            // 商品兑换
            ViewHolder holder = ViewHolder.get(mContext, convertView, R.layout.item_my_coin_goods);
            View headView = holder.getView(R.id.item_coin_good_title);
            TextView time = holder.getView(R.id.item_coin_good_time);
            TextView price = holder.getView(R.id.item_coin_good_price);
            TextView exchange = holder.getView(R.id.item_coin_good_exchange_btn);

            final int ruleCount = mRuleList == null ? 0 : mRuleList.size();
            final Goods.GoodsItem good = mGoodList.get(position - ruleCount);

            headView.setVisibility(position == ruleCount ? View.VISIBLE : View.GONE);
            time.setText(good.getName());
            price.setText(good.getPrice() + "");
            final boolean hasExchanged = checkHasExchanged(good);
            if (hasExchanged) {
                exchange.setBackgroundColor(mContext.getResources().getColor(R.color.my_coin_notice_color));
                exchange.setText(R.string.has_exchanged);
            } else {
                exchange.setBackgroundResource(R.drawable.black_btn_bg);
                exchange.setText(R.string.exchange);
            }
            exchange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogHelper.checkVipIntegralExchange(mContext);
                    if (!hasExchanged && mContext instanceof NewCoinActivity) {
                        ((NewCoinActivity) mContext).doExchange(good);
                    }
                }
            });

            return holder.getConvertView();
        }

    }

    /**
     * 判断商品是否已兑换
     *
     * @param good
     * @return
     */
    private boolean checkHasExchanged(Goods.GoodsItem good) {
        if (mOrderList == null || mOrderList.size() == 0)
            return false;

        for (Order.OrderItem item : mOrderList) {
            if (TextUtils.equals(item.getGoodsid() + "", good.getId()))
                return true;
        }

        return false;
    }
}
