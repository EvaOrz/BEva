package cn.com.modernmediausermodel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;

import cn.com.modernmedia.common.ShareTools;
import cn.com.modernmedia.util.sina.SinaAuth;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.listener.OpenAuthListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.api.BandAccountOperate;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Card.CardItem;
import cn.com.modernmediausermodel.util.CopyTextHelper;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 新建笔记页
 *
 * @author zhuqiao
 */
public class WriteNewCardActivity extends SlateBaseActivity implements OnClickListener {
    public static final String KEY_FROM = "intent_from";
    public static final String KEY_DATA = "share_data";
    public static final String VALUE_SHARE = "share_data";

    private Button cancelBtn, completeBtn;
    private ImageView shareBtn;
    private EditText contentEdit;
    private boolean isShareToWeibo = false;
    private SinaAuth weiboAuth;
    private String articleId; // 文章摘要分享时使用
    private TextView weiboText, copyText, copyArticleText;
    private CopyTextHelper mCopyHelper;
    private UserOperateController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_card);
        mController = UserOperateController.getInstance(this);
        init();
        initDataFromBundle(getIntent());
    }

    private void initDataFromBundle(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            if (VALUE_SHARE.equals(intent.getStringExtra(KEY_FROM))) {
                String content = intent.getStringExtra(KEY_DATA);
                if (!TextUtils.isEmpty(content)) {
                    content = content.trim();
                    if (content.contains("{:") && content.contains(":}") && content.indexOf("{:") == 0) { // 分享内容为文章摘要
                        articleId = content.substring(2, content.indexOf(":}"));
                        content = content.replace("{:" + articleId + ":}", "");
                    }
                    contentEdit.setText(content);
                    if (!TextUtils.isEmpty(articleId)) {
                        showMessage(content);
                    }
                    // contentEdit.setSelection(content.length());
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initDataFromBundle(intent);
    }

    private void init() {
        cancelBtn = (Button) findViewById(R.id.write_card_cancel);
        completeBtn = (Button) findViewById(R.id.write_card_complete);
        contentEdit = (EditText) findViewById(R.id.write_card_content);
        shareBtn = (ImageView) findViewById(R.id.share_to_weibo);
        weiboText = (TextView) findViewById(R.id.write_card_text_share_weibo);
        copyText = (TextView) findViewById(R.id.write_card_copy);
        copyArticleText = (TextView) findViewById(R.id.write_card_copy_article);
        cancelBtn.setOnClickListener(this);
        completeBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        weiboAuth = new SinaAuth(this);
        // 分享到微博栏，当应用不支持微博时，隐藏
        if (SlateApplication.mConfig.getHas_sina() != 1) {
            // weiBoLayout.setVisibility(View.GONE);
            weiboText.setVisibility(View.GONE);
            shareBtn.setVisibility(View.GONE);
        } else {

            User user = SlateDataHelper.getUserLoginInfo(this);
            if (!weiboAuth.checkIsOAuthed()) {
                shareBtn.setImageResource(R.drawable.img_weibo_normal);
                isShareToWeibo = false;
            } else {
                if (user != null && !TextUtils.isEmpty(weiboAuth.mAccessToken.getToken())) { // 微博绑定状态
                    shareBtn.setImageResource(R.drawable.img_weibo_select);
                    isShareToWeibo = true;
                }
            }
            weiboAuth.setWeiboAuthListener(new OpenAuthListener() {
                @Override
                public void onCallBack(boolean isSuccess, String uid, String token) {
                    if (isSuccess) {
                        shareBtn.setImageResource(R.drawable.img_weibo_select);
                        isShareToWeibo = true;
                    } else {
                        shareBtn.setImageResource(R.drawable.img_weibo_normal);
                    }
                }

            });
        }
        mCopyHelper = new CopyTextHelper(this, contentEdit, copyText);
    }

    private void showMessage(String content) {
        ClipboardManager board = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        board.setText(content);
        contentEdit.selectAll();
        copyArticleText.setVisibility(View.VISIBLE);
        copyText.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                copyArticleText.setVisibility(View.GONE);
                copyText.setVisibility(View.VISIBLE);
                // 更新显示状态
                mCopyHelper.doCopy(true);
            }
        }, 2000);
    }

    @Override
    public String getActivityName() {
        return WriteNewCardActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.write_card_cancel) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (v.getId() == R.id.write_card_complete) {
            checkBandPhone();
        } else if (v.getId() == R.id.share_to_weibo) {
            if (isShareToWeibo) {
                shareBtn.setImageResource(R.drawable.img_weibo_normal);
            } else {
                if (!weiboAuth.checkIsOAuthed()) {// 没认证
                    weiboAuth.oAuth();
                } else {
                    shareBtn.setImageResource(R.drawable.img_weibo_select);
                }
            }
            isShareToWeibo = !isShareToWeibo;
        }
    }

    private void addCard() {
        if (TextUtils.isEmpty(contentEdit.getText().toString())) {
            showToast(R.string.card_not_allow_null_toast);
            return;
        }
        CardItem item = new CardItem();
        item.setUid(Tools.getUid(this));
        item.setAppId(UserConstData.getInitialAppId());
        item.setTime(Calendar.getInstance().getTimeInMillis() / 1000 + "");
        item.setContents(contentEdit.getText().toString());
        item.setArticleId(ParseUtil.stoi(articleId));

        showLoadingDialog(true);
        mController.addCard(item, new UserFetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                showLoadingDialog(false);
                doAfterAddCard(entry);
            }
        });
    }

    private void shareToWeibo() {
        ShareTools.getInstance(this).shareWithSina(contentEdit.getText().toString());
    }

    private void doAfterAddCard(Entry entry) {
        if (entry != null && entry instanceof ErrorMsg) {
            ErrorMsg error = (ErrorMsg) entry;
            if (error.getNo() == 0) {
                setResult(RESULT_OK);
                if (isShareToWeibo) {
                    shareToWeibo();
                }
                if (SlateApplication.mConfig.getHas_coin() == 1)
                    //                    UserCentManager.getInstance(mContext).addCardCoinCent();
                    showToast(R.string.card_add_success);
                finish();
            } else {
                showToast(R.string.card_add_failed_toast);
                finish();
            }
        } else finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold, R.anim.down_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        weiboAuth.onActivityResult(requestCode, resultCode, data);


    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(WriteNewCardActivity.this);
            builder.setTitle(R.string.vip_notice_tip);
            builder.setMessage(R.string.band_phone_text);
            builder.setNegativeButton(R.string.goto_band, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent i = new Intent(WriteNewCardActivity.this, BandDetailActivity.class);
                    i.putExtra("band_type", BandAccountOperate.PHONE);
                    i.putExtra("band_user", SlateDataHelper.getUserLoginInfo(WriteNewCardActivity.this));
                    startActivity(i);
                }
            });
            builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (dialog != null) dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    };

    /**
     * 绑定手机
     */
    private void checkBandPhone() {
        if (SlateDataHelper.getBandPhone(this) == 0) {
            handler.sendEmptyMessage(0);
            return ;
        } else if (SlateDataHelper.getBandPhone(this) == -1) {
            /**
             * 获取绑定信息
             */
            mController.getBandStatus(this, SlateDataHelper.getUid(this), SlateDataHelper.getToken(this), new UserFetchEntryListener() {

                @Override
                public void setData(Entry entry) {
                    showLoadingDialog(false);
                    User u = (User) entry;
                    if (u != null) {
                        if (u.isBandPhone()) {
                            addCard();
                        } else {
                            handler.sendEmptyMessage(0);
                        }

                    }

                }
            });
        } else{
            addCard();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
