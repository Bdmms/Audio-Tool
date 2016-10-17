import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class CursorListener implements MouseListener, MouseMotionListener
{
	/**
	 * Note 1: This class contains required methods that CANNOT be removed
	 */
	
	//Records input of the left click button on the mouse
	private static boolean leftClick = false;
	//Records input of the right click button on the mouse
	private static boolean rightClick = false;
	//*Placeholder, used to determine which object is being moved
	private static byte object = 0;
	//Records location of mouse on screen; [0] = x, [1] = y
	private static short[] coordinates = {0, 0};
	
	private static short[] origin = {0, 0};
	
	//mouseClicked(MouseEvent e) responds to any digital input on the mouse
	//MouseEvent e = information of the mouse event
	public void mouseClicked(MouseEvent e) {
		
	}
	
	//mouseEntered(MouseEvent e) responds to the cursor entering the window
	//MouseEvent e = information of the mouse event
	public void mouseEntered(MouseEvent e) {
		
	}

	//mouseExited(MouseEvent e) responds to the cursor exiting the window
	//MouseEvent e = information of the mouse event
	public void mouseExited(MouseEvent e) {
		
	}

	//mousePressed(MouseEvent e) responds to any digital input on the mouse being held
	//MouseEvent e = information of the mouse event
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			leftClick = true;
			if(MIDIMain.rect.contains(e.getX() + MIDIMain.getCoordinates()[0], e.getY() - 50 + MIDIMain.getCoordinates()[1]))
			{
				object = 1;
				coordinates[0] = (short) (e.getX() - MIDIMain.rect.getSize().getWidth()/2);
				coordinates[1] = (short) (e.getY() - 50 - MIDIMain.rect.getSize().getHeight()/2);
			}
			else
			{
				coordinates[0] = (short) e.getX();
				coordinates[1] = (short) e.getY();
				origin[0] = (short) (coordinates[0] + MIDIMain.getCoordinates()[0]);
				origin[1] = (short) (coordinates[1] + MIDIMain.getCoordinates()[1]);
			}
		}
		else if(e.getButton() == MouseEvent.BUTTON3)
		{
			rightClick = true;
			if(MIDIMain.rect.contains(e.getX() + MIDIMain.getCoordinates()[0], e.getY() - 50 + MIDIMain.getCoordinates()[1]))
			{
				object = 1;
				coordinates[0] = (short) (e.getX() - MIDIMain.rect.getX());
			}
		}
		else
		{
			object = 0;
		}
	}

	//mousePressed(MouseEvent e) responds to any inputs on the mouse being released
	//MouseEvent e = information of the mouse event
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			leftClick = false;
			object = 0;
		}
		else if(e.getButton() == MouseEvent.BUTTON3)
		{
			rightClick = false;
			object = 0;
		}
	}
	
	//mouseDragged(MouseEvent e) responds to any movement of the mouse while a button is held
	//MouseEvent e = information of the mouse event
	public void mouseDragged(MouseEvent e) {
		if(leftClick == true)
		{
			if(object == 1)
			{
				coordinates[0] = (short) (e.getX() - MIDIMain.rect.getSize().getWidth()/2);
				coordinates[1] = (short) (e.getY() - 40 - MIDIMain.rect.getSize().getHeight()/2);
			}
			else
			{
				coordinates[0] = (short) (e.getX());
				coordinates[1] = (short) (e.getY());
			}
		}
		else if(rightClick == true)
		{
			coordinates[0] = (short) (e.getX() - MIDIMain.rect.getX());
		}
	}

	//mouseMoved(MouseEvent e) responds to any movement of the mouse (NOTE: while a button is not held)
	//MouseEvent e = information of the mouse event
	public void mouseMoved(MouseEvent e) {
		
	}	
	
	//getLocation() returns the location of the mouse cursor as (x, y)
	public static short[] getLocation()
	{
		return coordinates;
	}
	
	//getLocation() returns the location of the mouse cursor as (x, y)
	public static short[] getLocationDif()
	{
		short[] a = {(short)(origin[0] - coordinates[0]), (short)(origin[1] - coordinates[1])};
		return a;
	}
	
	//*Placeholder
	//getObjectNumber() returns the value of the object being held
	public byte getObjectNumber()
	{
		return object;
	}
	
	//getLeftClick() returns the status of the left click input
	public boolean getLeftClick()
	{
		return leftClick;
	}
	
	//setLeftClick() sets the status of the left click input
	//boolean state = new status of left click
	public static void setLeftClick(boolean state)
	{
		leftClick = state;
	}
	
	//getRightClick() returns the status of the right click input
	public boolean getRightClick()
	{
		return rightClick;
	}
}
