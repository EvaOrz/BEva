package cn.com.modernmedia.model;

/**
 * ����
 * 
 * @author ZhuQiao
 * 
 */
public class Share extends Entry {
	private static final long serialVersionUID = 1L;
	private int id;// ����id
	private String title = "";
	private String content = "";
	private String weburl = "";// ԭ��url

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getWeburl() {
		return weburl;
	}

	public void setWeburl(String weburl) {
		this.weburl = weburl;
	}

}
