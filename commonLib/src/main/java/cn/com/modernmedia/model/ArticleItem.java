package cn.com.modernmedia.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.AdvList.AdvTracker;
import cn.com.modernmediaslate.model.Entry;

/**
 * 列表文章统一数据
 *
 * @author ZhuQiao
 */
public class ArticleItem extends Entry {
    private static final long serialVersionUID = 1L;
    @Expose
    @SerializedName("articleid")
    private int articleId = -1;// 文章id
    @Expose
    private long updateTime;
    @Expose
    @SerializedName("favtime")
    private String favTime = "";// ?文章收藏时间
    @Expose
    @SerializedName("isdelete")
    private int favDel = 0; // 文章的收藏状态，1表示已取消收藏
    private String title = "";// 文章title
    private String desc = "";// 描述
    private String subcat = "";// 标签
    private List<Picture> thumbList = new ArrayList<Picture>();// 文章缩略图url
    private String slateLink = "";// 首页跳转文章
    private String tag = "";// 栏目名称
    private String offset = "";
    private IndexProperty property = new IndexProperty();
    private List<PhonePageList> pageUrlList = new ArrayList<PhonePageList>();// 文章地址列表
    private String weburl = "";// 分享链接
    private List<Picture> picList = new ArrayList<Picture>();// 文章大图url
    private Position position = new Position();// 图片位置,1:title,2:列表缩略图
    private boolean showTitleBar = false;// 首页文章titlebar
    private boolean showMoreCat = false;// 显示更多子栏目或者独立栏目
    private List<String> slateLinkList = new ArrayList<String>();// iweekly视野栏目使用
    private String author = "";
    private String outline = "";
    private AdvSource advSource = null;// 广告
    private int isAdv = 0;// 1：图片广告，2：网页广告 4：视频广告
    private AdvTracker advTracker = null;// 广告统计
    private long inputtime;
    private String jsonObject = "";
    private String tagName = "";
    private String groupname = "";// 排序用
    private String apiTag = "";// 请求api使用的tag,区分多个tag合并情况
    private int bottomResId;// iweekly分享底部图片
    private DbData dbData = new DbData();
    private int appid;
    private String catName = "";// 中文tagname
    private String subtitle = "";
    private String createuser = "";
    private String modifyuser = "";
    private String fromtagname = "";// 电台页面用来获取来源栏目
    private List<ArticleItem> subArticleList = new ArrayList<ArticleItem>();
    private String html = ""; // html文本

    private int advId;
    private String parent = "";
    private List<Audio> audioList = new ArrayList<Audio>();// 音频列表

    // 3.0.0新增参数
    private String groupdisplayname = "";// 推荐首页用来显示文章item的text
    private int groupdisplaycolor;// 推荐首页用来显示文章item的标题颜色

    // 3.3.0 特刊栏目标示
    private int isTekan = 0;// 1：特刊文章

    // 3.4 专栏栏目聚合作者信息
    private ZhuanLanAuthor zhuanlanAuthor = null;

    private int freePage;// 电子书免费试读到的页数
    private int downloadPercent = -1;// pdf文件的下载进度

    public int getDownloadPercent() {
        return downloadPercent;
    }

    public void setDownloadPercent(int downloadPercent) {
        this.downloadPercent = downloadPercent;
    }

    public int getIsAdv() {
        return isAdv;
    }

    public void setIsAdv(int isAdv) {
        this.isAdv = isAdv;
    }

    public String getSubcat() {
        return subcat;
    }

    public void setSubcat(String subcat) {
        this.subcat = subcat;
    }

    public int getFreePage() {
        return freePage;
    }

    public void setFreePage(int freePage) {
        this.freePage = freePage;
    }

    public ZhuanLanAuthor getZhuanlanAuthor() {
        return zhuanlanAuthor;
    }

    public void setZhuanlanAuthor(ZhuanLanAuthor zhuanlanAuthor) {
        this.zhuanlanAuthor = zhuanlanAuthor;
    }

    public List<ArticleItem> getSubArticleList() {
        return subArticleList;
    }

