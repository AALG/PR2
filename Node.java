import java.util.LinkedList;

/*
* Represent a destination in a TSP instance.
*/

public class Node{
	
	int ID;
	public double x;
	public double y;
	private LinkedList<Edge> edges;
	public Node tourNeighbour1;
	public Node tourNeighbour2;
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
	
	public LinkedList<Edge> getEdges() { 
		return edges; 
	}
	
	/*
	 * Override compare method.
	 */
	public int compareTo(Node other){
		
		if(other.x == x && other.y == y)
			return -1;
		else
			return 0;
		
	}
	
	public boolean isConnectedTo(Node other){
		if(other.ID == tourNeighbour1.ID || other.ID == tourNeighbour2.ID)
			return true;
		return false;
	}
	
	public void reconnect(Node oldNode, Node newNode ){
		if(tourNeighbour1.ID == oldNode.ID)
			tourNeighbour1 = newNode;
		else if(tourNeighbour2.ID == oldNode.ID){
			tourNeighbour2 = newNode;
		}else
			System.out.println("FAIL");
	}
	
	public Node connect(Node toConnectWith){
		if(tourNeighbour1 == null){
			tourNeighbour1 = toConnectWith;
			return toConnectWith;
		}
		if(tourNeighbour2 == null){
			tourNeighbour2 = toConnectWith;
			return toConnectWith;
		}
		return null;
			
	}
	
	public Node disconnect(Node toDisconnectWith){
		if(tourNeighbour1.ID == toDisconnectWith.ID){
			tourNeighbour1 = null;
			return toDisconnectWith;
		}
		if(tourNeighbour2.ID == toDisconnectWith.ID){
			tourNeighbour2 = null;
			return toDisconnectWith;
		}
		return null;
	}
    
	public Node getNextTourNeighbour(Node previous){
		if(getNrOfNeighbours() < 2)
			return null;
		if(previous.ID == tourNeighbour1.ID)
			return tourNeighbour2;
		else
			return tourNeighbour1;
	}
	
	public int getNrOfNeighbours(){
		int count = 0;
		if(tourNeighbour1 != null)
			count++;
		if(tourNeighbour2 != null)
			count++;
		return count;
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
