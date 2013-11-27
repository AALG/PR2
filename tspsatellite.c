#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <signal.h>
#include <math.h>
#include <time.h>
#include <stdint.h>
#include <float.h>




struct Node
{
    double x;
    double y;
    int ID;
};

short * satellites;
short * sTour;
int numberOfPoints;
double ** distanceMatrix;
struct Node * points;
struct Node * tour; 
int loops = 0;
double greedyLength = 0;
int iterations;

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



void printTour(){
	int i = 0;
	while(i < numberOfPoints){
		printf("%d\n", tour[i].ID);
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
	double x;
	double y;
	int ID;
	/* Read co-ordinates */
	int i = 0;
	while(i < numberOfPoints ){
		if( scanf("%lf %lf", &x, &y) == EOF )
			printf("Failure\n");
		points[i].x  = x;
		points[i].y  = y;
		points[i].ID = i;
		i++;
	}

}

double dist(struct Node * n1, struct Node * n2){

	double diffx = pow(n1->x - n2->x, 2);
	double diffy = pow(n1->y - n2->y, 2);

	return sqrt(diffx + diffy);

}

void createDistanceMatrix(){


	/* Allocate matrix */
	distanceMatrix = (double **)malloc(sizeof(double*)*numberOfPoints);
	int i;
	int j;
	for(i = 0; i < numberOfPoints; i++){
		distanceMatrix[i] = (double *)malloc(sizeof(double)*numberOfPoints);
	}

	for(i = 0; i < numberOfPoints - 1; i++)
		for(j = i + 1; j < numberOfPoints; j++){
			distanceMatrix[i][j] = dist(&points[i], &points[j]);
		}

}

double getDistanceFromMatrix(int i, int j){
	
	if(i > j){
		return distanceMatrix[j][i];
	}else if(i == j){
		return DBL_MAX;
	}
		return distanceMatrix[i][j];
}


int swapNodes(int i, int j){
	

	if(j < i){
		int tmp;
		tmp = i;
		i = j;
		j = tmp;
	}

	if(i == 0 && j == numberOfPoints - 1 || i == j)
		return 0;
   
    short iNode = sTour[i];
    short jNode = sTour[j];
	double edgeDistanceBefore = 0;
	double edgeDistanceAfter  = 0;

	if( i != 0){
		edgeDistanceBefore = getDistanceFromMatrix(iNode, sTour[i-1]);
		edgeDistanceAfter  = getDistanceFromMatrix(jNode, sTour[i-1]);
	}
	else{
		edgeDistanceBefore = getDistanceFromMatrix(iNode, sTour[numberOfPoints-1]);
		edgeDistanceAfter  = getDistanceFromMatrix(jNode, sTour[numberOfPoints-1]);
	}

	if( j != numberOfPoints - 1){
		edgeDistanceBefore += getDistanceFromMatrix(jNode, sTour[j+1]);
		edgeDistanceAfter  += getDistanceFromMatrix(iNode, sTour[j+1]);
	}else{
		edgeDistanceBefore += getDistanceFromMatrix(jNode, sTour[0]);
		edgeDistanceAfter  += getDistanceFromMatrix(iNode, sTour[0]);
	}

	/* The swap didn't yield a shorter path */
	if(edgeDistanceBefore <= edgeDistanceAfter)
		return 0;
	/* The swap did yield a shorter path */
	
	int c = j
;	int p = 0;
	short tmp;

	for(p = i ; p <= c; p++){
		tmp     = sTour[p];
		sTour[p] = sTour[c];
		sTour[c] = tmp;
		c--;
	}
	
	return 1;
}

void reverse(short a, short b, short c, short d){
    
    satellites[a] = c^1;
    satellites[c] = a^1;

    satellites[d^1] = b;
    satellites[b^1] = d;

}

double swapNodesCheck(short iNodeSat, short jNodeSat){

    if(getCity(iNodeSat) == getCity(jNodeSat))
		return -1;

	double edgeDistanceBefore = 0;
	double edgeDistanceAfter  = 0;

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
        /*setNext(sTour[i-1], sTour[i]);
        setPrev(sTour[i], sTour[i-1]);*/
        
 		used[bestCity] = 1;
 	}
    /*
    setNext(sTour[numberOfPoints-1], sTour[0]);
    setPrev(sTour[numberOfPoints-1], sTour[numberOfPoints-2]);
    */
 	free(used);

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


void twoOptTour(){

 	int i;
 	int j;
	
 	for(i = rand() / numberOfPoints; i < numberOfPoints; i++)
 		for(j = (i + 1) % numberOfPoints ; j != i; j = (j+1) % numberOfPoints){
 			swapNodes(i,j);
 		}
}


void twoOptSatTour(){

    short startNode = (rand() % (numberOfPoints*2));

    double currentResult;
    double bestResult;    
    short currentBestSat;

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
        }
        iNodeSat = getNextSat(iNodeSat);
        jNodeSat = iNodeSat;
    }

}

double checkInsert(short aNodeSat, short bNodeSat, short cNodeSat){

    double distanceBeforeInsert;
    double distanceAfterInsert;
    
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

void twoAndAHalfOpt(){

    short N = rand() % numberOfPoints;
    
    short aNodeSat = N*2;
    short bNodeSat = getNextSat(aNodeSat);
    
    short cNodeSat;
    short cNodeSatPrev;
    
    int i = 0;
    char swapped;
    short tmp;

    double currentResult;
    double bestResult;

    short currentBestSat;

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
                break;            
            }
            cNodeSat = getNextSat(cNodeSat);
            
            

        }
        if(currentBestSat != -1){
            insert(aNodeSat, bNodeSat, currentBestSat);
            
            //aNodeSat = cNodeSat;        
        }
        aNodeSat = bNodeSat;
        bNodeSat = getNextSat(bNodeSat);            
       
        i++;
    }

}


long specialIntExp(int exp){

    int i;
    long res = 1;
    for(i = 0; i < exp; i++){
        res *= 10;
        fprintf(stderr, "%ld\n", res);
    }
    
    return res;
}

void printSTour(){
	int i = 0;
	while(i < numberOfPoints){
		printf("%d\n", sTour[i]);
		i++;
	}
}




double calculateTourLength(){
	double tourLength = 0;
	int i;
	for(i = 1; i < numberOfPoints; i++){
		tourLength += getDistanceFromMatrix(sTour[i-1], sTour[i]);
	}
	tourLength += getDistanceFromMatrix(sTour[numberOfPoints-1], sTour[0]);
	return tourLength;
}

double calculateSatTourLength(){

    double tourLength = 0;
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
}



void interruptHandler(int sig){
    printSatTour();
    fprintf(stderr, "Iterations: %d\n", iterations);
    double tourLength = calculateSatTourLength();
    fprintf(stderr, "Length: %lf\n", tourLength);
    greedyTour();
    tourLength = calculateTourLength();
    fprintf(stderr, "Greedy length: %lf\n", tourLength);
    
        
    exit(0);
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
        initSat();
		greedyTour();
        //NNSatTour();
        convertStourToSat();
        int i;
		while(2 > 1){
            iterations++;
			twoOptSatTour();
            //for(i = 0; i < 2; i++)
                twoAndAHalfOpt();

		}
	}
	return 0;

}

