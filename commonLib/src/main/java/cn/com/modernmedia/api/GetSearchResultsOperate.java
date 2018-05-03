package cn.com.modernmedia.api;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 文搜索接口operate
 *
 * @author: zhufei
 */
public class GetSearchResultsOperate extends GetTagArticlesOperate {
    private String msg,top;
    private long start, end;

    public GetSearchResultsOperate(String msg, long start, long end,String top) {
        this.msg = msg;
        this.top = top ;
        this.start = start;
        this.end = end;
    }

    @Override
    protected String getUrl() {
        return UrlMaker.getSearchResult(msg, start, end,top);
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        JSONArray articletag = jsonObject.optJSONArray("articletag");
        if (isNull(articletag)) return;
        parseArr(articletag);
    }

    @Override
    protected void saveData(String data) {
    }

    @Override
    protected String getDefaultFileName() {
        return null;
    }

}
