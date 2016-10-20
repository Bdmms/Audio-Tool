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
	//Records input of the middle button on the mouse
	private static boolean middleClick = false;
	//Records input of the right click button on the mouse
	private static boolean rightClick = false;
	//Used to determine which object is being moved
	private static int object = 0;
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
		object = 0;
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			leftClick = true;
			object = Notes.identifyContained(e.getX() + MIDIMain.getCoordinates()[0] - GUI.sideBarWidth, e.getY() - 50 + MIDIMain.getCoordinates()[1] - GUI.fullAddHeight);
			if(object >= 0)
			{
				coordinates[0] = (short) (e.getX() - MIDIMain.getNote(object).getLength()/2 - GUI.sideBarWidth);
				coordinates[1] = (short) (e.getY() - 50 - MIDIMain.getPreHeight()/2 - GUI.fullAddHeight);
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
			object = Notes.identifyContained(e.getX() + MIDIMain.getCoordinates()[0] - GUI.sideBarWidth, e.getY() - 50 + MIDIMain.getCoordinates()[1] - GUI.fullAddHeight);
			if(object >= 0)
			{
				coordinates[0] = (short) (e.getX() - MIDIMain.getNote(object).getX() - GUI.sideBarWidth);
			}
		}
		else if(e.getButton() == MouseEvent.BUTTON2)
		{
			middleClick = true;
			origin[0] = (short) (e.getX() + MIDIMain.getPreLength());
			origin[1] = (short) (e.getY() + MIDIMain.getPreHeight());
			coordinates[0] = (short) (e.getX());
			coordinates[1] = (short) (e.getY());
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
		else if(e.getButton() == MouseEvent.BUTTON2)
		{
			middleClick = false;
		}
	}
	
	//mouseDragged(MouseEvent e) responds to any movement of the mouse while a button is held
	//MouseEvent e = information of the mouse event
	public void mouseDragged(MouseEvent e) {
		if(leftClick == true)
		{
			if(object >= 0)
			{
				coordinates[0] = (short) (e.getX() - MIDIMain.getNote(object).getLength()/2 - GUI.sideBarWidth);
				coordinates[1] = (short) (e.getY() - 50 - MIDIMain.getPreHeight()/2 - GUI.fullAddHeight);
			}
			else
			{
				coordinates[0] = (short) (e.getX());
				coordinates[1] = (short) (e.getY());
			}
		}
		else if(rightClick == true)
		{
			if(object >= 0)
			{
				coordinates[0] = (short) (e.getX() - MIDIMain.getNote(object).getX() - GUI.sideBarWidth);
			}
		}
		else if(middleClick == true)
		{
			coordinates[0] = (short) (e.getX());
			coordinates[1] = (short) (e.getY());
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
	public int getObjectNumber()
	{
		return object;
	}
	
	//getLeftClick() returns the status of the left click input
	public boolean getLeftClick()
	{
		return leftClick;
	}
	
	//getRightClick() returns the status of the middle button input
	public boolean getMiddleClick()
	{
		return middleClick;
	}
	
	//getRightClick() returns the status of the right click input
	public boolean getRightClick()
	{
		return rightClick;
	}
}
