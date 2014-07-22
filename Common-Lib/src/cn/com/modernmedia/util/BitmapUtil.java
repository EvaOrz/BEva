package cn.com.modernmedia.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;

public class BitmapUtil {
	public static final int UNCONSTRAINED = -1;

	/*
	 * 
	 * 获得设置信息
	 */

	private static Options getOptions(String path) {
		Options options = new Options();
		options.inJustDecodeBounds = true;// 只描边，不读取数据
		BitmapFactory.decodeFile(path, options);
		return options;

	}

	public static Bitmap getBitmapByPath(String path, int width, int height) {
		return getBitmapByPath(path, width, height, -1);
	}

	public static Bitmap getPhoto(String path, int width, int height) {
		ExifInterface exifInterface = null;
		try {
			exifInterface = new ExifInterface(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 获取图片的旋转角度
		int tag = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
				-1);
		int orientation = 0;
		if (tag == ExifInterface.ORIENTATION_ROTATE_90) {
			orientation = 90;
		} else if (tag == ExifInterface.ORIENTATION_ROTATE_180) {
			orientation = 180;
		} else if (tag == ExifInterface.ORIENTATION_ROTATE_270) {
			orientation = 270;
		}
		return getBitmapByPath(path, width, height, orientation);
	}

	/**
	 * 
	 * 获得图像
	 * 
	 * @param path
	 * 
	 * @param options
	 * 
	 * @return
	 * 
	 * @throws FileNotFoundException
	 */
	public static Bitmap getBitmapByPath(String path, int width, int height,
			int orientation) {
		Bitmap b = null;
		// FileInputStream in = null;
		try {
			File file = new File(path);
			if (!file.exists()) {
				return null;
			}
			// in = new FileInputStream(file);
			Options options = getOptions(path);
			if (options != null) {
				int ratio = 1; // 默认为不缩放
				if (width != 0 && height != 0) {
					ratio = calculateInSampleSize(options, width, height);
				}

				if (ratio == 1 && file.length() > 100 * 1024) {
					ratio = 2;
				}

				options.inSampleSize = ratio; // 设置缩放比例
				// PrintHelper.print("图片的缩放比例值inSampleSize:" + ratio);

				if (ConstData.getAppId() == 20 || orientation != -1) {
					String imageType = options.outMimeType;
					if (!TextUtils.isEmpty(imageType)
							&& "image/jpeg".equals(imageType)) {
						options.inPreferredConfig = Bitmap.Config.RGB_565;
					}
				}
				options.inPurgeable = true;
				options.inJustDecodeBounds = false;
			}
			b = BitmapFactory.decodeFile(path, options);
			// PrintHelper.print(url + "===after "
			// + (b.getRowBytes() * b.getHeight()) / 1024 + "kb");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		if (b != null && orientation != -1 && width > 0 && height > 0) {
			Matrix matrix = new Matrix();
			matrix.setRotate(orientation);
			return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
					matrix, true);
		}
		return b;
	}

	/**
	 * 根据指定的压缩比例，获得合适的Bitmap（方法二）
	 * 
	 * @param inStream
	 *            InputStream
	 * @param width
	 *            指定的宽度
	 * @param height
	 *            指定的高度
	 * @return Bitmap
	 * @throws IOException
	 */
	public static Bitmap decodeStream(InputStream inStream, int width,
			int height) throws IOException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 从输入流读取数据
		byte[] data = read(inStream);
		// System.out.println("before:" + data.length);
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		// 从服务器端获取的图片大小为：80x120
		// 我们想要的图片大小为：40x40
		// 缩放比：120/40 = 3，也就是说我们要的图片大小为原图的1/3
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int ratio = calculateInSampleSize(options, width, height);
		// System.out.println("图片的缩放比例值ratio = " + ratio);
		// 属性值inSampleSize表示缩略图大小为原始图片大小的几分之一，即如果这个值为2，
		// 则取出的缩略图的宽和高都是原始图片的1/2，图片大小就为原始大小的1/4。
		options.inSampleSize = ratio;
		if ("image/jpeg".equals(options.outMimeType)) {
			options.inPreferredConfig = Bitmap.Config.RGB_565;
		}
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		// System.out
		// .println("after:" + bitmap.getRowBytes() * bitmap.getHeight());
		return bitmap;
	}

	/**
	 * 从输入流读取数据
	 * 
	 * @param inStream
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	private static byte[] read(InputStream inStream) throws IOException {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}

	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

			// This offers some additional logic in case the image has a strange
			// aspect ratio. For example, a panorama may have a much larger
			// width than height. In these cases the total pixels might still
			// end up being too large to fit comfortably in memory, so we should
			// be more aggressive with sample down the image (=larger
			// inSampleSize).

			final float totalPixels = width * height;

			// Anything more than 2x the requested pixels we'll sample down
			// further
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}

		return inSampleSize;
	}

	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}
}
