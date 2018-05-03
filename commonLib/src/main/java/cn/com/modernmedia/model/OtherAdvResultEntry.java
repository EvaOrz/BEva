package cn.com.modernmedia.model;

import java.util.List;

import cn.com.modernmediaslate.model.Entry;

/**
 * Created by administrator on 2018/4/28.
 */

public class OtherAdvResultEntry extends Entry {
    /**
     * status : 0
     * errcode : 0
     * errmsg : OK
     * spaceInfo : [{"spaceID":"714","spaceParam":"","adpType":6,"refreshInterval":1,"screenDirection":1,"width":"0","height":"0","adpPosition":{"x":"40","y":"0"},"autoClose":false,"maxTime":0,"manualClosable":true,"minTime":0,"wifiPreload":false,"mute":false,"fullScreen":false,"autoPlay":false,"adResponse":[{"extInfo":"DavOTIuOT2mM9asN9atMpQq5A==","contentInfo":[{"renderType":4,"adcontentSlot":[{"md5":"Headline","content":"大众点评-美食频道"},{"md5":"images","content":"http://pgdt.gtimg.cn/gdt/0/DAAQQ9sAUAALQABhBZI6eBCdjiEdzk.jpg/0?ck=9edab9d75554dfb61256077e50575f58"}],"template":"{\"Headline\":\"大众点评-美食频道\",\"Image\":\"http://pgdt.gtimg.cn/gdt/0/DAAQiEdzk.jpg/0?ck=9eda58\",\"Images\":[\"http://pgdt.gtimg.cn/gdt/0/DAAQiEdzk.jpg/0?ck=9eda58\"],\"Body\":\"\",\"AppIcon\":\"\",\"Action\":\"\",\"Star\":\"\",\"Store\":\"\",\"Price\":\"\"}"}],"interactInfo":{"thirdpartInfo":[{"viewUrl":"http://stat.adx.yumimobi.com/api/s?r=3325c0e9","clickUrl":"http://stat.adx.yumimobi.com/api/s?r=e7683ffb"},{"viewUrl":"http://v.gdt.qq.com/gdt_stats.fcg?count=1"},{"viewUrl":"http://api.htp.hubcloud.com.cn:45600/vw?extInfo=zgT-dYFAz&ts=.UTC_TS.","clickUrl":"http://api.htp.hubcloud.com.cn:45600/ck?extInfo=zgT-dYFAz-bmupY&ts=.UTC_TS.&scdx=.SCRN_CLK_PT_DOWN_X.&scdy=.SCRN_CLK_PT_DOWN_Y.&scux=.SCRN_CLK_PT_UP_X.&scuy=.SCRN_CLK_PT_UP_Y."}],"landingPageUrl":"http://c.gdt.qq.com/gdt_mclick.fcg?viewid=vDA6pcEx7D"},"adLogo":{"adLabelUrl":"http://otqcy59kg.bkt.clouddn.com/adhub-adlabel.png","adLabel":"广告","sourceUrl":"http://otqcy59kg.bkt.clouddn.com/adhub-adlogo.png","sourceLabel":"ADHUB"}}],"userDayLimit":0,"userHourLimit":0,"orgID":10027,"contentType":64,"appID":"196","nativeTemp":"","dnfReq":"","dnfResp":""}]
     * ts : 1499928970297381718
     */

    private int status;
    private String errcode;
    private String errmsg;
    private long ts;
    private List<SpaceInfoBean> spaceInfo;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public List<SpaceInfoBean> getSpaceInfo() {
        return spaceInfo;
    }

    public void setSpaceInfo(List<SpaceInfoBean> spaceInfo) {
        this.spaceInfo = spaceInfo;
    }

