package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmediaslate.model.Entry;

/**
 * @author: zhufei
 */
public class SpecialList extends Entry {
    /**
     * appid : 1
     * tagname : cat_1498
     * parent : app_1
     * haveChildren : 0
     * articleupdatetime : 1457075283
     * phoneColumnProperty : {"name":"付费特刊88888","ename":"tekan8888","cname":"付费特刊88888","color":"25,67,171","noColumn":1,"noMenuBar":1,"noLeftMenu":1,"positionNum":1,"isindex":0,"link":"","notinTodayExtension":0}
     * enablesubscribe : 0
     * defaultsubscribe : 0
     * isfix : 0
     * group : 12
     * catname : 付费特刊88888
     * publishstatus : 0
     * modifyuser :
     * updatetime : 1457075283
     * publishuser :
     * publishtime : 0
     * devices : 1,2
     * originalCatId : 1498
     * coloumnupdatetime : 1457075283
     * columntype : normal
     * advUpdateTime : 1456805463
     * isRadio : 0
     * type : 0
     * ispay : 0
     */

    private static final long serialVersionUID = 1L;
    private List<ArticletagEntity> articletag;

    public void setArticletag(List<ArticletagEntity> articletag) {
        this.articletag = articletag;
    }

    public List<ArticletagEntity> getArticletag() {
        return articletag;
    }

    public static class ArticletagEntity extends Entry {
        private int appid;
        private String tagname;
        private String parent;
        private int haveChildren;
        private int articleupdatetime;
        /**
         * name : 付费特刊88888
         * ename : tekan8888
         * cname : 付费特刊88888
         * color : 25,67,171
         * noColumn : 1
         * noMenuBar : 1
         * noLeftMenu : 1
         * positionNum : 1
         * isindex : 0
         * link :
         * notinTodayExtension : 0
         */

        private PhoneColumnPropertyEntity phoneColumnProperty = new PhoneColumnPropertyEntity();
        private int enablesubscribe;
        private int defaultsubscribe;
        private int isfix;
        private int group;
        private String catname;
        private int publishstatus;
        private String modifyuser;
        private int updatetime;
        private String publishuser;
        private int publishtime;
        private String devices;
        private String originalCatId;
        private int coloumnupdatetime;
        private String columntype;
        private String advUpdateTime;
        private int isRadio;
        private int type;
        private int ispay;

        public void setAppid(int appid) {
            this.appid = appid;
        }

        public void setTagname(String tagname) {
            this.tagname = tagname;
        }

        public void setParent(String parent) {
            this.parent = parent;
        }

        public void setHaveChildren(int haveChildren) {
            this.haveChildren = haveChildren;
        }

        public void setArticleupdatetime(int articleupdatetime) {
            this.articleupdatetime = articleupdatetime;
        }

        public void setPhoneColumnProperty(PhoneColumnPropertyEntity phoneColumnProperty) {
            this.phoneColumnProperty = phoneColumnProperty;
        }

        public void setEnablesubscribe(int enablesubscribe) {
            this.enablesubscribe = enablesubscribe;
        }

        public void setDefaultsubscribe(int defaultsubscribe) {
            this.defaultsubscribe = defaultsubscribe;
        }

        public void setIsfix(int isfix) {
            this.isfix = isfix;
        }

        public void setGroup(int group) {
            this.group = group;
        }

        public void setCatname(String catname) {
            this.catname = catname;
        }

        public void setPublishstatus(int publishstatus) {
            this.publishstatus = publishstatus;
        }

        public void setModifyuser(String modifyuser) {
            this.modifyuser = modifyuser;
        }

        public void setUpdatetime(int updatetime) {
            this.updatetime = updatetime;
        }

        public void setPublishuser(String publishuser) {
            this.publishuser = publishuser;
        }

        public void setPublishtime(int publishtime) {
            this.publishtime = publishtime;
        }

        public void setDevices(String devices) {
            this.devices = devices;
        }

        public void setOriginalCatId(String originalCatId) {
            this.originalCatId = originalCatId;
        }

