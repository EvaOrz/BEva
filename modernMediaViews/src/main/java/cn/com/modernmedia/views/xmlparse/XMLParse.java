package cn.com.modernmedia.views.xmlparse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.PhonePageList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.adapter.FavAdapter;
import cn.com.modernmedia.views.column.ColumnAdapter;
import cn.com.modernmedia.views.index.adapter.BaseIndexAdapter;
import cn.com.modernmedia.views.model.Template.GridItem;
import cn.com.modernmedia.views.rpn.Calc;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.xmlparse.article.AtlasViewPagerParse;
import cn.com.modernmedia.views.xmlparse.article.XMLDataSetForArticle;
import cn.com.modernmedia.views.xmlparse.article.XMLDataSetForAtlas;
import cn.com.modernmedia.views.xmlparse.article.XMLDataSetForPushArticle;
import cn.com.modernmedia.views.xmlparse.column.XMLDataSetForColumn;
import cn.com.modernmedia.views.xmlparse.fav.XMLDataSetForFav;
import cn.com.modernmedia.views.xmlparse.widget.ButtonParse;
import cn.com.modernmedia.views.xmlparse.widget.FullScreenVideoViewParse;
import cn.com.modernmedia.views.xmlparse.widget.ImageViewParse;
import cn.com.modernmedia.views.xmlparse.widget.IndexHeadCircularViewPagerParse;
import cn.com.modernmedia.views.xmlparse.widget.JianBianViewParse;
import cn.com.modernmedia.views.xmlparse.widget.LinearLayoutParse;
import cn.com.modernmedia.views.xmlparse.widget.LoadingImageParse;
import cn.com.modernmedia.views.xmlparse.widget.RelativeLayoutParse;
import cn.com.modernmedia.views.xmlparse.widget.TextViewParse;
import cn.com.modernmedia.views.xmlparse.widget.ViewParse;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 解析xml layout
 * 
 * @author zhuqiao
 * 
 */
@SuppressLint("UseSparseArrays")
public class XMLParse {
	private Context mContext;
	private DisplayMetrics metrics;
	private FetchCustomViewListener listener;
	private String host = "";// 图片地址host
	/**
	 * key:作用域；value:view
	 */
	private HashMap<String, View> map = new HashMap<String, View>();
	/**
	 * grid类型列表，key:作用域；value:view及其索引
	 */
	private HashMap<String, List<GridItem>> gridMap = new HashMap<String, List<GridItem>>();
	/**
	 * 存储控件名称和其id的对应关系
	 */
	private HashMap<String, Integer> idsMap = new HashMap<String, Integer>();
	/**
	 * 判断选择状态的view列表
	 */
	private List<View> checkSelectView = new ArrayList<View>();
	/**
	 * 可点击的view列表
	 */
	private List<View> clickViewList = new ArrayList<View>();
	/**
	 * grid类型列表,可点击的view列表
	 */
	private List<GridItem> clickGridViewList = new ArrayList<GridItem>();
	/**
	 * 使用.9图片的view
	 */
	private List<View> ninePatchViewList = new ArrayList<View>();

	public XMLParse(Context context, FetchCustomViewListener listener) {
		mContext = context;
		this.listener = listener;
		metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		reStore();
	}

	private void reStore() {
		map.clear();
		gridMap.clear();
		idsMap.clear();
		checkSelectView.clear();
		clickViewList.clear();
		clickGridViewList.clear();
	}

