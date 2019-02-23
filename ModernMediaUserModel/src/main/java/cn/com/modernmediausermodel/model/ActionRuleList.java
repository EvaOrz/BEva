package cn.com.modernmediausermodel.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmediaslate.model.Entry;

/**
 * 商品
 *
 * @author user
 */
public class ActionRuleList extends Entry {
    private static final long serialVersionUID = 1L;
    private String uid;
    private int cent;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getCent() {
        return cent;
    }

    public void setCent(int cent) {
        this.cent = cent;
    }

    private List<ActionRuleItem> list = new ArrayList<ActionRuleList.ActionRuleItem>();

    public List<ActionRuleItem> getList() {
        return list;
    }

    public void setList(List<ActionRuleItem> list) {
        this.list = list;
    }

    public static class ActionRuleItem extends Entry {
        private static final long serialVersionUID = 1L;
        private String id = "";// id
        private String name = "";// id
        private int appId;// 应用id
        private String title = "";// 标题
        private String desc = "";// 描述
        private int cent = 0; // 添加的金币数
        private int poptype = 0; // 弹出框类型(预留)
        private int popdisplay = 0; // 是否弹出(预留)
        private String rulestatusmessage = "";
        private int rulestatus = 0;

        public int getRulestatus() {
            return rulestatus;
        }

        public void setRulestatus(int rulestatus) {
            this.rulestatus = rulestatus;
        }

        public String getRulestatusmessage() {
            return rulestatusmessage;
        }

        public void setRulestatusmessage(String rulestatusmessage) {
            this.rulestatusmessage = rulestatusmessage;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAppId() {
            return appId;
        }

        public void setAppId(int appId) {
            this.appId = appId;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getCent() {
            return cent;
        }

        public void setCent(int cent) {
            this.cent = cent;
        }

        public int getPoptype() {
            return poptype;
        }

        public void setPoptype(int poptype) {
            this.poptype = poptype;
        }

        public int getPopdisplay() {
            return popdisplay;
        }

        public void setPopdisplay(int popdisplay) {
            this.popdisplay = popdisplay;
        }
    }
}