    public static class SpaceInfoBean extends Entry {
        /**
         * spaceID : 714
         * spaceParam :
         * adpType : 6
         * refreshInterval : 1
         * screenDirection : 1
         * width : 0
         * height : 0
         * adpPosition : {"x":"40","y":"0"}
         * autoClose : false
         * maxTime : 0
         * manualClosable : true
         * minTime : 0
         * wifiPreload : false
         * mute : false
         * fullScreen : false
         * autoPlay : false
         * adResponse : [{"extInfo":"DavOTIuOT2mM9asN9atMpQq5A==","contentInfo":[{"renderType":4,"adcontentSlot":[{"md5":"Headline","content":"大众点评-美食频道"},{"md5":"images","content":"http://pgdt.gtimg.cn/gdt/0/DAAQQ9sAUAALQABhBZI6eBCdjiEdzk.jpg/0?ck=9edab9d75554dfb61256077e50575f58"}],"template":"{\"Headline\":\"大众点评-美食频道\",\"Image\":\"http://pgdt.gtimg.cn/gdt/0/DAAQiEdzk.jpg/0?ck=9eda58\",\"Images\":[\"http://pgdt.gtimg.cn/gdt/0/DAAQiEdzk.jpg/0?ck=9eda58\"],\"Body\":\"\",\"AppIcon\":\"\",\"Action\":\"\",\"Star\":\"\",\"Store\":\"\",\"Price\":\"\"}"}],"interactInfo":{"thirdpartInfo":[{"viewUrl":"http://stat.adx.yumimobi.com/api/s?r=3325c0e9","clickUrl":"http://stat.adx.yumimobi.com/api/s?r=e7683ffb"},{"viewUrl":"http://v.gdt.qq.com/gdt_stats.fcg?count=1"},{"viewUrl":"http://api.htp.hubcloud.com.cn:45600/vw?extInfo=zgT-dYFAz&ts=.UTC_TS.","clickUrl":"http://api.htp.hubcloud.com.cn:45600/ck?extInfo=zgT-dYFAz-bmupY&ts=.UTC_TS.&scdx=.SCRN_CLK_PT_DOWN_X.&scdy=.SCRN_CLK_PT_DOWN_Y.&scux=.SCRN_CLK_PT_UP_X.&scuy=.SCRN_CLK_PT_UP_Y."}],"landingPageUrl":"http://c.gdt.qq.com/gdt_mclick.fcg?viewid=vDA6pcEx7D"},"adLogo":{"adLabelUrl":"http://otqcy59kg.bkt.clouddn.com/adhub-adlabel.png","adLabel":"广告","sourceUrl":"http://otqcy59kg.bkt.clouddn.com/adhub-adlogo.png","sourceLabel":"ADHUB"}}]
         * userDayLimit : 0
         * userHourLimit : 0
         * orgID : 10027
         * contentType : 64
         * appID : 196
         * nativeTemp :
         * dnfReq :
         * dnfResp :
         */

        private String spaceID;
        private String spaceParam;
        private int adpType;
        private int refreshInterval;
        private int screenDirection;
        private String width;
        private String height;
        private AdpPositionBean adpPosition;
        private boolean autoClose;
        private int maxTime;
        private boolean manualClosable;
        private int minTime;
        private boolean wifiPreload;
        private boolean mute;
        private boolean fullScreen;
        private boolean autoPlay;
        private int userDayLimit;
        private int userHourLimit;
        private int orgID;
        private int contentType;
        private String appID;
        private String nativeTemp;
        private String dnfReq;
        private String dnfResp;
        private List<AdResponseBean> adResponse;

        public String getSpaceID() {
            return spaceID;
        }

        public void setSpaceID(String spaceID) {
            this.spaceID = spaceID;
        }

        public String getSpaceParam() {
            return spaceParam;
        }

        public void setSpaceParam(String spaceParam) {
            this.spaceParam = spaceParam;
        }

        public int getAdpType() {
            return adpType;
        }

        public void setAdpType(int adpType) {
            this.adpType = adpType;
        }

        public int getRefreshInterval() {
            return refreshInterval;
        }

        public void setRefreshInterval(int refreshInterval) {
            this.refreshInterval = refreshInterval;
        }

