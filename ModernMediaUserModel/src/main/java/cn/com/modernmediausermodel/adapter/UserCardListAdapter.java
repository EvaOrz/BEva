package cn.com.modernmediausermodel.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.modernmediaslate.adapter.ViewHolder;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.model.Card.CardItem;
import cn.com.modernmediausermodel.util.CardListPop;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 卡片列表adapter(广场、首页、用户卡片信息用)
 *
 * @author jiancong
 */
public class UserCardListAdapter extends CheckScrollAdapter<CardItem> {
    public static final int TO_LOGIN = 100;// 从登录回来刷新页面
    private Context mContext;
    private Card card;
    private boolean isForUser = true; // 是否是登录用户资料,默认是
    private CardListPop pop;
    private String uid;

    public UserCardListAdapter(Context context) {
        super(context);
        this.mContext = context;
        pop = new CardListPop(mContext, this);
    }

    public void setData(List<CardItem> cardList) {
        isScroll = false;
        synchronized (cardList) {
            for (CardItem item : cardList) {
                add(item);
            }
        }
    }

    public void setCard(Card card) {
        this.card = card;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final CardItem cardItem = getItem(position);
        ViewHolder viewHolder = ViewHolder.get(mContext, convertView,
                R.layout.user_card_list_item);
        ImageView divider = viewHolder.getView(R.id.divider);
        TextView content = viewHolder.getView(R.id.item_card_content);
        ImageView slidingBtn = viewHolder
                .getView(R.id.user_card_sliding_button);
        View userInfoLayout = viewHolder.getView(R.id.user_info_layout);
        ImageView avatar = viewHolder.getView(R.id.item_avatar);
        TextView userName = viewHolder.getView(R.id.item_user_name);
        TextView time = viewHolder.getView(R.id.time);
        final ImageView fav = viewHolder.getView(R.id.item_card_fav);//收藏

        // 显示某一用户的卡片列表时，不需要显示头像及名称；广场显示则相反
        if (!isForUser) {
            userInfoLayout.setVisibility(View.VISIBLE);
            doUserInfoLayout(userName, avatar, time, cardItem);
        } else {
            userInfoLayout.setVisibility(View.VISIBLE);
            doUserInfoLayout(userName, avatar, time, cardItem);
            //用户自己 不显示
            fav.setVisibility(cardItem.getType() == 0 ? View.GONE : View.VISIBLE);
        }
        divider.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        if (cardItem != null) {
            if (!isForUser) {
                doUserInfoLayout(userName, avatar, time, cardItem);
            }
            // fav 收藏
//              fav.setVisibility(cardItem.getType() == 0 ? View.GONE : View.VISIBLE);
            if (cardItem.getIsFav() == 0) {//未收藏
                fav.setImageResource(R.drawable.nav_un_fav);

            } else {
                fav.setImageResource(R.drawable.nav_has_fav);
            }
            // 笔记
            if (!TextUtils.isEmpty(cardItem.getContents())) {
                content.setText(cardItem.getContents());
            }
            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (cardItem.getIsFav() == 0) {//未收藏就添加
                        addCardFav(cardItem);
                    } else {
                        deleteCardFav(cardItem);// 已收藏就取消
                    }
                }
            });
            slidingBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    pop.showPop(v, cardItem);
                }
            });
            viewHolder.getConvertView().setOnClickListener(
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // 跳转到详情页
                            if (checkLogin())
                                UserPageTransfer.gotoCardDetailActivity(mContext, card, position);
                        }
                    });
        }
        return viewHolder.getConvertView();
    }

    private void doUserInfoLayout(TextView userName, ImageView avatar, TextView time, CardItem cardItem) {
        String uid = cardItem.getUid();
        if (cardItem.getType() == 2) { // 收藏关系
            uid = cardItem.getFuid();
        }
        final User user = card.getUserInfoMap().get(uid);
        if (user != null) {
            // 昵称
            userName.setText(user.getNickName());
            // 头像
            if (!isScroll) {
                UserTools.setAvatar(mContext, user.getAvatar(), avatar);
            }
            avatar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 跳转到用户资料页
                    if (checkLogin())
                        UserPageTransfer.gotoUserCardInfoActivity(mContext,
                                user, false);
                }
            });
            // time
            time.setText(UserTools.dateFormat(cardItem.getTime()));
        }
    }

    /**
     * 设置用于标识是否是显示用户资料页面卡片列表的adapter（true:用户资料页面；false:广场）
     *
     * @param isForUser
     */
    public void setIsForUser(boolean isForUser) {
        this.isForUser = isForUser;
    }

    /**
     * 广场需要判断是否登录;可能刚进页面的时候是未登录状态，所以时时获取一下
     */
    private boolean checkLogin() {
        User user = SlateDataHelper.getUserLoginInfo(mContext);
        if (user == null) {
            UserPageTransfer.gotoLoginActivityRequest(mContext, TO_LOGIN);
            return false;
        }
        return true;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        pop.dismissPop();
    }

    /**
     * 广场需要判断是否登录
     *
     * @return
     */
    private String checkUid() {
        User user = SlateDataHelper.getUserLoginInfo(mContext);
        if (user == null) {
            UserPageTransfer.gotoLoginActivityRequest(mContext,
                    UserCardListAdapter.TO_LOGIN);
            return "";
        }
        return Tools.getUid(mContext);
    }

    /**
     * 添加收藏
     *
     * @param item
     */
    private void addCardFav(final CardItem item) {
        uid = checkUid();
        if (TextUtils.isEmpty(uid))
            return;
        Tools.showLoading(mContext, true);
        UserOperateController.getInstance(mContext).addCardFav(
                Tools.getUid(mContext), item.getId(),
                new UserFetchEntryListener() {

                    @Override
                    public void setData(Entry entry) {
                        Tools.showLoading(mContext, false);
                        if (entry instanceof ErrorMsg) {
                            ErrorMsg error = (ErrorMsg) entry;
                            if (error.getNo() == 0) {
                                item.setIsFav(1);
                                notifyDataSetChanged();
                                Tools.showToast(mContext,
                                        R.string.collect_success);
                            } else {
                                Tools.showToast(mContext,
                                        R.string.collect_failed);
                            }
                        }
                    }
                });
    }

    /**
     * 取消卡片收藏
     */
    private void deleteCardFav(final CardItem item) {
        uid = checkUid();
        if (TextUtils.isEmpty(uid))
            return;
        Tools.showLoading(mContext, true);
        UserOperateController.getInstance(mContext).cancelCardFav(
                Tools.getUid(mContext), item.getId(),
                new UserFetchEntryListener() {

                    @Override
                    public void setData(Entry entry) {
                        Tools.showLoading(mContext, false);
                        if (entry instanceof ErrorMsg) {
                            ErrorMsg error = (ErrorMsg) entry;
                            if (error.getNo() == 0) {
                                item.setIsFav(0);
                                notifyDataSetChanged();
                                Tools.showToast(mContext,
                                        R.string.uncollect_success);
                            } else {
                                Tools.showToast(mContext,
                                        R.string.uncollect_failed);
                            }
                        }
                    }
                });
    }


}
