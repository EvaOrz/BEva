package cn.com.modernmedia.breakpoint;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.os.Handler;
import android.os.Message;

/**
 * Android Zip压缩解压缩
 */
public class ZipUtil {
	public static final int SUCCESS = 0;
	public static final int FAILED = 1;
	private UnZipCallBack callBack;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (callBack != null) {
				switch (msg.what) {
				case SUCCESS:
					callBack.callBack(true);
					break;
				case FAILED:
					callBack.callBack(false);
					break;
				default:
					break;
				}
			}
		}

	};

	public interface UnZipCallBack {
		public void callBack(boolean success);
	}

	/**
	 * 解压缩功能. 将zipFile文件解压到folderPath目录下.
	 * 
	 */
	public void Unzip(final String zipFile, final String targetDir,
			final UnZipCallBack callBack) {
		this.callBack = callBack;
		new Thread() {

			@Override
			public void run() {
				int BUFFER = 4096; // 这里缓冲区我们使用4KB，
				String strEntry; // 保存每个zip的条目名称
				try {
					BufferedOutputStream dest = null; // 缓冲输出流
					FileInputStream fis = new FileInputStream(zipFile);
					ZipInputStream zis = new ZipInputStream(
							new BufferedInputStream(fis));
					ZipEntry entry; // 每个zip条目的实例

					while ((entry = zis.getNextEntry()) != null) {
						try {
							int count;
							byte data[] = new byte[BUFFER];
							strEntry = entry.getName();

							File entryFile = new File(targetDir + strEntry);
							File entryDir = new File(entryFile.getParent());
							if (!entryDir.exists()) {
								entryDir.mkdirs();
							}

							FileOutputStream fos = new FileOutputStream(
									entryFile);
							dest = new BufferedOutputStream(fos, BUFFER);
							while ((count = zis.read(data, 0, BUFFER)) != -1) {
								dest.write(data, 0, count);
							}
							dest.flush();
							dest.close();
						} catch (Exception ex) {
							ex.printStackTrace();
							handler.sendEmptyMessage(FAILED);
						}
					}
					zis.close();
					handler.sendEmptyMessage(SUCCESS);
				} catch (Exception cwj) {
					cwj.printStackTrace();
					handler.sendEmptyMessage(FAILED);
				}
			}
		}.start();
	}
}