import java.awt.Color;
import java.awt.Graphics2D;

public class VolumeSlider 
{
	private boolean mute = false;
	private boolean button = false;
	private boolean register = false;
	private short width = 150;
	private short height = 30;
	private short x = 0;
	private short y = 0;
	private float value = 1;
	
	public VolumeSlider(short x, short y, boolean add)
	{
		this.x = x;
		this.y = y;
		button = add;
	}
	
	public void setLocation(short x, short y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void setBounds(short x, short y, short w, short h)
	{
		this.x = x;
		this.y = y;
		width = w;
		height = h;
	}
	
	public void drawVolumeSlider(Graphics2D g)
	{
		g.setStroke(GUI.bold);
		g.setColor(Color.BLACK);
		int[] xi = {x, x+width, x+width};
		int[] yi = {y+height/2, y+height, y};
		g.drawPolygon(xi, yi, 3);
		
		g.setStroke(GUI.basic);
		xi[1] = x + (short)(value*width);
		xi[2] = x + (short)(value*width);
		yi[1] = y + 15 + (int)(value*width*((double)height/2)/width) + 1;
		yi[2] = y + 15 - (int)(value*width*((double)height/2)/width);
		g.fillPolygon(xi, yi, 3);
		
		g.setStroke(GUI.bold);
		if(button)
		{
			if(mute)
				g.setColor(Color.RED);
			else
				g.setColor(Color.cyan);
				
			g.drawOval(x - 20, y - 15, 20, 20);
			g.drawLine(x - 18, y - 13, x - 2, y + 3);
		}
		
		g.setColor(Color.GREEN);
		g.drawRoundRect(x+(short)(value*width)-2, y, 4, 30, 2, 8);
		
		g.setStroke(GUI.basic);
	}
	
	public byte getPercent()
	{
		return (byte)(value*100);
	}
	
	public float getDecimal()
	{
		return value;
	}
	
	public boolean setButton(boolean click, boolean pressed)
	{
		if(button)
		{
			if(register == false && click)
			{
				register = true;
				if(pressed)
				{
					if(mute)
						mute = false;
					else
						mute = true;
				}
			}
			
			if(click == false)
				register = false;
		}
		return mute;
	}
	
	public void setValue(short v)
	{
		if(v > width + x)
			value = 1;
		else if(v < x)
			value = 0;
		else
			value = (float)(v-x)/width;
	}
	
	public boolean contains(short mousex, short mousey)
	{
		if(mousex > x - 5 && mousex < x + width + 5)
		{
			if(mousey > y && mousey < y + height)
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean buttonContains(short mousex, short mousey)
	{
		if(mousex > x - 20 && mousex < x)
		{
			if(mousey > y - 15 && mousey < y + 5)
			{
				return true;
			}
		}
		return false;
	}
}
