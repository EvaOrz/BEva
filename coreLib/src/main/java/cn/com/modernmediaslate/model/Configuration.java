package cn.com.modernmediaslate.model;

/**
 * 应用配置信息类
 * 
 * @author user
 * 
 */
public class Configuration {
	/**
	 * 是否含有新浪sdk；0没有，1有；如果没有，去掉新浪微博登录，去掉写卡片页去掉新浪微博分享，新浪微博分享使用系统分享
	 */
	private int has_sina = 1;
	/**
	 * 是否含有微信sdk；0没有，1有；如果没有，微信分享使用系统分享
	 */
	private int has_weixin = 1;
	/**
	 * 是否含有qqsdk；0没有，1有;如果没有，去掉QQ登录
	 */
	private int has_qq = 1;
	/**
	 * 首页是否滑屏显示；0不是，1是；商周、iweekly首页滑屏
	 */
	private int is_index_pager = 0;
	/**
	 * 是否包含金币系统；0不是，1是；商周包含
	 */
	private int has_coin = 0;
	/**
	 * 是否有栏目第一项内容显示在新的view中，0没有，1有；iweekly的视野
	 */
	private int has_single_view = 0;
	/**
	 * flurry key
	 */
	private String flurry_api_key = "";
	/**
	 * parse app id
	 */
	private String parse_app_id = "";
	/**
	 * parse client id
	 */
	private String parse_client_id = "";
	/**
	 * 微信qppid(除了商周简体版googleplay外用)
	 */
	private String weixin_app_id = "";
	/**
	 * 微信登陆&支付 appsecret
	 */
	private String weixin_app_secret = "";
	/**
	 * 微信支付 商户id
	 */
	private String weixin_partner_id = "";
	/**
	 * 微信qppid(商周简体版googleplay用)
	 */
	private String weixin_app_id_google = "";
	/**
	 * 微博appid(除了商周简体版googleplay外用)
	 */
	private String weibo_app_id = "";
	/**
	 * 微博appid(商周简体版googleplay用)
	 */
	private String weibo_app_id_goole = "";
	/**
	 * QQ appid
	 */
	private String qq_app_id = "";
	/**
	 * 缓存文件夹名
	 */
	private String cache_file_name = "";
	/**
	 * 模板最高支持的版本号
	 */
	private int template_version;
	/**
	 * 是否支持订阅
	 */
	private int has_subscribe = 0;
	/**
	 * 首页导航栏是否可以隐藏.0：滑动时不隐藏；1：滑动时隐藏；-1：强制隐藏
	 */
	private int nav_hide = 0;
	/**
	 * 首页列表与导航栏是否对齐
	 */
	private int align_bar = 0;
	/**
	 * 应用在新浪微博上的用户id
	 */
	private int weibo_uid = 0;
	/**
	 * 应用微信的官方公众账号
	 * 
	 */
	private String weixin_public_number = "";
	/**
	 * 导航栏（首页、文章页）根据栏目改变背景
	 */
	private int is_navbar_bg_change = 0;
	/**
	 * 友盟key
	 * 
	 */
	private String umeng_key = "";
	/**
	 * 小米推送app_id
	 */
	private String xiaomi_push_appid = "";
	/**
	 * 小米推送app_key
	 */
	private String xiaomi_push_appkey = "";
	/**
	 * 小米推送app_secret
	 */
	private String xiaomi_push_appsecret = "";

	/**
	 * 有赞id
	 */
	private String youzan_client_id = "";
	/**
	 * 有赞secret
	 */
	private String youzan_client_secret = "";

	public String getXiaomi_push_appid() {
		return xiaomi_push_appid;
	}

	public void setXiaomi_push_appid(String xiaomi_push_appid) {
		this.xiaomi_push_appid = xiaomi_push_appid;
	}

	public String getXiaomi_push_appkey() {
		return xiaomi_push_appkey;
	}

	public void setXiaomi_push_appkey(String xiaomi_push_appkey) {
		this.xiaomi_push_appkey = xiaomi_push_appkey;
	}

	public String getXiaomi_push_appsecret() {
		return xiaomi_push_appsecret;
	}

	public void setXiaomi_push_appsecret(String xiaomi_push_appsecret) {
		this.xiaomi_push_appsecret = xiaomi_push_appsecret;
	}

	public int getHas_sina() {
		return has_sina;
	}

	public void setHas_sina(int has_sina) {
		this.has_sina = has_sina;
	}

	public int getHas_weixin() {
		return has_weixin;
	}

	public void setHas_weixin(int has_weixin) {
		this.has_weixin = has_weixin;
	}

