/*
* Represent a destination in a TSP instance.
*/

public class Node{
	
	int ID;
	public double x;
	public double y;
	
	
	public Node(double x, double y, int ID){
		this.x  = x;
		this.y  = y;
		this.ID = ID;
	}

	public double getX(){
		return x;
	}

	public double getY(){
		return y;
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
	
}