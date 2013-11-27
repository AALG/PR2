
import java.util.*;

public class Algorithms{
	Tour tspTour;
	/**
	* Constructs a tour given a set of vertices by searching for
	* the nearest neighbour.
	*/
	public Algorithms(Tour tour){
		tspTour = tour;
	}

	public void nearestNeighbourTour(){
		Node[] points     = tspTour.tour;
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
                    if(tspTour.getDistanceFromMatrix(tour[i-1], cityToConsider) != 0 && 
                        (closestCity == null || 
                        tspTour.getDistanceFromMatrix(tour[i-1], cityToConsider) < 
                        tspTour.getDistanceFromMatrix(tour[i-1], closestCity))){
                        closestCity =  cityToConsider;
                    }
                }
            }
 
                tour[i] = closestCity;
                used[closestCity.ID] = true;
        
        
        }
        tspTour.setNewTour(tour);
    }
    
	/**
	*	Optimizes a given tour by deleting two edges
	*	and replace them with two new edges.
	**/
    public void twoOptTour(long deadline) throws InterruptedException{
    	Node[] tour = tspTour.tour;
        Random rand = new Random();
        int p = rand.nextInt(tour.length - 1);
        int N = tour.length;
        double currentResult   = 0;
        int    currentBestNode = 0;
        double bestResult      = Double.MIN_VALUE;
        int limit = 0;
        int i = 0;
        while(true){
            i = rand.nextInt(tour.length - 1);
            bestResult    = Double.MIN_VALUE;
            currentResult = -1;
            /* Begin at i + 1 we don't have to evaluate the same node again */
            for(int j = (i + 1) % N; j != i ; j = (j + 1) % N){
                if(System.currentTimeMillis() > deadline)
                        throw new InterruptedException();
                /* Swap nodes and calculate the new tour length */
                    currentResult = swapNodes(i,j,tour);
                    if(currentResult >= bestResult && currentResult != -1){
                        bestResult = currentResult;
                        currentBestNode = j;
                    }
            }

            if(bestResult != Double.MIN_VALUE)
                reverse(i,currentBestNode,tour);
            limit++;
            if(limit > tour.length - 1)
                break;
        }

    }
    private void reverse(int i, int j, Node[] tour){
        int c = j;
        Node tmp;
        for(int p = i; p <= c; p++){
                tmp = tour[p];
                tour[p] = tour[c];
                tour[c] = tmp;
                c--;
        }
    }
 	/**
 	*	Performs a swap of two edges in a given tour 
 	*	if the swap yields a shorter tour.
 	**/
    private double swapNodes(int i, int j, Node[] tour){
        if( i > j ){
                        int t = i;
                        i = j;
                        j = t;
        }
        /* If below statement is true, it's not necessary to swap */
        if(i == 0 && j == tour.length - 1){
                return -1;
        }
 
        Node iNode = tour[i];
        Node jNode = tour[j];
        double edgeDistanceBefore;
        double edgeDistanceAfter;
 
        if(i != 0){
                edgeDistanceBefore = tspTour.getDistanceFromMatrix(iNode, tour[i-1]);
                edgeDistanceAfter  = tspTour.getDistanceFromMatrix(jNode, tour[i-1]); 
        }else{
                edgeDistanceBefore = tspTour.getDistanceFromMatrix(iNode, tour[tour.length-1]);
                edgeDistanceAfter  = tspTour.getDistanceFromMatrix(jNode, tour[tour.length-1]); 
        }
 
        if(j != tour.length-1){
                edgeDistanceBefore += tspTour.getDistanceFromMatrix(jNode, tour[j+1]);
                edgeDistanceAfter  += tspTour.getDistanceFromMatrix(iNode, tour[j+1]);
        }else{
                edgeDistanceBefore += tspTour.getDistanceFromMatrix(jNode, tour[0]);
                edgeDistanceAfter  += tspTour.getDistanceFromMatrix(iNode, tour[0]);
        }
 
        /* The swap won't yield a better tour */
        if(edgeDistanceAfter >= edgeDistanceBefore )
                return -1;
        /* The swap will probably make the tour shorter */
      
 
        return (edgeDistanceBefore - edgeDistanceAfter);
    }

    public void twoAndHalfOpt(long deadline) throws InterruptedException{
        tspTour.setTourNeighbours();
        Random rand = new Random();
        int start = rand.nextInt(tspTour.tour.length);

        Node A = tspTour.tour[start];
        Node B = tspTour.tour[start+1];
        Node C;
        Node Cprev;
        double currentResult;
        double bestResult;
        Node currentBestNode;
        int i = 0;
        
        while(i < tspTour.tour.length){
            if(System.currentTimeMillis() > deadline)
                throw new InterruptedException();
            currentResult = 0;
            bestResult = Double.MIN_VALUE;
            currentBestNode = null;
            C     = B.getNextTourNeighbour(A);
            Cprev = B;
            while(C.ID != A.ID){
                if(System.currentTimeMillis() > deadline)
                        throw new InterruptedException();
                
                currentResult = checkInsert(A,B,C);
                if(currentResult > bestResult && currentResult != -1){
                    currentBestNode = C;
                    bestResult = currentResult;
                }     

                Node tmp = C;
                C        = C.getNextTourNeighbour(Cprev);
                Cprev    = tmp;
            }
            
            if(currentBestNode != null){
                insert(A,B,currentBestNode);
                Node tmp = A;
                A = B.getNextTourNeighbour(currentBestNode);
                B = A.getNextTourNeighbour(B);
            }else{
                Node tmp = B;
                B = B.getNextTourNeighbour(A);
                A = tmp;
            }
            
            i++;
        }
        tspTour.createTourArray(tspTour.tour[0]);
    }
    
    public void insert(Node A, Node B, Node C){

        /* Insert C between A and B. A - > B => A -> C -> B */    
        A.reconnect(B,C);
        B.reconnect(A,C);
        /* Merge connection between neighbours of C */
        C.tourNeighbour1.reconnect(C,C.tourNeighbour2);
        C.tourNeighbour2.reconnect(C,C.tourNeighbour1);
        /* Update C */
        C.reconnect(C.tourNeighbour1, A);
        C.reconnect(C.tourNeighbour2, B);
    }

    public double checkInsert(Node A, Node B, Node C){
        double distanceBeforeInsert;
        double distanceAfterInsert;
        
        distanceBeforeInsert = tspTour.getDistanceFromMatrix(A,B);
        distanceBeforeInsert += tspTour.getDistanceFromMatrix(C, C.tourNeighbour1);
        distanceBeforeInsert += tspTour.getDistanceFromMatrix(C, C.tourNeighbour2);
        
        distanceAfterInsert = tspTour.getDistanceFromMatrix(A,C);
        distanceAfterInsert += tspTour.getDistanceFromMatrix(C,B);
        distanceAfterInsert += tspTour.getDistanceFromMatrix(C.tourNeighbour1, C.tourNeighbour2);
        
        if(distanceBeforeInsert <= distanceAfterInsert)
            return -1.0;
        
        
        return (distanceBeforeInsert - distanceAfterInsert);
        
      
    
    }