    public void setSubArticleList(List<ArticleItem> subArticleList) {
        this.subArticleList = subArticleList;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public int getIsTekan() {
        return isTekan;
    }

    public void setIsTekan(int isTekan) {
        this.isTekan = isTekan;
    }

    public ArticleItem() {
        super();
    }

    public ArticleItem(int articleId, long updateTime, String favTime, int favDel, String title,
                       String desc, String slateLink, String tag, String offset, IndexProperty
                               property, String weburl, Position position, boolean showTitleBar,
                       boolean showMoreCat, String author, String outline, AdvSource advSource,
                       int isAdv, AdvTracker advTracker, long inputtime, String jsonObject,
                       String tagName, String groupname, String apiTag, int bottomResId, DbData
                               dbData, int appid, String subtitle, String createuser, String
                               modifyuser, int advId, String parent, String groupdisplayname,
                       String catname, int groupdisplaycolor, String fromtagname, int freePage,
                       String html,String subcat) {
        super();
        this.articleId = articleId;
        this.updateTime = updateTime;
        this.favTime = favTime;
        this.favDel = favDel;
        this.title = title;
        this.desc = desc;
        this.slateLink = slateLink;
        this.tag = tag;
        this.offset = offset;
        this.property = property;
        this.catName = catname;
        this.weburl = weburl;
        this.position = position;
        this.showTitleBar = showTitleBar;
        this.showMoreCat = showMoreCat;
        this.author = author;
        this.outline = outline;
        this.advSource = advSource;
        this.isAdv = isAdv;
        this.advTracker = advTracker;
        this.inputtime = inputtime;
        this.jsonObject = jsonObject;
        this.tagName = tagName;
        this.groupname = groupname;
        this.apiTag = apiTag;
        this.bottomResId = bottomResId;
        this.dbData = dbData;
        this.appid = appid;
        this.subtitle = subtitle;
        this.createuser = createuser;
        this.modifyuser = modifyuser;
        this.advId = advId;
        this.parent = parent;
        this.groupdisplayname = groupdisplayname;
        this.groupdisplaycolor = groupdisplaycolor;
        this.fromtagname = fromtagname;
        this.freePage = freePage;
        this.html = html;
        this.subcat = subcat;
    }

    public ArticleItem copy() {
        ArticleItem item = new ArticleItem(articleId, updateTime, favTime, favDel, title, desc,
                slateLink, tag, offset, property, weburl, position, showTitleBar, showMoreCat, author, outline, advSource, isAdv, advTracker, inputtime, jsonObject, tagName, groupname, apiTag, bottomResId, dbData, appid, subtitle, createuser, modifyuser, advId, parent, groupdisplayname, catName, groupdisplaycolor, fromtagname, freePage, html,subcat);
        item.getThumbList().addAll(thumbList);
        item.getPicList().addAll(picList);
        item.getPageUrlList().addAll(pageUrlList);
        item.getSlateLinkList().addAll(slateLinkList);
        return item;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public String getFromtagname() {
        return fromtagname;
    }

    public void setFromtagname(String fromtagname) {
        this.fromtagname = fromtagname;
    }

    public List<Audio> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<Audio> audioList) {
        this.audioList = audioList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<Picture> getPicList() {
        return picList;
    }

    public void setPicList(List<Picture> picList) {
        this.picList = picList;
    }

    public List<Picture> getThumbList() {
        return thumbList;
    }

    public void setThumbList(List<Picture> thumbList) {
        this.thumbList = thumbList;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isShowTitleBar() {
        return showTitleBar;
    }

    public void setShowTitleBar(boolean showTitleBar) {
        this.showTitleBar = showTitleBar;
    }

    public boolean isShowMoreCat() {
        return showMoreCat;
    }

    public void setShowMoreCat(boolean showMoreCat) {
        this.showMoreCat = showMoreCat;
    }

    public String getSlateLink() {
        return slateLink;
    }

    public void setSlateLink(String slateLink) {
        this.slateLink = slateLink;
    }

    public List<String> getSlateLinkList() {
        return slateLinkList;
    }

    public void setSlateLinkList(List<String> slateLinkList) {
        this.slateLinkList = slateLinkList;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getFavTime() {
        return favTime;
    }

    public void setFavTime(String favTime) {
        this.favTime = favTime;
    }

    public int getFavDel() {
        return favDel;
    }

    public void setFavDel(int favDel) {
        this.favDel = favDel;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getOutline() {
        return outline;
    }

    public void setOutline(String outline) {
        this.outline = outline;
    }

    public AdvSource getAdvSource() {
        return advSource;
    }

    public void setAdvSource(AdvSource advSource) {
        this.advSource = advSource;
    }

    public int isAdv() {
        return isAdv;
    }

    public void setAdv(int isAdv) {
        this.isAdv = isAdv;
    }

    public AdvTracker getAdvTracker() {
        return advTracker;
    }

    public void setAdvTracker(AdvTracker advTracker) {
        this.advTracker = advTracker;
    }

    public long getInputtime() {
        return inputtime;
    }

    public void setInputtime(long inputtime) {
        this.inputtime = inputtime;
    }

    public String getWeburl() {
        return weburl;
    }

    public void setWeburl(String weburl) {
        this.weburl = weburl;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public List<PhonePageList> getPageUrlList() {
        return pageUrlList;
    }

    public void setPageUrlList(List<PhonePageList> pageUrlList) {
        this.pageUrlList = pageUrlList;
    }

    public IndexProperty getProperty() {
        return property;
    }

    public void setProperty(IndexProperty property) {
        this.property = property;
    }

    public String getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(String jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getBottomResId() {
        return bottomResId;
    }

    public void setBottomResId(int bottomResId) {
        this.bottomResId = bottomResId;
    }

    public DbData getDbData() {
        return dbData;
    }

    public void setDbData(DbData dbData) {
        this.dbData = dbData;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getApiTag() {
        return apiTag;
    }

    public void setApiTag(String apiTag) {
        this.apiTag = apiTag;
    }

    public int getAppid() {
        return appid;
    }

    public void setAppid(int appid) {
        this.appid = appid;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getCreateuser() {
        return createuser;
    }

    public void setCreateuser(String createuser) {
        this.createuser = createuser;
    }

    public String getModifyuser() {
        return modifyuser;
    }

    public void setModifyuser(String modifyuser) {
        this.modifyuser = modifyuser;
    }

    public int getAdvId() {
        return advId;
    }

    public void setAdvId(int advId) {
        this.advId = advId;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getGroupdisplayname() {
        return groupdisplayname;
    }

    public void setGroupdisplayname(String groupdisplayname) {
        this.groupdisplayname = groupdisplayname;
    }

    public int getGroupdisplaycolor() {
        return groupdisplaycolor;
    }

    public void setGroupdisplaycolor(int groupdisplaycolor) {
        this.groupdisplaycolor = groupdisplaycolor;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    /**
     * 转换成分享的对象
     *
     * @param galleryIndex
     * @return
     */
    public ArticleItem convertToShare(int galleryIndex) {
        if (galleryIndex == -1) return this;
        if (property.getType() == 2 && pageUrlList.size() > galleryIndex) {
            // TODO 图集
            ArticleItem item = new ArticleItem();
            item.setArticleId(articleId);
            item.setTagName(tagName);
            item.setWeburl(weburl);
            item.setBottomResId(bottomResId);
            Picture picture = new Picture();
            picture.setUrl(pageUrlList.get(galleryIndex).getUrl());
            item.getPicList().add(picture);
            item.setTitle(pageUrlList.get(galleryIndex).getTitle());
            item.setDesc(pageUrlList.get(galleryIndex).getDesc());
            return item;
        }
        return this;
    }

    /**
     * 焦点图片位置信息
     *
     * @author ZhuQiao
     */
    public static class Position extends Entry {
        private static final long serialVersionUID = 1L;
        private int id = -1;// position_id: 1 ：首页header滚动栏 ； 2 ：list
        /**
         * 对应的模板.0.titlebar(客户端自己加);1小图文字;2.无图文字;3.大图文字;4.广告(客户端自己加);5.跑马灯(
         * 客户端自己加);
         * <p/>
         * 3.0.0 (0.titlebar(客户端自己加,包括栏目名称或者date显示); 6.广告模板; 7.跑马灯) 1. 小图; 2.
         * 组图; 3. 大图＋标题+描述; 4. 大图＋标题; 5. 横版组图模式(渐变色背景);
         * <p/>
         * 3.4.0 (8.专栏作者titlebar)
         * <p>
         * 4.0.0
         * 101：广告  102：跑马灯 103： 专栏作者
         * <p>
         * 4.6.0
         * 9.商品文章
         */
        private int style = 1;
        /**
         * style5 对应的渐变色
         */
        private int fromColor;
        private int toColor;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getStyle() {
            return style;
        }

        public void setStyle(int style) {
            this.style = style;
        }

        public int getFromColor() {
            return fromColor;
        }

        public void setFromColor(int fromColor) {
            this.fromColor = fromColor;
        }

        public int getToColor() {
            return toColor;
        }

        public void setToColor(int toColor) {
            this.toColor = toColor;
        }
    }

    /**
     * 文章url
     *
     * @author zhuqiao
     */
    public static class PhonePageList extends Entry {
        private static final long serialVersionUID = 1L;
        private String url = "";
        private String title = "";
        private String desc = "";
        private String uri = "";

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

    }

    public static class IndexProperty extends Entry {
        private static final long serialVersionUID = 1L;
        private int level = 0; // 文章阅读权限,每个用户都有一个level权限，用户的level大于或者等于文章的level时，才能阅读该文章
        private int type;// 1.普通文章；2.组图文章；3.视频文章；4.组合文章；5.动态文章；6.链接文章；7.音频文章; 11.专题列表文章; 13.商品文章
        private int havecard = 1; // 文章是否允许添加卡片
        private int scrollHidden = 0;// 滑动时是否隐藏该文章。0.否。1.是
        private int hasvideo;// 是否显示视频icon
        private int isadv = -1;// 是否是软文

        public int getIsadv() {
            return isadv;
        }

        public void setIsadv(int isadv) {
            this.isadv = isadv;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getHavecard() {
            return havecard;
        }

        public void setHavecard(int havecard) {
            this.havecard = havecard;
        }

        public int getScrollHidden() {
            return scrollHidden;
        }

        public void setScrollHidden(int scrollHidden) {
            this.scrollHidden = scrollHidden;
        }

        public int getHasvideo() {
            return hasvideo;
        }

        public void setHasvideo(int hasvideo) {
            this.hasvideo = hasvideo;
        }

    }

    public static class Picture extends Entry {
        private static final long serialVersionUID = 1L;
        private String url = "";
        private String bigimgurl = "";// iweekly视野使用
        private String videolink = "";// 视频
        private int autoplay = 0;//1：自动播放
        private String desc = "";// 描述

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public int getAutoplay() {
            return autoplay;
        }

        public void setAutoplay(int autoplay) {
            this.autoplay = autoplay;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getBigimgurl() {
            return bigimgurl;
        }

        public void setBigimgurl(String bigimgurl) {
            this.bigimgurl = bigimgurl;
        }

        public String getVideolink() {
            return videolink;
        }

        public void setVideolink(String videolink) {
            this.videolink = videolink;
        }

    }

    /**
     * 音频文章model
     *
     * @author lusiyuan
     */
    public static class Audio extends Entry {

        private static final long serialVersionUID = 1L;
        public String url = "";
        public String duration = "";
        public int size;
        public String resId = "";
        private int count; // 播放次数

        public String getResId() {
            return resId;
        }

        public void setResId(String resId) {
            this.resId = resId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    /**
     * 专栏栏目聚合作者信息
     *
     * @author Eva.
     */
    public static class ZhuanLanAuthor extends Entry {
        private static final long serialVersionUID = 1L;
        private String id = "";
        private String name = "";
        private int color;
        private String desc = "";
        private String picture = "";

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

    }

    /**
     * 数据迁移使用
     *
     * @author jiancong
     */
    public static class DbData extends Entry {
        private static final long serialVersionUID = 1L;
        private String uid = ""; // 用户id，收藏数据迁移用
        private int success;
        private String favtime;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public int getSuccess() {
            return success;
        }

        public void setSuccess(int success) {
            this.success = success;
        }

        public String getFavtime() {
            return favtime;
        }

        public void setFavtime(String favtime) {
            this.favtime = favtime;
        }
    }

}
