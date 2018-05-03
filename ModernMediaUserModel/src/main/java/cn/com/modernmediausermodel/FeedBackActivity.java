package cn.com.modernmediausermodel;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.api.FeedBackOperate.FeedBackResult;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 意见反馈
 * 
 * @author Eva.
 * 
 */
public class FeedBackActivity extends SlateBaseActivity implements
		OnClickListener {
	private EditText name, contact, subject;
	private ImageView imageView1, imageView2, imageView3;
	private TextView text1, text2, text3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_back);
		initView();
	}

	private void initView() {

		name = (EditText) findViewById(R.id.feedback_name);
		contact = (EditText) findViewById(R.id.feedback_contect);
		subject = (EditText) findViewById(R.id.feedback_subject);
		findViewById(R.id.feedback_cancle).setOnClickListener(this);
		findViewById(R.id.feedback_send).setOnClickListener(this);
		text1 = (TextView) findViewById(R.id.feed_add_text_1);
		text2 = (TextView) findViewById(R.id.feed_add_text_2);
		text3 = (TextView) findViewById(R.id.feed_add_text_3);

		imageView1 = (ImageView) findViewById(R.id.feed_add_pic_1);
		findViewById(R.id.feed_add_frame_1).setOnClickListener(this);
		imageView2 = (ImageView) findViewById(R.id.feed_add_pic_2);
		findViewById(R.id.feed_add_frame_2).setOnClickListener(this);
		imageView3 = (ImageView) findViewById(R.id.feed_add_pic_3);
		findViewById(R.id.feed_add_frame_3).setOnClickListener(this);

	}

	@Override
	public String getActivityName() {
		return FeedBackActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.feedback_send) {
			String sub = subject.getEditableText().toString();
			String na = name.getEditableText().toString();
			String con = contact.getEditableText().toString();

			if (!TextUtils.isEmpty(na) && !TextUtils.isEmpty(sub)) {
				showLoadingDialog(true);
				if (UserTools.checkIsEmailOrPhone(FeedBackActivity.this, con)) {

					UserOperateController.getInstance(FeedBackActivity.this)
							.feedBack(FeedBackActivity.this, sub, na, con,
									getImgs(), new UserFetchEntryListener() {

										@Override
										public void setData(Entry entry) {
											showLoadingDialog(false);
											if (entry instanceof FeedBackResult) {
												FeedBackResult backResult = (FeedBackResult) entry;
												if (backResult.getState() == 0) {
													showToast(backResult
															.getDesc());
													finish();
												} else {
													showToast(backResult
															.getDesc());
												}
											}

										}
									});
				} else
					showToast(R.string.feed_contact_error);// 账号格式错误
			}

		} else if (v.getId() == R.id.feedback_cancle) {
			finish();
		} else if (v.getId() == R.id.feed_add_frame_1) {
			uploadPic(1);
		} else if (v.getId() == R.id.feed_add_frame_2) {
			uploadPic(2);
		} else if (v.getId() == R.id.feed_add_frame_3) {
			uploadPic(3);
		}
	}

	private List<String> getImgs() {
		List<String> img = new ArrayList<String>();
		if (imageView1.getTag() != null && imageView1.getTag() instanceof Uri) {
			img.add(getImageFilePath((Uri) imageView1.getTag()));
		}
		if (imageView2.getTag() != null && imageView2.getTag() instanceof Uri) {
			img.add(getImageFilePath((Uri) imageView2.getTag()));
		}
		if (imageView3.getTag() != null && imageView3.getTag() instanceof Uri) {
			img.add(getImageFilePath((Uri) imageView3.getTag()));
		}
		return img;
	}

	private String getImageFilePath(Uri uri) {
		ContentResolver cr = this.getContentResolver();
		Cursor c = cr.query(uri, null, null, null, null);
		c.moveToFirst();
		// 这是获取的图片保存在sdcard中的位置
		String srcPath = c.getString(c.getColumnIndex("_data"));
		return srcPath;
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
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setTag(uri);
		iv.setImageURI(uri);
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
