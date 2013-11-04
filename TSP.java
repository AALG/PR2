
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

	public TSP(){
		io = new TSPIO();
	}

	/*
	* Naive algorithm. Tour produced by this
	* algorithm are used as a "base case" in KATTIS. 
	*/
	public void greedyTour(){
		int[] tour      = new int[points.length];
		boolean[] used  = new boolean[points.length];
		
		// Start at first point
		tour[0] = 0;
		used[0] = true;
		//The naive algorithm
		int best;
		for(int i = 1; i < points.length; i++){
			best = -1;
			for(int j = 0; j < points.length ; j++){
				if(!used[j])
					if(best == -1 || ( dist(tour[i-1],j) < dist(tour[i-1],best) )){
						best = j;
					}
				
			
			}
			
			tour[i] = best;
			used[best] = true;
		}
		
		io.outputToKattis(tour);
		this.printTourLength(tour);
	}
	
	/**
	 * Calculate the euclidian distance between
	 * two points. .
	 * dist(this,other) = sqr( (x - other.x)^2 + (y - other.y)^2) 
	 */
	public double dist(int node1, int node2 ){
		Node n1 = points[node1];
		Node n2 = points[node2];
		double diffx = Math.pow(n1.x - n2.x, 2);
		double diffy = Math.pow(n1.y - n2.y, 2);
		
		return Math.floor( Math.sqrt(diffx + diffy) );
	}
	

	public void initializePointsFromFile(String filename){
		points = io.readInputFromFile(filename);
	}
	
	public void initializePointsKattis(){
		points = io.readInputFromKattis();
	}
	
	public void printTourLength(int[] tour){
		double tourLength = 0;
		for(int i = 1; i < tour.length; i++){
			tourLength += dist(tour[i-1], tour[i]);
			//System.out.println(tour[i-1] + " | " + tour[i]);
		}
		System.out.println("Length of tour: " + tourLength);
	}
	
	public static void main(String[] args){
		TSP tsp = new TSP();
//		double start = System.currentTimeMillis();
//		tsp.initializePointsFromFile("input.txt");
		tsp.initializePointsKattis();
		tsp.greedyTour();
//		double stop = System.currentTimeMillis();
//		System.out.println("Total time: " + (stop - start) + " ms");
	}


}
