package NodeGraph;

/**
 * NodeGraphLinks �r en klass f�r att h�lla relationer mellan tv� noder 
 * 
 * denna klass best�r av tv� long v�rden:
 * source vilket �r id p� den f�rsta noden i relationen
 * target vilket �r id p� den nod source relaterar till
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
