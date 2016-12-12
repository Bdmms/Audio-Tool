import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class CursorListener implements MouseListener, MouseMotionListener, MouseWheelListener
{
	/**
	 * Date: October 16, 2016
	 * 
	 * The CursorListener class directly responds to all mouse related inputs.
	 * This includes button presses, mouse movement and scroll wheel. Information
	 * of the inputs is stored in this class
	 * 
	 * Note 1: This class contains required methods that CANNOT be removed
	 */

	private static byte click = 0;					//Records input of all mouse buttons (0 = left | 1 = middle | 2 = right)
	private static int object = -1;					//Used to determine which object is being moved
	private static short[] coordinates = {0, 0};	//Records location of mouse on screen; [0] = x, [1] = y
	private static short[] origin = {0, 0};			//The location of the mouse before dragging
	private static byte wheelScroll = 0;			//The value assigned to the mouse scroll wheel
	
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
		object = -1;
		coordinates[0] = (short) e.getX();
		coordinates[1] = (short) e.getY();
		//Left-Click
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			click = 1;
			object = Notes.identifyContained(e.getX() + MIDIMain.getXCoordinate() - GUI.sideBarWidth - GUI.mouseDisplacement, (short)(e.getY() + MIDIMain.getYCoordinate() - GUI.fullAddHeight - GUI.windowBarHeight));
			if(object >= 0)
			{
				origin[0] = (short) ((coordinates[0] - GUI.mouseDisplacement - GUI.sideBarWidth) - MIDISong.getNotes(MIDIMain.getTrackMenu(), object).getX());
			}
			else
			{
				origin[0] = (short) (coordinates[0] + MIDIMain.getXCoordinate());
				origin[1] = (short) (coordinates[1] + MIDIMain.getYCoordinate());
			}
		}
		//Middle-Click
		else if(e.getButton() == MouseEvent.BUTTON2)
		{
			click = 2;
			origin[0] = (short) (coordinates[0] + MIDIMain.getPreLength());
			origin[1] = (short) (coordinates[1] + MIDIMain.getPreHeight());
		}
		//Right-Click
		else if(e.getButton() == MouseEvent.BUTTON3)
		{
			click = 3;
			object = Notes.identifyContained(e.getX() + MIDIMain.getXCoordinate() - GUI.sideBarWidth - GUI.mouseDisplacement, (short)(e.getY()+ MIDIMain.getYCoordinate() - GUI.fullAddHeight - GUI.windowBarHeight));
			if(object < 0)
			{
				origin[0] = (short) coordinates[0];
				origin[1] = (short) coordinates[1];
			}
		}
	}

	//mouseReleased(MouseEvent e) responds to any inputs on the mouse being released
	//MouseEvent e = information of the mouse event
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3)
		{
			click = 0;
			object = -1;
		}
	}
	
	//mouseDragged(MouseEvent e) responds to any movement of the mouse while a button is held
	//MouseEvent e = information of the mouse event
	public void mouseDragged(MouseEvent e) {
		coordinates[0] = (short) (e.getX());
		coordinates[1] = (short) (e.getY());
	}

	//mouseMoved(MouseEvent e) responds to any movement of the mouse (NOTE: while a button is not held)
	//MouseEvent e = information of the mouse event
	public void mouseMoved(MouseEvent e) {
		coordinates[0] = (short) (e.getX());
		coordinates[1] = (short) (e.getY());
	}	
	
	//mouseWheelMoved(MouseEvent e) responds to any movement of the mouse scroll wheel
	//MouseWheelEvent e = information of the mouse scroll event
	public void mouseWheelMoved(MouseWheelEvent e) {
		wheelScroll = (byte) e.getWheelRotation();
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
	
	//Returns the origin location stored
	public static short[] getOrigin()
	{
		return origin;
	}
	
	//getObjectNumber() returns the value of the object being held
	public static int getObjectNumber()
	{
		return object;
	}
	
	//getLeftClick() returns the status of the mouse button presses
	public static byte getClick()
	{
		return click;
	}
	
	//getMouseWheel() returns the value of the mouse wheel
	public static byte getMouseWheel()
	{
		return wheelScroll;
	}
	
	//getObjectNumber() sets the object number
	//int obj = object number
	public static void setObjectNumber(int obj)
	{
		object = obj;
	}
	
	//setMouseWheel() sets the value of the mouse wheel
	public static void setMouseWheel(byte value)
	{
		wheelScroll = value;
	}
}
