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

public class Tracks extends SelectableObject
{	
	public static byte tracksVisible = 4;										//The number of tracks that can visible at one time
	public static final short trackSpace = 10;										//Spacing between track menus
	public static final short trackHeight = 70;										//The height of the track menus
	public static final String[] INSTRUMENT_LIST = MIDIReader.getInstrumentList(); 	//The instrument list for the tracks
	
	private static ArrayList<JButton> trackButtons = new ArrayList<JButton>();							//Buttons for tracks
	private static ArrayList<JComboBox<String>> instrumentList = new ArrayList<JComboBox<String>>();	//Instrument indicator for tracks
	private VolumeSlider slider = new VolumeSlider((short)0,(short)0, true);
	
	private int numNotes = 0;									//The number of notes in a track
	private int endNotes = 0;
	private byte instrument = 0;								//The instrument used for the track
	private byte volume = 100;									//The master volume of every note in a track
	private byte channel = 0;									//The channel that corresponds with track
	private ArrayList<Notes> notes = new ArrayList<Notes>();	//The array of note contained in the track

	//Constructor method
	//byte chan = channel of track
	public Tracks(byte chan)
	{
		channel = chan;
		updateNoteCount();
		
		//DEBUG
		/*
		if(channel >= 0)
		{
			System.out.println("\nTrack "+(channel+1)+" ----------------------------------------");
			for(int i = 0; i < MIDISong.getSequence().getTracks()[channel].size(); i++)
			{
				System.out.print("\n"+i+": "+String.format("%4d",MIDISong.getEvent(channel, i).getTick())+" ticks |");
				for(int m = 0; m < MIDISong.getMessage(channel, i).getLength(); m++)
				{
					System.out.print(String.format("%4d",MIDISong.getMessage(channel, i).getMessage()[m])+"|");
				}
			}
		}
		*/
		
		int v = readFor(channel, (byte)ShortMessage.PROGRAM_CHANGE, 0);
		if(v >= 0)
		{
			//System.out.println(v);
			setInstrument((byte)MIDISong.getMessage(channel, v).getMessage()[1]);
			MIDISong.getSequence().getTracks()[channel].remove(MIDISong.getSequence().getTracks()[channel].get(v));
		}
	}
	
	//changeChannel(byte chan) changes the channel that the track is assigned to
	//byte chan = channel being assigned
	public void changeChannel(byte chan)
	{
		channel = chan;
		updateNoteCount();
	}
	
