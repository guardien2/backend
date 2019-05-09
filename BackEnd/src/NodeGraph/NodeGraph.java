package NodeGraph;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.v1.types.Node;

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