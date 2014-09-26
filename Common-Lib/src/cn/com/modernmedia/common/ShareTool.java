package cn.com.modernmedia.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.util.BitmapUtil;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.sina.SinaAPI;
import cn.com.modernmedia.util.sina.SinaAuth;
import cn.com.modernmedia.util.sina.SinaRequestListener;
import cn.com.modernmedia.util.sina.UserModelAuthListener;
import cn.com.modernmedia.widget.ArticleDetailItem;
import cn.com.modernmediaslate.unit.Tools;

public class ShareTool {
	private Context mContext;
	private static final String SHARE_IMAGE_PATH_NAME = "/"
			+ CommonApplication.mConfig.getCache_file_name() + "/temp/";// 分享图片临时文件夹，退出应用删除
	private static final String SAVE_IMAGE_PATH_NAME = "/"
			+ CommonApplication.mConfig.getCache_file_name() + "/";// 保存图片
	private String defaultPath = Environment.getExternalStorageDirectory()
			.getPath();
	private static final int MAX_HEIGHT = 3000;

	public ShareTool(Context context) {
		mContext = context;
	}

	/**
	 * 通过邮箱分享
	 * 
	 * @param intent
	 * @param emailSubject
	 *            title
	 * @param emailBody
	 *            desc
	 * @param bitmap
	 * 
	 */
	public void shareByMail(Intent intent, String emailSubject,
			String emailBody, Bitmap bitmap) {
		// 图片文件
		File file = createShareBitmap(bitmap);
		intent.setType("text/html");
		intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
		intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(emailBody));
		if (file != null) {
			Uri uri = Uri.parse("file://" + file);
			if (uri != null) {
				intent.putExtra(Intent.EXTRA_STREAM, uri);
			}
		}
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		mContext.startActivity(intent);
	}

	/**
	 * 通过其他渠道（非邮箱）分享
	 * 
	 * @param intent
	 * @param extraText
	 *            share desc
	 * @param bitmap
	 */
	public void shareWithoutMail(Intent intent, String extraText, Bitmap bitmap) {
		// 图片文件
		File file = createShareBitmap(bitmap);
		intent.setType(bitmap == null ? "text/*" : "image/*");
		intent.putExtra(Intent.EXTRA_TEXT, extraText);
		if (file != null) {
			Uri uri = Uri.parse("file://" + file);
			if (uri != null) {
				intent.putExtra(Intent.EXTRA_STREAM, uri);
			}
		}
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		mContext.startActivity(intent);
	}

	/**
	 * 新浪微博分享
	 * 
	 * @param extraText
	 * @param bitmap
	 */
	public void shareWithSina(String extraText, Bitmap bitmap) {
		String path = "";
		File file = createShareBitmap(bitmap);
		if (file != null && file.exists())
			path = file.getAbsolutePath();
		shareWidthSina(extraText, path);
	}

	/**
	 * 分享朋友
	 * 
	 * @param extraText
	 */
	public void shareToFriend(String extraText) {
		Intent intent = new Intent();
		ComponentName comp = new ComponentName("com.tencent.mm",
				"com.tencent.mm.ui.tools.ShareImgUI");
		intent.setComponent(comp);
		intent.setAction("android.intent.action.SEND");
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_TEXT, extraText);
		mContext.startActivity(intent);
	}

	/**
	 * 分享至朋友圈
	 * 
	 * @param bitmap
	 */
	public void shareToMoments(Bitmap bitmap) {
		if (bitmap == null)
			return;
		File file = createShareBitmap(bitmap);
		Intent intent = new Intent();
		ComponentName comp = new ComponentName("com.tencent.mm",
				"com.tencent.mm.ui.tools.ShareToTimeLineUI");
		intent.setComponent(comp);
		intent.setAction("android.intent.action.SEND");
		intent.setType("image/*");
		if (file != null) {
			Uri uri = Uri.parse("file://" + file);
			if (uri != null) {
				intent.putExtra(Intent.EXTRA_STREAM, uri);
			}
		}
		mContext.startActivity(intent);
	}

	/**
	 * iweekly分享文章截屏
	 * 
	 * @param intent
	 * @param extraText
	 * @param bottomResId
	 *            底部拼接图片的资源id
	 */
	public void shareWithScreen(String extraText, int bottomResId) {
		String path = getScreen(bottomResId);
		if (TextUtils.isEmpty(path)) {
			Toast.makeText(mContext, "请检查SD卡", ConstData.TOAST_LENGTH).show();
			return;
		}
		shareWidthSina(extraText, path);
	}

	private void shareWidthSina(final String content, final String path) {
		SinaAuth auth = new SinaAuth(mContext);
		if (auth.checkIsOAuthed()) {
			SinaAPI.getInstance(mContext).sendTextAndImage(content, path,
					new SinaRequestListener() {

						@Override
						public void onSuccess(String response) {
							Tools.showToast(mContext,
									R.string.Weibo_Share_Success);
						}

						@Override
						public void onFailed(String error) {
							Tools.showToast(mContext,
									R.string.Weibo_Share_Error);
						}
					});
		} else {
			auth.oAuth();
			auth.setAuthListener(new UserModelAuthListener() {

				@Override
				public void onCallBack(boolean isSuccess) {
					if (isSuccess) {
						shareWidthSina(content, path);
					}
				}
			});
		}
	}

	public void saveToGallery(Bitmap bitmap) {
		// OutputStream outputStream = null;
		long dateTaken = System.currentTimeMillis();
		String fileName = createName(dateTaken);
		File file = createShareBitmap(bitmap, fileName, true);
		if (file == null) {
			Toast.makeText(mContext, R.string.save_picture_fail,
					Toast.LENGTH_SHORT).show();
			return;
		}
		// use MediaScannerConnection
		// MediaScannerConnection.scanFile(mContext, new
		// String[]{file.getPath()}, new String[]{"image/jpeg"}, null);

		// use Broadcast
		// mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
		// Uri.parse("file://" + Environment.getExternalStorageDirectory())));

		// use ContentResolver
		ContentValues values = new ContentValues(7);
		values.put(MediaStore.Images.Media.TITLE, fileName);
		values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
		values.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken);
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		values.put(MediaStore.Images.Media.ORIENTATION, 0);
		values.put(MediaStore.Images.Media.DATA, file.getPath());
		values.put(MediaStore.Images.Media.SIZE, file.length());
		mContext.getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	}

	private File createShareBitmap(Bitmap bitmap, String fileName, boolean save) {
		if (bitmap == null)
			return null;
		if (TextUtils.isEmpty(fileName)) {
			fileName = createName(0);
		}
		String imagePath = defaultPath
				+ (save ? SAVE_IMAGE_PATH_NAME : SHARE_IMAGE_PATH_NAME);
		File file = new File(imagePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		File picPath = new File(imagePath + fileName);
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(picPath), 1024);
			bitmap.compress(CompressFormat.JPEG, 80, bos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (bos != null) {
					bos.flush();
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return picPath;
	}

	private File createShareBitmap(Bitmap bitmap) {
		return createShareBitmap(bitmap, null, false);
	}

	private String createName(long dateTaken) {
		dateTaken = dateTaken == 0 ? System.currentTimeMillis() : dateTaken;
		return DateFormat.format("yyyy-MM-dd_kk.mm.ss", dateTaken).toString()
				+ ".jpg";
	}

	/**
	 * 获取iweekly文章页面截图
	 * 
	 * @param bottomResId
	 */
	private String getScreen(int bottomResId) {
		if (!(mContext instanceof CommonArticleActivity))
			return null;
		View view = ((CommonArticleActivity) mContext).getCurrView();
		if (!(view instanceof ArticleDetailItem)) {
			return null;
		}
		ArticleDetailItem detailItem = (ArticleDetailItem) view;
		WebView webView = detailItem.getWebView();
		if (webView == null) {
			return null;
		}
		// 1.原始图切出来最高到3000，2.图片宽为640
		Picture picture = webView.capturePicture();
		float scale = 640f / picture.getWidth();
		float height = scale * picture.getHeight();
		if (height > MAX_HEIGHT) {
			height = MAX_HEIGHT / scale;
		} else {
			height = picture.getHeight();
		}
		Bitmap b = Bitmap.createBitmap(picture.getWidth(), (int) height,
				Config.RGB_565);
		Canvas c = new Canvas(b);
		picture.draw(c);

		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale); // 长和宽放大缩小的比例
		b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix,
				true);
		// TODO 拼接
		Bitmap bottom = BitmapUtil.readBitMap(mContext, bottomResId);
		Bitmap result = Bitmap.createBitmap(b.getWidth(), b.getHeight()
				+ bottom.getHeight(), Config.RGB_565);
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(b, 0, 0, null);
		canvas.drawBitmap(bottom, 0, b.getHeight(), null);

		FileOutputStream fos = null;
		File file = new File(defaultPath + SHARE_IMAGE_PATH_NAME);
		if (!file.exists()) {
			file.mkdirs();
		}
		String screen_path = defaultPath + SHARE_IMAGE_PATH_NAME
				+ createName(0);
		try {
			fos = new FileOutputStream(screen_path);
			if (fos != null) {
				result.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (b != null && !b.isRecycled())
				b.recycle();
			if (bottom != null && !bottom.isRecycled())
				bottom.recycle();
			if (result != null && !result.isRecycled())
				result.recycle();
			CommonApplication.callGc();
		}
		return screen_path;
	}

	/**
	 * 离开应用删除临时分享的图片
	 */
	public void deleteShareImages() {
		String dataPath = defaultPath + SHARE_IMAGE_PATH_NAME;
		File file = new File(dataPath);
		if (!file.exists())
			return;
		File[] files = file.listFiles();
		if (files == null || files.length == 0)
			return;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				continue;
			} else {
				files[i].delete();
			}
		}
	}
}