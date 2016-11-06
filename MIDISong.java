import javax.sound.midi.Sequence;

public class MIDISong 
{
	private static Sequence sequence;			//The sequence for the song
	private static long length = 100;			//The length of the song in ticks
	private static short measureLength = 16;	//The length of each measure in ticks
	private static Tracks[] tracks;			//The tracks contained ini the song
	
	//setSong(Sequence seq) sets the sequence for the song and other information
	//Sequence seq = sequence the song reads
	public static void setSong(Sequence seq)
	{
		sequence = seq;
		length = sequence.getTickLength();
		tracks = new Tracks[sequence.getTracks().length];
		for(byte i = 0; i < tracks.length; i++)
		{
			tracks[i] = new Tracks(i);
		}
	}
	
	//openTrack(byte trackNum) opens the designated track to be used in the note editor
	//byte teackNum = specified track in the array
	public static void openTrack(byte trackNum)
	{
		tracks[trackNum].openTrack(sequence.getTracks()[trackNum]);
	}
	
	//closeTrack(byte trackNum) closes the designated track to be used in the note editor
	//byte teackNum = specified track in the array
	public static void closeTrack(byte trackNum)
	{
		tracks[trackNum].closeTrack();
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
