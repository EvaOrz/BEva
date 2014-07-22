package cn.com.modernmedia.views.util;

import java.lang.reflect.Field;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.Cat.CatItem;
import cn.com.modernmedia.model.SoloColumn.SoloColumnChild;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.adapter.BaseIndexAdapter;
import cn.com.modernmedia.views.adapter.BusIndexAdapter;
import cn.com.modernmedia.views.adapter.LadyIndexAdapter;
import cn.com.modernmedia.views.adapter.LohasIndexAdapter;
import cn.com.modernmedia.views.adapter.TancIndexAdapter;
import cn.com.modernmedia.views.adapter.WeeklyIndexAdapter;
import cn.com.modernmedia.views.adapter.ZhihuiyunIndexAdapter;
import cn.com.modernmedia.views.index.BusHeadView;
import cn.com.modernmedia.views.index.IndexHeadView;
import cn.com.modernmedia.views.index.WeeklyHeadView;
import cn.com.modernmedia.views.model.IndexListParm;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.util.CardUriParse;
import cn.com.modernmediausermodel.util.UserTools;

public class V {
	public static final String BUSINESS = "business";
	public static final String ILADY = "ilady";
	public static final String ILOHAS = "ilohas";
	public static final String TANC = "tanc";
	public static final String IWEEKLY = "iweekly";
	public static final String ZHIHUIYUN = "zhihuiyun";

	public static final int ID_ERROR = -1;
	public static final int ID_COLOR = -2;

	/**
	 * 列表点击
	 * 
	 * @param view
	 * @param context
	 * @param item
	 * @param articleType
	 * @param cls
	 */
	public static void clickSlate(BaseView view, Context context,
			ArticleItem item, ArticleType articleType, Class<?>... cls) {
		CardUriParse.getInstance();
		TransferArticle transferArticle = new TransferArticle(articleType,
				UserTools.getUid(context));
		view.clickSlate(new Entry[] { item, transferArticle }, cls);
	}

	/**
	 * 列表点击
	 * 
	 * @param context
	 * @param item
	 * @param articleType
	 * @param cls
	 */
	public static void clickSlate(Context context, ArticleItem item,
			ArticleType articleType, Class<?>... cls) {
		CardUriParse.getInstance();
		TransferArticle transferArticle = new TransferArticle(articleType,
				UserTools.getUid(context));
		UriParse.clickSlate(context, new Entry[] { item, transferArticle }, cls);
	}

