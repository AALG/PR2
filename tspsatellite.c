#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <signal.h>
#include <math.h>
#include <time.h>
#include <stdint.h>
#include <float.h>
#include <string.h>

struct Node
{
    float x;
    float y;
    short ID;
};

struct Edge
{
	short A;
	short B;
};


short * satellites;
short * satellitesRecord;
short * sTour;
int numberOfPoints;
int numberOfEdges;
float * distanceMatrix;
struct Node * points;
struct Edge * edges;
struct Node * tour; 
int loops = 0;
float greedyLength = 0;
int iterations;

/* #### SATELLITE GETS AND SETS #### */

short getCity(short sat_ind){

    return sat_ind >> 1;

}


void setNext(short city, short next){

    satellites[city << 1] = next << 1;

}

void setPrev(short city, short next){

    satellites[(city << 1) + 1] = (next << 1) + 1;

}

short getNext(short city){

    return getCity(satellites[city << 1]);

}

short getPrev(short city){

    return getCity(satellites[(city << 1) + 1]);

}

short getNextSat(short sat_ind){

    return satellites[sat_ind];

}

void setNextSat(short old_sat, short new_sat){

    satellites[old_sat] = new_sat; 
}

void setPrevSat(short old_sat, short new_sat){
    
    new_sat = new_sat ^ 1;
    satellites[old_sat ^ 1] = new_sat;

}

short getPrevSat(short sat_ind){
    
    return satellites[sat_ind ^ 1] ^ 1;

}

/* #### PRINT FUNCTIONS #### */

void printTour(){
	int i = 0;
	while(i < numberOfPoints){
		printf("%d\n", tour[i].ID);
		i++;
	}
}

void printSTour(){
	int i = 0;
	while(i < numberOfPoints){
		printf("%d\n", sTour[i]);
		i++;
	}
}

void printSatTour(){

    int i;
    short currSat = 0;
    for(i = 0; i < numberOfPoints; i++){
        currSat = getNextSat(currSat);
        fprintf(stdout, "%d\n", getCity(currSat));        
    }

}

void printSatellites(){

    int i;
    for(i = 0; i < numberOfPoints*2; i++){
        printf("%d\n", satellites[i]);
    }

}

void initializePoints(){
	/* Read first line */
	if( scanf ("%d", &numberOfPoints ) == EOF )
		printf("Failure\n");
	
	points = (struct Node *)malloc(sizeof(struct Node)*numberOfPoints);
	float x;
	float y;
	int ID;
	/* Read co-ordinates */
	int i = 0;
	while(i < numberOfPoints ){
		if( scanf("%f %f", &x, &y) == EOF )
			printf("Failure\n");
		points[i].x  = x;
		points[i].y  = y;
		points[i].ID = i;
		i++;
	}

}

float dist(struct Node * n1, struct Node * n2){

	float diffx = pow(n1->x - n2->x, 2);
	float diffy = pow(n1->y - n2->y, 2);

	return sqrt(diffx + diffy);

}

void createDistanceMatrix(){


	/* Allocate matrix */
	distanceMatrix = (float *)malloc(sizeof(float)*numberOfPoints*numberOfPoints);
	edges          = (struct Edge *)malloc(sizeof(struct Edge)*((numberOfPoints*numberOfPoints/2) + 1));
	int i;
	int j;
	int edgeCount = 0;
	for(i = 0; i < numberOfPoints - 1; i++)
		for(j = i + 1; j < numberOfPoints; j++){
			distanceMatrix[i*numberOfPoints + j] = dist(&points[i], &points[j]);
			/* Create edges */
			edges[edgeCount].A = points[i].ID;
			edges[edgeCount].B = points[j].ID;
			edgeCount++;
		}
		numberOfEdges = edgeCount;
}

float getDistanceFromMatrix(int i, int j){
	
	if(i > j){
		return distanceMatrix[j*numberOfPoints +i];
	}else if(i == j){
		return FLT_MAX;
	}
		return distanceMatrix[i*numberOfPoints +j];
}


