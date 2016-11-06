import java.awt.Rectangle;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

/**
 * Date: October 26, 2016
 * 
 * This class contains all of the data effecting the notes
 * inside the song
 */

public class Notes 
{
	public final static byte maxTone = 120;	//The maximum note value
	private static int numNotes = 0;		//Number of notes processed
	
	private int begin = 0;				//start of note in sequence
	private int end = 0;				//end of note in sequence

	//Initial method
	//int s = start of note (in array)
	//int e = end of note (in array)
	public Notes(int s, int e)
	{
		begin = s;
		end = e;
		numNotes++;
	}
	
	//contains(int x, int y) checks whether the sent coordinates are inside the note's rectangle and returns true or false
	//int x = x coordinate of mouse
	//int y = y coordinate of mouse
	public boolean contains(long x, short y)
	{
		return new Rectangle((int)getX(), getY(), getLength(), MIDIMain.getPreHeight()).contains((int)x, y);
	}
	
	//identifyContained(int x, int y) checks every existing note to determine which note is being clicked on, the object number of the note is returned
	//int x = x coordinate of mouse
	//int y = y coordinate of mouse
	public static int identifyContained(long x, short y)
	{
		for(int i = 0; i < numNotes; i++)
		{
			//If note is on screen
			if(GUI.isNoteVisible(i))
			{
				//If note contains coordinates
				if(MIDISong.getNotes(MIDIMain.getTrackMenu())[i].contains(x, y))
				{
					return i;
				}
			}
		}
		return -1;
	}
	
	//setTone(byte t) sets the tone value of the note
	//byte t = new tone
	public void setTone(byte t)
	{
		try {
			MidiEvent eve = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, MIDIMain.getTrackMenu(), t, MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(begin).getMessage().getMessage()[3]), MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(begin).getTick());
			MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].remove(MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(begin + 1));
			MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].add(eve);
	
			MidiEvent eve2 = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, MIDIMain.getTrackMenu(), t, MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(end).getMessage().getMessage()[3]), MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(end).getTick());
			MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].remove(MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(end + 1));
			MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].add(eve2);
		} catch (ArrayIndexOutOfBoundsException e) {} catch (InvalidMidiDataException e) {}
	}
	
	//setLength(int l) sets the length of the note
	//int l = length of note
	public void setLength(int l)
	{
		setEnd(begin + l);
	}
	
	//setEnd(long x) sets the end location of the note
	//long x = location of note's end
	public void setEnd(long x)
	{
		x = (x - x%MIDIMain.getPreLength()) / MIDIMain.getPreLength();
			
		if(x <= 0 || x <= getX())
			x = getX() + 1;
		
		MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(end).setTick(x);
	}
	
	//setLocation(byte t) sets the location of the note
	//int x = x location of note
	//int y = tone / y location of note
	public void setLocation(long x, short y)
	{
		x = (x - x%MIDIMain.getPreLength()) / MIDIMain.getPreLength();
		y = (short) (maxTone - (y - y%MIDIMain.getPreHeight()) / MIDIMain.getPreHeight());
		if(x < 0)
			x = 0;
		if(y < 0)
			y = 0;
		if(y > 120)
			y = 120;
		MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(end).setTick(x + getLength()/MIDIMain.getPreLength());
		MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(begin).setTick(x);
		//setTone((byte) y);
	}
	
	//getX() returns the x location of the note
	public long getX()
	{
		return MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(begin).getTick() * MIDIMain.getPreLength();
	}
	
	//getY() returns the y location of the note
	public int getY()
	{
		return (maxTone - MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(begin).getMessage().getMessage()[2]) * MIDIMain.getPreHeight();
	}
	
	//getLength() returns the exact length of the note
	public int getLength()
	{
		return (int)((MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(end).getTick() - MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(begin).getTick()) * MIDIMain.getPreLength());
	}
	
	//getVolume() returns the volume of the note
	public byte getVolume()
	{
		return MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(begin).getMessage().getMessage()[3];
	}
	
	//getNumNotes() returns the number of notes counted
	public static int getNumNotes()
	{
		return numNotes;
	}
	
	//resetNotes() resets the value of numNotes, it resets the number of notes
	public static void resetNotes()
	{
		numNotes = 0;
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
}
