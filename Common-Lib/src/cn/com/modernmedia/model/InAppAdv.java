package cn.com.modernmedia.model;

import cn.com.modernmediaslate.model.Entry;

/**
 * 入版广告
 * 
 * @author user
 * 
 */
public class InAppAdv extends Entry {
	private static final long serialVersionUID = 1L;
	private String start_date = "";
	private String end_date = "";
	private String file = "";// zip包地址
	private String html_file = "";// html文件名
	private int html_duration;// html显示时间长度

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getHtml_file() {
		return html_file;
	}

	public void setHtml_file(String html_file) {
		this.html_file = html_file;
	}

	public int getHtml_duration() {
		return html_duration;
	}

	public void setHtml_duration(int html_duration) {
		this.html_duration = html_duration;
	}
}
