package cn.com.modernmedia.views.xmlparse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.db.NewFavDb;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.pay.PayActivity;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.fav.BindFavToUserImplement;
import cn.com.modernmedia.views.index.adapter.BaseIndexAdapter;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.unit.DateFormatTool;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 设置数据基类
 * 
 * @author zhuqiao
 * 
 */
public class BaseXMLDataSet {
	protected Context mContext;
	protected HashMap<String, View> map = new HashMap<String, View>();
	protected List<View> ninePatchViewList = new ArrayList<View>();
	protected List<View> clickViewList;

	public BaseXMLDataSet(Context context, HashMap<String, View> map,
			List<View> clickViewList, List<View> ninePatchViewList) {
		mContext = context;
		this.map = map;
		this.clickViewList = clickViewList;
		this.ninePatchViewList = ninePatchViewList;
	}

	/**
	 * 标题
	 * 
	 * @param item
	 */
	protected void title(ArticleItem item, BaseIndexAdapter adapter) {
		if (!map.containsKey(FunctionXML.TITLE))
			return;
		View view = map.get(FunctionXML.TITLE);
		if (!(view instanceof TextView))
			return;
		TextView title = (TextView) view;
		String titleText = "";
		if (item.getPosition().getStyle() == 3
				&& TextUtils.equals(item.getApiTag(), "cat_15")) {
			titleText = item.getGroupdisplayname() + " | " + item.getTitle();
		} else
			titleText = item.getTitle();
		title.setText(titleText);
		TextPaint tp = title.getPaint();

		if (adapter == null)
			return;

		if (V.articleIsReaded(item)) {
			if (title.getTag(R.id.title_readed_color) instanceof String) {
				String color = title.getTag(R.id.title_readed_color).toString();
				if (color.startsWith("#"))
					title.setTextColor(Color.parseColor(color));
			}
			if (title.getTag(R.id.title_readed_font) instanceof String
					&& item.getPosition().getStyle() == 3
					&& TextUtils.equals(item.getApiTag(), "cat_15")) {
				title.setTextColor(item.getGroupdisplaycolor());
				tp.setFakeBoldText(false); // 推荐首页大图模式未读文章设为默认字体
			}
		} else {
			if (title.getTag(R.id.title_default_color) instanceof String) {
				String color = title.getTag(R.id.title_default_color)
						.toString();
				if (color.startsWith("#"))
					title.setTextColor(Color.parseColor(color));
			}
			if (title.getTag(R.id.title_default_font) instanceof String
					&& item.getPosition().getStyle() == 3
					&& TextUtils.equals(item.getApiTag(), "cat_15")) {
				title.setTextColor(item.getGroupdisplaycolor());
				tp.setFakeBoldText(true); // 推荐首页大图模式已读文章设为加粗字体
			}
		}
	}

	/**
	 * 商周付费专享logo
	 */
	protected void pay(ArticleItem item) {
		if (!map.containsKey(FunctionXML.HK_PAY_IMG))
			return;
		View view = map.get(FunctionXML.HK_PAY_IMG);

		if (item.getProperty().getLevel() != 1)
			view.setVisibility(View.GONE);
		else
			view.setVisibility(View.VISIBLE);
	}

	/**
	 * 描述
	 */
	protected void desc(ArticleItem item, int position, BaseIndexAdapter adapter) {
		if (!map.containsKey(FunctionXML.DESC))
			return;
		View view = map.get(FunctionXML.DESC);
		if (!(view instanceof TextView))
			return;
		TextView desc = (TextView) view;
		if (adapter == null) {
			desc.setText(item.getDesc());
			return;
		}
		if (desc.getTag(R.id.desc_check_scroll) == null) {
			desc.setText(item.getDesc());
			return;
		}
		if (!adapter.isConvertViewIsNull())
			desc.setText("");
		if (adapter.hasInitDesc(position))
			desc.setText(item.getDesc());
		if (!adapter.isScroll()) {
			if (!adapter.hasInitDesc(position)) {
				desc.setText(item.getDesc());
				adapter.addDesc(position);
			}
		}
	}

	/**
	 * 视频图片
	 * 
	 * @param item
	 */
	protected void video(ArticleItem item) {
		if (!map.containsKey(FunctionXML.VIDEO_IMG))
			return;
		map.get(FunctionXML.VIDEO_IMG).setVisibility(
				item.getProperty().getHasvideo() == 1 ? View.VISIBLE
						: View.GONE);
	}

