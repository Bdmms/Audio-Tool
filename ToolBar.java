import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * Date: October 24, 2016
 *
 * This class contains all elements of the toolbar at the
 * top of the screen
 */

public class ToolBar extends JPanel
{
	private static final long serialVersionUID = 2L;

	//Draws tool bar at top of screen
	public void paintComponent(Graphics g) 
	{
		drawToolBar(g);
	}
	
	//drawMenuFunctions(Graphics g) draws the menu containing the buttons at the top of the screen
	public void drawToolBar(Graphics g)
	{
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, GUI.screenWidth - 1, GUI.toolBarHeight);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, GUI.screenWidth - 1, GUI.toolBarHeight);
	}
}
