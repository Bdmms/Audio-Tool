import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 * <b>[Date: October 24, 2016]</b>
 * <p>
 * This class contains all elements of the toolbar at the
 * top of the screen
 * </p>
 */
public class ToolBar extends JPanel
{
	private static final long serialVersionUID = 2L;
	private static final String[] colourSetNames = {"Default","Analogous","Monochromatic","Holiday","Triad"};	//The names for the colours
	public static int toolLength = 11;												//The maximum amount of buttons in the tool bar
	private JComboBox<String> colourSwap = new JComboBox<String>(colourSetNames);	//A combo box that allows the colour scheme to be changed
	private JButton[] tools = new JButton[11];										//Tool bar buttons
	private VolumeSlider slider = new VolumeSlider((short)0, (short)0, false);		//The volume slider for notes
	private short y = -42;															//Y location of the tool bar extension					
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public ToolBar()}</pre></p> 
	 * The constructor. It sets the tools for the toolBar.</p> 
	 */
	public ToolBar()
	{
		//Goes through every tool
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
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void changeIcon(boolean playing)}</pre></p> 
	 * Changes the icon image of the tools when the song is paused or played.</p> 
	 * @param playing = whether the icon should for when the program is playing or not
	 */
	public void changeIcon(boolean playing)
	{
		//Only the first two tools change their image
		for(byte i = 0; i < 2; i++)
		{
			//If the song is playing
			if(playing)
				tools[i].setIcon(new ImageIcon(drawButton((byte)(i + 11))));
				//Additional images are stored beyond the tool length (why there is a +11)
			else
				tools[i].setIcon(new ImageIcon(drawButton(i)));
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void paintComponent(Graphics g)}</pre></p> 
	 * Draws tool bar at top of screen.</p> 
	 * @param g = component of the JPanel used to create visual elements
	 */
	public void paintComponent(Graphics g) 
	{
		drawToolBar(g);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public BufferedImage drawButton(byte buttonType)}</pre></p> 
	 * Draws the icon image for the tool (considering state and colour).</p>
	 * @param buttonType = index of tool that is being drawn 
	 * @return The image of the drawn icon
	 */
	public BufferedImage drawButton(byte buttonType)
	{
		BufferedImage im = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = im.createGraphics();
		//Draws background
		g.setColor(GUI.colours[GUI.getColourScheme()][1]);
		g.fillOval(0, 0, 29, 29);

		//The program chooses what icon to draw on top
		g.setColor(GUI.colours[GUI.getColourScheme()][2]);
		//TOOL: PLAY
		if(buttonType == 0)
		{
			int[] x = {8,26,8};
			int[] y = {24,15,6};
			g.fillPolygon(x, y, 3);
		}
		//TOOL: START
		else if(buttonType == 1)
		{
			g.drawRect(7, 7, 15, 15);
		}
		//TOOL: ADD
		else if(buttonType == 2)
		{
			g.fillRect(12, 5, 6, 20);
			g.fillRect(5, 12, 20, 6);
		}
		//TOOL: DELETE
		else if(buttonType == 3)
		{
			g.fillRect(5, 12, 20, 6);
		}
		//TOOL: SKIP LEFT
		else if(buttonType == 4)
		{
			int[] x = {15,4,15};
			int[] y = {25,15,5};
			g.fillPolygon(x, y, 3);
			g.fillRect(18, 5, 4, 20);
		}
		//TOOL: SKIP RIGHT
		else if(buttonType == 5)
		{
			int[] x = {15,26,15};
			int[] y = {25,15,5};
			g.fillPolygon(x, y, 3);
			g.fillRect(8, 5, 4, 20);
		}
		//TOOL: SWAP
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
		//TOOL: MERGE
		else if(buttonType == 7)
		{
			int[] x = {6,6,14};
			int[] y = {6,24,15};
			g.fillPolygon(x, y, 3);
			int[] a = {24,24,16};
			int[] b = {6,24,15};
			g.fillPolygon(a, b, 3);
		}
		//TOOL: TOGGLE INFOBAR
		else if(buttonType == 8)
		{
			g.drawRect(7, 7, 16, 16);
			g.drawRect(10, 10, 10, 10);
		}
		//TOOL: SELECT ALL
		else if(buttonType == 9)
		{
			g.setStroke(GUI.superBold);
			g.drawOval(7, 7, 16, 16);
			g.setStroke(GUI.basic);
		}
		//TOOL: RETURN / GO BACK
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
		//TOOL: PAUSE
		else if(buttonType == 11)
		{
			g.fillRect(8, 6, 5, 18);
			g.fillRect(17, 6, 5, 18);
		}
		//TOOL: STOP
		else if(buttonType == 12)
		{
			g.fillRect(7, 7, 16, 16);
		}
		
		//Draws border
		g.setColor(GUI.colours[GUI.getColourScheme()][GUI.COLOUR_TEXT]);
		g.drawOval(0, 0, 29, 29);
		g.dispose();
		return im;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void drawToolBar(Graphics g)}</pre></p> 
	 * Draws the background for the tool bar.</p>
	 * @param g = component of the JPanel used to create visual elements 
	 */
	public void drawToolBar(Graphics g)
	{
		g.setColor(GUI.colours[GUI.getColourScheme()][0]);
		g.fillRect(0, 0, GUI.screenWidth - 1, GUI.toolBarHeight);
		g.setColor(GUI.colours[GUI.getColourScheme()][GUI.COLOUR_TEXT]);
		g.drawRect(0, 0, GUI.screenWidth - 1, GUI.toolBarHeight);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void drawExtension(Graphics g)}</pre></p> 
	 * Draws the extension of the tool bar and also contains the drop down animation.</p>
	 * @param g = component of the JPanel used to create visual elements 
	 */
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
			
			//Volume slider
			slider.drawVolumeSlider(g);
			g.setStroke(GUI.basic);
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void resetButtonColours()}</pre></p> 
	 * Resets the background colours of the buttons to match the colour scheme.</p> 
	 */
	public void resetButtonColours()
	{
		for(byte i = 0; i < tools.length; i++)
		{
			tools[i].setBackground(GUI.colours[GUI.getColourScheme()][0]);
			tools[i].setIcon(new ImageIcon(drawButton(i)));
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void resetVolume()}</pre></p> 
	 * Resets the volume of the volume slider.</p> 
	 */
	public void resetVolume()
	{
		//If objects are selected
		if(!SelectableObject.isAllUnSelected())
			slider.setVolume(MIDISong.getAvgVolume(MIDIMain.getTrackMenu()));
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setColourScheme(byte c)}</pre></p> 
	 * Sets the colour scheme for the program.</p> 
	 * @param c = new colour scheme mode
	 */
	public void setColourScheme(byte c)
	{
		colourSwap.setSelectedIndex(c);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public VolumeSlider getSlider()}</pre></p> 
	 * Returns the volume slider in the toolBar extension.</p> 
	 * @return The <b>VolumeSlider</b> object
	 */
	public VolumeSlider getSlider()
	{
		return slider;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public JButton getTools(int index)}</pre></p> 
	 * Returns the specified tool button from the toolBar.</p> 
	 * @param index = the index of the tool
	 * @return The <b>JButton</b> object of the tool
	 */
	public JButton getTools(int index)
	{
		return tools[index];
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public JComboBox<String> getComboBox()}</pre></p> 
	 * Returns the combo box that stores the colour scheme data.</p> 
	 * @return The <b>JComboBox</b> object that controls colour schemes
	 */
	public JComboBox<String> getComboBox()
	{
		return colourSwap;
	}
}
