package cn.com.modernmedia.model;

/**
 * 装机统计
 * 
 * @author ZhuQiao
 * 
 */
public class Down extends Entry {
	private static final long serialVersionUID = 1L;
	private boolean success;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