static int compareEdge(const void * this, const void * other){
	struct Edge * edge_1 = (struct Edge *) this;
	struct Edge * edge_2 = (struct Edge *)other;

	float dist_1 = getDistanceFromMatrix(edge_1->A, edge_1->B);
	float dist_2 = getDistanceFromMatrix(edge_2->A, edge_2->B);

	if(dist_1 < dist_2){
		return -1;
	}else if(dist_1 > dist_2){
		return 1;
	}else{
		return 0;
	}

}

void reverse(short a, short b, short c, short d){
    
    satellites[a] = c^1;
    satellites[c] = a^1;

    satellites[d^1] = b;
    satellites[b^1] = d;

}

float swapNodesCheck(short iNodeSat, short jNodeSat){

    if(getCity(iNodeSat) == getCity(jNodeSat))
		return -1;

	float edgeDistanceBefore = 0;
	float edgeDistanceAfter  = 0;

    edgeDistanceBefore = getDistanceFromMatrix(getCity(iNodeSat), getCity(getPrevSat(iNodeSat)));
    edgeDistanceAfter = getDistanceFromMatrix(getCity(jNodeSat), getCity(getPrevSat(iNodeSat)));

    edgeDistanceBefore += getDistanceFromMatrix(getCity(jNodeSat), getCity(getNextSat(jNodeSat)));
    edgeDistanceAfter += getDistanceFromMatrix(getCity(iNodeSat), getCity(getNextSat(jNodeSat)));

    if(edgeDistanceBefore <= edgeDistanceAfter)
        return -1;

    return edgeDistanceAfter;
}


int swapNodesSat(short iNodeSat, short jNodeSat){

    reverse(jNodeSat, getNextSat(jNodeSat), getPrevSat(iNodeSat), iNodeSat);
    return 1;

}

 

void greedyTour(){

 	/* Allocate memory for tour */
 	sTour =  (short *)malloc(sizeof(short)*numberOfPoints);
 	char * used = (char *)malloc(sizeof(char)*numberOfPoints);
 	/* Start at first node in points */
 	sTour[0] = points[0].ID;
 	used[0] = 1;
 	short bestCity    = 0;
 	short currentCity = 0;
 	int i;
 	int j;
 	/* Start the tour ! */
 	for(i = 1; i < numberOfPoints; i++){
 		bestCity = -1;
 		for(j = 0; j < numberOfPoints; j++){
 			currentCity = points[j].ID;
 			if(used[currentCity] != 1)
 				if(bestCity == -1 || 
                  (getDistanceFromMatrix(sTour[i-1], 
                  currentCity) < getDistanceFromMatrix(sTour[i-1], bestCity)))
 					bestCity = currentCity;
 		}
 		sTour[i] = bestCity;
        setNext(sTour[i-1], sTour[i]);
        setPrev(sTour[i], sTour[i-1]);
        
 		used[bestCity] = 1;
 	}
   
    setNext(sTour[numberOfPoints-1], sTour[0]);
    //setPrev(sTour[numberOfPoints-1], sTour[numberOfPoints-2]);
    setPrev(sTour[0], sTour[numberOfPoints-1]);
 	//free(used);

 }




void convertStourToSat(){

    setNext(sTour[0], sTour[1]);
    setPrev(sTour[0], sTour[numberOfPoints-1]);
    int i;
    for(i = 1; i < numberOfPoints-1; i++){
        setNext(sTour[i], sTour[i+1]);
        setPrev(sTour[i], sTour[i-1]);
    }
    setNext(sTour[numberOfPoints-1], sTour[0]);
    setPrev(sTour[numberOfPoints-1], sTour[numberOfPoints-2]);
}

