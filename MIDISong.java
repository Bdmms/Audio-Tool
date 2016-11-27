import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;

/**
 * Date: November 1, 2016
 * 
 * This class stores all data in the song, including the sequence, 
 * length, and the tempo.
 */

public class MIDISong 
{
	private static Sequence sequence;			//The sequence for the song
	private static long length = 100;			//The length of the song in ticks
	private static short measureLength = 16;	//The length of each measure in ticks
	private static Tracks[] tracks;				//The tracks contained in the song
	
	//setSong(Sequence seq) sets the sequence for the song and other information
	//Sequence seq = sequence the song reads
	public static void setSong(Sequence seq)
	{
		sequence = seq;
		length = sequence.getTickLength();
		resetTracks();
	}
	
	//resetTracks() changes the amount of tracks in the sequence
	private static void resetTracks()
	{
		tracks = new Tracks[sequence.getTracks().length];
		for(byte i = 0; i < tracks.length; i++)
		{
			tracks[i] = new Tracks(i);
		}
	}
	
	//addTrack() adds a new track to the sequence
	public static void addTrack()
	{
		if(tracks.length < 16)
		{
			sequence.createTrack();
			resetTracks();
		}
		else
		{
			NotifyAnimation.sendMessage("Notification", "Track limit has been reached. (Only 16 tracks can exist in one song)");
		}
	}
	
	//addNote(byte trackNum) adds a note to the track in a sequence
	//byte trackNum
	public static void addNote(byte trackNum)
	{
		tracks[trackNum].closeTrack();
		try {
			sequence.getTracks()[trackNum].add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, trackNum, 60, 70), 0));
			sequence.getTracks()[trackNum].add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, trackNum, 60, 70), 8));
		} catch (InvalidMidiDataException e) {NotifyAnimation.sendMessage("Error", "note could note be added to track because it may have been deleted or corrupted");}
		tracks[trackNum].openTrack();
	}
	
	//openTrack(byte trackNum) opens the designated track to be used in the note editor
	//byte teackNum = specified track in the array
	public static void openTrack(byte trackNum)
	{
		tracks[trackNum].openTrack();
	}
	
	//closeTrack(byte trackNum) closes the designated track to be used in the note editor
	//byte teackNum = specified track in the array
	public static void closeTrack(byte trackNum)
	{
		tracks[trackNum].closeTrack();
	}
	
	//saveTrack(byte trackNum) saves the track so that notes are updated in the sequence
	//byte teackNum = specified track in the array
	public static void saveTrack(byte trackNum)
	{
		tracks[trackNum].closeTrack();
		tracks[trackNum].openTrack();
	}
	
	//getNotes(byte trackNum) returns the array of notes in a designated track
	//byte teackNum = specified track in the array
	public static Notes[] getNotes(byte trackNum)
	{
		return tracks[trackNum].getNotes();
	}
	
	//getTracks() returns the designated track in the song
	//byte teackNum = specified track in the array
	public static Tracks getTracks(byte trackNum)
	{
		return tracks[trackNum];
	}
	
	//getTracksLength() returns the amount of tracks in the song
	public static byte getTracksLength()
	{
		return (byte) tracks.length;
	}
	
	//getSequence() returns the sequence of the song
	public static Sequence getSequence()
	{
		return sequence;
	}
	
	//getMidiEvent(byte trackNum, int eventNum) returns the MidiEvent specified from the sequence
	//byte trackNum = track event is to be taken from
	//int eventNum = location of event in track
	public static MidiEvent getEvent(byte trackNum, int eventNum)
	{
		return sequence.getTracks()[trackNum].get(eventNum);
	}
	
	//getMidiEvent(byte trackNum, int eventNum) returns the MidiMessage specified from the sequence
	//byte trackNum = track event is to be taken from
	//int eventNum = location of event in track
	public static MidiMessage getMessage(byte trackNum, int eventNum)
	{
		return sequence.getTracks()[trackNum].get(eventNum).getMessage();
	}
	
	//getLength() returns the length of the song in ticks
	public static long getLength()
	{
		return length;
	}
	
	//setLength(long l) sets the length of the song in ticks
	public void setLength(long l)
	{
		length = l;
	}
	
	//getMeasureLength() returns the length of each measure in ticks
	public static long getMeasureLength()
	{
		return measureLength;
	}
	
	//setMeasureLength(short l) sets the length of each measure in ticks (max of 65534)
	public static void setMeasureLength(short l)
	{
		measureLength = l;
	}
}
