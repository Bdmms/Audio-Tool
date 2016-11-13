import java.awt.Color;
import java.awt.Graphics;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

/**
 * Date: October 31, 2016
 * 
 * This class stores all data that remains in a track of the song. The
 * class stores all notes present in the track.
 */

public class Tracks 
{	
	public static final short trackSpace = 10;	//Spacing between track menus
	public static final short trackHeight = 70;	//The height of the track menus
	
	private int numNotes = 0;					//The number of notes in a track
	private byte instrument = 0;				//The instrument used for the track
	private byte volume = 100;					//The master volume of every note in a track
	private byte channel = 0;					//The channel that corresponds with track
	private Notes[] notes;						//The array of note contained in the track
	
	//Constructor method
	//byte chan = channel of track
	public Tracks(byte chan)
	{
		channel = chan;
		numNotes = countMessage(channel, (byte)ShortMessage.NOTE_ON);

		/*
		System.out.println("Track "+channel+" ----------------------------------------");
		for(int i = 0; i < MIDISong.getSequence().getTracks()[chan].size(); i++)
		{
			System.out.println(MIDISong.getSequence().getTracks()[chan].get(i).getMessage().getStatus()+" - "+Integer.toHexString(MIDISong.getSequence().getTracks()[chan].get(i).getMessage().getStatus()));
		}
		*/
	}
	
	//openTrack(byte trackNum) opens the data in a track for use in the note editor
	//byte trackNum = the track being opened in the sequence
	public void openTrack()
	{
		numNotes = countMessage(channel, (byte)ShortMessage.NOTE_ON);
		//If their are a real amount of notes
		if(numNotes >= 0)
		{
			notes = new Notes[numNotes];
			Notes.setTrack(channel);
			int n = 0;
			
			for(int i = 0; i < MIDISong.getSequence().getTracks()[channel].size(); )
			{
				//If start of a note is found
				if(Notes.isMessageStatus((byte)MIDISong.getMessage(channel, i).getStatus(), (byte)ShortMessage.NOTE_ON))
				{
					//a = end of note's location
					int a = readForNotes(channel, MIDISong.getMessage(channel, i).getMessage()[Notes.DATA_TONE], i);
					//If an end of note can be identified
					if(a >= 0)
					{
						notes[n] = new Notes(i, a);
						n++;
					}
					else
					{
						MIDISong.getSequence().getTracks()[channel].remove(MIDISong.getEvent(channel, i));
						numNotes--;
					}
				}
				i++;
			}
		}
		//If note length is invalid
		else
		{
			NotifyAnimation.sendMessage("Error", "Track "+channel+" cannot be edited because it exceeds note limit!");
		}
	}
	
