package cn.com.modernmediausermodel;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.model.Goods.GoodsItem;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.widget.MyCoinView;

public class MyCoinActivity extends SlateBaseActivity {
	private MyCoinView myCoinView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		myCoinView = new MyCoinView(this);
		setContentView(myCoinView.fetchView());
		myCoinView.getBackBtn().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 标识已进入到金币页面
		UserDataHelper.saveIsFirstUseCoin(this, Tools.getUid(this), false);
	}

	/**
	 * 做兑换操作
	 * 
	 * @param goodsItem
	 *            商品信息
	 */
	public void doExchange(GoodsItem goodsItem) {
		myCoinView.doExchange(goodsItem);
	}

	@Override
	public String getActivityName() {
		return MyCoinActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

}
