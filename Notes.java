import java.awt.Rectangle;

/**
 * Date: October 26, 2016
 * 
 * This class contains all of the data effecting the notes
 * inside the song. Notes are only loaded when entering the
 * note editor of a track. Only one track's notes are loaded
 * at a time.
 */

public class Notes
{
	public final static byte MAX_TONE = 120;		//The maximum note value
	public final static byte DATA_STATUS = 0;		//value assigned to the message's status
	public final static byte DATA_TONE = 1;			//value assigned to the tone of the note
	public final static byte DATA_VELOCITY = 2;		//value assigned to the volume of the note
	
	private static int numNotes = 0;				//Number of notes processed
	private static byte track = 0;					//Track being accessed
	private static boolean allUnselected = true;	//true if all notes are not selected
	
	private int begin = 0;				//start of note in sequence
	private int end = 0;				//end of note in sequence
	private byte tone = 60;				//tone of the note
	private byte volume = 0;			//volume of the note
	private boolean selected = false;	//whether the note is selected
	
	//Constructor method
	//int s = start of note (in array)
	//int e = end of note (in array)
	public Notes(int s, int e)
	{
		begin = s;
		end = e;
		tone = MIDISong.getMessage(MIDIMain.getTrackMenu(), s).getMessage()[DATA_TONE];
		volume =  MIDISong.getMessage(MIDIMain.getTrackMenu(), s).getMessage()[DATA_VELOCITY];
		numNotes++;
	}
	
	//contains(long x, short y) checks whether the sent coordinates are inside the note's rectangle and returns true or false
	//long x = x coordinate of mouse
	//short y = y coordinate of mouse
	public boolean contains(long x, short y)
	{
		//If x coordinate is between the x values of the note
		if(x >= getX() && x <= getX() + getLength())
		{
			//If y coordinate is between the y values of the note
			if(y >= getY() && y <= getY() + MIDIMain.getPreHeight())
			{
				return true;
			}
		}
		return false;
	}
	
	//identifyContained(int x, int y) checks every existing note to determine which note is being clicked on, the object number of the note is returned
	//int x = x coordinate of mouse
	//int y = y coordinate of mouse
	public static int identifyContained(long x, short y)
	{
		//Notes are not sorted
		for(int i = 0; i < numNotes; i++)
		{
			//If note is on screen
			if(GUI.isNoteVisible(i))
			{
				//If note contains coordinates
				if(MIDISong.getNotes(track)[i].contains(x, y))
				{
					return i;
				}
			}
		}
		return -1;
	}
	
	//selectContained(Rectangle rect) searches all notes that are contained in the selection box and selects them
	//Rectangle select = selection box that selects all notes inside of it
	public static void selectContained(Rectangle select)
	{
		for(int i = 0; i < numNotes; i++)
		{
			//If notes are visible on screen
			if(GUI.isNoteVisible(i))
			{
				//If any part of the note is inside the box
				if(select.getX() < MIDISong.getNotes(track)[i].getX() + MIDISong.getNotes(track)[i].getLength() - MIDIMain.getXCoordinate() + GUI.sideBarWidth && select.getX() + select.getWidth() > MIDISong.getNotes(track)[i].getX() - MIDIMain.getXCoordinate() + GUI.sideBarWidth &&
						select.getY() < MIDISong.getNotes(track)[i].getY() + MIDIMain.getPreHeight() - MIDIMain.getYCoordinate() + GUI.fullAddHeight && select.getY() + select.getHeight() > MIDISong.getNotes(track)[i].getY() - MIDIMain.getYCoordinate() + GUI.fullAddHeight)
				{
					MIDISong.getNotes(track)[i].selection(true);
				}
			}
		}
	}
	
	//unSelectAll() changes the selection of every note in the track to false
	public static void unSelectAll()
	{
		//If notes are not already unselected
		if(allUnselected == false)
		{
			for(int i = 0; i < numNotes; i++)
			{
				MIDISong.getNotes(track)[i].selection(false);
			}
			allUnselected = true;
		}
	}
	
	//selection(boolean state) sets the selected value of the note
	//boolean state = state of note selection
	public void selection(boolean state)
	{
		//If a note is selected
		if(state == true)
			allUnselected = false;
		
		selected = state;
	}
	
	//setTone(byte t) sets the tone value of the note
	//byte t = new tone
	public void setTone(byte t)
	{
		tone = t;
	}
	
	//setEnd(long x) sets the end location of the note
	//long x = location of note's end
	public void setEnd(long x)
	{
		x = (x - x%MIDIMain.getPreLength()) / MIDIMain.getPreLength();
			
		if(x <= 0 || x <= getTick())
			x = getTick() + 1;
		
		MIDISong.getEvent(track, end).setTick(x);
	}
	
