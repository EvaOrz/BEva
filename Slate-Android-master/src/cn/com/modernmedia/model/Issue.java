package cn.com.modernmedia.model;


/**
 * 期数据
 * 
 * @author ZhuQiao
 * 
 */
public class Issue extends Entry {
	private static final long serialVersionUID = 1L;
	private int id;// 最新一期期id
	private String coverpic = "";// 这一期的封面图片
	private long columnUpdateTime;// 列表页最新更新时间
	private long articleUpdateTime;// 最近更新的文章的更新时间
	private Adv adv = new Adv();

	public Issue() {
	}

	public Issue(int id, long columnUpdateTime, long articleUpdateTime) {
		this.id = id;
		this.columnUpdateTime = columnUpdateTime;
		this.articleUpdateTime = articleUpdateTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCoverpic() {
		return coverpic;
	}

	public void setCoverpic(String coverpic) {
		this.coverpic = coverpic;
	}

	public long getColumnUpdateTime() {
		return columnUpdateTime;
	}

	public void setColumnUpdateTime(long columnUpdateTime) {
		this.columnUpdateTime = columnUpdateTime;
	}

	public long getArticleUpdateTime() {
		return articleUpdateTime;
	}

	public void setArticleUpdateTime(long articleUpdateTime) {
		this.articleUpdateTime = articleUpdateTime;
	}

	public Adv getAdv() {
		return adv;
	}

	public void setAdv(Adv adv) {
		this.adv = adv;
	}

}
