
/*
* Project 2 - Traveling Salesman problem 2D (AVALG13)
* Link: https://kth.kattis.scrool.se/problems/oldkattis:tsp
*
* Royal School of Technology - Computer Science
* @author Fredrik Oman    - frdo@kth.se
* @author Alfred Krappman - krappman@kth.se
*/

import java.util.*;


public class TSP{
    
    static final boolean DEBUG = false;
	
	TSPIO io;	          /* IO functionality */
	Tour tour;	          /* Holds information about the tour */
	Algorithms tspAlg;    /* TSP algorithms for tour construction and tour optimizing */
	Node[] points;        /* Containing all the nodes in the graph in input order */
    static long deadline; /* Global deadline */ 

    /* Set up IO functionality and read input */
	public TSP(){
		io     = new TSPIO();
	    initializePoints();
		tour   = new Tour(points);
		tspAlg = new Algorithms(tour);
	}

	/** 
	*	Initialize a points vector and
	*   create a tour from the points vector
	**/

	private void initializePoints(){
		points = io.readInputFromKattis();
		
	}

	public void tspTourNearestNeighbour(){
		tspAlg.nearestNeighbourTour();
	}

	
	public void christofidesStart(){
		tspAlg.christofides();
	}	
	
	public double getTourLength(){
		return tour.calculateTourLength();
	}

	public void twoOptTour() throws InterruptedException{
		tspAlg.twoOptTour(deadline);
	}

	public void printTour(){
		tour.printTour(io);
	}
	
	public void printMST(){
		
		for(Node n : points){
			LinkedList<Edge> edges = n.getEdges();
			for(Edge e : edges){
				//System.out.println(e);
				 //System.out.println(e.a.ID);
				//System.out.println(e.b.ID);
			}
		
		}
		
	}
	
	public void twoHalfOptTour() throws InterruptedException{
	    tspAlg.twoAndHalfOpt(deadline);
	}
	
	public double calculateLengthPointers(){
	    return tour.calculateTourLengthPointers(points[0]);
	}
	
	public void printPointerTour(){
	    tour.printTourTest(points[0]);
	}
	
	public static void main(String[] args){
		double before;
		double after;
		long start;
		/********/
        deadline = System.currentTimeMillis() + 1500;
		TSP tsp = new TSP();
        /********/

        if(DEBUG){
        	tsp.tspTourNearestNeighbour();
        	before = tsp.getTourLength();
        	 
        }else{
            if(tsp.points.length <= 3){
	            tsp.tspTourNearestNeighbour();
                try{ tsp.twoOptTour(); }catch(Exception e){}
                tsp.printTour();
                System.exit(0);
            }else
                tsp.tspTourNearestNeighbour();
        }

		int i = 0;
        try{
        	
            while(true){
            	
                if(System.currentTimeMillis() > deadline)
                    break; 
                tsp.twoOptTour();
                tsp.twoHalfOptTour();
                                
            }
            
        }catch(InterruptedException e) { }
         
         if(DEBUG){
        	System.out.println("Loops " + i);
	        //after = tsp.calculateLengthPointers();
	        after = tsp.getTourLength();
	       	System.out.println("Tour(NN): " + before);
	       	System.out.println("Tour(2 + 2.5 OPT): " + after);
	        System.out.println("DIFF: " + (before - after));
	        System.out.println("Improvement: " + (Math.floor(10000*((before)/after))/10000)); 
	    }else{
	        //if(twoOpted)
	    	    //tsp.printPointerTour();
    	    //else
    	    tsp.printTour();
	    }
        

		
	}
	

}
