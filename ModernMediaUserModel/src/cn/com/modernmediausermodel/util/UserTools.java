package cn.com.modernmediausermodel.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.net.Uri;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.listener.ImageDownloadStateListener;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.model.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UserTools {
	public static final int REQUEST_ZOOM = 111;
	public static final int STROKE = 1;// 边框宽度
	private static Handler handler = new Handler();
	private static SimpleDateFormat beforeToday = new SimpleDateFormat(
			"yyyy-MM-dd");
	private static SimpleDateFormat today = new SimpleDateFormat("HH:mm");

	/**
	 * 检查输入的密码是否合法,当前只检查是否为null或者空
	 * 
	 * @param password
	 *            密码
	 * @return true:密码格式有效; false:给出相应的错误提示
	 */
	public static boolean checkPasswordFormat(Context context, String password) {
		if (TextUtils.isEmpty(password)) {
			Toast.makeText(context, R.string.msg_login_pwd_null,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 检查输入的数据是否合法,当前检查数据是否是邮箱形式及是否为null或者空
	 * 
	 * @param data
	 *            账号
	 * @return true:账号格式有效; false:给出相应的错误提示
	 */
	public static boolean checkIsEmail(Context context, String data) {
		if (TextUtils.isEmpty(data)) {
			String text = context.getString(R.string.msg_login_email_null);
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			return false;
		}
		// 检查账号是否是邮箱格式
		data = data.trim();
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]"
				+ "{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern pattern = Pattern.compile(str);
		Matcher matcher = pattern.matcher(data);
		if (!matcher.matches()) {
			String text = context.getString(R.string.msg_login_email_error);
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 弹出一个带标题、内容及确定按钮的对话框
	 * 
	 * @param context
	 *            上下文环境
	 * @param title
	 *            标题
	 * @param message
	 *            消息内容
	 */
	public static void showDialogMsg(Context context, String title,
			String message) {
		new AlertDialog.Builder(context)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(context.getString(R.string.sure),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create().show();

	}

	/**
	 * 进入系统的图片裁剪页面
	 * 
	 * @param activity
	 * @param uri
	 * @param path
	 *            图片保存路径
	 */
	public static void startPhotoZoom(final Activity activity, final Uri uri,
			final String path) {
		if (uri == null || TextUtils.isEmpty(path))
			return;
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
				intent.putExtra("output", Uri.fromFile(new File(path))); //
				// 保存路径
				activity.startActivityForResult(intent, REQUEST_ZOOM);
			}
		}, 500);
	}

	public static void transforCircleBitmap(Context context, int resId,
			ImageView imageView) {
		transforCircleBitmap(
				BitmapFactory.decodeResource(context.getResources(), resId),
				imageView);
	}

	/**
	 * 把图片转换成圆形
	 * 
	 * @param bitmapimg
	 * @return
	 */
	public static void transforCircleBitmap(Bitmap bitmap, ImageView imageView) {
		if (bitmap == null)
			return;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 取小的
		int radius = width > height ? height / 2 : width / 2;
		Bitmap output = Bitmap.createBitmap(radius * 2, radius * 2,
				Config.ARGB_8888);
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
		canvas.drawCircle(radius, radius, radius, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

		canvas.drawBitmap(bitmap, src, dst, paint);

		setCircle(imageView, output, canvas, radius);
	}

	/**
	 * 画边框
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	private static void setCircle(ImageView imageView, Bitmap output,
			Canvas canvas, int radius) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(STROKE);
		paint.setColor(Color.parseColor("#FFCCCCCC"));
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
		canvas.drawCircle(radius, radius, radius - STROKE, paint);
		imageView.setImageBitmap(output);
	}

	/**
	 * 把对象转换成json
	 * 
	 * @param entry
	 * @return
	 */
	public static String objectToJson(Entry entry) {
		try {
			GsonBuilder builder = new GsonBuilder();
			// 不转换没有 @Expose 注解的字段
			builder.excludeFieldsWithoutExposeAnnotation();
			Gson gson = builder.create();
			return gson.toJson(entry);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
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
	 * 获取uid
	 * 
	 * @param context
	 * @return
	 */
	public static String getUid(Context context) {
		User user = UserDataHelper.getUserLoginInfo(context);
		return user == null ? ConstData.UN_UPLOAD_UID : user.getUid();
	}

	/**
	 * 格式化时间，默认为:yyyy-MM-dd HH:mm
	 * 
	 * @param date
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
		if (calendar.get(Calendar.YEAR) == current.get(Calendar.YEAR)
				&& calendar.get(Calendar.MONTH) == current.get(Calendar.MONTH)
				&& calendar.get(Calendar.DAY_OF_MONTH) == current
						.get(Calendar.DAY_OF_MONTH)) {
			// 同一天
			return today.format(new Date(time));
		}
		return beforeToday.format(new Date(time));
	}

	/**
	 * 获取设备ID
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	/**
	 * 卡片分享
	 * 
	 * @param context
	 * @param content
	 */
	public static void shareCard(Context context, String content) {
		if (TextUtils.isEmpty(content))
			return;
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share));
		intent.putExtra(Intent.EXTRA_TEXT, content);
		intent.putExtra(ConstData.SHARE_APP_ID, ConstData.getInitialAppId());
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
	public static void setAvatar(Context context, String url,
			final ImageView avatar) {
		transforCircleBitmap(context, R.drawable.avatar_placeholder, avatar);
		if (TextUtils.isEmpty(url)) {
			return;
		}
		CommonApplication.getImageDownloader().download(url,
				new ImageDownloadStateListener() {

					@Override
					public void loading() {
					}

					@Override
					public void loadOk(Bitmap bitmap) {
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
	 * @CASE1 如果 >= 10,000，就除以1,000，保留一位，后面加K---12,345 -> 12.3k
	 * @CASE2 如果 >= 1,000,000，就除以1,000,000，保留一位，后面加M---12,345,678 -> 12.3M
	 * 
	 * @return
	 */
	public static String changeNum(int num) {
		if (num < 10000) {
			return num + "";
		} else if (num < 1000000) {
			double res = num / 1000d;
			return NumberFormatHelper.getInstance().formatDoubleValue(res)
					+ "K";
		}
		double res = num / 1000000d;
		return NumberFormatHelper.getInstance().formatDoubleValue(res) + "M";
	}
}
