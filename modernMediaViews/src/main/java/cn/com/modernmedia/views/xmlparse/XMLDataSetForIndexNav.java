package cn.com.modernmedia.views.xmlparse;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.column.book.TopMenuHorizontalScrollView;
import cn.com.modernmedia.views.index.IndexView;
import cn.com.modernmediaslate.SlateApplication;

/**
 * 首页导航栏设置数据
 * 
 * @author jiancong
 * 
 */
public class XMLDataSetForIndexNav extends BaseXMLDataSet {
	private View navBar, navBg;

	public XMLDataSetForIndexNav(Context context, HashMap<String, View> map,
			List<View> clickViewList, List<View> ninePatchViewList) {
		super(context, map, clickViewList, ninePatchViewList);
	}

	public void setData() {
		showColumnMask();
		registerClick(null, null);
	}

	/**
	 * 取得左边栏目列表功能按钮view
	 * 
	 * @return
	 */
	public View getColumn() {
		return map.containsKey(FunctionIndexNav.COLUMN) ? map
				.get(FunctionIndexNav.COLUMN) : new View(mContext);
	}

	/**
	 * 取得右边收藏或用户中心功能按钮view
	 * 
	 * @return
	 */
	public View getFav() {
		return map.containsKey(FunctionIndexNav.FAV_OR_USER) ? map
				.get(FunctionIndexNav.FAV_OR_USER) : new View(mContext);
	}

	/**
	 * 设置导航栏分割线的透明度(艺术新闻效果)
	 * 
	 * @param alpha
	 */
	@SuppressLint("NewApi")
	public void setAlpha(int alpha) {
		if (!map.containsKey(FunctionIndexNav.DIVIDER))
			return;
		View view = map.get(FunctionIndexNav.DIVIDER);
		view.setAlpha(alpha);
	}

	/**
	 * 设置标题
	 * 
	 * @param text
	 */
	public void setTitle(String text) {
		if (!map.containsKey(FunctionXML.TITLE))
			return;
		View view = map.get(FunctionXML.TITLE);
		if (!(view instanceof TextView))
			return;
		TextView title = (TextView) view;
		title.setText(text);

		if (!map.containsKey(FunctionIndexNav.NAV_LOGO))
			return;
		View v = map.get(FunctionIndexNav.NAV_LOGO);
		if (!(v instanceof ImageView))
			return;
		ImageView logo = (ImageView) v;
		if (text.equals("好消息") || text.equals("Good News")) {

			title.setVisibility(View.GONE);
			logo.setVisibility(View.VISIBLE);
		} else {
			title.setVisibility(View.VISIBLE);
			logo.setVisibility(View.GONE);
		}
	}

	private boolean navShow = true, maskShow = true;

	/**
	 * 通知导航栏移动
	 * 
	 * @param padding
	 */
	public void callNavPadding(int padding) {
		if (padding <= 0 && SlateApplication.mConfig.getNav_hide() == 1) {
			View navBar = getNavBar();
			View navBg = getNavBg();
			if (navBar == null || navBg == null)
				return;
			navBar.setPadding(0, padding, 0, 0);
			IndexView.height = IndexView.BAR_HEIGHT + padding;
			IndexView.height = IndexView.height < 0 ? 0 : IndexView.height;
			navBg.getLayoutParams().height = IndexView.height;
			if (navShow && IndexView.height == 0) {
				navShow = false;
				showColumnMask();
			} else if (IndexView.height > 0) {
				navShow = true;
				showColumnMask();
			}
		}
	}

	private void showColumnMask() {
		if (!map.containsKey(FunctionIndexNav.COLUMN_MASK)) {
			return;
		}
		View columnMask = map.get(FunctionIndexNav.COLUMN_MASK);
		if (!navShow && !maskShow) {
			maskShow = true;
			if (columnMask.getVisibility() != View.VISIBLE)
				columnMask.setVisibility(View.VISIBLE);
			columnMask.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.up_in));
		} else if (navShow && maskShow) {
			maskShow = false;
			columnMask.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.up_out));
		}
	}

	public boolean isNavShow() {
		return navShow;
	}

	public View getNavBar() {
		if (navBar != null)
			return navBar;
		if (map.containsKey(FunctionIndexNav.NAV_BAR))
			return map.get(FunctionIndexNav.NAV_BAR);
		return null;
	}

	private View getNavBg() {
		if (navBg != null)
			return navBg;
		if (map.containsKey(FunctionIndexNav.NAV_BG))
			return map.get(FunctionIndexNav.NAV_BG);
		return null;
	}

	@Override
	protected void onClick(View view, ArticleItem item, ArticleType articleType) {
		if (!(view.getTag(R.id.click) instanceof String))
			return;
		if (!(mContext instanceof ViewsMainActivity))
			return;
		String click = view.getTag(R.id.click).toString();
		if (click.equals(FunctionIndexNav.COLUMN)
				|| click.equals(FunctionIndexNav.COLUMN_MASK)) {
			((ViewsMainActivity) mContext).getScrollView().clickButton(true);
		} else if (click.equals(FunctionIndexNav.FAV_OR_USER)) {
			((ViewsMainActivity) mContext).getScrollView().clickButton(false);
		} else if (click.equals(FunctionIndexNav.ISSUE_LIST)) {
			((ViewsMainActivity) mContext).showIssueListView();
		}
	}

	/**
	 * 设置top_menu
	 */
	public void setTopMenuData(TopMenuHorizontalScrollView top) {
		if (!map.containsKey(FunctionIndexNav.TOP_MENU))
			return;
		View top_menu = map.get(FunctionIndexNav.TOP_MENU);
		if (!(top_menu instanceof LinearLayout))
			return;
		((LinearLayout) top_menu).removeAllViews();
		((LinearLayout) top_menu).addView(top);
	}
	

}
