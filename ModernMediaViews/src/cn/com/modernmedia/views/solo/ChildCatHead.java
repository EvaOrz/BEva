package cn.com.modernmedia.views.solo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.index.IndexViewPagerItem;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.UserApplication;
import cn.com.modernmediausermodel.listener.AfterLoginListener;
import cn.com.modernmediausermodel.util.UserPageTransfer;

/**
 * 独立栏目/子栏目导航栏
 * 
 * @author zhuqiao
 * 
 */
public class ChildCatHead extends BaseChildCatHead {
	private HorizontalScrollView view;
	private List<XMLParse> parseList = new ArrayList<XMLParse>();

	public ChildCatHead(Context context, IndexViewPagerItem indexViewPagerItem,
			Template template) {
		super(context, indexViewPagerItem, template);
		init();
	}

	private void init() {
		frame = new LinearLayout(mContext);
		((LinearLayout) frame).setOrientation(LinearLayout.HORIZONTAL);
		view = new HorizontalScrollView(mContext);
		view.setHorizontalScrollBarEnabled(false);
		view.setVerticalScrollBarEnabled(false);
		view.addView(frame, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		String bg = template.getCatHead().getColor();
		if (!TextUtils.isEmpty(bg) && bg.startsWith("#")) {
			view.setBackgroundColor(Color.parseColor(bg));
		} else {
			view.setBackgroundColor(Color.WHITE);
		}
		setView(view);
	}

	@Override
	protected void initToolBar(final List<TagInfo> list, String currTag) {
		frame.removeAllViews();
		parseList.clear();
		if (!ParseUtil.listNotNull(list))
			return;
		for (int i = 0; i < list.size(); i++) {
			final TagInfo tagInfo = list.get(i);
			final int position = i;
			XMLParse parse = new XMLParse(mContext, null);
			final View child = parse.inflate(template.getCatHead().getData(),
					null, template.getHost());
			parse.setDataForCatHead(tagInfo, position, list.size());
			child.setTag(tagInfo);
			child.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					clickItem(tagInfo, position);
				}
			});
			frame.addView(child);
			parseList.add(parse);
		}
		if (AppValue.appInfo.getHaveSubscribe() == 1
				&& SlateApplication.mConfig.getHas_subscribe() == 1) {
			XMLParse parse = new XMLParse(mContext, null);
			View child = parse.inflate(template.getCatHead().getData(), null,
					template.getHost());
			TagInfo tagInfo = new TagInfo();
			tagInfo.setAdapter_id(3);
			parse.setDataForCatHead(tagInfo, 0, list.size());
			child.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (SlateDataHelper.getUserLoginInfo(mContext) == null) {
						UserApplication.afterLoginListener = new AfterLoginListener() {

							@Override
							public void doAfterLogin(int flag) {
								if (flag == ViewsMainActivity.SELECT_CHILD_COLUMN_LOGIN_REQUEST_CODE) {
									gotoSelectChildColumnActvity(list);
								}
								UserApplication.afterLoginListener = null;
							}
						};
						UserPageTransfer
								.gotoLoginActivity(
										mContext,
										ViewsMainActivity.SELECT_CHILD_COLUMN_LOGIN_REQUEST_CODE);
					} else {
						((ViewsMainActivity) mContext)
								.gotoSelectChildColumnActvity(list.get(0)
										.getParent());
					}
				}
			});
			frame.addView(child);
		}
		super.initToolBar(list, currTag);
	}

	private void gotoSelectChildColumnActvity(List<TagInfo> list) {
		SlateApplication.loginStatusChange = false;
		((ViewsMainActivity) mContext).getUserSubscript(
				list.get(0).getParent(),
				ViewsMainActivity.SELECT_CHILD_COLUMN_LOGIN_REQUEST_CODE);
	}

	@Override
	public void setStatusBySelect(View child, boolean select, int index) {
		if (ParseUtil.listNotNull(parseList)) {
			for (int i = 0; i < parseList.size(); i++) {
				XMLParse parse = parseList.get(i);
				parse.setDataForCatHead(i == index);
			}
		}
	}

	/**
	 * 隐藏headview
	 */
	public void hideHead() {
		frame.removeAllViews();
	}

	/**
	 * 显示headview
	 */
	public void showHead() {
	}
}
