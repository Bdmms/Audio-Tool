import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.swing.JButton;
import javax.swing.JComboBox;

/**
 * <b>[Date: November 1, 2016]</b>
 * <p>
 * This class stores all data that remains in a track of the song. The
 * class stores all notes present in the track.
 * </p>
 * <p>
 * Note: The program trys to keep the track numbers equal to the channel
 * of the notes contained in the track. However, track number and channel
 * may not always be equal to each other.
 * </p>
 */
public class Tracks extends SelectableObject
{	
	public static byte tracksVisible = 4;											//The number of tracks that can visible at one time
	public static final short trackSpace = 10;										//Spacing between track menus
	public static final short trackHeight = 70;										//The height of the track menus
	public static final String[] INSTRUMENT_LIST = MIDIReader.getInstrumentList(); 	//The instrument list for the tracks
	
	private JButton trackButtons;													//Entry button for track
	private JComboBox<String> instrumentList;										//Instrument indicator for track
	private VolumeSlider slider = new VolumeSlider((short)0,(short)0, true);		//Volume slider for the track
	
	private int numNotes = 0;									//The number of notes in a track
	private byte instrument = 0;								//The instrument used for the track
	private byte volume = 100;									//The master volume of every note in a track
	private byte channel = 0;									//The channel that corresponds with track
	private ArrayList<Notes> notes = new ArrayList<Notes>();	//The array of note contained in the track

	/**
	 * <blockquote>
	 * <p><pre>{@code public Tracks(byte chan)}</pre></p> 
	 * The constructor method.</p> 
	 * @param chan = channel of track
	 */
	public Tracks(byte chan)
	{
		channel = chan;
		createTrackButtons();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void changeChannel(byte chan)}</pre></p> 
	 * Changes the channel that the track is assigned to.</p> 
	 * @param chan = channel being assigned
	 */
	public void changeChannel(byte chan)
	{
		channel = chan;
		trackButtons.setText("Track "+(channel+1));
		updateNoteCount();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void createTrackButtons()}</pre></p> 
	 * Adds a set of components to the track window.</p>
	 */
	public void createTrackButtons()
	{
		instrumentList = new JComboBox<String>(INSTRUMENT_LIST);
		instrumentList.setFont(GUI.smallFont);
		instrumentList.setSize(GUI.screenWidth/3, 20);
		instrumentList.setBackground(Color.WHITE);
		instrumentList.setVisible(true);
		//To allow key listener to work
		instrumentList.setFocusable(false);
			
		trackButtons = new JButton("Track "+(channel+1));
		trackButtons.setFont(GUI.boldFont);
		trackButtons.setSize(100, 20);
		trackButtons.setBackground(Color.WHITE);
		trackButtons.setVisible(true);
		//To allow key listener to work
		trackButtons.setFocusable(false);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void trackLayout()}</pre></p> 
	 * Sets the component correctly for track window.</p>
	 */
	public void trackLayout()
	{
		try
		{
			//If button is behind another component
			if(trackButtons.getLocation().getY() < GUI.toolBarHeight || (trackButtons.getLocation().getY() + trackButtons.getHeight() > GUI.screenHeight - 140  && MIDIMain.isInfoBarVisible()))
			{
				//Disables button
				trackButtons.setEnabled(false);
				trackButtons.setOpaque(false);
			}
			else
			{
				trackButtons.setEnabled(true);
				trackButtons.setOpaque(true);
			}
			//If combo box is behind another component
			if(instrumentList.getLocation().getY() < GUI.toolBarHeight || (instrumentList.getLocation().getY() + instrumentList.getHeight() > GUI.screenHeight - 140 && MIDIMain.isInfoBarVisible()))
			{
				//Disables combo box
				instrumentList.setEnabled(false);
				instrumentList.setOpaque(false);
			}
			else
			{
				instrumentList.setEnabled(true);
				instrumentList.setOpaque(true);
			}
		}
		catch(Exception ex){}//Program is actually more likely to trigger an exception (Due to action listener making edits to the code while it is processing)
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void cleanTrack()}</pre></p> 
	 * Removes any unwanted or invalid midi messages and sorts messages to their correct channel.</p> 
	 */
	public void cleanTrack()
	{
		//Cycle through the track to each event
		for(int m = MIDISong.getSequence().getTracks()[channel].size() - 1; m >= 0; m--)
		{
			try
			{
				//If event has a negative tick value
				if(MIDISong.getEvent(channel, m).getTick() < 0)
				{
					MIDISong.getSequence().getTracks()[channel].remove(MIDISong.getEvent(channel, m));
				}
				//If the event's message is a channel dependent message
				if(MIDISong.getMessage(channel, m).getStatus() >= 0x80 && MIDISong.getMessage(channel, m).getStatus() < 0xF0)
				{
					//chan = channel that the message changes
					byte chan = Notes.getMessageChannel((byte)MIDISong.getMessage(channel, m).getStatus(), (byte)(MIDISong.getMessage(channel, m).getStatus() - MIDISong.getMessage(channel, m).getStatus()%16));
					//If the channel of the track is not the same as the message's channel
					if(chan != channel)
					{
						//message is moved to its corresponding track
						MIDISong.getSequence().getTracks()[chan].add(MIDISong.getEvent(channel, m));
						MIDISong.getSequence().getTracks()[channel].remove(MIDISong.getEvent(channel, m));
					}
				}
			}
			//If the catch is triggered it means that the message effects a channel greater than the track amount
			catch(IndexOutOfBoundsException e){
				//solution is to add more tracks until the message is accepted
				MIDISong.addTrack();
				m--;
			};
		}
	}

