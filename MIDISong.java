import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
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
	private static long length = 1;				//The length of the song in ticks
	private static long tempo = 0;				//The tempo of the song (in microseconds per beat)
	private static short measureLength = 16;	//The length of each measure in ticks
	private static ArrayList<Tracks> tracks;	//The tracks contained in the song
	private static MidiEvent tempoChange;		//The tempo change message
	
	//setSong(Sequence seq) sets the sequence for the song and other information
	//Sequence seq = sequence the song reads
	public static void setSong(Sequence seq)
	{
		sequence = seq;
		length = sequence.getTickLength();

		for(byte t = 0; t < seq.getTracks().length; t++)
		{
			int v = Tracks.readForMeta(t, (byte) 2, 0);
			
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
		
		for(byte t = 0; t < sequence.getTracks().length; t++)
		{
			for(int m = 0; m < sequence.getTracks()[t].size(); m++)
			{
				if(getMessage(t, m).getMessage()[1] == 0x51 && tempo == 0)
				{
					for(int d = 3; d < getMessage(t, m).getMessage().length; d++)
					{
						tempo += getMessage(t, m).getMessage()[d] * Math.pow(2, (getMessage(t, m).getMessage().length - d - 1)*8);
					}
					//System.out.println("Tempo: "+tempo);
					tempoChange = sequence.getTracks()[t].get(m);
					break;
				}
			}
		}
		resetTracks();
	}
	
	//resetTracks() changes the amount of tracks in the sequence
	private static void resetTracks()
	{
		tracks = new ArrayList<Tracks>();
		for(byte i = 0; i < sequence.getTracks().length; i++)
		{
			tracks.add(new Tracks(i));
		}
	}
	
	//addTrack() adds a new track to the sequence
	public static void addTrack()
	{
		if(tracks.size() < 16)
		{
			sequence.createTrack();
			tracks.add(new Tracks((byte)(sequence.getTracks().length - 1)));
		}
		else
		{
			NotifyAnimation.sendMessage("Notification", "Track limit has been reached. (Only 16 tracks can exist in one song)");
		}
	}
	
	//addNote(byte trackNum) adds a note to the track in a sequence
	//byte trackNum = track containing notes
	//long tick = tick of the new note
	//byte volume = volume of the note
	//long endTick = location of the end of the note
	public static void addNote(byte trackNum, long tick, byte tone, byte volume, long endTick)
	{
		tracks.get(trackNum).addNote(tick, endTick, tone, volume);
	}
	
	//removeNote(byte trackNum, int note) removes a note from the sequence
	//byte trackNum = track containing notes
	//int note = note being removed
	public static void removeNote(byte trackNum, int note)
	{
		sequence.getTracks()[trackNum].remove(MIDISong.getNotes(trackNum, note).getStartMessage());
		sequence.getTracks()[trackNum].remove(MIDISong.getNotes(trackNum, note).getEndMessage());
		tracks.get(trackNum).removeNote(note);
	}
	
	//removeSelectedNotes(byte trackNum, int note) removes multiple notes from a sequence
	//byte trackNum = track containing notes
	//int note = note being removed
	public static void removeSelectedNotes(byte trackNum)
	{
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
	
	//moveTrack(byte trackNum, byte target) changes the order of the tracks in the song
	//byte trackNum = selected track
	//byte target = target location
	public static void moveTrack(byte trackNum, byte target)
	{
		Tracks temp = tracks.get(target);
		tracks.set(target, tracks.get(trackNum));
		tracks.remove(trackNum);
		tracks.add(trackNum, temp);
		
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
	}
	
	//openTrack(byte trackNum) opens the designated track to be used in the note editor
	//byte teackNum = specified track in the array
	public static void openTrack(byte trackNum)
	{
		tracks.get(trackNum).openTrack();
	}
	
	//closeTrack(byte trackNum) closes the designated track to be used in the note editor
	//byte trackNum = specified track in the array
	public static void closeTrack(byte trackNum)
	{
		tracks.get(trackNum).closeTrack();
	}
	
	//deleteTrack(byte trackNum) removes the track from the sequence and everything inside
	//byte trackNum = selected track
	public static void deleteTrack(byte trackNum)
	{
		tracks.remove(trackNum);
		sequence.deleteTrack(sequence.getTracks()[trackNum]);
	}
	
	//saveTrack(byte trackNum) saves the track so that notes are updated in the sequence
	//byte teackNum = specified track in the array
	public static void saveTrack(byte trackNum)
	{

		tracks.get(trackNum).saveTrack();
	}
	
	//saveSequence() saves the sequence so that it can be written as a file
	public static Sequence saveSequence()
	{
		try {
			for(byte i = 0; i < tracks.size(); i++)
			{
				sequence.getTracks()[i].add(new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE + i, tracks.get(i).getInstrument(), i), 0));
			}
			
			setTempo(tempo);
			
			//END OF TRACK
			byte[] b = {};
			sequence.getTracks()[0].add(new MidiEvent(new MetaMessage(0x2F, b, 0), sequence.getTickLength()));

			return sequence;
		} catch (InvalidMidiDataException e) {NotifyAnimation.sendMessage("Error", "File could not be saved");}
		return null;
	}
	
	//setLength(long l) sets the length of the song in ticks
	public static void setLength(long l)
	{
		length = l;
	}
	
	//setTempo(long l) sets the tempo of the song (in microseconds per beat)
	//long t = new tempo (in microseconds per beat)
	public static void setTempo(long t)
	{
		tempo = t;
		//Set Tempo
		byte[] a = {(byte)((tempo%Math.pow(2, 32))/Math.pow(2, 16)),(byte)(tempo%Math.pow(2, 16)/Math.pow(2, 8)), (byte)(tempo%Math.pow(2, 8))};
		try {
			sequence.getTracks()[0].remove(tempoChange);
			tempoChange = new MidiEvent(new MetaMessage(0x51, a, a.length), 0);
			sequence.getTracks()[0].add(tempoChange);
		} catch (InvalidMidiDataException e) {
			NotifyAnimation.sendMessage("Error", "Tempo cannot be changed.");
			System.out.println("Original: "+tempoChange.getMessage().getMessage()[3]+" "+tempoChange.getMessage().getMessage()[4]+" "+tempoChange.getMessage().getMessage()[5]);
			System.out.println("New: "+a[0]+" "+a[1]+" "+a[2]);}
	}
	
	//getNotes(byte trackNum) returns the array of notes in a designated track
	//byte teackNum = specified track in the array
	//int note = index of note in array
	public static Notes getNotes(byte trackNum, int note)
	{
		return tracks.get(trackNum).getNotes(note);
	}
	
	//getTracks() returns the designated track in the song
	//byte teackNum = specified track in the array
	public static Tracks getTracks(byte trackNum)
	{
		return tracks.get(trackNum);
	}
	
	//getTracksLength() returns the amount of tracks in the song
	public static byte getTracksLength()
	{
		return (byte) tracks.size();
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
	
	//returns the tempo of song
	public static double getTempoBpm()
	{
		return((double) 60000000 / tempo) ;
	}
	
	//getTempo() returns the tempo of the song
	public static long getTempo()
	{
		return tempo;
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
