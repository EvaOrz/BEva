package cn.com.modernmediausermodel.model;

import cn.com.modernmediaslate.model.Entry;

/**
 * 上传头像结果
 * 
 * @author JianCong
 * 
 */
public class UploadAvatarResult extends Entry {
	private static final long serialVersionUID = 1L;
	private String status;
	private String msg;
	private String imagePath;// 要提交表单的头像相对地址
	private String avatarPath;// 完整头像地址

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getAvatarPath() {
		return avatarPath;
	}

	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
	}

}
