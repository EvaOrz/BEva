package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmediaslate.model.Entry;

/**
 * 往期列表
 * 
 * @author ZhuQiao
 * 
 */
public class IssueList extends Entry {
	private static final long serialVersionUID = 1L;
	private int total;
	private int numperpage;
	private List<IssueListItem> list = new ArrayList<IssueListItem>();

	// 往期的下载情况(只在显示的时候用，但点击查看往期的时候重新获取getissue，根据里面的fullpackage字段开始后续判断)
	// @SuppressLint("UseSparseArrays")
	// private Map<Integer, BreakPoint> breakMap = new HashMap<Integer,
	// BreakPoint>();

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getNumperpage() {
		return numperpage;
	}

	public void setNumperpage(int numperpage) {
		this.numperpage = numperpage;
	}

	public List<IssueListItem> getList() {
		return list;
	}

	public void setList(List<IssueListItem> list) {
		this.list = list;
	}

	public static class IssueListItem extends Entry {
		private static final long serialVersionUID = 1L;
		private int id;// issue id
		private String name = "";// 期开始结束时间(已弃用)
		private String coverpic = "";// 期封面(已弃用)
		private int appid;// 当期为从其他应用推送过来的期时，显示原应用id(无用)
		private String title = "";// 期标题
		private String startTime = "";// 期开始时间
		private String endTime = "";// 期结束时间
		private List<String> issuepicList = new ArrayList<String>();// 期相关图片，默认第一张为期封面
		private IssueArticle issueArticle = new IssueArticle();// 无用

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCoverpic() {
			return coverpic;
		}

		public void setCoverpic(String coverpic) {
			this.coverpic = coverpic;
		}

		public int getAppid() {
			return appid;
		}

		public void setAppid(int appid) {
			this.appid = appid;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public String getEndTime() {
			return endTime;
		}

		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}

		public List<String> getIssuepicList() {
			return issuepicList;
		}

		public void setIssuepicList(List<String> issuepicList) {
			this.issuepicList = issuepicList;
		}

		public IssueArticle getIssueArticle() {
			return issueArticle;
		}

		public void setIssueArticle(IssueArticle issueArticle) {
			this.issueArticle = issueArticle;
		}

	}

	/**
	 * 期文章（头版头条:只用于商周）
	 * 
	 * @author ZhuQiao
	 * 
	 */
	public static class IssueArticle extends Entry {
		private static final long serialVersionUID = 1L;
		private int id;
		private String title = "";

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

	}
}
