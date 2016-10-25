import java.awt.Rectangle;

public class Notes 
{
	public final static byte maxTone = 120;
	private static int numNotes = 0;
	
	private int location = 0;
	private int length = 0;
	private byte tone = 0;
	private byte volume = 0;
	
	public Notes()
	{
		this(5, 0, (byte)(maxTone - numNotes) , (byte)100);
	}
	
	public Notes(int l, int x, byte n, byte v)
	{
		length = l;
		location = x;
		tone = n;
		volume = v;
		numNotes++;
	}
	
	public boolean contains(int x, int y)
	{
		return new Rectangle(getX(), getY(), getLength(), MIDIMain.getPreHeight()).contains(x, y);
	}
	
	public static int identifyContained(int x, int y)
	{
		for(int i = 0; i < numNotes; i++)
		{
			if(MIDIMain.getNote(i).contains(x, y))
			{
				return i;
			}
		}
		return -1;
	}
	
	public void setTone(byte t)
	{
		tone = t;
	}
	
	public void setLength(int l)
	{
		l = l - l%MIDIMain.getPreLength();
		length = l / MIDIMain.getPreLength();
		
		if(length <= 0)
			length = 1;
	}
	
	public void setLocation(int x, int y)
	{
		x = x - x%MIDIMain.getPreLength();
		y = y - y%MIDIMain.getPreHeight();
		location = x / MIDIMain.getPreLength();
		tone = (byte) (maxTone - y / MIDIMain.getPreHeight());
		
		if(location < 0)
			location = 0;
		if(tone < 0)
			tone = 0;
		if(tone > 120)
			tone = 120;
	}
	
	public int getX()
	{
		return location * MIDIMain.getPreLength();
	}
	
	public int getY()
	{
		return (maxTone - tone) * MIDIMain.getPreHeight();
	}
	
	public int getLength()
	{
		return length * MIDIMain.getPreLength();
	}
	
	public byte getVolume()
	{
		return volume;
	}
	
	public static int getNumNotes()
	{
		return numNotes;
	}
	
	public static String convertToNote(byte c, boolean sharp)
	{
		byte octave = (byte) ((c-(c%12))/12);
		byte inc = 0;
		String semi = "#";
		
		if(sharp == false)
		{
			inc = 1;
			semi = "b";
		}

		if(c%12 == 0)
		{
			return "C "+octave;
		}
		else if(c%12 == 1)
		{
			return (char)('C'+inc)+" "+semi;
		}
		else if(c%12 == 2)
		{
			return "D";
		}
		else if(c%12 == 3)
		{
			return (char)('D'+inc)+" "+semi;
		}
		else if(c%12 == 4)
		{
			return "E";
		}
		else if(c%12 == 5)
		{
			return "F";
		}
		else if(c%12 == 6)
		{
			return (char)('F'+inc)+" "+semi;
		}
		else if(c%12 == 7)
		{
			return "G";
		}
		else if(c%12 == 8)
		{
			return (char)('G'+inc)+" "+semi;
		}
		else if(c%12 == 9)
		{
			return "A";
		}
		else if(c%12 == 10)
		{
			return (char)('A'+inc)+" "+semi;
		}
		else if(c%12 == 11)
		{
			return "B";
		}
		else
		{
			return c+"?";
		}
	}
}
