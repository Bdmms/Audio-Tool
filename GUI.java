import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

public class GUI extends JPanel
{
	/**
	 * Date: October 15, 2016
	 * 
	 * This class displays / outputs visual information by drawing pixels
	 * on the program's window.
	 * 
	 * Note 1: This class contains required methods that CANNOT be removed.
	 * 
	 * Note 2: Most methods in this class contain the parameter Graphics g, 
	 * therefore the explanation will not be repeated.
	 */
	
	//GUI Size = 720 x 480
	private static final long serialVersionUID = 1L;						
	public final static byte toolBarHeight = 40;							//Height of toolBar
	public final static byte topBarHeight = 20;								//Height of top label
	public final static byte fullAddHeight = toolBarHeight + topBarHeight ;//Combined height
	public final static byte windowBarHeight = 55;							//Height Window label bar
	public final static short sideBarWidth = 100;							//Side label width
	public final static short screenHeight = 480;							//Screen height
	public final static short screenWidth = 720;							//Screen width
	
	//paintComponent(Graphics g) responds to the .repaint() method when used
	//Graphics g = component of the JPanel used to create visual elements
	public void paintComponent(Graphics g) 
	{
		g.setColor(Color.BLACK);
		//g.drawRect(0, 0, 719, 479);
    
		//Welcome Screen
		if(MIDIMain.getMode() == 0)
		{
			drawStartScreen(g);
		}
		//Track Editor
		else if(MIDIMain.getMode() == 1)
		{
			drawTrackEditor(g);
			g.setColor(Color.BLACK);
		}
		//Note Editor
		else if(MIDIMain.getMode() == 2)
		{
			drawNoteEditor(g);
			g.setColor(Color.BLACK);
		}
		
		NotifyAnimation.drawNoteWindow(g);
    }
	
	//drawStartScreen(Graphics g) draws the welcome screen (menu = 0)
	public void drawStartScreen(Graphics g)
	{
		g.setFont(new Font("FONT", Font.BOLD, 50));
		g.drawString("WELCOME", 230, 240);
	}
	
	//drawTrackEditor(Graphics g) draws the track editor (menu = 1)
	public void drawTrackEditor(Graphics g)
	{
		for(byte i = 0; i < MIDISong.getTracksLength(); i++)
		{
			MIDISong.getTracks(i).drawTrack(g, i);
		}
		/* 
		 * The status window
		 * 
		 * g.setColor(Color.WHITE);
		 * g.fillRect(20, 430, 500, 50);
		 * g.setColor(Color.BLACK);
		 * g.drawRect(20, 430, 500, 50);
		 */
	}
	
	//drawNoteEditor(Graphics g) draws the note editor (menu = 2)
	public void drawNoteEditor(Graphics g)
	{
		g.setFont(new Font("FONT", Font.ROMAN_BASELINE, 12));
		drawGridField(g, (short) ((screenHeight - fullAddHeight)/MIDIMain.getPreHeight() + 1), (short) ((screenWidth - sideBarWidth)/MIDIMain.getPreLength() + 1));
		drawNotes(g);
		drawGridLabels(g, (short) ((screenHeight - fullAddHeight)/MIDIMain.getPreHeight() + 1), (short) ((screenWidth - sideBarWidth)/MIDIMain.getPreLength() + 1));
	}
	
	//drawGridLabels(Graphics g, short height, short width) draws the labels on the sides of the grid
	//short height = height of the grid (in tones)
	//short width = width of the grid (in ticks)
	public void drawGridLabels(Graphics g, short height, short width)
	{
		drawSideGridLabel(g, (short) height);
		drawTopGridLabel(g, (short) width);
		
		g.setColor(Color.WHITE);
		g.fillRect(0, toolBarHeight, sideBarWidth, topBarHeight);
		
		g.setColor(Color.BLACK);
		g.drawRect(0, toolBarHeight, sideBarWidth, topBarHeight);
		g.drawString("INSERT TITLE", 10, toolBarHeight + 15);
		
	}
	
	//drawTopGridLabel(Graphics g, short width) draws the label on the top side of the grid
	//short width = width of the grid (in ticks)
	public void drawTopGridLabel(Graphics g, short width)
	{
		g.setColor(Color.WHITE);
		g.fillRect(0, toolBarHeight, screenWidth, topBarHeight);
		
		g.setColor(Color.BLACK);
		g.drawRect(0, toolBarHeight, screenWidth, topBarHeight);
		for(int i = (int) (MIDIMain.getXCoordinate()/MIDIMain.getPreLength()-1); i < (int) (MIDIMain.getXCoordinate()/MIDIMain.getPreLength() + width + 1); i++)
		{
			//If scale > 20
			if(MIDIMain.getPreLength() > 20)
			{
				g.drawLine((int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), toolBarHeight, (int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), fullAddHeight);
				g.drawString(i+"", (int)(sideBarWidth + MIDIMain.getPreLength()/2 - 5 + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), fullAddHeight - 5);
			}
			//If 20 > scale > 15
			else if(MIDIMain.getPreLength() > 15 && i%2 == 0)
			{
				g.drawLine((int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), toolBarHeight, (int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), fullAddHeight);
				g.drawString(i+"", (int)(sideBarWidth + MIDIMain.getPreLength() - 5 + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), fullAddHeight - 5);
			}
			//If scale < 15
			else if(MIDIMain.getPreLength() > 10 && i%4 == 0)
			{
				g.drawLine((int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), toolBarHeight, (int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), fullAddHeight);
				g.drawString(i+"", (int)(sideBarWidth + MIDIMain.getPreLength()*2 - 5 + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), fullAddHeight - 5);
			}
			//If scale < 10
			else if(i%8 == 0)
			{
				g.drawLine((int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), toolBarHeight, (int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), fullAddHeight);
				if(MIDIMain.getPreLength() > 5)
					g.drawString(i+"", (int)(sideBarWidth + MIDIMain.getPreLength()*4 - 5 + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), fullAddHeight - 5);
			}
		}
	}
	
