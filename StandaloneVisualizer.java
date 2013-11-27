import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


public class StandaloneVisualizer extends JFrame {

    JPanel panel;
    Node[] points;
    int[] tour;
    int dimension;
    boolean tree;

    public StandaloneVisualizer(String pointsFile, String tourFile){

        if(tourFile.equals("tree.txt"))
            tree = true;

        TSPIO tspio = new TSPIO();
        points = tspio.readInputFromFile(pointsFile);

        Kattio io = null;
        try{
            File file = new File(tourFile);
            FileInputStream fs = new FileInputStream(file);
            BufferedInputStream bs = new BufferedInputStream(fs);
            io = new Kattio(bs, System.out);
        }catch(FileNotFoundException e){ e.printStackTrace(); }

        
        tour = new int[points.length];
        for(int i = 0; i < points.length; i++){
            tour[i] = io.getInt();  
        }

        dimension = 500;
        new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
        setSize(dimension, dimension);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        panel = new GraphicsPanel();
        add(panel);
        this.setVisible(true);

    }

    class GraphicsPanel extends JPanel{
		public GraphicsPanel(){
			setPreferredSize(new Dimension(dimension, dimension));
		}

        public void paintComponent(Graphics g){
			super.paintComponent(g);
			int x;
			int y;
			for(Node node : points){
				x = new Double(node.x).intValue()*(4);
				y = new Double(node.y).intValue()*(4);
				g.drawOval(x, y, 5, 5);
                g.drawString(new Integer(node.ID).toString(), x, y);
				g.fillOval(x, y, 5, 5);
				/*if(node.equals(points[0])){
					g.setColor(Color.BLUE);
					g.fillOval(x-2, y-2, 15, 15);
				}
				if(node.equals(points[points.length-1])){
					g.setColor(Color.RED);
					g.fillOval(x-2, y-2, 15, 15);
				}
				g.setColor(Color.BLACK);*/
			}

            if(!tree){
                for(int i = 1; i < points.length; i++){
                    this.drawLine(g, points[tour[i-1]], points[tour[i]]);            
                }
                g.setColor(Color.GRAY);
                this.drawLine(g, points[tour[points.length-1]], points[tour[0]]);
            }
            
        }
        public void drawLine(Graphics g, Node nodeStart, Node nodeEnd){
			int startX, startY, endX, endY;
			startX = new Double(nodeStart.x).intValue();
			startY = new Double(nodeStart.y).intValue();
			
			endX = new Double(nodeEnd.x).intValue();
			endY = new Double(nodeEnd.y).intValue();
			g.drawLine(startX*4+5, startY*4+5, endX*4+5, endY*4+5);
		}
    }

    public static void main(String[] args){
        StandaloneVisualizer standaloneVis = 
                new StandaloneVisualizer(args[0], args[1]);
        return;
    }

}