	/**
	 * 根据图片名称获取图片资源id
	 * 
	 * @param pic
	 * @return 0,错误；1.颜色；else 资源id
	 */
	public static int getId(String pic) {
		if (CommonApplication.drawCls == null) {
			return ID_ERROR;
		}
		if (TextUtils.isEmpty(pic)) {
			return ID_ERROR;
		}
		if (pic.startsWith("#")) {
			return ID_COLOR;
		}
		try {
			Field field = CommonApplication.drawCls.getDeclaredField(pic);
			return field.getInt(pic);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ID_ERROR;
	}

	/**
	 * 给控件设置image
	 * 
	 * @param view
	 * @param pic
	 */
	public static void setImage(View view, String pic) {
		int id = getId(pic);
		if (id == ID_ERROR)
			return;
		if (id == ID_COLOR) {
			view.setBackgroundColor(Color.parseColor(pic));
			return;
		}
		if (view instanceof ImageView)
			((ImageView) view).setImageResource(id);
		else
			view.setBackgroundResource(id);
	}

	/**
	 * 给view设置背景
	 * 
	 * @param view
	 * @param pic
	 */
	public static void setViewBack(View view, String pic) {
		int id = getId(pic);
		if (id == ID_ERROR)
			return;
		if (id == ID_COLOR)
			view.setBackgroundColor(Color.parseColor(pic));
		else
			view.setBackgroundResource(id);
	}

	/**
	 * 给listview设置divider
	 * 
	 * @param listView
	 * @param pic
	 */
	public static void setListviewDivider(Context context, ListView listView,
			String pic) {
		int id = getId(pic);
		if (id == ID_ERROR)
			return;
		if (id == ID_COLOR)
			listView.setDivider(new ColorDrawable(Color.parseColor(pic)));
		else
			listView.setDivider(new BitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(), id)));
	}

	/**
	 * 商周设置首页item右边的icon
	 * 
	 * @param imageView
	 * @param item
	 */
	public static void setIndexItemButtonImg(ImageView imageView,
			ArticleItem item) {
		int catId = item.getCatId();
		boolean hasRes = catId == 11 || catId == 12 || catId == 13
				|| catId == 14 || catId == 16 || catId == 18 || catId == 19
				|| catId == 20 || catId == 21 || catId == 32 || catId == 37;
		if (hasRes)
			setImage(imageView, "arrow_" + catId);
		else if (catId >= 110 && catId <= 114)
			setImage(imageView, "arrow_32");
		else if (catId == 99)
			setImage(imageView, "arrow_14");
		else {
			setImage(imageView, "arrow_xx");
			if (ParseUtil.mapContainsKey(DataHelper.columnPicMap, catId)) {
				List<String> list = DataHelper.columnPicMap.get(catId);
				if (ParseUtil.listNotNull(list))
					CommonApplication.finalBitmap.display(imageView,
							list.get(0));
			}
		}
	}

	public static final String PARENT = "menu_landscape@2x.png";
	public static final String CHILD = "menu_landscape_highlighted@2x.png";

	/**
	 * 为栏目设置默认icon
	 * 
	 * @param catId
	 * @param imageView
	 */
	public static void setImageForWeeklyCat(CatItem catItem,
			ImageView imageView, boolean isParent) {
		int catId = catItem.getId();
		if (catId == 191) {
			setImage(imageView, "shiye_icon");
		} else if (catId == 192) {
			setImage(imageView, "xinwen_icon");
		} else if (catId == 194) {
			if (isParent)
				setImage(imageView, "zhuanlan_icon");
			else
				setImage(imageView, "zuixin_icon");
		} else if (catId == 204) {
			setImage(imageView, "nyt_icon");
		} else if (catId == 198) {
			setImage(imageView, "wenhua_icon");
		} else if (catId == 200) {
			setImage(imageView, "shenghuo_icon");
		} else if (catId == 203) {
			setImage(imageView, "meirong_icon");
		} else if (catId == 202) {
			setImage(imageView, "meishi_icon");
		} else if (catId == 201) {
			if (isParent)
				setImage(imageView, "xingzuo_icon");
			else
				setImage(imageView, "xingzuo_child_icon");
		} else if (catId == 199) {
			setImage(imageView, "fengshang_icon");
		} else if (catId == 196) {
			setImage(imageView, "huabao_icon");
		} else if (catId == 195) {
			if (isParent)
				setImage(imageView, "chengshi_icon");
			else
				setImage(imageView, "didian_icon");
		} else if (catId == 206 || catId == 207) {
			setImage(imageView, "didian_icon");
		} else if (catId == 208) {
			setImage(imageView, "quanqiu_icon");
		}
		downloadIcon(catItem, imageView, isParent);
	}

	private static void downloadIcon(CatItem item, ImageView imageView,
			boolean isParent) {
		if (ParseUtil.mapContainsKey(DataHelper.columnPicMap, item.getId())) {
			List<String> list = DataHelper.columnPicMap.get(item.getId());
			if (!ParseUtil.listNotNull(list))
				return;
			for (String url : list) {
				if (isParent && url.contains(PARENT)) {
					CommonApplication.finalBitmap.display(imageView, url);
				} else if (!isParent && url.contains(CHILD)) {
					CommonApplication.finalBitmap.display(imageView, url);
				}
			}
		}
	}

	/**
	 * 是否独立栏目
	 * 
	 * @param item
	 * @return
	 */
	public static boolean isSoloColumn(Context context, CatItem item) {
		if (!CommonApplication.hasSolo || CommonApplication.issue == null)
			return false;
		int catId;
		if (CommonApplication.issue.getId() == 0) {
			// TODO 全部是独立栏目
			catId = item.getId();
		} else {
			catId = ModernMediaTools.isSoloCat(item.getLink());
			if (catId == -1) {
				return false;
			}
		}
		if (CommonApplication.manage != null)
			CommonApplication.manage.getProcess().showSoloChildCat(catId, true);
		return true;
	}

	/**
	 * 是否父栏目
	 * 
	 * @param context
	 * @param item
	 * @return
	 */
	public static boolean isParentColumn(Context context, CatItem item) {
		// if (item.getHaveChildren() == 1) {
		if (DataHelper.childMap.containsKey(item.getParentId())) {
			((ViewsMainActivity) context).showChildCat(item.getParentId());
			return true;
		}
		// }
		return false;
	}

	public static int getSoloParentId(Context context, CatItem item) {
		if (!CommonApplication.hasSolo || CommonApplication.issue == null
				|| item == null)
			return -1;
		if (CommonApplication.issue.getId() == 0)
			return item.getId();
		return ModernMediaTools.isSoloCat(item.getLink());
	}

	/**
	 * 设置首页标题
	 * 
	 * @param context
	 * @param entry
	 */
	public static void setIndexTitle(Context context, Entry entry) {
		if (!(context instanceof ViewsMainActivity)) {
			return;
		}
		if (entry instanceof CatItem) {
			CatItem item = (CatItem) entry;
			if (!ParseUtil.mapContainsKey(DataHelper.childMap, item.getId())) {
				((ViewsMainActivity) context).setTitle(item);
				return;
			}
			List<CatItem> list = DataHelper.childMap.get(item.getId());
			if (ParseUtil.listNotNull(list)) {
				int savedCatId = DataHelper.getChildId(context, item.getId()
						+ "");
				if (savedCatId != -1) {
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getId() == savedCatId) {
							((ViewsMainActivity) context).setTitle(list.get(i));
							return;
						}
					}
					((ViewsMainActivity) context).setTitle(list.get(0));
				} else {
					((ViewsMainActivity) context).setTitle(item);
				}
			} else {
				((ViewsMainActivity) context).setTitle(item);
			}
		} else if (entry instanceof SoloColumnChild) {
			SoloColumnChild child = (SoloColumnChild) entry;
			String title = child.getName();
			if (ParseUtil.mapContainsKey(DataHelper.columnTitleMap,
					child.getParentId())) {
				title = DataHelper.columnTitleMap.get(child.getParentId())
						+ " · " + child.getName();
			}
			((ViewsMainActivity) context).setIndexTitle(title);
		}
	}

	/**
	 * 根据type获取首页adapter
	 * 
	 * @param type
	 * @return
	 */
	public static BaseIndexAdapter getIndexAdapter(Context context,
			IndexListParm parm) {
		BaseIndexAdapter adapter;
		if (parm.getType().equals(V.BUSINESS)) {
			adapter = new BusIndexAdapter(context, parm);
		} else if (parm.getType().equals(V.ILADY)) {
			adapter = new LadyIndexAdapter(context, parm);
		} else if (parm.getType().equals(V.ILOHAS)) {
			adapter = new LohasIndexAdapter(context, parm);
		} else if (parm.getType().equals(V.TANC)) {
			adapter = new TancIndexAdapter(context, parm);
		} else if (parm.getType().equals(V.IWEEKLY)) {
			adapter = new WeeklyIndexAdapter(context, parm);
		} else if (parm.getType().equals(V.ZHIHUIYUN)) {
			adapter = new ZhihuiyunIndexAdapter(context, parm);
		} else {
			adapter = new BaseIndexAdapter(context, parm);
		}
		return adapter;
	}

	/**
	 * 根据type获取首页焦点图
	 * 
	 * @param context
	 * @param parm
	 * @return
	 */
	public static IndexHeadView getIndexHeadView(Context context,
			IndexListParm parm) {
		IndexHeadView headView = null;
		if (parm.getHead_type().equals(V.BUSINESS)) {
			headView = new BusHeadView(context, parm);
		} else if (parm.getHead_type().equals(V.IWEEKLY)) {
			headView = new WeeklyHeadView(context, parm);
		}
		return headView;
	}
}