	//drawSideGridLabel(Graphics g, short height) draws labels on the side of the grid
	//short height = height of the grid (in tones)
	public void drawSideGridLabel(Graphics g, short height)
	{
		g.setColor(Color.WHITE);
		g.fillRect(0, fullAddHeight, sideBarWidth, screenHeight - fullAddHeight);
		
		g.setColor(Color.BLACK);
		g.drawRect(0, fullAddHeight, sideBarWidth, screenHeight - fullAddHeight);
		for(byte i = (byte) (MIDIMain.getYCoordinate()/MIDIMain.getPreHeight()); i < MIDIMain.getYCoordinate()/MIDIMain.getPreHeight() + height + 1; i++)
		{
			//If scale > 15
			if(MIDIMain.getPreHeight() > 15)
			{
				g.drawLine(0, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate(), sideBarWidth, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate());
				g.drawString(Notes.convertToNote((byte)(Notes.MAX_TONE - i), true), sideBarWidth/2 - 10, fullAddHeight + MIDIMain.getPreHeight()/2 + 5 + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate());
			}
			//If scale < 15
			else if(i%2 == 0)
			{
				g.drawLine(0, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate(), sideBarWidth, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate());
				g.drawString(Notes.convertToNote((byte)(Notes.MAX_TONE - i), true), sideBarWidth/2 - 10, fullAddHeight + MIDIMain.getPreHeight() + 5 + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate());
			}
		}
	}
	
	//drawGridField(Graphics g, short height, short width) draws the lines of the grid
	//short height = height of the grid (in tones)
	//short width = width of the grid (in ticks)
	public void drawGridField(Graphics g, short height, short width)
	{
		//Vertical Lines
		g.setColor(Color.BLUE);
		for(int i = (int) (MIDIMain.getXCoordinate()/MIDIMain.getPreLength()); i < (int) (MIDIMain.getXCoordinate()/MIDIMain.getPreLength() + width + 1); i++)
		{
			if(MIDIMain.getPreLength() > 20 || (MIDIMain.getPreLength() > 15 && i%2 == 0) || (MIDIMain.getPreLength() > 10 && i%4 == 0) || i%8 == 0)
				g.drawLine((int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), fullAddHeight, (int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), screenHeight);
		}
		
		//Horizontal Lines
		g.setColor(Color.LIGHT_GRAY);
		for(byte i = (byte) (MIDIMain.getYCoordinate()/MIDIMain.getPreHeight()); i < MIDIMain.getYCoordinate()/MIDIMain.getPreHeight() + height + 1; i++)
		{
			if(MIDIMain.getPreHeight() > 15 || i%2 == 0)
				g.drawLine(sideBarWidth, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate(), screenWidth, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate());
		}
	}
	
	//drawNotes(Graphics g) draws the notes in a track
	public void drawNotes(Graphics g)
	{
		//Loops through every note and draws it as a rectangle
		g.setColor(Color.BLACK);
		for(int i = 0; i < MIDISong.getNotes(MIDIMain.getTrackMenu()).length; i++)
		{
			//If note is on screen
			if(isNoteVisible(i))
			{
				g.fillRect((int)(MIDISong.getNotes(MIDIMain.getTrackMenu())[i].getX() - MIDIMain.getXCoordinate() + sideBarWidth), MIDISong.getNotes(MIDIMain.getTrackMenu())[i].getY() + 1 - MIDIMain.getYCoordinate() + fullAddHeight, MIDISong.getNotes(MIDIMain.getTrackMenu())[i].getLength(), MIDIMain.getPreHeight() - 1);
			}
		}
	}
	
	//isNoteVisible(int note) returns true if note would be visible on the screen
	//int note = note being checked
	public static boolean isNoteVisible(int note)
	{
		if(MIDISong.getNotes(MIDIMain.getTrackMenu())[note].getX() + MIDISong.getNotes(MIDIMain.getTrackMenu())[note].getLength() >  MIDIMain.getXCoordinate() && MIDISong.getNotes(MIDIMain.getTrackMenu())[note].getX() < MIDIMain.getXCoordinate() + (screenWidth - sideBarWidth))
		{
			if(MIDISong.getNotes(MIDIMain.getTrackMenu())[note].getY() + MIDIMain.getPreHeight() > MIDIMain.getYCoordinate() && MIDISong.getNotes(MIDIMain.getTrackMenu())[note].getY() < MIDIMain.getYCoordinate() + (screenHeight - fullAddHeight))
				return true;
		}
		return false;
	}
}
