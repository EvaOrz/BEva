package cn.com.modernmediausermodel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

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

import cn.com.modernmedia.common.WeixinShare;
import cn.com.modernmedia.util.sina.SinaAuth;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.listener.OpenAuthListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.ImgFileManager;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.api.BandAccountOperate;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.UploadAvatarResult;
import cn.com.modernmediausermodel.util.FetchPhotoManager;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;
import cn.com.modernmediausermodel.widget.SignDialog;
import cn.com.modernmediausermodel.widget.ValEmailDialog;


/**
 * 用户信息界面
 *
 * @author ZhuQiao
 */
public class UserInfoActivity extends SlateBaseActivity implements OnClickListener {
    public static final String KEY_ACTION_FROM = "from"; // 作为获得标识从哪个按钮点击跳转到该页面的key
    public static final String PASSEORD = "password";

    private static final String KEY_IMAGE = "data";
    private static final String AVATAR_PIC = "avatar.jpg";

    private Context mContext;
    private UserOperateController mController;
    private TextView nickName, emailText, phoneText;// 个人签名、电子邮件、手机号码
    private TextView unvolied;// 邮箱未验证状态栏
    private ImageView avatar, sina, weixin, qq;
    private String picturePath;// 头像
    private User mUser;// 用户信息
    //    private CheckBox receive;//接收简报
    private boolean canMotifyInfo = false;// 取到用户绑定信息之前，不可以修改或者绑定
    // 第三方app与微信通信的openapi接口
    private IWXAPI api;
    public static int weixin_band = 0;
    private SinaAuth weiboAuth;
    private Tencent mTencent;

    private int bandType = 0; //  1：新浪微博；2：腾讯qq；3：微信

    public interface OnWXBandCallback {
        void onBand(boolean isFirstLogin, User user);
    }

