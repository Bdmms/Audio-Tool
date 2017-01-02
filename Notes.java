import java.awt.Rectangle;
import java.util.ArrayList;
import javax.sound.midi.MidiEvent;

/**
 * Date: October 26, 2016
 * 
 * This class contains all of the data effecting the notes
 * inside the song. Notes are only loaded when entering the
 * note editor of a track. Only one track's notes are loaded
 * at a time.
 */

public class Notes extends SelectableObject
{
	public final static byte MAX_TONE = 120;		//The maximum note value
	public final static byte DATA_STATUS = 0;		//value assigned to the message's status
	public final static byte DATA_TONE = 1;			//value assigned to the tone of the note
	public final static byte DATA_VELOCITY = 2;		//value assigned to the volume of the note
	
	private static ArrayList<long[]> copiedNotes = new ArrayList<long[]>();//An array of notes that get copied

	private static int numNotes = 0;				//Number of notes processed
	private static byte track = 0;					//Track being accessed
	
	private MidiEvent start;			//start of note in sequence
	private MidiEvent end;				//end of note in sequence
	private byte tone = 60;				//tone of the note
	private byte volume = 0;			//volume of the note
	
	//Constructor method
	//MidiEvent s = start of note (event)
	//MidiEvent e = end of note (event)
	public Notes(MidiEvent s, MidiEvent e)
	{
		start = s;
		end = e;
		tone = start.getMessage().getMessage()[DATA_TONE];
		volume =  start.getMessage().getMessage()[DATA_VELOCITY];
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
				if(MIDISong.getNotes(track, i).contains(x, y))
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
				if(select.getX() < MIDISong.getNotes(track, i).getX() + MIDISong.getNotes(track, i).getLength() - MIDIMain.getXCoordinate() + GUI.sideBarWidth && select.getX() + select.getWidth() > MIDISong.getNotes(track, i).getX() - MIDIMain.getXCoordinate() + GUI.sideBarWidth &&
						select.getY() < MIDISong.getNotes(track, i).getY() + MIDIMain.getPreHeight() - MIDIMain.getYCoordinate() + GUI.fullAddHeight && select.getY() + select.getHeight() > MIDISong.getNotes(track, i).getY() - MIDIMain.getYCoordinate() + GUI.fullAddHeight)
				{
					MIDISong.getNotes(track, i).selection(true);
				}
			}
		}
	}
	
	//selectAllNotes() selects all notes in the track
	public static void selectAllNotes()
	{
		for(int i = 0; i < numNotes; i++)
		{
			MIDISong.getNotes(track, i).selection(true);
		}
	}
	
	//copyNotes() clears every note that has been copied (to avoid pasting between tracks)
	public static void diposeCopiedNotes()
	{
		copiedNotes.clear();
	}
	
	//copyNotes() copies all of the notes selected
	public static void copyNotes()
	{
		diposeCopiedNotes();
		
		long start = 0;
		
		for(int n = 0; n < numNotes; n++)
		{
			if((start == 0 || start > MIDISong.getNotes(track, n).getTick()) && MIDISong.getNotes(track, n).isSelected())
			{
				start = MIDISong.getNotes(track, n).getTick();
			}
		}
		
		for(int i = 0; i < numNotes; i++)
		{
			if(MIDISong.getNotes(track, i).isSelected())
			{
				long[] noteInfo = {MIDISong.getNotes(track, i).getTick() - start, MIDISong.getNotes(track, i).getEndTick() - start, MIDISong.getNotes(track, i).getTone(), MIDISong.getNotes(track, i).getVolume()};
				copiedNotes.add(noteInfo);
			}
		}
	}
	
	//pasteNotes(long tick, byte tone) pastes selected note at a x and y coordinate
	//long tick = location in the song
	//byte tone = the pitch/tone of the notes (relative to there original position
	public static void pasteNotes(long tick, byte tone)
	{
		for(int i = 0; i < copiedNotes.size(); i++)
		{
			MIDISong.addNote(track, MIDIMain.getXCoordinate()/MIDIMain.getPreLength() + copiedNotes.get(i)[0], (byte)copiedNotes.get(i)[2], (byte)copiedNotes.get(i)[3], MIDIMain.getXCoordinate()/MIDIMain.getPreLength() + copiedNotes.get(i)[1]);
		}
	}
	
	//removeNote() decrements the number of notes to acount for a removed note
	public static void removeNote()
	{
		numNotes--;
	}
	
	//setMidiEvent(MidiEvent s, MidiEvent e) sets the event the note is tied to
	//MidiEvent s = start of note (event)
	//MidiEvent e = end of note (event)
	public void setMidiEvent(MidiEvent s, MidiEvent e)
	{
		start = s;
		end = e;
	}
	
	//setTone(byte t) sets the tone value of the note
	//byte t = new tone
	public void setTone(byte t)
	{
		if(t < 0)
			t = 0;
		if(t > 120)
			t = 120;
		tone = t;
	}
	
	//setVolume(byte v) sets the volume of the note
	//byte v = new volume
	public void setVolume(byte v)
	{
		volume = v;
	}
	
	//setEnd(long x) sets the end location of the note
	//long x = location of note's end
	public void setEnd(long x)
	{
		x = (x - x%MIDIMain.getPreLength()) / MIDIMain.getPreLength();
			
		if(x <= 0 || x <= getTick())
			x = getTick() + 1;
		
		end.setTick(x);;
	}
	
	//setLocation(long x, short y) sets the location of the note
	//long x = x location of note
	//short y = tone / y location of note
	public void setLocation(long x, short y)
	{
		x = (x - x%MIDIMain.getPreLength()) / MIDIMain.getPreLength();
		y = (short) (MAX_TONE - ((y - y%MIDIMain.getPreHeight()) / MIDIMain.getPreHeight()));
		if(x < 0)
			x = 0;
		if(x + (end.getTick() - start.getTick()) > MIDISong.getLength())
			x = MIDISong.getLength() - end.getTick() + start.getTick();
		end.setTick(x + (end.getTick() - start.getTick()));
		start.setTick(x);
		setTone((byte) y);
	}
	
	//setLocation(short y) sets the location of the note
	//short y = tone / y location of note
	public void setY(short y)
	{
		y = (short) (MAX_TONE - ((y - y%MIDIMain.getPreHeight()) / MIDIMain.getPreHeight()));
		setTone((byte) y);
	}
	
	//setTrack(byte trackNum) sets the track that notes come from
	//byte trackNum = track in sequence
	public static void setTrack(byte trackNum)
	{
		track = trackNum;
	}
	
	//getTick() returns the tick of the note
	public long getTick()
	{
		return start.getTick();
	}
	
	public MidiEvent getStartMessage()
	{
		return start;
	}
	
	public MidiEvent getEndMessage()
	{
		return end;
	}
	
	//getX() returns the x location of the note
	public long getX()
	{
		return start.getTick() * MIDIMain.getPreLength();
	}
	
	//getY() returns the y location of the note
	public int getY()
	{
		//return (MAX_TONE - MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(begin).getMessage().getMessage()[2]) * MIDIMain.getPreHeight();
		return (MAX_TONE - tone) * MIDIMain.getPreHeight();
	}
	
	//getX() returns the x location of the note's end
	public long getEndTick()
	{
		return end.getTick();
	}
	
	//getTone() retuns the tone of the note
	public byte getTone()
	{
		return tone;
	}
	
	//getLength() returns the exact length of the note
	public long getLength()
	{
		return (end.getTick() - start.getTick()) * MIDIMain.getPreLength();
	}
	
	//getVolume() returns the volume of the note
	public byte getVolume()
	{
		return volume;
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
	//byte mesStatus = status message being observed
	//byte statusType = status message being compared to
	public static byte getMessageChannel(byte mesStatus, byte statusType)
	{
		if(isMessageStatus(mesStatus, statusType))
		{
			return (byte) (mesStatus - statusType);
		}
		return -1;
	}

	//isMessageStatus(byte status, byte comparison) returns true or false whether the status of the message is a variant of its type
	//byte mesStatus = status message being observed
	//byte statusType = status message being compared to
	public static boolean isMessageStatus(byte mesStatus, byte statusType)
	{
		//If message is 1 of 16 variants
		if(mesStatus >= statusType && mesStatus < statusType+16)
			return true;
		else
			return false;
	}
	
	//isMessageChannel(byte status, byte comparison, byte channel) returns true or false whether the status of the message is the channel's variant type
	//byte mesStatus = status message being observed
	//byte statusType = status message being compared to
	//byte channel = channel that is being used 
	public static boolean isMessageChannel(byte mesStatus, byte statusType, byte channel)
	{
		//If message is 1 of 16 variants
		if(mesStatus == statusType + channel)
			return true;
		else
			return false;
	}
}
