package rest4;

public class Name {
	String name;
	String fqn;
	public Name(String name, String fqm) {
		this.name = name;
		this.fqn = fqm;
	}

	public String getFqn() {
		return fqn;
	}

	public void setFqn(String fqn) {
		this.fqn = fqn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