	public int getHas_qq() {
		return has_qq;
	}

	public void setHas_qq(int has_qq) {
		this.has_qq = has_qq;
	}

	public int getIs_index_pager() {
		return is_index_pager;
	}

	public void setIs_index_pager(int is_index_pager) {
		this.is_index_pager = is_index_pager;
	}

	public int getHas_coin() {
		return has_coin;
	}

	public void setHas_coin(int has_coin) {
		this.has_coin = has_coin;
	}

	public int getHas_single_view() {
		return has_single_view;
	}

	public void setHas_single_view(int has_single_view) {
		this.has_single_view = has_single_view;
	}

	public String getFlurry_api_key() {
		return flurry_api_key;
	}

	public void setFlurry_api_key(String flurry_api_key) {
		this.flurry_api_key = flurry_api_key;
	}

	public String getParse_app_id() {
		return parse_app_id;
	}

	public void setParse_app_id(String parse_app_id) {
		this.parse_app_id = parse_app_id;
	}

	public String getParse_client_id() {
		return parse_client_id;
	}

	public void setParse_client_id(String parse_client_id) {
		this.parse_client_id = parse_client_id;
	}

	public String getWeixin_app_id() {
		return weixin_app_id;
	}

	public void setWeixin_app_id(String weixin_app_id) {
		this.weixin_app_id = weixin_app_id;
	}

	public String getWeixin_app_id_google() {
		return weixin_app_id_google;
	}

	public void setWeixin_app_id_google(String weixin_app_id_google) {
		this.weixin_app_id_google = weixin_app_id_google;
	}

	public String getWeibo_app_id() {
		return weibo_app_id;
	}

	public void setWeibo_app_id(String weibo_app_id) {
		this.weibo_app_id = weibo_app_id;
	}

	public String getWeibo_app_id_goole() {
		return weibo_app_id_goole;
	}

	public void setWeibo_app_id_goole(String weibo_app_id_goole) {
		this.weibo_app_id_goole = weibo_app_id_goole;
	}

	public String getQq_app_id() {
		return qq_app_id;
	}

	public void setQq_app_id(String qq_app_id) {
		this.qq_app_id = qq_app_id;
	}

	public String getCache_file_name() {
		return cache_file_name;
	}

	public void setCache_file_name(String cache_file_name) {
		this.cache_file_name = cache_file_name;
	}

	public int getTemplate_version() {
		return template_version;
	}

	public void setTemplate_version(int template_version) {
		this.template_version = template_version;
	}

	public int getHas_subscribe() {
		return has_subscribe;
	}

	public void setHas_subscribe(int has_subscribe) {
		this.has_subscribe = has_subscribe;
	}

	public int getNav_hide() {
		return nav_hide;
	}

	public void setNav_hide(int nav_hide) {
		this.nav_hide = nav_hide;
	}

	public int getAlign_bar() {
		return align_bar;
	}

	public void setAlign_bar(int align_bar) {
		this.align_bar = align_bar;
	}

	public int getWeibo_uid() {
		return weibo_uid;
	}

	public void setWeibo_uid(int weibo_uid) {
		this.weibo_uid = weibo_uid;
	}

	public String getWeixin_public_number() {
		return weixin_public_number;
	}

	public void setWeixin_public_number(String weixin_public_number) {
		this.weixin_public_number = weixin_public_number;
	}

	public int getIs_navbar_bg_change() {
		return is_navbar_bg_change;
	}

	public void setIs_navbar_bg_change(int is_navbar_bg_change) {
		this.is_navbar_bg_change = is_navbar_bg_change;
	}

	public String getUmeng_key() {
		return umeng_key;
	}

	public void setUmeng_key(String umeng_key) {
		this.umeng_key = umeng_key;
	}

	public String getWeixin_app_secret() {
		return weixin_app_secret;
	}

	public void setWeixin_app_secret(String weixin_app_secret) {
		this.weixin_app_secret = weixin_app_secret;
	}

	public String getWeixin_partner_id() {
		return weixin_partner_id;
	}

	public void setWeixin_partner_id(String weixin_partner_id) {
		this.weixin_partner_id = weixin_partner_id;
	}

	public String getYouzan_client_id() {
		return youzan_client_id;
	}

	public void setYouzan_client_id(String youzan_client_id) {
		this.youzan_client_id = youzan_client_id;
	}

	public String getYouzan_client_secret() {
		return youzan_client_secret;
	}

	public void setYouzan_client_secret(String youzan_client_secret) {
		this.youzan_client_secret = youzan_client_secret;
	}
}
