package rest4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

//import java.util.ArrayList;
//import java.util.List;

import org.neo4j.driver.v1.types.Path.Segment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Path("admin")

public class HelloWorld {

	private String uri = "bolt://localhost:7687";
	private String user = "";
	private String password = "";

	@GET
	@Path("hello")
	public String sayHello() {
		return "Hello World";
	}

	@GET
	@Path("{id}")
	@Produces("application/json")
	public Person hamtaPerson(@PathParam("id") int id) {
		System.out.println(String.format("Sï¿½ker efter person med id %d", id));
		Person p = new Person(id, "John", "Eriksson", 1995);
		return p;
	}

	@GET
	@Path("connect")

	public String connect() {
		Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
		try (Session session = driver.session()) {
			StatementResult result = session.run("MATCH (n:Java) RETURN n.name LIMIT 25");

			Record res = null;
			while (result.hasNext()) {
				res = result.next();
				System.out.println(res.get(0));

			}

			/*
			 * List<Record> storeList = storeList(result); for(Record r :storeList) {
			 * System.out.print(r.get(0)); }
			 */

			return res.toString();
		}

	}

	/*
	 * public List<Record> storeList(StatementResult statementResult) {
	 * 
	 * List<Record> list = new ArrayList<>(); while (statementResult.hasNext()) {
	 * System.out.println(statementResult.next()); list.add(statementResult.next());
	 * 
	 * }
	 * 
	 * return list;
	 * 
	 * }
	 */
	@GET
	@Path("class")
	@Produces("application/json")
	public String ClassFind() {
		Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
		try (Session session = driver.session()) {
			StatementResult result = session.run(
					"MATCH q=(p:Server)-[:DEPENDS_ON*..5]->(a:Type:CSN {valid: true}) WHERE upper(p.name) =~ \".*KN50.*\" AND NOT a.fqn CONTAINS \"entities\" AND NOT a.fqn CONTAINS \"worksets\" AND NOT a.name CONTAINS \"$\" RETURN p,a,q");

			String sourceFileName = null;
			String fqn = null;
			String json = null;
			List<FullExpansion> nameList = new ArrayList<>();

			FullExpansion n = null;
			FullExpansion endNode = null;
			// Jackson ObjectMapper used to serialize java object as JSON output
			ObjectMapper objectMapper = new ObjectMapper();
			// ObjectNode jNode = (ObjectNode) objectMapper.createObjectNode();
			// System.out.println(result.list());

			org.neo4j.driver.v1.types.Path p;
			// loop through all data
			while (result.hasNext()) {
				// ObjectNode jNode = (ObjectNode) objectMapper.createObjectNode();
				Record res = result.next();
				// annars tror den den är jackson path
				p = res.get("q").asPath();
				// System.out.println(res.get("q"));
				for (Segment segment : p) {
					// System.out.println(segment.start());
					// System.out.println(segment.end());
					// System.out.println(segment.relationship());
					// endNode = new FullExpansion(segment.end().toString());

					List<String> relationList = new ArrayList<String>();

					relationList.add(segment.start().toString());
					relationList.add(segment.end().toString());

					// n = new FullExpansion(segment.start().toString() ,endNode);
					
					ListIterator<String> it = relationList.listIterator();

					while (it.hasNext()) {
						  
						   if(it.equals(it.next())) {
							   it.remove();
						   }
							   
						}
				

					// nameList.add(n);
				}

			}

			json = objectMapper.writeValueAsString(nameList);
			// System.out.println(json);

			return json;

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return "Cant Connect to DB";

	}

	@GET
	@Path("usedby/{test}")
	@Produces("application/json")
	public String UsedBy(@PathParam("test") String test) {
		System.out.println(test);
		Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));

		try (Session session = driver.session()) {
			StatementResult result = session.run("MATCH (ab:Class:CSN)-[:DEPENDS_ON {resolved: true}]->(t:Type:CSN) "
					+ "WHERE upper(t.name) CONTAINS \"" + test.toUpperCase() + "\" AND NOT t.name CONTAINS \"$\" "
					+ "AND NOT ab.name CONTAINS \"$\" RETURN ab,t");

			String fqn = null;
			String sourceFileName = null;
			String name = null;
			List<Node> nodes = new ArrayList<>();
			Node n = null;
			String json = null;

			while (result.hasNext()) {
				Record res = result.next();
				// System.out.println(res.get("ab").get("sourceFileName"));

				fqn = res.get("ab").get("fqn").toString();
				sourceFileName = res.get("ab").get("sourceFileName").toString();
				name = res.get("ab").get("name").toString();

				n = new Node(fqn, sourceFileName, name);

				nodes.add(n);

			}

			ObjectMapper objectMapper = new ObjectMapper();
			json = objectMapper.writeValueAsString(nodes);
			System.out.println(json);

			return json;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "db connection error";

	}

	public void createRelations() {

	}

}
