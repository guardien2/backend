package NodeGraph;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.v1.types.Node;
/**
 * NodeGraph klassen används för att skapa den andra sortens JSON class objekt för NodeGrafer
 * Denna klass innehåller två listor:
 * nodes vilket är en lista som innehåller alla noder
 * links vilket är en lista som innehåller alla relationer mellan de olika noderna i  nodes listan.    
 * 
 * @author csn8030
 *
 */
//class that holds nodes and the links between nodes
public class NodeGraph {
	List<NodeGraphIds> nodes = new ArrayList<>();
	List<NodeGraphLinks> links = new ArrayList<>();

	public NodeGraph() {

	}

	public List<NodeGraphIds> getNodes() {
		return nodes;
	}

	public void setNodes(List<NodeGraphIds> nodes) {
		this.nodes = nodes;
	}

	public List<NodeGraphLinks> getLinks() {
		return links;
	}

	public void setLinks(List<NodeGraphLinks> links) {
		this.links = links;
	}
}
