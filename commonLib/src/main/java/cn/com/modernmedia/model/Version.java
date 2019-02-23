package cn.com.modernmedia.model;

import cn.com.modernmediaslate.model.Entry;

/**
 * 在线升级
 * 
 * @author ZhuQiao
 * 
 */
public class Version extends Entry {
	private static final long serialVersionUID = 1L;
	private int version = -1;
	private String src = "";
	private String changelog = "";
	private String download_url = "";

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getChangelog() {
		return changelog;
	}

	public void setChangelog(String changelog) {
		this.changelog = changelog;
	}

	public String getDownload_url() {
		return download_url;
	}

	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}

}
