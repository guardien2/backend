package NodeGraph;

import org.neo4j.driver.v1.types.Node;
/**
 * Denna klass anv�nds f�r NodeGraph nodes listan
 * 
 * Just nu h�mtas bara ID och name fr�n noden.
 * Om mer information vill visas s� kan nya variabler skapas med getters and setter
 * Ett exempel f�r att h�mta FQN fr�n nod "node.get("fqn").toString()."
 * 
 * @author csn8030
 *
 */
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
