package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.breakpoint.BreakPointUtil;
import cn.com.modernmediaslate.model.Entry;

/**
 * 断点
 * 
 * @author ZhuQiao
 * 
 */
public class BreakPoint extends Entry {
	private static final long serialVersionUID = 1L;
	private int id;// issue_id
	private String url = "";// zip包地址
	private long complete = 0;// 完成度
	private long total = 0;// 总大小
	private List<ThreadInfo> infoList = new ArrayList<ThreadInfo>();
	private int status = BreakPointUtil.NONE;// 当前下载状态

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getComplete() {
		return complete;
	}

	public void setComplete(long complete) {
		this.complete = complete;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<ThreadInfo> getInfoList() {
		return infoList;
	}

	public void setInfoList(List<ThreadInfo> infoList) {
		this.infoList = infoList;
	}

	public static class ThreadInfo extends Entry {
		private static final long serialVersionUID = 1L;
		private int issueId;
		private int threadId;
		private long startPos = 0;// 开始点
		private long endPos = 0;// 结束点
		private String url = "";// zip包地址(被截取过的url)
		private long complete = 0;// 完成度
		private long total = 0;// 当前thread总大小

		public int getIssueId() {
			return issueId;
		}

		public void setIssueId(int issueId) {
			this.issueId = issueId;
		}

		public int getThreadId() {
			return threadId;
		}

		public void setThreadId(int threadId) {
			this.threadId = threadId;
		}

		public long getStartPos() {
			return startPos;
		}

		public void setStartPos(long startPos) {
			this.startPos = startPos;
		}

		public long getEndPos() {
			return endPos;
		}

		public void setEndPos(long endPos) {
			this.endPos = endPos;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public long getComplete() {
			return complete;
		}

		public void setComplete(long complete) {
			this.complete = complete;
		}

		public long getTotal() {
			return total;
		}

		public void setTotal(long total) {
			this.total = total;
		}

	}
}
