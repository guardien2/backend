package rest4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
import org.neo4j.driver.v1.types.Node;

//import java.util.ArrayList;
//import java.util.List;

import org.neo4j.driver.v1.types.Path.Segment;
import org.neo4j.driver.v1.types.Relationship;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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

	@GET
	@Path("class")
	@Produces("application/json")
	public String ClassFind() {
		Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
		try (Session session = driver.session()) {
			StatementResult result = session.run(
					"MATCH q=(p:Server)-[:DEPENDS_ON*..5]->(a:Type:CSN {valid: true}) WHERE upper(p.name) =~ \".*KN50.*\" AND NOT a.fqn CONTAINS \"entities\" AND NOT a.fqn CONTAINS \"worksets\" AND NOT a.name CONTAINS \"$\" RETURN p,a,q");

			String json = null;
			List<FullExpansion> nameList = new ArrayList<>();

			// Jackson ObjectMapper used to serialize java object as JSON output
			ObjectMapper objectMapper = new ObjectMapper();

			org.neo4j.driver.v1.types.Path p;
			// loop through all data

			List<Relationship> relationshipList = new ArrayList<>();
			List<org.neo4j.driver.v1.types.Node> nodeList = new ArrayList<>();

			while (result.hasNext()) {
				// ObjectNode jNode = (ObjectNode) objectMapper.createObjectNode();
				Record res = result.next();
				// annars tror den den är jackson path
				p = res.get("q").asPath();
				// System.out.println(res.get("q"));

				for (Segment segment : p) {

					if (!relationshipList.contains(segment.relationship())) {
						relationshipList.add(segment.relationship());
					}
					if (!nodeList.contains(segment.start())) {
						nodeList.add(segment.start());
						if (!nodeList.contains(segment.end())) {
							nodeList.add(segment.end());
						}

					}

				}

			}

			for (Relationship rList : relationshipList) {

				for (Node nList : nodeList) {
					String startNode = rList.startNodeId() + "";
					String endNode = rList.endNodeId() + "";
					List<NestedJson> nextList = new ArrayList<>();

					if (rList.startNodeId() == nList.id()) {
						NestedJson start = new NestedJson(startNode);
						NestedJson Next = new NestedJson(endNode);
						nextList.add(Next);

						System.out.println(rList.startNodeId() + " = " + nList.id());
					}

				}

			}

			System.out.println(relationshipList);
			System.out.println(nodeList);

			json = objectMapper.writeValueAsString(nameList);
			// System.out.println(json);

			return json;

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "Cant Connect to DB";
	}

	@GET
	@Path("tree")
	@Produces("application/json")
	public String BuildTheTree() {
		Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
		try (Session session = driver.session()) {

			StatementResult result = session.run(
					"MATCH q=(p:Server)-[:DEPENDS_ON*..5]->(a:Type:CSN {valid: true}) WHERE upper(p.name) =~ \".*KN50.*\" AND NOT a.fqn CONTAINS \"entities\" AND NOT a.fqn CONTAINS \"worksets\" AND NOT a.name CONTAINS \"$\" RETURN p,a,q");

			String json = null;

			ObjectMapper objectMapper = new ObjectMapper();

			org.neo4j.driver.v1.types.Path p;

			List<Relationship> relationshipList = new ArrayList<>();
			List<NodeWithNext> nodes = new ArrayList<>();

			// fyller lista med noder & lista med relationer
			while (result.hasNext()) {
				Record res = result.next();
				p = res.get("q").asPath();

				System.out.print(p);

				for (Segment segment : p) {
					if (!relationshipList.contains(segment.relationship())) {
						relationshipList.add(segment.relationship());
					}
					if (!containsNodeID(nodes, segment.start().id())) {
						nodes.add(new NodeWithNext(segment.start()));
					}
					if (!containsNodeID(nodes, segment.end().id())) {
						nodes.add(new NodeWithNext(segment.end()));
					}
				}
			}

			// bygger relationerna
			for (NodeWithNext nwn : nodes) {
				for (Relationship r : relationshipList) {
					if (nwn.node.id() == r.startNodeId()) {
						nwn.nextList.add(getNodeFromID(nodes, r.endNodeId()));
					}
				}
			}

			// json
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			json = objectMapper.writeValueAsString(nodes);
			return json;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "Cant Connect to DB";
	}

	// kollar om noden med ett visst ID finns i listan
	public boolean containsNodeID(final List<NodeWithNext> list, final Long id) {
		return list.stream().map(NodeWithNext::getNodeID).filter(id::equals).findFirst().isPresent();
	}

	// hittar och ger noden i listan som har rätt ID
	public NodeWithNext getNodeFromID(List<NodeWithNext> list, Long id) {
		for (NodeWithNext nwn : list) {
			if (nwn.node.id() == id) {
				return nwn;
			}
		}

		System.out.print("Hittar inte nod i lista");
		return null;
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
			List<NodeUsedBy> nodes = new ArrayList<>();
			NodeUsedBy n = null;
			String json = null;

			while (result.hasNext()) {
				Record res = result.next();
				// System.out.println(res.get("ab").get("sourceFileName"));

				fqn = res.get("ab").get("fqn").toString();
				sourceFileName = res.get("ab").get("sourceFileName").toString();
				name = res.get("ab").get("name").toString();

				n = new NodeUsedBy(fqn, sourceFileName, name);

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
