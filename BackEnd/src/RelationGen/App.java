package RelationGen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.driver.v1.Values;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;


@Path("main")
public class App {

	@GET
	@Path("update")
	public void main() {
		System.out.println("Getting database usage information");

		boolean dataBaseGen = true;
		boolean klientGen = true;
		boolean etikettGen = true;
		// Generera Etiketter
		if(etikettGen) {
			 sakerStallEtiketer();
			}

		// Database SQLs
		if (dataBaseGen) {
			List<SqlFileInfo> infoList = getDbInfo();
			sakerStallDbKopplingar(infoList);
		}
		// Klient kopplingar
		if (klientGen) {

			List<KlientInfo> klientInfoList = getKlientInfo();
			sakerStallKlientKopplingar(klientInfoList);
		}

	}

	private List<SqlFileInfo> getDbInfo() {
		List<SqlFileInfo> sql = hamtaSqlFilInfo();

		// Parsa hämtad SQL för att hitta de tabeller som används.
		Iterator<SqlFileInfo> it = sql.iterator();
		int position = 0;
		while (it.hasNext()) {
			SqlFileInfo info = it.next();
			position++;
			Statement stmt;
			String baseSql = "";
			try {
				baseSql = info.getSqlText();
				// Vi är inte intresseade av villkor och liknande
				// Dessa är dessutom ofta ej parsebara av vår parser.
				baseSql = baseSql.replaceAll("where .*", "");
				baseSql = baseSql.replaceAll("fetch first ?.*", "");
				baseSql = baseSql.replaceAll("with  ?.*", "");
				baseSql = baseSql.replaceAll("on .*", "");

				List<String> tableList = hittaTabellNamn(info.getSqlKey(), baseSql);

				Iterator<String> tableIt = tableList.iterator();
				System.out.print(info.getSqlKey() + "->");
				while (tableIt.hasNext()) {
					System.out.print(tableIt.next());
					if (tableIt.hasNext())
						System.out.print(", ");
				}
				System.out.println();
				info.setTables(tableList);

			} catch (JSQLParserException e) {
				System.err.println("Info " + info);
				System.err.println("Pos " + position + " Exception for " + baseSql);
				e.printStackTrace();
			}

		}
		return sql;
	}

	String DB_KOPPLING = "MATCH (c:Class:CSN {fileName:$classFile}), " + "(x:Xml:File {fileName:$xmlFile}) "
			+ " MERGE (c) -[:USES_DB_FILE]->(x)" + " MERGE (t:Table:Db {name:$tableName}) "
			+ " MERGE (c) -[:HAS_DB_OP]-> (op:Operation:Db {name:$opName, sql:$sqlString}) " + " -[:ON_TABLE]-> (t)"
			+ " RETURN c.fqn";

	String[] SET_NODE_TYPES = { "match (o:Operation:Db) where o.name starts with \"read\" set o:Read return count(o);",
			"match (o:Operation:Db) where o.name starts with \"update\" set o:Update return count(o);",
			"match (o:Operation:Db) where o.name starts with \"create\" set o:Create return count(o);",
			"match (o:Operation:Db) where o.name starts with \"delete\" set o:Delete return count(o);" };

