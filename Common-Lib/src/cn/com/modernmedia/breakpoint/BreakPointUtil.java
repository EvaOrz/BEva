package cn.com.modernmedia.breakpoint;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.breakpoint.ZipUtil.UnZipCallBack;
import cn.com.modernmedia.db.BreakPointDb;
import cn.com.modernmedia.model.BreakPoint;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.ModernMediaTools;

/**
 * 断点下载
 * 
 * 比如：如果要下载的文件大小是 1000 byte，
 * 
 * @1 设置 RANGE: bytes=0-499 表示前500个 byte
 * @2 设置 RANGE: bytes=500-999 表示后500个比byte
 * @3 设置 RANGE: bytes=500- 表示500字节以后的范围
 * @param breakPoint
 * @author ZhuQiao
 * 
 */
public class BreakPointUtil {
	public static final int THEAD_COUNT = 4;
	/**
	 * 未下载
	 */
	public static final int NONE = 1;
	/**
	 * 已下载，但未下载完成
	 */
	public static final int BREAK = 2;
	/**
	 * 下载完成
	 */
	public static final int DONE = 3;

	/**
	 * 未下载
	 */
	public static final int INIT = 10;
	/**
	 * 下载中
	 */
	public static final int DOWNLOADING = 11;
	/**
	 * 暂停下载
	 */
	public static final int PAUSE = 12;

	public static final int SUCCESS_MSG = 1;
	// public static final int SINGLE_SUCCESS_MSG = 2;
	public static final int FAILED_MSG = 3;
	// public static final int PROCESS_MSG = 4;
	public static final int DB_ADD_MSG = 5;
	public static final int DB_DELETE_MSG = 6;
	public static final int DB_UPDATE_MSG = 7;

	// private Context mContext;
	private File localFile;
	private int download_status = INIT;
	private DownloadPackageCallBack mCallBack;
	private BreakPointDb breakPointDb;

