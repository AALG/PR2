import java.io.*;
import java.util.Random;

/*
*
*	Generate a TSP test instance.
*
*/


public class GenerateTSP {
	Kattio io;
	public GenerateTSP(){
		io = new Kattio(System.in, System.out);
	}
	/*
	* Generates a test case which specifies the number of points at the first line
	* followed by the coordinates of each point at each line.
	*/
	public void create_points(int numberOfPoints){
	
		
		try{
			  FileWriter fstream = new FileWriter("input_generated.txt");
			  BufferedWriter out = new BufferedWriter(fstream);
			  
			out.write(numberOfPoints + "\n");
			Random rnd = new Random();
			int limit = 0;
			while(limit < numberOfPoints ){
				out.write( 100*rnd.nextDouble() + " " + 100*rnd.nextDouble() + "\n");
				limit++;
			}
			out.close();
		}catch(Exception e){System.out.println("Something failed lol");}
		
		System.out.println("Created " + numberOfPoints + " points!");
	}

	public static void main(String[] args){
		GenerateTSP gtsp = new GenerateTSP();
		gtsp.create_points(new Integer(args[0]));
	}
	
}