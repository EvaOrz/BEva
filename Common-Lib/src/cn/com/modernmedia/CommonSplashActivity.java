package cn.com.modernmedia;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.Down;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.GenericConstant;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Entry;

/**
 * 进版首页
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CommonSplashActivity extends BaseActivity {
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	protected void init() {
		setContentViewById();
		mContext = this;
		CommonApplication.clear();
		down();
		if (ConstData.getInitialAppId() == 20) {
			// TODO iweekly
			checkHasAdv(false, null, null, null);
		} else {
			getAdvList();
		}
	}

	protected abstract void setContentViewById();

	protected void gotoMainActivity() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(mContext, getMainActivity());
				intent.putExtra(GenericConstant.FROM_ACTIVITY,
						GenericConstant.FROM_ACTIVITY_VALUE);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.alpha_out, R.anim.hold);
			}
		}, ConstData.SPLASH_DELAY_TIME);
	};

	protected void gotoAdvActivity(final ArrayList<String> picList,
			final String effects, final String impressionUrl) {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(mContext, getAdvActivity());
				intent.putExtra(GenericConstant.FROM_ACTIVITY,
						GenericConstant.FROM_ACTIVITY_VALUE);
				intent.putExtra(GenericConstant.PIC_LIST, picList);
				intent.putExtra(GenericConstant.EFFECTS, effects);
				intent.putExtra(GenericConstant.IMPRESSION_URL, impressionUrl);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.alpha_out, R.anim.hold);
			}
		}, ConstData.SPLASH_DELAY_TIME);
	};

	/**
	 * 统计装机量
	 */
	private void down() {
		if (DataHelper.getDown(this))
			return;
		OperateController.getInstance(this).getDown(new FetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				if (entry instanceof Down) {
					Down down = (Down) entry;
					if (down.isSuccess()) {
						DataHelper.setDown(mContext);
					}
				}
			}
		});
	}

	private void init(AdvList advList) {
		if (advList == null || advList.getAdvMap().isEmpty()
				|| !advList.getAdvMap().containsKey(AdvList.RU_BAN)) {
			checkHasAdv(false, null, null, null);
		} else {
			ArrayList<String> picList = new ArrayList<String>();
			List<AdvItem> list = advList.getAdvMap().get(AdvList.RU_BAN);
			String effects = "";
			String impressionUrl = "";
			if (ParseUtil.listNotNull(list)) {
				for (AdvItem item : list) {
					// TODO 广告属于当前期并且广告没有过期,不考虑issueId
					if (!AdvTools.advIsExpired(item.getStartTime(),
							item.getEndTime())) {
						for (AdvSource pic : item.getSourceList()) {
							// TODO 当所有图片都下载成功时，才进入入版广告页
							if (FileManager.getImageFromFile(pic.getUrl()) == null) {
								continue;
							}
							picList.add(pic.getUrl());
						}
						effects = item.getEffects();
						impressionUrl = item.getTracker().getImpressionUrl();
					}
				}
			}
			checkHasAdv(!picList.isEmpty(), picList, effects, impressionUrl);
		}
	}

	protected void checkHasAdv(boolean hasAdv, ArrayList<String> picList,
			String effects, String impressionUrl) {
		if (hasAdv && ParseUtil.listNotNull(picList))
			gotoAdvActivity(picList, effects, impressionUrl);
		else
			gotoMainActivity();
	}

	private void getAdvList() {
		OperateController.getInstance(mContext).getAdvList(true,
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof AdvList) {
							init((AdvList) entry);
						} else
							init(null);
					}
				});
	}

	protected abstract Class<?> getMainActivity();

	protected abstract Class<?> getAdvActivity();

	@Override
	public void reLoadData() {
	}

}
