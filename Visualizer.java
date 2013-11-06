import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class Visualizer extends JFrame {

	JPanel panel;
	Node[] tsp;
	int xDim, yDim;
	
	public Visualizer(Node[] tsp, int shiftToRight, String title){
		super(title);
		this.tsp = tsp;
		xDim = 500;
		yDim = xDim;
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		setSize(xDim, yDim);
		Dimension windowSize = new Dimension(getPreferredSize());
		int wdwLeft = shiftToRight; //+ screenSize.width / 2 - windowSize.width / 2;
        int wdwTop = 0;
        setLocation(wdwLeft,wdwTop);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		panel = new GraphicsPanel();
		add(panel);
		this.setVisible(true);
	}
	
	class GraphicsPanel extends JPanel{
		public GraphicsPanel(){
			setPreferredSize(new Dimension(xDim, yDim));
		}
		
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			int x;
			int y;
			for(Node node : tsp){
				x = new Double(node.x).intValue()*4;
				y = new Double(node.y).intValue()*4;
				g.drawOval(x, y, 10, 10);
				g.fillOval(x, y, 10, 10);
				if(node.equals(tsp[0])){
					g.setColor(Color.BLUE);
					g.fillOval(x-2, y-2, 15, 15);
				}
				if(node.equals(tsp[tsp.length-1])){
					g.setColor(Color.RED);
					g.fillOval(x-2, y-2, 15, 15);
				}
				g.setColor(Color.BLACK);
			}
			for(int i = 1; i < tsp.length; i++)
				this.drawLine(g, tsp[i-1], tsp[i]);
			g.setColor(Color.GRAY);
			this.drawLine(g, tsp[tsp.length-1], tsp[0]);
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

}
