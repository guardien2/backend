package rest4;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.v1.types.Relationship;

/**
 * <p>Klass som inneh�ller resultat data fr�n cypher-satsen till grafdatabasen</p>
 * <p>Listar datat i tv� listor, en lista som inneh�ller noder och en lista som inneh�ller relationerna mellan noderna. 
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
