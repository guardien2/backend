package NodeGraph;

/**
 * NodeGraphLinks är en klass för att hålla relationer mellan två noder 
 * 
 * denna klass består av två long värden:
 * source vilket är id på den första noden i relationen
 * target vilket är id på den nod source relaterar till
 * 
 * @author csn8030
 *
 */
public class NodeGraphLinks {
	Long source;
	Long target;

	public Long getSource() {
		return source;
	}
	public void setSource(Long source) {
		this.source = source;
	}
	public Long getTarget() {
		return target;
	}
	public void setTarget(Long target) {
		this.target = target;
	}
}