	//setLocation(byte t) sets the location of the note
	//long x = x location of note
	//short y = tone / y location of note
	public void setLocation(long x, short y)
	{
		x = (x - x%MIDIMain.getPreLength()) / MIDIMain.getPreLength();
		y = (short) (MAX_TONE - ((y - y%MIDIMain.getPreHeight()) / MIDIMain.getPreHeight()));
		if(x < 0)
			x = 0;
		if(y < 0)
			y = 0;
		if(y > 120)
			y = 120;
		MIDISong.getEvent(track, end).setTick(x + getLength()/MIDIMain.getPreLength());
		MIDISong.getEvent(track, begin).setTick(x);
		setTone((byte) y);
	}
	
	//setTrack(byte trackNum) sets the track that notes come from
	//byte trackNum = track in sequence
	public static void setTrack(byte trackNum)
	{
		track = trackNum;
	}
	
	//getBeginning()() returns the point of the star message in the array of messages
	public int getBeginning()
	{
		return begin;
	}
	
	//getEnd() returns the point of the end message in the array of messages
	public int getEnd()
	{
		return end;
	}
	
	//getTick() returns the tick of the note
	public long getTick()
	{
		return MIDISong.getEvent(track, begin).getTick();
	}
	
	//getX() returns the x location of the note
	public long getX()
	{
		return MIDISong.getEvent(track, begin).getTick() * MIDIMain.getPreLength();
	}
	
	//getY() returns the y location of the note
	public int getY()
	{
		//return (MAX_TONE - MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(begin).getMessage().getMessage()[2]) * MIDIMain.getPreHeight();
		return (MAX_TONE - tone) * MIDIMain.getPreHeight();
	}
	
	//getX() returns the x location of the note's end
	public long getEndX()
	{
		return getX() + getLength();
	}
	
	//getTone() retuns the tone of the note
	public byte getTone()
	{
		return tone;
	}
	
	//getLength() returns the exact length of the note
	public int getLength()
	{
		return (int)(MIDISong.getEvent(track, end).getTick() - MIDISong.getEvent(track, begin).getTick()) * MIDIMain.getPreLength();
	}
	
	//getVolume() returns the volume of the note
	public byte getVolume()
	{
		//return MIDISong.getMessage(MIDIMain.getTrackMenu(), begin).getMessage()[DATA_VELOCITY];
		return volume;
	}
	
	public boolean isSelected()
	{
		return selected;
	}
	
	//getNumNotes() returns the number of notes counted
	public static int getNumNotes()
	{
		return numNotes;
	}
	
	//resetNotes() resets the value of numNotes, which resets the number of notes
	public static void resetNotes()
	{
		numNotes = 0;
		track = 0;
	}
	
	//convertToNote(byte c, boolean sharp) returns the note letter assigned to a note value
	//byte c = tone value
	//boolean sharp = whether the tone is displayed as flat of sharp
	public static String convertToNote(byte c, boolean sharp)
	{
		byte octave = (byte) ((c-(c%12))/12);
		byte inc = 0;			//Note is incremented if flat
		String semi = "#";		//The sign attached to tone letter
		
		//if display is set to flat tones
		if(sharp == false)
		{
			inc = 1;
			semi = "b";
		}

		//C
		if(c%12 == 0)
		{
			return "C "+octave;
		}
		//C# or Db
		else if(c%12 == 1)
		{
			return (char)('C'+inc)+" "+semi;
		}
		//D
		else if(c%12 == 2)
		{
			return "D";
		}
		//D# or Eb
		else if(c%12 == 3)
		{
			return (char)('D'+inc)+" "+semi;
		}
		//E
		else if(c%12 == 4)
		{
			return "E";
		}
		//F
		else if(c%12 == 5)
		{
			return "F";
		}
		//F# or Gb
		else if(c%12 == 6)
		{
			return (char)('F'+inc)+" "+semi;
		}
		//G
		else if(c%12 == 7)
		{
			return "G";
		}
		//G# or Ab
		else if(c%12 == 8)
		{
			return (char)('G'+inc)+" "+semi;
		}
		//A
		else if(c%12 == 9)
		{
			return "A";
		}
		//A# or Bb
		else if(c%12 == 10)
		{
			return (char)('A'+inc)+" "+semi;
		}
		//B
		else if(c%12 == 11)
		{
			return "B";
		}
		//Invalid note value
		else
		{
			return c+"?";
		}
	}

	//isMessageStatus(byte status, byte comparison) returns true or false whether the status of the message is a variant of its type
	public static boolean isMessageStatus(byte mesStatus, byte statusType)
	{
		//If message is 1 of 16 variants
		if(mesStatus >= statusType && mesStatus <= statusType+16)
			return true;
		else
			return false;
	}
}
