package cn.com.modernmediausermodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.tencent.connect.UserInfo;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.common.UsersAPI;
import cn.com.modernmedia.common.WeixinShare;
import cn.com.modernmedia.pay.newlogic.PayHttpsOperate;
import cn.com.modernmedia.util.sina.SinaAuth;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.listener.ImageDownloadStateListener;
import cn.com.modernmediaslate.listener.OpenAuthListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.UploadAvatarResult;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;
import cn.com.modernmediausermodel.vip.VipCodeActivity;
import cn.com.modernmediausermodel.vip.VipProductPayActivity;
import cn.com.modernmediausermodel.widget.OpenLoginPopwindow;

/**
 * 登录页面
 *
 * @author Eva.
 */
public class LoginActivity extends SlateBaseActivity implements OnClickListener {
    public interface OnWXLoginCallback {
        void onLogin(boolean isFirstLogin, User user);
    }

    public static OnWXLoginCallback sWXLoginCallback;
    private Tencent mTencent;

    private Context mContext;
    private UserOperateController mController;
    private Animation shakeAnim;
    private SinaAuth sinaAuth;
    private Button mLoginBtn;
    private EditText mAcountEdit, mPasswordEdit, mPhoneEdit, mCodeEdit;
    private LinearLayout nomalLoginLayout, phoneLoginLayout;

    private TextView getVerify, forgetTextView, phoneTextView, lastTextView;
    private ImageView pwdImg;
    private boolean canGetVerify = true;// 是否可获取验证码
    private String shareData = "";// 分享的内容
    private int gotoPage;// 登录完成需要跳转的页面
    private boolean shouldFinish = false;// 当直接跳到发笔记页的时候，不会立即执行destory，所以延迟500ms
    // 第三方app与微信通信的openapi接口
    private IWXAPI api;
    public static int weixin_login = 0;

    private boolean isNomalLogin = true;// 是否是正常登录方式
    private boolean isShowPassword = false;// 是否显示密码
    private boolean isLogining = false;// 是否正在登录

    private String lastUserName = "";// 记录登录方式（邮箱、手机号）

    private int loginType = 0;// 1：新浪微博；2：腾讯qq；3：微信

