import java.util.LinkedList;

/*
* Represent a destination in a TSP instance.
*/

public class Node{
	
	int ID;
	public double x;
	public double y;
	private LinkedList<Edge> edges;
	
	public Node(double x, double y, int ID){
		this.x  = x;
		this.y  = y;
		this.ID = ID;
		edges = new LinkedList<Edge>();
	}

	public Node(Node oldNode){
		this.x = oldNode.x;
		this.y = oldNode.y;
		this.ID = oldNode.ID;
	}
	
	public double getX(){
		return x;
	}

	public double getY(){
		return y;
	}
	
	public void connectTo(Edge newEdge){
		edges.add(newEdge);
	}
	
	public LinkedList<Edge> getEdges() { return edges; }
	
	/*
	 * Override compare method.
	 */
	public int compareTo(Node other){
		
		if(other.x == x && other.y == y)
			return -1;
		else
			return 0;
		
	}
	
	public String toString(){
		String toRet = "";
		toRet += "##NODE##\n";
		toRet += "ID: "+ ID + "\n";
		toRet += "X:" + x + ", Y:" + y + "\n";
		toRet += "########\n";
		return toRet;
		
	}
	
}