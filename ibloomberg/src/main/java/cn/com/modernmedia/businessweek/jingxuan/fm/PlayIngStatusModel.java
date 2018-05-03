package cn.com.modernmedia.businessweek.jingxuan.fm;

import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmediaslate.model.Entry;

/**
 * Created by Eva. on 2017/9/21.
 */

public class PlayIngStatusModel extends Entry {
    private ArticleItem articleItem;
    private int isPlaying = 0;// 0：未播放，1：正在播放

    public ArticleItem getArticleItem() {
        return articleItem;
    }

    public void setArticleItem(ArticleItem articleItem) {
        this.articleItem = articleItem;
    }

    public int getIsPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(int isPlaying) {
        this.isPlaying = isPlaying;
    }
}
