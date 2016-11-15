import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

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
	public final static short sideBarWidth = 100;							//Side label width
	public final static short screenHeight = 480;							//Screen height
	public final static short screenWidth = 720;							//Screen width
	
	public final static byte windowBarHeight = 54;							//Height of window label bar (It's very annoying to deal with)
	public final static byte mouseDisplacement = 8;							//The horizontal displacement of the mouse (seriously why is this a thing)
	
	public final static BasicStroke basic = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0);
	public final static BasicStroke bold = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0);
	
	//paintComponent(Graphics g) responds to the .repaint() method when used
	//Graphics g = component of the JPanel used to create visual elements
	public void paintComponent(Graphics g) 
	{
		Graphics2D g2D = (Graphics2D) g;	//Graphics2D allows access more methods
		
		g2D.setColor(Color.BLACK);
		g2D.setStroke(basic);
    
		//Welcome Screen
		if(MIDIMain.getMode() == 0)
		{
			drawStartScreen(g2D);
		}
		//Track Editor
		else if(MIDIMain.getMode() == 1)
		{
			drawTrackEditor(g2D);
			g.setColor(Color.BLACK);
		}
		//Note Editor
		else if(MIDIMain.getMode() == 2)
		{
			drawNoteEditor(g2D);
			g.setColor(Color.BLACK);
		}
		
		NotifyAnimation.drawNoteWindow(g2D);
		
		/*
		g2D.setColor(Color.BLACK);
		g2D.fillOval(CursorListener.getLocation()[0] - 10 - mouseDisplacement, CursorListener.getLocation()[1] - fullAddHeight - 10 + 5, 20, 20);
		g2D.setColor(Color.WHITE);
		g2D.fillOval(CursorListener.getLocation()[0] - 2 - mouseDisplacement, CursorListener.getLocation()[1] - fullAddHeight - 2 + 5, 4, 4);
		*/
	}
	
	//drawStartScreen(Graphics2D g) draws the welcome screen (menu = 0)
	public void drawStartScreen(Graphics2D g)
	{
		g.setFont(new Font("FONT", Font.BOLD, 50));
		g.drawString("WELCOME", 230, 240);
	}
	
	//drawTrackEditor(Graphics2D g) draws the track editor (menu = 1)
	public void drawTrackEditor(Graphics2D g)
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
	
	//drawNoteEditor(Graphics2D g) draws the note editor (menu = 2)
	public void drawNoteEditor(Graphics2D g)
	{
		g.setFont(new Font("FONT", Font.ROMAN_BASELINE, 12));
		drawGridField(g, (short) ((screenHeight - fullAddHeight)/MIDIMain.getPreHeight() + 1), (short) ((screenWidth - sideBarWidth)/MIDIMain.getPreLength() + 1));
		drawNotes(g);
		drawGridLabels(g, (short) ((screenHeight - fullAddHeight)/MIDIMain.getPreHeight() + 1), (short) ((screenWidth - sideBarWidth)/MIDIMain.getPreLength() + 1));
	}
	
	//drawGridLabels(Graphics2D g, short height, short width) draws the labels on the sides of the grid
	//short height = height of the grid (in tones)
	//short width = width of the grid (in ticks)
	public void drawGridLabels(Graphics2D g, short height, short width)
	{
		drawSideGridLabel(g, (short) height);
		drawTopGridLabel(g, (short) width);
		
		g.setStroke(bold);
		g.setColor(Color.WHITE);
		g.fillRect(0, toolBarHeight, sideBarWidth, topBarHeight);
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("A", Font.PLAIN, 10));
		g.drawRect(0, toolBarHeight, sideBarWidth, topBarHeight);
		g.drawString("INSERT TITLE", 10, toolBarHeight + 15);
		g.setStroke(basic);
		
	}
	
	//drawTopGridLabel(Graphics2D g, short width) draws the label on the top side of the grid
	//short width = width of the grid (in ticks)
	public void drawTopGridLabel(Graphics2D g, short width)
	{
		//Box
		g.setStroke(bold);
		g.setColor(Color.WHITE);
		g.fillRect(0, toolBarHeight, screenWidth, topBarHeight);
		g.setColor(Color.BLACK);
		g.drawRect(0, toolBarHeight, screenWidth, topBarHeight);
		g.setStroke(basic);

		for(int i = (int) (MIDIMain.getXCoordinate()/MIDIMain.getPreLength()-1); i < (int) (MIDIMain.getXCoordinate()/MIDIMain.getPreLength() + width + 1); i++)
		{
			//If line represents start of measure
			if((i%MIDISong.getMeasureLength() == 0 && MIDIMain.getPreLength() > 2) || (i%(MIDISong.getMeasureLength()*2) == 0 && i/MIDISong.getMeasureLength() < 999) || i%(MIDISong.getMeasureLength()*4) == 0)
			{
				g.setFont(new Font("A", Font.BOLD, 12));
				g.setColor(Color.BLACK);
				g.drawLine((int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), toolBarHeight, (int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), toolBarHeight + topBarHeight/8);
				g.drawLine((int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), toolBarHeight + topBarHeight*7/8, (int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), fullAddHeight);
				g.drawString(i/MIDISong.getMeasureLength()+"", (int)(sideBarWidth - 5 + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), fullAddHeight - 6);
			}
			//*FIX
			else if(i%(MIDISong.getMeasureLength()/4) == 0 && MIDIMain.getPreLength() > 10)
			{
				g.setFont(new Font("A", Font.PLAIN, 10));
				g.setColor(Color.BLACK);
				g.drawLine((int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), toolBarHeight, (int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), toolBarHeight + topBarHeight/8);
				g.drawLine((int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), toolBarHeight + topBarHeight*7/8, (int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), fullAddHeight);
				g.drawString(((i%MIDISong.getMeasureLength())/4+1)+"", (int)(sideBarWidth - 2 + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), fullAddHeight - 6);
			}
			else if(i%(MIDISong.getMeasureLength()/8) == 0 && MIDIMain.getPreLength() > 22)
			{
				g.setFont(new Font("A", Font.PLAIN, 10));
				g.setColor(Color.GRAY);
				g.drawLine((int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), toolBarHeight, (int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), toolBarHeight + topBarHeight/8);
				g.drawLine((int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), toolBarHeight + topBarHeight*7/8, (int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), fullAddHeight);
				g.drawString((i%MIDISong.getMeasureLength())/2+"/"+8, (int)(sideBarWidth - 6 + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), fullAddHeight - 6);
			}
			else if(i%(MIDISong.getMeasureLength()/16) == 0 && MIDIMain.getPreLength() > 40)
			{
				g.setFont(new Font("A", Font.PLAIN, 10));
				g.setColor(Color.GRAY);
				g.drawLine((int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), toolBarHeight, (int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), toolBarHeight + topBarHeight/8);
				g.drawLine((int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), toolBarHeight + topBarHeight*7/8, (int)(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), fullAddHeight);
				g.drawString(i%MIDISong.getMeasureLength()+"/"+16, (int)(sideBarWidth - 10 + MIDIMain.getPreLength()*i - MIDIMain.getXCoordinate()), fullAddHeight - 6);
			}
		}
	}
	
	//drawSideGridLabel(Graphics2D g, short height) draws labels on the side of the grid
	//short height = height of the grid (in tones)
	public void drawSideGridLabel(Graphics2D g, short height)
	{
		//Box
		g.setStroke(bold);
		g.setColor(Color.WHITE);
		g.fillRect(0, fullAddHeight, sideBarWidth, screenHeight - fullAddHeight);
		g.setColor(Color.BLACK);
		g.drawRect(0, fullAddHeight, sideBarWidth, screenHeight - fullAddHeight);
		g.setStroke(basic);

		for(byte i = (byte) (MIDIMain.getYCoordinate()/MIDIMain.getPreHeight()); i < MIDIMain.getYCoordinate()/MIDIMain.getPreHeight() + height + 1; i++)
		{
			//If scale > 15
			if(MIDIMain.getPreHeight() > 15)
			{
				g.drawLine(0, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate(), sideBarWidth, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate());
				g.drawString(Notes.convertToNote((byte)(Notes.MAX_TONE - i), true), sideBarWidth/2 - 10, fullAddHeight + MIDIMain.getPreHeight()/2 + 5 + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate());
			}
			//If scale < 15
			else if(i%2 == 0 && MIDIMain.getPreHeight() > 5)
			{
				g.drawLine(0, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate(), sideBarWidth, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate());
				g.drawString(Notes.convertToNote((byte)(Notes.MAX_TONE - i), true), sideBarWidth/2 - 10, fullAddHeight + MIDIMain.getPreHeight() + 5 + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate());
			}
			//If scale == 5
			else
			{
				//g.drawLine(0, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate(), sideBarWidth, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate());
				if(i%4 == 0)
				{
					g.drawString(Notes.convertToNote((byte)(Notes.MAX_TONE - i), true), sideBarWidth/2 - 10, fullAddHeight + MIDIMain.getPreHeight()*2 + 5 + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate());
					g.drawLine(0, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate(), sideBarWidth, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getYCoordinate());
				}
			}
		}
	}
	
	//drawGridField(Graphics2D g, short height, short width) draws the lines of the grid
	//short height = height of the grid (in tones)
	//short width = width of the grid (in ticks)
	public void drawGridField(Graphics2D g, short height, short width)
	{
		//Vertical Lines
		g.setColor(Color.GRAY);
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
	
	//drawNotes(Graphics2D g) draws the notes in a track
	public void drawNotes(Graphics2D g)
	{
		//Loops through every note and draws it as a rectangle
		for(int i = 0; i < MIDISong.getNotes(MIDIMain.getTrackMenu()).length; i++)
		{
			//If note is on screen
			if(isNoteVisible(i))
			{
				g.setColor(new Color(51,186,164));
				g.fillRoundRect((int)(MIDISong.getNotes(MIDIMain.getTrackMenu())[i].getX() - MIDIMain.getXCoordinate() + sideBarWidth), MIDISong.getNotes(MIDIMain.getTrackMenu())[i].getY() + 1 - MIDIMain.getYCoordinate() + fullAddHeight, MIDISong.getNotes(MIDIMain.getTrackMenu())[i].getLength(), MIDIMain.getPreHeight() - 1, (MIDIMain.getPreLength()*3)/4, (MIDIMain.getPreHeight()*3)/4);
				g.setColor(Color.BLACK);
				g.setStroke(bold);
				g.drawRoundRect((int)(MIDISong.getNotes(MIDIMain.getTrackMenu())[i].getX() - MIDIMain.getXCoordinate() + sideBarWidth), MIDISong.getNotes(MIDIMain.getTrackMenu())[i].getY() + 1 - MIDIMain.getYCoordinate() + fullAddHeight, MIDISong.getNotes(MIDIMain.getTrackMenu())[i].getLength(), MIDIMain.getPreHeight() - 1, (MIDIMain.getPreLength()*3)/4, (MIDIMain.getPreHeight()*3)/4);
			}
			g.setStroke(basic);
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
