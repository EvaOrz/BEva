package cn.com.modernmediaslate.model;

public class ErrorMsg extends Entry {
    private static final long serialVersionUID = 1L;
    private int no = 0;
    private String desc;

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