//******************* CHRISTOFIDES *********************************//

    /* Below christofides has a worst case results of 2*(OPT TOUR) */
    public void christofides(){
    	Node[] points = tspTour.tour;
		PriorityQueue<Edge> edges = new PriorityQueue<Edge>();//createEdgeQueue(points);
		
		/* Create a forest of nodes */
        List< Set<Node> > forest = new ArrayList< Set<Node> >();
        Set<Node> smallForest;
        for(Node n : points){
            smallForest = new HashSet<Node>();
            smallForest.add(n);
            forest.add(smallForest);
        }
		/* Create a minimum spanning tree */
		setUpMST(forest, edges, points);
	    /* Use "Depth-first search" to find a tour */
		boolean[] used = new boolean[points.length];
		Node[] tour   = new Node[points.length];
		ArrayDeque<Node> stack = new ArrayDeque<Node>(); 
		
		
		
		Node current = points[0];
		stack.addFirst(current);
		int i = 0;
		/* While stack is not empty */
		while(stack.size() != 0){
		    current = stack.poll();
		    tour[i] = current;
		    used[current.ID] = true;
		    
		    /* Retrieve MST Neighbours */
		    for(Edge e : current.getEdges() ){
		        /* TODO: May not be correct */
		        Node neighbour = e.b;
		        /* If not used */
		        if(!used[neighbour.ID])
		             stack.addFirst(neighbour);
	        }
	        i++;
	   
		}
		
        tspTour.setNewTour(tour);
	}
	
	private TreeSet<Edge> createEdgeQueue(Node[] points){
		TreeSet<Edge> edges = new TreeSet<Edge>(); /* Keep the edges in a sorted queue */
		Edge edge = null;
		Node from = null;
		for(int i = 0; i < points.length - 1; i++){
			from = points[i];
			for(int j = i + 1; j < points.length; j++){
					edge = new Edge(from, points[j], tspTour.getDistanceFromMatrix(points[i],points[j]));
					edges.add(edge);
					
					
			}
		}

		return edges;
	}
	
	private void setUpMST(List< Set<Node> > forest, PriorityQueue<Edge> edges, Node[] tour){
        Node mstRoot = edges.peek().a;
        Edge currentEdge;
        Set<Node> currentSmallForest;
        Set<Node> removedForest;
        int E = 0;
        int V = tour.length;
        int i = 0;
        /* While queue not empty */
        //tour = new Node[(V-1)*2];
        while(edges.size() > 0){
            currentEdge = edges.poll();
            /* Below code may not be optimal */
            currentSmallForest = getSmallForest(forest, currentEdge.a, false);
            
            if(!currentSmallForest.contains(currentEdge.b)){
                removedForest = getSmallForest(forest, currentEdge.b, true);
                currentSmallForest.addAll(removedForest);
                currentEdge.a.connectTo(currentEdge);
         
                currentEdge.b.connectTo(currentEdge.reverse());
                //tour[i]   = currentEdge.a;
                //tour[i+1] = currentEdge.b;
                //i += 2;
                E++;
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
	}

}
