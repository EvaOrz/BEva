package cn.com.modernmediaslate.model;

public class User extends Entry {
    private static final long serialVersionUID = 1L;
    private String uid = "";
    // 用户名(邮箱或邮箱)
    private String userName = "";
    private String phone = "";
    private String email = "";
    // 密码(注册和修改资料时传)
    private String password = "";
    // 昵称
    private String nickName = "";
    // 头像
    private String avatar = "";
    // 错误信息
    private ErrorMsg error = new ErrorMsg();
    // 新浪uid(新浪用户登陆时用)
    private String sinaId = "";
    // 用户token
    private String token = "";
    // 设备id
    private String deviceId = "";
    // 设备token
    private String deviceToken = "";
    // 新密码(修改密码时用)
    private String newPassword = "";
    // 应用appid
    private String appid = "";
    // 应用版本
    private String version = "";
    // 个人签名(一句话简介)
    private String desc = "";
    // 该用户的登录状态(默认未登录)
    private boolean isLogined = false;
    // qq openid(qq用户登陆时用)
    private String qqId = "";
    // weixin openid(weixin用户登录时用)
    private String weixinId = "";
    // 3.6 过度版整合使用weixin unionid
    private String unionId = "";
    //3.6 过度版整合使用
    private String openId = "";
    // 绑定电话状态
    private boolean isBandPhone = false;
    // 绑定微信状态
    private boolean isBandWeixin = false;
    // 绑定邮箱状态
    private boolean isBandEmail = false;
    // 绑定微博状态
    private boolean isBandWeibo = false;
    // 绑定qq状态
    private boolean isBandQQ = false;
    // 邮箱验证状态
    private boolean isValEmail = false;
    // 接受商周简报状态
    private int isPushEmail;// 0:不接受 1：接受
    //用户第三方消息，微博，微信
    private String openLoginJson = "";

    private String realname = "";            //姓名
    private int sex;                        //性别 0女1男2保密
    private String birth = "";           //生日
    private String vip = "";              //vip的id
    private long start_time = 0;      //首次开通VIP时间
    private long vip_end_time = 0;         //有效期
    private String industry = "";        //行业
    private String position = "";        //职位
    private String income = "";          //年收入
    private String province = "";            //省
    private String city = "";                //市
    private String address = "";            //详细地址
    private int level;               //vip权限0是小白，1是付费会员，2VIP
    private String send = "";                //是否发实卡  0没发，1要发，2已发
    private int completevip;         //是否完成VIP资料补全  0为补全 1补全
    private int user_status;         //判断用户状态1是付费会员，2VIP有效，3付费过期，4VIP过期

    public int getUser_status() {
        return user_status;
    }

    public void setUser_status(int user_status) {
        this.user_status = user_status;
    }

    public int getCompletevip() {
        return completevip;
    }

    public void setCompletevip(int completevip) {
        this.completevip = completevip;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getVip_end_time() {
        return vip_end_time;
    }

    public void setVip_end_time(long end_time) {
        this.vip_end_time = end_time;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getOpenLoginJson() {
        return openLoginJson;
    }

    public void setOpenLoginJson(String openLoginJson) {
        this.openLoginJson = openLoginJson;
    }

    public int isPushEmail() {
        return isPushEmail;
    }

    public void setPushEmail(int pushEmail) {
        isPushEmail = pushEmail;
    }

    public boolean isValEmail() {
        return isValEmail;
    }

    public void setValEmail(boolean isValEmail) {
        this.isValEmail = isValEmail;
    }

    public boolean isBandPhone() {
        return isBandPhone;
    }

    public void setBandPhone(boolean isBandPhone) {
        this.isBandPhone = isBandPhone;
    }

    public boolean isBandWeixin() {
        return isBandWeixin;
    }

    public void setBandWeixin(boolean isBandWeixin) {
        this.isBandWeixin = isBandWeixin;
    }

    public boolean isBandEmail() {
        return isBandEmail;
    }

    public void setBandEmail(boolean isBandEmail) {
        this.isBandEmail = isBandEmail;
    }

    public boolean isBandWeibo() {
        return isBandWeibo;
    }

    public void setBandWeibo(boolean isBandWeibo) {
        this.isBandWeibo = isBandWeibo;
    }

    public boolean isBandQQ() {
        return isBandQQ;
    }

    public void setBandQQ(boolean isBandQQ) {
        this.isBandQQ = isBandQQ;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public ErrorMsg getError() {
        return error;
    }

    public void setError(ErrorMsg error) {
        this.error = error;
    }

    public String getSinaId() {
        return sinaId;
    }

    public void setSinaId(String sinaId) {
        this.sinaId = sinaId;
    }

    public String getQqId() {
        return qqId;
    }

    public void setQqId(String qqId) {
        this.qqId = qqId;
    }

    public String getWeixinId() {
        return weixinId;
    }

    public void setWeixinId(String weixinId) {
        this.weixinId = weixinId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isLogined() {
        return isLogined;
    }

    public void setLogined(boolean isLogined) {
        this.isLogined = isLogined;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
