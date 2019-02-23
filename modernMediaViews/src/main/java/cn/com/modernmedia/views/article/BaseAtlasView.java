package cn.com.modernmedia.views.article;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.adapter.MyPagerAdapter.OnItemClickListener;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.PhonePageList;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.adapter.AtlasAdapter;
import cn.com.modernmedia.views.model.TemplateAtlas;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmedia.views.xmlparse.article.XMLDataSetForAtlas;
import cn.com.modernmedia.widget.AtlasViewPager;
import cn.com.modernmedia.widget.CommonAtlasView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 图集
 *
 * @author ZhuQiao
 */
public class BaseAtlasView extends CommonAtlasView {
    protected Context mContext;
    protected AtlasViewPager pager;
    private List<ImageView> dots = new ArrayList<ImageView>();
    private List<PhonePageList> list;
    private AtlasAdapter adapter;
    private TemplateAtlas template;
    private XMLDataSetForAtlas dataSet;
    private ArticleItem item;

    private NotifyArticleDesListener listener = new NotifyArticleDesListener() {

        @Override
        public void updateDes(int position) {
            if (list == null || list.size() < position + 1) return;
            dataSet.setData(list.get(position), item);
            dataSet.anim(list, position);
            for (int i = 0; i < dots.size(); i++) {
                if (i == position % list.size()) {
                    dots.get(i).setImageResource(R.drawable.dot_active);
                } else {
                    dots.get(i).setImageResource(R.drawable.dot);
                }
            }
        }

        @Override
        public void updatePage(int state) {
        }
    };

    public BaseAtlasView(Context context, boolean isCurrApp) {
        super(context);
        mContext = context;
        init(isCurrApp);
    }

    protected void init(boolean isCurrApp) {
        this.addView(LayoutInflater.from(mContext).inflate(R.layout.atlas, null));
        initProcess();
        RelativeLayout atlas = (RelativeLayout) findViewById(R.id.atlas_layout);
        template = ParseProperties.getInstance(mContext).parseAtlas(isCurrApp);
        XMLParse xmlParse = new XMLParse(mContext, null);
        atlas.addView(xmlParse.inflate(template.getData(), null, ""));
        dataSet = xmlParse.getDataSetForAtlas();
        pager = dataSet.getViewPager();
    }

    @Override
    protected void setValuesForWidget(ArticleItem item) {
        this.item = item;
        list = item.getPageUrlList();
        if (ParseUtil.listNotNull(list)) {
            adapter = new AtlasAdapter(mContext, template);
            adapter.setData(list);
            adapter.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(View view, int position) {
                    if (!TextUtils.isEmpty(list.get(position).getUri()))

                        UriParse.clickSlate(mContext, list.get(position).getUri(), new Entry[]{new ArticleItem()}, BaseAtlasView.this);
                }
            });
            pager.setAdapter(adapter);
            pager.setValue(list.size());
            pager.setListener(listener);
            dataSet.dot(list, dots);
            dataSet.initAnim(list);
        }
    }

    @Override
    public int getCurrentIndex() {
        return adapter == null ? -1 : adapter.getCurr();
    }

    @Override
    protected void reLoad() {
        super.reLoad();
    }

    @Override
    public AtlasViewPager getAtlasViewPager() {
        return pager;
    }
}
