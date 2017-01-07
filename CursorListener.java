import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * <b>[Date: October 16, 2016]</b>
 * <p>
 * The CursorListener class directly responds to all mouse related inputs.
 * This includes button presses, mouse movement and scroll wheel. Information
 * of the inputs is stored in this class
 * </p>
 * Note: This class contains required methods that CANNOT be removed
 */
public class CursorListener implements MouseListener, MouseMotionListener, MouseWheelListener
{
	private static byte click = 0;					//Records input of all mouse buttons (0 = left | 1 = middle | 2 = right)
	private static int object = -1;					//Used to determine which object is being moved
	private static short[] coordinates = {0, 0};	//Records location of mouse on screen; [0] = x, [1] = y
	private static short[] origin = {0, 0};			//The location of the mouse before dragging
	private static byte wheelScroll = 0;			//The value assigned to the mouse scroll wheel
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void mouseClicked(MouseEvent e)}</pre></p> 
	 * Responds to any digital input on the mouse.</p> 
	 * @param e = information of the mouse event
	 */
	public void mouseClicked(MouseEvent e){
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void mouseEntered(MouseEvent e)}</pre></p> 
	 * Responds to the cursor entering the window.</p> 
	 * @param e = information of the mouse event
	 */
	public void mouseEntered(MouseEvent e){
	}

	/**
	 * <blockquote>
	 * <p><pre>{@code public void mouseExited(MouseEvent e)}</pre></p> 
	 * Responds to the cursor exiting the window.</p> 
	 * @param e = information of the mouse event
	 */
	public void mouseExited(MouseEvent e){
	}

	/**
	 * <blockquote>
	 * <p><pre>{@code public void mousePressed(MouseEvent e)}</pre></p> 
	 * Responds to any digital input on the mouse being held.</p> 
	 * @param e = information of the mouse event
	 */
	public void mousePressed(MouseEvent e) 
	{
		object = -1;
		coordinates[0] = (short) e.getX();
		coordinates[1] = (short) e.getY();
		//Left-Click
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			click = 1;
			object = Notes.identifyContained(e.getX() + MIDIMain.getXCoordinate() - GUI.sideBarWidth - GUI.mouseDisplacement, (short)(e.getY() + MIDIMain.getYCoordinate() - GUI.fullAddHeight - GUI.windowBarHeight));
			//If touching an object (note)
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
			//If touching an object (note)
			if(object < 0)
			{
				origin[0] = (short) coordinates[0];
				origin[1] = (short) coordinates[1];
			}
		}
	}

	/**
	 * <blockquote>
	 * <p><pre>{@code public void mouseReleased(MouseEvent e)}</pre></p> 
	 * Responds to any inputs on the mouse being released.</p> 
	 * @param e = information of the mouse event
	 */
	public void mouseReleased(MouseEvent e) 
	{
		//If any button has been released
		if(e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3)
		{
			click = 0;
			object = -1;
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void mouseDragged(MouseEvent e)}</pre></p> 
	 * Responds to any movement of the mouse while a button is held.</p> 
	 * @param e = information of the mouse event
	 */
	public void mouseDragged(MouseEvent e) 
	{
		coordinates[0] = (short) (e.getX());
		coordinates[1] = (short) (e.getY());
	}

	/**
	 * <blockquote>
	 * <p><pre>{@code public void mouseMoved(MouseEvent e)}</pre></p> 
	 * Responds to any movement of the mouse (NOTE: while a button is not held).</p> 
	 * @param e = information of the mouse event
	 */
	public void mouseMoved(MouseEvent e) 
	{
		coordinates[0] = (short) (e.getX());
		coordinates[1] = (short) (e.getY());
	}	
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void mouseWheelMoved(MouseWheelEvent e)}</pre></p> 
	 * Responds to any movement of the mouse scroll wheel.</p> 
	 * @param e = information of the mouse scroll event
	 */
	public void mouseWheelMoved(MouseWheelEvent e) 
	{
		wheelScroll = (byte) e.getWheelRotation();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static short[] getLocation()}</pre></p> 
	 * Returns the location of the mouse cursor.</p> 
	 * @return The location of the mouse cursor as (x, y)
	 */
	public static short[] getLocation()
	{
		return coordinates;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static short[] getLocationDif()}</pre></p> 
	 * Returns the location difference of the mouse cursor versus the origin point.</p> 
	 * @return The location difference of the mouse cursor as (x, y)
	 */
	public static short[] getLocationDif()
	{
		short[] a = {(short)(origin[0] - coordinates[0]), (short)(origin[1] - coordinates[1])};
		return a;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static short[] getOrigin()}</pre></p> 
	 * Returns the origin location of the mouse cursor before being pressed.</p> 
	 * @return The origin location stored
	 */
	public static short[] getOrigin()
	{
		return origin;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static int getObjectNumber()}</pre></p> 
	 * Returns the value of the object being held.</p> 
	 * @return The value of the object being held
	 */
	public static int getObjectNumber()
	{
		return object;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static byte getClick()}</pre></p> 
	 * Returns the status of the mouse button presses.</p> 
	 * @return The value assigned to a pressed button
	 */
	public static byte getClick()
	{
		return click;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static byte getMouseWheel()}</pre></p> 
	 * Returns the value of the mouse wheel.</p> 
	 * @return The value of the mouse wheel
	 */
	public static byte getMouseWheel()
	{
		return wheelScroll;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void setObjectNumber(int obj)}</pre></p> 
	 * Sets the object number.</p> 
	 * @param obj = object number
	 */
	public static void setObjectNumber(int obj)
	{
		object = obj;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void setMouseWheel(byte value)}</pre></p> 
	 * Sets the value of the mouse wheel.</p> 
	 * @param value = new value for the mouse wheel
	 */
	public static void setMouseWheel(byte value)
	{
		wheelScroll = value;
	}
}
