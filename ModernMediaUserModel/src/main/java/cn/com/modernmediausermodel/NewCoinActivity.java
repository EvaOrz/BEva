package cn.com.modernmediausermodel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.adapter.MyCoinAdapter;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.ActionRuleList;
import cn.com.modernmediausermodel.model.Goods;
import cn.com.modernmediausermodel.model.Order;
import cn.com.modernmediausermodel.vip.VipInfoActivity;


/**
 * 商周3.4积分规则优化
 *
 * @AUTHOR: ZHUFEI
 */
public class NewCoinActivity extends SlateBaseActivity implements View.OnClickListener {
    private User mUser;
    private UserOperateController controller;
    private ActionRuleList actionRuleList;
    private Goods goods;
    private Order order = new Order();
    private int coinNumber = 0;
    public static final int CODE_ICON = 20;
    private String goodid = "";

    private ImageView back, mMyCoinAdImg;
    private ListView mListView;
    private MyCoinAdapter mAdapter;
    private TextView mMyCoinNum;// 总积分

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_coin_new_view);
        controller = UserOperateController.getInstance(this);
        mUser = SlateDataHelper.getUserLoginInfo(this);
        if (mUser == null) return;
        init();
        initEvent();
        Tools.showLoading(this, true);
        // 提交未获取金币的已读文章
        //        UserCentManager.getInstance(this).addArticleCoinCent(new UserCentChangeListener() {
        //
        //            @Override
        //            public void change(int number) {
        getActionRules();
        getGoodsList();
        //            }
        //        }, false);
    }

    private void init() {
        back = (ImageView) findViewById(R.id.my_coin_button_back);
        mListView = (ListView) findViewById(R.id.my_coin_listview);
        View headView = LayoutInflater.from(this).inflate(R.layout.my_coin_head, null);
        mMyCoinNum = (TextView) headView.findViewById(R.id.my_coin_number);

        mListView.addHeaderView(headView);
        mListView.setAdapter(mAdapter = new MyCoinAdapter(this));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.my_coin_button_back) {
            finish();
        }
    }

    /**
     * 获取规则列表
     */
    private void getActionRules() {
        Tools.showLoading(this, true);
        controller.getActionRules(mUser.getUid(), mUser.getToken(), new UserFetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (entry != null && entry instanceof ActionRuleList) {
                    actionRuleList = (ActionRuleList) entry;
                } else {
                    actionRuleList = new ActionRuleList();
                }
                mMyCoinNum.setText(actionRuleList.getCent() + "");
                coinNumber = actionRuleList.getCent();//积分用于兑换比对
                //                mAdapter.setRuleList(actionRuleList.getList());
            }
        });

    }

    /**
     * 获取商品列表
     */
    private void getGoodsList() {
        Tools.showLoading(this, true);
        controller.getGoodsList(new UserFetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                Tools.showLoading(NewCoinActivity.this, false);
                if (entry instanceof Goods) {
                    goods = (Goods) entry;
                } else {
                    goods = new Goods();
                }
                mAdapter.setGoodList(goods.getList());
                getChangeStatus();
            }
        });
    }

    /**
     * 用户兑换按钮状态
     */
    private void getChangeStatus() {
        Tools.showLoading(this, false);
        controller.getChangeStatus(mUser.getUid(), mUser.getToken(), new UserFetchEntryListener() {
            @Override
            public void setData(Entry entry) {
                if (entry != null && entry instanceof Order) order = (Order) entry;
                mAdapter.setOrderList(order.getList());

            }
        });
    }


    /**
     * 做兑换操作
     *
     * @param goodsItem 商品信息
     */
    public void doExchange(final Goods.GoodsItem goodsItem) {
        if (SlateDataHelper.getUserLoginInfo(this) != null) {// 已登录
            if (SlateDataHelper.getVipLevel(this) > 0) {//VIP有效
                showToast(R.string.msg_subscription_not_due);

            } else {
                if (mUser.getCompletevip() == 1) {// 完善资料
                    final int number = goodsItem.getPrice();
                    final boolean isEnough = number <= coinNumber;
                    int title = isEnough ? R.string.sure : R.string.msg_has_error;
                    String msg = this.getString(R.string.msg_your_coin_is_not_enough);
                    if (isEnough) {
                        msg = ParseUtil.parseString(this, R.string.msg_if_exchange, number + "", goodsItem.getName());
                    }
                    AlertDialog dialog = new AlertDialog.Builder(this).setTitle(title).setMessage(msg).setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (isEnough) {
                                exchange(goodsItem);
                            }
                            dialog.dismiss();
                        }
                    }).create();
                    if (isEnough) { // 增加取消button
                        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, this.getString(R.string.cancel), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dialog != null) dialog.dismiss();
                            }
                        });
                    }
                    try {
                        dialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {//普通登录用户,vip过期
                    Intent i = new Intent(this, VipInfoActivity.class);
                    i.putExtra("code", CODE_ICON);
                    startActivity(i);
                }
            }
        }
    }

    /**
     * 兑换
     */

    private void exchange(final Goods.GoodsItem goods) {
        final User user = SlateDataHelper.getUserLoginInfo(this);
        if (user == null) return;
        showLoadingDialog(true);
        UserOperateController.getInstance(this).addGoodsOrder(user.getUid(), user.getToken(), goods.getId(), new UserFetchEntryListener() {
                    @Override
                    public void setData(Entry entry) {
                        System.out.print("订单返回" + entry);
                        if (entry != null && entry instanceof ErrorMsg) {
                            ErrorMsg errorMsg = (ErrorMsg) entry;
                            showLoadingDialog(false);
                            if (errorMsg.getNo() == 1) { // 获取成功
                                // 修改商品兑换信息，刷新商品列表页面
                                getChangeStatus();//
                                showToast(errorMsg.getDesc());
                                // 更新显示的金币数量
                                coinNumber -= goods.getPrice();
                                mMyCoinNum.setText(coinNumber + "");
                            } else {
                                Tools.showToast(NewCoinActivity.this, errorMsg.getDesc());
                            }
                        } else {
                            Tools.showToast(NewCoinActivity.this, R.string.exchange_failed);
                        }
                    }
                }

        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3033 && resultCode == 4033) {
            finish();
        }
    }

    @Override
    public String getActivityName() {
        return NewCoinActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    private void initEvent() {
        back.setOnClickListener(this);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