        public void setColoumnupdatetime(int coloumnupdatetime) {
            this.coloumnupdatetime = coloumnupdatetime;
        }

        public void setColumntype(String columntype) {
            this.columntype = columntype;
        }

        public void setAdvUpdateTime(String advUpdateTime) {
            this.advUpdateTime = advUpdateTime;
        }

        public void setIsRadio(int isRadio) {
            this.isRadio = isRadio;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setIspay(int ispay) {
            this.ispay = ispay;
        }

        public int getAppid() {
            return appid;
        }

        public String getTagname() {
            return tagname;
        }

        public String getParent() {
            return parent;
        }

        public int getHaveChildren() {
            return haveChildren;
        }

        public int getArticleupdatetime() {
            return articleupdatetime;
        }

        public PhoneColumnPropertyEntity getPhoneColumnProperty() {
            return phoneColumnProperty;
        }

        public int getEnablesubscribe() {
            return enablesubscribe;
        }

        public int getDefaultsubscribe() {
            return defaultsubscribe;
        }

        public int getIsfix() {
            return isfix;
        }

        public int getGroup() {
            return group;
        }

        public String getCatname() {
            return catname;
        }

        public int getPublishstatus() {
            return publishstatus;
        }

        public String getModifyuser() {
            return modifyuser;
        }

        public int getUpdatetime() {
            return updatetime;
        }

        public String getPublishuser() {
            return publishuser;
        }

        public int getPublishtime() {
            return publishtime;
        }

        public String getDevices() {
            return devices;
        }

        public String getOriginalCatId() {
            return originalCatId;
        }

        public int getColoumnupdatetime() {
            return coloumnupdatetime;
        }

        public String getColumntype() {
            return columntype;
        }

        public String getAdvUpdateTime() {
            return advUpdateTime;
        }

        public int getIsRadio() {
            return isRadio;
        }

        public int getType() {
            return type;
        }

        public int getIspay() {
            return ispay;
        }

        public static class PhoneColumnPropertyEntity extends Entry{
            private String name;
            private String ename;
            private String cname;
            private String color;
            private int noColumn;
            private int noMenuBar;
            private int noLeftMenu;
            private int positionNum;
            private int isindex;
            private List<String> bigPicture = new ArrayList<String>();// 栏目大图
            private String link;
            private int notinTodayExtension;
            private String columnJson = "";

            public String getColumnJson() {
                return columnJson;
            }

            public void setColumnJson(String columnJson) {
                this.columnJson = columnJson;
            }

            public List<String> getBigPicture() {
                return bigPicture;
            }

            public void setBigPicture(List<String> bigPicture) {
                this.bigPicture = bigPicture;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setEname(String ename) {
                this.ename = ename;
            }

            public void setCname(String cname) {
                this.cname = cname;
            }

            public void setColor(String color) {
                this.color = color;
            }

            public void setNoColumn(int noColumn) {
                this.noColumn = noColumn;
            }

            public void setNoMenuBar(int noMenuBar) {
                this.noMenuBar = noMenuBar;
            }

            public void setNoLeftMenu(int noLeftMenu) {
                this.noLeftMenu = noLeftMenu;
            }

            public void setPositionNum(int positionNum) {
                this.positionNum = positionNum;
            }

            public void setIsindex(int isindex) {
                this.isindex = isindex;
            }

            public void setLink(String link) {
                this.link = link;
            }

            public void setNotinTodayExtension(int notinTodayExtension) {
                this.notinTodayExtension = notinTodayExtension;
            }

            public String getName() {
                return name;
            }

            public String getEname() {
                return ename;
            }

            public String getCname() {
                return cname;
            }

            public String getColor() {
                return color;
            }

            public int getNoColumn() {
                return noColumn;
            }

            public int getNoMenuBar() {
                return noMenuBar;
            }

            public int getNoLeftMenu() {
                return noLeftMenu;
            }

            public int getPositionNum() {
                return positionNum;
            }

            public int getIsindex() {
                return isindex;
            }

            public String getLink() {
                return link;
            }

            public int getNotinTodayExtension() {
                return notinTodayExtension;
            }
        }
    }
}
