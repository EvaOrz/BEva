package cn.com.modernmediausermodel.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.listener.ImageDownloadStateListener;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.DateFormatTool;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediausermodel.R;

public class UserTools {
    public static final int REQUEST_ZOOM = 111;
    public static final int STROKE = 2;// 边框宽度
    private static Handler handler = new Handler();

    /**
     * 检查输入的密码是否合法,当前只检查是否为null或者空
     *
     * @param password 密码
     * @return true:密码格式有效; false:给出相应的错误提示
     */
    public static boolean checkPasswordFormat(Context context, String password) {
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, R.string.msg_login_pwd_null, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 检查输入的数据是否合法,当前检查数据是否是邮箱形式（手机号码）及是否为null或者空
     *
     * @param data 账号
     * @return true:账号格式有效; false:给出相应的错误提示
     */
    public static boolean checkIsEmailOrPhone(Context context, String data) {
        if (TextUtils.isEmpty(data)) {
            String text = context.getString(R.string.msg_login_email_null);
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean ifEmail = true, ifPhone = true;
        data = data.trim();
        // 检查账号是否是邮箱格式
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]" + "{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern pattern = Pattern.compile(str);
        Matcher matcher = pattern.matcher(data);
        if (!matcher.matches()) {
            ifEmail = false;
        }
        // 检查账号是否是电话号码格式
        ifPhone = checkIsPhone(context, data);

        if (!(ifEmail || ifPhone)) {
            String text = context.getString(R.string.msg_login_email_error);
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }

        return ifEmail || ifPhone;
    }


    public static boolean checkIsEmail(Context context, String data) {
        if (TextUtils.isEmpty(data)) {
            String text = context.getString(R.string.msg_login_email_null);
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean ifEmail = true;
        data = data.trim();
        // 检查账号是否是邮箱格式
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]" + "{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern pattern = Pattern.compile(str);
        Matcher matcher = pattern.matcher(data);
        if (!matcher.matches()) {
            ifEmail = false;
        }
        return ifEmail;
    }

    /**
     * 检查输入的数据是否合法,当前检查数据是否是手机号码
     *
     * @param data 账号
     * @return true:账号格式有效; false:给出相应的错误提示
     */
    public static boolean checkIsPhone(Context context, String data) {
        //        String str1 = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
        //        Pattern p = Pattern.compile(str1);
        //        Matcher m = p.matcher(data);
        //        if (!m.matches()) {
        //            return false;
        //        }
        if (data.length() == 11 && !data.contains("@") && !(Pattern.compile("[a-zA-z]").matcher(data).find()))
            return true;
        else return false;

    }

    /**
     * 弹出一个带标题、内容及确定按钮的对话框
     *
     * @param context 上下文环境
     * @param title   标题
     * @param message 消息内容
     */
    public static void showDialogMsg(Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton(context.getString(R.string.sure), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();

    }

    /**
     * 进入系统的图片裁剪页面
     *
     * @param activity
     * @param uri
     * @param path     图片保存路径
     */
    public static void startPhotoZoom(final Activity activity, final Uri uri, final String path) {
        if (uri == null || TextUtils.isEmpty(path)) return;
        // TODO 防止当拍完照立马剪裁，内存被系统回收导致不执行接下来的方法，放在handler里
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(uri, "image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 64);
                intent.putExtra("outputY", 64);
                intent.putExtra("scale", true);// 黑边
                intent.putExtra("scaleUpIfNeeded", true);// 黑边
                intent.putExtra("return-data", true);
                intent.putExtra("output", Uri.fromFile(new File(path))); // 保存路径
                activity.startActivityForResult(intent, REQUEST_ZOOM);
            }
        }, 500);
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
        Bitmap output = Bitmap.createBitmap(radius * 2, radius * 2, Config.ARGB_8888);
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
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

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
    private static void setCircle(ImageView imageView, Bitmap output, Canvas canvas, int radius) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(STROKE);
        paint.setColor(Color.parseColor("#FFdbdbdb"));
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
        canvas.drawCircle(radius, radius, radius - STROKE, paint);
        imageView.setImageBitmap(output);
    }

    /**
     * 判断用户名、密码是否为空
     *
     * @param str
     * @param view
     * @return
     */
    public static boolean checkString(String str, View view, Animation animation) {
        if (TextUtils.isEmpty(str)) {
            UserTools.doAnim(view, animation);
            return false;
        }
        return true;
    }

    /**
     * 执行动画
     *
     * @param view
     * @param animation
     */
    private static void doAnim(View view, Animation animation) {
        view.startAnimation(animation);
    }

    /**
     * 格式化时间，默认为:yyyy-MM-dd HH:mm
     *
     * @param milliseconds
     * @return
     */
    public static String dateFormat(String milliseconds) {
        long time = ParseUtil.stol(milliseconds);
        if (time == 0) {
            return "";
        }
        time = time * 1000L;
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeInMillis(time);
        // 当前时间
        Calendar current = Calendar.getInstance(Locale.CHINA);
        if (calendar.get(Calendar.YEAR) == current.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == current.get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) == current.get(Calendar.DAY_OF_MONTH)) {
            // 同一天
            return DateFormatTool.format(time, "HH:mm");
        }
        return DateFormatTool.format(time, "yyyy-MM-dd");
    }


    /**
     * 卡片分享
     *
     * @param context
     * @param content
     */
    public static void shareCard(Context context, String content) {
        if (TextUtils.isEmpty(content)) return;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share));
        intent.putExtra(Intent.EXTRA_TEXT, content);
        context.startActivity(Intent.createChooser(intent, ""));
    }

    /**
     * 设置头像
     *
     * @param user
     * @param avatar
     */
    public static void setAvatar(Context context, User user, ImageView avatar) {
        if (user != null) {
            setAvatar(context, user.getAvatar(), avatar);
        } else {
            setAvatar(context, "", avatar);
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

    /**
     * 转化数字
     *
     * @return
     * @CASE1 如果 >= 10,000，就除以1,000，保留一位，后面加K---12,345 -> 12.3k
     * @CASE2 如果 >= 1,000,000，就除以1,000,000，保留一位，后面加M---12,345,678 -> 12.3M
     */
    public static String changeNum(int num) {
        if (num < 10000) {
            return num + "";
        } else if (num < 1000000) {
            double res = num / 1000d;
            return NumberFormatHelper.getInstance().formatDoubleValue(res) + "K";
        }
        double res = num / 1000000d;
        return NumberFormatHelper.getInstance().formatDoubleValue(res) + "M";
    }

    /**
     * 根据文本名称设置资源id
     *
     * @param textView
     * @return
     */
    public static void setText(TextView textView, String str) {
        if (SlateApplication.stringCls == null) {
            try {
                throw new NullPointerException("string class is null!");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return;
        }
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            Field field = SlateApplication.stringCls.getDeclaredField(str);
            textView.setText(field.getInt(str));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清理缓存
     */
    public static void clearApplicationData(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
            File imgsdir = new File(Environment.getExternalStorageDirectory().getPath() + SlateApplication.DEFAULT_IMAGE_PATH);
            if (imgsdir != null && imgsdir.isDirectory()) {
                deleteDir(imgsdir);
            }
            File videodir = new File(Environment.getExternalStorageDirectory().getPath() + SlateApplication.DEFAULT_VIDEO_PATH);
            if (videodir != null && videodir.isDirectory()) {
                deleteDir(videodir);
            }
            File apkdir = new File(Environment.getExternalStorageDirectory().getPath() + ConstData.DEFAULT_APK_PATH);
            if (apkdir != null && apkdir.isDirectory()) {
                deleteDir(apkdir);
            }
            File pakedir = new File(Environment.getExternalStorageDirectory().getPath() + ConstData.DEFAULT_PACKAGE_PATH);
            if (pakedir != null && pakedir.isDirectory()) {
                deleteDir(pakedir);
            }
            File temdir = new File(Environment.getExternalStorageDirectory().getPath() + ConstData.DEFAULT_TEMPLATE_PATH);
            if (temdir != null && temdir.isDirectory()) {
                deleteDir(temdir);
            }
            File datadir = new File(Environment.getExternalStorageDirectory().getPath() + ConstData.DEFAULT_DATA_PATH);
            if (datadir != null && datadir.isDirectory()) {
                deleteDir(datadir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
