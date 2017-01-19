import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;

/**
 * <b>[Date: November 1, 2016]</b>
 * <p>
 * This class stores all data in the song, including the sequence, 
 * length, and the tempo. Everything in this class is static, which
 * allows the data to be accessed from any other class.
 * </p>
 */
public class MIDISong 
{
	public static final byte MAX_CHANNELS = 16;	//The maximum channels in a song
	
	private static ArrayList<Tracks> tracks = new ArrayList<Tracks>();					//The tracks contained in the song
	private static ArrayList<MidiEvent> tempoChange = new ArrayList<MidiEvent>();		//The tempo change messages in the song (note: only the first is used in the program)
	private static Sequence sequence;			//The sequence for the song
	private static MidiEvent endOfTrack = null;	//The END_OF_TRACK message
	private static String artistName = "Artist";//The artist of the song
	private static long length = 1;				//The length of the song in ticks
	private static long tempo = 0;				//The tempo of the song (in microseconds per beat)
	private static byte[] measureLength = {4,4};//The time signature of the song (beats per measure, ticks per beat)
	private static byte endInChannel = 0;		//The channel containing END_OF_TRACK message
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void setSong(Sequence seq)}</pre></p> 
	 * Sets the sequence for the song and other information.</p> 
	 * @param seq = Sequence that is read
	 */
	public static void setSong(Sequence seq)
	{
		sequence = seq;
		length = sequence.getTickLength();

		//DEBUG (NOTE: Reads messages before they are moved
		/*
		System.out.println((byte)ShortMessage.PROGRAM_CHANGE);
		for(byte t = 0; t < sequence.getTracks().length; t++)
		{
			if(t == 1)
			{
				System.out.println("\nTrack "+(t+1)+" ----------------------------------------");
				for(int i = 0; i < MIDISong.getSequence().getTracks()[t].size(); i++)
				{
					System.out.print("\n"+String.format("%4d",i)+": "+String.format("%4d",MIDISong.getEvent(t, i).getTick())+" ticks |");
					for(int m = 0; m < MIDISong.getMessage(t, i).getLength(); m++)
					{
						System.out.print(String.format("%4d",MIDISong.getMessage(t, i).getMessage()[m])+"|");
					}
				}
				System.out.println(" ");
			}
		}
		*/
		
		/*
		 * UNUSED
		 * Reads for text messages written into the midi file.
		for(byte t = 0; t < seq.getTracks().length; t++)
		{
			int v = Tracks.readForMeta(t, (byte)0x01, 0);
			
			if(v >= 0)
			{
				String s = "";
				
				for(int d = 3; d < getMessage(t,v).getMessage().length; d++)
				{
					s += (char) getMessage(t,v).getMessage()[d];
				}
				
				System.out.println(s);
			}
		}
		*/
		
		resetTracks();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static Sequence saveSequence()}</pre></p> 
	 * Saves the sequence so that it can be written as a file.</p> 
	 * @return The saved sequence
	 */
	public static Sequence saveSequence()
	{
		try {
			//Creates a temporary sequence
			Sequence seq = new Sequence(sequence.getDivisionType(), sequence.getResolution());
		
			//Cloning the sequence to the temporary one
			for(byte t = 0; t < tracks.size(); t++)
			{
				seq.createTrack();
				for(int m = 0; m < sequence.getTracks()[t].size(); m++)
				{
					seq.getTracks()[t].add(getEvent(t, m));
				}
				//Adding the control and program change messages
				seq.getTracks()[t].add(new MidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE + t, 7, tracks.get(t).getVolume()), 0));
				seq.getTracks()[t].add(new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE + t, tracks.get(t).getInstrument(), t), 0));
			}
			
			//Sets the tempo message if it hasn't been updated
			setTempo(tempo);
			
			//Adds the end of track message
			//END OF TRACK
			if(endOfTrack == null)
			{
				byte[] b = {};
				seq.getTracks()[0].add(new MidiEvent(new MetaMessage(0x2F, b, 0), sequence.getTickLength()));
			}
			
			return seq;
		} catch (InvalidMidiDataException e) {NotifyAnimation.sendMessage("Error", "File could not be saved");}
		return null;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code private static void searchTrack(byte t)}</pre></p> 
	 * Searches the entire track for important information.</p> 
	 * @param t = track being searched
	 */
	private static void searchTrack(byte t)
	{
		//Search every event in the track
		for(int m = MIDISong.getSequence().getTracks()[t].size() - 1; m >= 0; m--)
		{
			try
			{
				//If message is end of track
				if(MIDISong.getMessage(t, m).getStatus() == 0xFF && MIDISong.getMessage(t, m).getMessage()[1] == 0x2F)
				{
					endOfTrack = getEvent(t, m);
					endInChannel = t;
				}
				//If message is program change (instrument change)
				if(Notes.isMessageStatus((byte)MIDISong.getMessage(t, m).getStatus(), (byte)ShortMessage.PROGRAM_CHANGE))
				{
					tracks.get(Notes.getMessageChannel((byte)MIDISong.getMessage(t, m).getStatus(), (byte)ShortMessage.PROGRAM_CHANGE)).addInstrument(MIDISong.getEvent(t, m));
					MIDISong.getSequence().getTracks()[t].remove(MIDISong.getSequence().getTracks()[t].get(m));
				}
				//If message is a volume change
				if(Notes.isMessageStatus((byte)MIDISong.getMessage(t, m).getStatus(), (byte)ShortMessage.CONTROL_CHANGE) && MIDISong.getMessage(t, m).getMessage()[1] == 7)
				{
					byte i = Notes.getMessageChannel((byte)MIDISong.getMessage(t, m).getStatus(), (byte)ShortMessage.CONTROL_CHANGE);
					MIDISong.getTracks(t).setVolume(MIDISong.getMessage(t, m).getMessage()[2]);
					MIDIPlayer.setVolume(i, MIDISong.getMessage(t, m).getMessage()[2]);
					MIDISong.getSequence().getTracks()[t].remove(MIDISong.getEvent(t, m));
				}
				//If message is a tempo change
				if(getMessage(t, m).getMessage()[1] == 0x51)
				{
					if(tempo == 0)
					{
						for(int d = 3; d < getMessage(t, m).getMessage().length; d++)
						{
							tempo += getMessage(t, m).getMessage()[d] * Math.pow(2, (getMessage(t, m).getMessage().length - d - 1)*8);
						}
					}
					tempoChange.add(sequence.getTracks()[t].get(m));
				}
				//If event has a negative tick value
				if(MIDISong.getEvent(t, m).getTick() < 0)
				{
					MIDISong.getSequence().getTracks()[t].remove(MIDISong.getEvent(t, m));
				}
				//If message is pitch bend (program does not support pitch bending notes)
				if(Notes.isMessageStatus((byte)MIDISong.getMessage(t, m).getStatus(), (byte)ShortMessage.PITCH_BEND))
				{
					MIDISong.getSequence().getTracks()[t].remove(MIDISong.getEvent(t, m));
				}
				//If the event's message is a channel dependent message
				if(MIDISong.getMessage(t, m).getStatus() >= 0x80 && MIDISong.getMessage(t, m).getStatus() < 0xF0)
				{
					//chan = channel that the message changes
					byte chan = Notes.getMessageChannel((byte)MIDISong.getMessage(t, m).getStatus(), (byte)(MIDISong.getMessage(t, m).getStatus() - MIDISong.getMessage(t, m).getStatus()%16));
					//If the channel of the track is not the same as the message's channel
					if(chan != t)
					{
						//message is moved to its corresponding track
						MIDISong.getSequence().getTracks()[chan].add(MIDISong.getEvent(t, m));
						MIDISong.getSequence().getTracks()[t].remove(MIDISong.getEvent(t, m));
					}
				}
			}
			catch(IndexOutOfBoundsException e){
				//Exception suggests that the sequence lacks tracks
				addTrack();
				m--;
			};
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code private static void resetTracks()}</pre></p> 
	 * Resets the amount of tracks in the sequence.</p> 
	 */
	private static void resetTracks()
	{
		tracks.clear();
		tempoChange.clear();
		endOfTrack = null;
		endInChannel = 0;
		tempo = 0;
		
		//Tracks have to be initialized first
		for(byte t = 0; t < sequence.getTracks().length; t++)
		{
			tracks.add(new Tracks(t));
		}
		
		//Searching has to be done before the tracks are finalized
		for(byte t = 0; t < sequence.getTracks().length; t++)
		{
			searchTrack(t);
			//tracks greater than 16 should be empty after search
			if(t >= MAX_CHANNELS)
			{
				sequence.deleteTrack(sequence.getTracks()[t]);
				tracks.remove(t);
				t--;
			}
		}
		
		//The note count can be updated after the sorting
		for(byte t = 0; t < sequence.getTracks().length; t++)
		{
			tracks.get(t).updateNoteCount();
		}
		
		//If a tempo has not been found (if sequence lacks a tempo then an error will occur)
		if(tempoChange.isEmpty())
		{
			//Giving a default tempo
			byte[] c = {0x20, 0x0, 0x0};
			try {
				tempoChange.add(new MidiEvent(new MetaMessage(0x51, c, 3), 0));
				sequence.getTracks()[0].add(tempoChange.get(0));
			} catch (InvalidMidiDataException e) {NotifyAnimation.sendMessage("Error", "Tempo cannot be added to song. Song is currently lacking a tempo.");}
		}
			
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static byte addTrack()}</pre></p> 
	 * Adds a new track to the sequence.</p> 
	 * @return the channel of the track added
	 */
	public static byte addTrack()
	{
		//tracks cannot exceed track limit
		if(tracks.size() < MAX_CHANNELS)
		{
			sequence.createTrack();
			tracks.add(new Tracks((byte)(sequence.getTracks().length - 1)));
			return (byte)(sequence.getTracks().length - 1);
		}
		else
		{
			NotifyAnimation.sendMessage("Notification", "Track limit has been reached. (Only 16 tracks can exist in one song)");
			return -1;
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void addNote(byte trackNum, byte tone, byte volume, long tick, long endTick)}</pre></p> 
	 * Adds a note to a track in the sequence.</p> 
	 * @param trackNum = track containing the new note
	 * @param tone = tone of the note
	 * @param volume = volume of the note
	 * @param tick = tick of the new note
	 * @param endTick = location of the end of the note
	 */
	public static void addNote(byte trackNum, byte tone, byte volume, long tick, long endTick)
	{
		tracks.get(trackNum).addNote(tick, endTick, tone, volume);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void removeNote(byte trackNum, int note)}</pre></p> 
	 * Removes a note from the sequence.</p> 
	 * @param trackNum = track containing note
	 * @param note = index of note being removed
	 */
	public static void removeNote(byte trackNum, int note)
	{
		sequence.getTracks()[trackNum].remove(MIDISong.getNotes(trackNum, note).getStartMessage());
		sequence.getTracks()[trackNum].remove(MIDISong.getNotes(trackNum, note).getEndMessage());
		tracks.get(trackNum).removeNote(note);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void removeSelectedNotes(byte trackNum)}</pre></p> 
	 * Removes multiple notes from the sequence.</p> 
	 * @param trackNum = track containing notes
	 */
	public static void removeSelectedNotes(byte trackNum)
	{
		//Search for selected notes
		for(int i = Notes.getNumNotes()-1; i >= 0; i--)
		{
			//if note is selected
			if(MIDISong.getNotes(trackNum, i).isSelected())
			{
				MIDISong.removeNote(trackNum, i);
			}
		}
		SelectableObject.unSelectAll();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void moveTrack(byte trackNum, byte target)}</pre></p> 
	 * Changes the order of the tracks in the song.</p> 
	 * @param trackNum = selected track
	 * @param target = target location to swap with
	 */
	public static void moveTrack(byte trackNum, byte target)
	{
		//If the the target and track are not the same
		if(trackNum != target)
		{
			Tracks temp = tracks.get(target);
			tracks.set(target, tracks.get(trackNum));
			tracks.remove(trackNum);
			tracks.add(trackNum, temp);
			
			//Using a empty sequence to create a temporary track
			Sequence seq = null;
			try {
				seq = new Sequence(javax.sound.midi.Sequence.PPQ,24);
			} catch (InvalidMidiDataException e) {}
			seq.createTrack();
			
			//create temporary (from target)
			for(int m = 0; m < sequence.getTracks()[target].size(); m++)
			{
				seq.getTracks()[0].add(getEvent(target, m));
			}
			//remove from target
			for(int m = sequence.getTracks()[target].size() - 1; m >= 0; m--)
					{
				sequence.getTracks()[target].remove(getEvent(target, m));
			}
			//add to target (from track)
			for(int m = 0; m < sequence.getTracks()[trackNum].size(); m++)
			{
				sequence.getTracks()[target].add(getEvent(trackNum, m));
			}
			//remove from track
			for(int m = sequence.getTracks()[trackNum].size() - 1; m >= 0; m--)
			{
				sequence.getTracks()[trackNum].remove(getEvent(trackNum, m));
			}
			//add to track (from temp)
			for(int m = 0; m < seq.getTracks()[0].size(); m++)
			{
				sequence.getTracks()[trackNum].add(seq.getTracks()[0].get(m));
			}
			tracks.get(trackNum).changeChannel(trackNum);
			tracks.get(target).changeChannel(target);	
			
			NotifyAnimation.sendMessage("Notification", "Swaping was successful.");
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void mergeTrack(byte trackNum, byte target)}</pre></p> 
	 * Merges a track into the targeted track.</p> 
	 * @param trackNum = selected track
	 * @param target = target location to merge with
	 */
	public static void mergeTrack(byte trackNum, byte target)
	{
		//Add every message from one track to another
		for(int m = 0; m < sequence.getTracks()[trackNum].size(); m++)
		{
			sequence.getTracks()[target].add(getEvent(trackNum, m));
		}
		tracks.get(target).updateNoteCount();
		//Remove track
		deleteTrack(trackNum);
		NotifyAnimation.sendMessage("Notification", "Merging was successful.");
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void openTrack(byte trackNum)}</pre></p> 
	 * Opens the designated track to be used in the note editor.</p> 
	 * @param teackNum = specified track in the array
	 */
	public static void openTrack(byte trackNum)
	{
		tracks.get(trackNum).openTrack();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void closeTrack(byte trackNum)}</pre></p> 
	 * Closes the designated track and removes its data from the note editor.</p> 
	 * @param teackNum = specified track in the array
	 */
	public static void closeTrack(byte trackNum)
	{
		tracks.get(trackNum).closeTrack();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void saveTrack(byte trackNum)}</pre></p> 
	 * Saves the track so that notes are updated in the sequence.</p> 
	 * @param teackNum = specified track in the array
	 */
	public static void saveTrack(byte trackNum)
	{
		tracks.get(trackNum).saveTrack();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void deleteTrack(byte trackNum)}</pre></p> 
	 * Removes the track from the sequence and everything inside.</p> 
	 * @param teackNum = selected track
	 */
	public static void deleteTrack(byte trackNum)
	{
		tracks.remove(trackNum);
		sequence.deleteTrack(sequence.getTracks()[trackNum]);
		//Shift tracks below the deleted track
		for(byte t = (byte)trackNum; t < tracks.size(); t++)
		{
			tracks.get(t).changeChannel((byte) (tracks.get(t).getChannel() - 1));
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void setLength(long l)}</pre></p> 
	 * Sets the length of the song.</p> 
	 * @param l = new length of song in ticks
	 */
	public static void setLength(long l)
	{
		//If END_OF_TRACK had been found
		if(endOfTrack != null)
		{
			sequence.getTracks()[endInChannel].remove(endOfTrack);
		}
		byte[] b = {};
		try {
			//Add in new message
			endOfTrack = new MidiEvent(new MetaMessage(0x2F, b, 0), length);
			endInChannel = 0;
			sequence.getTracks()[endInChannel].add(endOfTrack);
			length = l*measureLength[0]*16/measureLength[1];
		} catch (InvalidMidiDataException e) {NotifyAnimation.sendMessage("Error", "length could not be set.");}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void setTempoBpm(int t)}</pre></p> 
	 * Sets the tempo of the song in BPM.</p> 
	 * @param t = tempo in beats per minute
	 */
	public static void setTempoBpm(double t)
	{
		setTempo((long)(60000000 / t)) ;
		NotifyAnimation.sendMessage("NOTE", "Tempo = "+getTempoBpm());
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void setTempoBpm(long t)}</pre></p> 
	 * Sets the tempo of the song in MPB.</p> 
	 * @param t = tempo in microseconds per beat
	 */
	public static void setTempo(long t)
	{
		//Tempo has to be broken down into bytes
		byte[] a = {(byte)((tempo%Math.pow(2, 32))/Math.pow(2, 16)),(byte)(tempo%Math.pow(2, 16)/Math.pow(2, 8)), (byte)(tempo%Math.pow(2, 8))};
		try {
			//Old message is removed
			sequence.getTracks()[0].remove(tempoChange.get(0));
			//New message is added
			tempoChange.set(0, new MidiEvent(new MetaMessage(0x51, a, a.length), 0));
			sequence.getTracks()[0].add(tempoChange.get(0));
			tempo = t;
		} catch (InvalidMidiDataException e) {
			NotifyAnimation.sendMessage("Error", "Tempo cannot be changed.");
			System.out.println("Original: "+tempoChange.get(0).getMessage().getMessage()[3]+" "+tempoChange.get(0).getMessage().getMessage()[4]+" "+tempoChange.get(0).getMessage().getMessage()[5]);
			System.out.println("New: "+a[0]+" "+a[1]+" "+a[2]);}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void setTimeSignature(byte t, byte b)}</pre></p> 
	 * Sets the time signature of the song.</p> 
	 * @param t = top of signature (beats per measure)
	 * @param b = bottom of signature (note type valued as a beat)
	 */
	public static void setTimeSignature(byte t, byte b)
	{
		measureLength[0] = t;
		measureLength[1] = b;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void setArtistName(String name)}</pre></p> 
	 * Sets the name of the artist.</p> 
	 * @param name = new name of the artist
	 */
	public static void setArtistName(String name)
	{
		artistName = name;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static byte getAvgVolume(byte trackNum)}</pre></p> 
	 * Returns the average volume of all selected notes.</p> 
	 * @param trackNum = track containing notes
	 * @return The average volume of selected notes
	 */
	public static byte getAvgVolume(byte trackNum)
	{
		int v = 0;		//v = average volume
		int num = 0;	//num = number of selected notes
		//Find all selected notes in the track
		for(int n = 0; n < Notes.getNumNotes(); n++)
		{
			//If note is selected
			if(tracks.get(trackNum).getNotes(n).isSelected())
			{
				//Add up total volume
				v += tracks.get(trackNum).getNotes(n).getVolume();
				num++;
			}
		}
		return (byte)(v/num);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static Notes getNotes(byte trackNum, int note)}</pre></p> 
	 * Returns the array of notes in a designated track.</p> 
	 * @param trackNum = specified track in the array
	 * @param note = index of note in array
	 * @return The <b>Notes</b> object from array
	 */
	public static Notes getNotes(byte trackNum, int note)
	{
		return tracks.get(trackNum).getNotes(note);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static Tracks getTracks(byte trackNum)}</pre></p> 
	 * Returns the designated track in the song.</p> 
	 * @param trackNum = specified track in the array
	 * @return The <b>Tracks</b> object from array
	 */
	public static Tracks getTracks(byte trackNum)
	{
		return tracks.get(trackNum);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static byte getTracksLength()}</pre></p> 
	 * Returns the amount of tracks in the song.</p> 
	 * @return The amount of tracks in the song
	 */
	public static byte getTracksLength()
	{
		return (byte) tracks.size();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static Sequence getSequence()}</pre></p> 
	 * Returns the sequence of the song.</p> 
	 * @return The sequence of the song
	 */
	public static Sequence getSequence()
	{
		return sequence;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static MidiEvent getEvent(byte trackNum, int eventNum)}</pre></p> 
	 * Returns MidiEvent specified from the sequence.</p> 
	 * @param trackNum = track event is to be taken from
	 * @param eventNum = location of event in track
	 * @return The MidiEvent in the sequence
	 */
	public static MidiEvent getEvent(byte trackNum, int eventNum)
	{
		return sequence.getTracks()[trackNum].get(eventNum);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static MidiMessage getMessage(byte trackNum, int eventNum)}</pre></p> 
	 * Returns MidiMessage specified from the sequence.</p> 
	 * @param trackNum = track event is to be taken from
	 * @param eventNum = location of event in track
	 * @return The MidiMessage in the sequence
	 */
	public static MidiMessage getMessage(byte trackNum, int eventNum)
	{
		return sequence.getTracks()[trackNum].get(eventNum).getMessage();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static long getLength()}</pre></p> 
	 * Returns the length of the song.</p> 
	 * @return The length of the song in ticks
	 */
	public static long getLength()
	{
		return length;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static double getTempoBpm()}</pre></p> 
	 * Returns the tempo of song in BPM.</p> 
	 * @return The tempo of song in beats per minute
	 */
	public static double getTempoBpm()
	{
		return((double) 60000000 / tempo) ;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static long getTempo()}</pre></p> 
	 * Returns the tempo of song in MPB.</p> 
	 * @return The tempo of song in microseconds per beat
	 */
	public static long getTempo()
	{
		return tempo;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static byte getBeatNum()}</pre></p> 
	 * Returns the number of beats per measure in the song.</p> 
	 * @return The number of beats per measure
	 */
	public static byte getBeatNum()
	{
		return measureLength[0];
	}

	/**
	 * <blockquote>
	 * <p><pre>{@code public static long getMeasureLength()}</pre></p> 
	 * Returns the length of each measure.</p> 
	 * @return the length of a measure in ticks
	 */
	public static long getMeasureLength()
	{
		return measureLength[0]*16/measureLength[1];
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static String getArtistName()}</pre></p> 
	 * Returns the name of the artist.</p> 
	 * @return the name of the artist who made the song
	 */
	public static String getArtistName()
	{
		return artistName;
	}
}
