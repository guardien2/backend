package rest4;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.v1.types.Relationship;

/**
 * <p>Klass som innehåller resultat data från cypher-satsen till grafdatabasen</p>
 * <p>Listar datat i två listor, en lista som innehåller noder och en lista som innehåller relationerna mellan noderna. 
 * 
 * @author csn8029
 *
 */
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