	/**
	 * 播放视频
	 * 
	 * @param item
	 */
	protected void videoview(ArticleItem item, BaseIndexAdapter adapter) {
		if (!item.isAdv()) {
			return;
		}
		if (!map.containsKey(FunctionXML.VIDEO_VIEW)) {
			return;
		}

		View videoView = map.get(FunctionXML.VIDEO_VIEW);

		videoView.getLayoutParams().width = CommonApplication.width;
		AdvSource advPic = item.getAdvSource();
		if (advPic != null && advPic.getWidth() > 0) {
			int height = advPic.getHeight() * CommonApplication.width
					/ advPic.getWidth();
			videoView.getLayoutParams().height = height;
		}
	}

	protected void audio() {
		if (!map.containsKey(FunctionXML.AUDIO_IMG)) {
			return;
		}
		View view = map.get(FunctionXML.AUDIO_IMG);
		if (!(view instanceof ImageView))
			return;
		final ImageView audio = (ImageView) view;
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// 此处相当于布局文件中的Android:layout_gravity属性
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.setMargins(0, 15, 15, 0);
		audio.setLayoutParams(lp);

		audio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				V.muteAudio(mContext, !V.isMute, true);
				if (V.isMute) {
					audio.setImageResource(R.drawable.mute);
				} else {
					audio.setImageResource(R.drawable.volume);
				}
			}
		});
	}

	/**
	 * outline
	 */
	protected void outline(ArticleItem item, int position,
			BaseIndexAdapter adapter) {
		if (!map.containsKey(FunctionXML.OUTLINE))
			return;
		View view = map.get(FunctionXML.OUTLINE);
		if (!(view instanceof TextView))
			return;
		TextView outline = (TextView) view;
		if (adapter == null) {
			outline.setText(item.getOutline());
			return;
		}
		if (outline.getTag(R.id.desc_check_scroll) == null) {
			outline.setText(item.getOutline());
			return;
		}
		if (!adapter.isConvertViewIsNull())
			outline.setText("");
		if (adapter.hasInitDesc(position))
			outline.setText(item.getOutline());
		if (!adapter.isScroll()) {
			if (!adapter.hasInitDesc(position)) {
				outline.setText(item.getOutline());
				adapter.addDesc(position);
			}
		}
	}

	/**
	 * tag
	 */
	protected void tag(ArticleItem item, int position, BaseIndexAdapter adapter) {
		if (!map.containsKey(FunctionXML.TAG))
			return;
		View view = map.get(FunctionXML.TAG);
		if (!(view instanceof TextView))
			return;
		TextView tag = (TextView) view;
		if (adapter == null) {
			tag.setText(item.getTag());
			return;
		}
		if (tag.getTag(R.id.desc_check_scroll) == null) {
			tag.setText(item.getTag());
			return;
		}
		if (!adapter.isConvertViewIsNull())
			tag.setText("");
		if (adapter.hasInitDesc(position))
			tag.setText(item.getTag());
		if (!adapter.isScroll()) {
			if (!adapter.hasInitDesc(position)) {
				tag.setText(item.getTag());
				adapter.addDesc(position);
			}
		}
	}

	/**
	 * sub_title
	 */
	protected void subTitle(ArticleItem item, int position,
			BaseIndexAdapter adapter) {
		if (!map.containsKey(FunctionXML.SUB_TITLE))
			return;
		View view = map.get(FunctionXML.SUB_TITLE);
		if (!(view instanceof TextView))
			return;
		TextView sub_title = (TextView) view;
		if (adapter == null) {
			sub_title.setText(item.getSubtitle());
			return;
		}
		if (sub_title.getTag(R.id.desc_check_scroll) == null) {
			sub_title.setText(item.getSubtitle());
			return;
		}
		if (!adapter.isConvertViewIsNull())
			sub_title.setText("");
		if (adapter.hasInitDesc(position))
			sub_title.setText(item.getSubtitle());
		if (!adapter.isScroll()) {
			if (!adapter.hasInitDesc(position)) {
				sub_title.setText(item.getSubtitle());
				adapter.addDesc(position);
			}
		}
	}

	/**
	 * create_user
	 */
	protected void createUser(ArticleItem item, int position,
			BaseIndexAdapter adapter) {
		if (!map.containsKey(FunctionXML.CREATE_USER))
			return;
		View view = map.get(FunctionXML.CREATE_USER);
		if (!(view instanceof TextView))
			return;
		TextView create_user = (TextView) view;
		if (adapter == null) {
			create_user.setText(item.getCreateuser());
			return;
		}
		if (create_user.getTag(R.id.desc_check_scroll) == null) {
			create_user.setText(item.getCreateuser());
			return;
		}
		if (!adapter.isConvertViewIsNull())
			create_user.setText("");
		if (adapter.hasInitDesc(position))
			create_user.setText(item.getCreateuser());
		if (!adapter.isScroll()) {
			if (!adapter.hasInitDesc(position)) {
				create_user.setText(item.getCreateuser());
				adapter.addDesc(position);
			}
		}
	}

	/**
	 * modify_user
	 */
	protected void modifyUser(ArticleItem item, int position,
			BaseIndexAdapter adapter) {
		if (!map.containsKey(FunctionXML.MODIFY_USER))
			return;
		View view = map.get(FunctionXML.MODIFY_USER);
		if (!(view instanceof TextView))
			return;
		TextView modify_user = (TextView) view;
		if (adapter == null) {
			modify_user.setText(item.getModifyuser());
			return;
		}
		if (modify_user.getTag(R.id.desc_check_scroll) == null) {
			modify_user.setText(item.getModifyuser());
			return;
		}
		if (!adapter.isConvertViewIsNull())
			modify_user.setText("");
		if (adapter.hasInitDesc(position))
			modify_user.setText(item.getModifyuser());
		if (!adapter.isScroll()) {
			if (!adapter.hasInitDesc(position)) {
				modify_user.setText(item.getModifyuser());
				adapter.addDesc(position);
			}
		}
	}

	/**
	 * 日期
	 * 
	 * @param item
	 */
	protected void date(ArticleItem item) {
		for (String key : map.keySet()) {
			if (!key.startsWith(FunctionXML.DATE))
				continue;
			View view = map.get(key);
			if (!(view instanceof TextView))
				continue;
			if (!(view.getTag(R.id.date_format) instanceof String))
				continue;
			TextView date = (TextView) view;
			String language = view.getTag(R.id.date_format_language) == null ? ""
					: view.getTag(R.id.date_format_language).toString();
			date.setText(DateFormatTool.format(
					ParseUtil.stol(item.getInputtime()) * 1000L,
					view.getTag(R.id.date_format).toString(), language));
		}
	}

	/**
	 * 收藏
	 * 
	 * @param item
	 */
	protected void fav(final ArticleItem item) {
		if (!map.containsKey(FunctionXML.FAV))
			return;
		View view = map.get(FunctionXML.FAV);
		changeFav(view, item);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ModernMediaTools.addFav(mContext, item, Tools.getUid(mContext),
						new BindFavToUserImplement(mContext));
				changeFav(v, item);
			}
		});
	}

	/**
	 * 改变收藏背景图
	 * 
	 * @param view
	 * @param item
	 */
	private void changeFav(View view, ArticleItem item) {
		boolean faved = NewFavDb.getInstance(mContext).containThisFav(
				item.getArticleId(), Tools.getUid(mContext));
		if (faved) {
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
	 * 设置.9图片
	 */
	protected void ninePatch() {
		if (!ParseUtil.listNotNull(ninePatchViewList))
			return;
		for (View view : ninePatchViewList) {
			if (view.getTag(R.id.nine_patch_img) instanceof String) {
				V.setImage(view, view.getTag(R.id.nine_patch_img).toString());
			}
		}
	}

	/**
	 * 注册点击事件
	 * 
	 * @param item
	 * @param articleType
	 */
	protected void registerClick(final ArticleItem item,
			final ArticleType articleType) {
		if (ParseUtil.listNotNull(clickViewList)) {
			for (final View view : clickViewList) {
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						BaseXMLDataSet.this.onClick(view, item, articleType);
					}
				});
			}
		}
	}

	/**
	 * 注册点击事件
	 * 
	 * @param view
	 */
	protected void onClick(View view, ArticleItem item, ArticleType articleType) {
		if (item.getIsTekan() == 1
				&& SlateApplication.APP_ID == 1
				&& !TextUtils.equals("1",
						SlateDataHelper.getIssueLevel(mContext))) {
			Intent i = new Intent(mContext, PayActivity.class);
			mContext.startActivity(i);
		} else
			V.clickSlate(mContext, item, articleType);
		AdvTools.requestClick(item);
		log(item);
	}

	protected void log(ArticleItem item) {
		LogHelper.logOpenArticleFromColumnPage(mContext, item.getArticleId()
				+ "", item.getTagName() + "");
	}
}
