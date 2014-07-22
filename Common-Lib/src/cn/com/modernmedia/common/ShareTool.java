package cn.com.modernmedia.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
import android.text.format.DateFormat;
import android.webkit.WebView;
import android.widget.Toast;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.util.BitmapUtil;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.widget.ArticleDetailItem;

public class ShareTool {
	private Context mContext;
	private static final String SHARE_IMAGE_NAME = "share_image.jpg";
	private static final String SAVE_IMAGE_PATH_NAME = "/"
			+ ConstData.getAppName() + "/";
	private String defaultPath = Environment.getExternalStorageDirectory()
			.getPath();
	private static final String SCREEN_PATH = Environment
			.getExternalStorageDirectory().getPath()
			+ "/"
			+ ConstData.getAppName() + "/screen.jpg";
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
		File file = createShareBitmap(bitmap, SHARE_IMAGE_NAME);
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
		File file = createShareBitmap(bitmap, SHARE_IMAGE_NAME);
		intent.setType("image/*");
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
	 * iweekly分享文章截屏
	 * 
	 * @param intent
	 * @param extraText
	 * @param bottomResId
	 *            底部拼接图片的资源id
	 */
	public void shareWithScreen(Intent intent, String extraText, int bottomResId) {
		getScreen(bottomResId);
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_TEXT, extraText);
		File file = new File(SCREEN_PATH);
		if (file != null) {
			Uri uri = Uri.parse("file://" + file);
			if (uri != null) {
				intent.putExtra(Intent.EXTRA_STREAM, uri);
			}
		}
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		mContext.startActivity(intent);
	}

	public void saveToGallery(Bitmap bitmap) {
		// OutputStream outputStream = null;
		long dateTaken = System.currentTimeMillis();
		String fileName = createName(dateTaken) + ".jpg";
		File file = createShareBitmap(bitmap, fileName);
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

	private File createShareBitmap(Bitmap bitmap, String fileName) {
		if (bitmap == null)
			return null;
		String imagePath = defaultPath + SAVE_IMAGE_PATH_NAME;
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

	private String createName(long dateTaken) {
		return DateFormat.format("yyyy-MM-dd_kk.mm.ss", dateTaken).toString();
	}

	/**
	 * 获取iweekly文章页面截图
	 * @param bottomResId
	 */
	private void getScreen(int bottomResId) {
		if (!(mContext instanceof CommonArticleActivity))
			return;
		ArticleDetailItem detailItem = ((CommonArticleActivity) mContext)
				.getCurrentDetailItem();
		if (detailItem == null) {
			return;
		}
		WebView webView = detailItem.getWebView();
		if (webView == null) {
			return;
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
		try {
			fos = new FileOutputStream(SCREEN_PATH);
			if (fos != null) {
				result.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (b != null && !b.isRecycled())
				b.recycle();
			if (bottom != null && !bottom.isRecycled())
				bottom.recycle();
			if (result != null && !result.isRecycled())
				result.recycle();
			CommonApplication.callGc();
		}
	}
}