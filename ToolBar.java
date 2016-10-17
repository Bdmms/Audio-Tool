import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class ToolBar extends JPanel
{
	private static final long serialVersionUID = 2L;

	public void paintComponent(Graphics g) 
	{
		if(MIDIMain.getMode() > 0)
			drawMenuFunctions(g);
	}
	
	//drawMenuFunctions(Graphics g) draws the menu containing the buttons at the topo of the screen
	public void drawMenuFunctions(Graphics g)
	{
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, 719, 40);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, 719, 40);
	}
}
