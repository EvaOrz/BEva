package cn.com.modernmedia;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.utils.UrlSafeBase64;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import cn.com.modernmedia.util.BitmapUtil;

/**
 * 七牛上传图片
 *
 * @author: zhufei
 */
public class UploadPicsActivity extends BaseActivity implements View.OnClickListener {
    private String uptoken;
    private ImageView imageView1, imageView2, imageView3;
    private TextView text1, text2, text3;
    private UploadManager uploadManager;
    private static final String BUCKET = "uploadpictures";
    private static final long USER_TIME = 3600;
    private static final String SECRET_KEY = "pmTEmVSI2bCHHAAPWuki_c16hYWSIGbWgj6y5xVc";
    private static final String ACCESS_KEY = "uVoRLd7-UPAOm6IGX-AXF0I1PAAXofp_O7yYbZ_1";
    private List<String> url_list;
    private List<String> uploadPath = new ArrayList<String>();
    private String domain = "http://obzurs68x.bkt.clouddn.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadpics);
        text1 = (TextView) findViewById(R.id.upload_add_text_1);
        text2 = (TextView) findViewById(R.id.upload_add_text_2);
        text3 = (TextView) findViewById(R.id.upload_add_text_3);

        imageView1 = (ImageView) findViewById(R.id.upload_add_pic_1);
        findViewById(R.id.upload_add_frame_1).setOnClickListener(this);
        imageView2 = (ImageView) findViewById(R.id.upload_add_pic_2);
        findViewById(R.id.upload_add_frame_2).setOnClickListener(this);
        imageView3 = (ImageView) findViewById(R.id.upload_add_pic_3);
        findViewById(R.id.upload_add_frame_3).setOnClickListener(this);
        findViewById(R.id.upload_send).setOnClickListener(this);
        findViewById(R.id.upload_cancle).setOnClickListener(this);
        //new一个uploadManager类
        uploadManager = new UploadManager();
        uptoken = getUploadToken(USER_TIME, BUCKET);

    }

    /**
     * 获取上传文件的token
     *
     * @param usefulTime token有效时间（单位：秒）
     * @param bucket     上传的空间
     */
    public static String getUploadToken(long usefulTime, String bucket) {
        //构造上传策略
        JSONObject json = new JSONObject();
        long deadline = System.currentTimeMillis() + usefulTime;
        String uploadToken = null;
        try {
            // 有效时间为一个小时
            json.put("deadline", deadline);
            json.put("scope", bucket);
            String encodedPutPolicy = UrlSafeBase64.encodeToString(json.toString().getBytes());
            byte[] sign = hMacSHA1Encrypt(encodedPutPolicy, SECRET_KEY);
            String encodedSign = UrlSafeBase64.encodeToString(sign);
            uploadToken = ACCESS_KEY + ':' + encodedSign + ':' + encodedPutPolicy;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uploadToken;
    }

    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";

    /**
     * 使用 HMAC-SHA1 签名方法对对encryptText进行签名
     *
     * @param encryptText 被签名的字符串
     * @param encryptKey  密钥
     * @return
     * @throws Exception
     */
    public static byte[] hMacSHA1Encrypt(String encryptText, String encryptKey)
            throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        // 生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance(MAC_NAME);
        // 用给定密钥初始化 Mac 对象
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        // 完成 Mac 操作
        return mac.doFinal(text);
    }


    @Override
    public void onClick(View v) {
        Log.i("qiniutest", "starting......");
        url_list = new ArrayList<String>();
        if (v.getId() == R.id.upload_send) {//上传七牛服务器
            if (uploadPath.size() > 0) {
                showLoadingDialog(true);
                for (int i = 0; i < uploadPath.size(); i++) {
                    final int index = i;
                    String expectKey = UUID.randomUUID().toString();
                    uploadManager.put(uploadPath.get(i), expectKey, uptoken, new UpCompletionHandler() {
                        public void complete(String k, ResponseInfo rinfo, JSONObject response) {
                            String s = k + ", " + rinfo + ", " + response;
                            Log.i("qiniutest", s);
                            if (index == uploadPath.size() - 1)
                                showLoadingDialog(false);
                            if (rinfo.isOK()) {
                                showToast(getString(R.string.upload_num) + (index + 1) + getString(R.string.upload_size) + getString(R.string.upload_success));
                                String urls = getFileUrl(domain, k);
                                Log.i("qiniutest", urls);
                                url_list.add(urls);
                            } else {
                                showToast(getString(R.string.upload_num) + (index + 1) + getString(R.string.upload_size) + getString(R.string.upload_failed));
                            }
                        }
                    }, new UploadOptions(null, "jpeg", true, null, null));
                }
            } else {
                showToast(R.string.upload_null);
            }
        } else if (v.getId() == R.id.upload_cancle) {
            finish();
        } else if (v.getId() == R.id.upload_add_frame_1) {
            uploadPic(1);
        } else if (v.getId() == R.id.upload_add_frame_2) {
            uploadPic(2);
        } else if (v.getId() == R.id.upload_add_frame_3) {
            uploadPic(3);
        }
    }

    /**
     * 通过key获取上传的资源文件的全路径
     *
     * @param key
     * @return
     */
    public static String getFileUrl(String domain, String key) {
        StringBuilder sb = new StringBuilder();
        sb.append(domain);
        try {
            //1:构造URL
            String encode = URLEncoder.encode(key, "UTF-8");
            sb.append(encode);
            //2:为url加上过期时间  unix时间
            sb.append("?e=" + (System.currentTimeMillis() / 1000 + USER_TIME));
            //3:对1 2 操作后的url进行hmac-sha1签名 secrect
            String s = sb.toString();
            byte[] bytes = hMacSHA1Encrypt(s, SECRET_KEY);
            String sign = UrlSafeBase64.encodeToString(bytes);
            //4:将accsesskey 连接起来
            sb.append("&token=" + ACCESS_KEY + ":" + sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    text1.setVisibility(View.GONE);
                    showPic(imageView1, (Uri) msg.obj);
                    break;
                case 2:
                    text2.setVisibility(View.GONE);
                    showPic(imageView2, (Uri) msg.obj);
                    break;
                case 3:
                    text3.setVisibility(View.GONE);
                    showPic(imageView3, (Uri) msg.obj);
                    break;
            }
        }
    };

    private void showPic(ImageView iv, Uri uri) {
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setTag(uri);
        File file = getFileFromMediaUri(this, uri);
        Bitmap bitmap = compressImage(file.getPath());
        if (bitmap != null)
            iv.setImageBitmap(bitmap);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.e("获取图片uri", data.getData().toString());
            Message m = new Message();
            m.what = requestCode;
            m.obj = data.getData();
            handler.sendMessage(m);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 压缩图片
    public Bitmap compressImage(String filePath) {
        String targetPath = Environment.getExternalStorageDirectory()
                + "/compressImage/";

        Bitmap bm = BitmapUtil.getPhoto(filePath, 480, 800);

        File outputFile = new File(targetPath);
        if (!outputFile.exists()) {
            outputFile.mkdir();
        }
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        String path = outputFile + "/" + fileName;
        try {
            FileOutputStream out = new FileOutputStream(path);
            int count = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            while (baos.toByteArray().length / 1024 > 500 && count < 10) {
                // 质量压缩
                // bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
                baos.reset();
                // 这里压缩options%，把压缩后的数据存放到baos中
                count++;
                bm.compress(Bitmap.CompressFormat.JPEG, 100 - count * 10, baos);
            }
            baos.writeTo(out);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        uploadPath.add(path);
        return bm;
    }

    /**
     * 通过Uri获取文件
     *
     * @param ac
     * @param uri
     * @return
     */
    public static File getFileFromMediaUri(Context ac, Uri uri) {
        if (uri.getScheme().toString().compareTo("content") == 0) {
            ContentResolver cr = ac.getContentResolver();
            Cursor cursor = cr.query(uri, null, null, null, null);// 根据Uri从数据库中找
            if (cursor != null) {
                cursor.moveToFirst();
                String filePath = cursor.getString(cursor.getColumnIndex("_data"));// 获取图片路径
                cursor.close();
                if (filePath != null) {
                    return new File(filePath);
                }
            }
        } else if (uri.getScheme().toString().compareTo("file") == 0) {
            return new File(uri.toString().replace("file://", ""));
        }
        return null;
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    @Override
    public void reLoadData() {

    }


    @Override
    public String getActivityName() {
        return UploadPicsActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return UploadPicsActivity.this;
    }

    /**
     * 上传图片
     */
    private void uploadPic(int flag) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, flag);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
