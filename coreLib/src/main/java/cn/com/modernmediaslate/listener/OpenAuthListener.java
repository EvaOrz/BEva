package cn.com.modernmediaslate.listener;


/**
 * 新浪微博认证授权结果接口
 *
 * @author jiancong
 */
public interface OpenAuthListener {
    /**
     * 授权后回调方法
     *
     * @param isSuccess
     */
    public abstract void onCallBack(boolean isSuccess, String uid, String token);

}
