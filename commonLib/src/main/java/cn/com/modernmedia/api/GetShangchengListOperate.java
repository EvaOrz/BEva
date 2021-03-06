package cn.com.modernmedia.api;

import android.graphics.Color;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.IndexProperty;
import cn.com.modernmedia.model.ArticleItem.PhonePageList;
import cn.com.modernmedia.model.ArticleItem.Picture;
import cn.com.modernmedia.model.ArticleItem.Position;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 获取商城list数据
 * Created by Eva. on 2017/8/17.
 */

public class GetShangchengListOperate extends BaseOperate {

    private String tags;
    protected TagArticleList articleList = new TagArticleList();

    public GetShangchengListOperate(String tags) {
        this.tags = tags;
    }

    public Entry getDatas() {
        return articleList;
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        if (jsonObject == null) return;
        JSONArray array = jsonObject.optJSONArray("articletag");
        List<ArticleItem> datas = new ArrayList<>();
        if (!isNull(array)) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.optJSONObject(i);
                datas.add(parseArtcle(object));
            }
        }
        articleList.setArticleList(datas);
    }

    /**
     * 解析文章详情
     *
     * @param jsonObject
     * @return
     */
    private ArticleItem parseArtcle(JSONObject jsonObject) {
        ArticleItem aa = new ArticleItem();

        JSONArray artcleArr = jsonObject.optJSONArray("article");
        aa.setTagName(jsonObject.optString("tagname"));
        List<ArticleItem> das = new ArrayList<>();
        if (!isNull(artcleArr)) {
            for (int i = 0; i < artcleArr.length(); i++) {
                JSONObject obj = artcleArr.optJSONObject(i);
                if (isNull(obj)) continue;
                das.add(parseArticleItem(obj));

            }
            aa.setSubArticleList(das);
        }
        return aa;
    }


    public ArticleItem parseArticleItem(JSONObject obj) {
        ArticleItem item = new ArticleItem();
        item.setJsonObject(obj.toString());

        item.setIsTekan(obj.optInt("is_tekan"));
        item.setArticleId(obj.optInt("articleid", -1));
        item.setTitle(obj.optString("title", ""));
        item.setDesc(obj.optString("desc", ""));

        item.setOffset(obj.optString("offset", ""));
        JSONArray links = obj.optJSONArray("links");
        if (!isNull(links)) {
            item.setSlateLinkList(parseLinks(links));
            if (ParseUtil.listNotNull(item.getSlateLinkList()))
                item.setSlateLink(item.getSlateLinkList().get(0));
        }
        item.setAuthor(obj.optString("author", ""));
        item.setOutline(obj.optString("outline", ""));
        item.setInputtime(obj.optLong("inputtime"));
        item.setUpdateTime(obj.optLong("updatetime"));
        item.setWeburl(obj.optString("weburl", ""));
        item.setProperty(parseProperty(obj.optJSONObject("property")));
        item.setGroupname(obj.optString("groupname"));
        item.setTagName(item.getGroupname());
        item.setCatName(obj.optString("catname"));
        item.setSubtitle(obj.optString("subtitle"));
        item.setCreateuser(obj.optString("createuser"));
        item.setModifyuser(obj.optString("modifyuser"));
        item.setFreePage(obj.optInt("freepage",0));
        item.setFromtagname(obj.optString("fromtagname"));// iweekly电台栏目新增
        // 推荐首页title text
        item.setGroupdisplayname(obj.optString("groupdisplayname"));
        // 文章url地址解析
        JSONArray pageUrlList = obj.optJSONArray("phonepagelist");
        if (!isNull(pageUrlList)) {
            item.setPageUrlList(parsePageUrl(pageUrlList));
        }
        // 大图解析
        JSONArray picArr = obj.optJSONArray("picture");
        if (!isNull(picArr)) {
            item.setPicList(parsePicture(picArr));
        }
        // 列表图解析
        JSONArray thumbArr = obj.optJSONArray("thumb");
        if (!isNull(thumbArr)) {
            item.setThumbList(parseThumb(thumbArr));
        }
        // 图片位置
        item.setPosition(parsePosition(obj.optJSONObject("position")));
        articleList.setEndOffset(item.getOffset());

        // 音频解析
        JSONArray audioArr = obj.optJSONArray("audio");
        if (!isNull(audioArr)) {
            item.setAudioList(parseAudio(audioArr));
        }

        JSONArray subListArr = obj.optJSONArray("subArticleList");


        return item;
    }

    /**
     * 解析position
     *
     * @param obj
     * @return
     */
    private Position parsePosition(JSONObject obj) {
        Position position = new Position();
        if (!isNull(obj)) {
            position.setId(obj.optInt("positionid", -1));
            position.setStyle(obj.optInt("style", 1));
            position.setFromColor(transformColor(obj.optString("fromColor")));
        }
        return position;
    }

    /**
     * 把服务器返回的RGB转换成可用的色值
     *
     * @param result eg:235,3,13
     * @return
     */
    private int transformColor(String result) {
        int red = 0, green = 0, blue = 0;
        if (!TextUtils.isEmpty(result)) {
            String colorArr[] = result.split(",");
            if (colorArr.length == 3) {
                try {
                    red = Integer.parseInt(colorArr[0]);
                    green = Integer.parseInt(colorArr[1]);
                    blue = Integer.parseInt(colorArr[2]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                return Color.rgb(red, green, blue);
            }
        }
        return 0;
    }

    /***
     * 解析音频
     */
    private List<ArticleItem.Audio> parseAudio(JSONArray array) {
        List<ArticleItem.Audio> audioList = new ArrayList<ArticleItem.Audio>();
        JSONObject object;
        for (int i = 0; i < array.length(); i++) {
            object = array.optJSONObject(i);
            if (isNull(object)) continue;
            ArticleItem.Audio audio = new ArticleItem.Audio();
            audio.setUrl(object.optString("url"));
            audio.setDuration(object.optString("duration"));
            audio.setSize(object.optInt("size"));
            audio.setCount(object.optInt("count"));
            audio.setResId(object.optString("id"));
            audioList.add(audio);
        }
        return audioList;
    }

    /**
     * 解析缩略图
     *
     * @param array
     * @return
     */
    private List<Picture> parseThumb(JSONArray array) {
        List<Picture> pictureList = new ArrayList<Picture>();
        JSONObject object;
        for (int i = 0; i < array.length(); i++) {
            object = array.optJSONObject(i);
            if (isNull(object)) continue;
            Picture picture = new Picture();
            picture.setUrl(object.optString("url"));
            pictureList.add(picture);
        }
        return pictureList;
    }


    /**
     * 解析图片
     *
     * @param array
     * @return
     */
    private List<PhonePageList> parsePageUrl(JSONArray array) {
        List<PhonePageList> pageUrlList = new ArrayList<PhonePageList>();
        JSONObject object;
        PhonePageList page;
        int length = array.length();
        for (int i = 0; i < length; i++) {
            object = array.optJSONObject(i);
            if (isNull(object)) continue;
            page = new PhonePageList();
            page.setUrl(object.optString("url"));
            page.setTitle(object.optString("title"));
            page.setDesc(object.optString("desc"));
            page.setUri(object.optString("link"));
            pageUrlList.add(page);
        }
        return pageUrlList;
    }

    /**
     * 解析图片
     *
     * @param array
     * @return
     */
    private List<Picture> parsePicture(JSONArray array) {
        List<Picture> pictureList = new ArrayList<Picture>();
        JSONObject object;
        for (int i = 0; i < array.length(); i++) {
            object = array.optJSONObject(i);
            if (isNull(object)) continue;
            Picture picture = new Picture();
            picture.setUrl(object.optString("url"));
            picture.setVideolink(object.optString("videolink"));
            // iweekly视野使用
            picture.setBigimgurl(object.optString("bigimgurl"));
            pictureList.add(picture);
        }
        return pictureList;
    }

    /**
     * 解析Property
     *
     * @param obj
     * @return
     */
    private IndexProperty parseProperty(JSONObject obj) {
        IndexProperty property = new IndexProperty();
        if (!isNull(obj)) {
            property.setLevel(obj.optInt("level", 0));
            property.setType(obj.optInt("type", 1));
            property.setHavecard(obj.optInt("havecard", 1));
            property.setScrollHidden(obj.optInt("scrollHidden", 0));
            property.setHasvideo(obj.optInt("hasvideo", 0));
        }
        return property;
    }


    /**
     * 解析links，iweekly视野使用
     *
     * @param array
     * @return
     */
    private List<String> parseLinks(JSONArray array) {
        List<String> linkList = new ArrayList<String>();
        JSONObject object;
        for (int i = 0; i < array.length(); i++) {
            object = array.optJSONObject(i);
            if (isNull(object)) continue;
            linkList.add(object.optString("url"));
        }
        return linkList;
    }


    @Override
    protected void saveData(String data) {

    }

    @Override
    protected String getDefaultFileName() {
        return null;
    }

    @Override
    protected String getUrl() {
        return UrlMaker.getShangchengList(tags);
    }


}
