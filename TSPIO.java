/*
* This class contains IO functionality to handle
* input and output from/to KATTIS.
*/

import java.io.*;
import java.util.ArrayList;



public class TSPIO{


	Kattio io;

	public TSPIO(){
		//TODO: What todo here?
	}

	/**
	* Read input string from KATTIS.
	*/
	public Node[] readInputFromKattis(){
		io = new Kattio(System.in, System.out);
		return parseInput();
		
	}
	
	/**
	* Read input string from file and initialiaze
	* input/output streams.
	*
	* Used for off-line testing
	*/
	
	public Node[] readInputFromFile(String filename){
		
		try{
			File file               = new File(filename);
			FileInputStream fis     = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			io                      = new Kattio(bis, System.out);

		}catch(Exception e){ e.printStackTrace(); }
		
		return parseInput();
	}
	
	private Node[] parseInput(){
		int numberOfPoints = io.getInt();
		Node[] points = new Node[numberOfPoints];
		// Parse input convert to nodes.
		int counter = 0;
		double x = 0;
		double y = 0;
		while(counter < numberOfPoints){
			x = io.getDouble();
			y = io.getDouble();
			points[counter] = new Node(x,y,counter);
			counter++;
		}
		
		return points;
	}

	/* Print the tour */
	public void outputToKattis(Node[] tour){
		for(int i = 0; i < tour.length; i++){
			System.out.println(tour[i].ID);
		}
		
	}

}