	//
	private BreakPoint breakPoint;
	// private List<ThreadInfo> infoList;
	private long fileSize;
	private long current_complete;
	// 4.0以上系统，网络请求不能在主线程中，会报android.os.NetworkOnMainThreadException；；接口回调也用handler
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (mCallBack != null) {
				switch (msg.what) {
				case SUCCESS_MSG:
					CommonApplication.notityDwonload(breakPoint.getId(), 0);
					String zipName = ModernMediaTools
							.getPackageFileName(breakPoint.getUrl());
					final String foldName = ModernMediaTools
							.getPackageFolderName(breakPoint.getUrl());
					String zipPath = FileManager.getPackageNameByUrl(zipName);
					String targetDir = FileManager
							.getPackageNameByUrl(foldName) + "/";
					File file = new File(targetDir);
					if (!file.exists()) {
						// FileManager.deletePackageFold(file);
						file.mkdir();
						new ZipUtil().Unzip(zipPath, targetDir,
								new UnZipCallBack() {

									@Override
									public void callBack(boolean success) {
										if (success) {
											// 解压成功，删除zip包
											FileManager
													.deletePackageByName(breakPoint
															.getUrl());
											mCallBack.onSuccess(
													breakPoint.getId(),
													foldName);
										} else {
											mCallBack.onFailed(breakPoint
													.getId());
										}
									}
								});
					} else {
						// 解压成功，删除zip包
						FileManager.deletePackageByName(breakPoint.getUrl());
						mCallBack.onSuccess(breakPoint.getId(), foldName);
					}
					reset();
					break;
				case FAILED_MSG:
					mCallBack.onFailed(breakPoint.getId());
					CommonApplication.notityDwonload(breakPoint.getId(), 2);
					reset();
					break;
				case DB_ADD_MSG:
					// breakPointDb.addThreadInfoList(infoList);
					BreakPoint bp = breakPoint;
					bp.setTotal(fileSize);
					bp.setComplete(current_complete);
					breakPointDb.addThreadInfoList(bp);
					break;
				case DB_DELETE_MSG:
					// breakPointDb.deleteBreakPoint(ModernMediaTools
					// .getPackageFileName(breakPoint.getUrl()));
					break;
				case DB_UPDATE_MSG:
					breakPointDb.updateComplete(breakPoint.getUrl(),
							current_complete + "");
					break;
				default:
					break;
				}
			}
		}

	};

	public BreakPointUtil(Context context, DownloadPackageCallBack callBack) {
		breakPointDb = BreakPointDb.getInstance(context);
		mCallBack = callBack;
	}

	/**
	 * 当未下载过时，获取文件大小
	 * 
	 * @param packageUrl
	 */
	private void init() {
		new Thread() {

			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(breakPoint.getUrl());
					connection = (HttpURLConnection) url.openConnection();
					connection.setConnectTimeout(ConstData.CONNECT_TIMEOUT);
					connection.setReadTimeout(ConstData.READ_TIMEOUT);
					connection.setRequestMethod("GET");
					fileSize = connection.getContentLength();
					// long range = fileSize / THEAD_COUNT;
					// infoList = new ArrayList<ThreadInfo>();
					// for (int i = 0; i < THEAD_COUNT; i++) {
					// ThreadInfo info = new ThreadInfo();
					// info.setThreadId(i);
					// info.setIssueId(breakPoint.getId());
					// info.setStartPos(i * range);
					// if (i == THEAD_COUNT - 1) {
					// info.setEndPos(fileSize - 1);
					// } else {
					// info.setEndPos((i + 1) * range - 1);
					// }
					// info.setUrl(ModernMediaTools
					// .getPackageFileName(breakPoint.getUrl()));
					// info.setComplete(0);
					// info.setTotal(info.getEndPos() - info.getStartPos() + 1);
					// infoList.add(info);
					// current_complete = 0;
					// }
					// handler.sendEmptyMessage(DB_ADD_MSG);
					handler.post(new Runnable() {

						@Override
						public void run() {
							startDownload();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					// TODO init error show toast
					handler.sendEmptyMessage(FAILED_MSG);
				} finally {
					if (connection != null)
						connection.disconnect();
				}
			}

		}.start();
	}

	public void downLoad(BreakPoint breakPoint) {
		if (breakPoint != null) {
			this.breakPoint = breakPoint;
			localFile = FileManager.getPackageByName(breakPoint.getUrl());
			switch (breakPoint.getStatus()) {
			case NONE:
				firstDown();
				break;
			case BREAK:
				downByBreak();
				break;
			case DONE:
				System.out.println("done");
				handler.sendEmptyMessage(SUCCESS_MSG);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 第一次下载 (可能是数据库的信息被手动清除掉了，这样的话所有关于这条下载的记录都没有了，所以直接把本地的文件删除，重新下载)
	 */
	private void firstDown() {
		handler.sendEmptyMessage(DB_DELETE_MSG);
		System.out.println("none");
		init();
	}

	/**
	 * 从断点处开始下载
	 */
	private void downByBreak() {
		// infoList = breakPointDb.getThreadInfoList(ModernMediaTools
		// .getPackageFileName(breakPoint.getUrl()));
		// if (ParseUtil.listNotNull(infoList)) {
		// for (ThreadInfo info : infoList) {
		// current_complete += info.getComplete();
		// fileSize += info.getTotal();
		// }
		// System.out.println("break");
		// startDownload();
		// } else {
		// firstDown();
		// }
		init();
	}

	private void startDownload() {
		// if (ParseUtil.listNotNull(infoList)) {
		if (download_status == DOWNLOADING) {
			return;
		}
		download_status = DOWNLOADING;
		// for (ThreadInfo info : infoList) {
		// if (info.getTotal() <= 0 || info.getEndPos() <= 0
		// || fileSize == 0) {
		// firstDown();
		// break;
		// } else {
		// DownAsyncTask task = new DownAsyncTask();
		// task.execute(info);
		// }
		// }
		// }
		String zipName = ModernMediaTools.getPackageFileName(breakPoint
				.getUrl());
		String zipPath = FileManager.getPackageNameByUrl(zipName);
		File file = new File(zipPath);
		current_complete = file.length();
		handler.sendEmptyMessage(DB_ADD_MSG);
		DownAsyncTask task = new DownAsyncTask();
		task.execute(current_complete);
	}

	// 暂停下载
	public void pause() {
		if (mCallBack != null && breakPoint != null)
			mCallBack.onPause(breakPoint.getId());
		download_status = PAUSE;
	}

	// 恢复下载
	public void reStart() {
		CommonApplication.addPreIssueDown(breakPoint.getId(), this);
		CommonApplication.notityDwonload(breakPoint.getId(), 1);
		startDownload();
		download_status = INIT;
	}

	// 重置下载状态
	public void reset() {
		download_status = INIT;
	}

	public int getDownloadStatus() {
		return download_status;
	}

	private class DownAsyncTask extends AsyncTask<Long, Void, Boolean> {
		private long complete;

		@Override
		protected Boolean doInBackground(Long... params) {
			complete = params[0];
			if (downZip()) {
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				handler.sendEmptyMessage(SUCCESS_MSG);
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			mCallBack.onProcess(breakPoint.getId(), current_complete, fileSize);
			// breakPointDb.updateComplete(info.getUrl(), info.getThreadId(),
			// info.getComplete() + "");
			CommonApplication.notifyDown(breakPoint.getId(), current_complete,
					fileSize);
		}

		private boolean downZip() {
			HttpURLConnection connection = null;
			RandomAccessFile randomAccessFile = null;
			InputStream is = null;
			try {
				URL url = new URL(breakPoint.getUrl());
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(ConstData.CONNECT_TIMEOUT);
				connection.setReadTimeout(ConstData.READ_TIMEOUT);
				connection.setRequestMethod("GET");
				// start-end
				// String range = (info.getStartPos() + info.getComplete()) +
				// "-"
				// + info.getEndPos();
				String range = complete + "-";
				connection.setRequestProperty("RANGE", "bytes=" + range);
				randomAccessFile = new RandomAccessFile(localFile, "rwd");
				randomAccessFile.seek(complete);
				// randomAccessFile.seek(info.getStartPos() +
				// info.getComplete());
				// randomAccessFile.setLength(fileSize);

				is = connection.getInputStream();
				byte[] buffer = new byte[4096];
				int length = -1;
				while ((length = is.read(buffer)) != -1) {
					randomAccessFile.write(buffer, 0, length);
					// info.setComplete(info.getComplete() + length);

					current_complete += length;
					// updateDb(info);

					// TODO show progress
					// 实时刷新会卡死
					// handler.sendEmptyMessageDelayed(PROCESS_MSG, 300);
					publishProgress();
					if (download_status == PAUSE) {
						break;
					}
				}
				current_complete = current_complete >= fileSize ? fileSize
						: current_complete;
				handler.sendEmptyMessageDelayed(DB_UPDATE_MSG, 100);
				if (current_complete == fileSize) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				handler.sendEmptyMessage(FAILED_MSG);
				handler.sendEmptyMessageDelayed(DB_UPDATE_MSG, 100);
			} finally {
				try {
					if (is != null)
						is.close();
					if (randomAccessFile != null)
						randomAccessFile.close();
					if (connection != null)
						connection.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return false;
		}

	}

}
