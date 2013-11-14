
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
    
    static final boolean DEBUG = true;
	
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
        Node[] tour       = new Node[points.length];
        boolean[] used    = new boolean[points.length];
    
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
	
	public void setTourNeighbours(){
		tour[0].tourNeighbour1 = tour[points.length-1];
		tour[0].tourNeighbour2 = tour[1];
		for(int i = 1; i < points.length - 1; i++){
			tour[i].tourNeighbour1 = tour[i-1];
			tour[i].tourNeighbour2 = tour[i+1];
		}
		tour[points.length-1].tourNeighbour1 = tour[points.length - 2];
		tour[points.length-1].tourNeighbour2 = tour[0];
	}

    /* 2-OPT */
    public void twoOptTour() throws InterruptedException{
        Random rand = new Random();

        for(int i = 0; i < tour.length; i++){
            /* Begin at i + 1 we don't have to evaluate the same node again */
            for(int j = 0; j < tour.length; j++){
            	if(System.currentTimeMillis() > deadline)
            		throw new InterruptedException();
                /* Swap nodes */
                if(swapNodes(i,j,tour))
                	return;              
            }

        }
    }

    public void twoOptTourTest() throws InterruptedException{
    	Node start = tour[0];
    	Node tmp;
    	Node previousOuter = tour[0];
    	Node currentOuter  = tour[0];
    	Node previousInner = null;
    	Node currentInner  = null;
        while(true){
        	currentInner  = currentOuter.getNextTourNeighbour(previousOuter);
        	previousInner = currentOuter;
        	while(currentInner.ID != currentOuter.ID){
            	if(System.currentTimeMillis() > deadline)
            		throw new InterruptedException();
                /* Swap nodes */
                if(swapNodesTest(currentOuter,currentInner))
                	return;              
               	tmp = currentInner;
               	currentInner = currentInner.getNextTourNeighbour(previousInner);
               	previousInner = tmp;

            }
            tmp = currentOuter;
            currentOuter = currentOuter.getNextTourNeighbour(previousOuter);
            previousOuter = tmp;
            if(currentOuter.ID == start.ID)
            	break;

        }


    }


    public boolean swapNodesTest(Node a, Node b){
    	if(a.ID == b.ID)
    		return false;

    	double edgeDistanceBefore = 0;
    	double edgeDistanceAfter  = 0;


    	edgeDistanceBefore =  getDistanceFromMatrix(a,a.tourNeighbour1);
    	edgeDistanceBefore += getDistanceFromMatrix(b, b.tourNeighbour2);

    	edgeDistanceAfter  =  getDistanceFromMatrix(a, b.tourNeighbour2);
    	edgeDistanceAfter  += getDistanceFromMatrix(b, a.tourNeighbour1);


    	if(edgeDistanceBefore <= edgeDistanceAfter)
    		return false;

    	a.tourNeighbour1.reconnect(a,b);
    	b.tourNeighbour2.reconnect(b,a);

    	Node tmp = a.tourNeighbour1;
    	a.tourNeighbour1 = b.tourNeighbour2;
    	b.tourNeighbour2 = tmp;

    	return true;



    }

    public boolean swapNodes(int i, int j, Node[] tour){
    	if( i > j ){
    			int t = i;
    			i = j;
    			j = t;
    	}
    	/* If below statement is true, it's not necessary to swap */
    	if(i == 0 && j == tour.length - 1 || i == j){
    		return false;
    	}

    	Node iNode = tour[i];
    	Node jNode = tour[j];
    	double edgeDistanceBefore;
    	double edgeDistanceAfter;
    	double pi;
    	if(i != 0){
    		edgeDistanceBefore = getDistanceFromMatrix(iNode, tour[i-1]);
    		edgeDistanceAfter  = getDistanceFromMatrix(jNode, tour[i-1]); 
    	}else{
    		edgeDistanceBefore = getDistanceFromMatrix(iNode, tour[points.length-1]);
    		edgeDistanceAfter  = getDistanceFromMatrix(jNode, tour[points.length-1]); 
    	}
    	if(j != points.length-1){
    		edgeDistanceBefore += getDistanceFromMatrix(jNode, tour[j+1]);
    		edgeDistanceAfter  += getDistanceFromMatrix(iNode, tour[j+1]);
    		
    	}else{
    		edgeDistanceBefore += getDistanceFromMatrix(jNode, tour[0]);
    		edgeDistanceAfter  += getDistanceFromMatrix(iNode, tour[0]);
    	}

    	/* The swap won't yield a better tour */
    	if(edgeDistanceAfter >= edgeDistanceBefore)
    		return false;
    	/* The swap will probably make the tour shorter */
    	int c = j;
    	Node tmp;
    	for(int p = i; p <= c; p++){
    		tmp = tour[p];
    		tour[p] = tour[c];
    		tour[c] = tmp;
    		c--;
    	}

    	return true;
    }

	public void calculateDistances(){
		for(int i = 0; i < points.length - 1; i++)
			for(int j = i + 1; j < points.length; j++)
				distMatrix[i][j] = dist(points[i],points[j]);
			
		
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
		Set< Set<Node> > forest = new HashSet< Set<Node> >();
		Set<Node> smallForest;
		for(Node n : points){
			smallForest = new HashSet<Node>();
			smallForest.add(n);
			forest.add(smallForest);
		}


		/* Create MST*/
		setUpMST(forest);

	}
	

	
	
	public void setUpMST(Set< Set<Node> > forest){
		mstRoot = edges.peek().a;
		Edge currentEdge;
		Set<Node> currentSmallForest;
		int E = 0;
		int V = points.length; 
		/* While queue not empty */
		while(edges.size() > 0){
			currentEdge = edges.poll();
			currentSmallForest = getSmallForest(forest, currentEdge.a);
			
			if(!currentSmallForest.contains(currentEdge.b)){
				forest.remove( getSmallForest(forest, currentEdge.b));
				currentSmallForest.add(currentEdge.b);
				currentEdge.a.connectTo(currentEdge);
				E++;
			}

			if(E == V)
				break;

		}
	}

	public Set<Node> getSmallForest(Set< Set<Node> > forest, Node a){
		Iterator< Set<Node> > it = forest.iterator();
		Set<Node> tmp;
		while(it.hasNext()){
			tmp = it.next();
			if(tmp.contains(a))
				return tmp;
		}

		return null;
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
	}
	
	public double calculateTourLength(Node[] tour){
		double tourLength = 0;
		for(int i = 1; i < tour.length; i++){
			tourLength += getDistanceFromMatrix(tour[i-1], tour[i]);
		}
		tourLength += getDistanceFromMatrix(tour[tour.length-1], tour[0]);
		return tourLength;
	}

	public double calculateTourLengthTest(Node start){
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

	public void printTour(){
		io.outputToKattis(tour);
	}
	public void printTourTest(Node start){
		Node previous = start;
		Node next = start.tourNeighbour1;
		double tourLength = 0;
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

	public void fak(){
		for(Node n : points){
			System.out.println("Node: "+ n.ID + " N1: " + n.tourNeighbour1.ID + " N2: " + n.tourNeighbour2.ID);
		}	
	}

	public static void main(String[] args){
		double before;
		double after;
		long start;
		long stop;
		/********/
        deadline = System.currentTimeMillis() + 1600;
        Thread mainThread = Thread.currentThread();
        Thread timer = new Thread(new DeadlineTimer(mainThread, deadline));
        timer.start();
		TSP tsp = new TSP();
		tsp.initializePoints();
        /********/

        if(DEBUG){
        	start = System.currentTimeMillis();
        	tsp.greedyTour();
        	tsp.printTour();
        	
        	tsp.setTourNeighbours();
        	tsp.fak();
        	System.out.println("blu");
     		System.out.println("Greedy time: " + (System.currentTimeMillis() - start) + " ms");
        	before = tsp.calculateTourLength(tsp.tour);
        	//Visualizer vis_2 = new Visualizer(tsp.tour.clone(),500,"Greedy"); 
        }else
			tsp.greedyTour();
		int i = 0;
        try{
        	
            while(i < 1){
                if(System.currentTimeMillis() > deadline)
                    break;
                tsp.twoOptTourTest();
                i++;
            }
            
        }catch(Exception e) { }
         System.out.println("Loops " + i);
         if(DEBUG){
	        stop = System.currentTimeMillis();
	        after = tsp.calculateTourLengthTest(tsp.points[0]);
	       	System.out.println("Tour(Greedy): " + before);
	       	System.out.println("Tour(2OPT): " + after);
	        System.out.println("DIFF: " + (before - after));
	        //Visualizer vis = new Visualizer(tsp.tour.clone(),0,"2OPT"); 
	        tsp.printTourTest(tsp.points[0]);
	        tsp.fak();
	    }else
	    	tsp.printTour();
        
        //timer.interrupt();

		
	}
	

}
