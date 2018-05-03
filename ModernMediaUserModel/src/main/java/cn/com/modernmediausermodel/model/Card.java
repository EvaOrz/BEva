package cn.com.modernmediausermodel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;

/**
 * 卡片
 * 
 * @author user
 * 
 */
public class Card extends Entry {
	private static final long serialVersionUID = 1L;
	private String uid = "";// 当前用户的uid
	private int articleId;
	private String issueId = "";
	private int count;
	private ErrorMsg error = new ErrorMsg();
	private Map<String, User> userInfoMap = new HashMap<String, User>();// key:uid;value:User对象
	private List<CardItem> cardItemList = new ArrayList<CardItem>();

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int getArticleId() {
		return articleId;
	}

	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}

	public String getIssueId() {
		return issueId;
	}

	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public ErrorMsg getError() {
		return error;
	}

	public void setError(ErrorMsg error) {
		this.error = error;
	}

	public Map<String, User> getUserInfoMap() {
		return userInfoMap;
	}

	public void setUserInfoMap(Map<String, User> userInfoMap) {
		this.userInfoMap = userInfoMap;
	}

	public List<CardItem> getCardItemList() {
		return cardItemList;
	}

	public void setCardItemList(List<CardItem> cardItemList) {
		this.cardItemList = cardItemList;
	}

	public static class CardItem extends Entry {
		private static final long serialVersionUID = 1L;
		private String id = "";// 卡片id
		private String uid = "";// 表明是这个uid用户创建或者收藏的。。。(某个人的笔记首页的uid肯定是他自己)
		private int appId;// 应用id，暂时无用，可能以后会显示来自"商周、周末画报"..使用
		private int type;// 0.创建 2.收藏（如果uid是自己，type=0，isFav肯定是0；反之，type=2,isFav肯定是1）
		private String time = "";
		private String fuid = "";// 卡片对应的用户id(广场的fuid为"",因为广场上的卡片都是uid创建的，没有收藏)
		private String messageId = "";// 文章段落号
		private String webUrl = "";// 文章链接（暂时无用）
		private int categoryId;
		private String tags = "";
		private String contents = "";
		private int articleId;
		private String token = "";
		private int commentNum;// 被评论数
		private int favNum;// 被收藏数
		private String timeLineId = "";
		private int isDel;// 当前卡片是否被删除(暂时删除的卡片也显示)
		private int isFav;// 当前用户是否收藏,0，不是；1，是
		private String title = "";
		private String mark = "";
		private String updatetime = "";
		private List<CardPicture> pictures = new ArrayList<CardPicture>();
		private List<String> favUsers = new ArrayList<String>();

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public int getAppId() {
			return appId;
		}

		public void setAppId(int appId) {
			this.appId = appId;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getFuid() {
			return fuid;
		}

		public void setFuid(String fuid) {
			this.fuid = fuid;
		}

		public String getMessageId() {
			return messageId;
		}

		public void setMessageId(String messageId) {
			this.messageId = messageId;
		}

		public String getWebUrl() {
			return webUrl;
		}

		public void setWebUrl(String webUrl) {
			this.webUrl = webUrl;
		}

		public int getCategoryId() {
			return categoryId;
		}

		public void setCategoryId(int categoryId) {
			this.categoryId = categoryId;
		}

		public String getTags() {
			return tags;
		}

		public void setTags(String tags) {
			this.tags = tags;
		}

		public String getContents() {
			return contents;
		}

		public void setContents(String contents) {
			this.contents = contents;
		}

		public int getArticleId() {
			return articleId;
		}

		public void setArticleId(int articleId) {
			this.articleId = articleId;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public int getCommentNum() {
			return commentNum;
		}

		public void setCommentNum(int commentNum) {
			this.commentNum = commentNum;
		}

		public int getFavNum() {
			return favNum;
		}

		public void setFavNum(int favNum) {
			this.favNum = favNum;
		}

		public String getTimeLineId() {
			return timeLineId;
		}

		public void setTimeLineId(String timeLineId) {
			this.timeLineId = timeLineId;
		}

		public int getIsDel() {
			return isDel;
		}

		public void setIsDel(int isDel) {
			this.isDel = isDel;
		}

		public int getIsFav() {
			return isFav;
		}

		public void setIsFav(int isFav) {
			this.isFav = isFav;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getMark() {
			return mark;
		}

		public void setMark(String mark) {
			this.mark = mark;
		}

		public List<CardPicture> getPictures() {
			return pictures;
		}

		public String getUpdatetime() {
			return updatetime;
		}

		public void setUpdatetime(String updatetime) {
			this.updatetime = updatetime;
		}

		public void setPictures(List<CardPicture> pictures) {
			this.pictures = pictures;
		}

		public List<String> getFavUsers() {
			return favUsers;
		}

		public void setFavUsers(List<String> favUsers) {
			this.favUsers = favUsers;
		}

	}

	/**
	 * 笔记中的图片资源
	 * 
	 * @author jiancong
	 * 
	 */
	public static class CardPicture extends Entry {
		private static final long serialVersionUID = 1L;
		private String url = "";// 图片地址
		private String desc = "";// 资源附属描述 string
		private String title = "";// 资源附属标题 string
		private int width;// 资源宽 int
		private int height;// 资源高 int

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

	}

}
