package NodeGraph;

import org.neo4j.driver.v1.types.Node;

public class NodeGraphIds {
	Node node;
	Long id;
	String name;
	public NodeGraphIds(Node node){
		this.id = node.id();
		this.name = node.get("name").toString();
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
