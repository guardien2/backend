package rest4;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.v1.types.Node;

public class NodeWithNext {
	
	Node node;
	List<NodeWithNext> nextList = new ArrayList<>();
	
	public NodeWithNext(Node n) {
		this.node = n;
	}
	
	public Long getNodeID() {
		return node.id();
	}

	public List<NodeWithNext> getNextlist() {
		return nextList;
	}


	
}
