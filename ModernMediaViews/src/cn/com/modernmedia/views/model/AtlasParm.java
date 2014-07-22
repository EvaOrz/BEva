package cn.com.modernmedia.views.model;

/**
 * 图集参数
 * 
 * @author user
 * 
 */
public class AtlasParm {
	public static final float LADY_HEIGHT = 0.7f;// ilady高为屏幕的7/10
	private String type = ""; // 应用类型
	private String placeholder = ""; // 占位图
	private int width = 0;// business:fill_parent;ilady:根据高等比算
	private int height;// business:按720等比算；
	private String image_dot = "";// 图集dot
	private String image_dot_active = "";// 图集选中dot

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getImage_dot() {
		return image_dot;
	}

	public void setImage_dot(String image_dot) {
		this.image_dot = image_dot;
	}

	public String getImage_dot_active() {
		return image_dot_active;
	}

	public void setImage_dot_active(String image_dot_active) {
		this.image_dot_active = image_dot_active;
	}

}
