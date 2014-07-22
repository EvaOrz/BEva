package cn.com.modernmedia.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.BaseFragmentActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.Entry;
import cn.com.modernmedia.model.Version;

/**
 * 版本升级
 * 
 * @author ZhuQiao
 * 
 */
public class UpdateManager {
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;
	public static final int CHECK_DOWN = 3;

	private Context mContext;
	private OperateController controller;
	private CheckVersionListener listener;
	/* 进度条与通知ui刷新的handler和msg常量 */
	private ProgressBar mProgress;
	private int progress;
	private boolean interceptFlag = false;
	private String apkName = "";
	private Version version;
	private Dialog dialog;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				installApk();
				break;
			case CHECK_DOWN:
				update();
				break;
			default:
				break;
			}
		};
	};

	public interface CheckVersionListener {
		/**
		 * 比较已经停止，继续读取getissue接口
		 */
		public void checkEnd();
	}

	public UpdateManager(Context mContext, CheckVersionListener listener) {
		this.mContext = mContext;
		this.listener = listener;
		controller = new OperateController(mContext);
	}

	/**
	 * 获取服务器数据
	 */
	public void checkVersion() {
		if (!compareTime())
			return;
		controller.checkVersion(new FetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				if (entry != null && entry instanceof Version) {
					version = (Version) entry;
					compare();
				} else {
					listener.checkEnd();
				}
			}
		});
	}

	/**
	 * 是否距离上一次点击取消超过一天(一天内部重复提示升级)
	 * 
	 * @return true提示；false不提示
	 */
	private boolean compareTime() {
		long lastTime = DataHelper.getUpdate(mContext);
		if (lastTime == 0) {
			return true;
		}
		long hour = (System.currentTimeMillis() - lastTime) / (1000 * 60 * 60);
		if (hour > 24)
			return true;
		listener.checkEnd();
		return false;
	}

	private void compare() {
		if (version.getVersion() > ConstData.VERSION
				&& !TextUtils.isEmpty(version.getDownload_url())) {
			mHandler.sendEmptyMessage(CHECK_DOWN);
		} else {
			listener.checkEnd();
		}
	}

	/**
	 * 显示更新dialog
	 * 
	 * @param version
	 */
	private void update() {
		Builder builder = new Builder(mContext);
		builder.setTitle(R.string.update);
		builder.setMessage(ConstData.APP_NAME + " " + version.getChangelog());
		builder.setPositiveButton(R.string.download, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				downNow();
			}
		});
		builder.setNegativeButton(R.string.download_later,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						DataHelper.setUpdte(mContext,
								System.currentTimeMillis());
						listener.checkEnd();
					}
				});
		try {
			builder.create().show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void dissDialog() {
		if (dialog != null && dialog.isShowing()) {
			try {
				dialog.cancel();
				dialog.dismiss();
				dialog = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void downNow() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.update);
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.update_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_process);
		builder.setView(v);
		builder.setNegativeButton(R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				interceptFlag = true;
				DataHelper.setUpdte(mContext, System.currentTimeMillis());
				listener.checkEnd();
			}
		});
		try {
			dialog = builder.create();
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		downLoadApk();
	}

	/**
	 * 下载apk
	 */
	private void downLoadApk() {
		apkName = new String(ConstData.getAppName() + version.getVersion() + ".apk");
		new DownApkThread(version.getDownload_url()).start();
	}

	private class DownApkThread extends Thread {
		private String apkUrl;

		public DownApkThread(String url) {
			apkUrl = url;
		}

		@Override
		public void run() {
			FileOutputStream fos = null;
			InputStream is = null;
			try {
				URL url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				is = conn.getInputStream();

				File ApkFile = FileManager.getApkByName(apkName);
				fos = new FileOutputStream(ApkFile);

				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成通知安装
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载.
			} catch (Exception e) {
				e.printStackTrace();
				showToast();
				dissDialog();
				listener.checkEnd();
			} finally {
				try {
					if (fos != null)
						fos.close();
					if (is != null)
						is.close();
					dissDialog();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 安装apk
	 * 
	 * @param url
	 */
	private void installApk() {
		File apkfile = FileManager.getApkByName(apkName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}

	private void showToast() {
		if (mContext instanceof BaseActivity)
			((BaseActivity) mContext).showToast(R.string.download_error);
		else if(mContext instanceof BaseFragmentActivity)
			((BaseFragmentActivity) mContext).showToast(R.string.download_error);
	}
}
