package rest4;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.v1.types.Node;

public class NodeWithNext {
	
	Node node;
	String sourceFileName;
	String fqn;
	String fileName;
	String name;
	
	List<NodeWithNext> dependsOn = new ArrayList<>();
	
	public NodeWithNext(Node n) {
		this.node = n;
		this.sourceFileName = n.get("sourceFileName").toString();
		this.fqn = n.get("fqn").toString();
		this.fileName = n.get("fileName").toString();
		this.name = n.get("name").toString();
	}
	
	

	public Long getNodeID() {
		return node.id();
	}

	
	public List<NodeWithNext> getDependsOn() {
		return dependsOn;
	}



	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}
	public String getFqn() {
		return fqn;
	}

	public void setFqn(String fqn) {
		this.fqn = fqn;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
