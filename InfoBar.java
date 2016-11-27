import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;


public class InfoBar extends JPanel{

	/**
	 * Date: Novemeber 21, 2016
	 * 
	 * This class is responsible for displaying the information of the song in the track menu
	 * This information includes the song's tempo, length and time signature
	 */
	
	private static final long serialVersionUID = 2L;
	//private long length = (MIDISong.getSequence().getTickLength()) / 1000000;
	//private String songName = MIDISong.getSequence().toString();
	
	
	public void paintComponent(Graphics g) 
	{
		Graphics2D g2D = (Graphics2D) g;
		drawInfoBar(g2D);
	}
	
	public void drawInfoBar(Graphics2D g)
	{
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, (GUI.screenWidth / 2) -15, 60);
		g.setColor(Color.BLACK);
		g.setStroke(GUI.bold);
		g.drawRect(0, 0, (GUI.screenWidth / 2) -15, 60);
	}
}