	/**
	 * <blockquote>
	 * <p><pre>{@code public void openTrack()}</pre></p> 
	 * Opens the data in a track for use in the note editor.</p> 
	 */
	public void openTrack()
	{
		Notes.setTrack(channel);
		updateNoteCount();
		//endNotes refers to the number of end messages in the track (important for identifying notes)
		int endNotes = countMessage(channel, (byte)ShortMessage.NOTE_OFF);
		
		//If their are a real amount of notes
		if(numNotes >= 0)
		{
			//Cycles through every midi event present in the track
			for(int i = 0; i < MIDISong.getSequence().getTracks()[channel].size(); )
			{
				//If start of a note is found
				if(Notes.isMessageStatus((byte)MIDISong.getMessage(channel, i).getStatus(), (byte)ShortMessage.NOTE_ON))
				{
					//int a = end of note's location (which is found through the readNotes() method)
					int a = readForNotes(MIDISong.getMessage(channel, i).getMessage()[Notes.DATA_TONE], i, endNotes);
					//If an end of note can be identified
					if(a >= 0)
					{
						//Notes are deleted to prevent merging (when two notes share the same event/message)
						notes.add(new Notes(MIDISong.getEvent(channel, i), MIDISong.getEvent(channel, a)));
						MIDISong.getSequence().getTracks()[channel].remove(notes.get(Notes.getNumNotes()-1).getStartMessage());
						MIDISong.getSequence().getTracks()[channel].remove(notes.get(Notes.getNumNotes()-1).getEndMessage());
					}
					//Note is disposed if the end of it cannot be found
					else
					{
						MIDISong.getSequence().getTracks()[channel].remove(MIDISong.getEvent(channel, i));
						numNotes--;
						endNotes--;
					}
				}
				//Since notes are deleted when found, the loop only increments when the event isn't a note
				else
				{
					i++;
				}
			}
		}
		//If note length is invalid
		else
		{
			NotifyAnimation.sendMessage("Error", "Track "+channel+" cannot be edited because it exceeds note limit!");
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void closeTrack()}</pre></p> 
	 * Removes the data currently being used in the note editor.</p> 
	 */
	public void closeTrack()
	{
		saveTrack();
		notes.clear();
		Notes.resetNotes();
		numNotes = countMessage(channel, (byte)ShortMessage.NOTE_ON);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void saveTrack()}</pre></p> 
	 * Updates the values of the notes to the sequence.</p> 
	 */
	public void saveTrack()
	{
		//int n = note in the sequence
		int n = 0;
		try {
			//Remove all original messages (related to the notes)
			for(; n < Notes.getNumNotes(); n++)
			{
				MIDISong.getSequence().getTracks()[channel].remove(notes.get(n).getStartMessage());
				MIDISong.getSequence().getTracks()[channel].remove(notes.get(n).getEndMessage());
			}
			//Create new messages with the updated data
			for(n = 0; n < Notes.getNumNotes(); n++)
			{
				notes.get(n).setMidiEvent(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON + channel, channel, notes.get(n).getTone(), notes.get(n).getVolume()), notes.get(n).getTick()), new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF + channel, channel, notes.get(n).getTone(), notes.get(n).getVolume()), notes.get(n).getEndTick()));
				MIDISong.getSequence().getTracks()[channel].add(notes.get(n).getStartMessage());
				MIDISong.getSequence().getTracks()[channel].add(notes.get(n).getEndMessage());
			}
		} catch (ArrayIndexOutOfBoundsException e) {NotifyAnimation.sendMessage("Error", "Array index out of bound! ("+n+")");
		} catch (InvalidMidiDataException e) {NotifyAnimation.sendMessage("Error", "The current track has been deleted or corrupted!");}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void addNote(MidiEvent s, MidiEvent e)}</pre></p> 
	 * Creates a new <b>Notes</b> object using the parameters and adds it to the note array.</p> 
	 * @param s = location of start of note
	 * @param e = location of end of notes
	 */
	public void addNote(MidiEvent s, MidiEvent e)
	{
		notes.add(new Notes(s, e));
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void addNote(long tick, long endTick, byte t, byte v)}</pre></p> 
	 * Creates a new <b>Notes</b> object using the parameters and adds it to the note array.</p> 
	 * @param tick = location of start of note
	 * @param endTick = location of end of notes
	 * @param t = tone of note
	 * @param v = volume of note
	 */
	public void addNote(long tick, long endTick, byte t, byte v)
	{
		MidiEvent eveStart = null;
		MidiEvent eveEnd = null;
		try {
			//eveStart = start of the note (event)
			eveStart = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, channel, t, v), tick);
			//eveEnd = start of the note (event)
			eveEnd = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, channel, t, v), endTick);
			
			MIDISong.getSequence().getTracks()[channel].add(eveStart);
			MIDISong.getSequence().getTracks()[channel].add(eveEnd);
			notes.add(new Notes(eveStart, eveEnd));
		} catch (InvalidMidiDataException e) {NotifyAnimation.sendMessage("Error", "Note could not be created.");}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void removeNote(int note)}</pre></p> 
	 * Removes the note from the track using its index value.</p> 
	 * @param note = index of note being removed
	 */
	public void removeNote(int note)
	{
		notes.remove(note);
		Notes.removeNote();
		numNotes--;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void updateNoteCount()}</pre></p> 
	 * Updates the value of numNotes().</p> 
	 */
	public void updateNoteCount()
	{
		numNotes = countMessage(channel, (byte)ShortMessage.NOTE_ON);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static int readForNotes(byte tone, int eventFrom, int endNotes)}</pre></p> 
	 * Searches for the end of a note (or NOTE_OFF message).</p>
	 * @param tone = tone of the start of the note
	 * @param eventFrom = location being searched from
	 * @param endNotes = the number of end messages
	 * @return Index location of the end message
	 */
	public int readForNotes(byte tone, int eventFrom, int endNotes)
	{
		//If a message is added in, then it cannot add any more messages afterwards
		boolean add = false;
		
		//Cycle through every event message
		for(int i = eventFrom + 1; i < MIDISong.getSequence().getTracks()[channel].size(); i++)
		{
			//If status of message equals NOTE_OFF (the message being searched)
			if(Notes.isMessageStatus((byte)MIDISong.getMessage(channel, i).getStatus(), (byte)ShortMessage.NOTE_OFF) && MIDISong.getMessage(channel, i).getMessage()[Notes.DATA_TONE] == tone)
			{
				return i;
			}
			//If there is an difference between NOTE_ON and NOTE_OFF messages (means a message needs to be filled in)
			else if(endNotes != numNotes)
			{
				//If note has no end before the start of the next note
				if(Notes.isMessageStatus((byte)MIDISong.getMessage(channel, i).getStatus(), (byte)ShortMessage.NOTE_ON) && MIDISong.getMessage(channel, i).getMessage()[Notes.DATA_TONE] == tone && add == false)
				{
					//c is a temporary counter for the event messages
					int c = i;
					//Make sure that there isn't a NOTE_OFF message on the same tick as the NOTE_ON message found
					while(MIDISong.getEvent(channel, c).getTick() == MIDISong.getEvent(channel, i).getTick() && c < MIDISong.getSequence().getTracks()[channel].size())
					{
						//A NOTE_OFF message was found on the same tick
						if(Notes.isMessageStatus((byte)MIDISong.getMessage(channel, c).getStatus(), (byte)ShortMessage.NOTE_OFF))
						{
							return c;
						}
						c++;
					}
					try {
						//A new NOTE_OFF message is created to fill information and its placed right before the NOTE-ON message
						MIDISong.getSequence().getTracks()[channel].add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF+channel, channel, tone, MIDISong.getMessage(channel, i).getMessage()[Notes.DATA_VELOCITY]), MIDISong.getEvent(channel, i).getTick()));
						endNotes++;
						i = eventFrom + 1;	//Start from beginning to find the newly added message (index gets lost when a message is added to the sequence)
						add = true;
					} catch (ArrayIndexOutOfBoundsException e) {NotifyAnimation.sendMessage("Error", "Array Index Out of Bound! (No Value Assigned)");
					} catch (InvalidMidiDataException e) {NotifyAnimation.sendMessage("Error", "Invalid midi data!");}
				}
			}
		}
		return eventFrom + 1;
	}
	
	/*
	 * UNUSED
	 * //readFor(byte trackNum, byte message, int eventFrom) searches for a specific meta message in the track
	 * //byte trackNum = track being searched
	 * //byte message = message being searched for
	 * //int eventFrom = location to start search from
	 * public static int readForMeta(byte trackNum, byte message, int eventFrom){
	 * 		for(int i = eventFrom; i < MIDISong.getSequence().getTracks()[trackNum].size(); i++){
	 * 			if((byte)MIDISong.getMessage(trackNum, i).getMessage()[1] == message){
	 * 				return i;
	 *		 	}
	 * 		}
	 * 		return -1;
	 * }
	*/
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setInstrument(byte inst)}</pre></p> 
	 * Sets the instrument of the track.</p>
	 * @param inst = instrument value to set
	 */
	public void setInstrument(byte inst)
	{
		instrument = inst;
		instrumentList.setSelectedIndex(instrument);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setVolume(byte v)}</pre></p> 
	 * Sets the volume of the track.</p>
	 * @param v = new volume
	 */
	public void setVolume(byte v)
	{
		volume = v;
		slider.setVolume(v);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public byte getInstrument()}</pre></p> 
	 * Returns the instrument of the track.</p>
	 * @return The instrument of the track
	 */
	public byte getInstrument()
	{
		return instrument;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public Notes getNotes(int note)}</pre></p> 
	 * Returns the <b>Notes</b> object from the array.</p>
	 * @param note = index of note in array
	 * @return The <b>Notes</b> object
	 */
	public Notes getNotes(int note)
	{
		return notes.get(note);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public byte getChannel()}</pre></p> 
	 * Returns the channel the track is assigned to.</p>
	 * @return The channel the track is assigned to
	 */
	public byte getChannel()
	{
		return channel;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public byte getChannel()}</pre></p> 
	 * Returns the volume of the track.</p>
	 * @return The volume of the track
	 */
	public byte getVolume()
	{
		return volume;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public VolumeSlider getSlider()}</pre></p> 
	 * Returns the volume slider that sets the volume of the track.</p>
	 * @return The <b>VolumeSlider</b> object of the track
	 */
	public VolumeSlider getSlider()
	{
		return slider;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public JButton getTrackEntryButton()}</pre></p> 
	 * Returns the track button for the track.</p>
	 * @return The track button
	 */
	public JButton getTrackEntryButton()
	{
		return trackButtons;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public JComboBox<String> getInstrumentListComboBox()}</pre></p> 
	 * Returns the instrument list for the track.</p>
	 * @return The instrument list (combo box)
	 */
	public JComboBox<String> getInstrumentListComboBox()
	{
		return instrumentList;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void drawTrack(Graphics2D g)}</pre></p> 
	 * Draws the track window for the track.</p>
	 * @param g = component of the JPanel used to create visual elements
	 */
	public void drawTrack(Graphics2D g)
	{
		//yLoc is the y location of the track window after considering window size and the scroll value
		int yLoc = trackSpace + GUI.toolBarHeight + (trackHeight + 5)*channel - MIDIMain.getScrollValue();
		
		//Background
		g.setColor(GUI.colours[GUI.getColourScheme()][2]);
		g.fillRoundRect(50, yLoc, GUI.screenWidth-100, trackHeight, 50, 50);
		
		//Volume Slider
		slider.setBounds((short)(GUI.screenWidth/2 + 25), (short)(GUI.fullAddHeight + (trackHeight + 5)*channel + trackSpace - MIDIMain.getScrollValue()), (short)(GUI.screenWidth*3/4 - GUI.screenWidth/2), (short)30);
		slider.drawVolumeSlider(g);
		volume = slider.getPercent();
		
		//If track is selected
		if(isSelected())
		{
			g.setStroke(GUI.superBold);
			g.setColor(GUI.colours[GUI.getColourScheme()][4]);
			g.drawRoundRect(50, yLoc, GUI.screenWidth-100, trackHeight, 50, 50);
			g.setStroke(GUI.basic);
		}
		else
		{
			g.setColor(GUI.colours[GUI.getColourScheme()][GUI.COLOUR_TEXT]);
			g.drawRoundRect(50, yLoc, GUI.screenWidth-100, trackHeight, 50, 50);
		}
		
		//Text Boxes
		g.setColor(GUI.colours[GUI.getColourScheme()][GUI.COLOUR_BG]);
		g.fillRect(201, 11 + yLoc, 98, 18);
		g.fillRect(GUI.screenWidth*3/4 + 41, 16 + yLoc, 68, 18);
		g.fillRect(GUI.screenWidth*3/4 + 41, 36 + yLoc, 68, 18);
		
		//Borders
		g.setColor(GUI.colours[GUI.getColourScheme()][GUI.COLOUR_TEXT]);
		g.drawRect(200, 10 + yLoc, 100, 20);
		g.drawRect(GUI.screenWidth*3/4 + 40, 15 + yLoc, 70, 20);
		g.drawRect(GUI.screenWidth*3/4 + 40, 35 + yLoc, 70, 20);
		//Text
		g.setFont(GUI.defaultFont);
		g.drawString(numNotes+" notes", 210, 25 + yLoc);
		g.drawString(volume+"%", GUI.screenWidth*3/4 + 50, 50 + yLoc);
		g.setFont(GUI.boldFont);
		g.drawString("VOLUME", GUI.screenWidth*3/4 + 50, 30 + yLoc);
		//Divider
		g.drawLine(GUI.screenWidth/2, 5 + yLoc, GUI.screenWidth/2, trackHeight + yLoc - 5);

		//If channel is the percussion track
		if(channel == 9)
		{
			g.setFont(GUI.boldFont);
			g.drawString("[Percussion]", GUI.screenWidth*5/8, 15 + yLoc);
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static int countMessage(byte trackNum, byte message)}</pre></p> 
	 * counts the number of messages of a specified type in the track.
	 * @param trackNum = track being identified in sequence
	 * @param message =  status type being counted
	 * @return The amount of the message that is in the track.
	 */
	public static int countMessage(byte trackNum, byte message)
	{
		int counter = 0;
		for(int i = 0; i < MIDISong.getSequence().getTracks()[trackNum].size(); i++)
		{
			//If message is the message being searched
			if(Notes.isMessageStatus((byte)MIDISong.getMessage(trackNum, i).getStatus(), message))
				counter++;
		}
		return counter;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void resizeButtons()}</pre></p> 
	 * Resizes the button components for the new window size.</p>
	 */
	public static void resizeButtons()
	{
		for(byte t = 0; t < MIDISong.getTracksLength(); t++)
		{
			MIDISong.getTracks(t).getInstrumentListComboBox().setSize(GUI.screenWidth/3, 20);
		}
	}
}
