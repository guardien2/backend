package Xml;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SqlFileInfo {
	
	private String xmlFile;
	private String sqlKey;
	private String sqlText;
	private List<String> tables;
	
	public SqlFileInfo()
	{
	}
	


	public void setFromMap(String prefix, Map<String, Object> map) {
		
		String file = (String) map.get("xmlFile");
		String key = (String) map.get("sqlKey");
		@SuppressWarnings("unchecked")
		List<String> sqlList = (List<String>) map.get("sqlTexts");
		
		this.xmlFile = file;
		this.sqlKey = key;
		
		// System.out.println("Sql " + sqlList);
		Iterator<String> it = sqlList.iterator();
		String sql = "";
		while (it.hasNext()) {
			String s = it.next();
			// System.out.println("Adding " + s);
			
			if (endsWithKeyword(s)) s = s+ " ";
			if (startsWithKeyword(s)) s = " " + s;
			
			sql = sql + s;
		}


		sql = sql.replace('\n', ' ');
		sql = sql.replaceAll(" +", " ");
		
		this.sqlText = sql;
	}
	
	public enum OP_TYPE {
			UNKNOWN,
			CREATE,
			READ,
			UPDATE,
			DELETE
	};
	
	public OP_TYPE getOperationType()
	{
		String name = getSqlKey();
		if (name == null) {
			return OP_TYPE.UNKNOWN;
		} else if (name.startsWith("read")) {
			return OP_TYPE.READ;
		} else if (name.startsWith("update")) {
			return OP_TYPE.UPDATE;
		} else if (name.startsWith("delete")) {
			return OP_TYPE.DELETE; 
		} else if (name.startsWith("create")) {
			return OP_TYPE.CREATE;
		} else {
			return OP_TYPE.UNKNOWN;
		}		
		
	}
	
	private String[] keyWords = 
			{"select", "from", "join", "where",
			 "set", "order","by", "except","on","group",
			 "fetch", "first", "row", "rows", "only"};
					


	private boolean startsWithKeyword(String s) {
		for (int i = 0; i< keyWords.length; i++) {
			if (s.startsWith(keyWords[i])) return true;
		}
		return false;
	}

	private boolean endsWithKeyword(String s) {
		for (int i = 0; i< keyWords.length; i++) {
			if (s.endsWith(keyWords[i])) return true;
		}
		return false;
	}

	
	
	public String toString()
	{
		return "[[" +  xmlFile + ":::" + sqlKey + ":::" + sqlText + "]]"; 
	}

	public String getXmlFile() {
		return xmlFile;
	}

	public void setXmlFile(String xmlFile) {
		this.xmlFile = xmlFile;
	}

	public String getSqlKey() {
		return sqlKey;
	}

	public void setSqlKey(String sqlKey) {
		this.sqlKey = sqlKey;
	}

	public String getSqlText() {
		return sqlText;
	}

	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}


	public List<String> getTables() {
		return tables;
	}


	public void setTables(List<String> tables) {
		this.tables = tables;
	}
	
	
	
	

}

