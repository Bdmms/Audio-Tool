import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

public class GUI extends JPanel
{
	/**
	 * Note 1: This class contains required methods that CANNOT be removed
	 * 
	 * Note 2: Most methods in this class contain the parameter Graphics g, 
	 * therefore the explanation will not be repeated
	 */
	
	//GUI Size = 720 x 480
	private static final long serialVersionUID = 1L;
	public final static short toolBarHeight = 40;
	public final static short topBarHeight = 20;
	public final static short fullAddHeight = toolBarHeight + topBarHeight ;
	public final static short sideBarWidth = 100;
	public final static short screenHeight = 480;
	public final static short screenWidth = 720;
	
	//paintComponent(Graphics g) responds to the .repaint() method when used
	//Graphics g = component of the JPanel used to create visual elements
	public void paintComponent(Graphics g) 
	{
		g.setColor(Color.BLACK);
		//g.drawRect(0, 0, 719, 479);
    
		if(MIDIMain.getMode() == 0)
		{
			drawStartScreen(g);
		}
		else if(MIDIMain.getMode() == 1)
		{
			drawTrackEditor(g);
			g.setColor(Color.BLACK);
			g.drawString("Scroll: "+MIDIMain.getScrollValue(), 500, 35);
		}
		else if(MIDIMain.getMode() == 2)
		{
			drawNoteEditor(g);
			g.setColor(Color.BLACK);
			g.drawString("X: "+MIDIMain.getCoordinates()[0], 500, 20);
			g.drawString("Y: "+MIDIMain.getCoordinates()[1], 500, 35);
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
		g.setColor(Color.LIGHT_GRAY);
		for(byte i = 0; i < 9; i++)
		{
			g.fillRoundRect(50, 50+75*i-MIDIMain.getScrollValue(), 620, 70, 50, 50);
		}
		
		g.setColor(Color.WHITE);
		g.fillRect(20, 430, 500, 50);
		g.setColor(Color.BLACK);
		g.drawRect(20, 430, 500, 50);
	}
	
	//drawNoteEditor(Graphics g) draws the note editor (menu = 2)
	public void drawNoteEditor(Graphics g)
	{
		g.setFont(new Font("FONT", Font.ROMAN_BASELINE, 12));
		drawGridField(g, (short) ((screenHeight - fullAddHeight)/MIDIMain.getPreHeight() + 1), (short) ((screenWidth - sideBarWidth)/MIDIMain.getPreLength() + 1));
		drawNotes(g);
		drawGridLabels(g, (short) ((screenHeight - fullAddHeight)/MIDIMain.getPreHeight() + 1), (short) ((screenWidth - sideBarWidth)/MIDIMain.getPreLength() + 1));
	}
	
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
	
	public void drawTopGridLabel(Graphics g, short width)
	{
		g.setColor(Color.WHITE);
		g.fillRect(0, toolBarHeight, screenWidth, topBarHeight);
		
		g.setColor(Color.BLACK);
		g.drawRect(0, toolBarHeight, screenWidth, topBarHeight);
		for(short i = (short) (MIDIMain.getCoordinates()[0]/MIDIMain.getPreLength()); i < (short) (MIDIMain.getCoordinates()[0]/MIDIMain.getPreLength() + width); i++)
		{
			if(MIDIMain.getPreLength() > 20)
			{
				g.drawLine(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getCoordinates()[0], 0, sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getCoordinates()[0], fullAddHeight);
				g.drawString(i+"", sideBarWidth + MIDIMain.getPreLength()/2 - 5 + MIDIMain.getPreLength()*i - MIDIMain.getCoordinates()[0], fullAddHeight - 5);
			}
			else if(MIDIMain.getPreLength() > 15 && i%2 == 0)
			{
				g.drawLine(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getCoordinates()[0], 0, sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getCoordinates()[0], fullAddHeight);
				g.drawString(i+"", sideBarWidth + MIDIMain.getPreLength() - 5 + MIDIMain.getPreLength()*i - MIDIMain.getCoordinates()[0], fullAddHeight - 5);
			}
			else if(i%4 == 0)
			{
				g.drawLine(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getCoordinates()[0], 0, sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getCoordinates()[0], fullAddHeight);
				g.drawString(i+"", sideBarWidth + MIDIMain.getPreLength()*2 - 5 + MIDIMain.getPreLength()*i - MIDIMain.getCoordinates()[0], fullAddHeight - 5);
			}
		}
	}
	
	public void drawSideGridLabel(Graphics g, short height)
	{
		g.setColor(Color.WHITE);
		g.fillRect(0, fullAddHeight, sideBarWidth, screenHeight - fullAddHeight);
		
		g.setColor(Color.BLACK);
		g.drawRect(0, fullAddHeight, sideBarWidth, screenHeight - fullAddHeight);
		for(byte i = (byte) (MIDIMain.getCoordinates()[1]/MIDIMain.getPreHeight()); i < MIDIMain.getCoordinates()[1]/MIDIMain.getPreHeight() + height; i++)
		{
			if(MIDIMain.getPreHeight() > 15)
			{
				g.drawLine(0, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getCoordinates()[1], sideBarWidth, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getCoordinates()[1]);
				g.drawString(Notes.convertToNote((byte)(Notes.maxTone - i), true), sideBarWidth/2 - 10, fullAddHeight + MIDIMain.getPreHeight()/2 + 5 + MIDIMain.getPreHeight()*i - MIDIMain.getCoordinates()[1]);
			}
			else if(i%2 == 0)
			{
				g.drawLine(0, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getCoordinates()[1], sideBarWidth, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getCoordinates()[1]);
				g.drawString(Notes.convertToNote((byte)(Notes.maxTone - i), true), sideBarWidth/2 - 10, fullAddHeight + MIDIMain.getPreHeight() + 5 + MIDIMain.getPreHeight()*i - MIDIMain.getCoordinates()[1]);
			}
		}
	}
	
	public void drawGridField(Graphics g, short height, short width)
	{
		//Vertical Lines
		g.setColor(Color.BLUE);
		for(byte i = 0; i < width; i++)
		{
			if(MIDIMain.getPreLength() > 20 || (MIDIMain.getPreLength() > 15 && i%2 == 0) || i%4 == 0)
				g.drawLine(sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getCoordinates()[0]%MIDIMain.getPreLength(), fullAddHeight, sideBarWidth + MIDIMain.getPreLength()*i - MIDIMain.getCoordinates()[0]%MIDIMain.getPreLength(), screenHeight);
		}
		
		//Horizontal Lines
		g.setColor(Color.LIGHT_GRAY);
		for(byte i = 0; i < height; i++)
		{
			if(MIDIMain.getPreHeight() > 15 || i%2 == 0)
				g.drawLine(sideBarWidth, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getCoordinates()[1]%MIDIMain.getPreHeight(), screenWidth, fullAddHeight + MIDIMain.getPreHeight()*i - MIDIMain.getCoordinates()[1]%MIDIMain.getPreHeight());
		}
	}
	
	public void drawNotes(Graphics g)
	{
		//Loops through every note and draws it as a rectangle
		g.setColor(Color.BLACK);
		for(int i = 0; i < Notes.getNumNotes(); i++)
		{
			g.fillRect(MIDIMain.getNote(i).getX() - MIDIMain.getCoordinates()[0] + sideBarWidth, (int)MIDIMain.getNote(i).getY() + 1 - MIDIMain.getCoordinates()[1] + fullAddHeight, MIDIMain.getNote(i).getLength(), (int)(MIDIMain.getPreHeight() - 1));
		}
	}
}
