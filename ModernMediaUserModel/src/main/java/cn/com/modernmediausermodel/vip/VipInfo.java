package cn.com.modernmediausermodel.vip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.modernmediaslate.model.Entry;

/**
 * Vip信息 行业，职位，年收入
 *
 * @author: zhufei
 */
public class VipInfo extends Entry {
    public static final String INDUSTRY = "1";// 行业
    public static final String JOB = "2";// 职位
    public static final String INCOME = "3";// 年收入

    private static final long serialVersionUID = 1L;
    public Map<String, VipCategory> categoryMap = new HashMap<String, VipCategory>();
    public List<VipLevel> vipLevelList = new ArrayList<VipLevel>();
    public String updatetime = "";

    public static class VipCategory extends Entry {
        public String cate_id = "";
        public String category = "";
        public String pid = "";
        public String status = "";
        public String order = "";
        public List<VipChildCategory> firstCategoryList = new ArrayList<VipChildCategory>();
    }

    public static class VipChildCategory extends VipCategory {
        public List<VipChildCategory> secondCategoryList = new ArrayList<VipChildCategory>();
    }

    public static class VipLevel extends Entry {
        public int level;
        public String name;
    }
}
