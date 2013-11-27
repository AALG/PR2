
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
	
<<<<<<< HEAD
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
=======
	TSPIO io;
	Node[] points; /* Containing all the nodes in the graph in input order */
	Node mstRoot;
	Node[] tour;
	double[][] distMatrix;
	PriorityQueue<Edge> edges;
    static long deadline;
	public TSP(){
		io = new TSPIO();
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
                    if(getDistanceFromMatrix(tour[i-1], cityToConsider) != 0 && 
                    	(closestCity == null || 
                        getDistanceFromMatrix(tour[i-1], cityToConsider) < 
                        getDistanceFromMatrix(tour[i-1], closestCity))){
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
	
	/* UNOPTIMIZED 2-OPT */
    public void twoOptTour() throws InterruptedException{
 
        double tourLength = calculateTourLength(tour);
        double newTourLength = Double.MAX_VALUE;
        boolean[] used = new boolean[tour.length];
        Node[] possibleRoute;
        Random rand = new Random();

        for(int i = rand.nextInt(points.length - 1); i < tour.length; i++){
            /* Begin at i + 1 we don't have to evaluate the same node again */
            for(int j = i+1; j < tour.length - 1; j++){
            	if(System.currentTimeMillis() > deadline)
            		throw new InterruptedException();
                /* Swap nodes and calculate the new tour length */
                possibleRoute = swapNodes(i,j,tour);
                if(possibleRoute != null){
	            	newTourLength = calculateTourLength(possibleRoute);
	            	if(newTourLength < tourLength){ /* Did the swap yield a shorter solution ? */
	                	    tour = possibleRoute; /* Set tour to the shorter tour */
	                    	return;                                 /* Exit */
	            	}
	            }
                
            }

        }
    }



    /* UNOPTIMIZED SWAP. */
    public Node[] swapNodes(int i, int j, Node[] tour){
    		if( i > j){
    			int t = i;
    			i = j;
    			j = t;
    		}
    		if(i == j)
    			return null;
            Node[] possibleRoute = new Node[tour.length];
            //Add 0 to i-1 in order
            for(int p = 0; p < i; p++){
                    possibleRoute[p] = tour[p];
            }
            //Add i to j in reversed order
            int c = j;
            for(int p = i; p <= c ; p++){
            		possibleRoute[c] = tour[p];
            		possibleRoute[p] = tour[c];
                    c--;
            }
            //Add k+1 to the end of the tour in order
            for(int p = j + 1; p < tour.length; p++){
                    possibleRoute[p] = tour[p];
            }
            
            return possibleRoute;

    }
   


    public void printMatrix(){

    	for(int i = 0; i < points.length; i++){
    		for(int j = 0; j < points.length; j++){
    			System.out.print(" [" +Math.floor(distMatrix[i][j])+"]");
    		}
    		System.out.println("");
    	}
    }

	public void calculateDistances(){
		Node from = null;
		Node to   = null;
		double minimumDistance = Double.MAX_VALUE;
		for(int i = 0; i < points.length - 1; i++){
			from = points[i];
			for(int j = i + 1; j < points.length; j++){
				to = points[j];
				distMatrix[i][j] = dist(from,to);
			}
		}
	}


	public void setUpCompleteGraph(){
		edges = new PriorityQueue<Edge>(); /* Keep the edges in a sorted queue */
		Edge edge = null;
		Node from = null;
		for(int i = 0; i < points.length - 1; i++){
			from = points[i];
			for(int j = i+1; j < points.length; j++){
					edge = new Edge(from, points[j], dist(points[i],points[j]));
					edges.add(edge);
			}
		}

		/* Create a forest of nodes */
		List< Set<Node> > forest = new ArrayList< Set<Node> >();
		Set<Node> smallForest;
		for(Node n : points){
			smallForest = new HashSet<Node>();
			smallForest.add(n);
			forest.add(smallForest);
		}


		/* Create MST*/
		setUpMST(forest);

	}
	

	
	
	public void setUpMST(List< Set<Node> > forest){
		mstRoot = edges.peek().a;
		Edge currentEdge;
		Set<Node> currentSmallForest;
		Set<Node> removedForest;
		int E = 0;
		int V = points.length; 
		int i = 0;
		/* While queue not empty */
		tour = new Node[(V-1)*2];
		while(edges.size() > 0){
			currentEdge = edges.poll();
			currentSmallForest = getSmallForest(forest, currentEdge.a, false);
			
			if(!currentSmallForest.contains(currentEdge.b)){
				removedForest = getSmallForest(forest, currentEdge.b, true);
				currentSmallForest.addAll(removedForest);
				currentEdge.a.connectTo(currentEdge);
				System.out.println(currentEdge);
				E++;
				tour[i] = currentEdge.a;
				tour[i+1] = currentEdge.b;
				i += 2;
			}

			if(E == V-1)
				break;

		}
		
		
	}

	public Set<Node> getSmallForest(List< Set<Node> > forest, Node a, boolean remove){
		Iterator< Set<Node> > it = forest.iterator();
		Set<Node> tmp;
		while(it.hasNext()){
			tmp = it.next();
			if(tmp.contains(a)){
				if(remove)
					it.remove();
				return tmp;
			}
		}

		return null;
>>>>>>> 714b435a6d2351b87ff78c225f6a3646b5a8064c
	}

	public void twoOptTour() throws InterruptedException{
		tspAlg.twoOptTour(deadline);
	}
<<<<<<< HEAD

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
=======
	
	public double getDistanceFromMatrix(Node n1, Node n2){
		if(n1.ID < n2.ID){
			return distMatrix[n1.ID][n2.ID];
		}else
			return distMatrix[n2.ID][n1.ID];
	}
	
	public void initializePoints(){
		points = io.readInputFromKattis();
		distMatrix = new double[points.length][points.length];
		calculateDistances();
		//printMatrix();
	}
	
	public void initializePointsFromFile(String filename){
        points = io.readInputFromFile(filename);
}
	
	public double calculateTourLength(Node[] tour){
		double tourLength = 0;
		for(int i = 1; i < tour.length; i++){
            /*if(System.currentTimeMillis() > deadline)
                throw new InterruptedException();*/
			tourLength += getDistanceFromMatrix(tour[i-1], tour[i]);
		}
		tourLength += getDistanceFromMatrix(tour[tour.length-1], tour[0]);
		return tourLength;
>>>>>>> 714b435a6d2351b87ff78c225f6a3646b5a8064c
	}
	
	public void printPointerTour(){
	    tour.printTourTest(points[0]);
	}
<<<<<<< HEAD
	
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
        

		
=======
	public void printTourTest(Node[] t){
		io.outputToKattis(t);
	}
	public static void main(String[] args){
        TSP tsp = new TSP();
        tsp.initializePointsFromFile("input.txt");
        tsp.setUpCompleteGraph();
        Visualizer vis = new Visualizer(tsp.tour,0,"Greedy", false);
        /*deadline = System.currentTimeMillis() + 1500;
        Thread mainThread = Thread.currentThread();
        Thread timer = new Thread(new DeadlineTimer(mainThread, deadline));
        timer.start();
		TSP tsp = new TSP();
		tsp.initializePoints();
        if(DEBUG){
		    //tsp.setUpMST();
		    long start = System.currentTimeMillis();
		    tsp.greedyTour();

		    //tsp.tour = tsp.points;
		    double before = tsp.calculateTourLength(tsp.tour); 
            try{
            	
                System.out.println("Tour length before two opt: " + before);
		        //Visualizer vis = new Visualizer(tsp.tour,0,"Greedy");
		        System.out.println("Time elapsed: " + (System.currentTimeMillis() - start) + " ms");
		        int i = 0;
		        
		        while(i < 500) { tsp.twoOptTour(); i++; }
		        
            }catch(InterruptedException e) { }
		    //Visualizer vis_2 = new Visualizer(tsp.tour,500,"2-OPT");
		    double after = tsp.calculateTourLength(tsp.tour);
		    double improvement = 100.0 * Math.round(1000* ( (before - after)/before) )/1000.0;
		    System.out.println("Improvement after two opt: " + (before - after));
		    System.out.println("Time elapsed: " + (System.currentTimeMillis() - start) + " ms");
        }
        else{
            tsp.greedyTour();
            try{
            	int i = 0;
                while(i < 100){
                    if(System.currentTimeMillis() > deadline)
                        break;
	                tsp.twoOptTour();
	                i++;
                }
            }catch(Exception e) { }
            tsp.printTour();
        }
        timer.interrupt();
//		double stop = System.currentTimeMillis();
//		System.out.println("Total time: " + (stop - start) + " ms");
		*/
>>>>>>> 714b435a6d2351b87ff78c225f6a3646b5a8064c
	}
	

}
