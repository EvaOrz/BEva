package cn.com.modernmedia.zxing.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import cn.com.modernmedia.R;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmedia.zxing.camera.CameraManager;
import cn.com.modernmedia.zxing.camera.RGBLuminanceSource;
import cn.com.modernmedia.zxing.decoding.CaptureActivityHandler;
import cn.com.modernmedia.zxing.decoding.InactivityTimer;
import cn.com.modernmedia.zxing.util.Utils;
import cn.com.modernmedia.zxing.view.ViewfinderView;

/**
 * 扫一扫界面
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class CaptureActivity extends Activity implements Callback {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private TextView back;
    private TextView flash;
    private TextView photo;
    //闪光灯起始点击次数
    private int count = 2;
    private String photo_path;
    private Bitmap scanBitmap;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        //ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        back = (TextView) findViewById(R.id.back);
        flash = (TextView) findViewById(R.id.flash);
        photo = (TextView) findViewById(R.id.photo);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);


        /**
         * 扫一扫页面点击事件
         *
         * @author zhufei
         */
        back.setOnClickListener(new View.OnClickListener() {//返回
            @Override
            public void onClick(View v) {
                CaptureActivity.this.finish();
            }
        });
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if (count % 2 == 0) {
                    CameraManager.get().offLight();
                    flash.setBackgroundResource(R.drawable.shanguangd2x);
                } else {
                    CameraManager.get().openLight();
                    flash.setBackgroundResource(R.drawable.guanbishanguangd2x);
                }
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPickPhotoFromGallery();
            }
        });

    }

    //用来标识请求gallery的activity
    private static final int PHOTO_PICKED_WITH_DATA = 3021;


    // 请求Gallery程序
    protected void doPickPhotoFromGallery() {
        try {
            // Launch picker to choose photo for selected contact
            final Intent intent = getPhotoPickIntent();
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.no_photo, Toast.LENGTH_LONG).show();
        }
    }

    // 封装请求Gallery的intent
    public Intent getPhotoPickIntent() {
        Intent innerIntent = new Intent(); // "android.intent.action.GET_CONTENT"
        innerIntent.setAction(Intent.ACTION_GET_CONTENT);
        innerIntent.setType("image/*");
        Intent wrapperIntent = Intent.createChooser(innerIntent, CaptureActivity.this.getString(R.string.choose_qr_photo));
        Log.d("==", "0");
        return wrapperIntent;
    }

    /**
     * 返回扫描结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)

            return;
        if (requestCode == PHOTO_PICKED_WITH_DATA) {

            String[] proj = {MediaStore.Images.Media.DATA};
            // 获取选中图片的路径
            Cursor cursor = getContentResolver().query(data.getData(), proj, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                photo_path = cursor.getString(column_index);
                if (photo_path == null) {
                    photo_path = Utils.getPath(getApplicationContext(), data.getData());

                }
            }
            cursor.close();

            new Thread(new Runnable() {

                @Override
                public void run() {

                    Result result = scanningImage(photo_path);
                    if (result == null) {
                        Looper.prepare();
                        Toast.makeText(CaptureActivity.this, R.string.no_qrcode, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } else {

                        parseUri(result.toString());

                    }
                }
            }).start();


        }

    }

    //扫描图片
    protected Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {

            return null;

        }
        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小

        int sampleSize = (int) (options.outHeight / (float) 200);

        if (sampleSize <= 0) sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        //bitmap-->byte[]
        //        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //        scanBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

        int[] intArray = new int[scanBitmap.getWidth() * scanBitmap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        scanBitmap.getPixels(intArray, 0, scanBitmap.getWidth(), 0, 0, scanBitmap.getWidth(), scanBitmap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(scanBitmap.getWidth(), scanBitmap.getHeight(), intArray);

        //        PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(baos.toByteArray(),scanBitmap.getWidth(), scanBitmap.getHeight());
        //        LuminanceSource source=new PlanarYUVLuminanceSource(baos.toByteArray(),
        //                scanBitmap.getWidth(),scanBitmap.getHeight(),0,0,scanBitmap.getWidth(),scanBitmap.getHeight());
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {

            return reader.decode(bitmap1, hints);

        } catch (NotFoundException e) {

            e.printStackTrace();

        } catch (ChecksumException e) {
            e.printStackTrace();

        } catch (FormatException e) {

            e.printStackTrace();

        }

        return null;

    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        boolean isHttp = result.toString().toLowerCase().startsWith("http://");
        boolean isHttps = result.toString().toLowerCase().startsWith("https://");
        boolean isSlate = result.toString().toLowerCase().startsWith("slate://");
        //FIXME
        if (TextUtils.isEmpty(result.toString()) && result.equals("") && result.toString().length() < 1) {
            Toast.makeText(CaptureActivity.this, R.string.unkown_qrcode, Toast.LENGTH_SHORT).show();
        } else {
            Log.e("扫描结果", result.toString());
            parseUri(result.toString());
        }
        if (!TextUtils.isEmpty(result.toString()) && !isHttp && !isHttps && !isSlate) {
            Toast.makeText(CaptureActivity.this, R.string.unkown_qrcode, Toast.LENGTH_SHORT).show();
        }
        //        CaptureActivity.this.finish();

        //连续扫描
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        initCamera(surfaceHolder);

        if (handler != null)

            handler.restartPreviewAndDecode();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(CaptureActivity.this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };


    private void parseUri(String link) {
        if (link.startsWith("http://") || link.startsWith("https://")) {
            link = "slate://web/" + link;

        }
        //浏览器跳转
        ArticleItem item = new ArticleItem();
        item.setSlateLink(link);
        UriParse.clickSlate(CaptureActivity.this, new ArticleItem[]{item});
    }

}