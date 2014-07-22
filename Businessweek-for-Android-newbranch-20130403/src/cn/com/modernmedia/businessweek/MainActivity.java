package cn.com.modernmedia.businessweek;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.businessweek.widget.ColumnView;
import cn.com.modernmedia.businessweek.widget.FavView;
import cn.com.modernmedia.businessweek.widget.IndexView;
import cn.com.modernmedia.listener.NotifyAdapterListener;
import cn.com.modernmedia.listener.ScrollCallBackListener;
import cn.com.modernmedia.listener.SizeCallBackForButton;
import cn.com.modernmedia.model.Entry;
import cn.com.modernmedia.widget.AtlasViewPager;
import cn.com.modernmedia.widget.MainHorizontalScrollView;

/**
 * 主页
 * 
 * @author ZhuQiao
 * 
 */
public class MainActivity extends CommonMainActivity {
	private ColumnView columnView;// 栏目列表页
	private MainHorizontalScrollView scrollView;
	private Button columnButton, favButton;
	private IndexView indexView;
	private FavView favView;
	private List<NotifyAdapterListener> listenerList = new ArrayList<NotifyAdapterListener>();

	@Override
	protected void onResume() {
		super.onResume();
		if (indexView != null) {
			indexView.setAuto(true);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (indexView != null) {
			indexView.setAuto(false);
		}
	}

	@Override
	public void init() {
		setContentView(R.layout.main);
		columnView = (ColumnView) findViewById(R.id.main_column);
		scrollView = (MainHorizontalScrollView) findViewById(R.id.mScrollView);
		favView = (FavView) findViewById(R.id.main_fav);
		indexView = new IndexView(this);
		columnButton = indexView.getColumn();
		favButton = indexView.getFav();

		View leftView = new View(this);// 为了显示左边页面，设置透明区
		leftView.setBackgroundColor(Color.TRANSPARENT);
		View rightView = new View(this);
		rightView.setBackgroundColor(Color.TRANSPARENT);// 为了显示右边页面，设置透明区
		final View[] children = new View[] { leftView, indexView, rightView };
		scrollView.initViews(children, new SizeCallBackForButton(columnButton),
				columnView, favView);
		scrollView.setButtons(columnButton, favButton);
		scrollView.setListener(new ScrollCallBackListener() {

			@Override
			public void isShowIndex(boolean showIndex) {
				indexView.showCover(showIndex);
			}
		});
		columnButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				scrollView.clickButton(true);
			}
		});
		favButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				scrollView.clickButton(false);
			}
		});
	}

	@Override
	public void setDataForColumn(Entry entry) {
		columnView.setData(entry);
	}

	@Override
	public void setDataForIndex(Entry entry) {
		indexView.setData(entry);
	}

	@Override
	public MainHorizontalScrollView getScrollView() {
		return scrollView;
	}

	@Override
	public void gotoArticleActivity(int artcleId, int catId, boolean isIndex) {
		if (artcleId == -1)
			return;
		Intent intent = new Intent(this, ArticleActivity.class);
		intent.putExtra("ISSUE", getIssue());
		intent.putExtra("ARTICLE_ID", artcleId);
		intent.putExtra("CAT_ID", catId);
		intent.putExtra("IS_INDEX", isIndex);
		startActivityForResult(intent, REQUEST_CODE);
		overridePendingTransition(R.anim.right_in, R.anim.zoom_out);
	}

	@Override
	public void indexShowLoading(int flag) {
		if (flag == 0)
			indexView.disProcess();
		else if (flag == 1)
			indexView.showLoading();
		else if (flag == 2)
			indexView.showError();
	}

	@Override
	public void setIndexTitle(String name) {
		indexView.setTitle(name);
	}

	@Override
	protected void notifyRead() {
		for (NotifyAdapterListener listener : listenerList) {
			listener.notifyReaded();
		}
	}

	public void showChildCat(int parentId) {
		setColumnId(parentId + "");
		indexView.setDataForChild(getIssue(), parentId);
	}

	/**
	 * 给scrollview设置需要拦截的子scrollview
	 * 
	 * @param flag
	 *            0.只执行子scrollview;1.当子scrollview滑到头时，执行父scrollview；2。
	 *            清空子scrollview
	 * @param view
	 */
	public void setScrollView(int flag, View view) {
		if (flag == 0) {
			scrollView.setNeedsScrollView(view);
		} else if (flag == 1 && view instanceof AtlasViewPager) {
			scrollView.setAtlasViewPager((AtlasViewPager) view);
		} else if (flag == 2) {
			scrollView.setNeedsScrollView(null);
		} else {
			scrollView.setAtlasViewPager(null);
		}
	}

	public void addListener(NotifyAdapterListener listener) {
		listenerList.add(listener);
	}

	/**
	 * 通知更新栏目列表选中状态
	 * 
	 * @param catId
	 */
	public void notifyColumnAdapter(int catId) {
		for (NotifyAdapterListener listener : listenerList) {
			listener.nofitySelectItem(catId);
		}
	}

	@Override
	public String[] getFragmentTags() {
		return IndexView.TAGS;
	}

}
