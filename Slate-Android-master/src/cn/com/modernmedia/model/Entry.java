package cn.com.modernmedia.model;

import java.io.Serializable;

/**
 * ����model��ĸ��֧࣬�ַ��ͣ�����
 * 
 * @author ZhuQiao
 * 
 */
public class Entry implements Serializable {
	private static final long serialVersionUID = 1L;
	private String template = "";// ģ��

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

}
