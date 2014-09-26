package cn.com.modernmediausermodel.widget;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import cn.com.modernmedia.widget.CheckScrollListview;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.adapter.MyCoinListAdapter;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.CardViewListener;
import cn.com.modernmediausermodel.listener.UserCentChangeListener;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.ActionRuleList;
import cn.com.modernmediausermodel.model.Goods;
import cn.com.modernmediausermodel.model.Goods.GoodsItem;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.model.UserCent;
import cn.com.modernmediausermodel.util.UserCentManager;
import cn.com.modernmediausermodel.util.UserDataHelper;

/**
 * 我的金币
 * 
 * @author jiancong
 * 
 */
public class MyCoinView implements CardViewListener {
	private Context mContext;
	private View view;
	private CheckScrollListview listView;
	private MyCoinListAdapter adapter;
	private User mUser;
	private UserOperateController controller;
	private TextView coinNumberText;
	private ActionRuleList actionRuleList;
	private Goods goods;
	private int coinNumber = 0;
	private LayoutInflater inflater;

	public MyCoinView(Context context) {
		this.mContext = context;
		init();
	}

	private void init() {
		controller = UserOperateController.getInstance(mContext);
		inflater = LayoutInflater.from(mContext);
		mUser = UserDataHelper.getUserLoginInfo(mContext);
		if (mUser == null)
			return;
		view = inflater.inflate(R.layout.activity_my_coin, null);
		listView = (CheckScrollListview) view
				.findViewById(R.id.my_coin_listview);
		// 头部
		View headView = inflater.inflate(R.layout.my_coin_listview_head, null);
		coinNumberText = (TextView) headView.findViewById(R.id.my_coin_number);
		listView.addHeaderView(headView, null, false);

		adapter = new MyCoinListAdapter(mContext);
		listView.setAdapter(adapter);
		// 提交未获取金币的已读文章
		UserCentManager.getInstance(mContext).addArticleCoinCent(
				new UserCentChangeListener() {

					@Override
					public void change(int number) {
						// 获取金币数量
						getUserCoinNumber();
					}
				}, false);
	}

	/**
	 * 获取用户金币数
	 */
	private void getUserCoinNumber() {
		controller.getUserCoinNumber(mUser.getUid(), mUser.getToken(),
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof UserCent) {
							coinNumber = ((UserCent) entry).getNumber();
						}
						coinNumberText.setText(coinNumber + "");
						// 获取规则列表及商品列表数据
						getActionRules();
					}
				});
	}

	/**
	 * 获取规则列表
	 * 
	 */
	private void getActionRules() {
		Tools.showLoading(mContext, true);
		controller.getActionRules(new UserFetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				if (entry != null && entry instanceof ActionRuleList) {
					actionRuleList = (ActionRuleList) entry;
				} else {
					actionRuleList = new ActionRuleList();
				}
				getGoodsList();
			}
		});
	}

	/**
	 * 获取商品列表
	 */
	private void getGoodsList() {
		controller.getGoodsList(new UserFetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				Tools.showLoading(mContext, false);
				if (entry instanceof Goods) {
					goods = (Goods) entry;
				} else {
					goods = new Goods();
				}
				doAfterGetGoodsList();
			}
		});
	}

	private void doAfterGetGoodsList() {
		List<Entry> list = new ArrayList<Entry>();
		String firstActionRuleItemId = " ";
		String firstGoodsId = " ";
		if (ParseUtil.listNotNull(actionRuleList.getList())) {
			list.addAll(actionRuleList.getList());
			firstActionRuleItemId = actionRuleList.getList().get(0).getId();
		}
		if (ParseUtil.listNotNull(goods.getList())) {
			list.addAll(goods.getList());
			firstGoodsId = goods.getList().get(0).getId();
		}
		adapter.setFirstItem(firstActionRuleItemId, firstGoodsId);
		adapter.setData(list);
		// 底部
		View footerView = inflater.inflate(R.layout.my_coin_listview_footer,
				null);
		listView.addFooterView(footerView);
	}

	/**
	 * 做兑换操作
	 * 
	 * @param goodsItem
	 *            商品信息
	 */
	public void doExchange(final GoodsItem goodsItem) {
		final int number = goodsItem.getPrice();
		final boolean isEnough = number <= coinNumber;
		int title = isEnough ? R.string.sure : R.string.msg_has_error;
		String msg = mContext.getString(R.string.msg_your_coin_is_not_enough);
		if (isEnough) {
			msg = ParseUtil.parseString(mContext, R.string.msg_if_exchange,
					number + "", goodsItem.getName());
		}
		AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle(title)
				.setMessage(msg)
				.setPositiveButton(R.string.sure, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (isEnough) {
							exchange(goodsItem.getId(), number);
						}
						if (dialog != null)
							dialog.dismiss();
					}
				}).create();
		if (isEnough) { // 增加取消button
			dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
					mContext.getString(R.string.cancel), new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (dialog != null)
								dialog.dismiss();
						}
					});
		}
		try {
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 兑换
	 * 
	 * @param goodsId
	 * @param number
	 */
	private void exchange(String goodsId, final int number) {
		User user = UserDataHelper.getUserLoginInfo(mContext);
		if (user == null)
			return;
		Tools.showLoading(mContext, true);
		UserOperateController.getInstance(mContext).addGoodsOrder(
				user.getUid(), user.getToken(), goodsId,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry != null && entry instanceof ErrorMsg) {
							Tools.showLoading(mContext, false);
							if (((ErrorMsg) entry).getNo() == 1) { // 获取成功
								// 更新显示的金币数量
								coinNumber -= number;
								coinNumberText.setText(coinNumber + "");
								Tools.showToast(mContext,
										R.string.exchange_success);
							} else {
								Tools.showToast(mContext,
										R.string.exchange_failed);
							}
						} else {
							Tools.showToast(mContext, R.string.exchange_failed);
						}
					}
				});
	}

	@Override
	public void showTitleBar(boolean show) {
		view.findViewById(R.id.my_coin_bar_layout).setVisibility(
				show ? View.VISIBLE : View.GONE);
	}

	public View getBackBtn() {
		return view.findViewById(R.id.my_coin_button_back);
	}

	@Override
	public View fetchView() {
		return view;
	}
}
