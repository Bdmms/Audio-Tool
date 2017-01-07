import java.awt.Color;
import java.awt.Graphics2D;

/**
 * <b>[Date: December 12, 2016]</b>
 * <p>
 * This class is used to produce an object that can be interacted
 * with by the mouse. The mouse can slide a bar that will change 
 * the volume of a particular element.
 * </p>
 */
public class VolumeSlider 
{
	private boolean mute = false;		//Determines if the volume slider is muted
	private boolean button = false;		//Whether the volume slider should have a mute button
	private boolean register = false;	//Determines if the slider has already been interacted with by the mouse
	private short width = 150;			//How wide the volume slider should be
	private short height = 30;			//How tall the volume slider should be
	private short x = 0;				//The x - location of the volume slider
	private short y = 0;				//The y - location of the volume slider
	private float value = 1;			//The value assigned to the slider (location of the bar on the slider)
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public VolumeSlider(short x, short y, boolean add)}</pre></p> 
	 * The constructor method. Variables are initialized.</p>
	 * @param x = The x-location of the volume slider 
	 * @param y = The y-location of the volume slider
	 * @param add = Whether a mute button should be attached to the slider
	 */
	public VolumeSlider(short x, short y, boolean add)
	{
		this.x = x;
		this.y = y;
		button = add;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public boolean contains(short mousex, short mousey)}</pre></p> 
	 * Determines if the coordinates are inside of the volume slider.</p>
	 * @param mousex = x-location of the mouse in the window
	 * @param mousey = y-location of the mouse in the window
	 * @return Whether the coordinates are inside the volume slider
	 */
	public boolean contains(short mousex, short mousey)
	{
		//If mousex is inside the volume slider along the x-axis (extra space is given)
		if(mousex > x - 5 && mousex < x + width + 5)
		{
			//If mousey is inside the volume slider along the y-axis
			if(mousey > y && mousey < y + height)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public boolean buttonContains(short mousex, short mousey)}</pre></p> 
	 * Determines if the coordinates are inside of the mute button.</p>
	 * @param mousex = x-location of the mouse in the window
	 * @param mousey = y-location of the mouse in the window
	 * @return Whether the coordinates are inside the mute button
	 */
	public boolean buttonContains(short mousex, short mousey)
	{
		//If mousex is inside the mute button along the x-axis (mute buttons location is relative to the volume slider)
		if(mousex > x - 20 && mousex < x)
		{
			//If mousey is inside the mute button along the y-axis (mute buttons location is relative to the volume slider)
			if(mousey > y - 15 && mousey < y + 5)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void drawVolumeSlider(Graphics g)}</pre></p> 
	 * Draws the volume slider where at its stored x and y coordinates.</p> 
	 * @param g = component of the JPanel used to create visual elements
	 */
	public void drawVolumeSlider(Graphics2D g)
	{
		//Draws outline of the slider
		int[] xi = {x, x+width, x+width};
		int[] yi = {y+height/2, y+height, y};
		g.setStroke(GUI.bold);
		g.setColor(GUI.colours[GUI.getColourScheme()][GUI.COLOUR_TEXT]);
		g.drawPolygon(xi, yi, 3);
		
		//Fills in the area up to the bar
		g.setStroke(GUI.basic);
		xi[1] = x + (short)(value*width);
		xi[2] = x + (short)(value*width);
		yi[1] = y + 15 + (int)(value*width*((double)height/2)/width) + 1;
		yi[2] = y + 15 - (int)(value*width*((double)height/2)/width);
		g.fillPolygon(xi, yi, 3);
		
		g.setStroke(GUI.superBold);
		//If there should be a mute button
		if(button)
		{
			//If the volume slider is currently muted
			if(mute)
				g.setColor(Color.RED);
			else
				g.setColor(GUI.colours[GUI.getColourScheme()][3]);
				
			//Draw mute button
			g.drawOval(x - 20, y - 15, 20, 20);
			g.drawLine(x - 18, y - 13, x - 2, y + 3);
		}
		
		//Draw the bar
		g.setColor(GUI.colours[GUI.getColourScheme()][3]);
		g.drawRoundRect(x+(short)(value*width)-2, y, 4, 30, 2, 8);
		
		g.setStroke(GUI.basic);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setLocation(short x, short y)}</pre></p> 
	 * Sets the location of the volume slider.</p>
	 * @param x = The x-location of the volume slider 
	 * @param y = The y-location of the volume slider
	 */
	public void setLocation(short x, short y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setLocation(short x, short y)}</pre></p> 
	 * Sets the size and location of the volume slider.</p>
	 * @param x = The x-location of the volume slider 
	 * @param y = The y-location of the volume slider
	 * @param w = The width of the volume slider 
	 * @param h = The height of the volume slider
	 */
	public void setBounds(short x, short y, short w, short h)
	{
		this.x = x;
		this.y = y;
		width = w;
		height = h;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public boolean setButton(boolean click, boolean pressed)}</pre></p> 
	 * Sets the state of the mute button on the volume slider (Method makes sure that it only registers a mouse click one time).</p>
	 * @param click = Whether the mouse has clicked (anywhere on screen)
	 * @param pressed = If button has been clicked specifically
	 * @return Whether the button had been muted or not
	 */
	public boolean setButton(boolean click, boolean pressed)
	{
		//If volume slider has a mute button
		if(button)
		{
			//If button hasn't registered mouse click
			if(register == false && click)
			{
				register = true;
				//If button has been pressed on
				if(pressed)
				{
					
					//If volume slider is muted
					if(mute)
						mute = false;
					else
						mute = true;
				}
			}
			
			//Register resets when mouse click is released
			if(click == false)
				register = false;
		}
		return mute;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setVolume(byte v)}</pre></p> 
	 * Sets the bar location based off of the volume it has been given.</p>
	 * @param v = volume of the object the volume slider is dependent on
	 */
	public void setVolume(byte v)
	{
		value = (float)v/128;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setVolume(byte v)}</pre></p> 
	 * Sets the value of the volume slider (The value is a x-coordinate on screen).</p>
	 * @param v = value to set the bar at (location of the bar on screen) based on location on screen
	 */
	public void setValue(short v)
	{
		//If value given exceeds the bounds of the volume slider
		if(v > width + x)
			value = 1;
		else if(v < x)
			value = 0;
		else
			value = (float)(v-x)/width;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public byte getPercent()}</pre></p> 
	 * Returns the current value of the volume slider as a percent.</p>
	 * @return The percent value of the bar's location
	 */
	public byte getPercent()
	{
		return (byte) Math.round(value*100);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public float getDecimal()}</pre></p> 
	 * Returns the current value of the volume slider as a decimal.</p>
	 * @return The decimal value of the bar's location
	 */
	public float getDecimal()
	{
		return value;
	}
}
