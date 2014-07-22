package cn.com.modernmedia.businessweek;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.businessweek.widget.ArticleDetailItem;
import cn.com.modernmedia.businessweek.widget.AtlasView;
import cn.com.modernmedia.model.ArticleList.ArticleDetail;
import cn.com.modernmedia.widget.AtlasViewPager;
import cn.com.modernmedia.widget.CommonViewPager;

/**
 * ��������ҳ
 * 
 * @author ZhuQiao
 * 
 */
public class ArticleActivity extends CommonArticleActivity implements
		OnClickListener {
	private Button backBtn, favBtn, fontBtn, shareBtn;
	/**
	 * ��ViewPager�У������˼��ص�ǰҳ��������ص�ǰҳ������ҳ����������ʵ�ʿɲ��ɼ��������˵�ǰҳ��View���ǿɼ��ģ�
	 * ������ҳ��View���ǿɼ��ģ���������ʵ�ʿɲ��ɼ���
	 */
	private CommonViewPager viewPager;

	@Override
	protected void init() {
		setContentView(R.layout.article_activity);
		backBtn = (Button) findViewById(R.id.article_back_btn);
		favBtn = (Button) findViewById(R.id.article_fav_btn);
		fontBtn = (Button) findViewById(R.id.article_font_btn);
		shareBtn = (Button) findViewById(R.id.article_share_btn);
		viewPager = (CommonViewPager) findViewById(R.id.article_viewpager);
		viewPager.setOffscreenPageLimit(1);// ����Ԥ���أ�ֻ������һҳ

		backBtn.setOnClickListener(this);
		favBtn.setOnClickListener(this);
		fontBtn.setOnClickListener(this);
		shareBtn.setOnClickListener(this);
	}

	@Override
	protected CommonViewPager getViewPager() {
		return viewPager;
	}

	@Override
	protected void changeFavBtn(boolean isFavEd) {
		if (isFavEd)
			favBtn.setBackgroundResource(R.drawable.nav_faved);
		else
			favBtn.setBackgroundResource(R.drawable.nav_fav);
	}

	@Override
	protected void hideFont(boolean hide) {
		if (hide)
			fontBtn.setVisibility(View.GONE);
		else
			fontBtn.setVisibility(View.VISIBLE);
	}

	@Override
	protected View fetchView(ArticleDetail detail) {
		View view;
		String type = detail.getProperty().getType();
		if (type.equals("2")) {// ͼ��
			view = new AtlasView(this);
			((AtlasView) view).setData(detail, getIssue());
		} else {
			view = new ArticleDetailItem(this);
			((ArticleDetailItem) view).setData(detail);
			((ArticleDetailItem) view).changeFont();
		}
		return view;
	}

	@Override
	protected AtlasViewPager getAtlasViewPager(Object object) {
		return ((AtlasView) object).getAtlasViewPager();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.article_back_btn:
			if (!checkTime())
				break;
			finishAndAnim();
			break;
		case R.id.article_fav_btn:
			addFav();
			break;
		case R.id.article_font_btn:
			clickFont();
			break;
		case R.id.article_share_btn:
			showShare();
			break;
		default:
			break;
		}
	}

}