	/**
	 * 创建view
	 * 
	 * @param xml
	 * @param root
	 * @return
	 */
	public View inflate(String xml, ViewGroup root, String host) {
		this.host = host;
		if (TextUtils.isEmpty(xml))
			return new View(mContext);
		try {
			XmlPullParser parser = XmlPullParserFactory.newInstance()
					.newPullParser();
			parser.setInput(new ByteArrayInputStream(xml.getBytes()),
					HTTP.UTF_8);
			return inflate(parser, root);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new View(mContext);
	}

	/**
	 * 设置列表数据
	 */
	public void setData(ArticleItem item, int position,
			BaseIndexAdapter adapter, ArticleType articleType) {
		new XMLDataSet(mContext, map, clickViewList, ninePatchViewList, adapter)
				.setData(item, position, articleType);
	}

	/**
	 * Grid类型列表设置数据
	 */
	public void setData(List<ArticleItem> list, int position, int oneLineCount,
			BaseIndexAdapter adapter, ArticleType articleType) {
		new XMLDataSetForGrid(mContext, gridMap, clickGridViewList,
				ninePatchViewList, adapter).setData(list, position,
				oneLineCount, articleType);
	}

	/**
	 * 给焦点图设置数据
	 */
	public void setDataForHeadItem(ArticleItem item, int position,
			ArticleType articleType) {
		new XMLDataSetForHeadItem(mContext, map, clickViewList,
				ninePatchViewList).setData(item, position, articleType);
	}

	/**
	 * 独立栏目/子栏目导航栏设置数据
	 * 
	 * @param tagInfo
	 * @param position
	 * @param total
	 */
	public void setDataForCatHead(TagInfo tagInfo, int position, int total) {
		new XMLDataSetForCatHead(mContext, map, checkSelectView,
				ninePatchViewList).setData(tagInfo, position, total);
	}

	/**
	 * 独立栏目/子栏目导航栏切换选择状态
	 * 
	 * @param entry
	 * @param position
	 * @param total
	 */
	public void setDataForCatHead(boolean select) {
		new XMLDataSetForCatHead(mContext, map, checkSelectView,
				ninePatchViewList).select(select);
	}

	/**
	 * 获取焦点图
	 * 
	 * @return
	 */
	public XMLDataSetForHead getDataSetForHead() {
		return new XMLDataSetForHead(mContext, map, clickViewList,
				ninePatchViewList);
	}

	/**
	 * 列表headview设置数据
	 * 
	 * @param info
	 */
	public void setDataForListHead(TagInfo tagInfo) {
		new XMLDataSetForListHead(mContext, map, clickViewList,
				ninePatchViewList).setData(tagInfo);
	}

	/**
	 * 栏目列表设置数据
	 */
	public void setDataForColumn(TagInfo info, int position,
			ColumnAdapter adapter) {
		new XMLDataSetForColumn(mContext, map, clickViewList,
				ninePatchViewList, adapter).setData(info, position);
	}

	/**
	 * 收藏列表设置数据
	 */
	public void setDataForFavList(ArticleItem articleItem, int position,
			FavAdapter adapter) {
		new XMLDataSetForFav(mContext, map, clickViewList, ninePatchViewList,
				adapter).setData(articleItem, position);
	}

	/**
	 * 收藏导航栏设置数据
	 * 
	 */
	public void setDataForFavNavbar() {
		new XMLDataSetForFav(mContext, map, clickViewList).setDataForNavBar();
	}

	/**
	 * 关于设置数据
	 * 
	 */
	public void setDataForAbout() {
		new XMLDataSetForAbout(mContext, map, clickViewList, ninePatchViewList)
				.setData();
	}

	/**
	 * 获取文章设置
	 * 
	 * @return
	 */
	public XMLDataSetForArticle getDataSetForArticle() {
		return new XMLDataSetForArticle(mContext, map, clickViewList,
				ninePatchViewList);
	}

	/**
	 * 获取push文章设置
	 * 
	 * @return
	 */
	public XMLDataSetForPushArticle getDataSetForPushArticle() {
		return new XMLDataSetForPushArticle(mContext, map, clickViewList,
				ninePatchViewList);
	}

	/**
	 * 获取首页导航栏设置
	 * 
	 * @return
	 */
	public XMLDataSetForIndexNav getDataSetForIndexNav() {
		return new XMLDataSetForIndexNav(mContext, map, clickViewList,
				ninePatchViewList);
	}

	/**
	 * 获取图集设置
	 * 
	 * @return
	 */
	public XMLDataSetForAtlas getDataSetForAtlas() {
		return new XMLDataSetForAtlas(mContext, map, clickViewList,
				ninePatchViewList);
	}

	/**
	 * 图集ViewPager item 设置数据
	 * 
	 * @param item
	 */
	public void setDataForAtlasItem(PhonePageList item) {
		new XMLDataSetForAtlas(mContext, map, clickViewList, ninePatchViewList)
				.setPagerItemData(item);
	}

	private View inflate(XmlPullParser parser, ViewGroup root) throws Exception {
		View result = root;
		int type;

		// 寻找根节点
		while ((type = parser.next()) != XmlPullParser.START_TAG
				&& type != XmlPullParser.END_DOCUMENT) {
		}

		if (type != XmlPullParser.START_TAG) {
			throw new InflateException(parser.getPositionDescription()
					+ ": No start tag found!");
		}

		final String name = parser.getName();
		/**
		 * temp是这个xml里的根视图
		 */
		View temp = createViews(name, parser, null);
		rInflate(parser, temp);
		if (root == null)
			result = temp;
		return result;
	}

	/**
	 * 用递归方式实例化view以及子view
	 */
	private void rInflate(XmlPullParser parser, View parent)
			throws XmlPullParserException, IOException {

		final int depth = parser.getDepth();
		int type;

		while (((type = parser.next()) != XmlPullParser.END_TAG || parser
				.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {

			if (type != XmlPullParser.START_TAG) {
				continue;
			}

			final String name = parser.getName();
			final View view = createViews(name, parser, parent);
			if (parent != null && parent instanceof ViewGroup) {
				final ViewGroup viewGroup = (ViewGroup) parent;
				rInflate(parser, view);
				if (view != null)
					viewGroup.addView(view);
			}
		}
	}

	/**
	 * 创建view
	 * 
	 * @param name
	 * @param parser
	 * @param parent
	 * @return
	 */
	private View createViews(String name, XmlPullParser parser, View parent) {
		View view = null;
		if (name.equalsIgnoreCase("LinearLayout")) {
			// 封装LinearLayout
			view = new LinearLayoutParse(mContext, this).createLinearLayout(
					parser, parent);
		} else if (name.equalsIgnoreCase("RelativeLayout")) {
			// 封装RelativeLayout
			view = new RelativeLayoutParse(mContext, this)
					.createRelativeLayout(parser, parent);
		} else if (name.equalsIgnoreCase("TextView")) {
			// 封装TextView
			view = new TextViewParse(mContext, this).createTextView(parser,
					parent);
		} else if (name.equalsIgnoreCase("ImageView")) {
			// 封装ImageView
			view = new ImageViewParse(mContext, this).createImageView(parser,
					parent);
		} else if (name.equalsIgnoreCase("Button")) {
			// 封装Button
			view = new ButtonParse(mContext, this).createButton(parser, parent);
		} else if (name.equalsIgnoreCase("View")) {
			// 封装View
			view = new ViewParse(mContext, this).createView(parser, parent);
		} else if (name.equalsIgnoreCase("IndexHeadCircularViewPager")) {
			// 封装ImageCircularViewPager
			view = new IndexHeadCircularViewPagerParse(mContext, this)
					.createIndexHeadCircularViewPager(parser, parent);
		} else if (name.equalsIgnoreCase("AtlasViewPager")) {
			view = new AtlasViewPagerParse(mContext, this).createViewPager(
					parser, parent);
		} else if (name.equals("LoadingImage")) {
			// 封装LoadingImage
			view = new LoadingImageParse(mContext, this).createLoadingImage(
					parser, parent);
		} else if (name.equals("FullScreenVideoView")) {
			// 封装FullScreenVideoView
			view = new FullScreenVideoViewParse(mContext, this)
					.createVideoView(parser, parent);
		} else if (name.equals("JianBianIndexItem")) {
			// 封装渐变背景
			view = new JianBianViewParse(mContext, this).creatJianBian(parser,
					parent);
		} else if (listener != null) {
			view = listener.fecthView(name, parser, parent);
		}
		return view;
	}

	/**
	 * 设置padding属性
	 * 
	 * @param atts
	 * @param value
	 * @param padding
	 * @return
	 */
	public int[] parsePadding(String atts, String value, int[] padding) {
		if (atts.equalsIgnoreCase("padding")) {
			for (int i = 0; i < 4; i++) {
				padding[i] = getExpressionResult(value);
			}
		} else if (atts.equalsIgnoreCase("paddingLeft")) {
			padding[0] = getExpressionResult(value);
		} else if (atts.equalsIgnoreCase("paddingTop")) {
			padding[1] = getExpressionResult(value);
		} else if (atts.equalsIgnoreCase("paddingRight")) {
			padding[2] = getExpressionResult(value);
		} else if (atts.equalsIgnoreCase("paddingBottom")) {
			padding[3] = getExpressionResult(value);
		}
		return padding;
	}

	/**
	 * 设置margin值
	 * 
	 * @param atts
	 * @param value
	 * @param margin
	 * @return
	 */
	public int[] parseMargin(String atts, String value, int[] margin) {
		if (atts.equalsIgnoreCase("layout_margin")) {
			for (int i = 0; i < 4; i++) {
				margin[i] = getExpressionResult(value);
			}
		} else if (atts.equalsIgnoreCase("layout_marginLeft")) {
			margin[0] = getExpressionResult(value);
		} else if (atts.equalsIgnoreCase("layout_marginTop")) {
			margin[1] = getExpressionResult(value);
		} else if (atts.equalsIgnoreCase("layout_marginRight")) {
			margin[2] = getExpressionResult(value);
		} else if (atts.equalsIgnoreCase("layout_marginBottom")) {
			margin[3] = getExpressionResult(value);
		}
		return margin;
	}

	/**
	 * 获取控件大小
	 * 
	 * @param atts
	 * @param value
	 * @param xy
	 * @return
	 */
	public int[] parseWidthAndHeight(String atts, String value, int[] xy) {
		if (atts.equalsIgnoreCase("layout_width")) {
			xy[0] = getSize(value);
		} else if (atts.equalsIgnoreCase("layout_height")) {
			xy[1] = getSize(value);
		}
		return xy;
	}

	/**
	 * 解析背景
	 * 
	 * @param atts
	 * @param value
	 * @param view
	 */
	public void parseBackground(String atts, String value, View view) {
		if (atts.equalsIgnoreCase("background")) {
			value = getPicUrl(value);
			V.setViewBack(view, value);
		} else if (atts.equalsIgnoreCase("src")) {
			value = getPicUrl(value);
			if (value.contains("placeholder"))
				view.setTag(R.id.img_placeholder, value);
			V.setImage(view, value);
		} else if (atts.equalsIgnoreCase("select_bg")) {
			value = getPicUrl(value);
			view.setTag(R.id.select_bg, value);
			checkSelectView.add(view);
		} else if (atts.equalsIgnoreCase("unselect_bg")) {
			value = getPicUrl(value);
			view.setTag(R.id.unselect_bg, value);
		} else if (atts.equalsIgnoreCase("nine_patch_img")) {
			value = getPicUrl(value);
			view.setTag(R.id.nine_patch_img, value);
			ninePatchViewList.add(view);
		}
	}

	/**
	 * 解析选中状态的字体颜色
	 * 
	 * @param atts
	 * @param value
	 * @param view
	 */
	public void parseSelectColor(String atts, String value, TextView view) {
		if (atts.equalsIgnoreCase("select_color")) {
			view.setTag(R.id.select_color, value);
			checkSelectView.add(view);
		} else if (atts.equalsIgnoreCase("unselect_color")) {
			view.setTag(R.id.unselect_color, value);
		}
	}

	/**
	 * 解析gravity
	 * 
	 * @param gravity
	 */
	public int parseGravity(String value) {
		int gravity = -1;
		if (TextUtils.isEmpty(value))
			return gravity;
		String[] gravityArr = value.split("\\|");
		if (gravityArr == null || gravityArr.length == 0)
			return gravity;
		for (String str : gravityArr) {
			int g = -1;
			if (str.equalsIgnoreCase("center")) {
				g = Gravity.CENTER;
			} else if (str.equalsIgnoreCase("center_vertical")) {
				g = Gravity.CENTER_VERTICAL;
			} else if (str.equalsIgnoreCase("center_horizontal")) {
				g = Gravity.CENTER_HORIZONTAL;
			} else if (str.equalsIgnoreCase("left")) {
				g = Gravity.LEFT;
			} else if (str.equalsIgnoreCase("top")) {
				g = Gravity.TOP;
			} else if (str.equalsIgnoreCase("right")) {
				g = Gravity.RIGHT;
			} else if (str.equalsIgnoreCase("bottom")) {
				g = Gravity.BOTTOM;
			}
			if (g == -1)
				continue;
			if (gravity == -1)
				gravity = g;
			else
				gravity = gravity | g;
		}
		return gravity;
	}

	/**
	 * 解析与父view对齐方式
	 * 
	 * @param atts
	 * @param value
	 * @param params
	 */
	public void parseAlignParent(String atts, String value,
			RelativeLayout.LayoutParams params) {
		if (atts.equalsIgnoreCase("layout_alignParentRight")) {
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		} else if (atts.equalsIgnoreCase("layout_alignParentLeft")) {
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		} else if (atts.equalsIgnoreCase("layout_alignParentTop")) {
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		} else if (atts.equalsIgnoreCase("layout_alignParentBottom")) {
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		} else if (atts.equalsIgnoreCase("layout_centerInParent")) {
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
		} else if (atts.equalsIgnoreCase("layout_centerHorizontal")) {
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		} else if (atts.equalsIgnoreCase("layout_centerVertical")) {
			params.addRule(RelativeLayout.CENTER_VERTICAL);
		}
	}

	/**
	 * 解析与其他控件的对齐方式
	 * 
	 * @param atts
	 * @param value
	 * @param params
	 */
	public void parseAlignAnchor(String atts, String value,
			RelativeLayout.LayoutParams params) {
		if (atts.equalsIgnoreCase("layout_alignLeft")) {
			int anchor = getIdByWidgetName(value);
			if (anchor > 0)
				params.addRule(RelativeLayout.ALIGN_LEFT, anchor);
		} else if (atts.equalsIgnoreCase("layout_alignTop")) {
			int anchor = getIdByWidgetName(value);
			if (anchor > 0)
				params.addRule(RelativeLayout.ALIGN_TOP, anchor);
		} else if (atts.equalsIgnoreCase("layout_alignRight")) {
			int anchor = getIdByWidgetName(value);
			if (anchor > 0)
				params.addRule(RelativeLayout.ALIGN_RIGHT, anchor);
		} else if (atts.equalsIgnoreCase("layout_alignBottom")) {
			int anchor = getIdByWidgetName(value);
			if (anchor > 0)
				params.addRule(RelativeLayout.ALIGN_BOTTOM, anchor);
		}

		else if (atts.equalsIgnoreCase("layout_above")) {
			int anchor = getIdByWidgetName(value);
			if (anchor > 0)
				params.addRule(RelativeLayout.ABOVE, anchor);
		} else if (atts.equalsIgnoreCase("layout_below")) {
			int anchor = getIdByWidgetName(value);
			if (anchor > 0)
				params.addRule(RelativeLayout.BELOW, anchor);
		} else if (atts.equalsIgnoreCase("layout_toRightOf")) {
			int anchor = getIdByWidgetName(value);
			if (anchor > 0)
				params.addRule(RelativeLayout.RIGHT_OF, anchor);
		} else if (atts.equalsIgnoreCase("layout_toLeftOf")) {
			int anchor = getIdByWidgetName(value);
			if (anchor > 0)
				params.addRule(RelativeLayout.LEFT_OF, anchor);
		}
	}

	/**
	 * 解析id
	 * 
	 * @param atts
	 * @param value
	 * @param view
	 */
	public void parseId(String atts, String value, View view) {
		if (atts.equalsIgnoreCase("id")) {
			String name = parseIdName(value);
			if (!TextUtils.isEmpty(name)) {
				int id = GenerateViewId.generateViewId();
				view.setId(id);
				idsMap.put(name, id);
			}
		}
	}

	/**
	 * 解析visibility
	 * 
	 * @param atts
	 * @param value
	 * @param view
	 */
	public void parseVisibility(String atts, String value, View view) {
		if (atts.equalsIgnoreCase("visibility")) {
			if (value.equalsIgnoreCase("visible")) {
				view.setVisibility(View.VISIBLE);
			} else if (value.equalsIgnoreCase("invisible")) {
				view.setVisibility(View.INVISIBLE);
			} else if (value.equalsIgnoreCase("gone")) {
				view.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 解析最小长宽
	 * 
	 * @param atts
	 * @param value
	 * @param view
	 */
	public void parseMin(String atts, String value, View view) {
		if (atts.equalsIgnoreCase("minWidth")) {
			view.setMinimumWidth(getExpressionResult(value));
		} else if (atts.equalsIgnoreCase("minHeight")) {
			view.setMinimumHeight(getExpressionResult(value));
		}
	}

	/**
	 * 解析作用域
	 * 
	 * @param atts
	 * @param value
	 * @param view
	 */
	public void parseFunction(String atts, String value, View view) {
		if (atts.equals("function")) {
			map.put(value, view);
			if (value.contains(FunctionXML.SPLIT)) {
				// eg:image@0
				String[] arr = value.split(FunctionXML.SPLIT);
				if (arr != null && arr.length == 2) {
					int index = ParseUtil.stoi(arr[1], -1);
					if (index != -1) {
						if (!gridMap.containsKey(arr[0])) {
							gridMap.put(arr[0], new ArrayList<GridItem>());
						}
						gridMap.get(arr[0]).add(new GridItem(index, view));
					}
				}
			}
		}
	}

	/**
	 * 解析是否可点击
	 * 
	 * @param atts
	 * @param value
	 * @param view
	 */
	public void parseClick(String atts, String value, View view) {
		if (atts.equals("click")) {
			clickViewList.add(view);
			if (!TextUtils.isEmpty(value) && !TextUtils.equals(value, "true")) {
				view.setTag(R.id.click, value);
			}
			if (value.contains(FunctionXML.SPLIT)) {
				// eg:true@0
				String[] arr = value.split(FunctionXML.SPLIT);
				if (arr != null && arr.length == 2) {
					int index = ParseUtil.stoi(arr[1], -1);
					if (index != -1) {
						clickGridViewList.add(new GridItem(index, view));
					}
				}
			}
		}
	}

	/**
	 * 解析date_format
	 * 
	 * @param atts
	 * @param value
	 * @param view
	 */
	public void parseDataFormat(String atts, String value, View view) {
		if (atts.equals("date_format")) {
			view.setTag(R.id.date_format, value);
		}
		if (atts.equals("date_format_language")) {
			view.setTag(R.id.date_format_language, value);
		}
	}

	/**
	 * 解析dot
	 * 
	 * @param atts
	 * @param value
	 * @param view
	 */
	public void parseDot(String atts, String value, View view) {
		if (atts.equals("dot_size")) {
			view.setTag(R.id.dot_size, getSize(value));
		}
	}

	/**
	 * 
	 * @param lp
	 * @param rp
	 * @param parent
	 * @return
	 */
	public boolean isLinearLayoutParent(LinearLayout.LayoutParams lp,
			RelativeLayout.LayoutParams rp, View parent) {
		if (parent == null || !(parent instanceof ViewGroup))
			return false;
		if (parent instanceof RelativeLayout) {
			rp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			return false;
		} else if (parent instanceof LinearLayout) {
			lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			return true;
		}
		return false;
	}

	/**
	 * 获取大小
	 * 
	 * @param value
	 * @return
	 */
	private int getSize(String value) {
		if (value.equalsIgnoreCase("match_parent")
				|| value.equalsIgnoreCase("fill_parent"))
			return -1;
		if (value.equalsIgnoreCase("wrap_content"))
			return -2;
		return getExpressionResult(value);
	}

	/**
	 * 获取id实际name
	 * 
	 * @param name
	 * @return
	 */
	private String parseIdName(String name) {
		if (!TextUtils.isEmpty(name)) {
			String[] arr = name.split("id/");
			if (arr.length == 2)
				name = arr[1];
		}
		return name;
	}

	/**
	 * 根据控件名称获取其id
	 * 
	 * @param name
	 * @return
	 */
	private int getIdByWidgetName(String name) {
		name = parseIdName(name);
		if (TextUtils.isEmpty(name)) {
			return 0;
		}
		if (!ParseUtil.mapContainsKey(idsMap, name))
			return 0;
		return idsMap.get(name);
	}

	/**
	 * 获取图片绝对地址
	 * 
	 * @param pic
	 */
	private String getPicUrl(String pic) {
		if (TextUtils.isEmpty(pic) || !pic.startsWith("uri://"))
			return pic;
		String arr[] = pic.split("uri://");
		if (arr != null && arr.length == 2) {
			return host + arr[1];
		}
		return pic;
	}

	public HashMap<String, View> getMap() {
		return map;
	}

	public int getExpressionResult(String value) {
		return new Calc(value, metrics).evaluate();
	}

	public float getDpValue(String value) {
		if (TextUtils.isEmpty(value))
			return 0;
		if (value.contains("dp"))
			value = value.replace("dp", "");
		return ParseUtil.stof(value);
	}

}
