package rest4;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.v1.types.Relationship;

final class ResultData {
	public List<NodeTree> nodes = new ArrayList<>();
	public List<Relationship> relationshipList = new ArrayList<>();
	
	public ResultData(){
		
	}

	public List<NodeTree> getNodes() {
		return nodes;
	}

	public void setNodes(List<NodeTree> nodes) {
		this.nodes = nodes;
	}

	public List<Relationship> getRelationships() {
		return relationshipList;
	}

	public void setRelationships(List<Relationship> relationships) {
		this.relationshipList = relationships;
	}
		
}