    /**
     * 从网页跳转支付，需要先验证登录
     */
    private Object fromSplashBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mController = UserOperateController.getInstance(this);
        setContentView(R.layout.activity_login);
        if (checkIsShare() && SlateDataHelper.getUserLoginInfo(this) != null) {
            UserPageTransfer.gotoWriteCardActivity(this, shareData, false);
            shouldFinish = true;
        }
        initView();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (shouldFinish) {
                    finish();
                }
            }
        }, 500);

        fromSplashBundle = getIntent().getBundleExtra("html_pay");

        mTencent = Tencent.createInstance(SlateApplication.mConfig.getQq_app_id(), this.getApplicationContext());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Bundle bud = getIntent().getBundleExtra("html_pay");
        if (bud != null) {
            fromSplashBundle = bud;
        }

    }

    private void initView() {
        shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
        mAcountEdit = (EditText) findViewById(R.id.login_account);
        mPasswordEdit = (EditText) findViewById(R.id.login_password);
        mPhoneEdit = (EditText) findViewById(R.id.login_phonenumber);
        mCodeEdit = (EditText) findViewById(R.id.login_code);
        nomalLoginLayout = (LinearLayout) findViewById(R.id.nomal_login);
        phoneLoginLayout = (LinearLayout) findViewById(R.id.phone_login);
        getVerify = (TextView) findViewById(R.id.login_verify_get);
        mLoginBtn = (Button) findViewById(R.id.login_btn_login);
        forgetTextView = (TextView) findViewById(R.id.login_forget_pwd);
        phoneTextView = (TextView) findViewById(R.id.login_phone);
        pwdImg = (ImageView) findViewById(R.id.login_img_pass_show);
        lastTextView = (TextView) findViewById(R.id.login_login_username);

        String last = SlateDataHelper.getLastLoginUsername(this);

        if (TextUtils.isEmpty(last)) {
            lastTextView.setText("");
        } else if (last.equals("qq")) {
            lastTextView.setText("上次登录方式：QQ登录");
        } else if (last.equals("sina")) {
            lastTextView.setText("上次登录方式：新浪微博登录");
        } else if (last.equals("weixin")) {
            lastTextView.setText("上次登录方式：微信登录");
        } else if (UserTools.checkIsPhone(this, last)) {
            mAcountEdit.setText(last);
            lastTextView.setText("上次登录方式：手机登录");
        } else {
            mAcountEdit.setText(last);
            lastTextView.setText("上次登录方式：邮箱登录");
        }

        if (SlateApplication.APP_ID == 37 || SlateApplication.APP_ID == 18) {// verycity
            mAcountEdit.setHint(R.string.email);
        }

        findViewById(R.id.login_img_close).setOnClickListener(this);
        findViewById(R.id.login_img_clear).setOnClickListener(this);
        findViewById(R.id.login_phone_clear).setOnClickListener(this);
        forgetTextView.setOnClickListener(this);
        pwdImg.setOnClickListener(this);
        findViewById(R.id.login_registers).setOnClickListener(this);
        phoneTextView.setOnClickListener(this);
        findViewById(R.id.login_open).setOnClickListener(this);

        mLoginBtn.setOnClickListener(this);
        getVerify.setOnClickListener(this);
    }

    /**
     * 检查是否来自应用分享，若是，则会取得要分享的文本信息
     *
     * @return
     */
    public boolean checkIsShare() {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            gotoPage = bundle.getInt(UserPageTransfer.LOGIN_KEY);
            if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEND)) {
                if (bundle.containsKey(Intent.EXTRA_TEXT)) {
                    shareData = bundle.getString(Intent.EXTRA_TEXT);
                    if (TextUtils.isEmpty(shareData)) {
                        finish();
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        String psd = "";
        String phone = "", verifyCode = "";// 账号、昵称、密码、验证码

        if (mPhoneEdit != null) phone = mPhoneEdit.getText().toString();
        if (mCodeEdit != null) verifyCode = mCodeEdit.getText().toString();
        if (mAcountEdit != null) {
            lastUserName = mAcountEdit.getText().toString();
        }
        if (mPasswordEdit != null) {
            psd = mPasswordEdit.getText().toString();
        }

        if (v.getId() == R.id.login_img_clear) {
            if (mAcountEdit != null) mAcountEdit.setText("");
            return;
        } else if (v.getId() == R.id.login_img_pass_show) {
            if (isShowPassword) {// 隐藏
                mPasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                //                mPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                pwdImg.setImageResource(R.drawable.password_unshow);
            } else {//选择状态 显示明文--设置为可见的密码
                mPasswordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                //                mPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                pwdImg.setImageResource(R.drawable.password_show);
            }
            isShowPassword = !isShowPassword;
            return;


        } else if (v.getId() == R.id.login_phone_clear) {
            if (mPhoneEdit != null) mPhoneEdit.setText("");
            return;

        } else if (v.getId() == R.id.login_forget_pwd) {
            Intent i = new Intent(mContext, ForgetPwdActivity.class);
            startActivity(i);
            return;

        } else if (v.getId() == R.id.login_img_close) {
            setResult(1234);
            finish();
        } else if (v.getId() == R.id.login_registers) {// 注册
            Intent i = new Intent(mContext, RegisterActivity.class);
            startActivity(i);
            return;

        } else if (v.getId() == R.id.login_btn_login) {
            if (isNomalLogin) {
                if (UserTools.checkString(lastUserName, mAcountEdit, shakeAnim) && UserTools.checkString(psd, mPasswordEdit, shakeAnim))
                    doLogin(lastUserName, psd);
            } else {
                if (UserTools.checkIsPhone(LoginActivity.this, phone)) {
                    if (UserTools.checkString(verifyCode, mCodeEdit, shakeAnim)) {
                        User uP = new User();
                        uP.setPhone(phone);
                        openLogin(uP, 5, verifyCode);
                    }

                } else showToast(R.string.get_account_error);// 手机号码格式错误

            }
            return;

        } else if (v.getId() == R.id.login_verify_get) {// 获取验证码
            if (UserTools.checkIsPhone(this, phone)) doGetVerifyCode(phone);
            else showToast(R.string.get_account_error);// 手机号码格式错误
            return;


        } else if (v.getId() == R.id.login_phone) {// 切换登录方式
            if (isNomalLogin) { // 切换成phone登录
                nomalLoginLayout.setVisibility(View.GONE);
                phoneLoginLayout.setVisibility(View.VISIBLE);
                forgetTextView.setVisibility(View.GONE);
                phoneTextView.setText(R.string.pwd_login);
            } else {// 切换成nomal登录
                nomalLoginLayout.setVisibility(View.VISIBLE);
                phoneLoginLayout.setVisibility(View.GONE);
                forgetTextView.setVisibility(View.VISIBLE);
                phoneTextView.setText(R.string.phone_login);
            }
            isNomalLogin = !isNomalLogin;
            return;

        } else if (v.getId() == R.id.login_open) {
            new OpenLoginPopwindow(LoginActivity.this);
            return;

        }


    }

    /**
     * 获取手机验证码
     */
    protected void doGetVerifyCode(String phone) {
        if (canGetVerify) {
            canGetVerify = false;
            // 开启倒计时器
            new CountDownTimer(60000, 1000) {
                public void onTick(long millisUntilFinished) {
                    getVerify.setText(millisUntilFinished / 1000 + "s重新获取");
                }

                public void onFinish() {
                    getVerify.setText(R.string.get_verify_code);
                    canGetVerify = true;
                }
            }.start();
            mController.getVerifyCode(phone, new UserFetchEntryListener() {

                @Override
                public void setData(Entry entry) {
                    if (entry instanceof ErrorMsg && ((ErrorMsg) entry).getNo() != 0) {

                        showToast(((ErrorMsg) entry).getDesc());
                    }
                }
            });
        }
    }

    public void doWeixinlogin() {
        loginType = 3;
        weixin_login = 1;
        sWXLoginCallback = new OnWXLoginCallback() {
            @Override
            public void onLogin(boolean isFirstLogin, User user) {
                if (isFirstLogin) {
                    openLogin(user, 3, null);
                    Log.e("dessssss", "4444444444");
                } else {// 登录过
                    lastUserName = "weixin";
                    afterLogin(user);
                    showLoadingDialog(false);
                }
            }
        };

        if (api == null) {
            api = WXAPIFactory.createWXAPI(mContext, WeixinShare.APP_ID, true);
        }
        System.out.print(SlateApplication.mConfig.getWeixin_app_id());

        if (!api.isWXAppInstalled()) {
            Tools.showToast(mContext, R.string.no_weixin);
            return;
        }
        api.registerApp(WeixinShare.APP_ID);

        SendAuth.Req req = new SendAuth.Req();
        // post_timeline
        req.scope = "snsapi_userinfo";
        req.state = "weixin_login";
        System.out.print("***********" + req.toString());
        api.sendReq(req);
    }

    /**
     * 登录成功
     *
     * @param user
     */
    protected void afterLogin(User user) {
        SlateDataHelper.saveUserLoginInfo(this, user);
        // 记录上次登录username
        SlateDataHelper.setLastLoginUsername(this, lastUserName);
        // 返回上一级界面

        showToast(R.string.msg_login_success);
        //        UserCentManager.getInstance(mContext).addLoginCoinCent();

        notifyWeb(user);
        getIssueLevel(user);

    }

    /**
     * 通知网页js回调登录结果
     */
    private void notifyWeb(User user) {
        JSONObject result = new JSONObject();
        try {
            result.put("loginStatus", true);
            JSONObject uJson = new JSONObject();
            uJson.put("userId", user.getUid());
            uJson.put("userToken", user.getToken());
            result.put("user", uJson);
        } catch (JSONException e) {
        }
        if (CommonApplication.asynExecuteCommandListener != null)
            CommonApplication.asynExecuteCommandListener.onCallBack(result.toString());
    }

    /**
     * 开放平台(新浪微博、QQ等)账号登录
     *
     * @param user 用户信息
     * @param type 平台类型,目前0:普通登录；1：新浪微博；2：腾讯qq；3：微信；5：phone
     */
    public void openLogin(final User user, final int type, String code) {
        if (isLogining) return;
        isLogining = true;
        mController.openLogin(user, "", code, type, new UserFetchEntryListener() {

            @Override
            public void setData(final Entry entry) {
                isLogining = false;
                String toast = "";
                if (entry instanceof User) {
                    User mUser = (User) entry;
                    ErrorMsg error = mUser.getError();
                    if (error.getNo() == 0) {

                        // 上传头像
                        if (type == 1) {
                            lastUserName = "sina";
                        } else if (type == 2) {
                            lastUserName = "qq";
                        } else if (type == 3) {
                            lastUserName = "weixin";
                        } else if (type == 5) {
                            lastUserName = user.getPhone();
                        }
                        getOpenUserAvatar(user);
                        afterLogin(mUser);
                        return;
                    } else {
                        toast = error.getDesc();
                    }
                }
                showToast(TextUtils.isEmpty(toast) ? getString(R.string.msg_login_fail) : toast);
            }
        });
    }

    /**
     * 获取开放平台用户头像
     */
    private void getOpenUserAvatar(final User mUser) {
        SlateApplication.finalBitmap.display(mUser.getAvatar(), new ImageDownloadStateListener() {

            @Override
            public void loading() {
            }

            @Override
            public void loadError() {
            }

            @Override
            public void loadOk(Bitmap bitmap, NinePatchDrawable drawable, byte[] gifByte) {

            }
        });
    }

    /**
     * 上传用户头像
     *
     * @param imagePath 头像存储在本地的路径
     */
    protected void uploadAvatar(final User mUser, String imagePath) {
        if (mUser == null || TextUtils.isEmpty(imagePath)) return;

        if (!new File(imagePath).exists()) {
            showToast(R.string.msg_avatar_get_failed);// 头像获取失败
            return;
        }

        mController.uploadUserAvatar(imagePath, new UserFetchEntryListener() {

            @Override
            public void setData(final Entry entry) {
                String toast = "";
                if (entry instanceof UploadAvatarResult) {
                    UploadAvatarResult result = (UploadAvatarResult) entry;
                    String status = result.getStatus();
                    if (status.equals("success")) { // 头像上传成功
                        if (!TextUtils.isEmpty(result.getImagePath())) {
                            modifyUserInfo(mUser, result.getImagePath());
                            return;
                        }
                    } else toast = result.getMsg();
                }
                showToast(TextUtils.isEmpty(toast) ? getString(R.string.msg_avatar_upload_failed) : toast);
            }
        });
    }

    /**
     * 更新用户信息
     *
     * @param user
     * @param url  图片的相对地址(通过上传头像获得)
     */
    public void modifyUserInfo(User user, String url) {
        if (user == null) return;
        // 只更新头像
        mController.modifyUserInfo(user.getUid(), user.getToken(), user.getUserName(), user.getNickName(), url, null, user.getDesc(), false, new UserFetchEntryListener() {

            @Override
            public void setData(final Entry entry) {
                if (entry instanceof User) {
                    User resUser = (User) entry;
                    ErrorMsg error = resUser.getError();
                    if (error.getNo() == 0) {
                        SlateDataHelper.saveAvatarUrl(mContext, resUser.getUserName(), resUser.getAvatar());
                    }// 修改失败
                    //                    else {
                    //                        showToast(error.getDesc());
                    //                    }
                }
            }
        });
    }

    private IUiListener iuListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            Log.e("login", o.toString());
            try {
                if (!TextUtils.isEmpty(o.toString())) {
                    JSONObject jsonObject = new JSONObject(o.toString());
                    final String openid = jsonObject.optString("openid");
                    getQQUser(openid);
                }
            } catch (JSONException e) {

            }

        }

        @Override
        public void onError(UiError uiError) {
            Log.e("onError:", "code:" + uiError.errorCode + ", msg:" + uiError.errorMessage + ", detail:" + uiError.errorDetail);

        }

        @Override
        public void onCancel() {
            showToast("取消");
        }
    };

    /**
     * {"ret":0,"openid":"EDB238CADF1B62163DB1A72D062E22ED","access_token":"BE16D7DE7A6BDB7E3291E241309967F9","pay_token":"34D597216A05D33C25386F6D9744F4CD","expires_in":7776000,"pf":"desktop_m_qq-10000144-android-2002-","pfkey":"ce30f2f9e2695b3759df3f1d99e20aa0","msg":"","login_cost":783,"query_authority_cost":831,"authority_cost":0}
     */
    public void doQQLogin() {
        loginType = 2;
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, "all", iuListener);
        } else {
            getQQUser(mTencent.getOpenId());
        }
    }

    private void getQQUser(final String openid) {
        showLoadingDialog(true);
        UserInfo mInfo = new UserInfo(mContext, mTencent.getQQToken());
        mInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object o) {

                doAfterQQIsAuthed(o, openid);
            }

            @Override
            public void onError(UiError uiError) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    public void doSinaLogin() {
        loginType = 1;
        // 新浪微博认证
        sinaAuth = new SinaAuth(mContext);
        if (!sinaAuth.checkIsOAuthed()) {
            sinaAuth.oAuth();
        } else {
            getSinaUserInfo();
        }
        sinaAuth.setWeiboAuthListener(new OpenAuthListener() {

            @Override
            public void onCallBack(boolean isSuccess, String uid, String token) {
                getSinaUserInfo();
            }
        });
    }

    /**
     * 获取新浪用户相关信息
     */
    public void getSinaUserInfo() {
        showLoadingDialog(true);
        final UsersAPI mUsersAPI = new UsersAPI(this, "3735038", sinaAuth.mAccessToken);
        final long uid = Long.parseLong(sinaAuth.mAccessToken.getUid());
        mUsersAPI.show(uid, new RequestListener() {
            @Override
            public void onComplete(String response) {
                showLoadingDialog(false);
                try {
                    if (!TextUtils.isEmpty(response)) {
                        // 调用 User#parse 将JSON串解析成User对象
                        Log.i("sinagetuser", response);
                        JSONObject object = new JSONObject(response);
                        User mUser = new User();
                        mUser.setSinaId(object.optString("idstr", "")); // 新浪ID
                        mUser.setNickName(object.optString("screen_name")); // 昵称
                        mUser.setUserName(object.optString("idstr", ""));
                        mUser.setAvatar(object.optString("profile_image_url"));// 用户头像地址（中图），50×50像素
                        mUser.setOpenLoginJson(response);
                        openLogin(mUser, 1, null);
                        Log.e("dessssss", "111111111");
                    } else {
                        Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                showLoadingDialog(false);
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * {"ret":0,"msg":"","is_lost":0,"nickname":"Sundial ☀","gender":"女","province":"海南","city":"三亚","figureurl":"http:\/\/qzapp.qlogo.cn\/qzapp\/101082784\/EDB238CADF1B62163DB1A72D062E22ED\/30","figureurl_1":"http:\/\/qzapp.qlogo.cn\/qzapp\/101082784\/EDB238CADF1B62163DB1A72D062E22ED\/50","figureurl_2":"http:\/\/qzapp.qlogo.cn\/qzapp\/101082784\/EDB238CADF1B62163DB1A72D062E22ED\/100","figureurl_qq_1":"http:\/\/q.qlogo.cn\/qqapp\/101082784\/EDB238CADF1B62163DB1A72D062E22ED\/40","figureurl_qq_2":"http:\/\/q.qlogo.cn\/qqapp\/101082784\/EDB238CADF1B62163DB1A72D062E22ED\/100","is_yellow_vip":"0","vip":"0","yellow_vip_level":"0","level":"0","is_yellow_year_vip":"0"}
     */
    private void doAfterQQIsAuthed(Object o, String openid) {
        showLoadingDialog(false);
        try {
            JSONObject object = new JSONObject(o.toString());
            User mUser = new User();
            mUser.setQqId(openid);
            mUser.setNickName(object.optString("nickname")); // 昵称
            mUser.setAvatar(object.optString("figureurl_qq_1"));// 用户头像地址（中图），50×50像素
            mUser.setOpenLoginJson(o.toString());
            openLogin(mUser, 2, null);
            Log.e("dessssss", "22222222");
        } catch (JSONException e) {

        }

    }

    /**
     * 用户登录
     *
     * @param userName 用户名称
     * @param password 密码
     */
    protected void doLogin(final String userName, final String password) {
        if (isLogining) return;
        // 检查格式
        if (UserTools.checkIsEmailOrPhone(mContext, userName) && UserTools.checkPasswordFormat(mContext, password)) {
            showLoadingDialog(true);
            isLogining = true;
            mController.login(this, userName, password, new UserFetchEntryListener() {

                @Override
                public void setData(final Entry entry) {
                    isLogining = false;
                    String toast = "";
                    if (entry instanceof User) {
                        User user = (User) entry;
                        ErrorMsg error = user.getError();
                        // 登录成功
                        if (error.getNo() == 0 && !TextUtils.isEmpty(user.getUid())) {
                            user.setPassword(password);
                            user.setLogined(true);
                            // 将相关信息用SharedPreferences存储
                            if (UserTools.checkIsPhone(mContext, userName)) {
                                user.setPhone(userName);
                            } else {
                                user.setEmail(userName);
                            }
                            SlateDataHelper.saveUserLoginInfo(mContext, user);
                            SlateDataHelper.saveAvatarUrl(mContext, user.getUserName(), user.getAvatar());
                            showLoadingDialog(false);
                            afterLogin(user);
                            return;
                        } else {
                            toast = error.getDesc();
                        }
                        showLoadingDialog(false);
                        if (!TextUtils.isEmpty(toast)) showToast(toast);
                        else showToast(R.string.msg_login_fail);
                    }
                }
            });
        }
    }

    @Override
    public void finish() {
        if (SlateDataHelper.getUserLoginInfo(this) != null) setResult(RESULT_OK);


        super.finish();
        overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit);
    }

    /**
     * 取用户的付费权限
     */
    private void getIssueLevel(final User user) {
        final PayHttpsOperate con = PayHttpsOperate.getInstance(this);
        con.getUserPermission(new FetchDataListener() {
            @Override
            public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                SlateApplication.loginStatusChange = true;
                if (isSuccess) con.saveLevel(data);
                UserPageTransfer.afterLogin(LoginActivity.this, user, shareData, gotoPage);

                if (!checkIsfromHtmlVip()) finish();

            }
        });
    }

    /**
     * @return true 要执行html
     */
    private boolean checkIsfromHtmlVip() {
        if (fromSplashBundle != null) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (fromSplashBundle instanceof Bundle) {
                intent.setClass(this, VipProductPayActivity.class);
                intent.putExtra("html_pay", (Bundle) fromSplashBundle);
            } else {
                intent.setClass(this, VipCodeActivity.class);

            }
            startActivity(intent);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    finish();
                }
            }, 500);
            return true;
        }
        return false;
    }

    public static Class<LoginActivity> getLoginClass() {
        return LoginActivity.class;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            setResult(1234);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void onResume() {
        super.onResume();
        if (SlateDataHelper.getUserLoginInfo(this) != null) finish();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public String getActivityName() {
        return LoginActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (loginType == 1) {
            sinaAuth.onActivityResult(requestCode, resultCode, data);
        } else if (loginType == 2) {
            Tencent.onActivityResultData(requestCode, resultCode, data, iuListener);
        }

    }

}
