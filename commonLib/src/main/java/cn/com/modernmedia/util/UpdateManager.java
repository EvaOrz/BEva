package cn.com.modernmedia.util;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.BaseFragmentActivity;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.R;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.Version;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 版本升级
 *
 * @author ZhuQiao
 */
public class UpdateManager {
    private static final int DOWN_UPDATE = 10000;
    private static final int DOWN_OVER = 20000;
    public static final int CHECK_DOWN = 30000;

    private Context mContext;
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
                    showNoticeDialog();
                    break;
            }
        }

        ;
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
    }

    /**
     * 获取服务器数据
     */
    public void checkVersion() {
        OperateController.getInstance(mContext).checkVersion(Tools.getAppVersionName(mContext), new FetchEntryListener() {

            @Override
            public void setData(final Entry entry) {
                if (entry instanceof Version) {
                    version = (Version) entry;
                    /**
                     * 存储最新版本号
                     */
                    DataHelper.setLastVersion(mContext, version.getVersion());
                    compare();
                } else {
                    listener.checkEnd();
                }
            }
        });
    }

    /**
     * 获取服务器数据
     */
    public void checkVersion(Version version) {
        if (DataHelper.getRefuseNoticeVersion(mContext)) return;
        this.version = version;
        compare();
    }


    /**
     * 线上版本高于本地版本
     * <p>
     * 没有新版本把本地时间置空
     */
    private void compare() {
        if (version.getVersion() > Tools.getAppIntVersionName(mContext) && !TextUtils.isEmpty(version.getDownload_url())) {

            mHandler.sendEmptyMessage(CHECK_DOWN);
        } else {
            listener.checkEnd();
        }
    }

    /**
     * 显示更新dialog
     */
    private void showNoticeDialog() {
        Builder builder = new Builder(mContext);
        builder.setTitle(R.string.update);
        builder.setMessage(mContext.getString(R.string.app_name) + " " + version.getChangelog());
        builder.setPositiveButton(R.string.download, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                downNow();
            }
        });
        builder.setNegativeButton(R.string.download_later, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                DataHelper.setRefuseNoticeVersion(mContext, true);
                listener.checkEnd();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
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
                DataHelper.setRefuseNoticeVersion(mContext, true);
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
        apkName = new String(CommonApplication.mConfig.getCache_file_name() + version.getVersion() + ".apk");
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
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                Log.e("流量bug查询**", "UpdateManager:DownApkThread" + "-----" + apkUrl);
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
                    if (fos != null) fos.close();
                    if (is != null) is.close();
                    dissDialog();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = FileManager.getApkByName(apkName);
        if (!apkfile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
        mContext.startActivity(intent);

    }

    private void showToast() {
        if (mContext instanceof BaseActivity)
            ((BaseActivity) mContext).showToast(R.string.download_error);
        else if (mContext instanceof BaseFragmentActivity)
            ((BaseFragmentActivity) mContext).showToast(R.string.download_error);
    }
}
