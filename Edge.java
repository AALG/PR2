
public class Edge implements Comparable{

	public double weight;
	public Node a;
	public Node b;
	
	public Edge(Node a, Node b, double weight){
		this.a = a;
		this.b = b;
		this.weight = weight;
	}

    public int compareTo(Object o){
		Edge other = (Edge) o;
		if(this.weight < other.weight)
			return -1;
        if(this.weight > other.weight)
            return 1;
		else
			return 0;
		
	}
    
    public Edge reverse(){
        
        return new Edge(b,a,weight);
    }
    
    public double getWeight(){ return weight; }

	public String toString(){

		return a.ID + " -> " + b.ID;
	}
	
}
