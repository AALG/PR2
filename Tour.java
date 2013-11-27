import java.util.*;


public class Tour{
	
	Node[] tour;
	Node mstRoot;
	double[][] distMatrix;
	PriorityQueue<Edge> edges;
	public Tour(Node[] points){
		tour = points;
		calculateDistances(points);
		
	}


	private void calculateDistances(Node[] points){
	    
		distMatrix = new double[points.length][points.length];
		for(int i = 0; i < points.length - 1; i++)
			for(int j = i + 1; j < points.length; j++){
				distMatrix[i][j] = dist(points[i],points[j]);
			}
			
		
	}

	/**
	 * Calculate the euclidian distance between
	 * two points. .
	 * dist(this,other) = sqr( (x - other.x)^2 + (y - other.y)^2) 
	 */
	public double dist(Node node1, Node node2 ){
		double diffx = Math.pow(node1.x - node2.x, 2);
		double diffy = Math.pow(node1.y - node2.y, 2);
		
		return Math.sqrt(diffx + diffy);
	}

	public void setTourNeighbours(){
		tour[0].tourNeighbour1 = tour[tour.length-1];
		tour[0].tourNeighbour2 = tour[1];
		for(int i = 1; i < tour.length - 1; i++){
			tour[i].tourNeighbour1 = tour[i-1];
			tour[i].tourNeighbour2 = tour[i+1];
		}
		tour[tour.length-1].tourNeighbour1 = tour[tour.length - 2];
		tour[tour.length-1].tourNeighbour2 = tour[0];
	}


	public double getDistanceFromMatrix(Node n1, Node n2){

		if(n1.ID < n2.ID){
			return distMatrix[n1.ID][n2.ID];
		}else
			return distMatrix[n2.ID][n1.ID];
	}

	/**
	*	Calculates the tour length by traversing through the pointer
	*	structure.
	**/
	public double calculateTourLengthPointers(Node start){
		Node previous = start;
		Node next = start.tourNeighbour1;
		double tourLength = 0;
		Node tmp;
		while(true){
			tourLength += getDistanceFromMatrix(previous,next);
			tmp = next;
			next = next.getNextTourNeighbour(previous);
			previous = tmp;
			if(next.ID == start.ID){
				tourLength += getDistanceFromMatrix(previous,next);
				break;
			}
		}

		return tourLength;
	}
	/**
	*	Calculates the length of the tour
	**/
	public double calculateTourLength(){
		double tourLength = 0;
		for(int i = 1; i < tour.length; i++){
			tourLength += getDistanceFromMatrix(tour[i-1], tour[i]);
		}
		tourLength += getDistanceFromMatrix(tour[tour.length-1], tour[0]);
		return tourLength;
	}


	/**
	*	Creates an array representing the tour
	*	based on the pointer information in each node.
	**/
	public void createTourArray(Node start){
		Node previous = start;
		Node next = start.tourNeighbour1;
		tour[0] = start;
		Node tmp;
		int i = 1;
		while(true){
			tour[i] = next;
			tmp = next;
			next = next.getNextTourNeighbour(previous);
			previous = tmp;
			if(next.ID == start.ID){
				break;
			}
			i++;
		}
	}


	public void printTour(TSPIO io){
		io.outputToKattis(tour);
	}
	/**
	*	Prints the tour by traversing through pointers.
	**/
	public void printTourTest(Node start){
		Node previous = start;
		Node next = start.tourNeighbour1;
		System.out.println(start.ID);
		Node tmp;
		while(true){
			System.out.println(next.ID);
			tmp = next;
			next = next.getNextTourNeighbour(previous);
			previous = tmp;
			if(next.ID == start.ID){
				break;
			}
		}
	}

	public void setNewTour(Node[] tour){
		this.tour = tour;
	}

	/**
	*	Prints all edges in the tour.
	**/
	public void printTourEdges(Node[] points){
		for(Node n : points){
			System.out.println("Node: "+ n.ID + " N1: " + n.tourNeighbour1.ID + " N2: " + n.tourNeighbour2.ID);
		}	
	}

}
