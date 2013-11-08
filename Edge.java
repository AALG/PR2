
public class Edge {

	private double cost;
	private Node endStation;
	
	public Edge(Node node, double cost){
		this.endStation = node;
		this.cost = cost;
	}

    public int compareTo(Edge other){
		
		if(this.cost < other.cost)
			return -1;
        if(this.cost > other.cost)
            return 1;
		else
			return 0;
		
	}
	
}
