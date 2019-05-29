package rest4;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.v1.types.Node;

/**
 *
 * <h3>Klass f�r nod</h3>
 * 
 * Klassen som inneh�ller all data f�r hela noden.
 * 
 * @author csn8029
 * @author csn8030
 *
 */
public class NodeTree {
	Long id;
	Node node;
	String sourceFileName;
	String fqn;
	String fileName;
	String name;
	String jar;
	
	
	List<NodeTree> children = new ArrayList<>();
	List<NodeTree> parents = new ArrayList<>();
	
	/**
	 * 
	 * Konstruktorn tar emot ett Neo4j-nod objekt som resterande data kan extraheras ifr�n  
	 * 
	 * Neo4j noderna fr�n CRUD-funktionen hade inte variabeln "fqn" utan en "classFqn". Ifall det �r en s�n nod skickas det datat till medlemsvariablem fqn.
	 * 
	 * @param n	�r en Neo4j-nod
	 */
	public NodeTree(Node n) {
		
		this.id = n.id();
		this.node = n;
		this.sourceFileName = n.get("sourceFileName").toString();
		
		

		if(!n.get("fqn").isNull()) {
			this.fqn = n.get("fqn").toString();	
		}
		else if(!n.get("classFqn").isNull()) {
			this.fqn = n.get("classFqn").toString();
			
		}
		
		this.fileName = n.get("fileName").toString();
		this.name = n.get("name").toString();		

	}
	
	public NodeTree() {
		
	}
	
	
	public String getJar() {
		return jar;
	}
	public void setJar(String jar) {
		this.jar = jar;
	}

	
	public Long getId() {
		return id;
	}


	public List<NodeTree> getChildren() {
		return children;
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