        public int getScreenDirection() {
            return screenDirection;
        }

        public void setScreenDirection(int screenDirection) {
            this.screenDirection = screenDirection;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public AdpPositionBean getAdpPosition() {
            return adpPosition;
        }

        public void setAdpPosition(AdpPositionBean adpPosition) {
            this.adpPosition = adpPosition;
        }

        public boolean isAutoClose() {
            return autoClose;
        }

        public void setAutoClose(boolean autoClose) {
            this.autoClose = autoClose;
        }

        public int getMaxTime() {
            return maxTime;
        }

        public void setMaxTime(int maxTime) {
            this.maxTime = maxTime;
        }

        public boolean isManualClosable() {
            return manualClosable;
        }

        public void setManualClosable(boolean manualClosable) {
            this.manualClosable = manualClosable;
        }

        public int getMinTime() {
            return minTime;
        }

        public void setMinTime(int minTime) {
            this.minTime = minTime;
        }

        public boolean isWifiPreload() {
            return wifiPreload;
        }

        public void setWifiPreload(boolean wifiPreload) {
            this.wifiPreload = wifiPreload;
        }

        public boolean isMute() {
            return mute;
        }

        public void setMute(boolean mute) {
            this.mute = mute;
        }

        public boolean isFullScreen() {
            return fullScreen;
        }

        public void setFullScreen(boolean fullScreen) {
            this.fullScreen = fullScreen;
        }

        public boolean isAutoPlay() {
            return autoPlay;
        }

        public void setAutoPlay(boolean autoPlay) {
            this.autoPlay = autoPlay;
        }

        public int getUserDayLimit() {
            return userDayLimit;
        }

        public void setUserDayLimit(int userDayLimit) {
            this.userDayLimit = userDayLimit;
        }

        public int getUserHourLimit() {
            return userHourLimit;
        }

        public void setUserHourLimit(int userHourLimit) {
            this.userHourLimit = userHourLimit;
        }

        public int getOrgID() {
            return orgID;
        }

        public void setOrgID(int orgID) {
            this.orgID = orgID;
        }

        public int getContentType() {
            return contentType;
        }

        public void setContentType(int contentType) {
            this.contentType = contentType;
        }

        public String getAppID() {
            return appID;
        }

        public void setAppID(String appID) {
            this.appID = appID;
        }

        public String getNativeTemp() {
            return nativeTemp;
        }

        public void setNativeTemp(String nativeTemp) {
            this.nativeTemp = nativeTemp;
        }

        public String getDnfReq() {
            return dnfReq;
        }

        public void setDnfReq(String dnfReq) {
            this.dnfReq = dnfReq;
        }

        public String getDnfResp() {
            return dnfResp;
        }

        public void setDnfResp(String dnfResp) {
            this.dnfResp = dnfResp;
        }

        public List<AdResponseBean> getAdResponse() {
            return adResponse;
        }

        public void setAdResponse(List<AdResponseBean> adResponse) {
            this.adResponse = adResponse;
        }

        public static class AdpPositionBean extends Entry {
            /**
             * x : 40
             * y : 0
             */

            private String x;
            private String y;

            public String getX() {
                return x;
            }

            public void setX(String x) {
                this.x = x;
            }

            public String getY() {
                return y;
            }

            public void setY(String y) {
                this.y = y;
            }
        }

        public static class AdResponseBean extends Entry {
            /**
             * extInfo : DavOTIuOT2mM9asN9atMpQq5A==
             * contentInfo : [{"renderType":4,"adcontentSlot":[{"md5":"Headline","content":"大众点评-美食频道"},{"md5":"images","content":"http://pgdt.gtimg.cn/gdt/0/DAAQQ9sAUAALQABhBZI6eBCdjiEdzk.jpg/0?ck=9edab9d75554dfb61256077e50575f58"}],"template":"{\"Headline\":\"大众点评-美食频道\",\"Image\":\"http://pgdt.gtimg.cn/gdt/0/DAAQiEdzk.jpg/0?ck=9eda58\",\"Images\":[\"http://pgdt.gtimg.cn/gdt/0/DAAQiEdzk.jpg/0?ck=9eda58\"],\"Body\":\"\",\"AppIcon\":\"\",\"Action\":\"\",\"Star\":\"\",\"Store\":\"\",\"Price\":\"\"}"}]
             * interactInfo : {"thirdpartInfo":[{"viewUrl":"http://stat.adx.yumimobi.com/api/s?r=3325c0e9","clickUrl":"http://stat.adx.yumimobi.com/api/s?r=e7683ffb"},{"viewUrl":"http://v.gdt.qq.com/gdt_stats.fcg?count=1"},{"viewUrl":"http://api.htp.hubcloud.com.cn:45600/vw?extInfo=zgT-dYFAz&ts=.UTC_TS.","clickUrl":"http://api.htp.hubcloud.com.cn:45600/ck?extInfo=zgT-dYFAz-bmupY&ts=.UTC_TS.&scdx=.SCRN_CLK_PT_DOWN_X.&scdy=.SCRN_CLK_PT_DOWN_Y.&scux=.SCRN_CLK_PT_UP_X.&scuy=.SCRN_CLK_PT_UP_Y."}],"landingPageUrl":"http://c.gdt.qq.com/gdt_mclick.fcg?viewid=vDA6pcEx7D"}
             * adLogo : {"adLabelUrl":"http://otqcy59kg.bkt.clouddn.com/adhub-adlabel.png","adLabel":"广告","sourceUrl":"http://otqcy59kg.bkt.clouddn.com/adhub-adlogo.png","sourceLabel":"ADHUB"}
             */

            private String extInfo;
            private InteractInfoBean interactInfo;
            private AdLogoBean adLogo;
            private List<ContentInfoBean> contentInfo;

            public String getExtInfo() {
                return extInfo;
            }

            public void setExtInfo(String extInfo) {
                this.extInfo = extInfo;
            }

            public InteractInfoBean getInteractInfo() {
                return interactInfo;
            }

            public void setInteractInfo(InteractInfoBean interactInfo) {
                this.interactInfo = interactInfo;
            }

            public AdLogoBean getAdLogo() {
                return adLogo;
            }

            public void setAdLogo(AdLogoBean adLogo) {
                this.adLogo = adLogo;
            }

            public List<ContentInfoBean> getContentInfo() {
                return contentInfo;
            }

            public void setContentInfo(List<ContentInfoBean> contentInfo) {
                this.contentInfo = contentInfo;
            }

            public static class InteractInfoBean extends Entry {
                /**
                 * thirdpartInfo : [{"viewUrl":"http://stat.adx.yumimobi.com/api/s?r=3325c0e9","clickUrl":"http://stat.adx.yumimobi.com/api/s?r=e7683ffb"},{"viewUrl":"http://v.gdt.qq.com/gdt_stats.fcg?count=1"},{"viewUrl":"http://api.htp.hubcloud.com.cn:45600/vw?extInfo=zgT-dYFAz&ts=.UTC_TS.","clickUrl":"http://api.htp.hubcloud.com.cn:45600/ck?extInfo=zgT-dYFAz-bmupY&ts=.UTC_TS.&scdx=.SCRN_CLK_PT_DOWN_X.&scdy=.SCRN_CLK_PT_DOWN_Y.&scux=.SCRN_CLK_PT_UP_X.&scuy=.SCRN_CLK_PT_UP_Y."}]
                 * landingPageUrl : http://c.gdt.qq.com/gdt_mclick.fcg?viewid=vDA6pcEx7D
                 */

                private String landingPageUrl;
                private List<ThirdpartInfoBean> thirdpartInfo;

                public String getLandingPageUrl() {
                    return landingPageUrl;
                }

                public void setLandingPageUrl(String landingPageUrl) {
                    this.landingPageUrl = landingPageUrl;
                }

                public List<ThirdpartInfoBean> getThirdpartInfo() {
                    return thirdpartInfo;
                }

                public void setThirdpartInfo(List<ThirdpartInfoBean> thirdpartInfo) {
                    this.thirdpartInfo = thirdpartInfo;
                }

                public static class ThirdpartInfoBean extends Entry {
                    /**
                     * viewUrl : http://stat.adx.yumimobi.com/api/s?r=3325c0e9
                     * clickUrl : http://stat.adx.yumimobi.com/api/s?r=e7683ffb
                     */

                    private String viewUrl;
                    private String clickUrl;

                    public String getViewUrl() {
                        return viewUrl;
                    }

                    public void setViewUrl(String viewUrl) {
                        this.viewUrl = viewUrl;
                    }

                    public String getClickUrl() {
                        return clickUrl;
                    }

                    public void setClickUrl(String clickUrl) {
                        this.clickUrl = clickUrl;
                    }
                }
            }

            public static class AdLogoBean extends Entry {
                /**
                 * adLabelUrl : http://otqcy59kg.bkt.clouddn.com/adhub-adlabel.png
                 * adLabel : 广告
                 * sourceUrl : http://otqcy59kg.bkt.clouddn.com/adhub-adlogo.png
                 * sourceLabel : ADHUB
                 */

                private String adLabelUrl;
                private String adLabel;
                private String sourceUrl;
                private String sourceLabel;

                public String getAdLabelUrl() {
                    return adLabelUrl;
                }

                public void setAdLabelUrl(String adLabelUrl) {
                    this.adLabelUrl = adLabelUrl;
                }

                public String getAdLabel() {
                    return adLabel;
                }

                public void setAdLabel(String adLabel) {
                    this.adLabel = adLabel;
                }

                public String getSourceUrl() {
                    return sourceUrl;
                }

                public void setSourceUrl(String sourceUrl) {
                    this.sourceUrl = sourceUrl;
                }

                public String getSourceLabel() {
                    return sourceLabel;
                }

                public void setSourceLabel(String sourceLabel) {
                    this.sourceLabel = sourceLabel;
                }
            }

            public static class ContentInfoBean extends Entry {
                /**
                 * renderType : 4
                 * adcontentSlot : [{"md5":"Headline","content":"大众点评-美食频道"},{"md5":"images","content":"http://pgdt.gtimg.cn/gdt/0/DAAQQ9sAUAALQABhBZI6eBCdjiEdzk.jpg/0?ck=9edab9d75554dfb61256077e50575f58"}]
                 * template : {"Headline":"大众点评-美食频道","Image":"http://pgdt.gtimg.cn/gdt/0/DAAQiEdzk.jpg/0?ck=9eda58","Images":["http://pgdt.gtimg.cn/gdt/0/DAAQiEdzk.jpg/0?ck=9eda58"],"Body":"","AppIcon":"","Action":"","Star":"","Store":"","Price":""}
                 */

                private int renderType;
                private String template;
                private List<AdcontentSlotBean> adcontentSlot;

                public int getRenderType() {
                    return renderType;
                }

                public void setRenderType(int renderType) {
                    this.renderType = renderType;
                }

                public String getTemplate() {
                    return template;
                }

                public void setTemplate(String template) {
                    this.template = template;
                }

                public List<AdcontentSlotBean> getAdcontentSlot() {
                    return adcontentSlot;
                }

                public void setAdcontentSlot(List<AdcontentSlotBean> adcontentSlot) {
                    this.adcontentSlot = adcontentSlot;
                }

                public static class AdcontentSlotBean extends Entry {
                    /**
                     * md5 : Headline
                     * content : 大众点评-美食频道
                     */

                    private String md5;
                    private String content;

                    public String getMd5() {
                        return md5;
                    }

                    public void setMd5(String md5) {
                        this.md5 = md5;
                    }

                    public String getContent() {
                        return content;
                    }

                    public void setContent(String content) {
                        this.content = content;
                    }
                }
            }
        }
    }
}
