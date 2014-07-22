package cn.com.modernmedia.views.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.adapter.CheckScrollAdapter;
import cn.com.modernmedia.db.FavDb;
import cn.com.modernmedia.listener.NotifyAdapterListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.ImageScaleType;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.model.IndexListParm;
import cn.com.modernmedia.views.util.V;

public class BaseIndexAdapter extends CheckScrollAdapter<ArticleItem> {
	private BaseIndexAdapter me;
	protected Context mContext;
	private List<Integer> descList = new ArrayList<Integer>();// 直接加载desc比较卡
	private FavDb favDb;
	private List<ArticleItem> mItemList = new ArrayList<ArticleItem>();
	private boolean isSoloAdapter = false; // 描述当前adapter是否用于独立栏目的，默认不是
	protected IndexListParm parm = new IndexListParm();
	protected int imageHeight = CommonApplication.width / 2;// 图片高度

	/**
	 * 通知刷新readed
	 */
	private NotifyAdapterListener adapterListener = new NotifyAdapterListener() {

		@Override
		public void notifyReaded() {
			me.notifyDataSetChanged();
		}

		@Override
		public void nofitySelectItem(Object args) {
		}

		@Override
		public void notifyChanged() {
		}
	};

	public BaseIndexAdapter(Context context, IndexListParm parm) {
		super(context);
		me = this;
		mContext = context;
		this.parm = parm;
		favDb = FavDb.getInstance(mContext);
		if (context instanceof CommonMainActivity) {
			((CommonMainActivity) context).addListener(adapterListener);
		}
		mItemList.clear();
	}

	/**
	 * 根据配置信息计算图片高度
	 */
	protected void initImageHeight(int widthPadding) {
		int width = CommonApplication.width - 2 * widthPadding;
		if (parm.getItem_img_width() != 0 && parm.getItem_img_height() != 0) {
			imageHeight = width * parm.getItem_img_height()
					/ parm.getItem_img_width();
		}
	}

	public void setData(List<ArticleItem> list) {
		descList.clear();
		isScroll = false;
		synchronized (list) {
			for (ArticleItem item : list) {
				add(item);
			}
		}
	}

	/**
	 * 独立栏目使用
	 * 
	 * @param list
	 * @param addToFirst
	 *            是否添加在前面
	 */
	public void setData(List<ArticleItem> list, boolean addToFirst) {
		if (!ParseUtil.listNotNull(list))
			return;
		if (addToFirst) {
			clear();
			mItemList.addAll(0, list);
			setData(mItemList);
		} else {
			mItemList.addAll(list);
			setData(list);
		}
	}

	/**
	 * 是否阅读过
	 * 
	 * @param articleId
	 * @return
	 */
	protected boolean isReaded(int articleId) {
		if (!ParseUtil.listNotNull(ViewsApplication.readedArticles))
			return false;
		return ViewsApplication.readedArticles.contains(Integer
				.valueOf(articleId));
	}

	/**
	 * 是否收藏过
	 * 
	 * @param articleId
	 * @param uid
	 * @return
	 */
	protected boolean isFaved(int articleId, String uid) {
		return favDb.containThisFav(articleId, uid);
	}

	/**
	 * 添加已加载过的item
	 * 
	 * @param position
	 */
	protected void addDesc(int position) {
		try {
			if (!descList.contains(Integer.valueOf(position)))
				descList.add(position);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 是否加载过当前item
	 * 
	 * @param position
	 * @return
	 */
	protected boolean hasInitDesc(int position) {
		return descList.contains(position);
	}

	public void clearData() {
		mItemList.clear();
		descList.clear();
	}

	public boolean isSoloAdapter() {
		return isSoloAdapter;
	}

	public void setSoloAdapter(boolean isSoloAdapter) {
		this.isSoloAdapter = isSoloAdapter;
	}

	/**
	 * 下载图片
	 * 
	 * @param item
	 * @param imageView
	 */
	protected void downImage(ArticleItem item, ImageView image) {
		if (item == null || parm == null)
			return;
		String scaleType = parm.getScale_type();
		if (TextUtils.isEmpty(scaleType)) {
			scaleType = ImageScaleType.FIT_XY;
		}
		image.setTag(R.id.scale_type, scaleType);
		if (parm.isIs_picture()) {
			if (ParseUtil.listNotNull(item.getPicList()))
				CommonApplication.finalBitmap.display(image, item.getPicList()
						.get(0));
		} else {
			if (ParseUtil.listNotNull(item.getThumbList()))
				CommonApplication.finalBitmap.display(image, item
						.getThumbList().get(0));
		}
	}

	/**
	 * 设置imageview的背景图/颜色
	 * 
	 * @param imageView
	 */
	protected void setViewBack(View view) {
		if (parm == null)
			return;
		V.setViewBack(view, parm.getImage_background());
	}

	/**
	 * 设置占位图
	 * 
	 * @param imageView
	 */
	protected void setPlaceHolder(ImageView imageView) {
		if (TextUtils.isEmpty(parm.getPlaceholder()))
			imageView.setImageDrawable(null);
		else
			V.setImage(imageView, parm.getPlaceholder());
	}

	/**
	 * 设置item背景
	 * 
	 * @param contain
	 */
	protected void setItemBg(View contain) {
		String unSelect = parm.getItem_bg();
		String select = parm.getItem_bg_select();
		if (TextUtils.isEmpty(select) && TextUtils.isEmpty(unSelect)) {
			contain.setBackgroundColor(Color.WHITE);
			return;
		} else if (select.equals(unSelect)) {
			V.setViewBack(contain, select);
			return;
		}

		StateListDrawable drawable = new StateListDrawable();

		// TODO 选中状态
		Drawable selectBg;
		int selectId = V.getId(select);
		if (selectId == V.ID_ERROR)
			selectBg = new ColorDrawable(Color.WHITE);
		else if (selectId == V.ID_COLOR)
			selectBg = new ColorDrawable(Color.parseColor(select));
		else
			selectBg = mContext.getResources().getDrawable(selectId);
		drawable.addState(new int[] { android.R.attr.state_enabled,
				android.R.attr.state_focused }, selectBg);
		drawable.addState(new int[] { android.R.attr.state_pressed,
				android.R.attr.state_enabled }, selectBg);
		drawable.addState(new int[] { android.R.attr.state_focused }, selectBg);
		drawable.addState(new int[] { android.R.attr.state_pressed }, selectBg);

		// TODO 未选中状态
		Drawable unSelectBg;
		int unSelectId = V.getId(unSelect);
		if (unSelectId == V.ID_ERROR)
			unSelectBg = new ColorDrawable(Color.WHITE);
		else if (unSelectId == V.ID_COLOR)
			unSelectBg = new ColorDrawable(Color.parseColor(unSelect));
		else
			unSelectBg = mContext.getResources().getDrawable(unSelectId);
		// TODO 必须写在后面，不然没效果
		drawable.addState(new int[] { android.R.attr.state_enabled },
				unSelectBg);
		drawable.addState(new int[] {}, unSelectBg);

		contain.setBackgroundDrawable(drawable);
	}
}
