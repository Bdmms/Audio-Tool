import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
	private static final String[] colourSetNames = {"Default","Analogous","Monochromatic","Christmas","Triad"};
	private JComboBox<String> colourSwap = new JComboBox<String>(colourSetNames);
	private VolumeSlider slider = new VolumeSlider((short)0, (short)0, false);
	private JButton[] tools = new JButton[11];										//Tool bar buttons
	private short y = -42;															//Y location of the tool bar extension 
	public static int toolLength = 10;												//The maximum amount of buttons in the tool bar
	
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
			tools[i].setBorderPainted(false);
			this.add(tools[i]);
		}
		colourSwap.setBounds(GUI.screenWidth/2 + 50, 10, 200, 20);
		this.add(colourSwap);
		
		toolLength = tools.length;
	}
	
	//Draws tool bar at top of screen
	public void paintComponent(Graphics g) 
	{
		drawToolBar(g);
	}
	
	//resetButtonColours() resets the background colours of the buttons to match the colour scheme
	public void resetButtonColours()
	{
		for(byte i = 0; i < tools.length; i++)
		{
			tools[i].setBackground(GUI.colours[GUI.getColourScheme()][0]);
		}
	}
	
	//drawMenuFunctions(Graphics g) draws the menu containing the buttons at the top of the screen
	public void drawToolBar(Graphics g)
	{
		g.setColor(GUI.colours[GUI.getColourScheme()][0]);
		g.fillRect(0, 0, GUI.screenWidth - 1, GUI.toolBarHeight);
		g.setColor(GUI.colours[GUI.getColourScheme()][GUI.COLOUR_TEXT]);
		g.drawRect(0, 0, GUI.screenWidth - 1, GUI.toolBarHeight);
	}
	
	public void drawExtension(Graphics2D g)
	{
		//If in Note Editor
		if(MIDIMain.getMode() == 2)
		{
			//If notes have not been selected
			if(SelectableObject.isAllUnSelected())
				y = -42;
			else if(y < 0)
			{
				//At the start of the animation
				if(y == -42)
					slider.setVolume(MIDISong.getAvgVolume(MIDIMain.getTrackMenu()));
				y += 2;
			}
				
			//Volume Slider
			slider.setBounds((short)(GUI.screenWidth*5/8 + 10), (short)(GUI.toolBarHeight + 5 + y), (short)150, (short)30);
			
			//Background
			g.setColor(GUI.colours[GUI.getColourScheme()][2]);
			g.fillRoundRect(GUI.screenWidth*5/8, GUI.toolBarHeight + y, 250, 40, 20, 20);
			g.setColor(GUI.colours[GUI.getColourScheme()][GUI.COLOUR_BG]);
			g.fillRect(GUI.screenWidth*5/8 + 180, GUI.toolBarHeight + y + 10, 50, 20);
			//Text
			g.setColor(GUI.colours[GUI.getColourScheme()][GUI.COLOUR_TEXT]);
			g.setFont(GUI.defaultFont);
			g.drawString(slider.getPercent()+"%", GUI.screenWidth*5/8 + 190, GUI.toolBarHeight + y + 25);
			//Border
			g.setStroke(GUI.bold);
			g.drawRect(GUI.screenWidth*5/8 + 180, GUI.toolBarHeight + y + 10, 50, 20);
			g.drawRoundRect(GUI.screenWidth*5/8, GUI.toolBarHeight + y, 250, 40, 20, 20);
			
			slider.drawVolumeSlider(g);
			g.setStroke(GUI.basic);
		}
	}
	
	public void setColourScheme(byte c)
	{
		colourSwap.setSelectedIndex(c);
	}
	
	public VolumeSlider getSlider()
	{
		return slider;
	}
	
	public JButton getTools(int index)
	{
		return tools[index];
	}
	
	public JComboBox<String> getComboBox()
	{
		return colourSwap;
	}
}
