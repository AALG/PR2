#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <signal.h>
#include <math.h>
#include <time.h>
#include <stdint.h>
# include <float.h>


struct Node
{
    double x;
    double y;
    int ID;
};
int numberOfPoints;
double ** distanceMatrix;
struct Node * points;
struct Node * tour; 
int loops = 0;
double greedyLength = 0;


void printTour(){
	int i = 0;
	while(i < numberOfPoints){
		printf("%d\n", tour[i].ID);
		i++;
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
   
	struct Node iNode = tour[i];
	struct Node jNode = tour[j];
	double edgeDistanceBefore = 0;
	double edgeDistanceAfter  = 0;

	if( i != 0){
		edgeDistanceBefore = getDistanceFromMatrix(iNode.ID, tour[i-1].ID);
		edgeDistanceAfter  = getDistanceFromMatrix(jNode.ID, tour[i-1].ID);
	}
	else{
		edgeDistanceBefore = getDistanceFromMatrix(iNode.ID, tour[numberOfPoints-1].ID);
		edgeDistanceAfter  = getDistanceFromMatrix(jNode.ID, tour[numberOfPoints-1].ID);
	}

	if( j != numberOfPoints - 1){
		edgeDistanceBefore += getDistanceFromMatrix(jNode.ID, tour[j+1].ID);
		edgeDistanceAfter  += getDistanceFromMatrix(iNode.ID, tour[j+1].ID);
	}else{
		edgeDistanceBefore += getDistanceFromMatrix(jNode.ID, tour[0].ID);
		edgeDistanceAfter  += getDistanceFromMatrix(iNode.ID, tour[0].ID);
	}

	/* The swap didn't yield a shorter path */
	if(edgeDistanceBefore <= edgeDistanceAfter)
		return 0;
	/* The swap did yield a shorter path */
	
	int c = j
;	int p = 0;
	struct Node tmp;

	for(p = i ; p <= c; p++){
		tmp     = tour[p];
		tour[p] = tour[c];
		tour[c] = tmp;
		c--;
	}
	
	return 1;
}
 

 void greedyTour(){

 	/* Allocate memory for tour */
 	tour =  (struct Node *)malloc(sizeof(struct Node)*numberOfPoints);
 	char * used = (char *)malloc(sizeof(char)*numberOfPoints);
 	/* Start at first node in points */
 	tour[0] = points[0];
 	used[0] = 1;
 	struct Node *bestCity    = NULL;
 	struct Node *currentCity = NULL;
 	int i;
 	int j;
 	/* Start the tour ! */
 	for(i = 1; i < numberOfPoints; i++){
 		bestCity = NULL;
 		for(j = 0; j < numberOfPoints; j++){
 			currentCity = &points[j];
 			if(used[currentCity->ID] != 1)
 				if(bestCity == NULL || (getDistanceFromMatrix(tour[i-1].ID, currentCity->ID) < getDistanceFromMatrix(tour[i-1].ID, bestCity->ID)))
 					bestCity = currentCity;
 		}
 		tour[i] = *bestCity;
 		used[bestCity->ID] = 1;
 	}

 	free(used);

 }



 void twoOptTour(){

 	int i;
 	int j;
 	int val = 0;
	
 	for(i = rand() / numberOfPoints; i < numberOfPoints; i++)
 		for(j = 0; j < numberOfPoints - 1; j++){
 			val = swapNodes(i,j);
 			if( val == 1 )
 				return;
 		}
 }

double calculateTourLength(){
	double tourLength = 0;
	int i;
	for(i = 1; i < numberOfPoints; i++){
		tourLength += getDistanceFromMatrix(tour[i-1].ID, tour[i].ID);
	}
	tourLength += getDistanceFromMatrix(tour[numberOfPoints-1].ID, tour[0].ID);
	return tourLength;
}


void interruptHandler(int sig){
    printTour();
	/*printf("Tour(Greedy): %lf\n", greedyLength);
	double t = calculateTourLength();
	printf("Tour(2OPT): %lf\n", t);
	printf("DIFF: %lf\n", greedyLength - t);*/
	//free(tour);
	//free(points);
    exit(0);
}

int main(){
	int pid;
    pid = fork();
    if(pid == 0){
        usleep(1800000);
        kill(getppid(),SIGINT);
        return 0;
     }else{
		srand(time(NULL));
		signal(SIGINT,interruptHandler);
		
		initializePoints();	
		createDistanceMatrix();
		greedyTour();
		greedyLength = calculateTourLength();
		int i = 0;
		while(2 > 1){
			i++;
			twoOptTour();
		}
	}
	free(tour);
	free(points);
	//printTour();
	return 0;

}
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <signal.h>
#include <math.h>
#include <time.h>
#include <stdint.h>
# include <float.h>


struct Node
{
    double x;
    double y;
    int ID;
};
int numberOfPoints;
double ** distanceMatrix;
struct Node * points;
struct Node * tour; 
int loops = 0;
double greedyLength = 0;


void printTour(){
	int i = 0;
	while(i < numberOfPoints){
		printf("%d\n", tour[i].ID);
		i++;
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
   
	struct Node iNode = tour[i];
	struct Node jNode = tour[j];
	double edgeDistanceBefore = 0;
	double edgeDistanceAfter  = 0;

	if( i != 0){
		edgeDistanceBefore = getDistanceFromMatrix(iNode.ID, tour[i-1].ID);
		edgeDistanceAfter  = getDistanceFromMatrix(jNode.ID, tour[i-1].ID);
	}
	else{
		edgeDistanceBefore = getDistanceFromMatrix(iNode.ID, tour[numberOfPoints-1].ID);
		edgeDistanceAfter  = getDistanceFromMatrix(jNode.ID, tour[numberOfPoints-1].ID);
	}

	if( j != numberOfPoints - 1){
		edgeDistanceBefore += getDistanceFromMatrix(jNode.ID, tour[j+1].ID);
		edgeDistanceAfter  += getDistanceFromMatrix(iNode.ID, tour[j+1].ID);
	}else{
		edgeDistanceBefore += getDistanceFromMatrix(jNode.ID, tour[0].ID);
		edgeDistanceAfter  += getDistanceFromMatrix(iNode.ID, tour[0].ID);
	}

	/* The swap didn't yield a shorter path */
	if(edgeDistanceBefore <= edgeDistanceAfter)
		return 0;
	/* The swap did yield a shorter path */
	
	int c = j
;	int p = 0;
	struct Node tmp;

	for(p = i ; p <= c; p++){
		tmp     = tour[p];
		tour[p] = tour[c];
		tour[c] = tmp;
		c--;
	}
	
	return 1;
}
 

 void greedyTour(){

 	/* Allocate memory for tour */
 	tour =  (struct Node *)malloc(sizeof(struct Node)*numberOfPoints);
 	char * used = (char *)malloc(sizeof(char)*numberOfPoints);
 	/* Start at first node in points */
 	tour[0] = points[0];
 	used[0] = 1;
 	struct Node *bestCity    = NULL;
 	struct Node *currentCity = NULL;
 	int i;
 	int j;
 	/* Start the tour ! */
 	for(i = 1; i < numberOfPoints; i++){
 		bestCity = NULL;
 		for(j = 0; j < numberOfPoints; j++){
 			currentCity = &points[j];
 			if(used[currentCity->ID] != 1)
 				if(bestCity == NULL || (getDistanceFromMatrix(tour[i-1].ID, currentCity->ID) < getDistanceFromMatrix(tour[i-1].ID, bestCity->ID)))
 					bestCity = currentCity;
 		}
 		tour[i] = *bestCity;
 		used[bestCity->ID] = 1;
 	}

 	free(used);

 }



 void twoOptTour(){

 	int i;
 	int j;
 	int val = 0;
	
 	for(i = rand() / numberOfPoints; i < numberOfPoints; i++)
 		for(j = 0; j < numberOfPoints - 1; j++){
 			val = swapNodes(i,j);
 			if( val == 1 )
 				return;
 		}
 }

double calculateTourLength(){
	double tourLength = 0;
	int i;
	for(i = 1; i < numberOfPoints; i++){
		tourLength += getDistanceFromMatrix(tour[i-1].ID, tour[i].ID);
	}
	tourLength += getDistanceFromMatrix(tour[numberOfPoints-1].ID, tour[0].ID);
	return tourLength;
}


void interruptHandler(int sig){
    printTour();
	/*printf("Tour(Greedy): %lf\n", greedyLength);
	double t = calculateTourLength();
	printf("Tour(2OPT): %lf\n", t);
	printf("DIFF: %lf\n", greedyLength - t);*/
	//free(tour);
	//free(points);
    exit(0);
}

int main(){
	int pid;
    pid = fork();
    if(pid == 0){
        usleep(1800000);
        kill(getppid(),SIGINT);
        return 0;
     }else{
		srand(time(NULL));
		signal(SIGINT,interruptHandler);
		
		initializePoints();	
		createDistanceMatrix();
		greedyTour();
		greedyLength = calculateTourLength();
		int i = 0;
		while(2 > 1){
			i++;
			twoOptTour();
		}
	}
	free(tour);
	free(points);
	//printTour();
	return 0;

}

