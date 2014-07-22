package cn.com.modernmedia;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.FileManager;

/**
 * ���ҳ��
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
	 * ��ʾ�������������ҳ
	 */
	protected abstract void gotoMainActivity();

	@Override
	public void reLoadData() {

	}

}