	//closeTrack() closes the data in a track for use in the note editor
	public void closeTrack()
	{
		//int i = event in the sequence
		int i = MIDISong.getSequence().getTracks()[channel].size() - 1;
		//int n = note in the sequence
		int n = 0;
		MidiEvent[] eve = new MidiEvent[Notes.getNumNotes()];
		MidiEvent[] eve2 = new MidiEvent[Notes.getNumNotes()];
		try {
			for(; n < Notes.getNumNotes(); n++)
			{
				eve[n]  = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, channel, notes[n].getTone(), MIDISong.getMessage(channel, notes[n].getBeginning()).getMessage()[Notes.DATA_VELOCITY]), MIDISong.getEvent(channel, notes[n].getBeginning()).getTick());
				eve2[n] = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, channel, notes[n].getTone(), MIDISong.getMessage(channel, notes[n].getEnd()).getMessage()[Notes.DATA_VELOCITY]), MIDISong.getEvent(channel, notes[n].getEnd()).getTick());
			}
			for(; i >= 0; i--)
			{
				if(Notes.isMessageStatus((byte)MIDISong.getMessage(channel, i).getStatus(), (byte)ShortMessage.NOTE_ON) || Notes.isMessageStatus((byte)MIDISong.getMessage(channel, i).getStatus(), (byte)ShortMessage.NOTE_OFF))
					MIDISong.getSequence().getTracks()[channel].remove(MIDISong.getEvent(channel, i));
			}
			for(n = 0; n < eve.length; n++)
			{
				MIDISong.getSequence().getTracks()[channel].add(eve[n]);
				MIDISong.getSequence().getTracks()[channel].add(eve2[n]);
			}
		} catch (ArrayIndexOutOfBoundsException e) {NotifyAnimation.sendMessage("Error", "Array index out of bound! ("+i+", "+n+")");
		} catch (InvalidMidiDataException e) {NotifyAnimation.sendMessage("Error", "The current track has been deleted or corrupted!");}

		notes = new Notes[0];
		Notes.resetNotes();
	}
	
	//countMessage(byte trackNum, byte message) counts the number of notes in the class and returns it
	//byte trackNum = track being identified in sequence
	//byte message =  status type being counted
	public static int countMessage(byte trackNum, byte message)
	{
		int counter = 0;
		for(int i = 0; i < MIDISong.getSequence().getTracks()[trackNum].size(); i++)
		{
			//If message is a note
			if(Notes.isMessageStatus((byte)MIDISong.getMessage(trackNum, i).getStatus(), message))
			{
				counter++;
			}
			//If track size is too long
			if(i == 0xFFFFFFF)
			{
				counter = -1;
				break;
			}
		}
		return counter;
	}
	
	/*
	 * readForNotes(byte trackNum, byte tone, int eventFrom) searches for the end of a note
	 * byte trackNum = = track that is being searched in sequence
	 * byte tone = tone of the start of the note
	 * int EventFrom = location being searched from
	 */
	public static int readForNotes(byte trackNum, byte tone, int eventFrom)
	{
		boolean add = false;	//If a message is added in, then it cannot add any more messages afterwards
		for(int i = eventFrom + 1; i < MIDISong.getSequence().getTracks()[trackNum].size(); i++)
		{
			//If status of message equals status being searched
			if(Notes.isMessageStatus((byte)MIDISong.getMessage(trackNum, i).getStatus(), (byte)ShortMessage.NOTE_OFF) && MIDISong.getMessage(trackNum, i).getMessage()[Notes.DATA_TONE] == tone)
			{
				return i;
			}
			//If note has no end before the start of the next note
			else if(Notes.isMessageStatus((byte)MIDISong.getMessage(trackNum, i).getStatus(), (byte)ShortMessage.NOTE_ON) && MIDISong.getMessage(trackNum, i).getMessage()[Notes.DATA_TONE] == tone && add == false)
			{
				try {
					MIDISong.getSequence().getTracks()[trackNum].add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, trackNum, tone, MIDISong.getMessage(trackNum, i).getMessage()[Notes.DATA_VELOCITY]), MIDISong.getEvent(trackNum, i).getTick()));
					i = eventFrom + 1;
					add = true;
				} catch (ArrayIndexOutOfBoundsException e) {NotifyAnimation.sendMessage("Error", "Array Index Out of Bound! (No Value Assigned)");
				} catch (InvalidMidiDataException e) {NotifyAnimation.sendMessage("Error", "Invalid midi data!");}
			}
		}
		return -1;
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
	
	//getNotes() returns the array of notes in the track
	public Notes[] getNotes()
	{
		return notes;
	}
	
	//getChannel() returns the channel the track is assigned to
	public byte getChannel()
	{
		return channel;
	}
	
	//drawTrack(Graphics g, short y) draws the track window for the track
	//Graphics g = component of the JPanel used to create visual elements
	//short y = y location of the window
	public void drawTrack(Graphics g, short y)
	{
		//Background
		g.setColor(Color.LIGHT_GRAY);
		g.fillRoundRect(50, trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue(), GUI.screenWidth-100, trackHeight, 50, 50);
		
		//Text Boxes
		g.setColor(Color.WHITE);
		g.fillRect(201, 11+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue(), 98, 18);
		g.fillRect(201, 41+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue(), 98, 18);
		
		//Borders
		g.setColor(Color.BLACK);
		g.drawRect(200, 10+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue(), 100, 20);
		g.drawRect(200, 40+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue(), 100, 20);
		//Text
		g.drawString("Instrument: "+instrument, 215, 25+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue());
		g.drawString("Volume: "+volume+"%", 215, 55+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue());
		g.drawString(numNotes+" notes", 90, 55+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue());
		//Divider
		g.drawLine(GUI.screenWidth/2, 5+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue(), GUI.screenWidth/2, trackHeight+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue()-5);
	}
}
