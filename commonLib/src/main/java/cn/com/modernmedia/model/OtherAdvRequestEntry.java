package cn.com.modernmedia.model;

import java.util.List;

import cn.com.modernmediaslate.model.Entry;

/**
 * Created by administrator on 2018/4/28.
 */

public class OtherAdvRequestEntry extends Entry {
    /**
     * version : 0.2.2
     * srcType : 1
     * reqType : 1
     * timeStamp : 1499743507
     * appid : 196
     * appVersion : 1.0
     * devInfo : {"sdkUID":"121AF4B6-2807-4A35-A2D0-9D7C1DB3D5B8","imei":"355308089354037","mac":"02:00:00:00:00:00","phone":["14522098604"],"os":"22 (5.1)","platform":2,"devType":1,"brand":"HUAWEI","model":"HUAWEI TAG-AL00","resolution":"720_1184","screenSize":"5.7","language":"zh","density":"2.0","androidID":"5a3b287f2b13bef8"}
     * envInfo : {"net":4,"isp":1,"ip":"106.117.103.121","userAgent":"Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_2 like Mac OS X) AppleWebKit/603.2.4 (KHTML, like Gecko) Mobile/14F89","geo":{"longitude":"23.458635","latitude":"-50.273971"},"age":12,"yob":1982,"gender":1,"income":15000}
     * adReqInfo : [{"spaceID":"714","spaceParam":"","screenStatus":1}]
     */

    private String version;
    private int srcType;
    private int reqType;
    private int timeStamp;
    private String appid;
    private String appVersion;
    private DevInfoBean devInfo;
    private EnvInfoBean envInfo;
    private List<AdReqInfoBean> adReqInfo;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getSrcType() {
        return srcType;
    }

    public void setSrcType(int srcType) {
        this.srcType = srcType;
    }

    public int getReqType() {
        return reqType;
    }

    public void setReqType(int reqType) {
        this.reqType = reqType;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public DevInfoBean getDevInfo() {
        return devInfo;
    }

    public void setDevInfo(DevInfoBean devInfo) {
        this.devInfo = devInfo;
    }

    public EnvInfoBean getEnvInfo() {
        return envInfo;
    }

    public void setEnvInfo(EnvInfoBean envInfo) {
        this.envInfo = envInfo;
    }

    public List<AdReqInfoBean> getAdReqInfo() {
        return adReqInfo;
    }

    public void setAdReqInfo(List<AdReqInfoBean> adReqInfo) {
        this.adReqInfo = adReqInfo;
    }

    public static class DevInfoBean extends Entry{
        /**
         * sdkUID : 121AF4B6-2807-4A35-A2D0-9D7C1DB3D5B8
         * imei : 355308089354037
         * mac : 02:00:00:00:00:00
         * phone : ["14522098604"]
         * os : 22 (5.1)
         * platform : 2
         * devType : 1
         * brand : HUAWEI
         * model : HUAWEI TAG-AL00
         * resolution : 720_1184
         * screenSize : 5.7
         * language : zh
         * density : 2.0
         * androidID : 5a3b287f2b13bef8
         */

        private String sdkUID;
        private String imei;
        private String mac;
        private String os;
        private int platform;
        private int devType;
        private String brand;
        private String model;
        private String resolution;
        private String screenSize;
        private String language;
        private String density;
        private String androidID;
        private List<String> phone;

        public String getSdkUID() {
            return sdkUID;
        }

        public void setSdkUID(String sdkUID) {
            this.sdkUID = sdkUID;
        }

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public int getPlatform() {
            return platform;
        }

        public void setPlatform(int platform) {
            this.platform = platform;
        }

        public int getDevType() {
            return devType;
        }

        public void setDevType(int devType) {
            this.devType = devType;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getResolution() {
            return resolution;
        }

        public void setResolution(String resolution) {
            this.resolution = resolution;
        }

        public String getScreenSize() {
            return screenSize;
        }

        public void setScreenSize(String screenSize) {
            this.screenSize = screenSize;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getDensity() {
            return density;
        }

        public void setDensity(String density) {
            this.density = density;
        }

        public String getAndroidID() {
            return androidID;
        }

        public void setAndroidID(String androidID) {
            this.androidID = androidID;
        }

        public List<String> getPhone() {
            return phone;
        }

        public void setPhone(List<String> phone) {
            this.phone = phone;
        }
    }

    public static class EnvInfoBean extends Entry{
        /**
         * net : 4
         * isp : 1
         * ip : 106.117.103.121
         * userAgent : Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_2 like Mac OS X) AppleWebKit/603.2.4 (KHTML, like Gecko) Mobile/14F89
         * geo : {"longitude":"23.458635","latitude":"-50.273971"}
         * age : 12
         * yob : 1982
         * gender : 1
         * income : 15000
         */

        private int net;
        private int isp;
        private String ip;
        private String userAgent;
        private GeoBean geo;
        private int age;
        private int yob;
        private int gender;
        private int income;

        public int getNet() {
            return net;
        }

        public void setNet(int net) {
            this.net = net;
        }

        public int getIsp() {
            return isp;
        }

        public void setIsp(int isp) {
            this.isp = isp;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public GeoBean getGeo() {
            return geo;
        }

        public void setGeo(GeoBean geo) {
            this.geo = geo;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getYob() {
            return yob;
        }

        public void setYob(int yob) {
            this.yob = yob;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public int getIncome() {
            return income;
        }

        public void setIncome(int income) {
            this.income = income;
        }

        public static class GeoBean extends Entry{
            /**
             * longitude : 23.458635
             * latitude : -50.273971
             */

            private String longitude;
            private String latitude;

            public String getLongitude() {
                return longitude;
            }

            public void setLongitude(String longitude) {
                this.longitude = longitude;
            }

            public String getLatitude() {
                return latitude;
            }

            public void setLatitude(String latitude) {
                this.latitude = latitude;
            }
        }
    }

    public static class AdReqInfoBean extends Entry{
        /**
         * spaceID : 714
         * spaceParam :
         * screenStatus : 1
         */

        private String spaceID;
        private String spaceParam;
        private int screenStatus;

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

        public int getScreenStatus() {
            return screenStatus;
        }

        public void setScreenStatus(int screenStatus) {
            this.screenStatus = screenStatus;
        }
    }
}
