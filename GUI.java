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
		
		drawMenuFunctions(g);
	}
	
	//drawNoteEditor(Graphics g) draws the note editor (menu = 2)
	public void drawNoteEditor(Graphics g)
	{
		for(byte i = 0; i < 23; i++)
		{
			g.drawLine(100, 40+20*i-MIDIMain.getCoordinates()[1]%20, 720, 40+20*i-MIDIMain.getCoordinates()[1]%20);
		}
		g.drawLine(100, 40, 100, 480);
		g.setColor(Color.LIGHT_GRAY);
		for(byte i = 0; i < 32; i++)
		{
			g.drawLine(100+20*i-MIDIMain.getCoordinates()[0]%20, 40, 100+20*i-MIDIMain.getCoordinates()[0]%20, 480);
		}
		g.setColor(Color.BLACK);
		g.fillRect((int)(MIDIMain.rect.getX()) - MIDIMain.getCoordinates()[0], (int)(MIDIMain.rect.getY() + 1) - MIDIMain.getCoordinates()[1], (int)(MIDIMain.rect.getWidth()), (int)(MIDIMain.rect.getHeight() - 1));
		g.drawLine(100, 40, 100, 480);
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 100, 480);
		g.setColor(Color.BLACK);
		for(short i = (short) (MIDIMain.getCoordinates()[1]/20); i < (short) (MIDIMain.getCoordinates()[1]/20 + 23); i++)
		{
			g.drawLine(0, 40+20*i-MIDIMain.getCoordinates()[1], 100, 40+20*i-MIDIMain.getCoordinates()[1]);
			g.drawString(i+"", 20, 55+20*i-MIDIMain.getCoordinates()[1]);
		}
		
		drawMenuFunctions(g);
	}
	
	//drawMenuFunctions(Graphics g) draws the menu containing the buttons at the topo of the screen
	public void drawMenuFunctions(Graphics g)
	{
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, 720, 40);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, 720, 40);
		
		for(byte i = 0; i < 10; i++)
		{
			g.setColor(Color.WHITE);
			g.fillOval(5+40*i, 5, 30, 30);
			g.setColor(Color.BLACK);
			g.drawOval(5+40*i, 5, 30, 30);
		}
	}
}
