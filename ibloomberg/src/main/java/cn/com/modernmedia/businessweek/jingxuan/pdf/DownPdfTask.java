package cn.com.modernmedia.businessweek.jingxuan.pdf;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.unit.MD5;

/**
 * Created by Eva. on 2017/11/21.
 */

public class DownPdfTask extends AsyncTask<String, Integer, String> {
    private Context context;
    private PowerManager.WakeLock mWakeLock;
    private String url;
private DownLoadPdfLitener downLoadPdfLitener;

    public interface DownLoadPdfLitener {
        public void onProgressUpdate(int p);
    }

    public DownPdfTask(Context context,DownLoadPdfLitener downLoadPdfLitener) {
        this.context = context;
        this.downLoadPdfLitener = downLoadPdfLitener;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        url = sUrl[0];
        String path = Environment.getExternalStorageDirectory().getPath() + ConstData.DEFAULT_PDF_PATH;
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(path + MD5.MD5Encode(url) + ".dzs");

        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
            }
            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(file);
            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null) output.close();
                if (input != null) input.close();
                publishProgress(100);
            } catch (IOException ignored) {
            }
            if (connection != null) connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWakeLock.acquire();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        downLoadPdfLitener.onProgressUpdate(progress[0]);

    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        //            if (result != null) Log.e("Download error: ", result);
        //            else Log.e("File downloaded", result);
    }


}