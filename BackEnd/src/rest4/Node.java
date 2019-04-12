package rest4;

public class Node {
	
	private String fqn;
//	private boolean valid;
	private String sourceFileName;
//	private String visibility;
	private String name;
//	private String byteCodeVersion;
//	private String md5;
	
	public Node(String fqn, String sourceFileName, String name) {
		this.fqn = fqn;
		this.sourceFileName = sourceFileName;
		this.name = name;
	}

	public String getFqn() {
		return fqn;
	}

	public void setFqn(String fqn) {
		this.fqn = fqn;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
