/**
 * This class reads, opens and creates MIDI files for the program.
 * It enables the user interaction with MIDIs.
 */

//libraries
import java.io.*;
import javax.sound.midi.*;

public class MIDIReader {

	//variables
	private static String fileName = null;
	
	/*
	 * readFile(String fileName) returns sequence of the opened file
	 * String fileName = the name of the file
	 */
	public Sequence readFile(File file){
	
		if(MIDIFilter.getExtension(file).equals(MIDIFilter.mid[0]) || MIDIFilter.getExtension(file).equals(MIDIFilter.mid[1]))
		{
			fileName = file.getName();
				
			try
			{
				InputStream is = new FileInputStream(file);
				
				//if the file type is supported then create a buffered input stream for is
				if(!is.markSupported())
				{
					is = new BufferedInputStream(is);
				}
				Sequence s = MidiSystem.getSequence(is);
				is.close();
				
				return s;
			}
			catch (Exception ex){NotifyAnimation.sendMessage("Error", "File could not be read.");;}
		}
		
		fileName = fileName.substring(0, fileName.lastIndexOf(MIDIFilter.mid[0]));
		
		return null;
	}
	
	/*
	 * getFileName() returns the String of the file name
	 */
	public static String getFileName(int limit)
	{
		if(limit != 0 && fileName.length() > limit)
		{
			return fileName.substring(0, limit -2)+"...";
		}
		else
			return fileName;
	}
	
	/*
	 * setFileName(String s) sets the name of the file
	 * String s = new name of file
	 */
	public static void setFileName(String s)
	{
		fileName = s;
	}
	
	/*
	 * public sequence createFile() returns the sequence of a new file
	 */
	public Sequence createFile()
	{		
		try
		{
			Sequence s = new Sequence(javax.sound.midi.Sequence.PPQ,24);
			s.createTrack();
			fileName = "MIDI Song";
			
			//GENERAL INFO		
			byte[] b = {(byte)0xF0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, (byte)0xF7};
			s.getTracks()[0].add(new MidiEvent(new SysexMessage(b,8), 0));

			//SET TEMPO
			//{32,0,0} (0x200000)
			byte[] c = {0x20, 0x0, 0x0};
			//{81,c,3}
			s.getTracks()[0].add(new MidiEvent(new MetaMessage(0x51, c, 3), 0));
			
			//END OF TRACK
			byte[] a = {};
			s.getTracks()[0].add(new MidiEvent(new MetaMessage(0x2F, a, 0), 16*40));

			return s;
		}
		catch(InvalidMidiDataException ex){ex.printStackTrace();}
		
		return null;
	}
	
	/*
	 * public void saveFile saves the MIDI file
	 * Sequence s = the sequence that is being saved
	 */
	public void saveFile(Sequence s, String file)
	{
		if(!file.endsWith(MIDIFilter.mid[0]) || !file.endsWith(MIDIFilter.mid[1]))
			file += ".mid";
		try
		{
			MidiSystem.write(s, 1, new File(file));
		}
		catch(IOException ex){ex.printStackTrace();}
		
	}
	
	public Soundbank getSoundbank(File file)
	{
		Soundbank sound = null;
		try {
			sound = MidiSystem.getSoundbank(file);
		} catch (Exception ex) {NotifyAnimation.sendMessage("Error", "Soundbank could not be opened.");}
		return sound;
	}
	
	public static String[] getInstrumentList()
	{
		String[] s = new String[0];
		BufferedReader read;
		int lines = 0;
		try {
			read = new BufferedReader(new FileReader("Instrument_List.txt"));
			
			while (!read.readLine().equals("END"))
			{
				lines++;
			}
			
			s = new String[lines];
			read.close();
			
			read = new BufferedReader(new FileReader("Instrument_List.txt"));
			
			for(int i = 0; i < lines; i++)
			{
				s[i] = read.readLine();
			}
			
			read.close();
			
		} catch (FileNotFoundException e) {NotifyAnimation.sendMessage("Error", "Instrument list could not be read.");
		} catch (IOException e) {NotifyAnimation.sendMessage("Error", "Instrument list could not be read.");}
		
		return s;
	}
	
}
