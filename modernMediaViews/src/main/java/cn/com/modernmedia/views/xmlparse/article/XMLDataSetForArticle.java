package cn.com.modernmedia.views.xmlparse.article;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.WeeklyLogEvent;
import cn.com.modernmedia.views.ArticleActivity;
import cn.com.modernmedia.views.PushArticleActivity;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.xmlparse.BaseXMLDataSet;
import cn.com.modernmedia.views.xmlparse.FunctionIndexNav;
import cn.com.modernmedia.views.xmlparse.FunctionXML;
import cn.com.modernmediausermodel.util.UserPageTransfer;

/**
 * 文章页设置数据
 * 
 * @author zhuqiao
 * 
 */
public class XMLDataSetForArticle extends BaseXMLDataSet {
	private View navBar;

	public XMLDataSetForArticle(Context context, HashMap<String, View> map,
			List<View> clickViewList, List<View> ninePatchViewList) {
		super(context, map, clickViewList, ninePatchViewList);
	}

	public void setData() {
		ninePatch();
		registerClick(null, null);
		afterSettingsFont();
		afterSettingsLine();
	}

	@Override
	protected void onClick(View view, ArticleItem item, ArticleType articleType) {
		if (!(view.getTag(R.id.click) instanceof String))
			return;
		if (!((mContext instanceof ArticleActivity) || (mContext instanceof PushArticleActivity)))
			return;
		String click = view.getTag(R.id.click).toString();
		if (click.equals(FunctionArticle.BACK)) {
			back();
		} else if (click.equals(FunctionXML.FAV)) {
			fav();
		} else if (click.equals(FunctionArticle.SHARE_IMG)) {
			share();
		} else if (click.equals(FunctionArticle.FONT_IMG)) {
			font();
		} else if (click.equals(FunctionArticle.FONT_MINUS)) {
			fontMinus(view);
		} else if (click.equals(FunctionArticle.FONT_PLUS)) {
			fontPlus(view);
		} else if (click.equals(FunctionArticle.SPACING_MINUS)) {
			spacingMinus(view);
		} else if (click.equals(FunctionArticle.SPACING_PLUS)) {
			spacingPlus(view);
		} else if (click.equals(FunctionArticle.ACRTICLE_CARD)) {
			articleCard();
		}
	}

	/**
	 * 获取导航栏
	 * 
	 * @return
	 */
	public View getNavBar() {
		if (navBar != null)
			return navBar;
		if (map.containsKey(FunctionIndexNav.NAV_BAR))
			return map.get(FunctionIndexNav.NAV_BAR);
		return null;
	}

	/**
	 * 返回
	 */
	protected void back() {
		((ArticleActivity) mContext).back();
	}

	/**
	 * 收藏
	 */
	private void fav() {
		((ArticleActivity) mContext).addFav();
		if (ConstData.getInitialAppId() == 20)
			WeeklyLogEvent.logAndroidAddFavouriteCount(mContext);
	}

	/**
	 * 收藏
	 */
	protected void share() {
		((ArticleActivity) mContext).showShare();
	}

	/**
	 * 字体
	 */
	protected void font() {
		if (isSettingBarExist()) { // 存在设置栏
			if (isShowingSettings()) {
				disSetting(300);
			} else {
				showSettings(300);
			}
		} else {
			((CommonArticleActivity) mContext).clickFont();
		}
	}

	/**
	 * 前往文章摘要与评论页面
	 */
	private void articleCard() {
		int articleId = ((CommonArticleActivity) mContext).getCurrArticleId();
		UserPageTransfer.gotoArticleCardActivity(mContext, articleId + "");
	}

	/**
	 * 改变收藏背景
	 * 
	 * @param isFaved
	 */
	public void changeFavBtn(boolean isFaved) {
		if (!map.containsKey(FunctionXML.FAV))
			return;
		View view = map.get(FunctionXML.FAV);
		if (!(view instanceof ImageView))
			return;
		if (isFaved) {
			if (view.getTag(R.id.select_bg) instanceof String) {
				V.setImage(view, view.getTag(R.id.select_bg).toString());
			}
		} else {
			if (view.getTag(R.id.unselect_bg) instanceof String) {
				V.setImage(view, view.getTag(R.id.unselect_bg).toString());
			}
		}
	}

