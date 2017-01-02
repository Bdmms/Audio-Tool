import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
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
	private JComboBox<String> colourSwap = new JComboBox<String>(colourSetNames);	//A combo box that allows the colour scheme to be changed
	private JButton[] tools = new JButton[11];										//Tool bar buttons
	private VolumeSlider slider = new VolumeSlider((short)0, (short)0, false);		//The volume slider for notes
	private short y = -42;															//Y location of the tool bar extension 
	public static int toolLength = 10;												//The maximum amount of buttons in the tool bar
	
	public ToolBar()
	{
		for(byte i = 0; i < tools.length; i++)
		{
			tools[i] = new JButton(new ImageIcon(drawButton(i)));
			//To allow key listener to work
			tools[i].setFocusable(false);
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
	
	public void changeIcon(boolean playing)
	{
		for(byte i = 0; i < 2; i++)
		{
			if(playing)
				tools[i].setIcon(new ImageIcon(drawButton((byte)(i + 11))));
			else
				tools[i].setIcon(new ImageIcon(drawButton(i)));
		}
	}
	
	//resetButtonColours() resets the background colours of the buttons to match the colour scheme
	public void resetButtonColours()
	{
		for(byte i = 0; i < tools.length; i++)
		{
			tools[i].setBackground(GUI.colours[GUI.getColourScheme()][0]);
			tools[i].setIcon(new ImageIcon(drawButton(i)));
		}
	}
	
	public void resetVolume()
	{
		if(!SelectableObject.isAllUnSelected())
			slider.setVolume(MIDISong.getAvgVolume(MIDIMain.getTrackMenu()));
	}
	
	public BufferedImage drawButton(byte buttonType)
	{
		BufferedImage im = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = im.createGraphics();
		g.setColor(GUI.colours[GUI.getColourScheme()][1]);
		g.fillOval(0, 0, 29, 29);

		g.setColor(GUI.colours[GUI.getColourScheme()][2]);
		if(buttonType == 0)
		{
			int[] x = {8,26,8};
			int[] y = {24,15,6};
			g.fillPolygon(x, y, 3);
		}
		else if(buttonType == 1)
		{
			g.drawRect(7, 7, 15, 15);
		}
		else if(buttonType == 2)
		{
			g.fillRect(12, 5, 6, 20);
			g.fillRect(5, 12, 20, 6);
		}
		else if(buttonType == 3)
		{
			g.fillRect(5, 12, 20, 6);
		}
		else if(buttonType == 4)
		{
			int[] x = {15,4,15};
			int[] y = {25,15,5};
			g.fillPolygon(x, y, 3);
			g.fillRect(18, 5, 4, 20);
		}
		else if(buttonType == 5)
		{
			int[] x = {15,26,15};
			int[] y = {25,15,5};
			g.fillPolygon(x, y, 3);
			g.fillRect(8, 5, 4, 20);
		}
		else if(buttonType == 6)
		{
			g.setStroke(GUI.superBold);
			g.drawOval(7, 7, 16, 16);
			g.setStroke(GUI.basic);
			g.setColor(GUI.colours[GUI.getColourScheme()][1]);
			g.fillRect(13, 5, 4, 20);
			g.setColor(GUI.colours[GUI.getColourScheme()][2]);
			int[] x = {16,16,23};
			int[] y = {25,18,25};
			g.fillPolygon(x, y, 3);
			int[] a = {14,14,7};
			int[] b = {5,12,5};
			g.fillPolygon(a, b, 3);
		}
		else if(buttonType == 7)
		{
			int[] x = {6,6,14};
			int[] y = {6,24,15};
			g.fillPolygon(x, y, 3);
			int[] a = {24,24,16};
			int[] b = {6,24,15};
			g.fillPolygon(a, b, 3);
		}
		else if(buttonType == 8)
		{
			g.drawRect(7, 7, 16, 16);
			g.drawRect(10, 10, 10, 10);
		}
		else if(buttonType == 9)
		{
			g.setStroke(GUI.superBold);
			g.drawOval(7, 7, 16, 16);
			g.setStroke(GUI.basic);
		}
		else if(buttonType == 10)
		{
			g.setStroke(GUI.superBold);
			g.drawOval(13, 10, 10, 10);
			g.setColor(GUI.colours[GUI.getColourScheme()][1]);
			g.fillRect(9, 5, 9, 20);
			g.setColor(GUI.colours[GUI.getColourScheme()][2]);
			g.drawLine(18, 20, 10, 20);
			g.drawLine(18, 10, 10, 10);
			g.setStroke(GUI.basic);
			int[] x = {13,13,5};
			int[] y = {5,17,11};
			g.fillPolygon(x, y, 3);
		}
		else if(buttonType == 11)
		{
			g.fillRect(8, 6, 5, 18);
			g.fillRect(17, 6, 5, 18);
		}
		else if(buttonType == 12)
		{
			g.fillRect(7, 7, 16, 16);
		}
		
		g.setColor(GUI.colours[GUI.getColourScheme()][GUI.COLOUR_TEXT]);
		g.drawOval(0, 0, 29, 29);
		g.dispose();
		return im;
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
				y += 3;
				
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
