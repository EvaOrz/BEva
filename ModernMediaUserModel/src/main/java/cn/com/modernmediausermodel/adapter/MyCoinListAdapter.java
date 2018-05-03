package cn.com.modernmediausermodel.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import cn.com.modernmediaslate.adapter.ViewHolder;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediausermodel.MyCoinActivity;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.model.ActionRuleList.ActionRuleItem;
import cn.com.modernmediausermodel.model.Goods.GoodsItem;

public class MyCoinListAdapter extends CheckScrollAdapter<Entry> {
	private Context mContext;
	private String firstActionRuleId = ""; // 获取更多金币列表第一项id
	private String firstGoodsId = ""; // 兑换列表的第一项id

	public MyCoinListAdapter(Context context) {
		super(context);
		this.mContext = context;
	}

	public void setData(List<Entry> list) {
		synchronized (list) {
			for (Entry entry : list) {
				add(entry);
			}
		}
	}

	/**
	 * 设置列表的第一项ID
	 * 
	 * @param firstActionRuleId
	 * @param firstGoodsId
	 */
	public void setFirstItem(String firstActionRuleId, String firstGoodsId) {
		this.firstActionRuleId = firstActionRuleId;
		this.firstGoodsId = firstGoodsId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entry entry = getItem(position);
		ViewHolder viewHolder = ViewHolder.get(mContext, convertView,
				R.layout.my_coin_listview_item);
		View titleLayout = viewHolder.getView(R.id.my_coin_item_head_layout);
		TextView title = viewHolder.getView(R.id.my_coin_item_head_title);
		TextView name = viewHolder.getView(R.id.my_coin_item_title);
		TextView desc = viewHolder.getView(R.id.my_coin_item_desc);
		TextView number = viewHolder.getView(R.id.my_coin_item_number);
		Button exchange = viewHolder.getView(R.id.my_coin_item_button);

		if (entry instanceof ActionRuleItem) {
			ActionRuleItem actionRuleItem = (ActionRuleItem) entry;
			if (actionRuleItem.getId().equals(firstActionRuleId)) {
				titleLayout.setVisibility(View.VISIBLE);
				title.setText(R.string.get_more_coin);
			} else {
				titleLayout.setVisibility(View.GONE);
			}
			name.setText(actionRuleItem.getTitle());
			desc.setText(actionRuleItem.getDesc());
			desc.setTextColor(mContext.getResources().getColor(
					R.color.my_coin_green_color));
			number.setText("+" + actionRuleItem.getCent());
			number.setTextColor(mContext.getResources().getColor(
					R.color.my_coin_green_color));
			number.setVisibility(View.VISIBLE);
			exchange.setVisibility(View.GONE);
		} else if (entry instanceof GoodsItem) {
			final GoodsItem goodsItem = (GoodsItem) entry;
			if (goodsItem.getId().equals(firstGoodsId)) {
				titleLayout.setVisibility(View.VISIBLE);
				title.setText(R.string.exchange);
			} else {
				titleLayout.setVisibility(View.GONE);
			}
			name.setText(goodsItem.getName());
			desc.setText(ParseUtil.parseString(mContext, R.string.coin,
					goodsItem.getPrice() + ""));
			desc.setTextColor(mContext.getResources().getColor(
					R.color.my_coin_red_color));
			number.setVisibility(View.GONE);
			exchange.setVisibility(View.VISIBLE);
			exchange.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					((MyCoinActivity) mContext).doExchange(goodsItem);
				}
			});
		}
		return viewHolder.getConvertView();
	}
}
