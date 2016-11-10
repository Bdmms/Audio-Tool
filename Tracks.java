import java.awt.Color;
import java.awt.Graphics;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class Tracks 
{
	/**
	 * Date: October 31, 2016
	 * 
	 * This class stores all data that remains in a track of the song. The
	 * class stores all notes present in the track.
	 */
	
	public static final short trackHeight = 70;	//The height of the track window
	
	private byte instrument = 0;				//The instrument used for the track
	private byte channel = 0;					//The channel that corresponds with class
	private Notes[] notes;						//The array of note contained in the track
	
	//Initial method
	//byte chan = channel of track
	public Tracks(byte chan)
	{
		channel = chan;
	}
	
	//openTrack(Track track) opens the data in a track for use in the note editor
	//Track track = the track being opened
	public void openTrack(Track track)
	{
		notes = new Notes[countMessage_NOTE_ON(track)];
		int n = 0;
		
		for(int i = 0; i < track.size(); i++)
		{
			//If start of a note is found
			if(track.get(i).getMessage().getStatus() == ShortMessage.NOTE_ON)
			{
				//a = end of note's location
				int a = readFor(track, (short)ShortMessage.NOTE_OFF, (short)ShortMessage.NOTE_ON, track.get(i).getMessage().getMessage()[1], i);
				//If an end of note can be identified
				if(a >= 0)
				{
					notes[n] = new Notes(i, a);
				}
				n++;
			}
		}
	}
	
	//closeTrack(Track track) closes the data in a track for use in the note editor
	//Track track = the track being closed
	public void closeTrack()
	{
		int i = MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].size() - 1;
		int n = 0;
		MidiEvent[] eve = new MidiEvent[Notes.getNumNotes()];
		MidiEvent[] eve2 = new MidiEvent[Notes.getNumNotes()];
		try {
			for(; n < Notes.getNumNotes(); n++)
			{
				eve[n] = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, MIDIMain.getTrackMenu(), notes[n].getTone(), MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(notes[n].getBeginning()).getMessage().getMessage()[Notes.DATA_VELOCITY]), MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(notes[n].getBeginning()).getTick());
				eve2[n] = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, MIDIMain.getTrackMenu(), notes[n].getTone(), MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(notes[n].getEnd()).getMessage().getMessage()[Notes.DATA_VELOCITY]), MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(notes[n].getEnd()).getTick());
			}
			for(;i >= 0; i--)
			{
				if(MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(i).getMessage().getStatus() == ShortMessage.NOTE_ON || MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(i).getMessage().getStatus() == ShortMessage.NOTE_OFF)
					MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].remove(MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].get(i));
			}
			for(n = 0; n < Notes.getNumNotes(); n++)
			{
				MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].add(eve[n]);
				MIDISong.getSequence().getTracks()[MIDIMain.getTrackMenu()].add(eve2[n]);
			}
		} catch (ArrayIndexOutOfBoundsException e) {NotifyAnimation.sendMessage("Error: Array Index Out of Bound! ("+i+", "+n+")");
		} catch (InvalidMidiDataException e) {NotifyAnimation.sendMessage("Error: Invalid midi data!");}

		notes = new Notes[0];
		Notes.resetNotes();
	}
	
	//countMessage_NOTE_ON(Track track) counts the number of notes in the class and returns it
	//Track track = track being identified
	public static int countMessage_NOTE_ON(Track track)
	{
		int counter = 0;
		for(int i = 0; i < track.size(); i++)
		{
			//If message is a note
			if(track.get(i).getMessage().getStatus() == ShortMessage.NOTE_ON)
			{
				counter++;
			}
		}
		return counter;
	}
	
	/*
	 * readFor(Track track, int status, int statusALT, byte tone, int EventFrom) searches for the end of a note
	 * Track track = track that is being searched
	 * short status = status message being searched
	 * short statusALT = secondary status message being searched
	 * byte tone = tone of the start of the note
	 * int EventFrom = location being searched from
	 */
	public static int readFor(Track track, short status, short statusALT, byte tone, int EventFrom)
	{
		for(int i = EventFrom + 1; i < track.size(); i++)
		{
			//If status of message equals status being searched
			if(track.get(i).getMessage().getStatus() == status && track.get(i).getMessage().getMessage()[1] == tone)
			{
				return i;
			}
			else if(track.get(i).getMessage().getStatus() == statusALT && track.get(i).getMessage().getMessage()[1] == tone)
			{
				return i;
			}
		}
		return -1;
	}
	
	//getNotes() returns the array of notes in the track
	public Notes[] getNotes()
	{
		return notes;
	}
	
	//setInstrument(byte inst) sets the instrument of the track
	//byte inst = instrument value to set
	public void setInstrument(byte inst)
	{
		instrument = inst;
	}
	
	//getInstrument() returns the instrument of the track
	public byte getInstrument()
	{
		return instrument;
	}
	
	//getChannel() returns the channel the track is assigned to
	public byte getChannel()
	{
		return channel;
	}
	
	//drawTrack(Graphics g, int y) draws the track window for the track
	//Graphics g = component of the JPanel used to create visual elements
	//int y = y location of the window
	public void drawTrack(Graphics g, int y)
	{
		g.setColor(Color.LIGHT_GRAY);
		g.fillRoundRect(50, 10+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue(), GUI.screenWidth-100, trackHeight, 50, 50);
	}
}
