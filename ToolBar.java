import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
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
	public static final Color background = new Color(240, 240, 240);
	public static final Color border = new Color(200, 200, 200);
	public static int toolLength = 10;						//The maximum amount of buttons in the tool bar
	private JButton[] tools = new JButton[11];				//Tool bar buttons

	public ToolBar()
	{
		for(byte i = 0; i < tools.length; i++)
		{
			BufferedImage image = null;
			try {
				image = ImageIO.read(new File("Images/ButtonIcon"+i+".png"));
			} catch (IOException e) {
				try {
					image = ImageIO.read(new File("Images/ButtonIcon.png"));
				} catch (IOException e1) {}
			}
			tools[i] = new JButton(new ImageIcon(image));
			//tools[i] = new JButton();
			tools[i].setSelectedIcon(new ImageIcon("Images/ButtonSelectedIcon.png"));
			tools[i].setBounds(5+40*i,5,30,30);
			tools[i].setBackground(background);
			tools[i].setBorderPainted(false);
			this.add(tools[i]);
		}
		toolLength = tools.length;
	}
	
	//Draws tool bar at top of screen
	public void paintComponent(Graphics g) 
	{
		drawToolBar(g);
	}
	
	//drawMenuFunctions(Graphics g) draws the menu containing the buttons at the top of the screen
	public void drawToolBar(Graphics g)
	{
		g.setColor(background);
		g.fillRect(0, 0, GUI.screenWidth - 1, GUI.toolBarHeight);
		g.setColor(border);
		g.drawRect(0, 0, GUI.screenWidth - 1, GUI.toolBarHeight);
	}
	
	public JButton getTools(int index)
	{
		return tools[index];
	}
}
