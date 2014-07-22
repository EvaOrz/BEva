package cn.com.modernmedia;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.FileManager;

/**
 * 广告页面
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CommonAdvActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adv);
		Bitmap bitmap = FileManager
				.getImageFromFile(DataHelper.getAdvPic(this));
		((ImageView) findViewById(R.id.adv_image)).setImageBitmap(bitmap);
		gotoMainActivity();
	}

	/**
	 * 显示完入版广告后进入首页
	 */
	protected abstract void gotoMainActivity();

	@Override
	public void reLoadData() {

	}

}
