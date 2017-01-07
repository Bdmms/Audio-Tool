import java.awt.Rectangle;
import java.util.ArrayList;
import javax.sound.midi.MidiEvent;

/**
 * <b>[Date: October 26, 2016]</b>
 * <p>
 * This class contains all of the data effecting the notes
 * inside the song. Notes are only loaded when entering the
 * note editor of a track. Only one track's notes are loaded
 * at a time.
 * </p>
 */
public class Notes extends SelectableObject
{
	public final static byte MAX_TONE = 120;		//The maximum note value
	public final static byte DATA_STATUS = 0;		//value assigned to the message's status
	public final static byte DATA_TONE = 1;			//value assigned to the tone of the note
	public final static byte DATA_VELOCITY = 2;		//value assigned to the volume of the note
	
	private static ArrayList<long[]> copiedNotes = new ArrayList<long[]>();	//An array of notes that get copied
	private static int numNotes = 0;	//Number of notes processed
	private static byte track = 0;		//Track being accessed
	private MidiEvent start;			//start of note in sequence
	private MidiEvent end;				//end of note in sequence
	private byte tone = 60;				//tone of the note
	private byte volume = 0;			//volume of the note
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public Notes(MidiEvent s, MidiEvent e)}</pre></p> 
	 * Constructor method.</p> 
	 * @param s = start of note (event)
	 * @param e = end of note (event)
	 */
	public Notes(MidiEvent s, MidiEvent e)
	{
		start = s;
		end = e;
		tone = start.getMessage().getMessage()[DATA_TONE];
		volume =  start.getMessage().getMessage()[DATA_VELOCITY];
		numNotes++;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public boolean contains(long x, short y)}</pre></p> 
	 * Checks whether the sent coordinates are inside the note's rectangle and returns true or false.</p> 
	 * @param x = x coordinate of mouse
	 * @param y = y coordinate of mouse
	 * @return Whether the coordinates are inside the note
	 */
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
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setMidiEvent(MidiEvent s, MidiEvent e)}</pre></p> 
	 * Sets the events the note is tied to.</p> 
	 * @param s = start of note (event)
	 * @param e = end of note (event)
	 */
	public void setMidiEvent(MidiEvent s, MidiEvent e)
	{
		start = s;
		end = e;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setTone(byte t)}</pre></p> 
	 * Sets the tone value of the note.</p> 
	 * @param t = new tone
	 */
	public void setTone(byte t)
	{
		//If tone isn't inside limits
		if(t < 0)
			t = 0;
		if(t > 120)
			t = 120;
		tone = t;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setVolume(byte v)}</pre></p> 
	 * Sets the volume of the note.</p> 
	 * @param v = new volume
	 */
	public void setVolume(byte v)
	{
		volume = v;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setEnd(long x)}</pre></p> 
	 * Sets the end location of the note.</p> 
	 * @param x = location of note's end
	 */
	public void setEnd(long x)
	{
		x = (x - x%MIDIMain.getPreLength()) / MIDIMain.getPreLength();
			
		if(x <= 0 || x <= getTick())
			x = getTick() + 1;
		
		end.setTick(x);;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setLocation(long x, short y)}</pre></p> 
	 * Sets the location of the note.</p> 
	 * @param x = location of note
	 * @param y = tone / y location of note
	 */
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
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setY(short y)}</pre></p> 
	 * Sets the location of the note.</p> 
	 * @param y = tone / y location of note
	 */
	public void setY(short y)
	{
		y = (short) (MAX_TONE - ((y - y%MIDIMain.getPreHeight()) / MIDIMain.getPreHeight()));
		setTone((byte) y);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public long getTick()}</pre></p> 
	 * Returns the tick of the note.</p> 
	 * @return The tick of the note
	 */
	public long getTick()
	{
		return start.getTick();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public MidiEvent getStartMessage()}</pre></p> 
	 * Returns the event for the start of the note.</p> 
	 * @return The <b>MidiEvent</b> for the NOTE_ON message
	 */
	public MidiEvent getStartMessage()
	{
		return start;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public MidiEvent getEndMessage()}</pre></p> 
	 * Returns the event for the end of the note.</p> 
	 * @return The <b>MidiEvent</b> for the NOTE_OFF message
	 */
	public MidiEvent getEndMessage()
	{
		return end;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public long getX()}</pre></p> 
	 * Returns the x location of the note.</p> 
	 * @return The x location of the note
	 */
	public long getX()
	{
		return start.getTick() * MIDIMain.getPreLength();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public long getY()}</pre></p> 
	 * Returns the y location of the note.</p> 
	 * @return The y location of the note
	 */
	public int getY()
	{
		return (MAX_TONE - tone) * MIDIMain.getPreHeight();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public long getEndTick()}</pre></p> 
	 * Returns the location of the note's end.</p> 
	 * @return The tick of the note's end
	 */
	public long getEndTick()
	{
		return end.getTick();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public byte getTone()}</pre></p> 
	 * Returns the tone of the note.</p> 
	 * @return The tone of the note
	 */
	public byte getTone()
	{
		return tone;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public byte getLength()}</pre></p> 
	 * Returns the length of the note.</p> 
	 * @return The length of the note in ticks
	 */
	public long getLength()
	{
		return (end.getTick() - start.getTick()) * MIDIMain.getPreLength();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public byte getVolume()}</pre></p> 
	 * Returns the volume of the note.</p> 
	 * @return The volume of the note
	 */
	public byte getVolume()
	{
		return volume;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void setTrack(byte trackNum)}</pre></p> 
	 * Sets the track that notes come from.</p>
	 * @param  trackNum = track in sequence
	 */
	public static void setTrack(byte trackNum)
	{
		track = trackNum;
	}
		
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void resetNotes()}</pre></p> 
	 * Resets the number of notes.</p>
	 */
	public static void resetNotes()
		{
		numNotes = 0;
		track = 0;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static int identifyContained(long x, short y)}</pre></p> 
	 * Checks every existing note to determine which note is being clicked on, the object number of the note is returned.</p> 
	 * @param x = x coordinate of mouse
	 * @param y = y coordinate of mouse
	 * @return The note that is found to be selected (-1 if none are found)
	 */
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
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void selectContained(Rectangle select)}</pre></p> 
	 * Searches all notes that are contained in the selection box and selects them.</p> 
	 * @param select = selection box that selects all notes inside of it
	 */
	public static void selectContained(Rectangle select)
	{
		//Checks every note
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
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void selectAllNotes()}</pre></p> 
	 * Selects all notes in the track.</p> 
	 */
	public static void selectAllNotes()
	{
		//Goes through every note
		for(int i = 0; i < numNotes; i++)
		{
			MIDISong.getNotes(track, i).selection(true);
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void diposeCopiedNotes()}</pre></p> 
	 * Clears every note that has been copied (to avoid pasting between tracks).</p> 
	 */
	public static void diposeCopiedNotes()
	{
		copiedNotes.clear();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void copyNotes()}</pre></p> 
	 * Copies all selected notes in the track.</p> 
	 */
	public static void copyNotes()
	{
		diposeCopiedNotes();
		
		long start = 0;
		
		//Checks every note
		for(int n = 0; n < numNotes; n++)
		{
			//Finds the note that is closest to the end (left side)
			if((start == 0 || start > MIDISong.getNotes(track, n).getTick()) && MIDISong.getNotes(track, n).isSelected())
			{
				start = MIDISong.getNotes(track, n).getTick();
			}
		}
		//Goes through every note
		for(int i = 0; i < numNotes; i++)
		{
			//If note is selected
			if(MIDISong.getNotes(track, i).isSelected())
			{
				//Sets the tick location depending on the front note (which was identified before)
				long[] noteInfo = {MIDISong.getNotes(track, i).getTick() - start, MIDISong.getNotes(track, i).getEndTick() - start, MIDISong.getNotes(track, i).getTone(), MIDISong.getNotes(track, i).getVolume()};
				copiedNotes.add(noteInfo);
			}
		}
	}

	/**
	 * <blockquote>
	 * <p><pre>{@code public static void pasteNotes(long tick, byte tone)}</pre></p> 
	 * Pastes selected note at a x coordinate (tick).</p> 
	 * @param tick = location in the song
	 */
	public static void pasteNotes(long tick)
	{
		for(int i = 0; i < copiedNotes.size(); i++)
		{
			MIDISong.addNote(track, (byte)copiedNotes.get(i)[2], (byte)copiedNotes.get(i)[3], MIDIMain.getXCoordinate()/MIDIMain.getPreLength() + copiedNotes.get(i)[0], MIDIMain.getXCoordinate()/MIDIMain.getPreLength() + copiedNotes.get(i)[1]);
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void removeNote()}</pre></p> 
	 * Decrements the number of notes for when a note is removed.</p> 
	 */
	public static void removeNote()
	{
		numNotes--;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static String convertToNote(byte c, boolean sharp)}</pre></p> 
	 * Converts a tone value to a letter tone.</p> 
	 * @param c = tone value
	 * @param sharp = whether the tone is displayed as flat of sharp
	 * @return The note letter assigned to a note value
	 */
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
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static int getNumNotes()}</pre></p> 
	 * Returns the number of notes counted.</p> 
	 * @return The number of notes counted
	 */
	public static int getNumNotes()
	{
		return numNotes;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static byte getMessageChannel(byte mesStatus, byte statusType)}</pre></p> 
	 * Determines the channel of a message from a message type.</p> 
	 * @param mesStatus = status message being observed
	 * @param statusType = status message being compared to
	 * @return The channel of a message
	 */
	public static byte getMessageChannel(byte mesStatus, byte statusType)
	{
		//Checks if message will work with message type
		if(isMessageStatus(mesStatus, statusType))
		{
			return (byte) (mesStatus - statusType);
		}
		return -1;
	}

	/**
	 * <blockquote>
	 * <p><pre>{@code public static boolean isMessageStatus(byte mesStatus, byte statusType)}</pre></p> 
	 * Returns true or false whether the status of the message is a variant of its type.</p> 
	 * @param mesStatus = status message being observed
	 * @param statusType = status message being compared to
	 * @return Whether the message is a variant of a message type
	 */
	public static boolean isMessageStatus(byte mesStatus, byte statusType)
	{
		//If message is 1 of 16 variants
		if(mesStatus >= statusType && mesStatus < statusType+16)
			return true;
		else
			return false;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static boolean isMessageChannel(byte mesStatus, byte statusType)}</pre></p> 
	 * Returns true or false whether the status of the message is the channel's variant type.</p> 
	 * @param mesStatus = status message being observed
	 * @param statusType = status message being compared to
	 * @param channel = channel that is being compared
	 * @return Whether the message is set to the channel (-1 if message isn't a variant of its message type
	 */
	//isMessageChannel(byte status, byte comparison, byte channel) returns true or false whether the status of the message is the channel's variant type
	public static boolean isMessageChannel(byte mesStatus, byte statusType, byte channel)
	{
		//If message is 1 of 16 variants
		if(mesStatus == statusType + channel)
			return true;
		else
			return false;
	}
}
