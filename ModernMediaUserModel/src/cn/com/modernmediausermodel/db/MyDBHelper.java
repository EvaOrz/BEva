package cn.com.modernmediausermodel.db;

import java.util.ArrayList;
import java.util.List;

public class MyDBHelper {
	public static final String SPLIT = "##";
	private String tableName = "";
	private List<String> list = new ArrayList<String>();

	public MyDBHelper(String tableName) {
		this.tableName = tableName;
	}

	public void addColumn(String name, String type) {
		String column = name + " " + type;
		list.add(column);
	}

	public String getSql() {
		String sql = "create table " + tableName + " (";
		for (String str : list) {
			sql += str + ",";
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += ")";
		return sql;
	}
}