	/**
	 * Skapa Operation:Db och Table:Db i tabellen för de sqlsatser som har hittats
	 * med sina tabeller. Operationer och Tabeller som redan finns ändras ej.
	 * 
	 * @param infoList
	 */
	private void sakerStallDbKopplingar(List<SqlFileInfo> infoList) {

		Driver boltDriver = null;
		try {
			boltDriver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "pwd"));
			try (Session session = boltDriver.session()) {

				Iterator<SqlFileInfo> infoIt = infoList.iterator();

				// Skapa kopplingar för varje operation
				while (infoIt.hasNext()) {
					SqlFileInfo info = infoIt.next();
					String classFile = info.getXmlFile().replaceAll(".xml", ".class");

					List<String> tableList = info.getTables();
					if (tableList == null) {
						System.out.println("Inga tabeller funna för " + info.getXmlFile() + ":" + info.getSqlKey());
						continue;
					}

					// Skapa för varje tabell
					Iterator<String> tableIt = tableList.iterator();
					while (tableIt.hasNext()) {
						System.out.print(' ');
						String tableName = tableIt.next();
						String result = session.writeTransaction(new TransactionWork<String>() {
							@Override
							public String execute(Transaction tx) {
								StatementResult result = tx.run(DB_KOPPLING,
										Values.parameters("classFile", classFile, "xmlFile", info.getXmlFile(),
												"opName", info.getSqlKey(), "sqlString", info.getSqlText(), "tableName",
												tableName));
								return result.single().get(0).asString();
							}
						});
						// System.out.println("Got Result " + result);
					}
					System.out.println();
				}

				// Sätt nodtyper för varje databasoperation
				String result = session.writeTransaction(new TransactionWork<String>() {

					@Override
					public String execute(Transaction tx) {
						String strResult = "";
						for (int i = 0; i < SET_NODE_TYPES.length; i++) {
							StatementResult result = tx.run(SET_NODE_TYPES[i]);
							strResult += result.single().get(0).toString() + ":";
						}
						return strResult;
					}

				});
				System.out.println("Justerat nodetyper: " + result);

			}

		} finally {
			if (boltDriver != null)
				boltDriver.close();

		}
	}

	private List<String> hittaTabellNamn(String key, String baseSql) throws JSQLParserException {
		// Sqlparsern förstår inte except, vi är endast intresserade av tabeller så
		// vi delar upp sql i två delar som var och en kan förstås
		int exceptPos = baseSql.indexOf("except");
		if (exceptPos > 0) {
			String firstSql = baseSql.substring(0, exceptPos);
			String secondSql = baseSql.substring(exceptPos + 6);
			// System.out.println("Splitting for key " + key);
			List<String> firstList = hittaTabellNamnEnkel(firstSql);
			List<String> secondList = hittaTabellNamnEnkel(secondSql);
			firstList.addAll(secondList);
			return firstList;
		} else {
			return hittaTabellNamnEnkel(baseSql);
		}

	}

	private List<String> hittaTabellNamnEnkel(String baseSql) throws JSQLParserException {
		Statement stmt;
		stmt = CCJSqlParserUtil.parse(baseSql);
		TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
		List<String> tableList = tablesNamesFinder.getTableList(stmt);
		return tableList;
	}

	private static String XML_DB_MATCHES = "MATCH (file:Xml:File)-[:HAS_ROOT_ELEMENT]->(propNode {name:'properties'})"
			+ " -[:HAS_ELEMENT]->(entryNode {name:'entry'})" + " -[:HAS_FIRST_CHILD]->(textStart:Xml:Text),"
			+ " (entryNode) -[:HAS_ATTRIBUTE]->(keyNode:Attribute)," + " (entryNode) -[:HAS_LAST_CHILD]->(textEnd),"
			+ " p=(textStart)-[:HAS_SIBLING*0..]->(textEnd)"
			+ " return file.fileName as xmlFile , keyNode.value as sqlKey,   "
			+ " extract ( x IN nodes(p) | x.value)  as sqlTexts"
	// +" limit 10"
	;

	/**
	 * Läs information från alla XML filer som har databasoperationer
	 * 
	 * @return
	 */
	private List<SqlFileInfo> hamtaSqlFilInfo() {
		List<SqlFileInfo> result = new ArrayList<SqlFileInfo>();

		Driver boltDriver = null;
		try {
			boltDriver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "pwd"));

			try (Session session = boltDriver.session()) {

				StatementResult boltResult = session.run(XML_DB_MATCHES);
				System.out.println("Result : " + boltResult.toString());
				while (boltResult.hasNext()) {
					Record nextResult = boltResult.next();
					SqlFileInfo info = new SqlFileInfo();
					info.setFromMap("", nextResult.asMap());
					result.add(info);
				}

			}

		} finally {
			if (boltDriver != null)
				boltDriver.close();

		}

		return result;
	}

	/**
	 * Omvandla lista av map till en map. Map elementen i listan förutsätts ha
	 * nycklar "name" och "value", varje sådant par blir en entry i returnlistan.
	 * Dvs {"name" -> "A", "value" -> "B"} --> {"A" -> "B"}
	 * 
	 * @param list
	 * @return
	 */
	private Map<String, String> mapListTillMap(List<Map<String, Object>> list) {
		Map<String, String> result = new HashMap<String, String>();

		Iterator<Map<String, Object>> it = list.iterator();
		while (it.hasNext()) {
			Map<String, Object> curr = it.next();
			String currName = curr.get("name").toString();
			String currVal = curr.get("value").toString();
			// System.out.println("Adding map " + currName + " -> " + currVal);
			result.put(currName, currVal);
		}

		return result;
	}

	private String sokVillkorClient = "MATCH (file:File:Xml {})-[:HAS_ROOT_ELEMENT]->(procNode {name: \"procedure\"} ), "
			+ "(procNode)-[:HAS_ATTRIBUTE]->(attr) " + "WHERE  not file.fileName contains \"server\" "
			// "WHERE file.fileName =
			// \"/se/csn/stis/sh/provning/client/resources/procedures/Sh13K613CDaglResorBesluta.xml\"
			// " +
			+ "with file.fileName as xmlFile, procNode, collect(attr) as procAttrs "
			+ "optional match (procNode)-[:HAS_ELEMENT]->(onElement {name:\"on\"})-[:HAS_ATTRIBUTE]->(onAttr)  "
			+ "RETURN xmlFile, procAttrs, id(onElement) as id, collect(onAttr) as onAttrs";

	private List<KlientInfo> getKlientInfo() {
		List<KlientInfo> result = new ArrayList<KlientInfo>();
		System.out.println("Hämtar information om klienter");
		Driver boltDriver = null;
		int raknare = 0;
		try {
			boltDriver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "pwd"));

			try (Session session = boltDriver.session()) {

				StatementResult boltResult = session.run(sokVillkorClient);
				while (boltResult.hasNext()) {
					Record nextResult = boltResult.next();
					// System.out.println("Got result : " + nextResult.toString());

					String xmlFile = nextResult.get("xmlFile", "");
					int onElementId = nextResult.get("id", -1);

					if (onElementId == -1) {
						System.out.println("\nNo On for file " + xmlFile);
					}

					List<Map<String, Object>> procMapList = nextResult.get("procAttrs").asList(v -> v.asMap());
					List<Map<String, Object>> onMapList = nextResult.get("onAttrs").asList(v -> v.asMap());

					Map<String, String> procMap = mapListTillMap(procMapList);
					Map<String, String> onMap = mapListTillMap(onMapList);
					KlientExitInfo exitInfo = new KlientExitInfo(onMap);
					KlientInfo info = new KlientInfo(xmlFile, procMap, exitInfo);
					// System.out.println("Info : " + info);
					raknare++;
					System.out.print(".");
					if (raknare > 80) {
						System.out.println();
						raknare = 0;
					}
					result.add(info);
				}

			}

		} finally {
			if (boltDriver != null)
				boltDriver.close();

		}

		System.out.println("\nFick " + result.size() + " rader med klientflöden");
		return result;
	}

	// --------------------------------------------------------------------------------
	// Klientkopplingarna returnerar c.fqn för att möjliggöra spårningsutskrifter
	// ---------------------------------------------------------------------------------

	// Grundstomme i klientkoppling
	static String KLIENT_KOPPLING_BAS = "MATCH (c:CSN {fqn:$classFqn}), " + "(x:Xml:File {fileName:$xmlFile}) "
			+ "WHERE c:Class OR c:Interface" + " MERGE (c) -[:HAS_CLIENT_XML]->(x)"
			+ " MERGE (client:Client:Gui {name:$clientName, programName:$programName, classFqn:$classFqn, "
			+ " transactionName:$transactionName}) " + " MERGE (client) -[:HAS_CLIENT_XML]->(x)"
			+ " MERGE (c) -[:HAS_CLIENT] -> (client)";

	// Denna tas med om vi har ett exit state
	static String KLIENT_KOPPLING_EXIT = " MERGE (clientExit:ClientExit:Gui {name:$exitState, forClient:$clientName,"
			+ "    displayFirst:$displayFirst, action:$action, returnWhen:$returnWhen, "
			+ "    returnCommand:$returnCommand, to:$targetClientName} ) "
			+ " MERGE (client) -[:HAS_EXIT_STATE]-> (clientExit) ";

	// Möjliggör spårutskrift
	static String KLIENT_KOPPLING_RETURN = " RETURN DISTINCT c.fqn as fqn";

	// Totala Cyphersträngar, en för klienter med exit, en för klienter utan
	static String KLIENT_KOPPLING_MED_EXIT = KLIENT_KOPPLING_BAS + KLIENT_KOPPLING_EXIT + KLIENT_KOPPLING_RETURN;
	static String KLIENT_KOPPLING_UTAN_EXIT = KLIENT_KOPPLING_BAS + KLIENT_KOPPLING_RETURN;

	static String KOPPLA_EXIT_TILL_KLIENT = "MATCH (cl:Client), (exit:ClientExit) " + " WHERE cl.name = exit.to"
			+ " MERGE (exit) -[:EXITS_TO]->(cl)" + " RETURN count(exit)";

	/**
	 * Skapa Client och ClientExit noder utifrån vår datastruktur
	 * 
	 * @param clientInfoList
	 */
	private void sakerStallKlientKopplingar(List<KlientInfo> clientInfoList) {

		System.out.println("Skapar relationer för klienter");

		Driver boltDriver = null;
		int raknare = 0;
		try {
			boltDriver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "pwd"));
			try (Session session = boltDriver.session()) {
				Iterator<KlientInfo> infoIt = clientInfoList.iterator();

				// Skapa kopplingar för varje operation
				while (infoIt.hasNext()) {
					KlientInfo info = infoIt.next();
					String classFqn = info.getClassFqn();

					KlientExitInfo exitInfo = info.getExitInfo();

					String cypher;
					String exitType;
					if (exitInfo.getTo() == null) {
						cypher = KLIENT_KOPPLING_UTAN_EXIT;
						exitType = "UTAN EXIT";
					} else {
						cypher = KLIENT_KOPPLING_MED_EXIT;
						exitType = "MED EXIT";
					}

					String result = session.writeTransaction(new TransactionWork<String>() {
						@Override
						public String execute(Transaction tx) {
							// System.err.println("Matching : " + classFqn);

							try {

								StatementResult result = tx.run(cypher, Values.parameters("classFqn", classFqn,
										"xmlFile", info.getXmlFile(), "clientName", info.getClientName(), "programName",
										info.getProgramName(), "transactionName", info.getTransaction(), "exitState",
										exitInfo.getExitState(), "displayFirst",
										(exitInfo.isDisplayFirst() ? "true" : "false"), "action", exitInfo.getAction(),
										"returnWhen", exitInfo.getReturnWhen(), "returnCommand",
										exitInfo.getReturnCommand(), "targetClientName", exitInfo.getTo()));
								System.out.println("Summary: " + result.summary());
								// Ibland fås inget resultat, ibland fås flera men db verkar ok??
								String strResult = "";
								while (result.hasNext()) {
									Record nextResult = result.next();
									// System.out.println("R : " + nextResult);
									strResult += nextResult.get("fqn", "fqn");
								}
								return strResult;
							} catch (Exception e) {
								System.err.println("\nFel för xmlfil " + info.getXmlFile() + " och class " + classFqn);
								System.err.println("Exit :" + exitType);
								System.err.println("Exception :" + e.getMessage());
								return "-";
							}
						}
					});
					// System.out.println("Result : " + result);
					System.out.print(".");
					raknare++;
					if (raknare > 80) {
						System.out.println();
						raknare = 0;
					}
				}

				String kopplaResultat = utforEnkelNodeOperation(session, KOPPLA_EXIT_TILL_KLIENT);
				System.out.println("Kopplat klientexits : " + kopplaResultat);

			}

		} finally {
			if (boltDriver != null)
				boltDriver.close();

		}
		System.out.println("\nKlientrelationer skapade");

	}

	/*
	 * Kör uppdateringar på jqAssistant resultatet som kan göras med enkla Cypher
	 * TODO: Sätt CSN mfl nodtyper
	 */
	private void utforGrundOperationer() {
		Driver boltDriver = null;
		try {
			boltDriver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "pwd"));
			try (Session session = boltDriver.session()) {
				String x = utforEnkelNodeOperation(session, "MATCH (c:Client) RETURN count(c)");
				System.out.println("Antal klienter :" + x);
			}
		} catch (Exception e) {
			System.err.println("Fel för grundoperationer");
			e.printStackTrace();
		} finally {
			if (boltDriver != null)
				boltDriver.close();
		}

	}

	private String utforEnkelNodeOperation(Session session, String operation) {
		String result = session.writeTransaction(new TransactionWork<String>() {
			@Override
			public String execute(Transaction tx) {
				String strResult = "";
				StatementResult result = tx.run(operation);
				strResult += result.single().get(0).toString() + ":";

				return strResult;
			}
		});
		return result;
	}
	
	private void sakerStallEtiketer() {
		
		
		List<String> etikettCypher = new ArrayList<>();
		
		etikettCypher.add("MATCH (p:Class)-[:EXTENDS]->(t:Type) WHERE p.fqn CONTAINS '.server.' AND NOT p.fqn CONTAINS '.lab.' AND t.fqn = 'com.bphx.cool.Action' SET p:Server RETURN count(p)");
		etikettCypher.add("MATCH (p:Class)-[:EXTENDS]->(t:Type) WHERE t.fqn = 'com.bphx.cool.Action' SET p:ExtendsAction RETURN count(p)");
		
		etikettCypher.add("MATCH (t:Type) WHERE t.fqn STARTS WITH 'se.csn.' SET t:CSN RETURN count(t)");
		
		
		
		
		
		System.out.println("Sätter Etiketter");
	
		Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "pwd"));
		  try ( Session session = driver.session() )
	        {
	            String greeting = session.writeTransaction( new TransactionWork<String>()
	            {
	                @Override
	                public String execute( Transaction tx )
	                {
	                	for(String s : etikettCypher) {
	                		StatementResult result = tx.run(s);
	                		System.out.println(result.summary());
	                		
	          
	                	}
	                	System.out.println("done");
	                    return "done";
	                }
	            } );
	        }
		

	}
	
	

}
