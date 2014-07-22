package cn.com.modernmedia.db;

import java.util.ArrayList;
import java.util.List;

public class MyDBHelper {
	private String tableName = "";
	private List<String> list = new ArrayList<String>();

	public MyDBHelper(String tableName) {
		this.tableName = tableName;
	}

	protected void addColumn(String name, String type) {
		String column = name + " " + type;
		list.add(column);
	}

	protected String getSql() {
		String sql = "create table " + tableName + " (";
		for (String str : list) {
			sql += str + ",";
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += ")";
		return sql;
	}
}
