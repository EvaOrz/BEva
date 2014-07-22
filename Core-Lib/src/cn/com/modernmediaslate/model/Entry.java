package cn.com.modernmediaslate.model;

import java.io.Serializable;

/**
 * 所有model类的父类，支持泛型，复用
 * 
 * @author ZhuQiao
 * 
 */
public class Entry implements Serializable {
	private static final long serialVersionUID = 1L;
	private String template = "";// 模板

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

}
