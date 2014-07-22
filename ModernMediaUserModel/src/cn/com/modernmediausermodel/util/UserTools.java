package cn.com.modernmediausermodel.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Toast;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.model.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UserTools {
	public static final int REQUEST_ZOOM = 111;
	private static Handler handler = new Handler();

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
				intent.putExtra("output", Uri.fromFile(new File(path))); // 保存路径
				activity.startActivityForResult(intent, REQUEST_ZOOM);
			}
		}, 500);
	}

	/**
	 * 把图片转换成圆形
	 * 
	 * @param bitmapimg
	 * @return
	 */
	public static Bitmap transforCircleBitmap(Bitmap bitmapimg) {
		if (bitmapimg == null)
			return null;
		Bitmap output = Bitmap.createBitmap(bitmapimg.getWidth(),
				bitmapimg.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmapimg.getWidth(),
				bitmapimg.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawCircle(bitmapimg.getWidth() / 2, bitmapimg.getHeight() / 2,
				bitmapimg.getWidth() / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmapimg, rect, rect, paint);
		return output;
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
}