	/**
	 * 是否隐藏字体
	 * 
	 * @param hide
	 */
	public void hideFont(boolean hide) {
		if (!map.containsKey(FunctionArticle.FONT_IMG))
			return;
		View view = map.get(FunctionArticle.FONT_IMG);
		if (!(view instanceof ImageView))
			return;
		if (hide) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 广告时是否隐藏导航栏功能按钮
	 * 
	 * @param hide
	 *            是否隐藏
	 * @param isPush
	 *            是否是推送文章
	 */
	public void hideIfAdv(boolean hide, boolean isPush) {
		Set<String> keySets = map.keySet();
		for (String key : keySets) {
			boolean inKeys = FunctionArticle.FONT_IMG.equals(key)
					|| FunctionXML.FAV.equals(key)
					|| FunctionArticle.ACRTICLE_CARD.equals(key);
			if (!isPush)
				inKeys = inKeys || FunctionArticle.SHARE_IMG.equals(key);
			if (inKeys) {
				View view = map.get(key);
				if (!(view instanceof View))
					continue;
				if (hide) {
					view.setVisibility(View.GONE);
				} else {
					view.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	/**
	 * 显示设置栏
	 * 
	 * @param duration
	 *            动画时间
	 */
	public void showSettings(int duration) {
		if (!map.containsKey(FunctionArticle.SETTINGS))
			return;
		View view = map.get(FunctionArticle.SETTINGS);
		view.setVisibility(View.VISIBLE);
		view.startAnimation(getAnim(view, duration, true));
	}

	/**
	 * 隐藏设置栏
	 * 
	 * @param duration
	 */
	public void disSetting(int duration) {
		if (!map.containsKey(FunctionArticle.SETTINGS))
			return;
		View view = map.get(FunctionArticle.SETTINGS);
		if (view.getVisibility() == View.VISIBLE) {
			view.startAnimation(getAnim(view, duration, false));
		}
	}

	/**
	 * 设置栏是否可见
	 * 
	 * @return
	 */
	public boolean isShowingSettings() {
		if (!map.containsKey(FunctionArticle.SETTINGS))
			return false;
		View view = map.get(FunctionArticle.SETTINGS);
		return view.getVisibility() == View.VISIBLE;
	}

	/**
	 * 是否有设置栏
	 * 
	 * @return
	 */
	public boolean isSettingBarExist() {
		return map.containsKey(FunctionArticle.SETTINGS);
	}

	/**
	 * 减小字体
	 */
	protected void fontMinus(View v) {
		((ArticleActivity) mContext).clickFont(false);
		afterSettingsFont();
	}

	/**
	 * 增加字体
	 */
	protected void fontPlus(View v) {
		((ArticleActivity) mContext).clickFont(true);
		afterSettingsFont();
	}

	/**
	 * 减小行距
	 */
	protected void spacingMinus(View v) {
		((CommonArticleActivity) mContext).changeLineHeight(false);
		afterSettingsLine();
	}

	/**
	 * 增加行距
	 */
	protected void spacingPlus(View v) {
		((CommonArticleActivity) mContext).changeLineHeight(true);
		afterSettingsLine();
	}

	/**
	 * 更新字体按钮状态
	 */
	protected void afterSettingsFont() {
		int now = DataHelper.getFontSize(mContext);
		if (map.containsKey(FunctionArticle.FONT_PLUS)) {
			View plus = map.get(FunctionArticle.FONT_PLUS);
			if (now == 5) {
				if (plus.getTag(R.id.select_bg) instanceof String) {
					V.setViewBack(plus, (String) plus.getTag(R.id.select_bg));
				}
			} else if (plus.getTag(R.id.unselect_bg) instanceof String) {
				V.setViewBack(plus, (String) plus.getTag(R.id.unselect_bg));
			}
		}
		if (map.containsKey(FunctionArticle.FONT_MINUS)) {
			View minus = map.get(FunctionArticle.FONT_MINUS);
			if (now == 1) {
				if (minus.getTag(R.id.select_bg) instanceof String) {
					V.setViewBack(minus, (String) minus.getTag(R.id.select_bg));
				}
			} else if (minus.getTag(R.id.unselect_bg) instanceof String) {
				V.setViewBack(minus, (String) minus.getTag(R.id.unselect_bg));
			}
		}
	}

	/**
	 * 更新行距按钮状态
	 */
	protected void afterSettingsLine() {
		int now = DataHelper.getLineHeight(mContext);
		if (map.containsKey(FunctionArticle.SPACING_PLUS)) {
			View plus = map.get(FunctionArticle.SPACING_PLUS);
			if (now == 5) {
				if (plus.getTag(R.id.select_bg) instanceof String) {
					V.setViewBack(plus, (String) plus.getTag(R.id.select_bg));
				}
			} else if (plus.getTag(R.id.unselect_bg) instanceof String) {
				V.setViewBack(plus, (String) plus.getTag(R.id.unselect_bg));
			}
		}
		if (map.containsKey(FunctionArticle.SPACING_MINUS)) {
			View minus = map.get(FunctionArticle.SPACING_MINUS);
			if (now == 1) {
				if (minus.getTag(R.id.select_bg) instanceof String) {
					V.setViewBack(minus, (String) minus.getTag(R.id.select_bg));
				}
			} else if (minus.getTag(R.id.unselect_bg) instanceof String) {
				V.setViewBack(minus, (String) minus.getTag(R.id.unselect_bg));
			}
		}
	}

	/**
	 * 
	 * @param duration
	 *            持续时间
	 * @param in
	 *            是否显示
	 * @return
	 */
	private AnimationSet getAnim(final View view, int duration, final boolean in) {
		AnimationSet set = new AnimationSet(false);
		int height = view.getLayoutParams().height;
		int fromY = in ? -height : 0;
		int toY = in ? 0 : -height;
		float fromAlpha = in ? 0.0f : 1.0f;
		float toAlpha = in ? 1.0f : 0.0f;
		TranslateAnimation translate = new TranslateAnimation(0, 0, fromY, toY);
		AlphaAnimation alpha = new AlphaAnimation(fromAlpha, toAlpha);
		translate.setDuration(duration);
		translate.setFillAfter(true);
		alpha.setDuration(duration);
		alpha.setFillAfter(true);
		set.addAnimation(translate);
		set.addAnimation(alpha);
		set.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (!in)
					view.setVisibility(View.GONE);
			}
		});
		return set;
	}

}
