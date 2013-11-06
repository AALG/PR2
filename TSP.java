
/*
* Project 2 - Traveling Salesman problem 2D (AVALG13)
* Link: https://kth.kattis.scrool.se/problems/oldkattis:tsp
*
* Royal School of Technology - Computer Science
* @author Fredrik Öman    - frdo@kth.se
* @author Alfred Krappman - krappman@kth.se
*/

import java.util.*;

public class TSP{
	
	TSPIO io;
	Node[] points;
	Node mstRoot;
	Node[] tour;

	public TSP(){
		io = new TSPIO();
	}
	
	public void setUpCompleteGraph(){
		Edge edge = null;
		double dist = 0;
		for(Node base : points){
			for(Node connectTo : points){
				if(base != connectTo){
					dist = dist(base, connectTo);
					edge = new Edge(connectTo, dist);
					base.connectTo(edge);
				}
			}
		}
	}
	

	
	
	public void setUpMST(){
		setUpCompleteGraph(); //Omega n^2
		boolean[] used  = new boolean[points.length];
		mstRoot = new Node(points[0]);
		used[mstRoot.ID] = true;
		
		
		
		
	}

	/*
	* Naive algorithm. Tour produced by this
	* algorithm are used as a "base case" in KATTIS. 
	*/
	public void greedyTour(){
		Node[] tour      = new Node[points.length];
		boolean[] used  = new boolean[points.length];
		
		// Start at first point
		tour[0] = points[0];
		used[0] = true;
		//The naive algorithm
		
		Node closestCity = null;
		Node cityToConsider = null;
		for(int i = 1; i < points.length; i++){

			closestCity = null;
			for(int j = 0; j < points.length; j++){
				cityToConsider = points[j];
				if(!used[cityToConsider.ID]){
					if(closestCity == null || 
							(dist(tour[i-1], cityToConsider) < dist(tour[i-1], closestCity))){
						closestCity =  cityToConsider;
					}
				}
			}
			tour[i] = closestCity;
			used[closestCity.ID] = true;
		}
		this.tour = tour;
		//this.printTourLength(tour);
	}
	
	/**
	* Performs 2-OPT on a given tour. Note that this implementation
	* is NOT by any means optimal.
	* A solution to a more optimal implementation could be to use
	* edges instead of nodes and a more sophisticated data structure for
	* holding nodes.
	*/
	public void twoOptTour() throws InterruptedException{

		double tourLength    = calculateTourLength(tour);
		double newTourLength = Double.MAX_VALUE;
		Node[] newTour       = null;
		Node tmp             = null;

		/* NOTE: This is a shitty implementation */

		/* Exclude start node by setting bound newTour.length - 1 */
		for(int i = 0; i < tour.length - 1 ; i++){
            
			newTour = tour.clone(); /* Time consuming */

			/* Begin at i + 1 */
			for(int j = i + 1; j < tour.length; j++){
                if(Thread.interrupted())
                    throw new InterruptedException();
				/* Swap nodes */
				swapNodes(i,j,newTour);
				newTourLength = calculateTourLength(newTour); 
				if(newTourLength < tourLength){ /* Did the swap yield a shorter solution ? */
					tour = newTour; /* Set the shorter tour to our main tour */
					tourLength = newTourLength; /* Update tour length */
					break; /* break inner loop and continue to look for an even shorter solution */
				}else{
					/* Swap back. Should not be needed in a good implementation */
					swapNodes(j,i,newTour);
				}	
			}

		}

	}

	/* Shitty function for swapping nodes */
	public void swapNodes(int i, int j, Node[] newTour){
		Node tmp   = newTour[j];
		newTour[j] = newTour[i];
		newTour[i] = tmp;
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
	

	public void initializePointsFromFile(String filename){
		points = io.readInputFromFile(filename);
	}
	
	public void initializePointsKattis(){
		points = io.readInputFromKattis();
	}
	
	public void printTourLength(Node[] tour){
		double tourLength = 0;
		for(int i = 1; i < tour.length; i++){
			tourLength += dist(tour[i-1], tour[i]);
		}
		tourLength += dist(tour[tour.length-1], tour[0]);
		System.out.println("Length of tour: " + tourLength);
	}
	
	public double calculateTourLength(Node[] tour){
		double tourLength = 0;
		for(int i = 1; i < tour.length; i++){
			tourLength += dist(tour[i-1], tour[i]);
		}
		tourLength += dist(tour[tour.length-1], tour[0]);
		return tourLength;
	}

	public void printTour(){
		io.outputToKattis(tour);
	}

	public static void main(String[] args){
        Thread mainThread = Thread.currentThread();
        (new Thread(new DeadlineTimer(mainThread))).start();
        
		TSP tsp = new TSP();
//		double start = System.currentTimeMillis();
		tsp.initializePointsKattis();
		//tsp.setUpMST();
		
		tsp.greedyTour();
		//System.out.println("Tour length before two opt: " + tsp.calculateTourLength(tsp.tour));
		//Visualizer vis = new Visualizer(tsp.tour,0,"Greedy");
        try{
		    tsp.twoOptTour();
        }catch(InterruptedException e){ 
            //System.out.println("FHFHFH"); 
            tsp.printTour();
            System.exit(0); 
        }
		//System.out.println("Tour length after two opt: " + tsp.calculateTourLength(tsp.tour));
		//Visualizer vis_2 = new Visualizer(tsp.tour,500,"2-OPT");
        tsp.printTour();
//		double stop = System.currentTimeMillis();
//		System.out.println("Total time: " + (stop - start) + " ms");
	}


}
