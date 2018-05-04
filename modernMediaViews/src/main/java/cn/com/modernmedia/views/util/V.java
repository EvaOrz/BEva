package cn.com.modernmedia.views.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.TagInfoListDb;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.index.adapter.BaseIndexAdapter;
import cn.com.modernmedia.views.index.adapter.GridIndexAdapter;
import cn.com.modernmedia.views.index.adapter.ListIndexAdapter;
import cn.com.modernmedia.views.index.head.IndexViewHead;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.listener.ImageDownloadStateListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.util.CardUriParse;

public class V {
	public static final String BUSINESS = "business";
	public static final String ILADY = "ilady";
	public static final String ILOHAS = "ilohas";
	public static final String TANC = "tanc";
	public static final String IWEEKLY = "iweekly";
	public static final String ZHIHUIYUN = "zhihuiyun";
	public static final String WEIZIT = "weizit";

	public static final int ID_ERROR = -1;
	public static final int ID_COLOR = -2;
	public static final int ID_HTTP = -3;
	public static final int ID_GIF = -4;

	public static final int RADIUS = 19;

	public static boolean isMute = false;// 是否静音
	public static int volume;

	/**
	 * 保存自己创建的bitmap
	 */
	public static SparseArray<SoftReference<Bitmap>> bitmapCache = new SparseArray<SoftReference<Bitmap>>();

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
				Tools.getUid(context));
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
				Tools.getUid(context));
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
		if (pic.startsWith("http")) {
			return ID_HTTP;
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
	@SuppressLint("NewApi")
	public static void setImage(View view, String pic) {
		int id = getId(pic);
		if (id == ID_ERROR)
			return;
		if (id == ID_COLOR) {
			view.setBackgroundColor(Color.parseColor(pic));
			return;
		}
		if (id == ID_HTTP) {
			SlateApplication.finalBitmap.display(view, pic);
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
	public static void setViewBack(final View view, String pic) {
		int id = getId(pic);
		if (id == ID_ERROR)
			return;
		if (id == ID_COLOR)
			view.setBackgroundColor(Color.parseColor(pic));
		else if (id == ID_HTTP)
			SlateApplication.finalBitmap.display(pic,
					new ImageDownloadStateListener() {

						@Override
						public void loading() {
						}

						@Override
						public void loadOk(Bitmap bitmap,
								NinePatchDrawable drawable, byte[] gifByte) {
							if (drawable != null)
								view.setBackgroundDrawable(drawable);
							else
								view.setBackgroundDrawable(new BitmapDrawable(
										bitmap));
						}

						@Override
						public void loadError() {
						}
					});
		else
			view.setBackgroundResource(id);
	}

	/**
	 * 给listview设置divider
	 * 
	 * @param listView
	 * @param pic
	 */
	public static void setListviewDivider(Context context,
			final ListView listView, String pic) {
		int id = getId(pic);
		if (id == ID_ERROR)
			return;
		if (id == ID_COLOR)
			listView.setDivider(new ColorDrawable(Color.parseColor(pic)));
		else if (id == ID_HTTP)
			SlateApplication.finalBitmap.display(pic,
					new ImageDownloadStateListener() {

						@Override
						public void loading() {
						}

						@Override
						public void loadOk(Bitmap bitmap,
								NinePatchDrawable drawable, byte[] gifByte) {
							if (drawable != null)
								listView.setDivider(drawable);
							else
								listView.setDivider(new BitmapDrawable(bitmap));
						}

						@Override
						public void loadError() {
						}
					});
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
		String tagName = item.getGroupname();
		int color = Color.RED;
		if ( DataHelper.columnColorMap.containsKey(tagName)) {
			color = DataHelper.columnColorMap.get(tagName);
		}

		SoftReference<Bitmap> srBitmap = bitmapCache.get(color);
		if (srBitmap != null) {
			if (srBitmap.get() != null) {
				imageView.setImageBitmap(srBitmap.get());
				return;
			}
			bitmapCache.remove(color);
		}
		Bitmap bitmap = createRowBitmap(color);
		if (bitmap != null) {
			bitmapCache.put(color, new SoftReference<Bitmap>(bitmap));
			imageView.setImageBitmap(bitmap);
		}
	}

	/**
	 * 创建箭头图片
	 * 
	 * @param color
	 * @return
	 */
	private static Bitmap createRowBitmap(int color) {
		Bitmap output = Bitmap.createBitmap(RADIUS * 2, RADIUS * 2,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);
		// 画圆
		canvas.drawCircle(RADIUS, RADIUS, RADIUS, paint);
		// 画箭头
		paint.setColor(Color.WHITE);
		Path path = new Path();
		path.moveTo(RADIUS - 10, RADIUS - 2);
		path.lineTo(RADIUS, RADIUS - 2);
		path.lineTo(RADIUS, RADIUS - 2 - 5);
		path.lineTo(RADIUS + 10, RADIUS);
		path.lineTo(RADIUS, RADIUS + 2 + 5);
		path.lineTo(RADIUS, RADIUS + 2);
		path.lineTo(RADIUS - 10, RADIUS + 2);
		path.close();
		canvas.drawPath(path, paint);
		return output;
	}

	/**
	 * 下载栏目图片;如果没有栏目图，就用圆点
	 * 
	 * @param isChildSelectAdapter
	 *            只有iweekly有用
	 */
	public static void downCatPic(Context context, TagInfo info,
			ImageView imageView, boolean isChildSelectAdapter) {
		if (ParseUtil
				.mapContainsKey(DataHelper.columnPicMap, info.getTagName())) {
			List<String> list = DataHelper.columnPicMap.get(info.getTagName());
			if (ParseUtil.listNotNull(list)) {
				CommonApplication.finalBitmap.display(imageView, list.get(0));
				return;
			}
		}
		if ((info.getAppId() == 20 || info.getAppId() == 37)
				&& !isChildSelectAdapter) {
			// TODO nothing...
		} else if (info.getAppId() == 105) {
			// TODO nothing...
		} else {
			catPicShowColor(context, info, imageView);
		}
	}

	/**
	 * 显示颜色
	 * 
	 * @param context
	 * @param info
	 * @param imageView
	 */
	public static void catPicShowColor(Context context, TagInfo info,
			ImageView imageView) {
		if (ParseUtil.mapContainsKey(DataHelper.columnColorMap,
				info.getTagName())) {
			if (ConstData.isGoodLife()) {
				// GOODLIFE
				imageView.setBackgroundColor(DataHelper.columnColorMap.get(info
						.getTagName()));
			} else if (info.getAppId() == 12) {
				// 艺术新闻
				imageView.setScaleType(ScaleType.CENTER);
				imageView.setImageBitmap(getCircleBitmap(context,
						Color.parseColor("#F90000")));
			} else {
				imageView.setScaleType(ScaleType.CENTER);
				imageView.setImageBitmap(getCircleBitmap(context,
						DataHelper.columnColorMap.get(info.getTagName())));
			}
		}
	}

	/**
	 * 下载订阅大图
	 * 
	 * @param tagName
	 * @param imageView
	 */
	public static void downSubscriptPicture(String tagName, ImageView imageView) {
		if (ParseUtil.mapContainsKey(DataHelper.columnBigMap, tagName)) {
			List<String> list = DataHelper.columnBigMap.get(tagName);
			if (ParseUtil.listNotNull(list)) {
				CommonApplication.finalBitmap.display(imageView, list.get(0));
			}
		}
	}

	public static final String PARENT = "menu_landscape@2x.png";
	public static final String CHILD = "menu_landscape_highlighted@2x.png";

	/**
	 * 为栏目设置默认icon
	 * 
	 * @param imageView
	 */
	public static void setImageForWeeklyCat(Context context, TagInfo tagInfo,
			ImageView imageView) {
		String tagName = tagInfo.getTagName();
		if (ConstData.getAppId() != 20 && ConstData.getAppId() != 37) {
			downCatPic(context, tagInfo, imageView, false);
			return;
		}
		imageView.setImageBitmap(null);
		setImage(imageView, tagName);
		downCatPic(context, tagInfo, imageView, false);
	}

	/**
	 * 设置微在栏目icon
	 */
	public static void setWeZeitColumnIcon(Context context, TagInfo tagInfo,
			ImageView imageView) {
		String tagName = tagInfo.getTagName();
		if (ConstData.getAppId() != 105) {
			downCatPic(context, tagInfo, imageView, false);
			return;
		}
		int catId = ParseUtil.stoi(tagName.replace("cat_", ""), -1);
		if (catId == 46) {
			setImage(imageView, "column_main");
		} else if (catId == 11) {
			setImage(imageView, "column_fresh");
		} else if (catId == 16) {
			setImage(imageView, "column_yule");
		} else if (catId == 10) {
			setImage(imageView, "column_play");
		} else if (catId == 12) {
			setImage(imageView, "column_idle");
		} else if (catId == 17) {
			setImage(imageView, "column_zoo");
		}
		downCatPic(context, tagInfo, imageView, false);
	}

	/**
	 * 设置首页标题
	 * 
	 * @param context
	 * @param entry
	 */
	public static void setIndexTitle(Context context, Entry entry) {
		if (!(context instanceof CommonMainActivity)) {
			return;
		}
		if (entry instanceof TagInfo) {
			TagInfo tagInfo = (TagInfo) entry;
			if (setIndexTitleByUriTag(context, tagInfo))
				return;
			TagInfo parentTag;
			if (tagInfo.getTagLevel() == 2) {
				parentTag = TagInfoListDb.getInstance(context)
						.getTagInfoByName(tagInfo.getParent(), "", true);
			} else {
				parentTag = tagInfo;
			}
			if (ConstData.getAppId() == 37) {// verycity
				((CommonMainActivity) context).setIndexTitle(parentTag
						.getColumnProperty().getCname());
				return;
			}
			if (parentTag.showChildren()) {
				// TODO 子栏目显示"父栏目 · 子栏目"
				TagInfoList childInfoList = AppValue.ensubscriptColumnList
						.getChildHasSubscriptTagInfoList(parentTag.getTagName());
				if (ParseUtil.listNotNull(childInfoList.getList())) {
					String childName = "";
					if (tagInfo.getTagLevel() == 1) {
						childName = childInfoList.getList().get(0)
								.getColumnProperty().getCname();
					} else {
						childName = tagInfo.getColumnProperty().getCname();
					}
					String title = parentTag.getColumnProperty().getCname()
							+ " · " + childName;
					((CommonMainActivity) context).setIndexTitle(title);
					return;
				}
			}
			((CommonMainActivity) context).setIndexTitle(tagInfo
					.getColumnProperty().getCname());
		}
	}

	/**
	 * 根据uri新生成的tag
	 * 
	 * @param context
	 * @param tagInfo
	 * @return
	 */
	private static boolean setIndexTitleByUriTag(Context context,
			TagInfo tagInfo) {
		if (!tagInfo.isUriTag())
			return false;
		String title = "";
		if (TextUtils.isEmpty(tagInfo.getParent())
				|| tagInfo.getParent().startsWith("app")) {
			// TODO 一级栏目
			title = tagInfo.getColumnProperty().getCname();
			if (tagInfo.getHaveChildren() == 1
					&& AppValue.uriTagInfoList.getChildMap().containsKey(
							tagInfo.getTagName())) {
				Map<String, List<TagInfo>> map = AppValue.uriTagInfoList
						.getChildMap();
				if (ParseUtil.mapContainsKey(map, tagInfo.getTagName())
						&& ParseUtil.listNotNull(map.get(tagInfo.getTagName()))) {
					title += " · "
							+ map.get(tagInfo.getTagName()).get(0)
									.getColumnProperty().getCname();
					((CommonMainActivity) context).setIndexTitle(title);
					return true;
				}
			}
			((CommonMainActivity) context).setIndexTitle(title);
			return true;
		}
		// TODO 子栏目
		for (TagInfo _info : AppValue.uriTagInfoList.getList()) {
			if (_info.getTagName().equals(tagInfo.getParent())) {
				title = _info.getColumnProperty().getCname() + " · "
						+ tagInfo.getColumnProperty().getCname();
				((ViewsMainActivity) context).setIndexTitle(title);
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据type获取首页adapter
	 * 
	 * @return
	 */
	public static BaseIndexAdapter getIndexAdapter(Context context,
			Template template, ArticleType articleType) {
		if (template.getList().getOne_line_count() > 1)
			return new GridIndexAdapter(context, template, articleType);
		else
			return new ListIndexAdapter(context, template, articleType);
	}

	/**
	 * 根据type获取首页焦点图
	 * 
	 * @param context
	 * @param parm
	 * @return
	 */
	public static IndexViewHead getIndexHeadView(Context context,
			Template template) {
		return new IndexViewHead(context, template);
	}

	public static Bitmap getCircleBitmap(Context context, int color) {
		int size = context.getResources().getDimensionPixelSize(R.dimen.dp10);
		Bitmap bitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setColor(color);
		canvas.drawCircle(size / 2, size / 2, size / 2, paint);
		return bitmap;
	}

	public static void setTextById(TextView textView, String resId) {
		if (CommonApplication.stringCls == null) {
			return;
		}
		if (TextUtils.isEmpty(resId)) {
			return;
		}
		try {
			Field field = CommonApplication.stringCls.getDeclaredField(resId);
			textView.setText(field.getInt(resId));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 文章是否已读
	 * 
	 * @param item
	 * @return
	 */
	public static boolean articleIsReaded(ArticleItem item) {
		if (ConstData.getAppId() == 20 && item.getAppid() == 20) { // iweekly 文章
			if (ViewsApplication.lastestArticleId == null) {
				return true;
			} else {
				List<Integer> ids = ViewsApplication.lastestArticleId
						.getUnReadedId();
				if (ids.contains(item.getArticleId())) {
					return false;
				}
			}
			return true;
		}
		if (!ParseUtil.listNotNull(ViewsApplication.readedArticles))
			return false;
		return ViewsApplication.readedArticles.contains(Integer.valueOf(item
				.getArticleId()));
	}

	/**
	 * 判断是否需要插入订阅文章
	 * 
	 * @param context
	 * @param tagName
	 * @param loadMore
	 * @return
	 */
	public static boolean checkShouldInsertSubscribeArticle(Context context,
			String tagName) {
		// 去掉iweekly
		// if (ConstData.getAppId() != 20 && ConstData.getAppId() != 1) {
		// return false;
		// }
		// if (ConstData.getAppId() == 20 && !tagName.equals("cat_192")) {
		// return false;
		// }
		if (ConstData.getAppId() != 1) {
			return false;
		}
		if (ConstData.getInitialAppId() == 1 && !tagName.equals("cat_15")) {
			return false;
		}
		if (ConstData.getInitialAppId() == 18 && !tagName.equals("cat_167")) {
			return false;
		}
		if (SlateDataHelper.getUserLoginInfo(context) == null
				|| AppValue.ensubscriptColumnList.getColumnTagList(true, false)
						.size() == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 是否静音
	 * 
	 * @param mute
	 *            是否静音
	 * @param showUI
	 *            是否显示声音UI
	 *            是否判断开启声音时声音为0的情况
	 */
	public static void muteAudio(Context context, boolean mute, boolean showUI) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		isMute = mute;
		if (mute) {// 静音
			volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);// 记录
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,
					showUI ? AudioManager.FLAG_SHOW_UI : 0);
		} else {
			if (volume == 0) {
				volume = AudioManager.ADJUST_RAISE * 5;
			}
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
					showUI ? AudioManager.FLAG_SHOW_UI : 0);
		}
	}
}
