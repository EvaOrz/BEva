package cn.com.modernmediausermodel;

import cn.com.modernmediausermodel.listener.AfterLoginListener;
import cn.com.modernmediausermodel.listener.LogOutListener;
import cn.com.modernmediausermodel.listener.UserInfoChangeListener;
import cn.com.modernmediausermodel.model.ActionRuleList;
import cn.com.modernmediausermodel.util.UserObservable;

public class UserApplication {
    public static LogOutListener logOutListener; // 目前只有灵感用
    public static UserInfoChangeListener infoChangeListener;
    public static UserInfoChangeListener recommInfoChangeListener;// 关注页面需要刷新
    public static AfterLoginListener afterLoginListener;
    public static ActionRuleList actionRuleList;

    public static Class<?> favActivity;// 收藏页

    public static UserObservable userObservable = new UserObservable();

    public static void exit() {
        logOutListener = null;
        infoChangeListener = null;
        recommInfoChangeListener = null;
        afterLoginListener = null;
        actionRuleList = null;
        userObservable.deleteObservers();
    }
}