float twoOptSatTour(){

    short startNode = (rand() % (numberOfPoints*2));

    float currentResult;
    float bestResult;    
    short currentBestSat;
    
    float totalLoss = 0;

    //fprintf(stderr, "%d\n", startNode);
    short iNodeSat = startNode;
    short jNodeSat = iNodeSat;
    int i;
    for(i = getCity(startNode); i < numberOfPoints; i++){
        currentResult = -1;
        bestResult = -10;
        do{
            //fprintf(stderr, "%d\n", getCity(jNodeSat));
          
            currentResult = swapNodesCheck(iNodeSat, jNodeSat);
            if(currentResult >= bestResult && currentResult != -1){
                bestResult = currentResult;
                currentBestSat = jNodeSat;
            }
                 
            jNodeSat = getNextSat(jNodeSat);

        }while(jNodeSat != iNodeSat);
        if(bestResult != -10){
            swapNodesSat(iNodeSat, currentBestSat);
            totalLoss += bestResult;    
        }
        iNodeSat = getNextSat(iNodeSat);
        jNodeSat = iNodeSat;
    }

    return totalLoss;
}

float checkInsert(short aNodeSat, short bNodeSat, short cNodeSat){

    float distanceBeforeInsert;
    float distanceAfterInsert;
    
    distanceBeforeInsert = getDistanceFromMatrix(
                                      getCity(aNodeSat),
                                      getCity(bNodeSat));
    
    distanceBeforeInsert += getDistanceFromMatrix(
                                        getCity(cNodeSat),
                                        getCity(getNextSat(cNodeSat)));

    distanceBeforeInsert += getDistanceFromMatrix(
                                        getCity(cNodeSat),
                                        getCity(getPrevSat(cNodeSat)));

    
    distanceAfterInsert = getDistanceFromMatrix(
                                        getCity(aNodeSat),
                                        getCity(cNodeSat));

    distanceAfterInsert += getDistanceFromMatrix(
                                        getCity(cNodeSat),
                                        getCity(bNodeSat));
    
    distanceAfterInsert += getDistanceFromMatrix(
                                        getCity(getNextSat(cNodeSat)),
                                        getCity(getPrevSat(cNodeSat)));
     
    if(distanceBeforeInsert <= distanceAfterInsert)
        return -1;

    return distanceAfterInsert;

}

int insert(short aNodeSat, short bNodeSat, short cNodeSat){

    setNextSat(aNodeSat, cNodeSat);
    setPrevSat(bNodeSat, cNodeSat);
    //setNextSat(bNodeSat, getNextSat(cNodeSat));
    setNextSat(getPrevSat(cNodeSat), getNextSat(cNodeSat));
    setPrevSat(getNextSat(cNodeSat), getPrevSat(cNodeSat));
    

    setNextSat(cNodeSat, bNodeSat);
    setPrevSat(cNodeSat, aNodeSat);

    return 1;

}

float twoAndAHalfOpt(){

    short N = rand() % numberOfPoints;
    
    short aNodeSat = N*2;
    short bNodeSat = getNextSat(aNodeSat);
    
    short cNodeSat;
    short cNodeSatPrev;
    
    int i = 0;
    char swapped;
    short tmp;

    float currentResult;
    float bestResult;

    short currentBestSat;

    float totalLoss = 0;

    while(i < N){
        swapped = 0;    
        currentResult = 0;
        bestResult = -10;
        currentBestSat = -1;
        cNodeSat = getNextSat(bNodeSat);
        cNodeSatPrev = bNodeSat;
        while(getCity(cNodeSat) != getCity(aNodeSat)){
            
            currentResult = checkInsert(aNodeSat, bNodeSat, cNodeSat);
            if(currentResult > bestResult && currentResult != -1){
                currentBestSat = cNodeSat;
                bestResult = currentResult;
                //break;            
            }
            cNodeSat = getNextSat(cNodeSat);
            
            

        }
        if(currentBestSat != -1){
            insert(aNodeSat, bNodeSat, currentBestSat);
            totalLoss += bestResult;
            
            //aNodeSat = cNodeSat;        
        }
        aNodeSat = bNodeSat;
        bNodeSat = getNextSat(bNodeSat);            
       
        i++;
    }

    return totalLoss;
}

