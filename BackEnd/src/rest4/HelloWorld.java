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
	@Path("tree/{type}/{searchValue}/{graphBool}")
	@Produces("application/json")
	public String BuildTheTree(@PathParam("type") String type, @PathParam("searchValue") String searchValue,
			@PathParam("graphBool") Boolean graphBool) {
		Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));

		String cypherQuery = "";
		if (type.equals("usedby")) {
			cypherQuery = "MATCH q=(ab:Class:CSN)-[:DEPENDS_ON {resolved: true}]->(t:Type:CSN) "
					+ "WHERE upper(t.name) CONTAINS \"" + searchValue.toUpperCase()
					+ "\" AND NOT t.name CONTAINS \"$\" " + "AND NOT ab.name CONTAINS \"$\" RETURN ab,t,q";
		} else if (type.equals("fullexpansion")) {

			cypherQuery = "MATCH q=(p:Server)-[:DEPENDS_ON*..5]->(a:Type:CSN {valid: true}) "
					+ "WHERE upper(p.name) =~ \".*" + searchValue.toUpperCase()
					+ ".*\" AND NOT a.fqn CONTAINS \"entities\"AND NOT a.fqn CONTAINS \"worksets\" "
					+ "AND NOT a.name CONTAINS \"$\" RETURN p,a,q";
		} else if (type.equals("crud")) {
			cypherQuery = "MATCH q=(c:Class:CSN)-[:DECLARES]->(m:Method) WHERE upper(m.name) =~ \"(CREATE|READ|UPDATE|DELETE).*"
					+ searchValue.toUpperCase() + ".*\" RETURN c,m,q";
		} else if (type.equals("flowin")) {
			// TODO
		} else if (type.equals("flowout")) {
			// TODO
		} else {
			// something went wrong
			System.out.println("hehe xD");
		}

		try (Session session = driver.session()) {

			StatementResult result = session.run(cypherQuery);

			String json = null;

			ObjectMapper objectMapper = new ObjectMapper();

			org.neo4j.driver.v1.types.Path p;

			List<Relationship> relationshipList = new ArrayList<>();
			List<NodeWithNext> nodes = new ArrayList<>();
			List<NodeWithNext> roots = new ArrayList<>();
			List<NodeWithNext> headList = new ArrayList<>();

			NodeWithNext headNode = new NodeWithNext();

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
			if (type.equals("fullexpansion") || type.equals("crud")) {
				// bygger relationerna f�r fullexpansion
				for (NodeWithNext nwn : nodes) {
					System.out.println(nwn.id);
					for (Relationship r : relationshipList) {
						if (nwn.node.id() == r.startNodeId()) {
							nwn.children.add(getNodeFromID(nodes, r.endNodeId()));
						}

						if (nwn.node.id() == r.endNodeId()) {
							nwn.parents.add(getNodeFromID(nodes, r.startNodeId()));
						}

					}
					if (nwn.children.isEmpty()) {
						nwn.children = null;
					}
					if (nwn.parents.isEmpty()) {
						roots.add(nwn);
					}
				}
			} else if (type.equals("usedby")) {
				// bygger relationerna f�r used by
				for (NodeWithNext nwn : nodes) {
					System.out.println(nwn.id);
					for (Relationship r : relationshipList) {
						if (nwn.node.id() == r.endNodeId()) {
							nwn.children.add(getNodeFromID(nodes, r.startNodeId()));
						}

						if (nwn.node.id() == r.startNodeId()) {
							nwn.parents.add(getNodeFromID(nodes, r.endNodeId()));
						}

					}
					if (nwn.children.isEmpty()) {
						nwn.children = null;
					}
					if (nwn.parents.isEmpty()) {
						roots.add(nwn);
					}
				}
			}
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

			if (graphBool) {
				for (NodeWithNext n : roots) {
					headNode.children.add(n);
				}
				headList.add(headNode);
				json = objectMapper.writeValueAsString(headList);

			} else {
				json = objectMapper.writeValueAsString(roots);
			}

			return json;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "Cant Connect to DB";
	}

	// kollar om noden med ett visst ID finns i listan
	public boolean containsNodeID(final List<NodeWithNext> list, final Long id) {
		return list.stream().map(NodeWithNext::getId).filter(id::equals).findFirst().isPresent();
	}

	// hittar och ger noden i listan som har r�tt ID
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
	@Path("search/{test}/{searchString}")
	@Produces("application/json")
	public String UsedBy(@PathParam("test") String test, @PathParam("searchString") String searchString) {

		System.out.println(test);
		System.out.println(searchString);
		Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
		String cypherQuery = "";
		if (test.equals("usedby")) {
			cypherQuery = "MATCH (ab:Class:CSN)-[:DEPENDS_ON {resolved: true}]->(t:Type:CSN) "
					+ "WHERE upper(t.name) CONTAINS \"" + searchString.toUpperCase()
					+ "\" AND NOT t.name CONTAINS \"$\" " + "AND NOT ab.name CONTAINS \"$\" RETURN ab,t";
		} else if (test.equals("fullexpansion")) {
			cypherQuery = "MATCH q=(p:Server)-[:DEPENDS_ON*..5]->(a:Type:CSN {valid: true}) "
					+ "WHERE upper(p.name) =~ \".*" + searchString.toUpperCase()
					+ ".*\" AND NOT a.fqn CONTAINS \"entities\"AND NOT a.fqn CONTAINS \"worksets\" "
					+ "AND NOT a.name CONTAINS \"$\" RETURN p,a,q";
		} else if (test.equals("crud")) {
			// TODO
		} else if (test.equals("flowin")) {
			// TODO
		} else if (test.equals("flowout")) {
			// TODO
		} else {
			// something went wrong
			System.out.println("hehe xD");
		}

		try (Session session = driver.session()) {
			StatementResult result = session.run(cypherQuery);

			String fqn = null;
			String sourceFileName = null;
			String name = null;
			List<UsedBy> nodes = new ArrayList<>();
			UsedBy n = null;
			String json = null;

			while (result.hasNext()) {
				Record res = result.next();
				// System.out.println(res.get("ab").get("sourceFileName"));

				fqn = res.get("ab").get("fqn").toString();
				sourceFileName = res.get("ab").get("sourceFileName").toString();
				name = res.get("ab").get("name").toString();

				n = new UsedBy(fqn, sourceFileName, name);

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

	@GET
	@Path("NodeGraph/{searchType}/{searchValue}/")
	@Produces("application/json")
	public String createRelations(@PathParam("searchType") String searchType,
			@PathParam("searchValue") String searchValue) {

		String cypherQuery = "";
		if (searchType.equals("usedby")) {
			System.out.println("hej");
			cypherQuery = "MATCH q=(ab:Class:CSN)-[:DEPENDS_ON {resolved: true}]->(t:Type:CSN) "
					+ "WHERE upper(t.name) CONTAINS \"" + searchValue.toUpperCase()
					+ "\" AND NOT t.name CONTAINS \"$\" " + "AND NOT ab.name CONTAINS \"$\" RETURN ab,t,q";
		} else if (searchType.equals("fullexpansion")) {
			cypherQuery = "MATCH q=(p:Server)-[:DEPENDS_ON*..5]->(a:Type:CSN {valid: true}) "
					+ "WHERE upper(p.name) =~ \".*" + searchValue.toUpperCase()
					+ ".*\" AND NOT a.fqn CONTAINS \"entities\"AND NOT a.fqn CONTAINS \"worksets\" "
					+ "AND NOT a.name CONTAINS \"$\" RETURN p,a,q";
		} else if (searchType.equals("crud")) {
			// TODO
		} else if (searchType.equals("flowin")) {
			// TODO
		} else if (searchType.equals("flowout")) {
			// TODO
		} else {
			// something went wrong
			System.out.println("hehe xD");
		}

		Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
		try (Session session = driver.session()) {
			
			StatementResult result = session.run(cypherQuery);
			
			String json = null;
			ObjectMapper objectMapper = new ObjectMapper();

			org.neo4j.driver.v1.types.Path p;

			List<Relationship> relationshipList = new ArrayList<>();
			List<Node> nodes = new ArrayList<>();
			NodeGraph nodegraph = new NodeGraph();
			List<NodeGraphLinks> linkList = new ArrayList<>();
			List<NodeGraphIds> idList = new ArrayList<>();

		
			// fyller lista med noder & lista med relationer
			while (result.hasNext()) {
				Record res = result.next();
				p = res.get("q").asPath();
				System.out.print(p);

				for (Segment segment : p) {
					if (!relationshipList.contains(segment.relationship())) {
						relationshipList.add(segment.relationship());
					}
					if (!nodes.contains(segment.start())) {
						nodes.add(segment.start());
					}
					if (!nodes.contains(segment.end())) {
						nodes.add(segment.end());
					}
				}

			}
			for (Relationship r : relationshipList) {
				NodeGraphLinks links = new NodeGraphLinks();
				links.setSource(r.startNodeId());
				links.setTarget(r.endNodeId());
				linkList.add(links);

			}
			for (Node n : nodes) {
				NodeGraphIds id = new NodeGraphIds(n);
				idList.add(id);
			}
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			nodegraph.setNodes(idList);
			nodegraph.setLinks(linkList);

			json = objectMapper.writeValueAsString(nodegraph);

			return json;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "cannot connect to db";

	}

}
