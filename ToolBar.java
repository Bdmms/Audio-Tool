import java.awt.Color;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ToolBar extends JPanel
{
	/**
	 * Date: October 24, 2016
	 *
	 * This class contains all elements of the toolbar at the
	 * top of the screen
	 */
	
	private static final long serialVersionUID = 2L;
	
	public static final byte toolLength = 18;				//The maximum amount of buttons in the tool bar
	
	private JButton[] tools = new JButton[18];				//Tool bar buttons

	public ToolBar()
	{
		for(byte i = 0; i < tools.length; i++)
		{
			tools[i] = new JButton(new ImageIcon("Images/ButtonIcon.png"));
			tools[i].setSelectedIcon(new ImageIcon("Images/ButtonSelectedIcon.png"));
			tools[i].setBounds(5+40*i,5,30,30);
			tools[i].setBackground(Color.LIGHT_GRAY);
			tools[i].setBorderPainted(false);
			this.add(tools[i]);
		}
	}
	
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
	
	public JButton getTools(int index)
	{
		return tools[index];
	}
}
