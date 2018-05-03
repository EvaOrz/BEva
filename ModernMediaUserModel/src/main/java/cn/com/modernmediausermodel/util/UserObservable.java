package cn.com.modernmediausermodel.util;

import java.util.Observable;

/**
 * 登录观察者
 *
 * @author: zhufei
 */
public class UserObservable extends Observable {

    public void setData(Object tag) {
        setChanged();
        notifyObservers(tag);
    }
}
