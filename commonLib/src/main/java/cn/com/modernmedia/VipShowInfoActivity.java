package cn.com.modernmedia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.listener.ImageDownloadStateListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;


/**
 * 扫描VIP二维码后 显示VIP信息
 *
 * @author: zhufei
 */
public class VipShowInfoActivity extends SlateBaseActivity implements View.OnClickListener {
    public static final String OTHER_UID = "other_uid";
    public static final int STROKE = 2;// 边框宽度
    private TextView name, level, id, industry, district;
    private ImageView avartar;
    private User user;
    private String mOtherUid;

    public static void launch(Context context, String otherUid) {
        Intent intent = new Intent(context, VipShowInfoActivity.class);
        intent.putExtra(OTHER_UID, otherUid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vipshowinfo);
        LogHelper.logBusinesscard(this);
        findViewById(R.id.vip_show_back).setOnClickListener(this);
        avartar = (ImageView) findViewById(R.id.vip_show_avartar);
        name = (TextView) findViewById(R.id.vip_show_name);
        industry = (TextView) findViewById(R.id.vip_show_industry);
        level = (TextView) findViewById(R.id.vip_show_level);
        id = (TextView) findViewById(R.id.vip_show_id);
        district = (TextView) findViewById(R.id.vip_show_district);

        mOtherUid = getIntent().getStringExtra(OTHER_UID);
        user = SlateDataHelper.getUserLoginInfo(this);

        if (user == null) {
            String broadcastIntent = "cn.com.modernmediausermodel.LoginActivity_nomal";
            Intent intent = new Intent(broadcastIntent);
            sendBroadcast(intent);
        }

    }

    private void initData() {
        showLoadingDialog(true);
        OperateController.getInstance(this).showVipInfo(user.getUid(), user.getToken(), mOtherUid, new FetchEntryListener() {
            @Override
            public void setData(Entry entry) {
                showLoadingDialog(false);
                if (entry instanceof User) {
                    initViews((User) entry);
                }
            }
        });
    }


    private void initViews(User user) {
        setAvatar(this, user.getAvatar(), avartar);
        if (!TextUtils.isEmpty(user.getRealname())) {
            name.setText(user.getRealname());
        } else {
            name.setText("——");
            name.setTextColor(getResources().getColor(R.color.btn_cancel_color));
        }
        if (user.getLevel() == 2) {//VIP 不用考虑有效期
            level.setText(getString(R.string.vip_show_vip));
        } else {
            level.setText(getString(R.string.vip_show_normal));
        }
        if (!TextUtils.isEmpty(user.getVip())) {
            id.setText("NO." + user.getVip());
        } else {
            id.setText("——");
            id.setTextColor(getResources().getColor(R.color.btn_cancel_color));
        }
        if (!TextUtils.isEmpty(user.getProvince()) && !TextUtils.isEmpty(user.getCity())) {
            district.setText(user.getProvince() + " " + user.getCity());
        } else {
            district.setText("——");
            district.setTextColor(getResources().getColor(R.color.btn_cancel_color));
        }
        if (!TextUtils.isEmpty(user.getIndustry())) {
            industry.setText(user.getIndustry());
        } else {
            industry.setText("——");
            industry.setTextColor(getResources().getColor(R.color.btn_cancel_color));
        }
    }

    /**
     * 设置头像
     *
     * @param url
     * @param avatar
     */
    public static void setAvatar(Context context, String url, final ImageView avatar) {
        transforCircleBitmap(context, R.drawable.avatar_placeholder, avatar);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        SlateApplication.finalBitmap.display(url, new ImageDownloadStateListener() {

            @Override
            public void loading() {

            }

            @Override
            public void loadOk(Bitmap bitmap, NinePatchDrawable drawable, byte[] gifByte) {
                transforCircleBitmap(bitmap, avatar);
            }

            @Override
            public void loadError() {
            }
        });
    }

    public static void transforCircleBitmap(Context context, int resId, ImageView imageView) {
        transforCircleBitmap(BitmapFactory.decodeResource(context.getResources(), resId), imageView);
    }

    /**
     * 把图片转换成圆形
     *
     * @param bitmap
     * @return
     */
    public static void transforCircleBitmap(Bitmap bitmap, ImageView imageView) {
        if (bitmap == null) return;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 取小的
        int radius = width > height ? height / 2 : width / 2;
        Bitmap output = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        int left = 0, top = 0;
        int right = width, bottom = height;
        if (width > height) {
            left = width / 2 - radius;
            right = width / 2 + radius;
        } else if (width < height) {
            top = height / 2 - radius;
            bottom = height / 2 + radius;
        }
        Rect src = new Rect(left, top, right, bottom);// 截取原始图片的地方
        Rect dst = new Rect(0, 0, 2 * radius, 2 * radius);

        paint.setAntiAlias(true);
        canvas.drawCircle(radius, radius, radius - STROKE, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, src, dst, paint);

        setCircle(imageView, output, canvas, radius);
    }

    /**
     * 画边框
     *
     * @param imageView
     * @param output
     * @param canvas
     * @param radius
     */
    public static void setCircle(ImageView imageView, Bitmap output, Canvas canvas, int radius) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE);
        paint.setColor(Color.parseColor("#CCCCCC"));
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawCircle(radius, radius, radius - STROKE, paint);
        imageView.setImageBitmap(output);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vip_show_back) {
            finish();
        }
    }

    @Override
    public String getActivityName() {
        return VipShowInfoActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = SlateDataHelper.getUserLoginInfo(this);
        if (user != null) {
            initData();
        }
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
