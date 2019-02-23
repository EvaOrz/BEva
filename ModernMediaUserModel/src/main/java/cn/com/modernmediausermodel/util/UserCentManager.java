package cn.com.modernmediausermodel.util;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserCentChangeListener;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.ActionRuleList;
import cn.com.modernmediausermodel.model.ActionRuleList.ActionRuleItem;

/**
 * 当前登录用户金币积分管理类 主要负责添加积分、规则获取
 *
 * @author jiancong
 */
public class UserCentManager {
    public static final String TYPE_LOGIN = "CentOpenApp"; // 登录
    public static final String TYPE_READ_ARTICLE = "CentReadNewArticle"; // 阅读文章
    public static final String TYPE_CREATE_CARD = "CentCreateNewCard"; // 在文章中创建笔记
    public static final String TYPE_CREATE_USER = "CentCreateNewUser"; // 新用户创建
    public static final String TYPE_SHARE_ARTICLE = "CentShareArticle"; // 分享文章

    private Context mContext;
    private static User mUser;
    private static UserCentManager instance;
    private ActionRuleList mActionRuleList = new ActionRuleList();
    private String ruleTitle = "";

    private UserCentManager(Context context) {
        this.mContext = context;
    }

    /**
     * 获得单例
     *
     * @param context
     * @return
     */
    public static UserCentManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserCentManager(context);
        }
        mUser = SlateDataHelper.getUserLoginInfo(context);
        return instance;
    }

    /**
     * 进应用/登录成功添加金币
     */
    public void addLoginCoinCent() {
        if (SlateApplication.mConfig.getHas_coin() == 0) {
            return;
        }
        if (mUser == null) {
            return;
        }
        if (UserDataHelper.checkHasLogin(mContext, mUser.getUid())) {
            return;
        }
        getActionRules(TYPE_LOGIN, null, true);
    }

    /**
     * 新建一篇笔记，添加金币
     */
    public void addCardCoinCent() {
        if (mUser != null)
            getActionRules(TYPE_CREATE_CARD, null, true);
    }

    /**
     * 新用户创建，添加金币
     */
    public void createNewUserCoinCent() {
        if (mUser != null)
            getActionRules(TYPE_CREATE_USER, null, false);
    }

    /**
     * 分享文章，添加金币
     */
    public void shareArticleCoinCent() {
        if (mUser != null)
            getActionRules(TYPE_SHARE_ARTICLE, null, true);
    }

    /**
     * 阅读文章
     */
    public void addArticleCoinCent(UserCentChangeListener listener,
                                   boolean showToast) {
        List<Integer> ids = ReadDb.getInstance(mContext).getAllUpCoinArticle();
        if (mUser == null || !ParseUtil.listNotNull(ids)) {
            if (listener != null)
                listener.change(-1);
            return;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < ids.size(); i++) {
            buffer.append(TYPE_READ_ARTICLE).append(",");
        }
        String names = buffer.toString();
        if (names.length() > 0) {
            names = names.substring(0, names.length() - 1);
            getActionRules(names, listener, showToast);
        }
    }

    /**
     * 添加金币
     *
     * @param names
     * @param listener
     */
    private void addUserCoinNumber(final String names, final boolean showToast,
                                   final UserCentChangeListener listener) {
        UserOperateController.getInstance(mContext).addUserCoinNumber(
                mUser.getUid(), mUser.getToken(), getActionRuleId(names),
                new UserFetchEntryListener() {

                    @Override
                    public void setData(Entry entry) {
                        int num = -1;
                        if (entry instanceof ErrorMsg) {
                            if (((ErrorMsg) entry).getNo() == 1) {
                                num = ParseUtil.stoi(((ErrorMsg) entry)
                                        .getDesc());
                                if (!TextUtils.isEmpty(names)
                                        && names.contains(TYPE_READ_ARTICLE)) {
                                    ReadDb.getInstance(mContext)
                                            .updateArticleStatus();
                                }
                            }
                        }
                        if (num == -1) {
                            UserDataHelper.clearLoginDate(mContext,
                                    mUser.getUid());
                        } else if (num > 0) {
                            if (showToast) {
                                Tools.showToast(mContext, ParseUtil
                                        .parseString(mContext,
                                                R.string.add_coin_num,
                                                ruleTitle, num + ""));
                            }
                        }
                        if (listener != null)
                            listener.change(num);
                    }
                });
    }

    /**
     * 获取应用规则列表
     */
    private void getActionRules(final String names,
                                final UserCentChangeListener listener, final boolean showToast) {
        UserOperateController.getInstance(mContext).getActionRules(
                Tools.getUid(mContext), Tools.getToken(mContext), new UserFetchEntryListener() {

                    @Override
                    public void setData(Entry entry) {
                        if (entry instanceof ActionRuleList) {
                            mActionRuleList = (ActionRuleList) entry;
                        } else {
                            UserDataHelper.clearLoginDate(mContext,
                                    mUser.getUid());
                        }
                        if (ParseUtil.listNotNull(mActionRuleList.getList())) {
                            addUserCoinNumber(names, showToast, listener);
                        }
                    }
                });
    }

    /**
     * 获取相应的id串，多个用逗号连接
     *
     * @param names
     * @return
     */
    private String getActionRuleId(String names) {
        if (TextUtils.isEmpty(names))
            return "";
        String[] name = names.split(",");
        StringBuilder idsBuilder = new StringBuilder();
        int len = name.length;
        for (int i = 0; i < len; i++) {
            for (ActionRuleItem item : mActionRuleList.getList()) {
                if (item.getName().equals(name[i])) {
                    idsBuilder.append(item.getId()).append(",");
                    ruleTitle = item.getTitle();
                    break;
                }
            }
        }
        String ids = idsBuilder.toString();
        return ids.lastIndexOf(",") > 0 ? ids.substring(0, ids.length() - 1)
                : "";
    }

}
