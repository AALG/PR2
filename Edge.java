
public class Edge {

	private double cost;
	private Node endStation;
	
	public Edge(Node node, double cost){
		this.endStation = node;
		this.cost = cost;
	}
	
}
