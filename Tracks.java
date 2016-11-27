import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.swing.JButton;
import javax.swing.JComboBox;

/**
 * Date: November 1, 2016
 * 
 * This class stores all data that remains in a track of the song. The
 * class stores all notes present in the track.
 * 
 * Note: The program trys to keep the track numbers equal to the channel
 * of the notes contained in the track. However, track number and channel
 * may not always be equal to each other.
 */

public class Tracks 
{	
	public static final short trackSpace = 10;										//Spacing between track menus
	public static final short trackHeight = 70;										//The height of the track menus
	public static final String[] INSTRUMENT_LIST = MIDIReader.getInstrumentList(); 	//The instrument list for the tracks
	
	private static ArrayList<JButton> trackButtons = new ArrayList<JButton>();							//Buttons for tracks
	private static ArrayList<JComboBox<String>> instrumentList = new ArrayList<JComboBox<String>>();	//Instrument indicator for tracks
	
	private int numNotes = 0;					//The number of notes in a track
	private byte instrument = 0;				//The instrument used for the track
	//private byte volume = 100;				//The master volume of every note in a track
	private byte channel = 0;					//The channel that corresponds with track
	private Notes[] notes;						//The array of note contained in the track
	
	//Constructor method
	//byte chan = channel of track
	public Tracks(byte chan)
	{
		channel = chan;
		numNotes = countMessage(channel, (byte)ShortMessage.NOTE_ON);

		//DEBUG
		/*
		System.out.println("Track "+channel+1+" ----------------------------------------");
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
			//int n counts the current number of notes that have been set
			int n = 0;
			
			for(int i = 0; i < MIDISong.getSequence().getTracks()[channel].size(); )
			{
				//If start of a note is found
				if(Notes.isMessageStatus((byte)MIDISong.getMessage(channel, i).getStatus(), (byte)ShortMessage.NOTE_ON))
				{
					//int a = end of note's location
					int a = readForNotes(channel, MIDISong.getMessage(channel, i).getMessage()[Notes.DATA_TONE], Notes.getMessageChannel((byte)MIDISong.getMessage(channel, i).getStatus(), (byte)ShortMessage.NOTE_ON), i);
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
		saveTrack();
		notes = new Notes[0];
		Notes.resetNotes();
	}
	
	//saveTrack() updates the values of the notes to the sequence
	public void saveTrack()
	{
		//int i = message in sequence
		int i = 0;
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
			for(; i < MIDISong.getSequence().getTracks()[channel].size();)
			{
				if(Notes.isMessageStatus((byte)MIDISong.getMessage(channel, i).getStatus(), (byte)ShortMessage.NOTE_ON) || Notes.isMessageStatus((byte)MIDISong.getMessage(channel, i).getStatus(), (byte)ShortMessage.NOTE_OFF))
					MIDISong.getSequence().getTracks()[channel].remove(MIDISong.getEvent(channel, i));
				else
					i++;
			}
			for(n = 0; n < eve.length; n++)
			{
				MIDISong.getSequence().getTracks()[channel].add(eve[n]);
				MIDISong.getSequence().getTracks()[channel].add(eve2[n]);
			}
		} catch (ArrayIndexOutOfBoundsException e) {NotifyAnimation.sendMessage("Error", "Array index out of bound! ("+i+", "+n+")");
		} catch (InvalidMidiDataException e) {NotifyAnimation.sendMessage("Error", "The current track has been deleted or corrupted!");}
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
	 * readForNotes(byte trackNum, byte tone, byte chan, int eventFrom) searches for the end of a note
	 * byte trackNum = track that is being searched in sequence
	 * byte tone = tone of the start of the note
	 * byte chan = channel the note applies to
	 * int eventFrom = location being searched from
	 */
	public static int readForNotes(byte trackNum, byte tone, byte chan, int eventFrom)
	{
		boolean add = false;	//If a message is added in, then it cannot add any more messages afterwards
		for(int i = eventFrom + 1; i < MIDISong.getSequence().getTracks()[trackNum].size(); i++)
		{
			//If status of message equals status being searched
			if(Notes.isMessageChannel((byte)MIDISong.getMessage(trackNum, i).getStatus(), (byte)ShortMessage.NOTE_OFF, chan) && MIDISong.getMessage(trackNum, i).getMessage()[Notes.DATA_TONE] == tone)
			{
				return i;
			}
			//If note has no end before the start of the next note
			else if(Notes.isMessageChannel((byte)MIDISong.getMessage(trackNum, i).getStatus(), (byte)ShortMessage.NOTE_ON, chan) && MIDISong.getMessage(trackNum, i).getMessage()[Notes.DATA_TONE] == tone && add == false)
			{
				try {
					MIDISong.getSequence().getTracks()[trackNum].add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, trackNum, tone, MIDISong.getMessage(trackNum, i).getMessage()[Notes.DATA_VELOCITY]), MIDISong.getEvent(trackNum, i).getTick()));
					i = eventFrom + 1;	//Start from beginning
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
	public void drawTrack(Graphics2D g, short y)
	{
		//Background
		g.setColor(Color.LIGHT_GRAY);
		g.fillRoundRect(50, trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue(), GUI.screenWidth-100, trackHeight, 50, 50);
		//Text Boxes
		g.setColor(Color.WHITE);
		g.fillRect(201, 11+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue(), 98, 18);
		//Borders
		g.setColor(Color.BLACK);
		g.drawRect(200, 10+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue(), 100, 20);
		//Text
		g.drawString(numNotes+" notes", 210, 25+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue());
		//Divider
		g.drawLine(GUI.screenWidth/2, 5+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue(), GUI.screenWidth/2, trackHeight+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue()-5);
	}
	
	//trackLayout() sets the components correctly in the track editor
	public static void trackLayout()
	{
		try
		{
			//Checks if button is behind components
			for(byte i = 0; i < trackButtons.size(); i++)
			{
				if(trackButtons.get(i).getLocation().getY() < GUI.toolBarHeight)
					trackButtons.get(i).setEnabled(false);
				else
					trackButtons.get(i).setEnabled(true);
				
				if(instrumentList.get(i).getLocation().getY() < GUI.toolBarHeight)
					instrumentList.get(i).setEnabled(false);
				else
					instrumentList.get(i).setEnabled(true);
			}
		}
		catch(Exception ex){}//Program is actually more likely to trigger and exception
	}
	
	//addInstrumentList() adds an instrument button to the interface
	public static void addTrackButtons()
	{
		if(trackButtons.size() < 16 && instrumentList.size() < 16)
		{
			instrumentList.add(new JComboBox<String>(INSTRUMENT_LIST));
			instrumentList.get(instrumentList.size() - 1).setFont(GUI.smallFont);
			instrumentList.get(instrumentList.size() - 1).setSize(230, 20);
			instrumentList.get(instrumentList.size() - 1).setBackground(Color.WHITE);
			instrumentList.get(instrumentList.size() - 1).setVisible(true);
			
			trackButtons.add(new JButton("Track "+(trackButtons.size()+1)));
			trackButtons.get(trackButtons.size() - 1).setFont(GUI.boldFont);
			trackButtons.get(trackButtons.size() - 1).setSize(100, 20);
			trackButtons.get(trackButtons.size() - 1).setBackground(Color.WHITE);
			trackButtons.get(trackButtons.size() - 1).setVisible(true);
		}
	}
	
	public static void removeAllButtons()
	{
		for(byte i = 0; i < trackButtons.size(); i++)
		{
			trackButtons.remove(i);
			instrumentList.remove(i);
		}
	}
	
	public static JButton getTrackEntryButton(int index)
	{
		return trackButtons.get(index);
	}
	
	public static JComboBox<String> getInstrumentListButton(int index)
	{
		return instrumentList.get(index);
	}
	
	public static byte getButtonLength()
	{
		return (byte)trackButtons.size();
	}
	
	public static boolean isNotMaximum()
	{
		if(trackButtons.size() < 16 && instrumentList.size() < 16)
		{
			return true;
		}
		return false;
	}
}
