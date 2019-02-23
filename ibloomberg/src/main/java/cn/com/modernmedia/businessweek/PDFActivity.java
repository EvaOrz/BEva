package cn.com.modernmedia.businessweek;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.businessweek.jingxuan.pdf.DownPdfTask;
import cn.com.modernmedia.businessweek.jingxuan.pdf.EbookHistoryDB;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import es.voghdev.pdfviewpager.library.PDFViewPager;

import static cn.com.modernmedia.businessweek.R.id.pdf_gopay_button;

/**
 * PDF文档查看器
 * Created by Eva. on 17/1/4.
 */

public class PDFActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    //    private RemotePDFViewPager loadingViewPager;// 需要网络加载的页面
    private PDFViewPager fileViewPager;// 加载文件夹文件的页面
    private LinearLayout root, pdf_gopay;
    private boolean hasLevel = false;// 是否有阅读权限
    private TextView title, page, proText, payHint;
    private int currentPage, pageCount;
    private ArticleItem articleItem;
    private ProgressBar progressBar;
    private RelativeLayout loading;// 加载页面
    private int progress; // 加载进度百分比


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getSerializableExtra("pdf_article_item") != null)
            articleItem = (ArticleItem) getIntent().getSerializableExtra("pdf_article_item");

        setContentView(R.layout.activity_pdf);
        Log.e("获取pdf阅读进度", EbookHistoryDB.getInstance(this).getHistoryPage(articleItem.getArticleId()) + "");
        currentPage = EbookHistoryDB.getInstance(this).getHistoryPage(articleItem.getArticleId());
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        hasLevel();
        handler.sendEmptyMessage(0);
    }

    private void checktoPage(int page) {
        fileViewPager.setCurrentItem(page, true);
        showToast("已为您恢复到上次阅读位置");
    }

    /**
     * 是否有阅读电子书的权限
     *
     * @return
     */
    private void hasLevel() {
        hasLevel = articleItem.getProperty().getLevel() == 0 || SlateDataHelper.getLevelByType(this, articleItem.getProperty().getLevel());
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: // page
                    if (!hasLevel && currentPage > articleItem.getFreePage()) {
                        // 需要付费但是没权限
                        pdf_gopay.setVisibility(View.VISIBLE);
                        if (SlateDataHelper.getVipLevel(PDFActivity.this) == 1) {
                            payHint.setText("升级VIP进阶套餐");
                        } else {
                            payHint.setText("开通VIP进阶套餐");
                        }
                    } else {
                        pdf_gopay.setVisibility(View.GONE);
                        page.setText(currentPage + "/" + pageCount);
                    }

                    break;
                case 1:// 更新进度
                    progressBar.setProgress(progress);
                    proText.setText(progress + "%");
                    if (progress == 100) {
                        handler.sendEmptyMessage(100);
                    }
                    break;
                case 100:// PDF下载完成
                    fileViewPager = new PDFViewPager(PDFActivity.this, FileManager.getPdfFilePath(articleItem.getPageUrlList().get(0).getUrl()));
                    fileViewPager.setOnPageChangeListener(PDFActivity.this);
                    loading.setVisibility(View.GONE);
                    root.removeAllViewsInLayout();
                    root.addView(fileViewPager, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    pageCount = fileViewPager.getAdapter().getCount();
                    if (currentPage > 0) {
                        checktoPage(currentPage - 1);
                    } else currentPage = 1;
                    handler.sendEmptyMessage(0);
                    break;
            }
        }
    };

    private void initView() {
        findViewById(R.id.epub_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title = findViewById(R.id.epub_title);
        page = findViewById(R.id.pdf_page);
        pdf_gopay = findViewById(R.id.pdf_gopay);
        payHint = findViewById(pdf_gopay_button);
        payHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserPageTransfer.gotoShangchengActivity(PDFActivity.this, false);//开通或升级VIP
            }
        });
        loading = findViewById(R.id.pdf_loading);
        proText = findViewById(R.id.progress_text);
        progressBar = findViewById(R.id.pdf_down_process);
        progressBar.setIndeterminate(false);
        progressBar.setProgressDrawable(getResources().getDrawable(android.R.drawable.progress_horizontal));
        progressBar.setIndeterminateDrawable(getResources().getDrawable(android.R.drawable.progress_indeterminate_horizontal));

        if (!TextUtils.isEmpty(articleItem.getTitle())) title.setText(articleItem.getTitle());
        root = findViewById(R.id.remote_pdf_root);

        final String url = articleItem.getPageUrlList().get(0).getUrl();
        if (FileManager.ifHasPdfFilePath(url)) {
            handler.sendEmptyMessage(100);
        } else {
            final DownPdfTask downloadTask = new DownPdfTask(this, new DownPdfTask.DownLoadPdfLitener() {
                @Override
                public void onProgressUpdate(int p) {
                    progress = p;
                    handler.sendEmptyMessage(1);
                }
            });
            downloadTask.execute(url);
        }
    }


    /**
     * 页面处于stop状态时，记录阅读进度
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.e("缓存pdf文件page number", articleItem.getTitle() + " () " + currentPage);
        EbookHistoryDB.getInstance(this).addHistory(articleItem.getArticleId(), currentPage);

    }

    @Override
    public Activity getActivity() {
        return PDFActivity.this;
    }

    @Override
    public String getActivityName() {
        return PDFActivity.class.getName();
    }

    @Override
    public void reLoadData() {

    }

    @Override
    public void onPageSelected(int position) {
        currentPage = position + 1;
        handler.sendEmptyMessage(0);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}