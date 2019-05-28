package rest4;

import java.util.ArrayList;

import java.util.List;

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

import org.neo4j.driver.v1.types.Path.Segment;
import org.neo4j.driver.v1.types.Relationship;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import NodeGraph.NodeGraph;
import NodeGraph.NodeGraphIds;
import NodeGraph.NodeGraphLinks;

/**
 * <p>Den h�r klassen �r huvudelen av projektet, hela API:et som anv�nds av Frontend
 * 
 *
 *   
 * @author csn8029
 * @author csn8030
 * 
 */
@Path("admin")
public class BuildRest {

	private String uri = "bolt://localhost:7687";
	private String user = "";
	private String password = "";

	@GET
	@Path("hello")
	public String sayHello() {
		return "Hello World";
	}

	/**
	 * 	
	 * <h2>/app/admin/tree/{type}/{searchValu} path</h2> 
	 * 
	 * Skapar en tr�dstruktur i JSON av resultatet fr�n databasen.
	 * 
	 * @param type s�k-typen som det fr�gas efter, vilken s�k-funktion som ska anv�ndas(flowin, flowout, usedby, fullexpansion eller crud). 
	 * @param searchValue s�kstr�ngen som det ska s�kas efter
	 * @return formaterad JSON-str�ng som kan visualiseras i frontend.
	 */
	@GET
	@Path("tree/{type}/{searchValue}")
	@Produces("application/json")
	public String BuildTheTree(@PathParam("type") String type, @PathParam("searchValue") String searchValue) {
		Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));

		String cypherQuery = getCypherQuery(searchValue, type);

		try (Session session = driver.session()) {

			ResultData resultData = new ResultData();
			StatementResult result = session.run(cypherQuery);

			resultData = getDataFromResult(result);

			String json = null;

			ObjectMapper objectMapper = new ObjectMapper();

			List<NodeTree> roots = new ArrayList<>();

			if (type.equals("fullexpansion") || type.equals("crud") || type.equals("flowout")) {

				roots = buildRelation(resultData.nodes, resultData.relationshipList);

			} else if (type.equals("usedby") || type.equals("flowin")) {

				roots = buildInverseRelation(resultData.nodes, resultData.relationshipList);

			}

			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

			json = objectMapper.writeValueAsString(roots);

			return json;

		} catch (Exception e) {
			// e.printStackTrace();
			return e.toString();

		}

		// return "Cant Connect to DB";
	}

	/**
	 * 
	 * <h2>/app/admin/graphTree/{type}/{searchValu} path</h2>
	 * 
	 * En annan path beh�vs f�r att rita den statiska grafen, detta d� JSON-str�ngen
	 * beh�ver formateras annorlunda. Alla noder beh�ver visas under en gemensam root f�r att beh�lla dynamiken n�r
	 * det klickas i tr�det f�r att st�nga och �ppna noderna som har undernoder.
	 * 
	 * @param type 			Typen som det s�ks efter i frontend, d.v.s. vilken s�k-funktion 
	 * @param searchValue	Fritext-str�ngen som det s�ks efter ifr�n frontend
	 * @return				JSON-str�ngen som kan visualisera detta i frontend
	 */
	@GET
	@Path("graphTree/{type}/{searchValue}")
	@Produces("application/json")
	public String BuildTheGraphTree(@PathParam("type") String type, @PathParam("searchValue") String searchValue) {
		Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));

		String cypherQuery = getCypherQuery(searchValue, type);

		try (Session session = driver.session()) {

			ResultData resultData = new ResultData();
			StatementResult result = session.run(cypherQuery);

			resultData = getDataFromResult(result);

			String json = null;

			ObjectMapper objectMapper = new ObjectMapper();

			List<NodeTree> roots = new ArrayList<>();
			List<NodeTree> headList = new ArrayList<>();

			NodeTree headNode = new NodeTree();

			if (type.equals("fullexpansion") || type.equals("crud") || type.equals("flowout")) {

				roots = buildRelation(resultData.nodes, resultData.relationshipList);

			} else if (type.equals("usedby") || type.equals("flowin")) {

				roots = buildInverseRelation(resultData.nodes, resultData.relationshipList);

			}
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

			for (NodeTree n : roots) {
				headNode.children.add(n);
			}
			headList.add(headNode);
			json = objectMapper.writeValueAsString(headList);

			return json;

		} catch (Exception e) {
			// e.printStackTrace();
			return e.toString();

		}

		// Cannot connect to DB
	}

	/**
	 * 
	 * <h2>/app/admin/NodeGraph/{searchType}/{searchValu} path</h2>
	 * 
	 * Dena anv�nds f�r att rita det dynamiska tr�det i frontend och anv�nds n�r man det kickas p� "Show dynamic graph"-knappen
	 * Den h�r skijler sig fr�n dom andra tv� d� JSON-strukturen m�ste skapas helt  annorlunda. Tr�det har en lista med alla
	 * noder och en annan lista vilka noder som �r i relation medd varandra.
	 * 
	 * @param searchType 	Typen som det s�ks efter i frontend, d.v.s. vilken s�k-funktion 
	 * @param searchValue	Fritext-str�ngen som det s�k efter ifr�n frontend
	 * @return				JSON-str�ngen som kan visualisera detta i frontend
	 */
	@GET
	@Path("NodeGraph/{searchType}/{searchValue}/")
	@Produces("application/json")
	public String createRelations(@PathParam("searchType") String searchType,
			@PathParam("searchValue") String searchValue) {

		String cypherQuery = getCypherQuery(searchValue, searchType);

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

			while (result.hasNext()) {
				Record res = result.next();
				p = res.get("q").asPath();

				for (Segment segment : p) {
					if (!relationshipList.contains(segment.relationship())) {
						
						String start = segment.start().get("fileName").toString();
						String end = segment.end().get("fileName").toString();
						
						if (!end.contains(".jar") && !start.contains(".jar")) {
							relationshipList.add(segment.relationship());
						}
					}
					if (!nodes.contains(segment.start())) {

						String start = segment.start().get("fileName").toString();

						if (!start.contains(".jar")) {
							nodes.add(segment.start());
						}
					}
					if (!nodes.contains(segment.end())) {

						String end = segment.end().get("fileName").toString();

						if (!end.contains(".jar")) {
							nodes.add(segment.end());
						}
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
			return e.toString();
		}
	}

	/**
	 * 
	 * Metod f�r att ge r�tt cypher-sats, viktigt att varje sats f�r med Artifacterna och returnerar hela relationen q.
	 * 
	 * @param searchValue	Str�ngen som det ska s�kas med
	 * @param type			Vilken s�k-funktion som ska anv�ndas 
	 * @return				En text-str�ng med en cypher-sats som kan skickas till databasen
	 */
	public String getCypherQuery(String searchValue, String type) {
		String cypherQuery = "";

		if (type.equals("usedby")) {

			System.out.println(type + " " + searchValue);

			cypherQuery = "MATCH q=(a:Artifact)-[:CONTAINS]->(ab:Class:CSN)-[:DEPENDS_ON {resolved: true}]->(t:Type:CSN) <-[:CONTAINS]-(a2:Artifact)"
					+ "WHERE upper(t.name) CONTAINS \"" + searchValue.toUpperCase()
					+ "\" AND NOT t.name CONTAINS \"$\" " + "AND NOT ab.name CONTAINS \"$\" RETURN ab,t,q";

		} else if (type.equals("fullexpansion")) {

			System.out.println(type + " " + searchValue);

			cypherQuery = "MATCH q=(ab:Artifact)-[:CONTAINS]->(p:Server)-[:DEPENDS_ON*..5]->(a:Type:CSN {valid: true})<-[:CONTAINS]-(ab2:Artifact) "
					+ "WHERE upper(p.name) =~ \".*" + searchValue.toUpperCase()
					+ ".*\" AND NOT a.fqn CONTAINS \"entities\"AND NOT a.fqn CONTAINS \"worksets\" "
					+ "AND NOT a.name CONTAINS \"$\" RETURN q";

		} else if (type.equals("crud")) {

			System.out.println(type + " " + searchValue);

			cypherQuery = "MATCH q=(a:Artifact)-[:CONTAINS]->(c:Class:CSN)-[:DECLARES]->(m:Method) WHERE upper(m.name) =~ \"(CREATE|READ|UPDATE|DELETE).*"
					+ searchValue.toUpperCase() + ".*\" RETURN c,m,q";

		} else if (type.equals("flowin")) {

			System.out.println(type + " " + searchValue);

			cypherQuery = "match q=(c:Client) <-[:EXITS_TO]-(exit)<-[:HAS_EXIT_STATE]-(caller:Client) where upper(c.name) CONTAINS \""
					+ searchValue.toUpperCase() + "\"return q";

		} else if (type.equals("flowout")) {

			cypherQuery = "match q=(c:Client) <-[:EXITS_TO]-(exit)<-[:HAS_EXIT_STATE]-(caller:Client) where upper(caller.name) CONTAINS \""
					+ searchValue.toUpperCase() + "\"return q";

		} else {
			// n�got �r galet
			System.out.println("Se till att q finns och returneras");
		}

		return cypherQuery;
	}

	
	/**
	 * 
	 * Metod som tar mot ett resultat ifr�n databasen och skapar tv� listor som medlemsvariabler i objektet ResultData,
	 * en lista med noder {@link ResultData#nodes} och en lista med relationer {@link ResultData#relationshipList}  
	 * 
	 * @param result resultatet fr�n cyphersaten som k�rdes till databasen
	 * @return ResultDataobjektet som inneh�ller de tv� medlemsvariablerna nodes och relationshipList
	 */
	public ResultData getDataFromResult(StatementResult result) {
		org.neo4j.driver.v1.types.Path p;

		ResultData resultData = new ResultData();

		while (result.hasNext()) {
			Record res = result.next();
			p = res.get("q").asPath();

			for (Segment segment : p) {
				if (!resultData.relationshipList.contains(segment.relationship())) {
					resultData.relationshipList.add(segment.relationship());

				}
				if (!containsNodeID(resultData.nodes, segment.start().id())) {
					resultData.nodes.add(new NodeTree(segment.start()));
				}
				if (!containsNodeID(resultData.nodes, segment.end().id())) {
					resultData.nodes.add(new NodeTree(segment.end()));
				}
			}
		}
		return resultData;
	}

	
	/**
	 * 
	 * Bygger relationerna alla noder har tillvarandra, s�tter in noder i
	 * {@link NodeTree#children} och {@link NodeTree#parents} utifr�n relations-listan
	 * 
	 * @param resultData	ResultData objekt som inneh�ller medlemsvariabler nodes - lisa av noder och relationshipList - lista av relationer
	 * @return 				En lista med noderna som �r r�tter, dvs noder som en tom parents-lista.
	 */
	public List<NodeTree> buildRelation(List<NodeTree> nodes, List<Relationship> relationshipList) {
		List<NodeTree> roots = new ArrayList<>();

		for (NodeTree treeNode : nodes) {
			for (Relationship r : relationshipList) {

				if (treeNode.node.id() == r.endNodeId()) {

					NodeTree parent = getNodeFromID(nodes, r.startNodeId());

					if (!parent.getFileName().contains(".jar")) {
						treeNode.parents.add(parent);
					}
				}

				if (treeNode.node.id() == r.startNodeId()) {
					NodeTree child = getNodeFromID(nodes, r.endNodeId());
					if (treeNode.getFileName().contains(".jar")) {
						child.setJar(treeNode.getFileName());

					} else {
						treeNode.children.add(child);
					}
				}
			}

			if (treeNode.children.isEmpty()) {
				treeNode.children = null;
			}
			if (treeNode.parents.isEmpty() && !treeNode.getFileName().contains(".jar")) {
				roots.add(treeNode);
			}
		}

		return roots;
	}

	
	/**
	 * 
	 * <p>Mycket liknande {@link #buildRelation(ResultData)} men bygger relationerna omv�nt, d.v.s. att tr�det byggs utifr�n och in.
	 * Ist�llet f�r som buildRelation g�r att bygga tr�det inifr�n och ut.</p> 
	 * 
	 * <p>Used by och flow in funktionerna m�ste byggas p� det h�r s�ttet.</p> 
	 * 
	 * @param resultData	ResultData objekt som inneh�ller medlemsvariabler nodes - lisa av noder och relationshipList - lista av relationer
	 * @return				En lista med noderna som �r r�tter i tr�det
	 */
	public List<NodeTree> buildInverseRelation(List<NodeTree> nodes, List<Relationship> relationshipList) {
		List<NodeTree> roots = new ArrayList<>();

		for (NodeTree treeNode : nodes) {

			for (Relationship r : relationshipList) {

				if (treeNode.node.id() == r.startNodeId()) {
					NodeTree parent = getNodeFromID(nodes, r.endNodeId());
					System.out.println(treeNode.getFileName());

					if (treeNode.getFileName().contains(".jar")) {
						parent.setJar(treeNode.getFileName());

					}

					treeNode.parents.add(parent);
				}

				if (treeNode.node.id() == r.endNodeId()) {
					NodeTree child = getNodeFromID(nodes, r.startNodeId());

					if (!child.getFileName().contains(".jar")) {
						treeNode.children.add(child);
					}

				}

			}
			if (treeNode.children.isEmpty()) {
				treeNode.children = null;
			}
			if (treeNode.parents.isEmpty()) {
				roots.add(treeNode);
			}
		}

		return roots;
	}

	/**
	 * 
	 * <p>Kollar om noden med ett specifikt ID redan finns i listan, detta d� det inga duplikationer av noder f�r finnas i den
	 * listan �r relationerna byggs.</p>
	 * 
	 * <p>om noden med ett specifikt id redan finns i listan ska den inte l�ggas till igen.</p>
	 * 
	 * @param list	listan med noder som det ska kollas i
	 * @param id	det id noden har om det redan finns i listan
	 * @return		sant om den noden redan finns i listan, annars falskt
	 */
	public boolean containsNodeID(final List<NodeTree> list, final Long id) {
		return list.stream().map(NodeTree::getId).filter(id::equals).findFirst().isPresent();
	}

	/**
	 * 
	 * Metod som letar efter en nod med ett specifikt ID i en lista av noder och returnerar hela objektet som har det ID.
	 * 
	 * @param list	listan som det ska letas efter
	 * @param id	det ID noden har som vill hittas
	 * @return		hela noden som har det ID:et
	 */
	public NodeTree getNodeFromID(List<NodeTree> list, Long id) {
		for (NodeTree treeNode : list) {
			if (treeNode.node.id() == id) {
				return treeNode;
			}
		}

		System.out.print("Hittar inte nod i lista");
		return null;
	}

}