    public static OnWXBandCallback sWXBandCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_userinfo);
        mController = UserOperateController.getInstance(mContext);
        picturePath = Environment.getExternalStorageDirectory().getPath() + "/" + AVATAR_PIC;
        mUser = SlateDataHelper.getUserLoginInfo(this);
        initView();
        mTencent = Tencent.createInstance(SlateApplication.mConfig.getQq_app_id(), this.getApplicationContext());

    }

    private void initView() {
        nickName = (TextView) findViewById(R.id.uinfo_nick);
        //        signText = (TextView) findViewById(R.id.uinfo_sign);
        emailText = (TextView) findViewById(R.id.uinfo_email);
        phoneText = (TextView) findViewById(R.id.uinfo_phone);
        sina = (ImageView) findViewById(R.id.uinfo_btn_sina_login);
        weixin = (ImageView) findViewById(R.id.uinfo_btn_weixin_login);
        qq = (ImageView) findViewById(R.id.uinfo_btn_qq_login);
        avatar = (ImageView) findViewById(R.id.userinfo_avatar);
        unvolied = (TextView) findViewById(R.id.userinfo_un_valied);
        //        receive = (CheckBox) findViewById(R.id.uinfo_checkbox);

        sina.setOnClickListener(this);
        weixin.setOnClickListener(this);
        qq.setOnClickListener(this);
        avatar.setOnClickListener(this);
        //        receive.setOnClickListener(this);

        if (mUser != null)
            afterFetchPicture(SlateDataHelper.getAvatarUrl(this, mUser.getUserName()));
        unvolied.setOnClickListener(this);
        findViewById(R.id.uinfo_close).setOnClickListener(this);
        findViewById(R.id.uinfo_motify_pwd_linear).setOnClickListener(this);
        //        findViewById(R.id.uinfo_logout).setOnClickListener(this);
        //        signText.setOnClickListener(this);
        nickName.setOnClickListener(this);
        emailText.setOnClickListener(this);
        phoneText.setOnClickListener(this);
        handler.sendEmptyMessage(1);
        if (mUser == null) return;
        /**
         * 获取绑定信息
         */
        mController.getBandStatus(UserInfoActivity.this, mUser.getUid(), mUser.getToken(), new UserFetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                showLoadingDialog(false);
                User u = (User) entry;
                if (u != null) {
                    mUser.setBandPhone(u.isBandPhone());
                    mUser.setBandEmail(u.isBandEmail());
                    mUser.setBandQQ(u.isBandQQ());
                    mUser.setBandWeibo(u.isBandWeibo());
                    mUser.setBandWeixin(u.isBandWeixin());
                    mUser.setValEmail(u.isValEmail());
                    handler.sendEmptyMessage(0);
                }

            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {// 绑定信息变更
                canMotifyInfo = true;
                if (mUser == null) return;
                if (mUser.isBandQQ()) qq.setImageResource(R.drawable.login_qq);
                if (mUser.isBandWeibo()) sina.setImageResource(R.drawable.login_sina);
                if (mUser.isBandWeixin()) weixin.setImageResource(R.drawable.login_weixin);
                if (mUser.isBandPhone())
                    phoneText.setText(mUser.getPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
                else phoneText.setText(R.string.band_yet);// 未绑定

                if (mUser.isBandEmail() || !TextUtils.isEmpty(mUser.getEmail())) {// 已绑定或者邮箱数据不为空
                    emailText.setText(mUser.getEmail());
                    if (mUser.isValEmail()) unvolied.setVisibility(View.GONE);
                    else unvolied.setVisibility(View.VISIBLE);// 未验证
                } else emailText.setText(R.string.band_yet);// 未绑定

            } else if (msg.what == 1) {// 昵称、头像、签名变更
                //                signText.setText(SlateDataHelper.getDesc(mContext));
                nickName.setText(SlateDataHelper.getNickname(mContext));
                //                if (mUser.isPushEmail() == 1) {//3.5接受简报 isPushEmail
                //                    receive.setChecked(true);
                //                } else receive.setChecked(false);
                // 设置头像
                afterFetchPicture(mUser.getAvatar());
            }

        }
    };

    /**
     * 获取保存图片的路径
     *
     * @return
     */
    protected void setPicturePath(String path) {
        picturePath = path + AVATAR_PIC;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.uinfo_close) finish();
        else if (id == R.id.uinfo_motify_pwd_linear) {// 修改密码
            if (canMotifyInfo && (mUser.isBandEmail() || mUser.isBandPhone())) {
                UserPageTransfer.gotoModifyPasswordActivity(this);
            } else {//第三方登录，先绑定
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage(R.string.need_band);
                dialog.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                //去设置
                dialog.setNegativeButton(R.string.set_band, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoBandDetail(BandAccountOperate.PHONE);
                    }
                });
                dialog.show();
            }
            //        } else if (id == R.id.uinfo_logout) // 登出
            //            doLoginOut();
        } else if (id == R.id.userinfo_avatar) // 修改头像
            doFecthPicture();
        else if (id == R.id.uinfo_btn_sina_login) { // 新浪绑定
            if (canMotifyInfo) {
                if (mUser.isBandWeibo()) showToast(R.string.band_already);
                else doSinaBand();
            }
        } else if (id == R.id.uinfo_btn_qq_login) { // qq绑定
            if (canMotifyInfo) {
                if (mUser.isBandQQ()) showToast(R.string.band_already);
                else doQQBand();
            }
        } else if (id == R.id.uinfo_btn_weixin_login) { // 微信绑定
            if (canMotifyInfo) {
                if (mUser.isBandWeixin()) showToast(R.string.band_already);
                else doWeixinBand();
            }
            //        } else if (id == R.id.uinfo_sign) {// 签名
            //            new SignDialog(UserInfoActivity.this, 2);
        } else if (id == R.id.uinfo_email || id == R.id.userinfo_un_valied) {// 绑定邮箱
            if (canMotifyInfo) {
                if (!mUser.isValEmail() && !TextUtils.isEmpty(mUser.getEmail())) // 未验证过
                    new ValEmailDialog(this, mUser.getEmail());
                else gotoBandDetail(BandAccountOperate.EMAIL);
            }
        } else if (id == R.id.uinfo_phone) {// 绑定手机号码
            if (canMotifyInfo) {
                gotoBandDetail(BandAccountOperate.PHONE);
            }
        } else if (id == R.id.uinfo_nick) {// 修改昵称
            new SignDialog(UserInfoActivity.this, 1);
        } else if (id == R.id.uinfo_checkbox) {
            if (mUser.isPushEmail() == 1) {//isPushEmail
                handler.sendEmptyMessage(1);
                Tools.showToast(UserInfoActivity.this, R.string.userinfo_receive_tips1);
            } else {
                if (mUser.isValEmail()) {
                    User u = mUser;
                    u.setPushEmail(1);
                    modifyUserInfo(u, "", "", true);
                } else {
                    Tools.showToast(UserInfoActivity.this, R.string.userinfo_receive_tips);
                    handler.sendEmptyMessage(1);
                }
            }
        }
    }

    private void doQQBand() {
        bandType = 2;
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, "all", listener);
        } else {
            doBand(mTencent.getOpenId(), BandAccountOperate.QQ, null);
        }
    }

    private IUiListener listener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            Log.e("login", o.toString());
            try {
                if (!TextUtils.isEmpty(o.toString())) {
                    JSONObject jsonObject = new JSONObject(o.toString());
                    final String openid = jsonObject.optString("openid");
                    doBand(openid, BandAccountOperate.QQ, null);
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
     * 微博授权
     */
    private void doSinaBand() {
        bandType = 1;
        // 新浪微博认证
        weiboAuth = new SinaAuth(this);
        if (!weiboAuth.checkIsOAuthed()) {
            weiboAuth.oAuth();
        } else {
            String sinaId = weiboAuth.mAccessToken.getUid();
            Log.e("已认证sinaId", sinaId);
            mUser.setSinaId(sinaId);
            doBand(sinaId, BandAccountOperate.WEIBO, null);
        }
        weiboAuth.setWeiboAuthListener(new OpenAuthListener() {

            @Override
            public void onCallBack(boolean isSuccess, String uid, String token) {
                if (isSuccess) {
                    mUser.setSinaId(uid);
                    doBand(uid, BandAccountOperate.WEIBO, mUser.getUnionId());
                }
            }


        });
    }

    /**
     * 绑定
     *
     * @param userName username
     * @param bindType 绑定类型
     */
    public void doBand(final String userName, final int bindType, final String unionId) {
        if (mUser == null) return;
        mController.bandAccount(mUser.getUid(), mUser.getToken(), bindType, userName, unionId, new UserFetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                showLoadingDialog(false);
                String toast = "";
                if (entry != null && entry instanceof ErrorMsg) {
                    ErrorMsg e = (ErrorMsg) entry;
                    if (e.getNo() == 0) {
                        toast = getString(R.string.band_succeed);
                        // 存储绑定信息
                        if (bindType == BandAccountOperate.WEIBO) mUser.setBandWeibo(true);
                        else if (bindType == BandAccountOperate.WEIXIN) mUser.setBandWeixin(true);
                        else if (bindType == BandAccountOperate.QQ) mUser.setBandQQ(true);
                        else if (bindType == BandAccountOperate.EMAIL) // 重新发送邮件
                            toast = getString(R.string.send_email_done);

                        handler.sendEmptyMessage(0);
                        SlateDataHelper.saveUserLoginInfo(UserInfoActivity.this, mUser);
                    } else toast = e.getDesc();
                }
                showToast(TextUtils.isEmpty(toast) ? getString(R.string.band_failed) : toast);
            }
        });
    }

    /**
     * @param type
     */
    public void gotoBandDetail(int type) {
        Intent i = new Intent(this, BandDetailActivity.class);
        i.putExtra("band_type", type);
        i.putExtra("band_user", mUser);
        startActivityForResult(i, BandDetailActivity.BAND_SUCCESS);
    }

    /**
     * 微信绑定
     */
    private void doWeixinBand() {
        bandType = 3;
        weixin_band = 2;
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

        sWXBandCallback = new OnWXBandCallback() {
            @Override
            public void onBand(boolean isFirstLogin, User user) {
                if (user != null) {
                    mUser.setWeixinId(user.getWeixinId());
                    doBand(user.getWeixinId(), BandAccountOperate.WEIXIN, mUser.getUnionId());
                }
            }
        };
    }

    protected void doFecthPicture() {
        FetchPhotoManager fetchPhotoManager = new FetchPhotoManager(this, picturePath);
        fetchPhotoManager.doFecthPicture();
    }

    /**
     * 上传用户头像
     *
     * @param imagePath 头像存储在本地的路径
     */
    protected void uploadAvatar(String imagePath) {
        if (mUser == null || TextUtils.isEmpty(imagePath)) return;

        if (!new File(imagePath).exists()) {
            showLoadingDialog(false);
            showToast(R.string.msg_avatar_get_failed);// 头像获取失败
            return;
        }

        showLoadingDialog(true);
        mController.uploadUserAvatar(imagePath, new UserFetchEntryListener() {

            @Override
            public void setData(final Entry entry) {
                showLoadingDialog(false);
                String toast = "";
                if (entry instanceof UploadAvatarResult) {
                    UploadAvatarResult result = (UploadAvatarResult) entry;
                    String status = result.getStatus();
                    if (status.equals("success")) { // 头像上传成功
                        if (!TextUtils.isEmpty(result.getImagePath()) && !TextUtils.isEmpty(result.getAvatarPath())) {
                            SlateDataHelper.saveAvatarUrl(mContext, mUser.getUserName(), result.getImagePath());
                            // 刷新页面
                            modifyUserInfo(mUser, result.getImagePath(), result.getAvatarPath(), false);
                            return;
                        }
                    } else {
                        toast = result.getMsg();
                    }
                }
                showToast(TextUtils.isEmpty(toast) ? getString(R.string.msg_avatar_upload_failed) : toast);
            }
        });
    }

    /**
     * 更新用户信息
     *
     * @param user
     * @param url        图片的相对地址(通过上传头像获得)
     * @param avatar_url 头像的绝对地址
     */
    public void modifyUserInfo(User user, String url, final String avatar_url, final boolean isPushEmail) {
        if (user == null) return;
        showLoadingDialog(true);
        // 只更新头像、昵称信息
        mController.modifyUserInfo(user.getUid(), user.getToken(), user.getUserName(), user.getNickName(), url, null, user.getDesc(), isPushEmail, new UserFetchEntryListener() {

            @Override
            public void setData(final Entry entry) {
                showLoadingDialog(false);
                if (entry instanceof User) {
                    User resUser = (User) entry;
                    ErrorMsg error = resUser.getError();
                    if (error.getNo() == 0) {
                        mUser.setPushEmail(resUser.isPushEmail());
                        if (isPushEmail) {
                            showToast(R.string.userinfo_receive_success);
                        } else {
                            SlateDataHelper.setNickname(mContext, resUser.getNickName());
                            SlateDataHelper.setDesc(mContext, resUser.getDesc());
                            mUser.setNickName(resUser.getNickName());
                            mUser.setAvatar(resUser.getAvatar());
                            mUser.setDesc(resUser.getDesc());
                        }
                        handler.sendEmptyMessage(1);
                        SlateApplication.loginStatusChange = true;
                    }// 修改失败
                    else {
                        showToast(error.getDesc());
                    }
                }

            }
        });
    }

    /**
     * 设置拿到头像后的操作
     *
     * @param picUrl
     */
    protected void afterFetchPicture(String picUrl) {
        UserTools.setAvatar(this, picUrl, avatar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == FetchPhotoManager.REQUEST_CAMERA) {
                UserTools.startPhotoZoom(this, Uri.fromFile(new File(picturePath)), picturePath);
            } else if (requestCode == FetchPhotoManager.REQUEST_GALLERY) {
                if (data != null) {
                    UserTools.startPhotoZoom(this, data.getData(), picturePath);
                }
            } else if (requestCode == UserTools.REQUEST_ZOOM) {
                if (data != null && data.getExtras() != null) {
                    Bitmap bitmap = data.getExtras().getParcelable(KEY_IMAGE);
                    ImgFileManager.saveImage(bitmap, picturePath);
                    if (bitmap != null) {
                        uploadAvatar(picturePath);
                        bitmap.recycle();
                        bitmap = null;
                    }
                } else {
                    showToast(R.string.upload_failed);
                }
            } else if (requestCode == BandDetailActivity.BAND_SUCCESS) {
                mUser.setEmail(SlateDataHelper.getUserLoginInfo(this).getEmail());
                mUser.setPhone(SlateDataHelper.getUserPhone(this));
                handler.sendEmptyMessage(0);
            }
        }

        if (bandType == 1) {
            weiboAuth.onActivityResult(requestCode, resultCode, data);
        } else if (bandType == 2) {
            Tencent.onActivityResultData(requestCode, resultCode, data, listener);
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }

    @Override
    public String getActivityName() {
        return UserInfoActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        mUser.setPhone(SlateDataHelper.getUserPhone(this));
        mUser.setEmail(SlateDataHelper.getUserLoginInfo(this).getEmail());
        handler.sendEmptyMessage(0);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