float calculateTourLength(){
	float tourLength = 0;
	int i;
	for(i = 1; i < numberOfPoints; i++){
		tourLength += getDistanceFromMatrix(sTour[i-1], sTour[i]);
	}
	tourLength += getDistanceFromMatrix(sTour[numberOfPoints-1], sTour[0]);
	return tourLength;
}

float calculateSatTourLength(){

    float tourLength = 0;
    int i;
    short currSat = 0;
    short prevSat = 0;
    for(i = 0; i < numberOfPoints; i++){
        currSat = getNextSat(currSat);
        tourLength += getDistanceFromMatrix(getCity(prevSat), getCity(currSat));
        prevSat = currSat;        
    }
    return tourLength;
}

void initSat(){

    const int satelliteL = numberOfPoints << 1;
    satellites = (short*)malloc(satelliteL*sizeof(short));
    satellitesRecord = (short*)malloc(satelliteL*sizeof(short));
}


void printEdges(){
	qsort(edges,(size_t)numberOfEdges, sizeof(struct Edge),compareEdge);
	//int i;
	//for(i = 0; i < numberOfEdges; i++){
	//	printf("[%d-%d]: %f\n", edges[i].A, edges[i].B, getDistanceFromMatrix(edges[i].A, edges[i].B));
	//}
}

void interruptHandler(int sig){
    satellites = satellitesRecord;
    //printSatTour();
    printEdges();
    fprintf(stderr, "Iterations: %d\n", iterations);
    float tourLength = calculateSatTourLength();
    fprintf(stderr, "Length: %f\n", tourLength);
    greedyTour();
    tourLength = calculateTourLength();
    fprintf(stderr, "Greedy length: %f\n", tourLength);
    
        
    exit(0);
}

void randomInsert(){

    short aNodeSat = (rand() % numberOfPoints)*2;

    short cNodeSat = (rand() % numberOfPoints)*2;

    if(getCity(aNodeSat) == getCity(cNodeSat) || 
       getCity(getNextSat(aNodeSat)) == getCity(cNodeSat) ||
       getCity(getPrevSat(aNodeSat)) == getCity(cNodeSat))
            return;

    insert(aNodeSat, getNextSat(aNodeSat), cNodeSat);

}




int main(){
    iterations = 0;
	int pid;
    pid = fork();
    if(pid == 0){
        usleep(1880000);
        kill(getppid(),SIGINT);
        return 0;
     }else{
		srand(time(NULL));
		signal(SIGINT,interruptHandler);
		initializePoints();	
		createDistanceMatrix();
		printEdges();
		exit(0);
        initSat();
		greedyTour();
        //convertStourToSat();
        
        memcpy(satellitesRecord, satellites, sizeof(short)*numberOfPoints*2);
        //NNSatTour();
        
        int i;
        int j;
        float oldRecord = 0;
        float newRecord = 0;
        float intermediary = 0;
        float loss = 0;
        oldRecord = calculateSatTourLength();
        intermediary = oldRecord;
		while(2 > 1){
            iterations++;
            while(2 > 1){
			    twoOptSatTour();
                twoAndAHalfOpt();
                newRecord = calculateSatTourLength();
                //fprintf(stderr, "OldRecord: %f\nNewRecord: %f\n", oldRecord, newRecord);
                if(((abs(intermediary - newRecord)/intermediary) < 0.01)){
                    break;
                }
                intermediary = newRecord;
            }
            
            //newRecord = calculateSatTourLength();
            //newRecord = intermediary_a;
            if(newRecord < oldRecord){
                memcpy(satellitesRecord, satellites, sizeof(short)*numberOfPoints*2);
                oldRecord = newRecord;
            }
            for(i = 0; i < 1; i++)
                randomInsert();
		}
	}
	return 0;

}