	//openTrack(byte trackNum) opens the data in a track for use in the note editor
	//byte trackNum = the track being opened in the sequence
	public void openTrack()
	{
		Notes.setTrack(channel);
		updateNoteCount();
		endNotes = countMessage(channel, (byte)ShortMessage.NOTE_OFF);
		//If their are a real amount of notes
		if(numNotes >= 0)
		{
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
						notes.add(new Notes(MIDISong.getEvent(channel, i), MIDISong.getEvent(channel, a)));
						MIDISong.getSequence().getTracks()[channel].remove(notes.get(Notes.getNumNotes()-1).getStartMessage());
						MIDISong.getSequence().getTracks()[channel].remove(notes.get(Notes.getNumNotes()-1).getEndMessage());
					}
					else
					{
						MIDISong.getSequence().getTracks()[channel].remove(MIDISong.getEvent(channel, i));
						numNotes--;
					}
				}
				else
				{
					i++;
				}
			}
			saveTrack();
		}
		//If note length is invalid
		else
		{
			NotifyAnimation.sendMessage("Error", "Track "+channel+" cannot be edited because it exceeds note limit!");
		}
	}
	
	//cleanTrack() removes any unwanted or invalid midi messages
	public void cleanTrack()
	{
		for(int m = 0; m < MIDISong.getSequence().getTracks()[channel].size(); m++)
		{
			if(MIDISong.getEvent(channel, m).getTick() < 0)
			{
				MIDISong.getSequence().getTracks()[channel].remove(MIDISong.getEvent(channel, m));
			}
			//0x80 = NOTE_OFF || checks all varied message types that exist
			/*
			for(int status = 0x80; status < 0xF0; status += 16)
			{
				if(Notes.isMessageStatus((byte)MIDISong.getMessage(channel, m).getStatus(), (byte)status) && Notes.getMessageChannel((byte)MIDISong.getMessage(channel, m).getStatus(), (byte)status) != channel)
				{
					MIDISong.getSequence().getTracks()[Notes.getMessageChannel((byte)MIDISong.getMessage(channel, m).getStatus(), (byte)status)].add(MIDISong.getEvent(channel, m));
					MIDISong.getSequence().getTracks()[channel].remove(MIDISong.getEvent(channel, m));
				}
			}
			*/
		}
	}
	
	//closeTrack() closes the data in a track for use in the note editor
	public void closeTrack()
	{
		saveTrack();
		notes.clear();
		Notes.resetNotes();
		numNotes = countMessage(channel, (byte)ShortMessage.NOTE_ON);
	}
	
	//saveTrack() updates the values of the notes to the sequence
	public void saveTrack()
	{
		//int n = note in the sequence
		int n = 0;
		try {
			for(; n < Notes.getNumNotes(); n++)
			{
				MIDISong.getSequence().getTracks()[channel].remove(notes.get(n).getStartMessage());
				MIDISong.getSequence().getTracks()[channel].remove(notes.get(n).getEndMessage());
			}
			for(n = 0; n < Notes.getNumNotes(); n++)
			{
				notes.get(n).setMidiEvent(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON + channel, channel, notes.get(n).getTone(), notes.get(n).getVolume()), notes.get(n).getTick()), new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF + channel, channel, notes.get(n).getTone(), notes.get(n).getVolume()), notes.get(n).getEndTick()));
				MIDISong.getSequence().getTracks()[channel].add(notes.get(n).getStartMessage());
				MIDISong.getSequence().getTracks()[channel].add(notes.get(n).getEndMessage());
			}
		} catch (ArrayIndexOutOfBoundsException e) {NotifyAnimation.sendMessage("Error", "Array index out of bound! ("+n+")");
		} catch (InvalidMidiDataException e) {NotifyAnimation.sendMessage("Error", "The current track has been deleted or corrupted!");}
	}
	
	//addNote(int s, int e) creates a new Notes object using the parameters and adds it to the note array
	//int s = location of start of note
	//int e = location of end of notes
	public void addNote(MidiEvent s, MidiEvent e)
	{
		notes.add(new Notes(s, e));
	}
	
	//addNote(int s, int e) creates a new Notes object using the parameters and adds it to the note array
	//long tick = location of note
	//long endTick = location of note's end
	//byte t = tone of note
	//byte v = volume of note
	public void addNote(long tick, long endTick, byte t, byte v)
	{
		MidiEvent eveStart = null;
		MidiEvent eveEnd = null;
		try {
			eveStart = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, channel, t, v), tick);
			eveEnd = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, channel, t, v), endTick);
		} catch (InvalidMidiDataException e) {NotifyAnimation.sendMessage("Error", "Note could not be created.");}
		MIDISong.getSequence().getTracks()[channel].add(eveStart);
		MIDISong.getSequence().getTracks()[channel].add(eveEnd);
		notes.add(new Notes(eveStart, eveEnd));
	}
	
	//removeNote(Notes note) removes the note from the track
	//int note = note being removed
	public void removeNote(int note)
	{
		notes.remove(note);
		Notes.removeNote();
		numNotes--;
	}
	
	public void updateNoteCount()
	{
		numNotes = countMessage(channel, (byte)ShortMessage.NOTE_ON);
	}
	
	//countMessage(byte trackNum, byte message) counts the number of notes in the class and returns it
	//byte trackNum = track being identified in sequence
	//byte message =  status type being counted
	public static int countMessage(byte trackNum, byte message)
	{
		int counter = 0;
		for(int i = 0; i < MIDISong.getSequence().getTracks()[trackNum].size(); i++)
		{
			//If message is the message being searched
			if(Notes.isMessageStatus((byte)MIDISong.getMessage(trackNum, i).getStatus(), message))
				counter++;
			//If track size is too long (Error handler for large tracks)
			if(i == 0xFFFFFFFF)
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
			else if(MIDISong.getTracks(trackNum).endNotes != MIDISong.getTracks(trackNum).numNotes)
			{
				//If note has no end before the start of the next note
				if(Notes.isMessageChannel((byte)MIDISong.getMessage(trackNum, i).getStatus(), (byte)ShortMessage.NOTE_ON, chan) && MIDISong.getMessage(trackNum, i).getMessage()[Notes.DATA_TONE] == tone && add == false)
				{
					int c = i;
					while(MIDISong.getEvent(trackNum, c).getTick() == MIDISong.getEvent(trackNum, i).getTick() && c < MIDISong.getSequence().getTracks()[trackNum].size())
					{
						if(Notes.isMessageChannel((byte)MIDISong.getMessage(trackNum, c).getStatus(), (byte)ShortMessage.NOTE_OFF, chan))
						{
							return c;
						}
						c++;
						MIDISong.getTracks(trackNum).endNotes++;
					}
					try {
						MIDISong.getSequence().getTracks()[trackNum].add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, trackNum, tone, MIDISong.getMessage(trackNum, i).getMessage()[Notes.DATA_VELOCITY]), MIDISong.getEvent(trackNum, i).getTick()));
						i = eventFrom + 1;	//Start from beginning
						add = true;
					} catch (ArrayIndexOutOfBoundsException e) {NotifyAnimation.sendMessage("Error", "Array Index Out of Bound! (No Value Assigned)");
					} catch (InvalidMidiDataException e) {NotifyAnimation.sendMessage("Error", "Invalid midi data!");}
				}
			}
		}
		return eventFrom + 1;
	}
	
	//readFor(byte trackNum, byte message, int eventFrom) searches for a variant message in the track
	//byte trackNum = track being searched
	//byte message = message being searched for
	//int eventFrom = location to start search from
	public static int readFor(byte trackNum, byte message, int eventFrom)
	{
		for(int i = eventFrom; i < MIDISong.getSequence().getTracks()[trackNum].size(); i++)
		{
			if(Notes.isMessageStatus((byte)MIDISong.getMessage(trackNum, i).getStatus(), message))
			{
				return i;
			}
		}
		return -1;
	}
	
	//readFor(byte trackNum, byte message, int eventFrom) searches for a specific meta message in the track
	//byte trackNum = track being searched
	//byte message = message being searched for
	//int eventFrom = location to start search from
	public static int readForMeta(byte trackNum, byte message, int eventFrom)
	{
		for(int i = eventFrom; i < MIDISong.getSequence().getTracks()[trackNum].size(); i++)
		{
			if((byte)MIDISong.getMessage(trackNum, i).getMessage()[1] == message)
			{
				return i;
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
	
	//setVolume(byte v) sets the volume of the track
	//byte v = new volume
	public void setVolume(byte v)
	{
		volume = v;
		slider.setVolume(v);
	}
	
	//getInstrument() returns the instrument of the track
	public byte getInstrument()
	{
		return instrument;
	}
	
	//getNotes() returns the array of notes in the track
	//int note = index of note in array
	public Notes getNotes(int note)
	{
		return notes.get(note);
	}
	
	//getChannel() returns the channel the track is assigned to
	public byte getChannel()
	{
		return channel;
	}
	
	//getVolume() returns the volume of the track
	public byte getVolume()
	{
		return volume;
	}
	
	//VolumeSlider getSlider() returns the volume slider that sets the volume of the track
	public VolumeSlider getSlider()
	{
		return slider;
	}
	
	//drawTrack(Graphics g, short y) draws the track window for the track
	//Graphics g = component of the JPanel used to create visual elements
	//short y = y location of the window
	public void drawTrack(Graphics2D g, short y)
	{
		//Background
		g.setColor(GUI.colours[GUI.getColourScheme()][2]);
		g.fillRoundRect(50, trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue(), GUI.screenWidth-100, trackHeight, 50, 50);
		
		//Volume Slider
		slider.setBounds((short)(GUI.screenWidth/2 + 25), (short)(GUI.fullAddHeight + (trackHeight + 5)*y + trackSpace - MIDIMain.getScrollValue()), (short)(GUI.screenWidth*3/4 - GUI.screenWidth/2), (short)30);
		slider.drawVolumeSlider(g);
		volume = slider.getPercent();
		
		if(isSelected())
		{
			g.setStroke(GUI.superBold);
			g.setColor(GUI.colours[GUI.getColourScheme()][4]);
			g.drawRoundRect(50, trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue(), GUI.screenWidth-100, trackHeight, 50, 50);
			g.setStroke(GUI.basic);
		}
		else
		{
			g.setColor(GUI.colours[GUI.getColourScheme()][GUI.COLOUR_TEXT]);
			g.drawRoundRect(50, trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue(), GUI.screenWidth-100, trackHeight, 50, 50);
		}
		
		//Text Boxes
		g.setColor(GUI.colours[GUI.getColourScheme()][GUI.COLOUR_BG]);
		g.fillRect(201, 11+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue(), 98, 18);
		g.fillRect(GUI.screenWidth*3/4 + 41, 16+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y - MIDIMain.getScrollValue(), 68, 18);
		g.fillRect(GUI.screenWidth*3/4 + 41, 36+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y - MIDIMain.getScrollValue(), 68, 18);
		//Borders
		g.setColor(GUI.colours[GUI.getColourScheme()][GUI.COLOUR_TEXT]);
		g.drawRect(200, 10+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue(), 100, 20);
		g.drawRect(GUI.screenWidth*3/4 + 40, 15+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y - MIDIMain.getScrollValue(), 70, 20);
		g.drawRect(GUI.screenWidth*3/4 + 40, 35+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y - MIDIMain.getScrollValue(), 70, 20);
		//Text
		g.setFont(GUI.defaultFont);
		g.drawString(numNotes+" notes", 210, 25+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y-MIDIMain.getScrollValue());
		g.drawString(volume+"%", GUI.screenWidth*3/4 + 50, 50+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y - MIDIMain.getScrollValue());
		g.setFont(GUI.boldFont);
		g.drawString("VOLUME", GUI.screenWidth*3/4 + 50, 30+trackSpace+GUI.toolBarHeight+(trackHeight + 5)*y - MIDIMain.getScrollValue());
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
				if(trackButtons.get(i).getLocation().getY() < GUI.toolBarHeight || (trackButtons.get(i).getLocation().getY() + trackButtons.get(i).getHeight() > GUI.screenHeight - 140  && MIDIMain.isInfoBarVisible()))
				{
					trackButtons.get(i).setEnabled(false);
					trackButtons.get(i).setOpaque(false);
				}
				else
				{
					trackButtons.get(i).setEnabled(true);
					trackButtons.get(i).setOpaque(true);
				}
				
				if(instrumentList.get(i).getLocation().getY() < GUI.toolBarHeight || (instrumentList.get(i).getLocation().getY() + instrumentList.get(i).getHeight() > GUI.screenHeight - 140 && MIDIMain.isInfoBarVisible()))
				{
					instrumentList.get(i).setEnabled(false);
					instrumentList.get(i).setOpaque(false);
				}
				else
				{
					instrumentList.get(i).setEnabled(true);
					instrumentList.get(i).setOpaque(true);
				}
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
			instrumentList.get(instrumentList.size() - 1).setSize(GUI.screenWidth/3, 20);
			instrumentList.get(instrumentList.size() - 1).setBackground(Color.WHITE);
			instrumentList.get(instrumentList.size() - 1).setVisible(true);
			instrumentList.get(instrumentList.size() - 1).setSelectedIndex(MIDISong.getTracks((byte)(instrumentList.size() - 1)).getInstrument());
			
			trackButtons.add(new JButton("Track "+(trackButtons.size()+1)));
			trackButtons.get(trackButtons.size() - 1).setFont(GUI.boldFont);
			trackButtons.get(trackButtons.size() - 1).setSize(100, 20);
			trackButtons.get(trackButtons.size() - 1).setBackground(Color.WHITE);
			trackButtons.get(trackButtons.size() - 1).setVisible(true);
		}
	}
	
	//removeTrackButton(byte button) removes a set of buttons for a track
	//byte button = button being removed
	public static void removeTrackButton()
	{
		MIDIMain.removeTrackButton((byte)(trackButtons.size() - 1));
		instrumentList.remove(trackButtons.size() - 1);
		trackButtons.remove(trackButtons.size() - 1);
	}
	
	public static void resizeButtons()
	{
		for(byte t = 0; t < MIDISong.getTracksLength(); t++)
		{
			instrumentList.get(t).setSize(GUI.screenWidth/3, 20);
		}
	}
	
	public static void resetAllButtons()
	{
		for(byte i = (byte) (trackButtons.size() - 1); i >= 0; i--)
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
