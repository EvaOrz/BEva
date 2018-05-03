package cn.com.modernmedia.model;

import android.content.Intent;
import cn.com.modernmediaslate.model.Entry;

/**
 * 分享列表item
 * 
 * @author user
 * 
 */
public class ShareDialogItem extends Entry {
	private static final long serialVersionUID = 1L;
	private Intent intent;// 系统分享各个app的intent
	private int icon;// 默认分享app的icon
	private String name;// 默认分享app的name
	private int id;// 默认分享app的id,用来区分更多

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
