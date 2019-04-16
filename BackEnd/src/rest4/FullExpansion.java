package rest4;

import java.util.ArrayList;
import java.util.List;

public class FullExpansion {
	String startNode;
	FullExpansion endnode;
	List<FullExpansion> DEPENDS_ON = new ArrayList<FullExpansion>();
	


	FullExpansion(String sourceFileName) {
		this.startNode = sourceFileName;
		

	}
	FullExpansion(String sourceFileName,FullExpansion endNode) {
		this.startNode = sourceFileName;
	
		this.DEPENDS_ON.add(endNode);

	}

	public String getSourceFileName() {
		return startNode;
		
	}

	public void setSourceFileName(String sourceFileName) {
		this.startNode = sourceFileName;
	}


}
