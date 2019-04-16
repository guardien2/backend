package rest4;

import java.util.List;

public class NestedJson {

	String node;
	List<NestedJson> next;


	public NestedJson(String node) {
		this.node = node;

	}

	public String getNode() {
		return node;
	}


	public void setNode(String node) {
		this.node = node;
	}

	public List<NestedJson> getNext() {
		return next;
	}

	public void setNext(List<NestedJson> next) {
		this.next = next;
	}


	

}
